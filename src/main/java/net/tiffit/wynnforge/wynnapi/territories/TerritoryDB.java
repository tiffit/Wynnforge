package net.tiffit.wynnforge.wynnapi.territories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.tiffit.wynnforge.Wynnforge;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class TerritoryDB {

	public static List<WynnTerritory> territories;
	
	public static void init(){
		try {
			URL url = new URL("https://api.wynncraft.com/public_api.php?action=territoryList");
			JsonObject obj = Wynnforge.gson.fromJson(new InputStreamReader(url.openStream()), JsonObject.class).get("territories").getAsJsonObject();
			Set<Entry<String, JsonElement>> entries = obj.entrySet();
			territories = new ArrayList<WynnTerritory>();
			for(Entry<String, JsonElement> entry : entries){
				territories.add(Wynnforge.gson.fromJson(entry.getValue(), WynnTerritory.class));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
