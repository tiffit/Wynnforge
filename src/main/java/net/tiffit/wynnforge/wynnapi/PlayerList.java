package net.tiffit.wynnforge.wynnapi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.tiffit.wynnforge.Wynnforge;

public class PlayerList {

	public static String[] getPlayersForWorld(String world){
		try {
			JsonObject obj = getObject();
			JsonArray arr = obj.get(world).getAsJsonArray();
			String[] players = new String[arr.size()];
			for(int i = 0; i < arr.size(); i++){
				players[i] = arr.get(i).getAsString();
			}
			return players;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static JsonObject cache;
	private static long lastCache = -1;
	
	private static JsonObject getObject() throws IOException{
		if(cache != null && System.currentTimeMillis() - lastCache < 1000*10)return cache;
		URL url = new URL("https://api.wynncraft.com/public_api.php?action=onlinePlayers");
		cache = Wynnforge.gson.fromJson(new InputStreamReader(url.openStream()), JsonObject.class);
		lastCache = System.currentTimeMillis();
		return cache;
	}
	
}
