package ImageConvertor.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ImageConvertor.core.coreInterfaces.Pathfinder;
import ImageConvertor.data.Chunk;

public class SquarePathFinder implements Pathfinder {

	private Chunk[][] allChosedLayersInOneLayer;
	private int maxRange;
	private int totalChunks;

	public SquarePathFinder(Controller controller) {

		int chunkSize = controller.getChunkSize();
		final int height = (controller.getImageHeight() - controller.getImageHeight() % chunkSize) / chunkSize;
		final int width = (controller.getImageWidth() - controller.getImageWidth() % chunkSize) / chunkSize;
		this.maxRange = height >= width ? height : width;
		createOneLayerFromAllSelectedLayers(controller);
		List<List<Chunk>> res = new LinkedList<>();
		res.add(getPath());
		controller.setPathsPointList(res);
	}

	private void createOneLayerFromAllSelectedLayers(Controller controller) {
		/**
		 * copy list to avoid {@link java.util.ConcurrentModificationException}
		 */
		List<List<Chunk>> allLayers = new ArrayList<>(controller.getChosedLayersForDraw());
		int chunkSize = controller.getChunkSize();
		int height = (controller.getImageHeight() - controller.getImageHeight() % chunkSize) / chunkSize;
		int width = (controller.getImageWidth() - controller.getImageWidth() % chunkSize) / chunkSize;

		allChosedLayersInOneLayer = new Chunk[height][width];

		for (List<Chunk> currentlayer : allLayers) {
			for (Chunk currentChunk : currentlayer) {
				allChosedLayersInOneLayer[currentChunk.chunkPosition.y][currentChunk.chunkPosition.x] = currentChunk;
			}
		}
		for (int i = 0; i < allChosedLayersInOneLayer.length; i++) {
			for (int j = 0; j < allChosedLayersInOneLayer[i].length; j++) {
				if (allChosedLayersInOneLayer[i][j] != null) {
					totalChunks++;
				}
			}
		}
	}

	@Override
	public List<Chunk> getPath() {
		List<Chunk> result = new LinkedList<>();
		Chunk[][] cloud = allChosedLayersInOneLayer;
		Chunk seed = null;
		breaker: for (int i = 0; i < cloud.length; i++) {
			for (int j = 0; j < cloud[i].length; j++) {
				if (cloud[i][j] != null && !cloud[i][j].locked) {
					seed = cloud[i][j];
					break breaker;
				}
			}
		}
		if (seed == null) {
			return null;
		}
		result.add(seed);
		int range = 1;
		Optional<Chunk> next = getNextChunk(seed, range);
		while (result.size() < totalChunks) {
			if (next.isPresent()) {
				range = 1;
				Chunk _next = next.get();
				result.add(_next);
				_next.locked = true;
				next = getNextChunk(_next, range);
			} else {
				range++;
				Chunk _last = result.get(result.size() - 1);
				next = getNextChunk(_last, range);
				while (next.isEmpty() && range < maxRange) {
					// System.out.println("here");
					range++;
					next = getNextChunk(_last, range);
				}
			}
		}
		return result;
	}

	private Optional<Chunk> getNextChunk(Chunk seed, int range) {

		short startWidth = (short) seed.chunkPosition.x;
		short startHeight = (short) seed.chunkPosition.y;

		short currentWidth = (short) (startWidth - range);
		short currentHeight = (short) (startHeight - range);

		// from top left to top right
		while (currentWidth <= startWidth + range) {
			if (checkBound(currentWidth, currentHeight)) {
				if (allChosedLayersInOneLayer[currentHeight][currentWidth] != null
						&& !allChosedLayersInOneLayer[currentHeight][currentWidth].locked)
					return Optional.of(allChosedLayersInOneLayer[currentHeight][currentWidth]);
			}
			currentWidth++;
		}
		/*
		 * width's cursor now is in one position bigger than it needn't because of "while" statement
		 * so decrement it before run next step
		 */
		currentWidth--;
		/*
		 * increment height cursor because it needn't to add chunk from previous "while" step
		 * again
		 */
		currentHeight++;
		// why here is "<=" instead of "<" was explained above. Situation the same.
		// from top right to bottom right
		while (currentHeight <= startHeight + range) {
			if (checkBound(currentWidth, currentHeight)) {
				if (allChosedLayersInOneLayer[currentHeight][currentWidth] != null
						&& !allChosedLayersInOneLayer[currentHeight][currentWidth].locked)
					return Optional.of(allChosedLayersInOneLayer[currentHeight][currentWidth]);
			}
			currentHeight++;
		}
		// decrement height cursor because it now one position higher because of previous "while" step
		currentHeight--;
		// decrement width cursor because it needn't to add chunk from previous "while" step
		// {@link AntPathWorketManager.java:<<350>>}
		currentWidth--;
		// here ">=" instead of ">" because we need to return to start width position.
		// from bottom right to bottom left
		while (currentWidth >= startWidth - range) {
			if (checkBound(currentWidth, currentHeight)) {
				if (allChosedLayersInOneLayer[currentHeight][currentWidth] != null
						&& !allChosedLayersInOneLayer[currentHeight][currentWidth].locked)
					return Optional.of(allChosedLayersInOneLayer[currentHeight][currentWidth]);
			}
			currentWidth--;
		}
		/*
		 * increment width cursor because it now one position lower than we need because of previous
		 * "while" step
		 */
		currentWidth++;
		/*
		 * decrement height cursor because it now one position lower than we need because of previous
		 * "while" step
		 */
		currentHeight--;
		// from bottom left to top left -1
		while (currentHeight > startHeight - range) {
			if (checkBound(currentWidth, currentHeight)) {
				if (allChosedLayersInOneLayer[currentHeight][currentWidth] != null
						&& !allChosedLayersInOneLayer[currentHeight][currentWidth].locked)
					return Optional.of(allChosedLayersInOneLayer[currentHeight][currentWidth]);
			}
			currentHeight--;
		}
		return Optional.empty();
	}

	private boolean checkBound(int width, int height) {
		return width >= 0 && height >= 0 && width < allChosedLayersInOneLayer[0].length
				&& height < allChosedLayersInOneLayer.length;
	}

	@Override
	public void cancelTask() {
	}

}
