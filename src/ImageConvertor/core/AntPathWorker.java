package ImageConvertor.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ImageConvertor.data.Chunk;
import ImageConvertor.views.desktop.View;

public class AntPathWorker implements Runnable {

	public class DataHolder {
		public int index;
		public float distance;
		public float transitionCost = 0.5f;
		public int h;
		public int w;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(h, w);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DataHolder other = (DataHolder) obj;
			if (this.w == other.w && this.h == other.h) {
				return true;
			} else if (this.w == other.h && this.h == other.w) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return h + "." + w + " ";
		}

		private AntPathWorker getEnclosingInstance() {
			return AntPathWorker.this;
		}
	}

	private boolean printTestData;
	private List<Chunk> result;
	private List<Chunk> input;
	private DataHolder[][] matrix;

	public AntPathWorker() {
	}

	public AntPathWorker(List<Chunk> input) {
		result = new ArrayList<>();
		this.input = input;
		matrix = new DataHolder[input.size()][];
		clearVisitedFlags();
	}

	@Override
	public void run() {

		List<List<Chunk>> cont = new LinkedList<>();

		for (Chunk startSeed : input) {
			List<Chunk> currentSequence = new LinkedList<>();
			startSeed.locked = true;
			currentSequence.add(startSeed);
			Chunk current = startSeed;
			while (currentSequence.size() < input.size()) {
				Optional<Chunk> next = whereToGo(current);
				if (next.isPresent()) {
					Chunk nextChunk = next.get();
					nextChunk.locked = true;
					currentSequence.add(nextChunk);
					current = nextChunk;
				} else {
					break;
				}
			}
			clearVisitedFlags();
			cont.add(currentSequence);
		}
		List<Chunk> rs = cont.stream().max((o1, o2) -> Integer.compare(o1.size(), o2.size())).get();
		// Set<Chunk> hash = new HashSet<>(rs);
		// System.out.println(rs.size() + " " + hash.size());
		result.addAll(rs);
	}

	public List<Chunk> getResult() {
		return result;
	}

	private void clearVisitedFlags() {
		input.stream().forEach(e -> e.locked = false);
	}

	/*
	 * heuristic method. it chose the next chunk to go
	 */

	private Optional<Chunk> whereToGo(Chunk from) {
		/*
		 * if free chunks is not exist, we can stop
		 */
		List<Chunk> freeAroundChunks = from.getFreeAroundChunks();
		if (freeAroundChunks.size() < 1) {
			if (printTestData) {
				System.out.println("CANT FIND NEXT CHUNK");
			}
			return Optional.empty();
			// return null;
		}
		float INVERSE_DISTANCE_DIVIDER = 1f;
		byte POW_COST = 3;
		byte POW_INVERSE_DISTANCE = 1;
		/*
		 * edges to go container
		 */
		List<AntEdgeChoserContainer> antChanceContainer = new LinkedList<>();
		/*
		 * add to list every available edge to go
		 */
		freeAroundChunks.stream().forEach(e -> {
			Optional<AntEdgeChoserContainer> chose = chanceToGoTo(INVERSE_DISTANCE_DIVIDER, POW_COST,
					POW_INVERSE_DISTANCE, from, e);
			if (chose.isPresent()) {
				antChanceContainer.add(chose.get());
			}
		});

		/*
		 * compute sum of all wishes to go from container
		 */
		float chanceToAll = antChanceContainer.stream().map(e -> e.chanceToGoHere).reduce(0f, Float::sum);

		/*
		 * here we we equate the sum of all the chances to 1
		 */

		antChanceContainer.stream().forEach(e -> e.chanceToGoHere = e.chanceToGoHere / chanceToAll);

		/*
		 * sorting to simplify chose the next edge
		 */
		antChanceContainer.sort((o1, o2) -> Float.compare(o1.chanceToGoHere, o2.chanceToGoHere));

		ThreadLocalRandom tlr = ThreadLocalRandom.current();
		float random = tlr.nextFloat();

		/*
		 * chose the next edge
		 */
		Optional<AntEdgeChoserContainer> antEdgeContainer = closest(antChanceContainer, random);

		if (antEdgeContainer.isPresent()) {
			AntEdge resultEdge = antEdgeContainer.get().antEdge;
			/*
			 * chose the next chunk from edge
			 */
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
					AntEdge edg = e.antEdge;
					System.out.print("chanceToGo: " + chance);
					System.out.println(" curEdgfe: " + edg);
					if (chance < 1f)
						f.set(f.get() + chance);
				});
				System.out.println("sum of all chances: " + f.get() + ", sum of chances: " + chanceToAll);
			}
			/*
			 * mark chunk result as visited
			 */
			// result.visited = true;
			// return result;
			// System.out.println("SELECTED " + result);
			return Optional.of(result);
		} else {
			return Optional.empty();
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
	private Optional<AntEdgeChoserContainer> chanceToGoTo(float INVERSE_DISTANCE_DIVIDER, byte POW_COST,
			byte POW_INVERSE_DISTANCE, Chunk from, Chunk to) {
		AntEdge edge = null;
		/*
		 * find the same edge in two vertexes
		 */
		for (AntEdge _edge : from.edges) {
			if (_edge.vertexOne /* == */.equals(from) && _edge.vertexTwo.equals(/* == */ to)) {
				edge = _edge;
				break;
			} else if (_edge.vertexOne.equals(/* == */ to) && _edge.vertexTwo.equals/* == */(from)) {
				edge = _edge;
				break;
			}
		}
		if (edge == null) {
			return Optional.empty();
		}
		float chance = (float) (Math.pow(edge.getTransitionCost(), POW_COST)
				* Math.pow((INVERSE_DISTANCE_DIVIDER / edge.distanceBetweenVertexes()), POW_INVERSE_DISTANCE));
		AntEdgeChoserContainer result = new AntEdgeChoserContainer();
		result.antEdge = edge;
		result.chanceToGoHere = chance;
		return Optional.of(result);
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

	/*
	 * Selecting the closest value in range.
	 * For example, we have list of chances with values
	 * 0.1, 0.2, 0.4, 0.5 and random number 0.3.
	 * We iterate through list and check that current number in list
	 * less-or-equals random. If that, we save current iteration result to outer variable
	 * and go next.
	 * If current value is greater than random, we break loop.
	 * If outer chance value is not changed (outer variable was set to Float.MAX_VALUE)
	 * we just pick first value in chances list, because list was sorted
	 * If list size less equals 0, we can't chose anything and just return empty/null value.
	 */
	private Optional<AntEdgeChoserContainer> closest(List<AntEdgeChoserContainer> chances, float random) {
		if (chances.size() < 1) {
			// return null;
			return Optional.empty();
		}
		AtomicReference<AntEdgeChoserContainer> result = new AtomicReference<>(null);
		AtomicReference<Float> minChance = new AtomicReference<>(Float.MAX_VALUE);

		for (short i = 0; i < chances.size(); i++) {
			if (chances.get(i).chanceToGoHere <= random) {
				result.set(chances.get(i));
				minChance.set(chances.get(i).chanceToGoHere);
			} else {
				break;
			}
		}

		/*
		 * chances.stream().forEach(e -> {
		 * if (e.chanceToGoHere <= random) {
		 * result.set(e);
		 * minChance.set(e.chanceToGoHere);
		 * }
		 * });
		 */
		if (minChance.get() == Float.MAX_VALUE) {
			result.set(chances.get(0));
		}
		// return result.get();
		return Optional.of(result.get());
	}

	public void run_v2() {
		createAdjacencyMatrix();
		clearVisitedFlags();
		List<List<Chunk>> allSequencesContainer = new LinkedList<>();

		/*
		 * Chunk first=input.get(22);
		 * int _next=whereToGo_v2(22);
		 * Chunk next=input.get(_next);
		 * System.out.println(first+"\n"+next);
		 */

		for (int i = 0; i < input.size(); i++) {
			System.out.println("NEXT " + i);
			List<Chunk> chunks = new LinkedList<>();
			clearVisitedFlags();
			chunks.add(input.get(i));
			input.get(i).locked = true;
			while (chunks.size() < input.size()) {
				int nnext = whereToGo_v2(i);
				if (nnext > -1) {
					chunks.add(input.get(nnext));
					input.get(nnext).locked = true;
				} else {
					break;
				}
			}
			allSequencesContainer.add(chunks);
		}
		System.out.println("JOBA DONA, START SEARCH MIN LENGTH");
		AtomicReference<Float> minLength = new AtomicReference<>(Float.MAX_VALUE);
		allSequencesContainer.stream().forEach(e -> {
			AtomicReference<Float> curLength = new AtomicReference<>(0f);
			Chunk a = e.get(0);
			e.stream().skip(1).forEach(ee -> {
				curLength.set(curLength.get() + (float) a.chunkPosition.distance(ee.chunkPosition));
			});
			if (curLength.get() <= minLength.get()) {
				minLength.set(curLength.get());
				result = e;
			}
			System.out.println("CUR: " + curLength.get());
		});
		System.out.println("MIN :" + minLength.get());

	}

	private void createAdjacencyMatrix() {
		for (int i = 0, size = input.size(); i < matrix.length; i++) {
			matrix[i] = new DataHolder[size--];
		}
		int index = 0;
		for (int i = 0; i < matrix.length; i++) {
			Chunk vertexOne = input.get(i);
			for (int j = 0; j < matrix[i].length; j++) {
				int q = j + i;
				Chunk vertexTwo = input.get(q);
				DataHolder holder = new DataHolder();
				holder.index = index++;
				holder.h = i;
				holder.w = q;
				holder.distance = (float) vertexOne.chunkPosition.distance(vertexTwo.chunkPosition);
				matrix[i][j] = holder;
			}
		}
		try {
			Files.deleteIfExists(Paths.get(System.getProperty("user.home") + "\\Desktop\\s.txt"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < matrix.length; i++) {
				sb.append(Arrays.toString(matrix[i]) + "\n");
			}
			Files.write(Paths.get(System.getProperty("user.home") + "\\Desktop\\s.txt"), sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private int whereToGo_v2(int height) {
		Chunk from = input.get(height);
		from.locked = true;
		short powCost = 1;
		short powDistance = 5;
		float distanceDivider = 1f;
		DataHolder[] edg = matrix[height];
		List<DataHolder> edgesHolderContainer = new LinkedList<>();
		int _height = height - 1;
		int _width = 1;
		while (_height > -1) {
			DataHolder data = matrix[_height][_width];
			if (!input.get(data.h).locked) {
				edgesHolderContainer.add(data);
			}
			_height--;
			_width++;
		}
		for (int i = 1; i < edg.length; i++) {
			if (!input.get(edg[i].w).locked)
				edgesHolderContainer.add(edg[i]);
		}

		List<AntEdgeChoserContainer> choser = new LinkedList<>();
		edgesHolderContainer.stream().forEach(e -> {
			AntEdgeChoserContainer aecc = new AntEdgeChoserContainer();
			aecc.dataHolderEdge = e;
			aecc.chanceToGoHere = (float) (Math.pow(e.transitionCost, powCost)
					* (Math.pow(distanceDivider / e.distance, powDistance)));
			choser.add(aecc);
		});
		float chanceToAll = choser.stream().map(e -> e.chanceToGoHere).reduce(0f, Float::sum);
		choser.stream().forEach(e -> e.chanceToGoHere = e.chanceToGoHere / chanceToAll);

		choser.sort((o1, o2) -> Float.compare(o1.chanceToGoHere, o2.chanceToGoHere));

		ThreadLocalRandom tlr = ThreadLocalRandom.current();
		float rnd = tlr.nextFloat();

		Optional<AntEdgeChoserContainer> res = closest_v2(choser, rnd);

		if (res.isPresent()) {
			AntEdgeChoserContainer chosedAECC = res.get();
			DataHolder chosedDH = chosedAECC.dataHolderEdge;
			int where = chosedDH.h == height ? chosedDH.w : chosedDH.h;
			Chunk result = input.get(where);
			result.locked = true;
			return where;
		}

		return -1;

	}

	private Optional<AntEdgeChoserContainer> closest_v2(List<AntEdgeChoserContainer> chances, float random) {
		if (chances.size() < 1) {
			// return null;
			return Optional.empty();
		}
		AtomicReference<AntEdgeChoserContainer> result = new AtomicReference<>(null);
		AtomicReference<Float> minChance = new AtomicReference<>(Float.MAX_VALUE);

		for (short i = 0; i < chances.size(); i++) {
			if (chances.get(i).chanceToGoHere <= random) {
				result.set(chances.get(i));
				minChance.set(chances.get(i).chanceToGoHere);
			} else {
				break;
			}
		}

		if (minChance.get() == Float.MAX_VALUE) {
			result.set(chances.get(0));
		}
		// return result.get();
		return Optional.of(result.get());
	}

}
