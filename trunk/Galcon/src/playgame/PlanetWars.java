package playgame;

// Contestants do not need to worry about anything in this file. This is just
// helper code that does the boring stuff for you, so you can focus on the
// interesting stuff. That being said, you're welcome to change anything in
// this file if you know what you're doing.

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PlanetWars {
	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	private Game game;
	private int player;

	// Constructs a PlanetWars object instance, given a string containing a
	// description of a game state.
	public PlanetWars(Game game) {
		this.game = game;
	}

	// Returns the number of planets. Planets are numbered starting with 0.
	public int NumPlanets() {
		return game.NumPlanets();
	}

	// Returns the planet with the given planet_id. There are NumPlanets()
	// planets. They are numbered starting at 0.
	public Planet GetPlanet(int planetID) {
		return game.GetPlanet(planetID);
	}

	// Returns the number of fleets.
	public int NumFleets() {
		return game.NumFleets();
	}

	// Returns the fleet with the given fleet_id. Fleets are numbered starting
	// with 0. There are NumFleets() fleets. fleet_id's are not consistent from
	// one turn to the next.
	public Fleet GetFleet(int fleetID) {
		return game.GetFleet(fleetID);
	}
	
	public List<Fleet> Fleets() {
		return game.GetFleets();
	}
	
	
	public List<Fleet> GetEnemyFleet()
	{
		List<Fleet> listfleet= new  ArrayList<Fleet>( game.getFleets());
		
		for (Iterator iterator = listfleet.iterator(); iterator.hasNext();) {
			Fleet fleet = (Fleet) iterator.next();
			if(fleet.Owner()!=player)
			{
			iterator.remove();
			}
		}
		
		return listfleet;
	}

	// Returns a list of all the planets.
	public List<Planet> Planets() {
		return game.getPlanets();
	}

	// Return a list of all the planets owned by the current player. By
	// convention, the current player is always player number 1.
	public List<Planet> MyPlanets() {
		List<Planet> r = new ArrayList<Planet>();
		for (Planet p : Planets()) {
			if (p.Owner() == player) {
				r.add(p);
			}
		}
		return r;
	}

	// Return a list of all neutral planets.
	public List<Planet> NeutralPlanets() {
		List<Planet> r = new ArrayList<Planet>();
		for (Planet p : Planets()) {
			if (p.Owner() == 0) {
				r.add(p);
			}
		}
		return r;
	}

	// Return a list of all the planets owned by rival players. This excludes
	// planets owned by the current player, as well as neutral planets.
	public List<Planet> EnemyPlanets() {
		List<Planet> r = new ArrayList<Planet>();
		for (Planet p : Planets()) {
			if (p.Owner() != 0 && p.Owner() != player) {
				r.add(p);
			}
		}
		return r;
	}

	// Return a list of all the planets that are not owned by the current
	// player. This includes all enemy planets and neutral planets.
	public List<Planet> NotMyPlanets() {
		List<Planet> r = new ArrayList<Planet>();
		for (Planet p : Planets()) {
			if (p.Owner() != player) {
				r.add(p);
			}
		}
		return r;
	}

	// Return a list of all the fleets owned by the current player.
	public List<Fleet> MyFleets() {
		List<Fleet> r = new ArrayList<Fleet>();
		for (Fleet f : game.getFleets()) {
			if (f.Owner() == player) {
				r.add(f);
			}
		}
		return r;
	}

	// Returns the distance between two planets, rounded up to the next highest
	// integer. This is the number of discrete time steps it takes to get
	// between the two planets.
	public int Distance(int sourcePlanet, int destinationPlanet) {
		return game.Distance(sourcePlanet, destinationPlanet);
	}

	// Returns the distance between two planets, rounded up to the next highest
	// integer. This is the number of discrete time steps it takes to get
	// between the two planets.
	public int Distance(Planet source, Planet destination) {
		return game.Distance(source.PlanetID(), destination.PlanetID());
	}

	// Sends an order to the game engine. An order is composed of a source
	// planet number, a destination planet number, and a number of ships. A
	// few things to keep in mind:
	// * you can issue many orders per turn if you like.
	// * the planets are numbered starting at zero, not one.
	// * you must own the source planet. If you break this rule, the game
	// engine kicks your bot out of the game instantly.
	// * you can't move more ships than are currently on the source planet.
	// * the ships will take a few turns to reach their destination. Travel
	// is not instant. See the Distance() function for more info.
	public void IssueOrder(int sourcePlanet, int destinationPlanet, int numShips) {
		System.out.println("" + sourcePlanet + " " + destinationPlanet + " "
				+ numShips);
		System.out.flush();
	}

	// Sends an order to the game engine. An order is composed of a source
	// planet number, a destination planet number, and a number of ships. A
	// few things to keep in mind:
	// * you can issue many orders per turn if you like.
	// * the planets are numbered starting at zero, not one.
	// * you must own the source planet. If you break this rule, the game
	// engine kicks your bot out of the game instantly.
	// * you can't move more ships than are currently on the source planet.
	// * the ships will take a few turns to reach their destination. Travel
	// is not instant. See the Distance() function for more info.
	public void IssueOrder(Planet source, Planet dest, int numShips) {
		System.out.println(player + ":" + source.PlanetID() + " " + dest.PlanetID() + " "
				+ numShips);
		game.IssueOrder(player, source.PlanetID(), dest.PlanetID(), numShips);
	}

	// Sends the game engine a message to let it know that we're done sending
	// orders. This signifies the end of our turn.
	public void FinishTurn() {
		System.out.println("go");
		System.out.flush();
	}

	// Returns true if the named player owns at least one planet or fleet.
	// Otherwise, the player is deemed to be dead and false is returned.
	public boolean IsAlive(int playerID) {
		for (Planet p : Planets()) {
			if (p.Owner() == playerID) {
				return true;
			}
		}
		for (Fleet f : game.getFleets()) {
			if (f.Owner() == playerID) {
				return true;
			}
		}
		return false;
	}

	// If the game is not yet over (ie: at least two players have planets or
	// fleets remaining), returns -1. If the game is over (ie: only one player
	// is left) then that player's number is returned. If there are no
	// remaining players, then the game is a draw and 0 is returned.
	public int Winner() {
		Set<Integer> remainingPlayers = new TreeSet<Integer>();
		for (Planet p : Planets()) {
			remainingPlayers.add(p.Owner());
		}
		for (Fleet f : game.getFleets()) {
			remainingPlayers.add(f.Owner());
		}
		switch (remainingPlayers.size()) {
		case 0:
			return 0;
		case 1:
			return ((Integer) remainingPlayers.toArray()[0]).intValue();
		default:
			return -1;
		}
	}

	// Returns the number of ships that the current player has, either located
	// on planets or in flight.
	public int NumShips(int playerID) {
		int numShips = 0;
		for (Planet p : Planets()) {
			if (p.Owner() == playerID) {
				numShips += p.NumShips();
			}
		}
		for (Fleet f : game.getFleets()) {
			if (f.Owner() == playerID) {
				numShips += f.NumShips();
			}
		}
		return numShips;
	}

	// Returns the production of the given player.
	public int Production(int playerID) {
		int prod = 0;
		for (Planet p : Planets()) {
			if (p.Owner() == playerID) {
				prod += p.GrowthRate();
			}
		}
		return prod;
	}

}
