package ImageConvertor.data;

import java.util.List;

import ImageConvertor.core.Controller;

public class Edge implements Cloneable {
	public float distanceBetweenPoints;
	public float weight = 0.2f;
	public int heightIndex = -1;
	public int widthIndex = -1;
	public byte[] visited = new byte[Controller.N_THREADS];
	public Chunk from;
	public Chunk to;
	public double distance;
	public List<Chunk> reachableChunks;

	private Edge() {
	}

	public Edge(int heightIndex, int widthIndex, float distanceBetweenPoints) {
		this.heightIndex = heightIndex;
		this.widthIndex = widthIndex;
		this.distanceBetweenPoints = distanceBetweenPoints;
		distance = from.chunkPosition.distance(to.chunkPosition);
	}

	@Override
	public Edge clone() {
		Edge copy = new Edge();
		copy.distanceBetweenPoints = distanceBetweenPoints;
		copy.weight = weight;
		copy.heightIndex = heightIndex;
		copy.widthIndex = widthIndex;
		return copy;
	}

	@Override
	public String toString() {
		return String.format("[distande: %f, weight: %f, index: %d]", distanceBetweenPoints, weight, heightIndex);
	}
}
