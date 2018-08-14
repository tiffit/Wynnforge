package net.tiffit.wynnforge.wynnapi.territories;

import net.minecraft.util.math.Vec2f;

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
		
		public Vec2f findCenter() {
			float diffX = (endX - startX)/2f + startX;
			float diffY = (endY - startY)/2f + startY;
			return new Vec2f(diffX, diffY);
		}
	}
	
}
