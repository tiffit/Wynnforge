package net.tiffit.wynnforge;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.channel.ChannelPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.tiffit.wynnforge.data.FriendsManager;
import net.tiffit.wynnforge.data.LocalData;
import net.tiffit.wynnforge.module.ModuleBase;
import net.tiffit.wynnforge.module.ModuleBetterShops;
import net.tiffit.wynnforge.module.ModuleInfo;
import net.tiffit.wynnforge.module.ModuleInstantConnect;
import net.tiffit.wynnforge.module.ModuleItemLock;
import net.tiffit.wynnforge.module.ModuleJourneymap;
import net.tiffit.wynnforge.module.ModuleMusicVisualizer;
import net.tiffit.wynnforge.module.ModulePlayerInfo;
import net.tiffit.wynnforge.module.ModuleQuickDrop;
import net.tiffit.wynnforge.module.ModuleQuickParty;
import net.tiffit.wynnforge.module.ModuleUsefulCompass;
import net.tiffit.wynnforge.module.ModuleWorldSelection;
import net.tiffit.wynnforge.module.ModuleXpPercent;
import net.tiffit.wynnforge.wynnapi.items.ItemDB;
import net.tiffit.wynnforge.wynnapi.territories.TerritoryDB;

@Mod(modid = Wynnforge.MODID, name = Wynnforge.NAME, version = Wynnforge.VERSION, clientSideOnly = true, dependencies = "before:journeymap;")
public class Wynnforge {
	public static final String MODID = "wynnforge";
	public static final String NAME = "Wynnforge";
	public static final String VERSION = "0.0.2";

	private static List<ModuleBase> MODULES = new ArrayList<ModuleBase>();
	
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
	
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		
		logger = event.getModLog();
		
		ConfigManager.load(new Configuration(event.getSuggestedConfigurationFile()));
		
		LocalData.loadData();
		FriendsManager.load();
		
		registerModule(new ModuleInstantConnect());
		registerModule(new ModuleWorldSelection());
		registerModule(new ModuleXpPercent());
		registerModule(new ModuleItemLock());
		registerModule(new ModuleQuickDrop());
		registerModule(new ModuleInfo());
		registerModule(new ModuleJourneymap());
		registerModule(new ModuleBetterShops());
		registerModule(new ModuleMusicVisualizer());
		registerModule(new ModuleQuickParty());
		registerModule(new ModuleUsefulCompass());
		registerModule(new ModulePlayerInfo());
		
		for(ModuleBase mod : MODULES){
			MinecraftForge.EVENT_BUS.register(mod);
		}
		TerritoryDB.init();
		ItemDB.init();
	}
	
	private void registerModule(ModuleBase m){
		if(ConfigManager.isModuleLoaded(m.getConfigName(), m.defaultEnabled())){
			MODULES.add(m);
			ConfigCategory cat = ConfigManager.getCategory(m.getConfigName());
			m.loadConfig(cat);
			if(cat.isEmpty())ConfigManager.getConfig().removeCategory(cat);
		}
		ConfigManager.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		for(ModuleBase mod : MODULES){
			mod.init(event);
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
	
	@SubscribeEvent
	public void handlePackets(ClientConnectedToServerEvent e){
		ChannelPipeline chn = e.getManager().channel().pipeline();
		NetworkManager manager  = (NetworkManager) chn.get("packet_handler");
		NetHandlerPlayClient old = (NetHandlerPlayClient) manager.getNetHandler();
		manager.setNetHandler(new WFNetHandler(Minecraft.getMinecraft(), new GuiMultiplayer(null), manager, old.getGameProfile()));
	}
	
	public static void addChatMessage(String msg){
		String prefix = TextFormatting.GOLD + "[" + TextFormatting.YELLOW + NAME + TextFormatting.GOLD + "] ";
		String message = prefix + TextFormatting.GREEN + msg;
		Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentString(message));
	}
}
