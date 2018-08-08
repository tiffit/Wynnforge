package net.tiffit.wynnforge.wynnapi.territories;

public class WynnTerritory {

	public String territory;
	public String guild;
	public String acquired;
	public String attacker;
	public TerritoryLoc location;

	public static class TerritoryLoc{
		public int startX;
		public int startY;
		public int endX;
		public int endY;
	}
	
}
