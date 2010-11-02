package bots;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import playgame.ConquerOptimizer;
import playgame.Planet;
import playgame.PlanetWars;
import playgame.PlayerBot;

public class Test implements PlayerBot{

	
	public void DoTurn(PlanetWars war) {
		
		
		for(Planet p:war.MyPlanets())
		{
			ConquerOptimizer opt = new ConquerOptimizer(war,p);
			opt.optimize();
			
			
		}
		
		
	/*	int sendfleet = 0;
		
		if(arrayOptimize==null)
			return;
		
		int lenghtOptimize=arrayOptimize.size();
		 Iterator<?> it = arrayOptimize.entrySet().iterator();
		 Boolean next=true;
		  Map.Entry<Planet,Integer> e = null;
		for (Planet p : war.MyPlanets()) {
			
		
			
			if(next)
			{
		
				if(it.hasNext()){
					e = (Map.Entry<Planet,Integer>)it.next();
					sendfleet=e.getValue();
				}else
				{
					opt = new ConquerOptimizer(war);
					arrayOptimize=opt.optimize();
					return;
				}
			next=false;
			}
			
		if(p.NumShips()>sendfleet)
		{
			
			war.IssueOrder(p,e.getKey(), e.getValue());
			next=true;
			sendfleet=0;
		}else
			if(p.NumShips()<sendfleet)
			{
				
				war.IssueOrder(p,e.getKey(), p.NumShips()-1);
				sendfleet-=p.NumShips()-1;
			}
		
		}*/
		
		
	}

}
