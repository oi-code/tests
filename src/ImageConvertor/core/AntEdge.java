package ImageConvertor.core;

import java.util.Objects;

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

	public void changeDirection() {
		Chunk temp = from;
		from = to;
		to = temp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(from.index, to.index);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AntEdge other = (AntEdge) obj;
		return Objects.equals(from.index, other.from.index) && Objects.equals(to.index, other.to.index);
	}

}
