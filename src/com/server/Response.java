package com.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * 
 * @author zk
 *
 */
public class Response {
	
	String regex = ".*\\.(jpg|jpeg|JPG|png|PNG|bmp|BMP|gif|GIF|tif|TIF)";
	
	String CRLF = "\r\n";
	
	private Socket socket;
	
	String serverRoot = null;
	
	String thumbnail = null;
	//缩略图宽度阈值
	private int thumbnail_width = 500;
	//
	private byte[] buffer = null;
	//
	StringBuffer responseHeader = null;
	
	private BufferedOutputStream ostream =null;
	
	private BufferedInputStream istream = null;

	public Response(Socket socket,String root,String thb,int width) throws IOException {
		//初始化byte[]
		buffer = new byte[8192];
		//初始化socket
		this.socket = socket;
		
		//记录服务器根目录
		this.serverRoot = root;
		//缩略图目录
		this.thumbnail = thb;
		this.thumbnail_width = width;
		//初始化构造响应体的stringbuffer
		responseHeader = new StringBuffer();
		/**
		 * 初始化输入输出流
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());
		istream = new BufferedInputStream(socket.getInputStream());
	
	}
	/**
	 * 解析请求的头部
	 * @return
	 */
	public String[] getRequestHeader(){
		
		String[] requestHeader = null;
		
		StringBuffer header = new StringBuffer();
		int last = 0,c = 0;
		
		boolean flag = true;
		try{
			while(flag && ((c = istream.read()) != -1)){
				switch (c) {
				case '\r':
					break;
				case '\n':
					if (c == last) {
						flag = false;
						break;
					}
					last = c;
					header.append("\n");
				default:
					last = c;
					header.append((char) c);
				}
			}
			requestHeader = header.toString().replace(CRLF, "\\s").split("\\s");
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return requestHeader;
	}
	/**
	 * 构造响应头部
	 * @param header_type
	 * @param content_length
	 * @throws IOException
	 */
	public void constructResponseHeader(String header_type,long content_length) throws IOException{
		
		responseHeader.append("Content-Type:" + header_type + CRLF);
		responseHeader.append("Content-Length:" + content_length + CRLF + CRLF);
		
		//写入到输出流中
		ostream.write(responseHeader.toString().getBytes(), 0, responseHeader.length());
		System.out.println("Server:constract " + header_type +" header success");
	}
	/**
	 * 构造响应的报文体
	 * @param type IMG
	 * @param directory
	 * @throws IOException
	 */
	public void constructIMGResponse(String type,String directory) throws IOException{
		
		FileInputStream fis = null;
		
		File file = new File(serverRoot,directory);
		
		//处理对文件的请求
		if (file.exists() && file.isFile() && file.getName().matches(regex)) {
			
			fis = new FileInputStream(file);
			//发送文件字节流
			constructIMGBuffer(type,fis);
			//关闭打开的文件
			fis.close();
		} else if (file.exists() && file.isDirectory()){
			//访问文件夹图标
			File dir = new File(this.serverRoot,"\\thb\\folder.jpg");
			//打开文件夹图标
			fis = new FileInputStream(dir);
			//发送文件字节流
			constructIMGBuffer(type,fis);
			//关闭打开的文件
			fis.close();
		} else {
			System.out.println("<3>客户端存在非法请求");
			System.out.print(" # ");
		}
		//清空输出流缓冲区
		allOutFlush();
	}
	/**
	 * 形成目录列表
	 * @param type DIR
	 * @param directory
	 * @throws IOException
	 */
	public void constractDIRResponse(String type,String directory) throws IOException{
		
		StringBuffer cDB = new StringBuffer();
		
		File dir = new File(serverRoot,directory);
		if (dir.exists() && dir.isDirectory()) {
			File[] dirs = dir.listFiles();
			
			//将目录加入到Stringbuffer中
			for (int i = 0 ; i < dirs.length ; i ++ ) {
				if (dirs[i].isDirectory() && !dirs[i].getName().equals("thb")){
					cDB.append("<D>" + dirs[i].getName() + CRLF);
				}
			}
			
			//将文件加入到stringbuffer中
			for (int i = 0 ; i < dirs.length ; i ++ ) {
				if (dirs[i].isFile() && dirs[i].getName().matches(regex)){
					cDB.append("<F>" + dirs[i].getName() + CRLF);
				}
			}
			//构造响应头部
			constructResponseHeader(type,cDB.length());
			//将有效长度的数据位写入到输出流
			ostream.write(cDB.toString().getBytes(), 0, cDB.length());
			
			System.out.println("Server:constract " + type + " body success");
			System.out.print(" # ");
			//清空输出流缓冲区
			allOutFlush();
		} else {
			System.out.println("<4>客户端存在非法请求");
			System.out.print(" # ");
		}
	}
	/**
	 * 构造对缩略图请求的响应
	 * @param type
	 * @param directory
	 * @throws IOException 
	 */
	public void constractTHBResponse(String type, String directory) throws IOException {
		
		File thumbnail = new File(this.thumbnail,directory);
		
		File originalFile = new File(this.serverRoot,directory);
		
		if (thumbnail.exists() && thumbnail.isFile() && thumbnail.getName().matches(regex)){
			//缩略图存在
			constructIMGResponse("THB","\\thb" + directory);
			
		} else if (!thumbnail.exists() && originalFile.exists() && originalFile.isFile() 
				&& originalFile.getName().matches(regex)){
			//创建缩略图
			AffineTransImage aff = new AffineTransImage(originalFile,thumbnail,thumbnail_width);
			aff.run();
			//
			if (thumbnail.exists()){
				System.out.println("产生新缩略图：" + thumbnail.getAbsolutePath());
			} else {
				System.out.println("缩略图产生失败：" + thumbnail.getAbsolutePath());
			}
			//后续处理和缩略图存在时一致
			constructIMGResponse("THB","\\thb" + directory);
			
		} else if (originalFile.exists() && originalFile.isDirectory()){
			//返回文件夹缩略图
			constructIMGResponse("THB","\\thb\\t_folder.jpg");
		} else {
			System.out.println("<5>客户端存在非法请求");
			System.out.print(" # ");
		}
	}
	/**
	 * 构造文件二进制流
	 * @param type 返回的报文类型
	 * @param fis 打开的文件输入流
	 * @throws IOException
	 */
	public void constructIMGBuffer(String type,FileInputStream fis) throws IOException{
		
		long fileLength = fis.available();
		
		constructResponseHeader(type,fileLength);
		//输出、观察服务器构造的响应头部
		System.out.print(responseHeader.toString());
		
		int currentPos = 0 , bytesRead = 0;
		
		while (currentPos < fileLength) {
			//从已经打开的文件中读取byte到比特数组，记录读取到的位数
			bytesRead = fis.read(buffer);
			//将有效长度的数据位写入到输出流
			ostream.write(buffer, 0, bytesRead);
			//记录此时读到的数据位长度
			currentPos += bytesRead;
		}
		System.out.println("Server:constract " + type + " body success");
		System.out.print(" # ");
	}
	public void createThumbnail(){
		
	}
	/**
	 * 清空输出流的缓冲区
	 * @throws IOException
	 */
	public void allOutFlush() throws IOException{
		ostream.flush();
	}
	/**
	 * 关闭socket和流
	 * @throws IOException
	 */
	public void close() throws IOException{
		istream.close();
		ostream.close();
		socket.close();
	}
	
	public void clear(){
		buffer = new byte[8192];
		responseHeader = new StringBuffer();
	}


}
