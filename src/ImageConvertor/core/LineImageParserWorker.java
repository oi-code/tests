package ImageConvertor.core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ImageConvertor.data.Direction;
import ImageConvertor.data.Chunk;

class LineImageParserWorker {

	protected BufferedImage img;
	protected short imageWidth;
	protected short imageHeight;
	protected short chunkResolution;
	protected float minLum, maxLum;
	protected byte layer;
	protected Controller c;

	public LineImageParserWorker(Controller c) {
		this.c = c;
		this.img = c.getBufferedImage();
		this.chunkResolution = c.getChunkSize();
		this.imageWidth = (short) (img.getWidth() - (img.getWidth() % chunkResolution));
		this.imageHeight = (short) (img.getHeight() - (img.getHeight() % chunkResolution));
		// this.maxLum = c.getMaxLum();
		// this.minLum = c.getMinLum();
		// sb.append("width: " + width + " height: " + height + "\n");
	}

	public void setMinLum(float minLum) {
		this.minLum = minLum;
	}

	public void setMaxLum(float maxLum) {
		this.maxLum = maxLum;
	}

	public void setLayer(byte layer) {
		this.layer = layer;
	}

	private Chunk searchMaxLineInChunk(short w, short h, Direction direction, Chunk points, short startW,
			short startH) {
		// short[] arr = getDirection(w, h, direction);
		if (isBlackPixel(w, h)) {
			// points.startH = h;
			// points.startW = w;
			points.startPoint.x = w;
			points.startPoint.y = h;
			while (isBlackPixel(w, h)) {
				if (c.isCanceled()) {
					return null;
				}
				// w = arr[0];
				// h = arr[1];
				w = (short) (w + direction.getWidth());
				h = (short) (h + direction.getHeight());
				// points.endW = w;
				// points.endH = h;
				points.direction = direction;
				points.endPoint.x = w;
				points.endPoint.y = h;
				// arr = getDirection(w, h, direction);
				if (w < startW || w > startW - 1 + chunkResolution || h < startH || h > startH - 1 + chunkResolution) {
					break;
				}
			}
		}

		return points;
	}

	private List<Chunk> search() {

		List<Chunk> maxPoints = new ArrayList<Chunk>();
		int index = 0;

		// outer loop through the whole image
		for (short inLoopImageHeight = 0; inLoopImageHeight < imageHeight; inLoopImageHeight += chunkResolution) {

			short chunkHeightPosition = (short) (inLoopImageHeight / chunkResolution);

			for (short inLoopImageWidth = 0; inLoopImageWidth < imageWidth; inLoopImageWidth += chunkResolution) {

				short cunkWidthPosition = (short) (inLoopImageWidth / chunkResolution);

				List<Chunk> pList = new ArrayList<Chunk>();

				Chunk chunk_1 = new Chunk(chunkHeightPosition, cunkWidthPosition);
				Chunk chunk_2 = new Chunk(chunkHeightPosition, cunkWidthPosition);
				Chunk chunk_3 = new Chunk(chunkHeightPosition, cunkWidthPosition);
				Chunk chunk_4 = new Chunk(chunkHeightPosition, cunkWidthPosition);

				chunk_1.index = index;
				chunk_2.index = index;
				chunk_3.index = index;
				chunk_4.index = index;

				index++;
				// inner loop through one chunk
				for (short chunkHeight = inLoopImageHeight; chunkHeight < (inLoopImageHeight
						+ chunkResolution); chunkHeight++) {
					for (short chunkWidth = inLoopImageWidth; chunkWidth < (inLoopImageWidth
							+ chunkResolution); chunkWidth++) {
						if (c.isCanceled()) {
							return null;
						}

						/*
						 * chunk_1.layer = minLum;
						 * chunk_2.layer = minLum;
						 * chunk_3.layer = minLum;
						 * chunk_4.layer = minLum;
						 */

						pList.add(searchMaxLineInChunk(chunkWidth, chunkHeight, Direction.RIGHT, chunk_1,
								inLoopImageWidth, inLoopImageHeight));
						pList.add(searchMaxLineInChunk(chunkWidth, chunkHeight, Direction.DOWN, chunk_2,
								inLoopImageWidth, inLoopImageHeight));
						pList.add(searchMaxLineInChunk(chunkWidth, chunkHeight, Direction.RIGHT_DOWN, chunk_3,
								inLoopImageWidth, inLoopImageHeight));
						pList.add(searchMaxLineInChunk(chunkWidth, chunkHeight, Direction.RIGHT_UP, chunk_4,
								inLoopImageWidth, inLoopImageHeight));
					}
				}

				chunk_1.layer = layer;
				chunk_2.layer = layer;
				chunk_3.layer = layer;
				chunk_4.layer = layer;

				Chunk max = pList.stream().max((i1, i2) -> Double.compare(i1.getLength(), i2.getLength())).get();

				if (max.getLength() == 0) {
					max.direction = Direction.STUB;
					continue;
				}

				if (c.isRandom()) {
					/*
					 * max.startW = (short) ThreadLocalRandom.current().nextInt(max.startW - chunkSize,
					 * max.startW + chunkSize);
					 * max.startH = (short) ThreadLocalRandom.current().nextInt(max.startH - chunkSize,
					 * max.startH + chunkSize);
					 * max.endW = (short) ThreadLocalRandom.current().nextInt(max.endW - chunkSize, max.endW +
					 * chunkSize);
					 * max.endH = (short) ThreadLocalRandom.current().nextInt(max.endH - chunkSize, max.endH +
					 * chunkSize);
					 */
					ThreadLocalRandom tlr = ThreadLocalRandom.current();
					max.startPoint.x = tlr.nextInt(max.startPoint.x - chunkResolution,
							max.startPoint.x + chunkResolution);
					max.startPoint.y = tlr.nextInt(max.startPoint.y - chunkResolution,
							max.startPoint.y + chunkResolution);

					max.endPoint.x = tlr.nextInt(max.endPoint.x - chunkResolution, max.endPoint.x + chunkResolution);
					max.endPoint.y = tlr.nextInt(max.endPoint.y - chunkResolution, max.endPoint.y + chunkResolution);
				}
				// if (max.direction != Direction.STUB) {
				// System.out.println(max);
				maxPoints.add(max);
				// }
				// pList=null;
			}
		}
		// System.out.println("end");
		// System.out.println("SngleThread line 70. Size: " + maxPoints.size());
		/*
		 * maxPoints.stream().forEach(e -> {
		 * float currentLumiance = 0;
		 * for (int i = e.absolutePositionInImagePoint.x; i < e.absolutePositionInImagePoint.x
		 * + chunkResolution; i++) {
		 * for (int j = e.absolutePositionInImagePoint.y; j < e.absolutePositionInImagePoint.y
		 * + chunkResolution; j++) {
		 * int color = img.getRGB(i, j);
		 * int red = (color >>> 16) & 0xff;
		 * int green = (color >>> 8) & 0xff;
		 * int blue = color & 0xff;
		 * float luminance = ((red * 0.2126f) + (green * 0.7152f) + (blue * 0.0722f)) / 255;
		 * currentLumiance += luminance;
		 * }
		 * }
		 * e.totalLuminiance = currentLumiance / (chunkResolution * chunkResolution);
		 * });
		 */
		maxPoints.sort((o1, o2) -> Integer.compare(o1.index, o2.index));
		return maxPoints;
	}

	public short getChunkSize() {
		return chunkResolution;
	}

	public List<Chunk> getChunks() {
		return search();
	}

	private boolean isBlackPixel(short curWidth, short curHeight) {

		if (curWidth > imageWidth - 1 || curHeight > imageHeight - 1 || curWidth < 0 || curHeight < 0) {
			return false;
		}
		int color = img.getRGB(curWidth, curHeight);
		/*
		 * if ((color >>> 24) == 0x00) {
		 * return false;
		 * }
		 */
		// int alpha = (color >>> 24) & 0xff;
		int red = (color >>> 16) & 0xff;
		int green = (color >>> 8) & 0xff;
		int blue = color & 0xff;
		float luminance = ((red * 0.2126f) + (green * 0.7152f) + (blue * 0.0722f)) / 255;

		if (luminance >= minLum && luminance < maxLum) {
			return true;
		} else {
			return false;
		}
	}
}
