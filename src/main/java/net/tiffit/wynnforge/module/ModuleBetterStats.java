package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.PacketRecieveEvent;
import net.tiffit.wynnforge.gui.GuiStats;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;

@ModuleClass
public class ModuleBetterStats extends ModuleBase {

	public ModuleBetterStats() {
		super("Better Stats");
	}
	
	@SubscribeEvent
	public void onPacket(PacketRecieveEvent e){
		Minecraft mc = Minecraft.getMinecraft();
		if(e.getPacket() instanceof SPacketWindowItems && !e.pre){
			SPacketWindowItems packet = (SPacketWindowItems) e.getPacket();
			Container con = mc.player.openContainer;
			EntityPlayer player = mc.player;
			if(player.isSneaking())return;
			if(con != null && mc.currentScreen instanceof GuiChest){
				String guiName = ((GuiChest)mc.currentScreen).lowerChestInventory.getDisplayName().getUnformattedText();
				if(guiName.endsWith("skill points remaining") || (guiName.endsWith("skill point remaining"))){
					mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiStats(packet, player)));
				}
			}
		}
	}

}
