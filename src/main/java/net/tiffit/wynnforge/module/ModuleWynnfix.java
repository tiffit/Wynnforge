package net.tiffit.wynnforge.module;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
	
}
