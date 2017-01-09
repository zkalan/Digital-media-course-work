package com.server;

import java.util.Scanner;

public class Stop implements Runnable {
	
	private String version = "1.3.0";
	//默认服务器端口为80
	private int PORT = 80;
	//服务器根目录
	private String SERVER_ROOT = System.getProperty("user.dir");
	//缩略图宽度阈值
	private int thumbnail_width = 500;
	//缩略图目录
	private String thumbnail = null;

	public void run() {
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while(true){
			
			System.out.print(" # ");
			
			String flag = sc.nextLine();
			if(flag.equals("exit")){
				System.out.println("图片浏览服务器已关闭");
				System.exit(0);
			} else if(flag.equals("help")){
				System.out.println("HELP Menu:\n    1. exit 退出");
				System.out.println("    2. help 查看帮助");
				System.out.println("    3. detail 查看服务器信息");
				System.out.println("    4. 启动参数 可选:-p <port> -r <server root> -w <thumbnail_width>");
			} else if (flag.equals("detail")) {
				System.out.println("#######################");
				System.out.println("#2016年秋季学期-数字媒体课程设计#");
				System.out.println("#######################");
				System.out.println("-----------------------");
				System.out.println("|地址：localhost:" + this.PORT + "      |");
				System.out.println("|根目录：" + this.SERVER_ROOT + "   |");
				System.out.println("|缩略图阈值：" + this.thumbnail_width + "   |");
				System.out.println("|缩略图目录：" + this.thumbnail + "\\thb" + "   |");
				System.out.println("-----------------------");
				System.out.println("  *版本：" + version + "\n  *图片浏览服务器正在运行...");
				System.out.println("-----------------------");
			} else {
				System.out.println("请输入\"help\"查看帮助");
			}
		}
	}
	
	public Stop(String version,int port,String server_root,int width, String thumbnail_root){
		this.version = version;
		this.PORT = port;
		this.SERVER_ROOT = server_root;
		this.thumbnail_width = width;
		this.thumbnail = thumbnail_root;
	}

}
