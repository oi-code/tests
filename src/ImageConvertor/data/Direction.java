package ImageConvertor.data;

public enum Direction {

	LEFT(new int[] { -1, 0 }),

	RIGHT(new int[] { 1, 0 }),

	UP(new int[] { 0, -1 }),

	DOWN(new int[] { 0, 1 }),

	RIGHT_UP(new int[] { 1, -1 }),

	LEFT_UP(new int[] { -1, -1 }),

	RIGHT_DOWN(new int[] { 1, 1 }),

	LEFT_DOWN(new int[] { -1, 1 }),

	STUB(new int[] { 0, 0 });

	private int[] ans;

	Direction(int[] is) {
		ans = is;
	}

	public int[] getDirection() {
		return ans;
	}
	
	public int getHeight() {
		return ans[1];
	}
	
	public int getWidth() {
		return ans[0];
	}

	@Override
	public String toString() {
		return "" + this.name().charAt(0) + "" + this.name().charAt(this.name().length() - 1) + "".toUpperCase();
	}
}
