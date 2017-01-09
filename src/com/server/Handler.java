package com.server;

import java.net.Socket;
/**
 * 
 * @author zk
 *
 */
public class Handler implements Runnable {
	
	private Socket socket;
	
	private String serverRoot = null;
	//缩略图宽度阈值
	private int thumbnail_width = 500;
	//缩略图目录
	private String thumbnail = null;
	
	/**
	 * 构造函数
	 * @param socket
	 */
	public Handler(Socket socket,String root,String thb,int width){
		this.socket = socket;
		this.serverRoot = root;
		this.thumbnail = thb;
		this.thumbnail_width = width;
	}
	
	public void setServerRoot(String root){
		this.serverRoot = root;
	}
	
	public String getServerRoot(){
		return this.serverRoot;
	}

	@Override
	public void run() {
		
		System.out.println("\n连接成功！客户端地址：" + socket.getInetAddress() + ":" 
		+ socket.getPort());
		
		try{
			
			Response response = new Response(socket,serverRoot,thumbnail,thumbnail_width);
			
			//while (true) {
			
				String[] head = response.getRequestHeader();
				
				System.out.println(head.length);
				if (head.length != 2){
					
					System.out.println("<1>客户端请求报文格式非法");
					System.out.print(" # ");
					
				} else {
					//输出客户端请求头部
					System.out.println(head[0] + " " + head[1]);
					
					if (head[0].equals("IMG")){
						response.constructIMGResponse(head[0], head[1]);
					} else if(head[0].equals("DIR")){
						response.constractDIRResponse(head[0], head[1]);
					} else if(head[0].equals("THB")){
						response.constractTHBResponse(head[0], head[1]);
					}  else {
						System.out.println("<2>客户端存在非法请求");
						System.out.print(" # ");
					}
				}
				response.close();
			//	response.clear();
			//}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
