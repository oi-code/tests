package ImageConvertor.core;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ImageConvertor.views.desktop.View;

public class GCodeCreator {
	private float pixelSize = 0.207f;
	private short[] a4Sheet = new short[] { 210, 297 };
	private String up = "M5 S0";
	private String down = "M3 S30";
	private String delayString = "G4 P0.1";

	Controller controller;
	private List<List<Point>> path;
	private StringBuilder sb;
	private int width;
	private int height;
	private float scale;

	public GCodeCreator(Controller controller, List<String> settings) {
		super();
		this.controller = controller;
		path = new ArrayList<>();
		/*
		 * controller.getForDrawContainer().stream().forEach(e -> {
		 * List<Point> temp = new ArrayList<>();
		 * e.stream().forEach(i -> {
		 * temp.add(i.startPoint);
		 * temp.add(i.endPoint);
		 * });
		 * path.add(temp);
		 * });
		 */
		path = controller.getFinalList();
		sb = new StringBuilder();
		width = controller.getImageWidth();
		height = controller.getImageHeight();

		up = settings.get(0);
		down = settings.get(1);
		delayString = settings.get(2);
		scale = Float.valueOf(settings.get(3)).floatValue();

		createGCode();
		saveGCode();
	}

	private void createGCode() {
		String servoUpCutPath = up + "\n" + delayString + "\n";
		String servoDownCutPath = down + "\n" + delayString + "\n";

		float imageRealWidth = width * pixelSize;
		float imageRealHeight = height * pixelSize;

		float scaleHeight = a4Sheet[0] / imageRealWidth;
		float scaleWidth = a4Sheet[1] / imageRealHeight;

		
		float scaler = Math.min(scaleHeight, scaleWidth);		

		int chunkSize = controller.getChunkSize();
		int maxConnectedRange = 10;

		float maxCalcRange = chunkSize * maxConnectedRange;

		String pathTemplate = "G1 Y%f X%f F20000\n";
		sb.append("G21\n");
		sb.append("G90\n");
		sb.append(servoUpCutPath);
		boolean isUp = false;
		for (List<Point> list : path) {
			sb.append(servoUpCutPath);
			isUp = true;
			Point prev = list.get(0);
			sb.append(String.format(pathTemplate, prev.getX() * pixelSize * scaler, prev.getY() * pixelSize * scaler));
			sb.append(servoDownCutPath);
			for (Point cur : list) {
				if (isUp) {
					sb.append(servoDownCutPath);
					isUp = false;
				}
				if (prev.distance(cur) > maxCalcRange && !isUp) {
					sb.append(servoUpCutPath);
					isUp = true;
				}
				double curX = cur.getX() * pixelSize;
				double curY = cur.getY() * pixelSize;
				sb.append(String.format(pathTemplate, curX * scaler, curY * scaler));
				prev = cur;
			}
		}
		sb.append(servoUpCutPath);
		sb.append("G28");
		String text = sb.toString().replaceAll(",", ".");
		sb.setLength(0);
		sb.append(text);
	}

	private void saveGCode() {
		try {
			// Files.deleteIfExists(Paths.get(View.DESKTOP_PATH + "\\layer.txt"));
			Files.write(Paths.get(View.DESKTOP_PATH + "\\" + controller.getFileName() + ".gcode"),
					sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
