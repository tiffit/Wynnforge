package net.tiffit.wynnforge.wynnapi.territories;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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
		
		public double distance(){
			Vec2f center = findCenter();
			EntityPlayer p = Minecraft.getMinecraft().player;
			Vec2f player = new Vec2f((float)p.posX, (float)p.posZ);
			return Math.sqrt(Math.pow(center.x - player.x, 2) + Math.pow(center.y - player.y, 2));
		}
		
		public boolean isIn() {
			EntityPlayer p = Minecraft.getMinecraft().player;
			double x = p.posX;
			double y = p.posZ;
			int startX = Math.min(this.startX, this.endX);
			int endX = Math.max(this.startX, this.endX);
			int startY = Math.min(this.startY, this.endY);
			int endY = Math.max(this.startY, this.endY);
			if(startX <= x && endX >= x && startY <= y && endY >= y)return true;
			return false;
		}
	}
	
}
