package ImageConvertor.core;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ImageConvertor.core.coreInterfaces.ImageParser;
import ImageConvertor.data.Chunk;
import ImageConvertor.data.Direction;

public class LuminImageParserWorker implements ImageParser{

	private BufferedImage image;
	private int chunkSize;
	private int matrixHeight;
	private int matrixWidth;
	private int imageHeight;
	private int imageWidth;
	private int layers;

	private Chunk[][] matrix;

	public LuminImageParserWorker(Controller controller) {
		this.image = controller.getBufferedImage();
		this.chunkSize = controller.getChunkSize();

		this.matrixHeight = (controller.getImageHeight() - controller.getImageHeight() % chunkSize) / chunkSize;
		this.matrixWidth = (controller.getImageWidth() - controller.getImageWidth() % chunkSize) / chunkSize;

		this.imageWidth = image.getWidth() - (image.getWidth() % chunkSize);
		this.imageHeight = image.getHeight() - (image.getHeight() % chunkSize);

		this.layers = controller.getLayers();
		matrix = new Chunk[matrixHeight][matrixWidth];
		//System.out.println(imageWidth+" "+imageHeight);
		//System.out.println(matrixWidth+" "+matrixHeight);
	}

	public List<List<Chunk>> doTask() {
		computeChunkLuminiance();
		List<List<Chunk>> result = new LinkedList<>();
		float lumStep = 1f / layers;
		float start = 0f;
		List<Float> steps = new ArrayList<>();
		steps.add(start);
		result.add(new LinkedList<Chunk>());
		while (start < 1f) {
			start = start + lumStep;
			steps.add(start);
			result.add(new LinkedList<Chunk>());
		}
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				Chunk currentChunk = matrix[i][j];
				if (currentChunk == null)
					continue;
				int chosedLayerIndex = 0;
				for (int currentStep = 0; currentStep < steps.size(); currentStep++) {
					float curStep = steps.get(currentStep);
					if (curStep <= currentChunk.chunkTotalLuminiance) {
						// System.out.println(currentChunk.chunkTotalLuminiance + " " + curStep);
						chosedLayerIndex = currentStep;
					}
				}
				result.get(chosedLayerIndex).add(currentChunk);
			}
		}
		//result.stream().map(e -> e.size()).forEach(System.out::println);
		return result;
	}

	private void computeChunkLuminiance() {
		ThreadLocalRandom tlr = ThreadLocalRandom.current();
		for (int out_i = 0; out_i < imageHeight; out_i += chunkSize) {
			for (int out_j = 0; out_j < imageWidth; out_j += chunkSize) {
				Chunk currentChunk = new Chunk((short) (out_i / chunkSize), (short) (out_j / chunkSize));
				currentChunk.direction = Direction.RIGHT;
				currentChunk.startPoint = new Point(out_j, out_i);
				currentChunk.endPoint = new Point(out_j + tlr.nextInt(chunkSize), out_i + tlr.nextInt(chunkSize));
				float currentChunkLuminiance = 0f;
				for (int inner_i = out_i; inner_i < out_i + chunkSize; inner_i++) {
					for (int inner_j = out_j; inner_j < out_j + chunkSize; inner_j++) {
						currentChunkLuminiance += currentPixelLuminiance(inner_j, inner_i);
					}
				}
				currentChunkLuminiance = currentChunkLuminiance / (chunkSize * chunkSize);
				currentChunk.chunkTotalLuminiance = currentChunkLuminiance;
				matrix[out_i / chunkSize][out_j / chunkSize] = currentChunk;
			}
		}
	}

	private float currentPixelLuminiance(int curWidth, int curHeight) {
		int color = image.getRGB(curWidth, curHeight);
		// int alpha = (color >>> 24) & 0xff;
		int red = (color >>> 16) & 0xff;
		int green = (color >>> 8) & 0xff;
		int blue = color & 0xff;
		float luminance = ((red * 0.2126f) + (green * 0.7152f) + (blue * 0.0722f)) / 255;
		return luminance;
	}
}
