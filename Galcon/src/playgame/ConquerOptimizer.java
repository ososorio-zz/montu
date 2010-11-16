package playgame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// sum(Nj) - sum( Ni + 1 + {Gi}*di) + sum( Gi* sum( [t - dij] ) )
// {Gi} = (i belongs enemy)? Gi : 0; 
// [t] = Max(0, t)

public class ConquerOptimizer {
	private class Combinator extends Combinatorial
	{
		public Combinator(int combinationSize, int setSize) {
			super(combinationSize, setSize);
		}

		@Override
		public void onCombination(Collection<Integer> comb) {
			int sumShips = 0;

			// discard if the combination is above the 
			// fleetSize
			for(Integer i : comb)
			{
				Planet p = neutral.get(i);
				sumShips += p.NumShips() + 1;
				if(sumShips > myPlanet.NumShips())
					return;
			}
			
			
			//Calculate the gain formula for this combination
			// N - sum( Ni + 1 ) + sum( Gi*Max(0, Max(di) - di)
			int sumGrowth = 0;
			for(Integer i : comb)
			{
				Planet p = neutral.get(i);
				int dist = war.Distance(1, p.PlanetID());
				sumGrowth += p.GrowthRate()*(maxDist - dist);
			}
			int score = myPlanet.NumShips() - sumShips + sumGrowth;
			if(bestScore < score)
			{
				bestScore = score;
				bestComb = new ArrayList<Planet>(comb.size());
				for(Integer i:comb)
				{
					bestComb.add(neutral.get(i));
				}
			}
		}
	}
	class SortByDistance implements Comparator<Planet>
	{
		@Override
		public int compare(Planet A, Planet B) {
			return Double.compare(A.getRelativeDist(), B.getRelativeDist());
		}
	}
	class SortByShips implements Comparator<Planet>
	{
		@Override
		public int compare(Planet A, Planet B) {
			return A.NumShips() - B.NumShips();
		}
	}
	PlanetWars war;
	List<Planet> neutral;
	Planet myPlanet;
	List<Planet> bestComb;

	int bestScore;
	
	int maxDist = 0;

	double risk;
	
	SortByShips sortByShips = new SortByShips();
	private Planet hisPlanet;
	
	public ConquerOptimizer(PlanetWars war,Planet myPlanet)
	{		
		this.war  = war;
		neutral = war.NotMyPlanets();	
		//PlanetGroup myPlanets = new PlanetGroup(war.MyPlanets());
		this.myPlanet = myPlanet;
		hisPlanet = war.GetPlanet(2);
		//Reduce the search to neutral most closer to me than the enemy
		for (Iterator<Planet> it = neutral.iterator(); it.hasNext();) {
			Planet p = (Planet) it.next();
			int toMyPlanet = war.Distance(p, myPlanet);
			maxDist = Math.max(maxDist, toMyPlanet);
			
			float chkFrontier = toMyPlanet / (toMyPlanet + (float)war.Distance(p, hisPlanet));
			p.setRelativeDist(chkFrontier);
			if( chkFrontier > TuningParams.FRONTIER)
				it.remove();
		}
		float relDistToHim = 1 - (float)war.Distance(hisPlanet, myPlanet) / maxDist;
		risk = (1 - Math.sqrt(1 - relDistToHim*relDistToHim));		
		maxDist++;
	}
	
	public void optimize()
	{
		HashMap<Planet, Integer> arrayOptimize= new HashMap<Planet, Integer>();
		//Find what is the max number of items in the combination
		Collections.sort(neutral, sortByShips);
		int accum = 0;
		int combSize = 0;
		for (; combSize < neutral.size(); combSize++) {
			accum += neutral.get(combSize).NumShips();
			if(accum > myPlanet.NumShips()+1)
			{				
				break;
			}
		}
		if(combSize==0)
		{
			return;
		}
		
		Combinator comb = new Combinator(combSize, neutral.size());
		for(int i = 1; i <= combSize; i++)
		{
			comb.setCombinationSize(i);
			comb.start();
		}
		
		//Distribute ships wisely among the planets		
		
//		calculateScore(myPlanet);
//		int totalCost = 0;
//		float divisor = 0;
//		for(Planet p:bestComb)
//		{
//			totalCost += p.NumShips() + 1;
//			divisor += p.getRelativeDist();
//		}
//		int saldo = myPlanet.NumShips() - totalCost;
//
//		divisor += saldo*risk;
//		for(Planet p:bestComb)
//		{
//			float weight = p.getRelativeDist()/divisor;
//			int send = Math.round(p.NumShips()+ 1 + saldo * weight);
//			System.out.println("Sending "+ send + " to "+p.PlanetID() );
//			war.IssueOrder(this.myPlanet, p, send);
//		}
	}

	/**
	 * Calculate the relative score of each of my planets (or enemy planets). 
	 * If score is negative, means under-protected
	 * If score is positive, means over-protected
	 * 
	 * If all the planets have the same score, is balanced.
	 * If the balanced score is negative -> loosing!
	 * If the balanced score is positive -> winning!
	 * 
	 * Sp = Np + Gp*K
	 * e = enemy
	 * m = myself
	 * Score(s) = Ss*sum(1/d(s,m)^2) - sum[ Se / d(s,m)^2] + sum[ Sm/ d(m,s)^2 ]
	 * 
	 * @param myPlanet s
	 * @return
	 */
	private double calculateScore(Planet myPlanet) {
		float G_WEIGHT = 3;
		float extern = 0;
		float sumDist = 0;
		for(Planet p : war.Planets())
		{
			if(p.PlanetID() != myPlanet.PlanetID() && p.Owner() != 0)
			{
				int dist = war.Distance(p, myPlanet);
				float distPow2 = dist * dist;
				sumDist += 1f/distPow2;
				double score = (p.GrowthRate()*G_WEIGHT + p.NumShips()) / distPow2;
				extern += (p.Owner() == myPlanet.Owner())? score : -score;
			}
		}
		
		for(Fleet f : war.Fleets())
		{
			if(f.DestinationPlanet() == myPlanet.PlanetID())
			{
				int dist = f.TurnsRemaining();
				double score = f.NumShips() / (dist*dist);
				extern += (f.Owner() == myPlanet.Owner())? score : -score;
			}
		}
		return (myPlanet.NumShips() + myPlanet.GrowthRate()*G_WEIGHT)* sumDist + extern;
	}

}
