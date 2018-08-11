package net.tiffit.wynnforge;

import java.util.HashMap;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigManager {

	private static Configuration conf;
	private static HashMap<String, Boolean> modules = new HashMap<String, Boolean>();
	
	public static void load(Configuration config){
		conf = config;
		conf.getCategory("Modules").setComment("Enable/Disable Modules");
		save();
	}
	
	public static boolean isModuleLoaded(String name, boolean def){
		if(modules.containsKey(name))return modules.get(name);
		boolean loaded = conf.get("Modules", name, def).getBoolean();
		save();
		return loaded;
	}
	
	public static ConfigCategory getCategory(String name){
		return conf.getCategory("mod_" + name);
	}
	
	public static void save() {
		conf.save();
	}
	
	public static Configuration getConfig() {
		return conf;
	}
	
}
