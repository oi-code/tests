package ImageConvertor.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import ImageConvertor.data.Chunk;

public class AntPathWorker implements Runnable {

	@Override
	public void run() {
	}

	/*
	 * для работы необходима матрица смежности
	 * 
	 * первый метод описывает желание перехода из одной точки в другую
	 * выглядит это так
	 * 
	 * первый метод:
	 * вероятность перехода из вершины FROM в вершину TO будет равняться:
	 * 
	 * количество феромона, лежащего на грани между FROM в TO умноженное на CONST делёная
	 * на расстояние между этими ОТКУДА в КУДА
	 * 
	 * feramon*(CONST/from.distance(to))
	 * 
	 * делёному на сумму всех желаний перейти из FROM во все доступные TO (не посещённые)
	 * сумма вероятностей должна равняться 1
	 * 
	 * итоговая вероятность получается из
	 * генерируется случайное число
	 * 
	 * например 0.4
	 * 
	 * из метода выше получаются вероятности перехода в доступные не посещённые вершины
	 * например 3 вершины с вероятностями 0.1, 0.3 и 0.5
	 * 
	 * переход будет осуществлён в вершину 0.3
	 * 
	 * второй метод:
	 * описывает распределение феромона на гранях
	 * добавление феромона на грань между FROM в TO будет выполняться по следующему алгоритму
	 * константа CONST делёная на длинну маршрута, пройденую муравьём при условии, что эта грань FROM TO
	 * попала в маршрут муравья
	 * CONST/path.length
	 */

	private Chunk whereToGo(Chunk from) {

		final float INVERSE_DISTANCE_DIVIDER = 1f;
		final float CONST_B = 1f;
		final float CONST_C = 1f;

		final float CHANCE_TO_ALL = chanceToGoToAll(INVERSE_DISTANCE_DIVIDER, from);

		List<Object[]> chances = new LinkedList<>();
		from.getFreeAroundChunks().stream().forEach(to -> {
			Object[] temp = chanceToGoTo(INVERSE_DISTANCE_DIVIDER, from, to);
			temp[1] = (float) temp[1] / CHANCE_TO_ALL;
			chances.add(temp);
		});
		chances.sort((o1, o2) -> Float.compare((float) o1[1], (float) o2[1]));
		ThreadLocalRandom tlr = ThreadLocalRandom.current();

		float random = tlr.nextFloat();

		AntEdge resultEdge = null;
		float prev = 0;
		for (Object[] f : chances) {
			float check = (float) f[1];
			if (check >= prev && (float) check < random) {
				resultEdge = (AntEdge) f[0];
				break;
			}
			prev = check;
		}
		if (chances.size() > 0 && resultEdge == null) {
			resultEdge = (AntEdge) chances.get(chances.size() - 1)[0];
		}
		if (resultEdge != null) {
			resultEdge.visited = true;
			Chunk result = resultEdge.vertexOne == from ? resultEdge.vertexTwo : resultEdge.vertexOne;
			return result;
		}
		return null;
	}

	private float chanceToGoToAll(float INVERSE_DISTANCE_DIVIDER, Chunk from) {
		AtomicReference<Float> result = new AtomicReference<>(0f);
		from.edges.stream().filter(e -> !e.visited).forEach(e -> result.set(
				result.get() + e.getTransitionCost() * (INVERSE_DISTANCE_DIVIDER / e.distanceBetweenVertexes())));
		return result.get();
	}

	/*
	 * if @return value is null, then we can't continue path finding
	 */
	private Object[] chanceToGoTo(float INVERSE_DISTANCE_DIVIDER, Chunk from, Chunk to) {
		AntEdge edge = null;
		/*
		 * find the same edge in two vertexes
		 */
		exit: for (AntEdge fromEdge : from.edges) {
			for (AntEdge toEdge : to.edges) {
				// if (fromEdge.equals(toEdge)) {
				if (fromEdge == toEdge) {
					edge = fromEdge;
					break exit;
				}
			}
		}
		if (edge == null) {
			return null;
		}
		float chance = edge.getTransitionCost() * (INVERSE_DISTANCE_DIVIDER / edge.distanceBetweenVertexes());
		Object[] result = new Object[2];
		result[0] = edge;
		result[1] = chance;
		return result;
	}

}
