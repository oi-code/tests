package ImageConvertor.data;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ImageConvertor.core.AntEdge;

public class Chunk implements Cloneable, Comparable<Chunk> {
	public Direction direction = Direction.STUB;
	public boolean notAvalivable;
	public boolean locked;
	public int index = 0;
	public Point startPoint;
	public Point endPoint;
	public Point chunkPosition;
	public byte layer;
	public float chunkTotalLuminiance;
	public Set<Chunk> avalivableChunks;
	public int cloudIndex;
	public Set<AntEdge> edges;

	private Chunk() {

	}

	public Chunk(short myHeight_, short myWidth_) {
		startPoint = new Point();
		endPoint = new Point();
		chunkPosition = new Point(myWidth_, myHeight_);
		avalivableChunks = new HashSet<>();
		edges = new HashSet<>();

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
		res.notAvalivable = this.notAvalivable;
		res.locked = this.locked;
		res.startPoint = this.startPoint;
		res.endPoint = this.endPoint;
		res.chunkPosition = this.chunkPosition;
		return res;
	}
	
	public void notAvalivable() {
		this.notAvalivable = true;
	}

	public List<Chunk> getFreeAroundChunks() {
		return avalivableChunks.stream().filter(e -> !e.locked).toList();
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
		return "Chunk [visited=" + notAvalivable + ", locked=" + locked + ", index=" + index + ", startPoint=" + startPoint
				+ ", endPoint=" + endPoint + ", chunkPosition=" + chunkPosition + ", layer=" + layer
				+ ", chunkTotalLuminiance=" + chunkTotalLuminiance + ", avalivableChunksSise=" + avalivableChunks.size()
				+ ", cloudIndex=" + cloudIndex + "]";
	}

	@Override
	public int compareTo(Chunk o) {
		return Integer.compare(this.index, o.index);
	}

}