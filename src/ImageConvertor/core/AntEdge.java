package ImageConvertor.core;

import java.util.Arrays;
import java.util.Objects;

import ImageConvertor.data.Chunk;

public class AntEdge {
	public Chunk vertexOne;
	public Chunk vertexTwo;
	public boolean[] visited;
	private float distance;
	private float transitionCost=0.5f;

	public AntEdge(Chunk vertexOne, Chunk vertexTwo) {
		this.vertexOne = vertexOne;
		this.vertexTwo = vertexTwo;
		distance = (float) vertexOne.chunkPosition.distance(vertexTwo.chunkPosition);
		visited = new boolean[1];
	}

	public float distanceBetweenVertexes() {
		return distance;
	}

	public float getTransitionCost() {
		return transitionCost;
	}

	public void setTransitionCost(float transitionCost) {
		this.transitionCost = transitionCost;
	}

	public void swapVertexes() {
		Chunk temp = this.vertexOne;
		this.vertexOne = this.vertexTwo;
		this.vertexTwo = temp;
	}

	/*
	 * public boolean isTheSameEdge(AntEdge other) {
	 * Chunk vertexOne = other.vertexOne;
	 * Chunk vertexTwo = other.vertexTwo;
	 * if (this.vertexOne == vertexOne) {
	 * if (this.vertexTwo == vertexTwo) {
	 * return true;
	 * }
	 * } else if (this.vertexOne == vertexTwo) {
	 * if (this.vertexTwo == vertexOne) {
	 * return true;
	 * }
	 * }
	 * return false;
	 * // return (aa == from || aa == to) && (bb == from || bb == to);
	 * }
	 */

	@Override
	public int hashCode() {
		return Objects.hash((vertexOne.index + vertexTwo.index) * 31);
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
		if (this.vertexOne == other.vertexOne) {
			if (this.vertexTwo == other.vertexTwo) {
				return true;
			}
		} else if (this.vertexOne == other.vertexTwo) {
			if (this.vertexTwo == other.vertexOne) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "AntEdge [vertexOne=" + vertexOne.index + ", vertexTwo=" + vertexTwo.index + ", visited=" + Arrays.toString(visited)
				+ ", distance=" + distance + ", transitionCost=" + transitionCost + "]";
	}

}
