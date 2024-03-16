package ImageConvertor.core;

import java.util.LinkedList;
import java.util.List;
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

	private void chanceToGo() {
		short a = 1;
		Chunk from = new Chunk(a, a);
		List<Chunk> avalivableChunks = from.getFreeAroundChunks();
		float transitionCost = 0.5f;

		final float CONST_A = 1f;
		final float CONST_B = 1f;
		final float CONST_C = 1f;

		final float CHANCE_TO_ALL = chanceToGoToAll(avalivableChunks, transitionCost, CONST_A, from);

		List<Float> chances = new LinkedList<>();
		avalivableChunks.stream().forEach(e -> {
			chances.add(chanceToGoTo(transitionCost, CONST_A, from, e) / CHANCE_TO_ALL);
		});

	}

	private float chanceToGoToAll(List<Chunk> avalivableChunks, float transitionCost, float CONST_A, Chunk from) {
		AtomicReference<Float> result = new AtomicReference<>(0f);
		avalivableChunks.stream().forEach(e -> {
			result.set(
					result.get() + (transitionCost * (float) (CONST_A / from.chunkPosition.distance(e.chunkPosition))));
		});
		return result.get();
	}

	private float chanceToGoTo(float transitionCost, float CONST_A, Chunk from, Chunk to) {
		AtomicReference<Float> result = new AtomicReference<>(0f);
		result.set(result.get() + (float) (transitionCost * (CONST_A / from.chunkPosition.distance(to.chunkPosition))));
		return result.get();
	}

}
