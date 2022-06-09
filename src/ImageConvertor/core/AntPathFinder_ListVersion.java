package ImageConvertor.core;

/*
 * import java.util.ArrayList;
 * import java.util.Collection;
 * import java.util.Collections;
 * import java.util.HashMap;
 * import java.util.List;
 * import java.util.Map;
 * import java.util.concurrent.Callable;
 * import java.util.concurrent.ThreadLocalRandom;
 * 
 * /*public class AntPathFinder_ListVersion implements Callable<List<Points>> {
 * List<Points> pointsList;
 * int iterations;
 * float veightRate;
 * float rangeRate;
 * int maxRange;
 * int pointsCount;
 * Controller c;
 * 
 * public AntPathFinder_ListVersion(List<Points> pointsList, int iterations, float veightRate, float
 * rangeRate,
 * int maxRange, Controller c) {
 * this.pointsList = pointsList;
 * this.iterations = iterations;
 * this.veightRate = veightRate;
 * this.rangeRate = rangeRate;
 * this.maxRange = maxRange;
 * this.c = c;
 * this.pointsCount = pointsList.size() - 1;
 * }
 * 
 * // Сам алгоритм, входная точка
 * 
 * @Override
 * public List<Points> call() throws Exception {
 * List<Points> bestResult = null;
 * float lastPathLength = Float.MAX_VALUE;
 * Map<Float, List<Points>> result = new HashMap<>();
 * while (iterations > 0) {
 * System.out.println("currentIteration: " + iterations);
 * for (Points entry : pointsList) {
 * clearMatrix();
 * findPathFromPoint(entry, result);
 * }
 * 
 * iterations--;
 * float min = result.keySet().stream().map(e -> Float.valueOf(e)).min((o1, o2) -> Float.compare(o1,
 * o2))
 * .get();
 * if (min < lastPathLength) {
 * bestResult = result.get(min);
 * lastPathLength = min;
 * }
 * weightVaporization();
 * weightAddition(result);
 * result.clear();
 * System.out.println("\tcurrent best length: " + lastPathLength + ", current rount length: " +
 * min);
 * }
 * return bestResult;
 * 
 * }
 * 
 * /*
 * for(Points p:antPointsList) {
 * p.currentVeight = 0.4d * p.currentVeight;
 * }for(
 * List<Points> list:container)
 * {
 * double pathLength = 0;
 * Points p1 = list.get(0);
 * Points p2;
 * for (int i = 1; i < list.size(); i++) {
 * p2 = list.get(i);
 * pathLength += Math.sqrt((p1.myWidth - p2.myWidth) * (p1.myWidth - p2.myWidth)
 * + (p1.myHeight - p2.myHeight) * (p1.myHeight - p2.myHeight));
 * p1 = p2;
 * }
 * double addition = 2d / pathLength;
 * // System.out.println(addition + ":" + pathLength);
 * for (Points p : list) {
 * p.currentVeight += addition;
 * }
 * 
 * }container.clear();
 * 
 * }
 * // System.out.println(kek);
 * return kek;
 */

// Очистка матрицы
/*
 * private void clearMatrix() {
 * for (Points pp : pointsList) {
 * if (pp != null)
 * pp.visited = false;
 * }
 * }
 * 
 * private void findPathFromPoint(Points entry, Map<Float, List<Points>> resultMap) {
 * List<Points> resultList = new ArrayList<>();
 * entry.visited = true;
 * float currentPathLength = 0f;
 * float sumOfAllWishes;
 * while (resultList.size() < pointsCount - 5) {
 * 
 * resultList.add(entry);
 * calculateRelativesRange(entry);
 * sumOfAllWishes = calculateSumOfAllWishes();
 * calculateRelativesWishes(sumOfAllWishes);
 * 
 * entry = getNearestValue();
 * entry.visited = true;
 * currentPathLength += entry.currentRangeToThisPoints;
 * }
 * resultMap.put(currentPathLength, resultList);
 * }
 */

// Расчёт расстояний от переданной точки до всех остальных
/* private void calculateRelativesRange(Points cur/* , List<Points> relatives *//* ) { */
/*
 * int widthEntry = cur.myPosition.x;
 * int heightEntry = cur.myPosition.y;
 * for (Points p : pointsList) {
 * if (p.visited)
 * continue;
 * // Сокращённая формула
 * 
 * p.currentRangeToThisPoints = 10f / (Math.abs(widthEntry - p.myPosition.x) * Math.abs(widthEntry -
 * p.myPosition.x)
 * + Math.abs(heightEntry - p.myPosition.y) * Math.abs((heightEntry - p.myPosition.y)));
 * 
 * // Полная формула
 * /*
 * p.currentRangeToThisPoints = 10f / (float) Math.sqrt((widthEntry - p.myWidth) * (widthEntry -
 * p.myWidth)
 * +(heightEntry - p.myHeight) * (heightEntry - p.myHeight));
 */

/*
 * p.currentRangeToThisPoints = 10f
 * / (float) Math.hypot(Math.abs(heightEntry - p.myHeight), Math.abs(widthEntry - p.myWidth));
 */
/*
 * }
 * }
 * 
 * private float calculateSumOfAllWishes() {
 * float ans = 0f;
 * for (Points curPoints : pointsList) {
 * if (curPoints.visited)
 * continue;
 * ans += Math.pow(curPoints.currentRangeToThisPoints, rangeRate)
 * + Math.pow(curPoints.currentVeight, veightRate);
 * }
 * return ans;
 * }
 * 
 * private void calculateRelativesWishes(float sumOfAllWishes) {
 * float allWishes = 0f;
 * float currentWish;
 * for (Points curPoints : pointsList) {
 * if (curPoints.visited)
 * continue;
 * 
 * currentWish = (float) (Math.pow(curPoints.currentRangeToThisPoints, rangeRate)
 * + Math.pow(curPoints.currentVeight, veightRate));
 * curPoints.currentWishToGoToThisPoint = currentWish;
 * allWishes += currentWish;
 * 
 * }
 * for (Points curPoints : pointsList) {
 * if (curPoints.visited)
 * continue;
 * curPoints.currentChanceToGoToThisPoint = curPoints.currentWishToGoToThisPoint / allWishes;
 * }
 * }
 * 
 * // Вычисление ближайшего числа в массиве к переданному.
 * private Points getNearestValue() {
 * float roulete = ThreadLocalRandom.current().nextFloat();
 * List<Points> floatContainer = new ArrayList<>();
 * for (Points curPoints : pointsList) {
 * if (curPoints.visited)
 * continue;
 * floatContainer.add(curPoints);
 * }
 * Collections.sort(floatContainer,
 * (o1, o2) -> Double.compare(o1.currentChanceToGoToThisPoint, o2.currentChanceToGoToThisPoint));
 * if (0 < roulete && roulete < floatContainer.get(0).currentChanceToGoToThisPoint) {
 * return floatContainer.get(0);
 * }
 * if (roulete > floatContainer.get(floatContainer.size() - 1).currentChanceToGoToThisPoint) {
 * return floatContainer.get(floatContainer.size() - 1);
 * }
 * Points ans = null;
 * Points last = new Points((short) -1, (short) -1);
 * last.currentChanceToGoToThisPoint = Float.MAX_VALUE;
 * floatContainer.add(last);
 * for (int i = 1; i < floatContainer.size(); i++) {
 * Points p1 = floatContainer.get(i - 1);
 * Points p2 = floatContainer.get(i);
 * if (p1.currentChanceToGoToThisPoint <= roulete && roulete < p2.currentChanceToGoToThisPoint) {
 * ans = p1;
 * }
 * }
 * return ans;
 */
/*
 * double val = Double.MAX_VALUE * 2;
 * for (double d : arr) {
 * if (val > Math.abs(n - d)) {
 * val = Math.abs(n - d);
 * ans = d;
 * }
 * }
 * return ans;
 */
/*
 * }
 * 
 * private void weightVaporization() {
 * for (Points curPoints : pointsList) {
 * curPoints.currentVeight *= 0.52;
 * }
 * }
 * 
 * private void weightAddition(Map<Float, List<Points>> result) {
 * result.entrySet().stream().forEach(entry -> {
 * float addition = 4f / entry.getKey();
 * List<Points> curList = entry.getValue();
 * for (Points inner : curList) {
 * inner.currentVeight += addition;
 * }
 * });
 * }
 */

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
 * return width > -1 && height > -1 && height < searchMatrix.length && width <
 * searchMatrix[0].length
 * && searchMatrix[height][width] != null && !searchMatrix[height][width].visited
 * && searchMatrix[height][width].direction != Direction.STUB;
 * }
 */

//}
