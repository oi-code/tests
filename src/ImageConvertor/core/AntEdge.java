package ImageConvertor.core;

import ImageConvertor.data.Chunk;

public class AntEdge {
	private Chunk from;
	private Chunk to;
	private float distance;
	private float transitionCost;

	public AntEdge(Chunk from, Chunk to) {
		this.from = from;
		this.to = to;
		distance = (float) from.chunkPosition.distance(to.chunkPosition);
	}

	public float getDistance() {
		return distance;
	}

	public float getTransitionCost() {
		return transitionCost;
	}

	public void setTransitionCost(float transitionCost) {
		this.transitionCost = transitionCost;
	}
}
