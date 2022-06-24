package ImageConvertor.core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ImageConvertor.data.Direction;
import ImageConvertor.data.Points;

class SingleThreadParseImage {

	protected BufferedImage img;
	protected short width;
	protected short height;
	protected short chunkSize;
	protected float minLum, maxLum;
	protected int layer;
	protected Controller c;

	public SingleThreadParseImage(Controller c) {
		this.c = c;
		this.img = c.getBufferedImage();
		// this.sb = new StringBuilder();
		this.width = (short) img.getWidth();
		this.height = (short) img.getHeight();
		this.chunkSize = c.getChunkSize();
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

	protected Points searchMaxLine(short w, short h, Direction direction, Points points, short startW, short startH) {
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
				if (w < startW || w > startW - 1 + chunkSize || h < startH || h > startH - 1 + chunkSize) {
					break;
				}
			}
		}

		return points;
	}

	protected List<Points> search() {

		List<Points> maxPoints = new ArrayList<Points>();
		int index = 0;
		for (short h = 0; h < height; h += chunkSize) {
			short cHeight = (short) (h / chunkSize);
			for (short w = 0; w < width; w += chunkSize) {
				short cWidth = (short) (w / chunkSize);
				List<Points> pList = new ArrayList<Points>();

				for (short h1 = h; h1 < (h + chunkSize); h1++) {
					for (short w1 = w; w1 < (w + chunkSize); w1++) {

						if (c.isCanceled())
							return null;
						Points p1 = new Points(cHeight, cWidth);
						Points p2 = new Points(cHeight, cWidth);
						Points p3 = new Points(cHeight, cWidth);
						Points p4 = new Points(cHeight, cWidth);

						p1.index = index;
						p2.index = index;
						p3.index = index;
						p4.index = index;

						p1.layer = minLum;
						p2.layer = minLum;
						p3.layer = minLum;
						p4.layer = minLum;

						pList.add(searchMaxLine(w1, h1, Direction.RIGHT, p1, w, h));
						pList.add(searchMaxLine(w1, h1, Direction.DOWN, p2, w, h));
						pList.add(searchMaxLine(w1, h1, Direction.RIGHT_DOWN, p3, w, h));
						pList.add(searchMaxLine(w1, h1, Direction.RIGHT_UP, p4, w, h));
					}

				}
				index++;

				Points max = pList.stream().max((i1, i2) -> Double.compare(i1.getLength(), i2.getLength())).get();

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
					max.startPoint.x = tlr.nextInt(max.startPoint.x - chunkSize, max.startPoint.x + chunkSize);
					max.startPoint.y = tlr.nextInt(max.startPoint.y - chunkSize, max.startPoint.y + chunkSize);

					max.endPoint.x = tlr.nextInt(max.endPoint.x - chunkSize, max.endPoint.x + chunkSize);
					max.endPoint.y = tlr.nextInt(max.endPoint.y - chunkSize, max.endPoint.y + chunkSize);
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
		maxPoints.sort((o1, o2) -> Integer.compare(o1.index, o2.index));
		return maxPoints;
	}

	public short getChunkSize() {
		return chunkSize;
	}

	public List<Points> getPointsList() {
		return search();
	}

	protected boolean isBlackPixel(short curHeight, short curWidth) {

		if (curHeight > width - 1 || curWidth > height - 1 || curHeight < 0 || curWidth < 0) {
			return false;
		}
		int color = img.getRGB(curHeight, curWidth);
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
