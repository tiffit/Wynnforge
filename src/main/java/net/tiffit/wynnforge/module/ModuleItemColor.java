package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.ConfigHelper;
import net.tiffit.wynnforge.wynnapi.items.ItemDB;
import net.tiffit.wynnforge.wynnapi.items.WynnItem;

@ModuleClass
public class ModuleItemColor extends ModuleBase {

	// Config
	private boolean id, unid;
	
	private boolean skipRender;

	public ModuleItemColor() {
		super("Item Color");
	}

	@SubscribeEvent
	public void onRender(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (!skipRender)
			renderElements(event.getGui());
		skipRender = false;
	}

	@SubscribeEvent
	public void drawTooltipEvent(RenderTooltipEvent.Pre event) {
		renderElements(Minecraft.getMinecraft().currentScreen);
		skipRender = true;
	}

	private void renderElements(GuiScreen gui) {
		if (gui instanceof GuiContainer) {
			GuiContainer guiContainer = (GuiContainer) gui;
			Container container = guiContainer.inventorySlots;

			int guiLeft = guiContainer.getGuiLeft();
			int guiTop = guiContainer.getGuiTop();
			for (Slot s : container.inventorySlots) {
				ItemStack stack = s.getStack();
				int x = guiLeft + s.xPos;
				int y = guiTop + s.yPos;
				
				Gui.drawRect(x, y, x + 16, y + 16, getColor(stack));
			}
		}
	}

	private int getColor(ItemStack stack) {
		if(stack.isEmpty())return 0;
		String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
		WynnItem itm = null;
		int c_unique = 0x50FFFF55;
		int c_rare = 0x50FF55FF;
		int c_legendary = 0x5055FFFF;
		int c_mythic = 0x50AA0000;
		int c_set = 0x5055FF55;
		if(id && itm != (itm = ItemDB.findItem(name, true))){
			String tier = itm.tier;
			switch(tier){
			case "Unique": return c_unique;
			case "Rare": return c_rare;
			case "Legendary": return c_legendary;
			case "Mythic": return c_mythic;
			case "Set": return c_set;
			}
		}else if(unid && name.startsWith("Unidentified")){
			String prefix = stack.getDisplayName().substring(0, 2);
			if(prefix.equals(TextFormatting.YELLOW.toString()))return c_unique;
			if(prefix.equals(TextFormatting.LIGHT_PURPLE.toString()))return c_rare;
			if(prefix.equals(TextFormatting.AQUA.toString()))return c_legendary;
			if(prefix.equals(TextFormatting.RED.toString()))return c_mythic;
			if(prefix.equals(TextFormatting.GREEN.toString()))return c_set;
		}
		return 0;
	}
	
	@Override
	public void loadConfig(ConfigHelper cat) {
		id = cat.getBoolean("identified", true, null);
		unid = cat.getBoolean("un-identified", true, null);
	}

}
