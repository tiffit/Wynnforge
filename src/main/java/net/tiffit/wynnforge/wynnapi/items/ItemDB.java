package net.tiffit.wynnforge.wynnapi.items;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonObject;

import net.tiffit.wynnforge.Wynnforge;

public class ItemDB {
	
	public static WynnItem[] items;
	
	public static void init(){
		try {
			URL url = new URL("https://api.wynncraft.com/public_api.php?action=itemDB&category=all");
			items = Wynnforge.gson.fromJson(Wynnforge.gson.fromJson(new InputStreamReader(url.openStream()), JsonObject.class).get("items"), WynnItem[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static WynnItem findItem(String search, boolean exact){
		if(items == null)return null;
		for(WynnItem item : items){
			if((exact && item.name.equals(search)) || (!exact && item.name.toLowerCase().contains(search.toLowerCase())))return item;
		}
		return null;
	}
	
}
