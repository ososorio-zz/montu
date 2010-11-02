package playgame;
//Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 10/21/2010 8:47:17 AM
//Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
//Decompiler options: packimports(3) 
//Source File Name:   Fleet.java


public class Fleet
 implements Comparable<Fleet>, Cloneable
{

 public Fleet(int i, int j, int k, int l, int i1, int j1)
 {
     owner = i;
     numShips = j;
     sourcePlanet = k;
     destinationPlanet = l;
     totalTripLength = i1;
     turnsRemaining = j1;
 }

 public Fleet(int i, int j)
 {
     owner = i;
     numShips = j;
     sourcePlanet = -1;
     destinationPlanet = -1;
     totalTripLength = -1;
     turnsRemaining = -1;
 }

 public int Owner()
 {
     return owner;
 }

 public int NumShips()
 {
     return numShips;
 }

 public int SourcePlanet()
 {
     return sourcePlanet;
 }

 public int DestinationPlanet()
 {
     return destinationPlanet;
 }

 public int TotalTripLength()
 {
     return totalTripLength;
 }

 public int TurnsRemaining()
 {
     return turnsRemaining;
 }

 public void RemoveShips(int i)
 {
     numShips -= i;
 }

 public void Kill()
 {
     owner = 0;
     numShips = 0;
     turnsRemaining = 0;
 }

 public void TimeStep()
 {
     if(turnsRemaining > 0)
         turnsRemaining--;
     else
         turnsRemaining = 0;
 }

 public int compareTo(Fleet obj)
 {
     Fleet fleet = (Fleet)obj;
     return numShips - fleet.numShips;
 }

 private Fleet(Fleet fleet)
 {
     owner = fleet.owner;
     numShips = fleet.numShips;
     sourcePlanet = fleet.sourcePlanet;
     destinationPlanet = fleet.destinationPlanet;
     totalTripLength = fleet.totalTripLength;
     turnsRemaining = fleet.turnsRemaining;
 }

 public Object clone()
 {
     return new Fleet(this);
 }

 private int owner;
 private int numShips;
 private int sourcePlanet;
 private int destinationPlanet;
 private int totalTripLength;
 private int turnsRemaining;
}
