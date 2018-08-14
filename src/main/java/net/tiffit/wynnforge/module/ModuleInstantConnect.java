package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.ServerResourceMode;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;

@ModuleClass
public class ModuleInstantConnect extends ModuleBase{

	public ModuleInstantConnect() {
		super("Instant Connect");
	}

	private static boolean connected = false;
	
	@SubscribeEvent
	public void onLoadMenu(GuiOpenEvent e){
		if(e.getGui() instanceof GuiMainMenu && !connected){
			ServerData server = new ServerData("Wynncraft", "play.wynncraft.com", false);
			server.setResourceMode(ServerResourceMode.ENABLED);
			e.setGui(new GuiConnecting(e.getGui(), Minecraft.getMinecraft(), server));
			connected = true;
		}
	}
	
}
