package net.tiffit.wynnforge;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ConfigManager {

	private static Configuration conf;
	
	public static void load(Configuration config){
		conf = config;
		conf.getCategory("Modules").setComment("Enable/Disable Modules");
		save();
	}
	
	public static boolean isModuleLoaded(String name, boolean def){
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
	
	@SubscribeEvent
	public static void onConfigGuiEdited(OnConfigChangedEvent e) {
		if (e.getModID().equals(Wynnforge.MODID)) {
			conf.save();
			Wynnforge.setupModules();
		}
	}
	
}
