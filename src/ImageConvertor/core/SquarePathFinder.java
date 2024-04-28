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
	private int currentSide = 0;
	private int distance = 1;

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
		/*
		 * looking for entry
		 */
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
		seed.locked = true;
		Optional<Chunk> next;
		while (result.size() < totalChunks) {
			/*
			 * top
			 */
			next = getNextChunk(seed);
			if (next.isEmpty()) {
				/*
				 * right
				 */
				next = getNextChunk(seed);
				if (next.isEmpty()) {
					/*
					 * bottom
					 */
					next = getNextChunk(seed);
					if (next.isEmpty()) {
						/*
						 * left
						 */
						next = getNextChunk(seed);
					}
				}
			}
			if (next.isEmpty()) {
				changeSide();
				distance++;
			} else {
				distance = 1;
				Chunk _next = next.get();
				_next.locked = true;
				seed = _next;
				result.add(_next);
			}

		}
		return result;

	}

	private Optional<Chunk> getNextChunk(Chunk seed) {
		Optional<Chunk> next;
		switch (currentSide) {
		case 0:
			next = searchTop(seed);
			if (next.isPresent()) {
				return next;
			}
			changeSide();
			break;
		case 1:
			next = searchRight(seed);
			if (next.isPresent()) {
				return next;
			}
			changeSide();
			break;
		case 2:
			next = searchBot(seed);
			if (next.isPresent()) {
				return next;
			}
			changeSide();
			break;
		case 3:
			next = searchLeft(seed);
			if (next.isPresent()) {
				return next;
			}
			changeSide();
			break;
		default:
			break;
		}
		return Optional.empty();
	}

	private void changeSide() {
		currentSide++;
		if (currentSide == 4) {
			currentSide = 0;
		}
	}

	/*
	 * xxx
	 * o-o
	 * ooo
	 */
	private Optional<Chunk> searchTop(Chunk seed) {
		if (distance == 1) {
			Optional<Chunk> next;
			next = chunk_1(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}
			next = chunk_2(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}
			next = chunk_4(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}
			next = chunk_3(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}
			next = chunk_6(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}
			next = chunk_5(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}
			next = chunk_8(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}
			next = chunk_7(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}

		}
		int chunkWidth = seed.chunkPosition.x;
		int chunkHeight = seed.chunkPosition.y;

		int currentWidth = chunkWidth - distance;
		int currentHeight = chunkHeight - distance;

		while (currentWidth <= chunkWidth + distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
				Chunk next = allChosedLayersInOneLayer[currentHeight][currentWidth];
				if (next != null && !next.locked) {
					// if (distance == 1) {
					// return validateTop(seed, next);
					// }
					return Optional.of(next);
				}
			}
			currentWidth++;
		}
		return Optional.empty();
	}

	/*
	 * oox
	 * o-x
	 * oox
	 */
	private Optional<Chunk> searchRight(Chunk seed) {
		if (distance == 1) {
			Optional<Chunk> next;

			next = chunk_3(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}
			next = chunk_4(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}
			next = chunk_6(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}
			next = chunk_5(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}
			next = chunk_8(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}
			next = chunk_7(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}
			next = chunk_2(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}
			next = chunk_1(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}

		}
		int chunkWidth = seed.chunkPosition.x;
		int chunkHeight = seed.chunkPosition.y;

		int currentWidth = chunkWidth + distance;
		int currentHeight = chunkHeight - distance;

		while (currentHeight <= chunkHeight + distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
				Chunk next = allChosedLayersInOneLayer[currentHeight][currentWidth];
				if (next != null && !next.locked) {
					// if (distance == 1) {
					// return validateRight(seed, next);
					// }
					return Optional.of(next);
				}
			}
			currentHeight++;
		}
		return Optional.empty();
	}

	/*
	 * ooo
	 * o-o
	 * xxx
	 */
	private Optional<Chunk> searchBot(Chunk seed) {
		if (distance == 1) {
			Optional<Chunk> next;

			next = chunk_5(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}
			next = chunk_6(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}
			next = chunk_7(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}
			next = chunk_8(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}
			next = chunk_2(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}
			next = chunk_1(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}
			next = chunk_4(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}
			next = chunk_3(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}

		}
		int chunkWidth = seed.chunkPosition.x;
		int chunkHeight = seed.chunkPosition.y;

		int currentWidth = chunkWidth + distance;
		int currentHeight = chunkHeight + distance;

		while (currentWidth >= chunkWidth - distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
				Chunk next = allChosedLayersInOneLayer[currentHeight][currentWidth];
				if (next != null && !next.locked) {
					// if (distance == 1) {
					// return validateBot(seed, next);
					// }
					return Optional.of(next);
				}
			}
			currentWidth--;
		}
		return Optional.empty();
	}

	/*
	 * xoo
	 * x-o
	 * xoo
	 */
	private Optional<Chunk> searchLeft(Chunk seed) {
		if (distance == 1) {
			Optional<Chunk> next;
			next = chunk_7(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}
			next = chunk_8(seed);
			if (next.isPresent()) {
				currentSide = 2;
				return next;
			}
			next = chunk_2(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}
			next = chunk_1(seed);
			if (next.isPresent()) {
				currentSide = 3;
				return next;
			}
			next = chunk_4(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}
			next = chunk_3(seed);
			if (next.isPresent()) {
				currentSide = 0;
				return next;
			}
			next = chunk_6(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}
			next = chunk_5(seed);
			if (next.isPresent()) {
				currentSide = 1;
				return next;
			}

		}
		int chunkWidth = seed.chunkPosition.x;
		int chunkHeight = seed.chunkPosition.y;

		int currentWidth = chunkWidth - distance;
		int currentHeight = chunkHeight + distance;

		while (currentHeight >= chunkHeight - distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
				Chunk next = allChosedLayersInOneLayer[currentHeight][currentWidth];
				if (next != null && !next.locked) {
					// if (distance == 1) {
					// return validateLeft(seed, next);
					// }
					return Optional.of(next);
				}
			}
			currentHeight--;
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_1(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x - 1, seed.chunkPosition.y - 1)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y - 1][seed.chunkPosition.x - 1];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_2(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x, seed.chunkPosition.y - 1)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y - 1][seed.chunkPosition.x];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_3(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x + 1, seed.chunkPosition.y - 1)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y - 1][seed.chunkPosition.x + 1];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_4(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x + 1, seed.chunkPosition.y)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y][seed.chunkPosition.x + 1];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_5(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x + 1, seed.chunkPosition.y + 1)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y + 1][seed.chunkPosition.x + 1];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_6(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x, seed.chunkPosition.y + 1)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y + 1][seed.chunkPosition.x];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_7(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x - 1, seed.chunkPosition.y + 1)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y + 1][seed.chunkPosition.x - 1];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional<Chunk> chunk_8(Chunk seed) {
		if (isCoordinatesInMatrix(seed.chunkPosition.x - 1, seed.chunkPosition.y)) {
			Chunk a = allChosedLayersInOneLayer[seed.chunkPosition.y][seed.chunkPosition.x - 1];
			if (a != null && !a.locked) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	// @Override
	public List<Chunk> AgetPath() {
		/*
		 * result path container
		 */
		List<Chunk> result = new LinkedList<>();
		/*
		 * legacy of copy-paste
		 */
		Chunk[][] cloud = allChosedLayersInOneLayer;
		Chunk seed = null;
		/*
		 * looking for entry
		 */
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
		seed.locked = true;
		int distance = 1;
		/*
		 * search next chunk in distance 1 (pixel, chunk, ect)
		 */
		Optional<Chunk> next = getNextChunk_distanceOneChunk(seed);
		while (result.size() < totalChunks) {
			if (next.isPresent()) {
				distance = 1;
				Chunk _next = next.get();
				result.add(_next);
				_next.locked = true;
				next = getNextChunk_distanceOneChunk(_next);
			} else {
				/*
				 * if chunk in distance 1 (pixel, chunk, ect) is not exist,
				 * start to increase distance and check matrix for existing acceptable chunk
				 */
				distance++;
				Chunk _last = result.get(result.size() - 1);
				next = getNextChunk_distanceMaxRangeChunk(_last, distance);
				while (next.isEmpty() && distance < maxRange) {
					// System.out.println("here");
					distance++;
					next = getNextChunk_distanceMaxRangeChunk(_last, distance);
				}
			}
		}
		return result;
	}

	/*
	 * check chunks in distance 1 (pixel, chunk, ect)
	 */
	private Optional<Chunk> getNextChunk_distanceOneChunk(Chunk seed) {
		int seedHeight = seed.chunkPosition.y;
		int seedWidth = seed.chunkPosition.x;
		Chunk next = null;
		/*
		 * OOO
		 * X-O
		 * OOO
		 */
		if (isCoordinatesInMatrix(seedWidth - 1, seedHeight - 1)) {
			next = allChosedLayersInOneLayer[seedHeight - 1][seedWidth - 1];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		/*
		 * XOO
		 * O-O
		 * OOO
		 */
		if (isCoordinatesInMatrix(seedWidth - 1, seedHeight - 1)) {
			next = allChosedLayersInOneLayer[seedHeight - 1][seedWidth - 1];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		/*
		 * OXO
		 * O-O
		 * OOO
		 */
		if (isCoordinatesInMatrix(seedWidth, seedHeight - 1)) {
			next = allChosedLayersInOneLayer[seedHeight - 1][seedWidth];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		/*
		 * OOX
		 * O-O
		 * OOO
		 */
		if (isCoordinatesInMatrix(seedWidth + 1, seedHeight - 1)) {
			next = allChosedLayersInOneLayer[seedHeight - 1][seedWidth + 1];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		/*
		 * OOO
		 * O-X
		 * OOO
		 */
		if (isCoordinatesInMatrix(seedWidth + 1, seedHeight)) {
			next = allChosedLayersInOneLayer[seedHeight][seedWidth + 1];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		/*
		 * OOO
		 * O-O
		 * OOX
		 */
		if (isCoordinatesInMatrix(seedWidth + 1, seedHeight + 1)) {
			next = allChosedLayersInOneLayer[seedHeight + 1][seedWidth + 1];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		/*
		 * OOO
		 * O-O
		 * OXO
		 */
		if (isCoordinatesInMatrix(seedWidth, seedHeight + 1)) {
			next = allChosedLayersInOneLayer[seedHeight + 1][seedWidth];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		/*
		 * OOO
		 * O-O
		 * XOO
		 */
		if (isCoordinatesInMatrix(seedWidth - 1, seedHeight + 1)) {
			next = allChosedLayersInOneLayer[seedHeight + 1][seedWidth - 1];
			if (next != null && !next.locked) {
				return Optional.of(next);
			}
		}
		return Optional.empty();
	}

	/*
	 * check chunk in distance more than 1 (pixel, chunk, ect)
	 */
	private Optional<Chunk> getNextChunk_distanceMaxRangeChunk(Chunk seed, int distance) {

		short startWidth = (short) seed.chunkPosition.x;
		short startHeight = (short) seed.chunkPosition.y;

		short currentWidth = (short) (startWidth - distance);
		short currentHeight = (short) (startHeight - distance);

		// from top left to top right
		while (currentWidth <= startWidth + distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
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
		while (currentHeight <= startHeight + distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
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
		while (currentWidth >= startWidth - distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
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
		while (currentHeight > startHeight - distance) {
			if (isCoordinatesInMatrix(currentWidth, currentHeight)) {
				if (allChosedLayersInOneLayer[currentHeight][currentWidth] != null
						&& !allChosedLayersInOneLayer[currentHeight][currentWidth].locked)
					return Optional.of(allChosedLayersInOneLayer[currentHeight][currentWidth]);
			}
			currentHeight--;
		}
		return Optional.empty();
	}

	private boolean isCoordinatesInMatrix(int matrixWidth, int matrixHeight) {
		return matrixWidth >= 0 && matrixHeight >= 0 && matrixWidth < allChosedLayersInOneLayer[0].length
				&& matrixHeight < allChosedLayersInOneLayer.length;
	}

	@Override
	public void cancelTask() {
	}

}
