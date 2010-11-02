package playgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class Game implements Cloneable {

	public Game(String mapInfo, int i, int initMode, String logFile) {
		logFilename = logFile;
		planets = new ArrayList<Planet>();
		fleets = new ArrayList<Fleet>();
		gamePlayback = new StringBuffer();
		this.initMode = initMode;
		switch (this.initMode) {
		case 0: // '\0'
			mapFilename = mapInfo;
			break;

		case 1: // '\001'
			mapData = mapInfo;
			break;
		}
		maxGameLength = i;
		numTurns = 0;
	}

	public int Init() {
		if (logFilename != null)
			try {
				FileOutputStream fileoutputstream = new FileOutputStream(
						logFilename);
				fileoutputstream.close();
				WriteLogMessage("initializing");
			} catch (Exception exception) {
			}
		switch (initMode) {
		case 0: // '\0'
			return LoadMapFromFile(mapFilename);

		case 1: // '\001'
			return ParseGameState(mapData);
		}
		return 0;
	}

	public void WriteLogMessage(String s) {
		if (logFilename == null)
			return;
		try {
			if (logFile == null)
				logFile = new BufferedWriter(new FileWriter(logFilename, true));
			logFile.write(s);
			logFile.newLine();
			logFile.flush();
		} catch (Exception exception) {
		}
	}

	public int NumPlanets() {
		return planets.size();
	}

	public Planet GetPlanet(int i) {
		return (Planet) planets.get(i);
	}

	public int NumFleets() {
		return fleets.size();
	}

	public Fleet GetFleet(int i) {
		return (Fleet) fleets.get(i);
	}

	public String toString() {
		return PovRepresentation(-1);
	}

	public String PovRepresentation(int i) {
		StringBuilder stringbuilder = new StringBuilder();
		Planet planet;
		for (Iterator<Planet> iterator = planets.iterator(); iterator.hasNext(); stringbuilder
				.append(String.format("P %f %f %d %d %d\n", new Object[] {
						Double.valueOf(planet.X()), Double.valueOf(planet.Y()),
						Integer.valueOf(PovSwitch(i, planet.Owner())),
						Integer.valueOf(planet.NumShips()),
						Integer.valueOf(planet.GrowthRate()) })))
			planet = (Planet) iterator.next();

		Fleet fleet;
		for (Iterator<Fleet> iterator1 = fleets.iterator(); iterator1.hasNext(); stringbuilder
				.append(String.format("F %d %d %d %d %d %d\n", new Object[] {
						Integer.valueOf(PovSwitch(i, fleet.Owner())),
						Integer.valueOf(fleet.NumShips()),
						Integer.valueOf(fleet.SourcePlanet()),
						Integer.valueOf(fleet.DestinationPlanet()),
						Integer.valueOf(fleet.TotalTripLength()),
						Integer.valueOf(fleet.TurnsRemaining()) })))
			fleet = (Fleet) iterator1.next();

		return stringbuilder.toString();
	}

	public static int PovSwitch(int i, int j) {
		if (i < 0)
			return j;
		if (j == i)
			return 1;
		if (j == 1)
			return i;
		else
			return j;
	}

	public int Distance(int i, int j) {
		Planet planet = (Planet) planets.get(i);
		Planet planet1 = (Planet) planets.get(j);
		double d = planet.X() - planet1.X();
		double d1 = planet.Y() - planet1.Y();
		return (int) Math.ceil(Math.sqrt(d * d + d1 * d1));
	}

	private void FightBattle(Planet planet) {
		TreeMap<Integer, Integer> treemap = new TreeMap<Integer, Integer>();
		treemap.put(Integer.valueOf(planet.Owner()), Integer.valueOf(planet
				.NumShips()));
		Iterator<Fleet> iterator = fleets.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Fleet fleet = (Fleet) iterator.next();
			if (fleet.TurnsRemaining() <= 0
					&& GetPlanet(fleet.DestinationPlanet()) == planet) {
				if (!treemap.containsKey(Integer.valueOf(fleet.Owner())))
					treemap.put(Integer.valueOf(fleet.Owner()), Integer
							.valueOf(fleet.NumShips()));
				else
					treemap.put(Integer.valueOf(fleet.Owner()), Integer
							.valueOf(fleet.NumShips()
									+ treemap.get(Integer
											.valueOf(fleet.Owner()))
											.intValue()));
				iterator.remove();
			}
		} while (true);
		Fleet fleet1 = new Fleet(0, 0);
		Fleet fleet2 = new Fleet(0, 0);
		Iterator<Entry<Integer,Integer>> iterator1 = treemap.entrySet().iterator();
		do {
			if (!iterator1.hasNext())
				break;
			Entry<Integer,Integer> entry = (Entry<Integer,Integer>) iterator1.next();
			if (((Integer) entry.getValue()).intValue() > fleet2.NumShips())
				if (((Integer) entry.getValue()).intValue() > fleet1.NumShips()) {
					fleet2 = fleet1;
					fleet1 = new Fleet(((Integer) entry.getKey()).intValue(),
							((Integer) entry.getValue()).intValue());
				} else {
					fleet2 = new Fleet(((Integer) entry.getKey()).intValue(),
							((Integer) entry.getValue()).intValue());
				}
		} while (true);
		if (fleet1.NumShips() > fleet2.NumShips()) {
			planet.NumShips(fleet1.NumShips() - fleet2.NumShips());
			planet.Owner(fleet1.Owner());
		} else {
			planet.NumShips(0);
		}
	}

	public void DoTimeStep() {
		Iterator<Planet> iterator = planets.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Planet planet = (Planet) iterator.next();
			if (planet.Owner() > 0)
				planet.AddShips(planet.GrowthRate());
		} while (true);
		Fleet fleet;
		for (Iterator<Fleet> iterator1 = fleets.iterator(); iterator1.hasNext(); fleet
				.TimeStep())
			fleet = (Fleet) iterator1.next();

		Planet planet1;
		for (Iterator<Planet> iterator2 = planets.iterator(); iterator2.hasNext(); FightBattle(planet1))
			planet1 = (Planet) iterator2.next();

		boolean flag = false;
		for (Iterator<Planet> iterator3 = planets.iterator(); iterator3.hasNext();) {
			Planet planet2 = (Planet) iterator3.next();
			if (flag)
				gamePlayback.append(",");
			gamePlayback.append(planet2.Owner());
			gamePlayback.append(".");
			gamePlayback.append(planet2.NumShips());
			flag = true;
		}

		Fleet fleet1;
		for (Iterator<Fleet> iterator4 = fleets.iterator(); iterator4.hasNext(); gamePlayback
				.append(fleet1.TurnsRemaining())) {
			fleet1 = (Fleet) iterator4.next();
			if (flag)
				gamePlayback.append(",");
			gamePlayback.append(fleet1.Owner());
			gamePlayback.append(".");
			gamePlayback.append(fleet1.NumShips());
			gamePlayback.append(".");
			gamePlayback.append(fleet1.SourcePlanet());
			gamePlayback.append(".");
			gamePlayback.append(fleet1.DestinationPlanet());
			gamePlayback.append(".");
			gamePlayback.append(fleet1.TotalTripLength());
			gamePlayback.append(".");
		}

		gamePlayback.append(":");
		numTurns++;
	}

	public int IssueOrder(int player, int from, int to, int fleetSize) {
		Planet planet = (Planet) planets.get(from);
		if (planet.Owner() != player || fleetSize > planet.NumShips() || fleetSize < 0) {
			WriteLogMessage((new StringBuilder()).append("Dropping player ")
					.append(player).append(". source.Owner() = ").append(
							planet.Owner()).append(", playerID = ").append(player)
					.append(", numShips = ").append(fleetSize).append(
							", source.NumShips() = ").append(planet.NumShips())
					.toString());
			DropPlayer(player);
			return -1;
		} else {
			planet.RemoveShips(fleetSize);
			int dist = Distance(from, to);
			Fleet fleet = new Fleet(planet.Owner(), fleetSize, from, to, dist, dist);
			fleets.add(fleet);
			return 0;
		}
	}

	public void AddFleet(Fleet fleet) {
		fleets.add(fleet);
	}

	public int IssueOrder(int i, String s) {
		String as[] = s.split(" ");
		if (as.length != 3) {
			return -1;
		} else {
			int j = Integer.parseInt(as[0]);
			int k = Integer.parseInt(as[1]);
			int l = Integer.parseInt(as[2]);
			return IssueOrder(i, j, k, l);
		}
	}

	public void DropPlayer(int i) {
		Iterator<Planet> planetIter = planets.iterator();
		do {
			if (!planetIter.hasNext())
				break;
			Planet planet = (Planet) planetIter.next();
			if (planet.Owner() == i)
				planet.Owner(0);
		} while (true);
		Iterator<Fleet> fleetIter = fleets.iterator();
		do {
			if (!fleetIter.hasNext())
				break;
			Fleet fleet = (Fleet) fleetIter.next();
			if (fleet.Owner() == i)
				fleet.Kill();
		} while (true);
	}

	public boolean IsAlive(int i) {
		for (Iterator<Planet> iterator = planets.iterator(); iterator.hasNext();) {
			Planet planet = (Planet) iterator.next();
			if (planet.Owner() == i)
				return true;
		}

		for (Iterator<Fleet> iterator1 = fleets.iterator(); iterator1.hasNext();) {
			Fleet fleet = (Fleet) iterator1.next();
			if (fleet.Owner() == i)
				return true;
		}

		return false;
	}

	public int Winner() {
		TreeSet<Integer> treeset = new TreeSet<Integer>();
		Planet planet;
		for (Iterator<Planet> iterator = planets.iterator(); iterator.hasNext(); treeset
				.add(Integer.valueOf(planet.Owner())))
			planet = (Planet) iterator.next();

		Fleet fleet;
		for (Iterator<Fleet> iterator1 = fleets.iterator(); iterator1.hasNext(); treeset
				.add(Integer.valueOf(fleet.Owner())))
			fleet = (Fleet) iterator1.next();

		treeset.remove(Integer.valueOf(0));
		if (numTurns > maxGameLength) {
			int i = -1;
			int j = -1;
			Iterator<Integer> iterator2 = treeset.iterator();
			do {
				if (!iterator2.hasNext())
					break;
				int k = iterator2.next().intValue();
				int l = NumShips(k);
				if (l == j)
					i = 0;
				else if (l > j) {
					i = k;
					j = l;
				}
			} while (true);
			return i;
		}
		switch (treeset.size()) {
		case 0: // '\0'
			return 0;

		case 1: // '\001'
			return ((Integer) treeset.toArray()[0]).intValue();
		}
		return -1;
	}

	public String GamePlaybackString() {
		return gamePlayback.toString();
	}

	public int NumShips(int i) {
		int j = 0;
		Iterator<?> iterator = planets.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Planet planet = (Planet) iterator.next();
			if (planet.Owner() == i)
				j += planet.NumShips();
		} while (true);
		iterator = fleets.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Fleet fleet = (Fleet) iterator.next();
			if (fleet.Owner() == i)
				j += fleet.NumShips();
		} while (true);
		return j;
	}

	private Color GetColor(int i, ArrayList<Color> arraylist) {
		if (i > arraylist.size())
			return Color.PINK;
		else
			return (Color) arraylist.get(i);
	}

	private Point getPlanetPos(Planet planet, double d, double d1, double d2,
			double d3, int i, int j) {
		int k = (int) (((planet.X() - d1) / (d2 - d1)) * (double) i);
		int l = j - (int) (((planet.Y() - d) / (d3 - d)) * (double) j);
		return new Point(k, l);
	}

	private double inherentRadius(Planet planet) {
		return Math.sqrt(planet.GrowthRate());
	}

	void Render(int i, int j, double d, BufferedImage bufferedimage,
			ArrayList<Color> colors, Graphics2D graphics2d) {
		Font font = new Font("Sans Serif", 1, 12);
		Font font1 = new Font("Sans serif", 1, 18);
//		Color color = new Color(188, 189, 172);
		Color color1 = Color.BLACK;
		if (bufferedimage != null)
			graphics2d.drawImage(bufferedimage, 0, 0, null);
		double d1 = 1.7976931348623157E+308D;
		double d2 = 1.7976931348623157E+308D;
		double d3 = 4.9406564584124654E-324D;
		double d4 = 4.9406564584124654E-324D;
		Iterator<Planet> iterator = planets.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Planet planet = (Planet) iterator.next();
			if (planet.X() < d2)
				d2 = planet.X();
			if (planet.X() > d3)
				d3 = planet.X();
			if (planet.Y() > d4)
				d4 = planet.Y();
			if (planet.Y() < d1)
				d1 = planet.Y();
		} while (true);
		double d5 = d3 - d2;
		double d6 = d4 - d1;
		double d7 = 0.10000000000000001D;
		d2 -= d5 * d7;
		d3 += d5 * d7;
		d1 -= d6 * d7;
		d4 += d6 * d7;
		Point apoint[] = new Point[planets.size()];
		graphics2d.setFont(font);
		FontMetrics fontmetrics = graphics2d.getFontMetrics(font);
		double d8 = 1.7976931348623157E+308D;
		for (int k = 0; k < planets.size(); k++) {
			for (int i1 = k + 1; i1 < planets.size(); i1++) {
				Planet planet1 = (Planet) planets.get(k);
				Planet planet3 = (Planet) planets.get(i1);
				double d9 = planet3.X() - planet1.X();
				double d11 = planet3.Y() - planet1.Y();
				double d14 = Math.sqrt(d9 * d9 + d11 * d11);
//				double d16 = inherentRadius(planet1);
//				double d18 = inherentRadius(planet3);
				double d20 = d14 / Math.sqrt(planet1.GrowthRate());
				d8 = Math.min(d20, d8);
			}

		}

		d8 *= 1.2D;
		int l = 0;
		Planet planet2;
		int j1;
		int k1;
		for (Iterator<Planet> iterator1 = planets.iterator(); iterator1.hasNext(); graphics2d
				.drawString(Integer.toString(planet2.NumShips()), j1, k1)) {
			planet2 = (Planet) iterator1.next();
			Point point = getPlanetPos(planet2, d1, d2, d3, d4, i, j);
			apoint[l++] = point;
			j1 = point.x;
			k1 = point.y;
			double d12 = d8 * inherentRadius(planet2);
			int l1 = (int) Math.min((d12 / (d3 - d2)) * (double) i,
					(d12 / (d4 - d1)) * (double) j);
			graphics2d.setColor(GetColor(planet2.Owner(), colors));
			int i2 = j1 - l1 / 2;
			int j2 = k1 - l1 / 2;
			graphics2d.fillOval(i2, j2, l1, l1);
			Color color2 = graphics2d.getColor();
			for (int k2 = 1; k2 >= 0; k2--) {
				graphics2d.setColor(graphics2d.getColor().brighter());
				graphics2d.drawOval(j1 - (l1 - k2) / 2, k1 - (l1 - k2) / 2, l1
						- k2, l1 - k2);
			}

			graphics2d.setColor(color2);
			for (int l2 = 0; l2 < 3; l2++) {
				graphics2d.drawOval(j1 - (l1 + l2) / 2, k1 - (l1 + l2) / 2, l1
						+ l2, l1 + l2);
				graphics2d.setColor(graphics2d.getColor().darker());
			}

			Rectangle2D rectangle2d = fontmetrics.getStringBounds(Integer
					.toString(planet2.NumShips()), graphics2d);
			j1 = (int) ((double) j1 - rectangle2d.getWidth() / 2D);
			k1 += fontmetrics.getAscent() / 2;
			graphics2d.setColor(color1);
		}

		graphics2d.setFont(font1);
		fontmetrics = graphics2d.getFontMetrics(font1);
		Iterator<Fleet> iterator2 = fleets.iterator();
		do {
			if (!iterator2.hasNext())
				break;
			Fleet fleet = (Fleet) iterator2.next();
			Point point1 = apoint[fleet.SourcePlanet()];
			Point point2 = apoint[fleet.DestinationPlanet()];
			double d10 = 1.0D - (double) fleet.TurnsRemaining()
					/ (double) fleet.TotalTripLength();
			if (d10 <= 0.98999999999999999D && d10 >= 0.01D) {
				double d13 = point2.x - point1.x;
				double d15 = point2.y - point1.y;
				double d17 = (double) point1.x + d13 * d10;
				double d19 = (double) point1.y + d15 * d10;
				Rectangle2D rectangle2d1 = fontmetrics.getStringBounds(Integer
						.toString(fleet.NumShips()), graphics2d);
				graphics2d
						.setColor(GetColor(fleet.Owner(), colors).darker());
				graphics2d.drawString(Integer.toString(fleet.NumShips()),
						(int) (d17 - rectangle2d1.getWidth() / 2D),
						(int) (d19 + rectangle2d1.getHeight() / 2D));
			}
		} while (true);
	}

	/**
	 * Load Planets & Fleets
	 * @param s
	 * @return
	 */
	private int ParseGameState(String s) {
		planets.clear();
		fleets.clear();
		String as[] = s.split("\n");
		for (int i = 0; i < as.length; i++) {
			String s1 = as[i];
			int j = s1.indexOf('#');
			if (j >= 0)
				s1 = s1.substring(0, j);
			if (s1.trim().length() == 0)
				continue;
			String as1[] = s1.split(" ");
			if (as1.length == 0)
				continue;
			if (as1[0].equals("P")) {
				if (as1.length != 6)
					return 0;
				double x = Double.parseDouble(as1[1]);
				double y = Double.parseDouble(as1[2]);
				int owner = Integer.parseInt(as1[3]);
				int ships = Integer.parseInt(as1[4]);
				int growthRate = Integer.parseInt(as1[5]);
				Planet planet = new Planet(owner, ships, growthRate, x, y);
				planet.setPlanetID(planets.size());
				planets.add(planet);
				if (gamePlayback.length() > 0)
					gamePlayback.append(":");
				gamePlayback.append((new StringBuilder()).append("").append(x)
						.append(",").append(y).append(",").append(owner).append(
								",").append(ships).append(",").append(growthRate)
						.toString());
				continue;
			}
			if (as1[0].equals("F")) {
				if (as1.length != 7)
					return 0;
				int k = Integer.parseInt(as1[1]);
				int l = Integer.parseInt(as1[2]);
				int i1 = Integer.parseInt(as1[3]);
				int j1 = Integer.parseInt(as1[4]);
				int l1 = Integer.parseInt(as1[5]);
				int j2 = Integer.parseInt(as1[6]);
				Fleet fleet = new Fleet(k, l, i1, j1, l1, j2);
				fleets.add(fleet);
			} else {
				return 0;
			}
		}

		gamePlayback.append("|");
		return 1;
	}

	private int LoadMapFromFile(String s)
    {
        StringBuffer stringbuffer;
        BufferedReader bufferedreader;
        stringbuffer = new StringBuffer();
        bufferedreader = null;
        try {
			bufferedreader = new BufferedReader(new FileReader(s));
	        String s1;
	        while((s1 = bufferedreader.readLine()) != null) 
	        {
	            stringbuffer.append(s1);
	            stringbuffer.append("\n");
	        }
            bufferedreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
        return ParseGameState(stringbuffer.toString());
    }

	private Game(Game game) {
		planets = new ArrayList<Planet>();
		Planet planet;
		for (Iterator<Planet> iterator = game.planets.iterator(); iterator.hasNext(); planets
				.add((Planet) (Planet) planet.clone()))
			planet = (Planet) iterator.next();

		fleets = new ArrayList<Fleet>();
		Fleet fleet;
		for (Iterator<Fleet> iterator1 = game.fleets.iterator(); iterator1.hasNext(); fleets
				.add((Fleet) (Fleet) fleet.clone()))
			fleet = (Fleet) iterator1.next();

		if (game.mapFilename != null)
			mapFilename = new String(game.mapFilename);
		if (game.mapData != null)
			mapData = new String(game.mapData);
		initMode = game.initMode;
		if (game.gamePlayback != null)
			gamePlayback = new StringBuffer(game.gamePlayback);
		maxGameLength = game.maxGameLength;
		numTurns = game.numTurns;
	}

	public Object clone() {
		return new Game(this);
	}

	private ArrayList<Planet> planets;
	private ArrayList<Fleet> fleets;
	private String mapFilename;
	private String mapData;
	private int initMode;
	private StringBuffer gamePlayback;
	private int maxGameLength;
	private int numTurns;
	private String logFilename;
	private BufferedWriter logFile;
	
	//TODO hide please!
	public List<Planet> getPlanets() {
		return planets;
	}

	//TODO hide please!
	public List<Fleet> getFleets() {
		return fleets;
	}
}
