package com.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	
	private String version = "1.3.0";
	//服务器套接字
	private ServerSocket serverSocket;
	//线程池
	private ExecutorService executorService;
	//默认服务器端口为80
	private int PORT = 80;
	//服务器根目录
	private String SERVER_ROOT = System.getProperty("user.dir");
	//缩略图宽度阈值
	private int thumbnail_width = 500;
	//缩略图目录
	private String thumbnail = null;
	//单个处理器线程池工作线程数量
	private final static int POOL_SIZE = 4;
	
	/**
	 * 设置服务器端口
	 * @param port
	 */
	public void setPORT(int port){
		this.PORT = port;
	}
	/**
	 * 设置服务器根目录
	 * @param server_root
	 */
	public void setSERVERROOT(String server_root){
		this.SERVER_ROOT = server_root;
	}
	/**
	 * 获得服务器根目录
	 * @return
	 */
	public String getSERVERROOT(){
		return this.SERVER_ROOT;
	}
	
	public Server(String[] parameter) throws IOException{
		//创建服务器套接字
		//自定义端口
		this.setPORT(Integer.parseInt(parameter[0]));
		this.serverSocket = new ServerSocket(this.PORT);
		//设置服务器根目录
		this.setSERVERROOT(parameter[1]);
		//设置缩略图宽度阈值
		this.thumbnail_width = Integer.parseInt(parameter[2]);
		//设置缩略图目录
		this.thumbnail = this.SERVER_ROOT + "\\thb";
		//如果缩略图目录不存在就创建
		File thb = new File(this.thumbnail);
		if (!thb.exists()){
			thb.mkdirs();
		}
		
		//创建线程池
		this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * POOL_SIZE);
		
		System.out.println("#######################");
		System.out.println("#2016年秋季学期-数字媒体课程设计#");
		System.out.println("#######################");
		System.out.println("-----------------------");
		System.out.println("|地址：localhost:" + this.PORT + "      |");
		System.out.println("|根目录：" + this.getSERVERROOT() + "   |");
		System.out.println("|缩略图阈值：" + this.thumbnail_width + "   |");
		System.out.println("|缩略图目录：" + this.getSERVERROOT() + "\\thb" + "   |");
		System.out.println("-----------------------");
		System.out.println("  *版本：" + version + "\n  *图片浏览服务器已经启动...\n  *请输入\"help\"查看帮助");
		System.out.println("-----------------------");
	}

	/**
	 * 程序入口
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//处理输入的命令
		String[] parameter = getParameter(args);
		
		if(parameter[0].equals("EXIT")){
			System.out.println("请检查你的启动参数：\n    启动命令参数 -p <port> -r <server_root> -w <thumbnail_width>" );
			System.exit(0);
		} else{
			new Server(parameter).service();
		}
	}
	/**
	 * 阻塞以等待请求
	 * @throws IOException
	 */
	public void service() throws IOException{
		Socket socket = null;
		
		this.executorService.execute(new Stop(version,PORT,SERVER_ROOT,thumbnail_width,thumbnail));
		
		while(true){
			socket = serverSocket.accept();
			//线程池创建线程
			this.executorService.execute(new Handler(socket,SERVER_ROOT,thumbnail,thumbnail_width));
		}
	}
	
	/**
	 * 解析服务器启动参数
	 * @param args
	 * @return
	 */
	public static String[] getParameter(String[] args){
		String[] result = new String[3];
		if(args.length == 0){
			result[0] = "80";
			result[1] = System.getProperty("user.dir");
			result[2] = "500";
		} else if (args.length == 2 && args[0].equals("-p")){
			result[0] = args[1];
			result[1] = System.getProperty("user.dir");
			result[2] = "500";
		} else if (args.length == 2 && args[0].equals("-r")){
			result[0] = "80";
			result[1] = args[1];
			result[2] = "500";
		} else if (args.length == 2 && args[0].equals("-w")){
			result[0] = "80";
			result[1] = System.getProperty("user.dir");
			result[2] = args[1];
		}
		
		else if (args.length == 4 && args[0].equals("-p") && args[2].equals("-r")){
			result[0] = args[1];
			result[1] = args[3];
			result[2] = "500";
		} else if (args.length == 4 && args[0].equals("-p") && args[2].equals("-w")){
			result[0] = args[1];
			result[1] = System.getProperty("user.dir");
			result[2] = args[3];
		} else if (args.length == 4 && args[0].equals("-w") && args[2].equals("-p")){
			result[0] = args[3];
			result[1] = System.getProperty("user.dir");
			result[2] = args[1];
		} else if (args.length == 4 && args[0].equals("-w") && args[2].equals("-r")){
			result[0] = "80";
			result[1] = args[3];
			result[2] = args[1];
		} else if (args.length == 4 && args[0].equals("-r") && args[2].equals("-w")){
			result[0] = "80";
			result[1] = args[1];
			result[2] = args[3];
		} else if (args.length == 4 && args[0].equals("-r") && args[2].equals("-p")){
			result[0] = args[3];
			result[1] = args[1];
			result[2] = "500";
		}
		
		else if (args.length == 6 && args[0].equals("-p") && args[2].equals("-r") && args[4].equals("-w")){
			result[0] = args[1];
			result[1] = args[3];
			result[2] = args[5];
		} else if (args.length == 6 && args[0].equals("-p") && args[2].equals("-w") && args[4].equals("-r")){
			result[0] = args[1];
			result[1] = args[5];
			result[2] = args[3];
		} else if (args.length == 6 && args[0].equals("-w") && args[2].equals("-p") && args[4].equals("-r")){
			result[0] = args[3];
			result[1] = args[5];
			result[2] = args[1];
		} else if (args.length == 6 && args[0].equals("-w") && args[2].equals("-r") && args[4].equals("-p")){
			result[0] = args[5];
			result[1] = args[3];
			result[2] = args[1];
		} else if (args.length == 6 && args[0].equals("-r") && args[2].equals("-w") && args[4].equals("-p")){
			result[0] = args[5];
			result[1] = args[1];
			result[2] = args[3];
		} else if (args.length == 6 && args[0].equals("-r") && args[2].equals("-p") && args[4].equals("-w")){
			result[0] = args[3];
			result[1] = args[1];
			result[2] = args[5];
		}
		
		
		else {
			result[0] = "EXIT";
			result[1] = "EXIT";
			result[2] = "EXIT";
		}
		return result;
	}

}
