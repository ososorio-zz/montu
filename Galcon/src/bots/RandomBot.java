package bots;
import java.util.List;
import java.util.Random;

import playgame.Planet;
import playgame.PlanetWars;

public class RandomBot {
	public static void DoTurn(PlanetWars pw) {
		// (1) If we current have a fleet in flight, then do nothing until it
		// arrives.
		if (pw.MyFleets().size() >= 1) {
			return;
		}
		// (2) Pick one of my planets at random.
		Random r = new Random();
		Planet source = null;
		List<Planet> p = pw.MyPlanets();
		if (p.size() > 0) {
			source = p.get(r.nextInt(p.size()));
		}
		// (3) Pick a target planet at random.
		Planet dest = null;
		p = pw.Planets();
		if (p.size() > 0) {
			dest = p.get(r.nextInt(p.size()));
		}
		// (4) Send half the ships from source to dest.
		if (source != null && dest != null) {
			int numShips = source.NumShips() / 2;
			pw.IssueOrder(source, dest, numShips);
		}
	}
}
