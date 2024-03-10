package ImageConvertor.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chunk implements Cloneable, Comparable<Chunk> {
	public Direction direction = Direction.STUB;
	public boolean visited = false;
	public boolean locked = false;
	public int index = 0;
	public Point startPoint;
	public Point endPoint;
	public Point chunkPosition;
	public float layer;
	public float chunkTotalLuminiance;
	public List<Chunk> avalivableChunks;
	public int cloudIndex;

	private Chunk() {
	}

	public Chunk(short myHeight_, short myWidth_) {
		startPoint = new Point();
		endPoint = new Point();
		chunkPosition = new Point(myWidth_, myHeight_);
		avalivableChunks = new ArrayList<>();

	}

	public double getLength() {
		// return (int) Math.sqrt((endW - startW) * (endW - startW) + (endH - startH) * (endH - startH));
		// return (int)Math.hypot(Math.abs(endH-startH), Math.abs(endW-startW));
		return startPoint.distance(endPoint);
	}

	@Override
	public Chunk clone() {
		Chunk res = new Chunk();
		res.index = this.index;
		res.direction = this.direction;
		res.visited = this.visited;
		res.locked = this.locked;
		res.startPoint = this.startPoint;
		res.endPoint = this.endPoint;
		res.chunkPosition = this.chunkPosition;
		return res;
	}



	@Override
	public int hashCode() {
		return Objects.hash(index);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		return index == other.index;
	}

	@Override
	public String toString() {
		return "Chunk [direction=" + direction + ", visited=" + visited + ", locked=" + locked + ", index=" + index
				+ ", startPoint=" + startPoint + ", endPoint=" + endPoint + ", chunkPosition=" + chunkPosition
				+ ", layer=" + layer + ", chunkTotalLuminiance=" + chunkTotalLuminiance + ", avalivableChunks="
				+ avalivableChunks + ", cloudIndex=" + cloudIndex + "]";
	}

	@Override
	public int compareTo(Chunk o) {
		return Integer.compare(this.index, o.index);
	}

}