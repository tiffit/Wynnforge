package net.tiffit.wynnforge.module;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.utils.WFUtils;

public class ModuleBetterShops extends ModuleBase {

	private EntityLivingBase lastInteract = null;
	
	private List<String> SHOPS = Arrays.asList("Weapon Shop", "Armour Shop", "Dungeon Scroll Shop", "Liquid Shop",
			"Emerald Shop", "Potion Shop", "Horse Shop", "Scroll Shop", "Junk Shop", "Dungeon Shop",
			"Wheat Shop", "Egg Shop", "Bucket Shop", "Bowl Shop", "Accessory Shop", "Powder Shop", "Seasail Shop",
			"Potato Shop", "Mushroom Shop", "Quartz Shop", "Melon Shop", "Explosives Shop");
	
	public ModuleBetterShops() {
		super("Better Shops");
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteractSpecific e){
		if(e.getTarget() instanceof EntityLivingBase){
			lastInteract = (EntityLivingBase) e.getTarget();
		}
	}
	
	@SubscribeEvent
	public void onRenderGui(DrawScreenEvent.Post e) {
		if (e.getGui() instanceof GuiChest) {
			GuiChest g = (GuiChest) e.getGui();
			if (SHOPS.contains(g.lowerChestInventory.getName())) {
				Minecraft mc = Minecraft.getMinecraft();
				int guiLeft = (g.width - 176) / 2;
				int guiTop = (g.height - 166) / 2;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GuiInventory.drawEntityOnScreen(guiLeft - 40, guiTop + 100, 50, guiLeft - e.getMouseX() - 40, guiTop - e.getMouseY() + 20, lastInteract == null ? new EntityVillager(mc.world) : lastInteract);
				mc.fontRenderer.drawString(g.lowerChestInventory.getName(), g.width/2 - mc.fontRenderer.getStringWidth(g.lowerChestInventory.getName())/2, guiTop - 40, 0xffeeee33);
				String emeralds = "Carrying " + WFUtils.getCurrentEmeralds() + "\u00B2";
				mc.fontRenderer.drawString(emeralds, guiLeft - 40 - mc.fontRenderer.getStringWidth(emeralds)/2, guiTop - 10, 0xff11cc11);
			}
		}
	}

}
