package ImageConvertor.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import ImageConvertor.data.Chunk;
import ImageConvertor.views.desktop.View;

public class GCodeGenerator {
	private float pixelSize = 0.207f;
	private short[] a4Sheet;;// = new short[] { 210, 297 };
	private String up;// = "M5 S0";
	private String down;// = "M3 S20";
	private String delayString;// = "G4 P0.1";
	private String feedrate;

	Controller controller;
	private List<List<Chunk>> path;
	private List<List<Chunk>> chosedLayers;
	private List<Chunk[][]> matrixes;
	private StringBuilder sb;
	private int width;
	private int height;
	private float scale;

	public GCodeGenerator(Controller controller, List<String> settings) {
		super();
		this.controller = controller;
		matrixes = new LinkedList<>();
		chosedLayers = controller.getChosedLayersForDraw();
		int chunkSize = controller.getChunkSize();
		final int _height = (controller.getImageHeight() - controller.getImageHeight() % chunkSize) / chunkSize;
		final int _width = (controller.getImageWidth() - controller.getImageWidth() % chunkSize) / chunkSize;
		chosedLayers.forEach(e -> {
			Chunk[][] temp = new Chunk[_height][_width];
			e.forEach(chunk -> {
				temp[chunk.chunkPosition.y][chunk.chunkPosition.x] = chunk;
			});
			matrixes.add(temp);
		});
		path = controller.getFinalList();
		sb = new StringBuilder();
		int boundHeight = 0;
		int boundWidth = 0;
		while (boundWidth < controller.getImageWidth()) {
			boundWidth += chunkSize;
		}
		while (boundHeight < controller.getImageHeight()) {
			boundHeight += chunkSize;
		}
		width = boundWidth;
		height = boundHeight;

		up = settings.get(0);
		down = settings.get(1);
		delayString = settings.get(2);
		scale = Float.valueOf(settings.get(3)).floatValue();
		feedrate = settings.get(4);
		a4Sheet = new short[] { Short.valueOf(settings.get(5)), Short.valueOf(settings.get(6)) };

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

		float scaler = Math.min(scaleHeight, scaleWidth) * scale;

		int chunkSize = controller.getChunkSize();
		int maxConnectedRange = 7;

		float maxCalcRange = chunkSize * maxConnectedRange;

		// String pathTemplate = "G1 Y%f X%f F90000\n";
		String pathTemplate = "Y%f X%f\n";
		sb.append("G21\n");
		sb.append("G90\n");
		// sb.append("G94 " + feedrate + "\n");
		sb.append(feedrate + "\n");
		sb.append("G1\n");
		// sb.append(servoUpCutPath);
		boolean isUp = false;
		for (List<Chunk> list : path) {
			sb.append(servoUpCutPath);
			isUp = true;
			Chunk prev = list.get(0);
			sb.append(String.format(pathTemplate, prev.startPoint.getX() * pixelSize * scaler,
					prev.startPoint.getY() * pixelSize * scaler));
			sb.append(servoDownCutPath);
			isUp = false;
			for (Chunk cur : list) {
				if (isUp) {
					sb.append(servoDownCutPath);
					isUp = false;
				}
				if (prev.endPoint.distance(cur.startPoint) > maxCalcRange && !isUp) {
					sb.append(servoUpCutPath);
					isUp = true;
				}
				matrixes.stream().forEach(matrix -> {
					Chunk pathChunk = matrix[cur.chunkPosition.y][cur.chunkPosition.x];
					if (pathChunk != null) {

						if (controller.isRandom()) {
							double curX = pathChunk.startPoint.x * pixelSize;
							double curY = pathChunk.endPoint.y * pixelSize;
							sb.append(String.format(pathTemplate, curX * scaler, curY * scaler));

						} else {
							double curX = pathChunk.chunkPosition.x * pixelSize * chunkSize;
							double curY = pathChunk.chunkPosition.y * pixelSize * chunkSize;
							sb.append(String.format(pathTemplate, curX * scaler, curY * scaler));
						}
					}
				});
				prev = cur;
			}
		}
		sb.append(servoUpCutPath);
		sb.append("G28");
		String text = sb.toString().replaceAll(",", ".");
		sb.setLength(0);
		sb.append(text);
		System.out.println("gcode generated");
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
