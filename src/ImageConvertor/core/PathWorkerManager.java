package ImageConvertor.core;

import java.io.IOException;
import java.lang.Thread.State;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ImageConvertor.data.Direction;
import ImageConvertor.data.Edge;
import ImageConvertor.core.coreInterfaces.Pathfinder;
import ImageConvertor.data.Chunk;
import ImageConvertor.views.desktop.View;

public class PathWorkerManager implements Pathfinder {
	Controller controller;
	int chunkSize, imageWidth, imageHeight;
	List<List<Chunk>> pointsList;
	List<Chunk[][]> pathContainer = new ArrayList<>();
	Chunk[][] searchMatrix;
	int boundWidth = 0, boundHeight = 0;
	List<Chunk> tmp;
	List<List<Chunk>> cur;
	Chunk[][] copyPoints;
	boolean isCanceled = false;
	Chunk[][] chunkMatrix;

	private int maxRange = 0;
	private int totalConnectedPointsLimit;
	private int limitConnectedPoints;
	private float rangeRate;
	private float weightRate;
	private float pathLengthDivider;
	private int iterationsCount;
	private float initalPathDivider;
	private float vaporizeDivider;
	private Queue<String> queue;

	public PathWorkerManager(Controller controller, int totalConnectedPointsLimit, int limitConnectedPoints,
			float rangeRate, float weightRate, float pathLengthDivider, int maxRange, float initalPathDivider,
			int iterationsCount, float vaporizeDivider, Queue<String> queue) {
		this(controller);
		this.totalConnectedPointsLimit = totalConnectedPointsLimit;
		this.limitConnectedPoints = limitConnectedPoints;
		this.rangeRate = rangeRate;
		this.weightRate = weightRate;
		this.pathLengthDivider = pathLengthDivider;
		this.maxRange = maxRange;
		this.iterationsCount = iterationsCount;
		this.initalPathDivider = initalPathDivider;
		this.vaporizeDivider = vaporizeDivider;
		this.queue = queue;
	}

	private PathWorkerManager(Controller c) {
		controller = c;
		chunkSize = c.getChunkSize();
		imageWidth = c.getImageWidth();
		imageHeight = c.getImageHeight();
		while (boundWidth < imageWidth) {
			boundWidth += chunkSize;
		}
		while (boundHeight < imageHeight) {
			boundHeight += chunkSize;
		}
		pointsList = controller.getChosedLayersForDraw();
		// clearIsLockedFlagInAllPoints();
	}

	@Override
	public List<Chunk> getPath() {
		return null;
	}

	private void reloadPointsContainer() {
		pathContainer.clear();
		if (pointsList.size() > 0) {
			for (int i = 0; i < pointsList.size(); i++) {
				pathContainer.add(getMatrix(i));
			}
			fillPathMatrix();
			controller.setChunks(searchMatrix[0].length);
		}
	}

	private Chunk[][] getMatrix(int nextList) {
		List<Chunk> temp = pointsList.get(nextList);
		Chunk[][] matrix = new Chunk[boundHeight / chunkSize][boundWidth / chunkSize];
		for (Chunk p : temp) {
			matrix[p.chunkPosition.y][p.chunkPosition.x] = p;
		}
		return matrix;
	}

	private List<List<Chunk>> getPath_() {

		List<Set<Chunk>> finalList = new ArrayList<>();

		// Set<Chunk> cnt = new HashSet<>();

		while (recursionInit(new HashSet<>())) {
			// System.out.println("init path creator...");
			queue.offer(controller.getLocaleText("init_path_construcctor"));
			// System.out.println("start create matrix");
			queue.offer(controller.getLocaleText("start_create_matrix"));

			Edge[][] ajMatrix = createAdjacencyMatrix();

			// System.out.println("matrix created, start compute");
			queue.offer(controller.getLocaleText("matrix_created"));

			List<Chunk> currentIterationBestPath = new ArrayList<>();
			AtomicInteger counter = new AtomicInteger(0);
			List<PathWorker> workers = new ArrayList<>();
			float lgth = Float.MAX_VALUE;
			Map<Float, List<Edge>> paths = new ConcurrentHashMap<>();
			int iterations = 0;
			IntStream.rangeClosed(0, Controller.N_THREADS - 1).forEach(i -> {
				PathWorker worker = new PathWorker(ajMatrix, tmp, controller, rangeRate, weightRate,
						ThreadLocalRandom.current().nextInt(tmp.size()), i, counter, paths);
				worker.start();
				workers.add(worker);
				counter.incrementAndGet();
			});
			while (iterations < iterationsCount && !isCanceled) {
				// System.out.println(String.format("next iteration started %d, length: %f", iterations, lgth));
				queue.offer(String.format(controller.getLocaleText("next_iteration"), iterations, lgth));
				// controller.messageExchanger.offer(String.format("next iteration started %d, length: %f",
				// iterations, lgth));
				while (counter.get() > 0 && !isCanceled) {
					try {
						for (PathWorker w : workers) {
							if (w.getState() == State.WAITING && !w.isWorkDone && !isCanceled) {
								synchronized (w.myMutex) {
									// System.out.println("notified");
									w.myMutex.notify();
								}
							}
						}
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				queue.offer(controller.getLocaleText("upd_weights"));
				vaporizeMatrixWeight(ajMatrix);
				queue.offer(controller.getLocaleText("upd_weoghts_done"));
				paths.entrySet().stream().forEach(e -> {
					float length_ = e.getKey();
					List<Edge> edges = e.getValue();
					updateMaxrixWeight(length_, edges, ajMatrix);
				});
				float min = (float) paths.keySet().stream().mapToDouble(e -> e).min().orElse(Float.MAX_VALUE);
				if (min < lgth) {
					lgth = min;
					currentIterationBestPath = paths.get(min).stream().map(e -> tmp.get(e.heightIndex)).toList();
				}
				paths.clear();
				// counter.set(workers.size());
				iterations++;
				for (PathWorker w : workers) {
					w.isWorkDone = false;
				}
			}
			for (int i = 0; i < workers.size(); i++) {
				if (workers.get(i).isAlive()) {
					synchronized (workers.get(i).myMutex) {
						workers.get(i).interrupt();
						workers.get(i).myMutex.notify();
						i = 0;
					}
				}
			}
			// System.out.println("all threads died.");
			queue.offer(controller.getLocaleText("threads_die"));
			if (isCanceled) {
				return null;
			}
			List<Chunk> pointsConnectedFromAllLayersInView = new ArrayList<>();
			for (Chunk outer : currentIterationBestPath) {
				c: for (List<Chunk> m : controller.getAllLayers()) {
					for (Chunk inner : m) {
						if (inner.index == outer.index) {
							pointsConnectedFromAllLayersInView.add(outer);
							continue c;
						}
					}
				}
			}

			Set<Chunk> pathFromCurrentPoints = new HashSet<>();

			pointsConnectedFromAllLayersInView.stream().forEach(e -> {
				pathFromCurrentPoints.add(e);
			});
			if (pathFromCurrentPoints.size() > 5 && !isCanceled) {
				// pathFromCurrentPoints.stream().forEach(finalList::add);
				finalList.add(pathFromCurrentPoints);
			}
		}
		if (isCanceled) {
			return null;
		}
		queue.offer(controller.getLocaleText("comp_end"));

		List<List<Chunk>> result = new ArrayList<>();
		for (Set<Chunk> s : finalList) {
			result.add(new ArrayList<>(s));
		}
		controller.setPathsPointList(result);
		return result;
		// .sorted((o1, o2) -> o2.stream().findFirst().get().index - o1.stream().findFirst().get().index);

	}

	private void fillPathMatrix() {
		Chunk[][] result = new Chunk[boundHeight / chunkSize][boundWidth / chunkSize];
		for (Chunk[][] p : pathContainer) {
			for (int i = 0; i < p.length; i++) {
				for (int j = 0; j < p[i].length; j++) {
					if (p[i][j] != null && p[i][j].direction != Direction.STUB) {
						result[i][j] = p[i][j];
					}
				}
			}
		}
		searchMatrix = result;
	}

	public void createSVG() {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<svg viewBox=\"0 0 %d %d\" xmlns=\"http://www.w3.org/2000/svg\">\n", imageWidth,
				imageHeight));

		List<List<Chunk>> path = controller.getFinalList();

		for (List<Chunk> list : path) {
			sb.append("<polyline fill=\"none\" stroke=\"#000000\" stroke-width=\"0.5\" points=\"");
			Chunk prev = list.get(0);
			for (Chunk p : list) {
				if (prev.startPoint.distance(p.startPoint) > controller.getChunkSize() * 5) {
					sb.append("\"/>");
					sb.append("<polyline fill=\"none\" stroke=\"#000000\" stroke-width=\"0.5\" points=\"");
				}
				sb.append(p.startPoint.x + "," + p.startPoint.y + "\n");
				prev = p;
			}
			sb.append("\"/>");
		}
		sb.append("</svg>");

		try {
			Files.write(Paths.get(View.DESKTOP_PATH + "\\" + controller.getFileName() + ".svg"),
					sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean recursionInit(Set<Chunk> result) {
		if (isCanceled) {
			return false;
		}
		queue.offer(controller.getLocaleText("rec_init"));
		reloadPointsContainer();
		int count = 0;
		for (int i = 0; i < searchMatrix.length; i++) {
			for (int j = 0; j < searchMatrix[i].length; j++) {
				if (searchMatrix[i][j] != null && !searchMatrix[i][j].locked) {
					count++;
				}
			}
		}
		// System.out.println("freePoints: " + count);
		queue.offer(controller.getLocaleText("free_points") + ": " + count);

		short[] entry = getEntryPoint();
		int w = entry[1];
		int h = entry[0];
		if (h == -1 || w == -1) {
			// System.out.println("CANT FIND ENTRY");
			queue.offer(controller.getLocaleText("cant_find_entry"));
			return false;
		}
		// Set<Points> result = new HashSet<>();
		Chunk cur = searchMatrix[h][w];
		cur.locked = true;
		recursion(cur, result);

		tmp = result.stream().collect(Collectors.toList());
		// System.out.println("counted points: " + result.size());
		queue.offer(controller.getLocaleText("counted_points") + ": " + result.size());
		/*
		 * if (result.size() < limitConnectedPoints) {
		 * return recursionInit(result);
		 * }
		 * 
		 * Points[][] copy = new Points[searchMatrix.length][searchMatrix[0].length];
		 * for (Points p : tmp) {
		 * copy[p.myPosition.y][p.myPosition.x] = p;
		 * }
		 * copyPoints = copy;
		 */
		// System.out.println("rec finished");
		queue.offer(controller.getLocaleText("rec_fin"));
		// result.clear();
		return true;
	}

	private void recursion(Chunk current, Set<Chunk> result) {
		// collectedPoints++;
		if (/* collectedPoints > totalConnectedPointsLimit || */result.size() > totalConnectedPointsLimit) {
			return;
		}
		result.add(current);
		List<Chunk> container = getRelatives(current.chunkPosition.y, current.chunkPosition.x);
		for (Chunk p : container) {
			// collectedPoints++;
			result.add(p);
			recursion(p, result);
		}
	}

	private List<Chunk> getRelatives(int curHeight, int curWidth) {
		List<Chunk> result = new ArrayList<>();
		/*
		 * Chunk temp;
		 * while (true) {
		 * 
		 * if (check(curHeight - 1, curWidth - 1)) {
		 * temp = searchMatrix[curHeight - 1][curWidth - 1];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * 
		 * } else if (check(curHeight - 1, curWidth)) {
		 * temp = searchMatrix[curHeight - 1][curWidth];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * 
		 * } else if (check(curHeight - 1, curWidth + 1)) {
		 * temp = searchMatrix[curHeight - 1][curWidth + 1];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * 
		 * } else if (check(curHeight, curWidth + 1)) {
		 * temp = searchMatrix[curHeight][curWidth + 1];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * 
		 * } else if (check(curHeight + 1, curWidth + 1)) {
		 * temp = searchMatrix[curHeight + 1][curWidth + 1];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * 
		 * } else if (check(curHeight + 1, curWidth)) {
		 * temp = searchMatrix[curHeight + 1][curWidth];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * 
		 * } else if (check(curHeight + 1, curWidth - 1)) {
		 * temp = searchMatrix[curHeight + 1][curWidth - 1];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * 
		 * } else if (check(curHeight + 1, curWidth)) {
		 * temp = searchMatrix[curHeight + 1][curWidth];
		 * result.add(temp);
		 * temp.locked = true;
		 * continue;
		 * }
		 * break;
		 * }
		 */

		int temp = 1;
		int startHeight = curHeight - temp;
		int endHeight = curHeight + temp;
		int startWidth = curWidth - temp;
		int endWidth = curWidth + temp;
		do {
			for (; startWidth < endWidth; startWidth++) {
				if (check(startHeight, startWidth)) {
					Chunk cur = searchMatrix[startHeight][startWidth];
					cur.locked = true;
					result.add(cur);
				}
			}
			for (; startHeight < endHeight; startHeight++) {
				if (check(startHeight, startWidth)) {
					Chunk cur = searchMatrix[startHeight][startWidth];
					cur.locked = true;
					result.add(cur);
				}
			}
			for (; startWidth > curWidth - temp; startWidth--) {
				if (check(startHeight, startWidth)) {
					Chunk cur = searchMatrix[startHeight][startWidth];
					cur.locked = true;
					result.add(cur);
				}
			}
			for (; startHeight > curHeight - temp; startHeight--) {
				if (check(startHeight, startWidth)) {
					Chunk cur = searchMatrix[startHeight][startWidth];
					cur.locked = true;
					result.add(cur);
				}
			}
			temp++;
			startWidth = curWidth - temp;
			endWidth = curWidth + temp;
			startHeight = curHeight + temp;
			endHeight = curHeight - temp;
		} while (temp <= maxRange);

		Comparator<Chunk> comp = new Comparator<Chunk>() {
			@Override
			public int compare(Chunk o1, Chunk o2) {
				return o1.index > o2.index ? 1 : o1.index < o2.index ? -1 : 0;
			}
		};
		result.sort(comp);
		return result;
	}

	private boolean check(int height, int width) {
		return width > -1 && height > -1 && height < searchMatrix.length && width < searchMatrix[0].length
				&& searchMatrix[height][width] != null && !searchMatrix[height][width].locked
				&& searchMatrix[height][width].direction != Direction.STUB;
	}

	private short[] getEntryPoint() {
		short[] ans = new short[] { -1, -1 };
		for (short height = 0; height < searchMatrix.length; height++) {
			for (short width = 0; width < searchMatrix[0].length; width++) {
				if (check(height, width)) {
					ans[0] = height;
					ans[1] = width;
					// System.out.println("next entry: " + ans[0] + " <-> " + ans[1]);
					return ans;
				}
			}
		}
		// System.out.println("next entry: " + ans[0] + " <-> " + ans[1]);
		return ans;
	}

	private Edge[][] createAdjacencyMatrix() {
		int size = tmp.size();
		Edge[][] matrix = new Edge[size][];

		for (int heightIndex = 0; heightIndex < tmp.size(); heightIndex++) {

			Chunk firstPointInRow = tmp.get(heightIndex);
			Edge firstAntInRow = new Edge(heightIndex, 0, 0f);
			// firstAntInRow.visited = true;
			matrix[heightIndex] = new Edge[size];
			matrix[heightIndex][0] = firstAntInRow;

			int widthIndex = heightIndex + 1;

			for (int w = 1; w < matrix[heightIndex].length; w++) {
				Chunk nextPointsInRow = tmp.get(widthIndex);
				float distanceBetweenPoints = initalPathDivider
						/ (float) firstPointInRow.chunkPosition.distance(nextPointsInRow.chunkPosition);
				Edge nextEdgeInRow = new Edge(heightIndex, widthIndex, distanceBetweenPoints);
				matrix[heightIndex][w] = nextEdgeInRow;
				widthIndex++;
			}
			size--;
		}
		return matrix;
	}

	private void updateMaxrixWeight(float length, List<Edge> container, Edge[][] ajMatrix) {
		float addition = pathLengthDivider / length;
		for (Edge e : container) {
			int height = e.heightIndex;
			int width = e.widthIndex;
			ajMatrix[height][width].weight += addition;
		}
	}

	private void vaporizeMatrixWeight(Edge[][] ajMatrix) {
		for (int i = 0; i < ajMatrix.length; i++) {
			for (int j = 0; j < ajMatrix[i].length; j++) {
				if (ajMatrix[i][j] != null) {
					ajMatrix[i][j].weight *= vaporizeDivider;
				}
			}
		}
	}

	public void cancelTask() {
		isCanceled = true;
	}

}
