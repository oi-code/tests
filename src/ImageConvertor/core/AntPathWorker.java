package ImageConvertor.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import ImageConvertor.data.Chunk;

public class AntPathWorker implements Runnable {

	private boolean printTestData;

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
			if (temp == null) {
				return;
			}
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
			if (check >= prev && check < random) {
				resultEdge = (AntEdge) f[0];
				break;
			}
			prev = check;
		}
		if (chances.size() > 0 && resultEdge == null) {
			// resultEdge = (AntEdge) chances.get(chances.size() - 1)[0];
			resultEdge = (AntEdge) chances.stream().max((o1, o2) -> Float.compare((float) o1[1], (float) o2[1]))
					.get()[0];
			if (printTestData)
				System.err.println("NILL " + chances.size() + " " + resultEdge + " " + random);
		}
		if (resultEdge != null) {
			resultEdge.visited[0] = true;
			Chunk result = resultEdge.vertexOne == from ? resultEdge.vertexTwo : resultEdge.vertexOne;
			if (printTestData) {
				System.out.println("result: " + result);
				System.out.println("random: " + random);
				System.out.println("chances size: " + chances.size());
				System.out.println("avalCh: " + from.avalivableChunks.size());
				System.out.println("edgSz: " + from.edges.size());
				AtomicReference<Float> f = new AtomicReference<>(0f);
				chances.stream().forEach(e -> {
					float chance = (float) e[1];
					AntEdge edg = (AntEdge) e[0];
					System.out.print("chanceToGo: " + chance);
					System.out.println(" curEdgfe: " + edg);
					f.set(f.get() + chance);
				});
				System.out.println("sum of all chances: " + f.get());
			}
			return result;
		}
		return null;
	}

	private float chanceToGoToAll(final float INVERSE_DISTANCE_DIVIDER, Chunk from) {
		AtomicReference<Float> result = new AtomicReference<>(0f);
		from.edges.stream().filter(e -> !e.visited[0]).forEach(e -> result
				.set(result.get() + e.getTransitionCost() * (INVERSE_DISTANCE_DIVIDER / e.distanceBetweenVertexes())));
		return result.get();
	}

	/*
	 * if @return value is null, then we can't continue path finding
	 */
	private Object[] chanceToGoTo(final float INVERSE_DISTANCE_DIVIDER, Chunk from, Chunk to) {
		AntEdge edge = null;
		/*
		 * find the same edge in two vertexes
		 */
		for (AntEdge _edge : from.edges) {
			if (_edge.vertexOne == from && _edge.vertexTwo == to) {
				edge = _edge;
				break;
			} else if (_edge.vertexOne == to && _edge.vertexTwo == from) {
				edge = _edge;
				break;
			}
		}
		if (edge == null || edge.visited[0]) {
			return null;
		}
		float chance = edge.getTransitionCost() * (INVERSE_DISTANCE_DIVIDER / edge.distanceBetweenVertexes());
		Object[] result = new Object[2];
		result[0] = edge;
		result[1] = chance;
		return result;
	}

	public void TEST() {

		printTestData = true;

		Chunk seed = new Chunk((short) 2, (short) 2);

		Chunk a1 = new Chunk((short) 1, (short) 1);
		Chunk a2 = new Chunk((short) 1, (short) 2);
		Chunk a3 = new Chunk((short) 1, (short) 3);

		Chunk a4 = new Chunk((short) 2, (short) 3);
		Chunk a5 = new Chunk((short) 3, (short) 3);

		Chunk a6 = new Chunk((short) 3, (short) 2);
		Chunk a7 = new Chunk((short) 3, (short) 1);

		Chunk a8 = new Chunk((short) 2, (short) 1);

		seed.index = 0;
		a1.index = 1;
		a2.index = 2;
		a3.index = 3;
		a4.index = 4;
		a5.index = 5;
		a6.index = 6;
		a7.index = 7;
		a8.index = 8;

		// a1.visited = true;
		a2.visited = true;
		a3.visited = true;
		a4.visited = true;
		a5.visited = true;
		// a6.visited=true;
		a7.visited = true;
		a8.visited = true;

		AntEdge e1 = new AntEdge(seed, a1);
		AntEdge e2 = new AntEdge(seed, a2);
		AntEdge e3 = new AntEdge(seed, a3);
		AntEdge e4 = new AntEdge(seed, a4);
		AntEdge e5 = new AntEdge(seed, a5);
		AntEdge e6 = new AntEdge(seed, a6);
		AntEdge e7 = new AntEdge(seed, a7);
		AntEdge e8 = new AntEdge(seed, a8);

		// e1.visited[0] = true;
		e2.visited[0] = true;
		e3.visited[0] = true;
		e4.visited[0] = true;
		e5.visited[0] = true;
		// e6.visited[0]=true;
		e7.visited[0] = true;
		e8.visited[0] = true;

		seed.avalivableChunks.addAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8));
		seed.edges.addAll(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8));

		this.whereToGo(seed);

	}

}
