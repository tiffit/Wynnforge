package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketResourcePackStatus.Action;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.PacketRecieveEvent;
import net.tiffit.wynnforge.WFNetHandler;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;

@ModuleClass
public class ModuleWynnfix extends ModuleBase{

	public ModuleWynnfix() {
		super("Wynn Fix");
	}
	
	@SubscribeEvent
	public void onInteractHorse(EntityInteract e){
		if(e.getTarget() instanceof AbstractHorse) {
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onPacket(PacketRecieveEvent e){
		if(e.pre && e.getPacket() instanceof SPacketResourcePackSend){
			IResourcePack pack = Minecraft.getMinecraft().getResourcePackRepository().getServerResourcePack();
			if(pack != null){
				CPacketResourcePackStatus packet = new CPacketResourcePackStatus(Action.SUCCESSFULLY_LOADED);
				WFNetHandler.INSTANCE.sendPacket(packet);
				e.setCanceled(true);
			}
		}
	}
	
}
