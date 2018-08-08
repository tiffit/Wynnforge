package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.PacketSendEvent;
import net.tiffit.wynnforge.wynnapi.items.ItemDB;

public class ModuleItemLock extends ModuleBase {

	public ModuleItemLock() {
		super("Item Lock");
	}

	@SubscribeEvent
	public void onPacket(PacketSendEvent e){
		if(e.getPacket() instanceof CPacketPlayerDigging){
			CPacketPlayerDigging p = (CPacketPlayerDigging) e.getPacket();
			if(p.getAction() == Action.DROP_ITEM || p.getAction() == Action.DROP_ALL_ITEMS){
				ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
				String itemName = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
				if(stack.getItem() == Items.SADDLE || ItemDB.findItem(itemName, true) != null)e.setCanceled(true);
			}
		}
	}

}
