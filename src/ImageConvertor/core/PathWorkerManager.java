package ImageConvertor.core;

import java.awt.Point;
import java.io.IOException;
import java.lang.Thread.State;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ImageConvertor.data.Direction;
import ImageConvertor.data.Edge;
import ImageConvertor.data.Points;
import ImageConvertor.views.desktop.View;

public class PathWorkerManager {
	Controller controller;
	int chunkSize, imageWidth, imageHeight;
	List<List<Points>> pointsList;
	List<Points[][]> pathContainer = new ArrayList<>();
	Points[][] searchMatrix;
	int boundWidth = 0, boundHeight = 0;
	List<Points> tmp;
	List<List<Points>> cur;
	Points[][] copyPoints;
	boolean isCanceled = false;

	private int maxRange;
	private int totalConnectedPointsLimit;
	private int limitConnectedPoints;
	private float rangeRate;
	private float weightRate;
	private float pathLengthDivider;
	private int iterationsCount;
	private float initalPathDivider;
	private float vaporizeDivider;
	private ArrayBlockingQueue<String> queue;

	public PathWorkerManager(Controller controller, int totalConnectedPointsLimit, int limitConnectedPoints,
			float rangeRate, float weightRate, float pathLengthDivider, int maxRange, float initalPathDivider,
			int iterationsCount, float vaporizeDivider, ArrayBlockingQueue<String> queue) {
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
		this.queue=queue;
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
		pointsList = controller.getForDrawContainer();
		clearIsLockedFlagInAllPoints();
	}

	private void reloadPointsContainer() {
		pathContainer.clear();
		if(pointsList.size()>0) {
		for (int i = 0; i < pointsList.size(); i++) {
			pathContainer.add(getMatrix(i));
		}
		fillPathMatrix();
		controller.setChunks(searchMatrix[0].length);}
	}

	private Points[][] getMatrix(int nextList) {
		List<Points> temp = pointsList.get(nextList);
		Points[][] matrix = new Points[boundHeight / chunkSize][boundWidth / chunkSize];
		for (Points p : temp) {
			matrix[p.myPosition.y][p.myPosition.x] = p;
		}
		return matrix;
	}

	public void getPath() {
		List<List<Point>> finalList = new ArrayList<>();
		Set<Points> cnt = new HashSet<>();
		while (recursionInit(cnt)) {

			// System.out.println("init path creator...");
			queue.offer(controller.getLocaleText("init_path_construcctor"));
			// System.out.println("start create matrix");
			queue.offer(controller.getLocaleText("start_create_matrix"));

			Edge[][] ajMatrix = createAdjacencyMatrix();

			// System.out.println("matrix created, start compute");
			queue.offer(controller.getLocaleText("matrix_created"));

			List<Points> currentIterationBestPath = null;
			AtomicInteger counter = new AtomicInteger(0);
			List<PathWorker> workers = new ArrayList<>();
			float lgth = Float.MAX_VALUE;
			Map<Float, List<Edge>> paths = new ConcurrentHashMap<>();
			int iterations = 0;
			IntStream.rangeClosed(0, Controller.N_THREADS - 1).forEach(i -> {
				PathWorker w = new PathWorker(ajMatrix, tmp, controller, rangeRate, weightRate,
						ThreadLocalRandom.current().nextInt(tmp.size()), i, counter, paths);
				w.start();
				workers.add(w);
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
						TimeUnit.MILLISECONDS.sleep(10);
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
				float min = (float) paths.keySet().stream().mapToDouble(e -> e).min().getAsDouble();
				if (min < lgth) {
					lgth = min;
					currentIterationBestPath = paths.get(min).stream().map(e -> tmp.get(e.heightIndex)).toList();
				}
				paths.clear();
				counter.set(workers.size());
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
				return;
			}
			List<Points> pointsConnectedFromAllLayersInView = new ArrayList<>();
			for (Points outer : currentIterationBestPath) {
				c: for (List<Points> m : controller.getAllLayersContainer()) {
					for (Points inner : m) {
						if (inner.index == outer.index) {
							pointsConnectedFromAllLayersInView.add(outer);
							continue c;
						}
					}
				}
			}

			List<Point> pathFromCurrentPoints = new ArrayList<>();

			pointsConnectedFromAllLayersInView.stream().forEach(e -> {
				pathFromCurrentPoints.add(e.startPoint);
				pathFromCurrentPoints.add(e.endPoint);
			});
			if (pathFromCurrentPoints.size() > 5 && !isCanceled) {
				finalList.add(pathFromCurrentPoints);
			}
		}
		/*
		 * while (iterations < 10) {
		 * System.gc();
		 * System.out.println(String.format("iteration: %d, curLength: %f", iterations, lgth));
		 * for (int i = 0; i < View.N_THREADS; i++) {
		 * Worker apf = new Worker(ajMatrix, tmp, controller, 20f, 1f,
		 * ThreadLocalRandom.current().nextInt(tmp.size()), i, counter);
		 * workers.add(apf);
		 * // service.submit(apf);
		 * // count++;
		 * }
		 *
		 * try {
		 * while (count > 0) {
		 * Future<Object[]> a = service.take();
		 * if (a.isDone()) {
		 * Object[] result = a.get();
		 * List<Edge> list = (List<Edge>) result[1];
		 * float nextPathLength = (float) result[0];
		 * if (nextPathLength < lgth) {
		 * resultEdges = list.stream().map(e -> tmp.get(e.heightIndex)).toList();
		 * lgth = nextPathLength;
		 * }
		 * count--;
		 * // System.out.println("update matrix");
		 * updateMaxrixWeight(nextPathLength, list, ajMatrix);
		 * } else {
		 * continue;
		 * }
		 * }
		 * 
		 * } catch (InterruptedException | ExecutionException e) {
		 * e.printStackTrace();
		 * }
		 *
		 * iterations++;
		 * }
		 */
		if (isCanceled) {
			return;
		}
		// System.out.println("compute end. start draw");
		queue.offer(controller.getLocaleText("comp_end"));
		controller.setPathsPointList(finalList);
		// System.out.println(finalList.size());
		// PathsImagePreview pip = new PathsImagePreview(controller, finalList);
		// PathsImagePreview pip = new PathsImagePreview(controller);
		// pip.showImage();
	}

	private void fillPathMatrix() {
		Points[][] result = new Points[boundHeight / chunkSize][boundWidth / chunkSize];
		for (Points[][] p : pathContainer) {
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

		/*
		 * List<List<Point>> path = new ArrayList<>();
		 * controller.getForDrawContainer().stream().forEach(e -> {
		 * List<Point> temp = new ArrayList<>();
		 * e.stream().forEach(i -> {
		 * temp.add(i.startPoint);
		 * temp.add(i.endPoint);
		 * });
		 * path.add(temp);
		 * });
		 */
		List<List<Point>> path = controller.getFinalList();

		// stroke-width=\"0.1\" stroke-opacity=\"1\"

		for (List<Point> list : path) {
			sb.append("<polyline fill=\"none\" stroke=\"#000000\" stroke-width=\"0.5\" points=\"");
			Point prev = list.get(0);
			for (Point p : list) {
				if (prev.distance(p) > controller.getChunkSize() * 5) {
					sb.append("\"/>");
					sb.append("<polyline fill=\"none\" stroke=\"#000000\" stroke-width=\"0.5\" points=\"");
				}
				sb.append(p.x + "," + p.y + "\n");
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

	private boolean recursionInit(Set<Points> result) {
		if (isCanceled) {
			return false;
		}
		// System.out.println("rec init");
		queue.offer(controller.getLocaleText("rec_init"));
		reloadPointsContainer();
		int count = 0;
		for (int i = 0; i < searchMatrix.length; i++) {
			for (int j = 0; j < searchMatrix[i].length; j++) {
				if (searchMatrix[i][j] != null && !searchMatrix[i][j].locked) {
					// searchMatrix[i][j].locked = false;
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
		Points cur = searchMatrix[h][w];
		cur.locked = true;
		recursion(cur, result);

		tmp = result.stream().collect(Collectors.toList());
		// System.out.println("counted points: " + result.size());
		queue.offer(controller.getLocaleText("counted_points") + ": " + result.size());
		if (result.size() < limitConnectedPoints) {
			return recursionInit(result);
		}
		/*
		 * Points[][] copy = new Points[searchMatrix.length][searchMatrix[0].length];
		 * for (Points p : tmp) {
		 * copy[p.myPosition.y][p.myPosition.x] = p;
		 * }
		 * copyPoints = copy;
		 */
		// System.out.println("rec finished");
		queue.offer(controller.getLocaleText("rec_fin"));
		result.clear();
		return true;
	}

	private void recursion(Points current, Set<Points> result) {
		// collectedPoints++;
		if (/* collectedPoints > totalConnectedPointsLimit || */result.size() > totalConnectedPointsLimit) {
			return;
		}
		result.add(current);
		List<Points> container = getRelatives(current.myPosition.y, current.myPosition.x);
		for (Points p : container) {
			// collectedPoints++;
			result.add(p);
			recursion(p, result);
		}
	}

	private List<Points> getRelatives(int curHeight, int curWidth) {
		List<Points> result = new ArrayList<>();
		int temp = 1;
		int startHeight = curHeight - temp;
		int endHeight = curHeight + temp;
		int startWidth = curWidth - temp;
		int endWidth = curWidth + temp;
		do {
			for (; startWidth < endWidth; startWidth++) {
				if (check(startHeight, startWidth)) {
					Points cur = searchMatrix[startHeight][startWidth];
					cur.locked = true;
					result.add(cur);
				}
			}
			for (; startHeight < endHeight; startHeight++) {
				if (check(startHeight, startWidth)) {
					Points cur = searchMatrix[startHeight][startWidth];
					cur.locked = true;
					result.add(cur);
				}
			}
			for (; startWidth > curWidth - temp; startWidth--) {
				if (check(startHeight, startWidth)) {
					Points cur = searchMatrix[startHeight][startWidth];
					cur.locked = true;
					result.add(cur);
				}
			}
			for (; startHeight > curHeight - temp; startHeight--) {
				if (check(startHeight, startWidth)) {
					Points cur = searchMatrix[startHeight][startWidth];
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

			Points firstPointInRow = tmp.get(heightIndex);
			Edge firstAntInRow = new Edge(heightIndex, 0, 0f);
			// firstAntInRow.visited = true;
			matrix[heightIndex] = new Edge[size];
			matrix[heightIndex][0] = firstAntInRow;

			int widthIndex = heightIndex + 1;

			for (int w = 1; w < matrix[heightIndex].length; w++) {
				Points nextPointsInRow = tmp.get(widthIndex);
				float distanceBetweenPoints = initalPathDivider
						/ (float) firstPointInRow.myPosition.distance(nextPointsInRow.myPosition);
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

	private void clearIsLockedFlagInAllPoints() {
		for (int i = 0; i < pointsList.size(); i++) {
			for (Points p : pointsList.get(i)) {
				if (p != null && p.locked) {
					p.locked = false;
				}
			}
		}
	}
}
