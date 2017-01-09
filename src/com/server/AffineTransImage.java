package com.server;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;

public class AffineTransImage {

	//原始文件
	private File originalIMG = null;
	//修改后的文件
	private File modifyIMG = null;
	//修改后的宽度
	private int modify_width = 500;
	
	public AffineTransImage(File original,File modify,int m_width){
		this.originalIMG = original;
		this.modifyIMG = modify;
		if (m_width > 0){
			this.modify_width = m_width;
		}
	}
	
	public void run() {  
		try {
			creaateThumbnailFolder(modifyIMG);
			/* 
			AffineTransform 类表示 2D 仿射变换，它执行从 2D 坐标到其他 2D 
			坐标的线性映射，保留了线的“直线性”和“平行性”。可以使用一系 
			列平移、缩放、翻转、旋转和剪切来构造仿射变换。 
			*/  
			AffineTransform transform = new AffineTransform();
			//读取图片  
			BufferedImage bis = ImageIO.read(originalIMG);
			//记录原图的长和宽
			int o_width = bis.getWidth();  
			int o_height = bis.getHeight();  
			//等比例计算得到变换后的高 
			int m_height = (modify_width * o_height) / o_width;
			//获得放缩系数
			double sx = (double)modify_width / o_width;  
			double sy = (double)m_height/o_height;
			//将变换设置为放缩变换
			//Sets this transform to a scaling transformation
			if (o_width <= modify_width) {
				transform.setToScale(1,1);
				modify_width = o_width;
				m_height = o_height;
				
			} else {
				transform.setToScale(sx,sy);
			}
			/**
			 * AffineTransformOp类使用仿射转换来执行从源图像或 Raster 中 2D 坐标到目标图像或 
			 *  Raster 中 2D 坐标的线性映射。所使用的插值类型由构造方法通过 
			 *  一个 RenderingHints 对象或通过此类中定义的整数插值类型之一来指定。 
			 *  如果在构造方法中指定了 RenderingHints 对象，则使用插值提示和呈现 
			 *  的质量提示为此操作设置插值类型。要求进行颜色转换时，可以使用颜色 
			 *  呈现提示和抖动提示。 注意，务必要满足以下约束：源图像与目标图像 
			 *  必须不同。 对于 Raster 对象，源图像中的 band 数必须等于目标图像中 
			 *  的 band 数。 
			*/  
			AffineTransformOp ato = new AffineTransformOp(transform,null);
			//不存在阿尔法通道
			BufferedImage bid = null;
			if (getIMGSuffix(this.originalIMG).equals("png") || getIMGSuffix(this.originalIMG).equals("PNG")) {
				bid = new BufferedImage(modify_width,m_height,BufferedImage.TYPE_4BYTE_ABGR);
			} else if (getIMGSuffix(this.originalIMG).equals("gif") || getIMGSuffix(this.originalIMG).equals("gif")){
				bid = new BufferedImage(modify_width,m_height,BufferedImage.TYPE_INT_ARGB);
			} else{
				bid = new BufferedImage(modify_width,m_height,BufferedImage.TYPE_3BYTE_BGR);
			}
			
			ato.filter(bis,bid);
			//将文件写入到原始色彩空间
			ImageIO.write(bid,this.getIMGSuffix(originalIMG),modifyIMG);  
		} catch(Exception e) {
			e.printStackTrace();  
		}  
	}  
	/**
	* 获得文件的后缀名
	* @param original
	* @return
	*/
	public String getIMGSuffix(File original){
		
		String img_name = original.getName();
		
		return img_name.substring(img_name.lastIndexOf(".") + 1);
	}
	/**
	 * 创建缩略图的父目录
	 * @param thumbnail
	 */
	public void creaateThumbnailFolder(File thumbnail){
		String parentFolder = thumbnail.getParent();
		
		File parentFile = new File(parentFolder);
		
		if (!parentFile.exists()){
			parentFile.mkdirs();
		}
	}
	
}