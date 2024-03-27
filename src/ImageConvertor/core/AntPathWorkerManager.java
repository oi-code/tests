package ImageConvertor.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ImageConvertor.data.Chunk;
import ImageConvertor.views.desktop.View;

public class AntPathWorkerManager implements Pathfinder {

	private Controller controller;
	private Chunk[][] chunkMatrix;
	private int totalConnectedPointsLimit;
	private int limitConnectedPoints;
	private float rangeRate;
	private float weightRate;
	private float pathLengthDivider;
	private int maxRange;
	private float pathDivider;
	private int iterations;
	private float vaporizeRate;
	private Queue<String> queue;
	private int chunkSize;
	private int totalChunks;
	private boolean isTaskCanceled;

	public AntPathWorkerManager() {

	};

	public AntPathWorkerManager(Controller controller, int totalConnectedPointsLimit, int limitConnectedPoints,
			float rangeRate, float weightRate, float pathLengthDivider, int maxRange, float pathDivider, int iterations,
			float vaporizeRate, Queue<String> queue) {
		super();
		this.controller = controller;
		this.totalConnectedPointsLimit = totalConnectedPointsLimit;
		this.limitConnectedPoints = limitConnectedPoints;
		this.rangeRate = rangeRate;
		this.weightRate = weightRate;
		this.pathLengthDivider = pathLengthDivider;
		this.maxRange = maxRange;
		this.pathDivider = pathDivider;
		this.iterations = iterations;
		this.vaporizeRate = vaporizeRate;
		this.queue = queue;
		this.chunkSize = controller.getChunkSize();
	}

	@Override
	public List<List<Chunk>> getSequencesOfPaths() {

		List<Set<Chunk>> clouds = createClouds();

		List<List<Chunk>> result = new ArrayList<>();
		for (Set<Chunk> s : clouds) {
			result.add(new ArrayList<>(s));
		}
		controller.setPathsPointList(result);
		return result;
	}

	private List<Set<Chunk>> createClouds() {
		chunkMatrix = createOneLayerFromAllLayers();
		List<Set<Chunk>> clouds = getCloudOfChunks(/*Integer.MAX_VALUE*/20);
		return clouds;
	}

	/*
	 * take selected layers after parsed image and create one-layer-chunk matrix from all layers
	 */
	private Chunk[][] createOneLayerFromAllLayers() {
		/**
		 * copy list to avoid {@link java.util.ConcurrentModificationException}
		 */
		List<List<Chunk>> allLayers = new ArrayList<>(controller.getChosedLayersForDraw());
		int height = (controller.getImageHeight() - controller.getImageHeight() % chunkSize) / chunkSize;
		int width = (controller.getImageWidth() - controller.getImageWidth() % chunkSize) / chunkSize;

		Chunk[][] allChosedLayersInOneLayer = new Chunk[height][width];

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
		return allChosedLayersInOneLayer;
	}

	/*
	 * create clouds before finding path
	 */
	private List<Set<Chunk>> getCloudOfChunks(int limitOfChunkInOneCloud) {
		List<Set<Chunk>> resultCloud = new ArrayList<>();
		int currentProgress = 0;
		short cloudIndex = 1;
		while (currentProgress < totalChunks && !isTaskCanceled) {
			for (int i = 0; i < chunkMatrix.length; i++) {
				for (int j = 0; j < chunkMatrix[i].length; j++) {
					Chunk currentChunk = chunkMatrix[i][j];
					/*
					 * get next entry
					 */
					if (currentChunk != null && !currentChunk.locked) {
						queue.offer("next entry found. count of paths " + currentProgress);
						Set<Chunk> currentChunkCloud = getNextOneCloudOfChunks(currentChunk, cloudIndex++);
						resultCloud.add(currentChunkCloud);
						currentProgress += currentChunkCloud.size();
						cloudIndex++;
					}
				}
			}
		}
		return resultCloud;
	}

	private Set<Chunk> getNextOneCloudOfChunks(Chunk seed, short cloudIndex) {
		queue.offer("start next cloud");
		Set<Chunk> result = new HashSet<>();

		result.add(seed);
		seed.locked = true;
		/* after adding seed to result set, we can start compute cloud */
		createCloud(result, cloudIndex);
		return result;
	}

	private void createCloud(Set<Chunk> input, short cloudIndex) {
		queue.offer("check holes");

		Set<Chunk> check = new HashSet<>();
		Set<Chunk> buffer = new HashSet<>();
		/* here we trying to find free chunks around every chunk */
		input.stream().forEach(element -> {
			setAroundChunks(element);
			List<Chunk> freeChunks = element.getFreeAroundChunks();
			if (freeChunks.size() > 0) {
				freeChunks.stream().forEach(e -> {
					check.add(e);
					e.locked = true;
					e.cloudIndex = cloudIndex;
				});
			}
		});
		queue.offer("holes found: " + check.size());
		input.addAll(check);
		/* if we found any free chunks, we can expanse this cloud */
		int counter = 0;
		while (check.size() > 0 && !isTaskCanceled) {
			/* free memory */
			System.gc();
			check.clear();
			counter++;
			/* add every new free element to result and lock it */
			Iterator<Chunk> iter = new HashSet<>(input).iterator();
			while (iter.hasNext() && !isTaskCanceled) {

				Chunk element = iter.next();
				// for (Chunk element : input.keySet()) {
				setAroundChunks(element);
				List<Chunk> freeChunks = element.getFreeAroundChunks();
				if (freeChunks.size() == 0) {
					input.remove(element);
					buffer.add(element);
					continue;
				}
				freeChunks.stream().forEach(e -> {
					check.add(e);
					e.locked = true;
					e.cloudIndex = cloudIndex;
				});
			}
			/* add all new chunks to result */
			input.addAll(check);
			if (counter == 100) {
				queue.offer("holes:" + check.size() + ", connected " + (buffer.size() + check.size() + input.size())
						+ " of " + totalChunks + ",cloud:" + cloudIndex);
				counter = 0;
			}

		}
		input.addAll(buffer);
		input.addAll(check);
	}

	/*
	 * get EVERY element around chunk and set edges between
	 */
	private void setAroundChunks(Chunk seed) {
		// Set<Chunk> aroundChunks = new HashSet<>();
		if (seed.avalivableChunks.size() != 0) {
			return;
		}
		List<short[]> aroundeIndexes;
		int currentAroundIndex = 1;
		while (currentAroundIndex < rangeRate) {
			aroundeIndexes = getAroundIndexes(seed, currentAroundIndex);
			currentAroundIndex++;
			for (short[] indexes : aroundeIndexes) {
				short width = indexes[0];
				short height = indexes[1];
				Chunk check = chunkMatrix[height][width];
				if (check != null) {
					// aroundChunks.add(check);
					seed.avalivableChunks.add(check);
					AntEdge edge = new AntEdge(seed, check);
					seed.edges.add(edge);
					check.edges.add(edge);
				}
			}
		}
	}

	private boolean checkBound(short widthPosition, short heightPosition) {
		return widthPosition >= 0 && widthPosition < chunkMatrix[0].length && heightPosition >= 0
				&& heightPosition < chunkMatrix.length;

	}

	/*
	 * get indexes around seed taking into account the distance(multiplier) between chunks
	 * method don't check any matrix's boundaries
	 */
	private List<short[]> getAroundIndexes(Chunk seed, int rangeBetweenChunks) {

		short startWidth = (short) seed.chunkPosition.x;
		short startHeight = (short) seed.chunkPosition.y;

		short currentWidth = (short) (startWidth - rangeBetweenChunks);
		short currentHeight = (short) (startHeight - rangeBetweenChunks);

		List<short[]> indexes = new ArrayList<>();

		/*
		 * why is here "<=" instead of '<'?
		 * because we need to find chunks include central "seed" chunk
		 * so if we pass to method even multiplier, 2 as example, we have to find indexes 0 1 2 3 4. 2 is
		 * central.
		 * if we pass odd multiplier, 1 as example, we have to find indexes 0 1 2. 1 is central index.
		 * 
		 * Considerations about how to pass through matrix:
		 * passing through matrix will be done in such sequence:
		 * if we have square with side of 7 chunks, passing through it with clockwise direction will be like
		 * 
		 * 7 indexes of top side
		 * 6 indexes of right side and bottom side
		 * 5 indexes of left side
		 * 
		 * to come into the center of square matrix taking into account every chunk in matrix
		 * next rotation will be start with 5 chunk's length after first pass of 7 chunk's length
		 * if first index in rotation was 0 0 and length was 7 chunks
		 * then second pass will be start with indexes 1 1 and length of 5 chunks
		 * full pass into the center of square into the center will look like
		 * 7 6 6 5 5 4 4 3 3 2 2 1 0
		 */
		// from top left to top right
		while (currentWidth <= startWidth + rangeBetweenChunks) {
			if (checkBound(currentWidth, currentHeight)) {
				indexes.add(new short[] { currentWidth, currentHeight });
			}
			currentWidth++;
		}
		/*
		 * width's cursor now is in one position bigger than we need because of "while" statement
		 * so we have to decrement it before run next step
		 */
		currentWidth--;
		/*
		 * here we increment height cursor because we don't need to add chunk from previous "while" step
		 * again
		 */
		currentHeight++;
		// why here is "<=" instead of "<" was explained above. Situation the same.
		// from top right to bottom right
		while (currentHeight <= startHeight + rangeBetweenChunks) {
			if (checkBound(currentWidth, currentHeight)) {
				indexes.add(new short[] { currentWidth, currentHeight });
			}
			currentHeight++;
		}
		// we decrement height cursor because it now one position higher because of previous "while" step
		currentHeight--;
		// we decrement width cursor because we don't need to add chunk from previous "while" step
		// {@link AntPathWorketManager.java:<<350>>}
		currentWidth--;
		// here ">=" instead of ">" because we need to return to start width position.
		// from bottom right to bottom left
		while (currentWidth >= startWidth - rangeBetweenChunks) {
			if (checkBound(currentWidth, currentHeight)) {
				indexes.add(new short[] { currentWidth, currentHeight });
			}
			currentWidth--;
		}
		/*
		 * we increment width cursor because it now one position lower than we need because of previous
		 * "while" step
		 */
		currentWidth++;
		/*
		 * we decrement height cursor because it now one position lower than we need because of previous
		 * "while" step
		 */
		currentHeight--;
		// from bottom left to top left -1
		while (currentHeight > startHeight - rangeBetweenChunks) {
			if (checkBound(currentWidth, currentHeight)) {
				indexes.add(new short[] { currentWidth, currentHeight });
			}
			currentHeight--;
		}
		return indexes;

	}

	@Override
	public void cancelTask() {
		isTaskCanceled = true;
		queue.offer("task was canceled");
	}
}