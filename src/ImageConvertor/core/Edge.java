package ImageConvertor.core;

import ImageConvertor.views.desktop.View;

public class Edge implements Cloneable {
	public float distanceBetweenPoints;
	public float weight = 0.2f;
	public int heightIndex = -1;
	public int widthIndex = -1;
	public byte[] visited = new byte[Controller.N_THREADS];

	private Edge() {
	}

	public Edge(int heightIndex, int widthIndex, float distanceBetweenPoints) {
		this.heightIndex = heightIndex;
		this.widthIndex = widthIndex;
		this.distanceBetweenPoints = distanceBetweenPoints;
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
