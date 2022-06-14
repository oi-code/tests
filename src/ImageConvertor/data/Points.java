package ImageConvertor.data;

import java.awt.Point;

public class Points implements Cloneable, Comparable<Points> {
	public Direction direction = Direction.STUB;
	public boolean visited = false;
	public boolean locked = false;
	public int index = 0;
	public Point startPoint;
	public Point endPoint;
	public Point myPosition;
	public float layer;

	private Points() {
	}

	public Points(short myHeight_, short myWidth_) {
		startPoint = new Point();
		endPoint = new Point();
		myPosition = new Point(myWidth_, myHeight_);

	}

	public double getLength() {
		// return (int) Math.sqrt((endW - startW) * (endW - startW) + (endH - startH) * (endH - startH));
		// return (int)Math.hypot(Math.abs(endH-startH), Math.abs(endW-startW));
		return startPoint.distance(endPoint);
	}

	@Override
	public Points clone() {
		Points res = new Points();
		res.index = this.index;
		res.direction = this.direction;
		res.visited = this.visited;
		res.locked = this.locked;
		res.startPoint = this.startPoint;
		res.endPoint = this.endPoint;
		res.myPosition = this.myPosition;
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
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
		Points other = (Points) obj;
		if (index != other.index)
			return false;
		return true;
	}

	public String toString() {
		return String.format("[index: %d, curPointLgth: %f, dir: %s, height %d, width %d]", index, getLength(), direction,
				myPosition.y, myPosition.x);
	}

	@Override
	public int compareTo(Points o) {
		return Integer.compare(this.index, o.index);
	}

}