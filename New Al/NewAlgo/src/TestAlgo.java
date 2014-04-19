import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class TestAlgo extends JFrame {
	BufferedImage i = null;

	public static void main(String a[]) {
		new TestAlgo("detector");
		new TestAlgo("plain");
	}

	public TestAlgo(String string) {
		try {
			i = ImageIO.read(new File(
					"E:\\Final InputAlgo\\2014-3-19-17-39-23.jpg"));
			//i = rgbtoGray(i);
			i = resize(i, 640, 480);
			setBounds(10, 10, 600, 480);
			if (string.contains("detector")) {
				setBounds(800, 10, 600, 480);
				i = detector(i);
			}
			repaint();
			setSize(640,480);
			setResizable(false);
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true){
						try {
							Thread.sleep(50);
							repaint();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			}).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paint(arg0);
		arg0.drawImage(i, 0, 0, getWidth(), getHeight(), this);
	}

	public static BufferedImage resize(BufferedImage leftImage, int width,
			int height) {
		// TODO Auto-generated method stub
		if (leftImage == null)
			return null;
		BufferedImage im = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = im.getGraphics();
		g.drawImage(leftImage, 0, 0, width, height, null);
		g.dispose();
		return im;
	}

	public static BufferedImage rgbtoGray(BufferedImage input) {
		BufferedImage image = new BufferedImage(input.getWidth(),
				input.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = image.getGraphics();
		g.drawImage(input, 0, 0, null);
		g.dispose();
		return image;
	}

	public static BufferedImage crop(BufferedImage src, int x, int y, int w,
			int h) {

		ImageFilter filter = new CropImageFilter(x, y, w, h);
		FilteredImageSource source = new FilteredImageSource(src.getSource(),
				filter);
		Image test = Toolkit.getDefaultToolkit().createImage(source);
		return toBufferedImage(test);
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	BufferedImage detector(BufferedImage img) {
		// img = rgbtoGray(img);
		System.out.println("In detector");
		int maxLevel = 2;
		int factor = 50;
		int averageValue = 40;
		boolean upflag = false, downflag = false;
		Color point = null;
		int upperX = 0, upperY = 0, downX = 0, downY = 0;
		mainup: for (int j = factor; j < img.getHeight() - 1; j++) {
			for (int i = factor; i < img.getWidth() - 1; i++) {
				point = new Color(img.getRGB(i, j));
				int ave = (point.getRed());
				if (ave < averageValue) { // to chech it as
											// black
					upflag = true;
					// scanPoint(i, j, img, maxLevel, averageValue);

					if (upflag) {
						System.out.println("found upper at" + i + " " + j);
						System.out.println("Colors" + point.getRed() + " "
								+ point.getBlue() + " " + point.getGreen());

						upperX = i;
						upperY = j;
						break mainup;
					}
				}
			}
		}

		maindown: for (int j = img.getHeight() - factor; j > factor; j--) {
			for (int i = img.getWidth() - factor; i > factor; i--) {
				point = new Color(img.getRGB(i, j));
				int ave = (point.getRed() + point.getGreen()) / 2;
				if (ave < averageValue) { // to chech it as
											// black
					downflag = true;
					// scanPoint(i, j, img, maxLevel, averageValue);
					if (downflag) {
						System.out.println("found Down at " + (i-factor) + " " + (j-factor));
						downX = i;
						downY = j;
						break maindown;
					}
				}
			}
		}

		if (upperX > downX) {
			int tmp = upperX;
			upperX = downX;
			downX = tmp;
		}
		System.out.println("found up at " + upperX + " " + upperY);
		System.out.println("found down at " + downX + " " + downY);
		

		if (upperX != 0 && downX != 0) {
			return crop(img, upperX, upperY, Math.abs(downX - upperX),
					Math.abs(downY - upperY));
			
		} else {
			System.out.println("Here is prob");
			return null;
		}
	}
}
