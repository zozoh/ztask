package org.nutz.mongo.dao.pojo;

public class SInner {

	public static SInner me(int x, int y) {
		SInner si = new SInner();
		si.setX(x);
		si.setY(y);
		return si;
	}

	private int x;

	private int y;

	private SInnerType type;

	public SInnerType getType() {
		return type;
	}

	public void setType(SInnerType type) {
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
