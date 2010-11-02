package bots;
import playgame.Planet;
import playgame.PlanetWars;

public class RageBot {
	public static void DoTurn(PlanetWars pw) {
		for (Planet source : pw.MyPlanets()) {
			if (source.NumShips() < 10 * source.GrowthRate()) {
				continue;
			}
			Planet dest = null;
			int bestDistance = 999999;
			for (Planet p : pw.EnemyPlanets()) {
				int dist = pw.Distance(source, p);
				if (dist < bestDistance) {
					bestDistance = dist;
					dest = p;
				}
			}
			if (dest != null) {
				pw.IssueOrder(source, dest, source.NumShips());
			}
		}
	}

}
