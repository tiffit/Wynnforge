package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.PacketRecieveEvent;
import net.tiffit.wynnforge.gui.GuiWorldSelection;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;

@ModuleClass
public class ModuleWorldSelection extends ModuleBase {

	public ModuleWorldSelection() {
		super("Better World Selection");
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
				if(((GuiChest)mc.currentScreen).lowerChestInventory.getDisplayName().getUnformattedText().startsWith("Wynncraft Servers")){
					mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiWorldSelection(packet, player)));
				}
			}
		}
	}

}
