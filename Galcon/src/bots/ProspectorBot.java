package bots;
import playgame.Planet;
import playgame.PlanetWars;

public class ProspectorBot {
	public static void DoTurn(PlanetWars pw) {
		// (1) If we current have a fleet in flight, just do nothing.
		if (pw.MyFleets().size() >= 1) {
			return;
		}
		// (2) Find my strongest planet.
		Planet source = null;
		double sourceScore = Double.MIN_VALUE;
		for (Planet p : pw.MyPlanets()) {
			double score = (double) p.NumShips() / (1 + p.GrowthRate());
			if (score > sourceScore) {
				sourceScore = score;
				source = p;
			}
		}
		// (3) Find the weakest enemy or neutral planet.
		Planet dest = null;
		double destScore = Double.MIN_VALUE;
		for (Planet p : pw.NotMyPlanets()) {
			double score = (double) (1 + p.GrowthRate()) / p.NumShips();
			if (score > destScore) {
				destScore = score;
				dest = p;
			}
		}
		// (4) Send half the ships from my strongest planet to the weakest
		// planet that I do not own.
		if (source != null && dest != null) {
			int numShips = source.NumShips() / 2;
			pw.IssueOrder(source, dest, numShips);
		}
	}

}
