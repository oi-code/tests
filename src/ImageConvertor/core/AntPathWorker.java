package ImageConvertor.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

	private Optional<Chunk> whereToGo(Chunk from) {
		List<Chunk> freeAroundChunks = from.getFreeAroundChunks();
		if (freeAroundChunks.size() < 1) {
			if (printTestData) {
				System.out.println("CANT FIND NEXT CHUNK");
			}
			return Optional.empty();
			// return null;
		}
		float INVERSE_DISTANCE_DIVIDER = 1f;
		byte POW_COST = 1;
		byte POW_INVERSE_DISTANCE = 1;
		List<AntEdgeChoserContainer> antChanceContainer = new LinkedList<>();
		freeAroundChunks.stream().forEach(e -> {
			antChanceContainer.add(chanceToGoTo(INVERSE_DISTANCE_DIVIDER, POW_COST, POW_INVERSE_DISTANCE, from, e));
		});
		float chanceToAll = antChanceContainer.stream().map(e -> e.chanceToGoHere).reduce(0f, Float::sum);

		antChanceContainer.stream().forEach(e -> e.chanceToGoHere = e.chanceToGoHere / chanceToAll);

		antChanceContainer.sort((o1, o2) -> Float.compare(o1.chanceToGoHere, o2.chanceToGoHere));

		ThreadLocalRandom tlr = ThreadLocalRandom.current();
		float random = tlr.nextFloat();
		AntEdge resultEdge = closest(antChanceContainer, random).get().edge;

		if (resultEdge != null) {
			resultEdge.visited[0] = true;

			Chunk result = resultEdge.vertexOne == from ? resultEdge.vertexTwo : resultEdge.vertexOne;

			if (printTestData) {
				System.out.println("result: " + result);
				System.out.println("result edges size: " + result.edges.size());
				System.out.println("result avalChunks size: " + result.avalivableChunks.size());
				System.out.println("random: " + random);
				System.out.println("chances size: " + antChanceContainer.size());
				System.out.println("avalCh: " + from.avalivableChunks.size());
				System.out.println("edgSz: " + from.edges.size());
				AtomicReference<Float> f = new AtomicReference<>(0f);
				antChanceContainer.stream().forEach(e -> {
					float chance = e.chanceToGoHere;
					AntEdge edg = e.edge;
					System.out.print("chanceToGo: " + chance);
					System.out.println(" curEdgfe: " + edg);
					if (chance < 1f)
						f.set(f.get() + chance);
				});
				System.out.println("sum of all chances: " + f.get() + ", sum of chances: " + chanceToAll);
			}
			// return result;
			return Optional.of(result);
		} else if (antChanceContainer.size() > 0) {
			AntEdge ae = antChanceContainer.get(antChanceContainer.size() - 1).edge;
			Chunk result = ae.vertexOne == from ? ae.vertexTwo : ae.vertexOne;
			// return result;
			return Optional.of(result);
		} else {
			return null;
		}

	}

	/*
	 * private Chunk whereToGo(Chunk from) {
	 * 
	 * List<Chunk> freeAroundChunks = from.getFreeAroundChunks();
	 * if (freeAroundChunks.size() < 1) {
	 * if (printTestData) {
	 * System.out.println("CANT FIND NEXT CHUNK");
	 * }
	 * return null;
	 * }
	 * 
	 * float INVERSE_DISTANCE_DIVIDER = 1f;
	 * byte POW_COST = 1;
	 * byte POW_INVERSE_DISTANCE = 1;
	 * 
	 * final float CHANCE_TO_ALL = chanceToGoToAll(INVERSE_DISTANCE_DIVIDER, POW_COST,
	 * POW_INVERSE_DISTANCE, from);
	 * 
	 * List<AntEdgeChoserContainer> chances = new LinkedList<>();
	 * freeAroundChunks.stream().forEach(to -> {
	 * AntEdgeChoserContainer temp = chanceToGoTo(INVERSE_DISTANCE_DIVIDER, POW_COST,
	 * POW_INVERSE_DISTANCE, from,
	 * to);
	 * if (temp == null) {
	 * return;
	 * }
	 * temp.chanceToGoHere = temp.chanceToGoHere / CHANCE_TO_ALL;
	 * chances.add(temp);
	 * });
	 * chances.sort((o1, o2) -> Float.compare(o1.chanceToGoHere, o2.chanceToGoHere));
	 * 
	 * ThreadLocalRandom tlr = ThreadLocalRandom.current();
	 * 
	 * float random = tlr.nextFloat();
	 * 
	 * List<AntEdgeChoserContainer> chosed = new LinkedList<>();
	 * 
	 * AntEdge resultEdge = null;
	 * 
	 * resultEdge = closest(chances, random).edge;
	 * 
	 * if (resultEdge != null) {
	 * resultEdge.visited[0] = true;
	 * 
	 * Chunk result = resultEdge.vertexOne == from ? resultEdge.vertexTwo : resultEdge.vertexOne;
	 * 
	 * if (printTestData) {
	 * System.out.println("result: " + result);
	 * System.out.println("random: " + random);
	 * System.out.println("chances size: " + chances.size());
	 * System.out.println("avalCh: " + from.avalivableChunks.size());
	 * System.out.println("edgSz: " + from.edges.size());
	 * AtomicReference<Float> f = new AtomicReference<>(0f);
	 * chances.stream().forEach(e -> {
	 * float chance = e.chanceToGoHere;
	 * AntEdge edg = e.edge;
	 * System.out.print("chanceToGo: " + chance);
	 * System.out.println(" curEdgfe: " + edg);
	 * if (chance < 1f)
	 * f.set(f.get() + chance);
	 * });
	 * System.out.println("sum of all chances: " + f.get());
	 * }
	 * return result;
	 * } else if (chosed.size() > 0) {
	 * AntEdge ae = chosed.get(chosed.size() - 1).edge;
	 * return ae.vertexOne == from ? ae.vertexTwo : ae.vertexOne;
	 * } else {
	 * return null;
	 * }
	 * }
	 * 
	 * private float chanceToGoToAll(float INVERSE_DISTANCE_DIVIDER, byte POW_COST, byte
	 * POW_INVERSE_DISTANCE,
	 * Chunk from) {
	 * AtomicReference<Float> result = new AtomicReference<>(0f);
	 * from.edges.stream().filter(e -> !e.visited[0])
	 * .forEach(e -> result.set((float) (result.get() + Math.pow(e.getTransitionCost(), POW_COST)
	 * Math.pow(INVERSE_DISTANCE_DIVIDER / e.distanceBetweenVertexes(), POW_INVERSE_DISTANCE))));
	 * return result.get();
	 * }
	 */

	/*
	 * if @return value is null, then we can't continue path finding
	 */
	private AntEdgeChoserContainer chanceToGoTo(float INVERSE_DISTANCE_DIVIDER, byte POW_COST,
			byte POW_INVERSE_DISTANCE, Chunk from, Chunk to) {
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
			// return null;
		}
		float chance = (float) (Math.pow(edge.getTransitionCost(), POW_COST)
				* Math.pow((INVERSE_DISTANCE_DIVIDER / edge.distanceBetweenVertexes()), POW_INVERSE_DISTANCE));
		AntEdgeChoserContainer result = new AntEdgeChoserContainer();
		result.edge = edge;
		result.chanceToGoHere = chance;
		return result;
	}

	public void TEST() {

		printTestData = true;

		Chunk seed = new Chunk((short) 2, (short) 2);
		seed.visited = true;
		seed.locked = true;

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
		// a2.visited = true;
		// a3.visited = true;
		// a4.visited = true;
		// a5.visited = true;
		// a6.visited = true;
		// a7.visited = true;
		// a8.visited = true;

		a1.avalivableChunks.add(seed);
		a2.avalivableChunks.add(seed);
		a3.avalivableChunks.add(seed);
		a4.avalivableChunks.add(seed);
		a5.avalivableChunks.add(seed);
		a6.avalivableChunks.add(seed);
		a7.avalivableChunks.add(seed);
		a8.avalivableChunks.add(seed);

		AntEdge e1 = new AntEdge(seed, a1);
		AntEdge e2 = new AntEdge(seed, a2);
		AntEdge e3 = new AntEdge(seed, a3);
		AntEdge e4 = new AntEdge(seed, a4);
		AntEdge e5 = new AntEdge(seed, a5);
		AntEdge e6 = new AntEdge(seed, a6);
		AntEdge e7 = new AntEdge(seed, a7);
		AntEdge e8 = new AntEdge(seed, a8);

		a1.edges.add(e1);
		a2.edges.add(e2);
		a3.edges.add(e3);
		a4.edges.add(e4);
		a5.edges.add(e5);
		a6.edges.add(e6);
		a7.edges.add(e7);
		a8.edges.add(e8);

		// e1.visited[0] = true;
		// e2.visited[0] = true;
		// e3.visited[0] = true;
		// e4.visited[0] = true;
		// e5.visited[0] = true;
		// e6.visited[0] = true;
		// e7.visited[0] = true;
		// e8.visited[0] = true;

		seed.avalivableChunks.addAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8));
		seed.edges.addAll(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8));

		this.whereToGo(seed);

	}

	private Optional<AntEdgeChoserContainer> closest(List<AntEdgeChoserContainer> chances, float random) {
		if (chances.size() < 1) {
			// return null;
			return Optional.empty();
		}
		AtomicReference<AntEdgeChoserContainer> result = new AtomicReference<>(null);
		AtomicReference<Float> minChance = new AtomicReference<>(Float.MAX_VALUE);

		chances.stream().forEach(e -> {
			if (e.chanceToGoHere <= random) {
				result.set(e);
				minChance.set(e.chanceToGoHere);
			}
		});
		if (minChance.get() == Float.MAX_VALUE) {
			result.set(chances.get(0));
		}
		// return result.get();
		return Optional.of(result.get());
	}

}
