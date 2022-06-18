package ImageConvertor.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import ImageConvertor.data.Edge;
import ImageConvertor.data.Points;
import ImageConvertor.data.ValuesContainer;

public class PathWorker extends Thread implements Callable<Object[]> {

	Edge[][] matrix;
	List<Points> pointsContainer;
	AtomicInteger counter;
	Map<Float, List<Edge>> paths;
	Controller c;
	StringBuilder sb = new StringBuilder();
	Object myMutex = new Object();

	float rangeRate;
	float weightRate;
	int startHeight;
	final int threadIndex;
	public boolean isWorkDone = false;

	public PathWorker(Edge[][] matrix, List<Points> pointsContainer, Controller c, float rangeRate, float weightRate,
			int startHeight, int threadIndex, AtomicInteger counter, Map<Float, List<Edge>> paths) {
		this.matrix = matrix;
		this.c = c;
		this.rangeRate = rangeRate;
		this.weightRate = weightRate;
		this.pointsContainer = pointsContainer;
		this.startHeight = startHeight;
		this.threadIndex = threadIndex;
		this.counter = counter;
		this.paths = paths;
		this.setDaemon(true);
		this.setName("manual-thread-worker-" + threadIndex);
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			synchronized (myMutex) {
				try {
					myMutex.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
			if (isInterrupted()) {
				return;
			}
			int maxPathLength = matrix[0].length - 1;
			int currentCountOfPoints = 0;
			clearMatrix();
			List<Edge> currentEdgesList = new ArrayList<>();
			List<ValuesContainer> wishes;
			Edge nextEdge;
			float length = 0f;
			int curHeight = startHeight;

			while (currentCountOfPoints < maxPathLength) {
				if (isInterrupted()) {
					return;
				}
				nextEdge = matrix[curHeight][0];
				nextEdge.visited[threadIndex] = 1;
				currentEdgesList.add(nextEdge);

				wishes = calculateSumOfAllWishesAndRelativeWishes(curHeight);

				nextEdge = getNextEdgeToPath(wishes);

				if (curHeight == nextEdge.heightIndex) {
					curHeight = nextEdge.widthIndex;
				} else {
					curHeight = nextEdge.heightIndex;
				}
				//length += nextEdge.distanceBetweenPoints;
				currentCountOfPoints++;
			}
			for(int i=1;i<currentEdgesList.size();i++) {
				Point p1=pointsContainer.get(currentEdgesList.get(i-1).heightIndex).startPoint;
				Point p2=pointsContainer.get(currentEdgesList.get(i).heightIndex).startPoint;
				length+=p1.distance(p2);
			}
			paths.put(length, currentEdgesList);
			counter.decrementAndGet();
			isWorkDone = true;
		}
	}

	@Override
	public Object[] call() throws Exception {
		throw new UnsupportedOperationException("NOT DEVELOPED");
		/*int maxPathLength = matrix[0].length - 1;
		int iter = 0;
		List<Points> ans = null;
		float minLength = Float.MAX_VALUE;
		// while (iter < 1000) {
		// for (int i = 0; i < matrix.length; i += 1) {

		int currentCountOfPoints = 0;
		clearMatrix();
		List<Edge> currentEdgesList = new ArrayList<>();
		List<ValuesContainer> wishes;

		Edge nextEdge;

		float length = 0f;
		// int curHeight = i;
		int curHeight = startHeight;
		clearMatrix();

		while (currentCountOfPoints < maxPathLength) {

			nextEdge = matrix[curHeight][0];
			nextEdge.visited[threadIndex] = 1;
			currentEdgesList.add(nextEdge);

			wishes = calculateSumOfAllWishesAndRelativeWishes(curHeight);

			nextEdge = getNextEdgeToPath(wishes);

			if (curHeight == nextEdge.heightIndex) {
				curHeight = nextEdge.widthIndex;
			} else {
				curHeight = nextEdge.heightIndex;
			}
			length += nextEdge.distanceBetweenPoints;
			currentCountOfPoints++;
		}
		// edgesContainer.put(length, currentEdgesList);

		// }
		// updateMaxrixWeight(edgesContainer);
		// float min = edgesContainer.keySet().stream().min((o1, o2) -> Float.compare(o1,
		// o2)).get().floatValue();
		// if (min <= minLength) {
		// ans = edgesContainer.get(min).stream().map(e -> pointsContainer.get(e.heightIndex))
		// .collect(Collectors.toList());
		// minLength = min;
		// }
		// System.out.println(
		// "current iteration: " + iter + ", current min length: " + min + ", the minnest length: " +
		// minLength);
		// iter++;
		// }
		/*
		 * for (int i = 0; i < matrix.length; i++) {
		 * for (int j = 0; j < matrix[i].length; j++) {
		 * System.out.print(matrix[i][j].rangeFromFirstToThis+"\s");
		 * }
		 * System.out.println();
		 * }
		 *
		return new Object[] { length, currentEdgesList };*/
	}

	// Очистка матрицы
	private void clearMatrix() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 1; j < matrix[i].length; j++) {
				matrix[i][j].visited[threadIndex] = 0;
			}
		}
	}

	private List<ValuesContainer> calculateSumOfAllWishesAndRelativeWishes(int height) {

		List<ValuesContainer> curWishList = new ArrayList<ValuesContainer>();

		float ans = 0f;

		Edge cur;
		ValuesContainer temp;

		int curWidth = height;

		for (int w = 0; w < height; w++, curWidth--) {
			cur = matrix[w][curWidth];
			if (cur.visited[threadIndex] == 1)
				continue;
			temp = new ValuesContainer();
			temp.edge = cur;
			temp.currentWishToGoHere = (float) (Math.pow(cur.distanceBetweenPoints, rangeRate)
					+ Math.pow(cur.weight, weightRate));
			ans += temp.currentWishToGoHere;
			cur.visited[threadIndex] = 1;
			curWishList.add(temp);
		}

		for (int width = 1; width < matrix[height].length; width++) {
			cur = matrix[height][width];
			if (cur.visited[threadIndex] == 1)
				continue;
			temp = new ValuesContainer();
			temp.edge = cur;
			temp.currentWishToGoHere = (float) (Math.pow(cur.distanceBetweenPoints, rangeRate)
					+ Math.pow(cur.weight, weightRate));
			ans += temp.currentWishToGoHere;
			cur.visited[threadIndex] = 1;
			curWishList.add(temp);
		}
		for (ValuesContainer a : curWishList) {
			a.currentChanceToGoHere = a.currentWishToGoHere / ans;
		}
		return curWishList;
	}

	private Edge getNextEdgeToPath(List<ValuesContainer> chancesContainer) {
		float currentChance = ThreadLocalRandom.current().nextFloat();
		float sum = 0f;
		ValuesContainer curValue;
		for (int i = 0; i < chancesContainer.size(); i++) {
			curValue = chancesContainer.get(i);
			sum += curValue.currentChanceToGoHere;
			if (sum >= currentChance) {
				return curValue.edge;
			}
		}
		if (sum < currentChance) {
			return chancesContainer.get(chancesContainer.size() - 1).edge;
		}
		return null;
	}

	private void updateMaxrixWeight(Map<Float, List<Edge>> container) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 1; j < matrix[i].length; j++) {
				matrix[i][j].weight *= 0.62f;
			}
		}
		container.entrySet().stream().forEach(e -> {
			float length = 10f / e.getKey();
			for (Edge a : e.getValue()) {
				a.weight += length;
			}
		});
	}
}

/*
 * private List<Points> getRelatives(int curHeight, int curWidth) {
 * List<Points> result = new ArrayList<>();
 * int temp = 1;
 * int startHeight = curHeight - temp;
 * int endHeight = curHeight + temp;
 * int startWidth = curWidth - temp;
 * int endWidth = curWidth + temp;
 * do {
 * for (; startWidth < endWidth; startWidth++) {
 * if (check(startHeight, startWidth)) {
 * Points cur = searchMatrix[startHeight][startWidth];
 * result.add(cur);
 * }
 * }
 * for (; startHeight < endHeight; startHeight++) {
 * if (check(startHeight, startWidth)) {
 * Points cur = searchMatrix[startHeight][startWidth];
 * result.add(cur);
 * }
 * }
 * for (; startWidth > curWidth - temp; startWidth--) {
 * if (check(startHeight, startWidth)) {
 * Points cur = searchMatrix[startHeight][startWidth];
 * result.add(cur);
 * }
 * }
 * for (; startHeight > curHeight - temp; startHeight--) {
 * if (check(startHeight, startWidth)) {
 * Points cur = searchMatrix[startHeight][startWidth];
 * result.add(cur);
 * }
 * }
 * temp++;
 * startWidth = curWidth - temp;
 * endWidth = curWidth + temp;
 * startHeight = curHeight + temp;
 * endHeight = curHeight - temp;
 * } while (temp <= maxRange);
 * return result;
 * }
 * 
 * private boolean check(int height, int width) {
 * return width > -1 && height > -1 && height < matrix.length && width < matrix[0].length
 * && matrix[height][width] != null && !matrix[height][width].visited;
 * }
 *
 * }
 **/
