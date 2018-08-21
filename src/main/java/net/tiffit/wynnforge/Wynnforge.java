package net.tiffit.wynnforge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.tiffit.wynnforge.data.FriendsManager;
import net.tiffit.wynnforge.data.LocalData;
import net.tiffit.wynnforge.module.ModuleBase;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.ConfigHelper;
import net.tiffit.wynnforge.wynnapi.items.ItemDB;
import net.tiffit.wynnforge.wynnapi.territories.TerritoryDB;

@Mod(modid = Wynnforge.MODID, name = Wynnforge.NAME, version = Wynnforge.VERSION, clientSideOnly = true, dependencies = "before:journeymap;", guiFactory = Wynnforge.CONFIG_GUI_FACTORY)
public class Wynnforge {
	public static final String MODID = "wynnforge";
	public static final String NAME = "Wynnforge";
	public static final String VERSION = "0.2.0";
	public static final String CONFIG_GUI_FACTORY = "net.tiffit.wynnforge.gui.config.ConfigGuiFactory";

	private static List<ModuleBase> MODULES = new ArrayList<ModuleBase>();

	public static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

	public static Logger logger;

	private static File configFile;
	private static Set<ASMData> moduleASM;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		logger = event.getModLog();

		configFile = event.getSuggestedConfigurationFile();
		moduleASM = event.getAsmData().getAll(ModuleClass.class.getName());
		setupModules();
		TerritoryDB.init();
		ItemDB.init();
	}
	
	public static void setupModules(){
		ConfigManager.load(new Configuration(configFile));

		LocalData.loadData();
		FriendsManager.load();
		for (ModuleBase mod : MODULES) {
			MinecraftForge.EVENT_BUS.unregister(mod);
		}
		MODULES.clear();
		for (ASMData data : moduleASM) {
			try {
				Class<?> c = Class.forName(data.getClassName());
				if (ModuleBase.class.isAssignableFrom(c)) {
					String reqMod = (String) data.getAnnotationInfo().get("reqMod");
					if (reqMod == null || Loader.isModLoaded(reqMod))
						registerModule((ModuleBase) c.newInstance());
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		for (ModuleBase mod : MODULES) {
			MinecraftForge.EVENT_BUS.register(mod);
		}
	}

	private static void registerModule(ModuleBase m) {
		if (ConfigManager.isModuleLoaded(m.getConfigName(), m.defaultEnabled())) {
			MODULES.add(m);
			ConfigCategory cat = ConfigManager.getCategory(m.getConfigName());
			m.loadConfig(new ConfigHelper(cat));
			if (cat.isEmpty())
				ConfigManager.getConfig().removeCategory(cat);
		}
		ConfigManager.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		for (ModuleBase mod : MODULES) {
			mod.loadModule();
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@SubscribeEvent
	public void handlePackets(ClientConnectedToServerEvent e) {
		ChannelPipeline chn = e.getManager().channel().pipeline();
		NetworkManager manager = (NetworkManager) chn.get("packet_handler");
		NetHandlerPlayClient old = (NetHandlerPlayClient) manager.getNetHandler();
		manager.setNetHandler(new WFNetHandler(Minecraft.getMinecraft(), new GuiMultiplayer(null), manager, old.getGameProfile()));
	}

	

	public static void addChatMessage(String msg) {
		String prefix = TextFormatting.GOLD + "[" + TextFormatting.YELLOW + NAME + TextFormatting.GOLD + "] ";
		String message = prefix + TextFormatting.GREEN + msg;
		Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentString(message));
	}
}
