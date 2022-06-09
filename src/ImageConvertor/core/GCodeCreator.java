package ImageConvertor.core;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ImageConvertor.views.desktop.View;

public class GCodeCreator {
	public static final float PIXEL_SIZE = 0.207f;
	public static final short[] A4_SHEET = new short[] { /* 210, 297 */200, 280 };
	private static final String SERVO_UP = "M5 S0";
	private static final String SERVO_DOWN = "M3 S30";
	private static final String SERVO_DELAY = "G4 P0.1";

	Controller controller;
	private List<List<Point>> path;
	private StringBuilder sb;
	private int width;
	private int height;
	private short chunkSize;
	private int chunks;

	public GCodeCreator(Controller controller) {
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
		path = controller.finalList;
		sb = new StringBuilder();
		width = controller.getImageWidth();
		height = controller.getImageHeight();
		chunkSize = controller.getChunkSize();
		chunks = controller.getChunks();
		createGCode();
		saveGCode();
	}

	private void createGCode() {
		String servoUpCutPath = SERVO_UP + "\n" + SERVO_DELAY + "\n";
		String servoDownCutPath = SERVO_DOWN + "\n" + SERVO_DELAY + "\n";

		float imageRealWidth = width * PIXEL_SIZE;
		float imageRealHeight = height * PIXEL_SIZE;

		float scaleHeight = A4_SHEET[0] / imageRealWidth;
		float scaleWidth = A4_SHEET[1] / imageRealHeight;

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
			isUp=true;
			Point prev = list.get(0);
			sb.append(
					String.format(pathTemplate, prev.getX() * PIXEL_SIZE * scaler, prev.getY() * PIXEL_SIZE * scaler));
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
				double curX = cur.getX() * PIXEL_SIZE;
				double curY = cur.getY() * PIXEL_SIZE;
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

	public void saveGCode() {
		try {
			// Files.deleteIfExists(Paths.get(View.DESKTOP_PATH + "\\layer.txt"));
			Files.write(Paths.get(View.DESKTOP_PATH + "\\" + controller.getFileName() + ".gcode"),
					sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
