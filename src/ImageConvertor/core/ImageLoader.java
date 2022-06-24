package ImageConvertor.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ImageLoader {

	Path path = null;
	Controller controller;

	public ImageLoader(Controller controller) {
		super();
		this.controller = controller;
	}

	public BufferedImage loadImage() {

		java.awt.FileDialog fd = new java.awt.FileDialog((java.awt.Frame) null);
		fd.setFile("*.jpg;*.jpeg;*.png;*.bmp;");
		fd.setDirectory(System.getProperty("user.home") + "\\Desktop\\");
		fd.setTitle(controller.getLocaleText("chose_image"));
		fd.setLocationRelativeTo(null);
		fd.setVisible(true);
		if (fd.getFile() == null) {
			JOptionPane.showMessageDialog(null, controller.getLocaleText("fide_not_chosed"),
					controller.getLocaleText("error"), JOptionPane.INFORMATION_MESSAGE);
			return null;
		} else {
			path = Paths.get(fd.getDirectory() + "\\" + fd.getFile());
		}
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(path.toFile());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpg", bos);
			// ColorConvertOp colorConvertOp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),
			// null);
			// colorConvertOp.filter(bufferedImage, bufferedImage);
		} catch (Exception e) {
			// e.printStackTrace();
			JOptionPane.showMessageDialog(null, controller.getLocaleText("broken_file"),
					controller.getLocaleText("error"), JOptionPane.ERROR_MESSAGE);
			return null;
		}

		/*
		 * JFileChooser jFileChooser = new JFileChooser();
		 * FileNameExtensionFilter images = new FileNameExtensionFilter("images", "png", "jpeg", "bmp",
		 * "jpg");
		 * jFileChooser.setFileFilter(images);
		 * 
		 * jFileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop\\"));
		 * jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		 * jFileChooser.showDialog(null, "Select image");
		 * 
		 * if (jFileChooser.getSelectedFile() == null) {
		 * JOptionPane.showMessageDialog(null, "You not choose the file.", "Error",
		 * JOptionPane.INFORMATION_MESSAGE);
		 * return null;
		 * } else {
		 * path = Paths.get(jFileChooser.getSelectedFile().getAbsolutePath());
		 * }
		 * 
		 * BufferedImage bufferedImage = null;
		 * try {
		 * bufferedImage = ImageIO.read(path.toFile());
		 * ByteArrayOutputStream bos = new ByteArrayOutputStream();
		 * ImageIO.write(bufferedImage, "jpg", bos);
		 * // ColorConvertOp colorConvertOp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),
		 * null);
		 * // colorConvertOp.filter(bufferedImage, bufferedImage);
		 * } catch (Exception e) {
		 * //e.printStackTrace();
		 * String text=jFileChooser.getSelectedFile().getName();
		 * String result=text.substring(text.lastIndexOf("."));
		 * JOptionPane.showMessageDialog(jFileChooser, "Unsuported file format "+result, result
		 * +" is not support.", JOptionPane.ERROR_MESSAGE);
		 * return null;
		 * }
		 */
		return bufferedImage;
	}

	public Path getPath() {
		return path;
	}

}
