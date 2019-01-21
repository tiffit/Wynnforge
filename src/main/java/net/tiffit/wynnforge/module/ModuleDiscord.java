package net.tiffit.wynnforge.module;

import me.paulhobbel.discordrp.api.rpc.DiscordEventHandlers;
import me.paulhobbel.discordrp.api.rpc.DiscordRPC;
import me.paulhobbel.discordrp.api.rpc.DiscordRPCHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.support.WynncraftRichPresence;
import net.tiffit.wynnforge.wynnapi.PlayerList;
import net.tiffit.wynnforge.wynnapi.territories.TerritoryDB;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory;

@ModuleClass
public class ModuleDiscord extends ModuleBase {

	private WynncraftRichPresence rp;

	private boolean connected = false;
	private Thread runningThread = null;

	public ModuleDiscord() {
		super("Discord");
	}

	@Override
	public void loadModule() {
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		DiscordRPC.Initialize("479449550931755021", handlers);
		DiscordRPCHandler.start();
		rp = new WynncraftRichPresence.Builder().state("").details("Not Connected").build();
	}
	
	@Override
	public void unloadModule() {
		DiscordRPC.ClearPresence();
		DiscordRPCHandler.stop();
	}
	
	@SubscribeEvent
	public void connectServer(ClientConnectedToServerEvent e) {
		connected = true;
		runningThread = new Thread(new Runnable() {
			
			private long lastUpdate = 0;
			
			@Override
			public void run() {
				while (connected) {
					EntityPlayerSP p = Minecraft.getMinecraft().player;
					if (p != null && System.currentTimeMillis() - lastUpdate > 1000) {
						lastUpdate = System.currentTimeMillis();
						final String username = p.getName();
						String world = PlayerList.getPlayerWorld(username);
						if(world == null)continue;
						String details = world;
						String lit = username;
						if (world.startsWith("WC")){
							details = world.replace("WC", "World ");
							lit = username + " | lvl" + p.experienceLevel;
						}else if (world.startsWith("lobby")){
							details = world.replace("lobby", "Lobby ");
							lit = username;
						}
						boolean update = false;
						if (!rp.details.equals(details)) {
							rp.details = details;
							update = true;
						}
						if (!rp.largeImageText.equals(lit)) {
							rp.largeImageText = lit;
							update = true;
						}
						if (world.startsWith("WC")) {
							WynnTerritory found = null;
							WynnTerritory closest = null;
							double closestDistance = -1;
							for (WynnTerritory terr : TerritoryDB.territories) {
								if (terr.location != null && terr.location.isIn()) {
									found = terr;
									break;
								}else if(terr.location != null){
									if(closest == null){
										closest = terr;
										closestDistance = terr.location.distance();
									}else{
										double distanceNew = terr.location.distance();
										if(distanceNew > closestDistance){
											closest = terr;
											closestDistance = distanceNew;
										}
									}
								}
							}
							if (found != null) {
								if (!rp.state.equals("At " + found.territory)) {
									rp.state = "At " + found.territory;
									update = true;
								}
							}else if(closest != null && closestDistance < 3000){
								if (!rp.state.equals("Near " + closest.territory)) {
									rp.state = "Near " + closest.territory;
									update = true;
								}
							} else {
								if (!rp.state.isEmpty()) {
									rp.state = "";
									update = true;
								}
							}
						}
						if (update)
							rp.setPresence();
					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		runningThread.setName("Discord World Checker");
		runningThread.start();
	}

	@SubscribeEvent
	public void disconnectServer(ClientDisconnectionFromServerEvent e) {
		connected = false;
		runningThread = null;
		DiscordRPC.ClearPresence();
	}

}