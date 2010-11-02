package playgame;

public class Planet implements Cloneable {

	public Planet(int owner, int numShips, int growthRate, double x, double y) {
		this.owner = owner;
		this.numShips = numShips;
		this.growthRate = growthRate;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {

		String fmt = "{id:%d, sh:%d, gr:%d, d:%.2f}";
		return String.format(fmt, id,numShips,growthRate,relativeDist);
	}

	public int Owner() {
		return owner;
	}

	public int NumShips() {
		return numShips;
	}

	public int GrowthRate() {
		return growthRate;
	}

	public double X() {
		return x;
	}

	public double Y() {
		return y;
	}

	public void Owner(int i) {
		owner = i;
	}

	public void NumShips(int i) {
		numShips = i;
	}

	public void AddShips(int i) {
		numShips += i;
	}

	public void RemoveShips(int i) {
		numShips -= i;
	}

	private Planet(Planet planet) {
		owner = planet.owner;
		numShips = planet.numShips;
		growthRate = planet.growthRate;
		x = planet.x;
		y = planet.y;
	}

	public Object clone() {
		return new Planet(this);
	}

	private int owner;
	private int numShips;
	private int growthRate;
	private double x;
	private double y;
	private int id;
	
	/**
	 * A value between 0-1,
	 * near 0 means near myself
	 * near 1 means near opponent
	 */
	private float relativeDist;

	public void setPlanetID(int id) {
		this.id = id;
	}

	public int PlanetID() {
		return id;
	}

	public void setRelativeDist(float dist) {
		this.relativeDist = dist;
	}

	public float getRelativeDist() {
		return this.relativeDist;
	}
}
