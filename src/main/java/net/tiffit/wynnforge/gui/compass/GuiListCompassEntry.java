package net.tiffit.wynnforge.gui.compass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory.TerritoryLoc;

public class GuiListCompassEntry implements IGuiListEntry {

	public final WynnTerritory territory;
	private final GuiListCompass parent;
	private long lastClickTime;
	public final int distance;
	
	public GuiListCompassEntry(WynnTerritory territory, GuiListCompass parent) {
		this.territory = territory;
		this.parent = parent;
		Minecraft mc = Minecraft.getMinecraft();
		distance = (int)territory.location.distance();
	}
	
	@Override
	public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		fr.drawStringWithShadow(territory.territory, x + 2, y + 2, 0xffffffff);
		fr.drawString(territory.guild, x + listWidth - fr.getStringWidth(territory.guild) - 3, y + 24, 0xff444444);
		TerritoryLoc loc = territory.location;
		String boundTemplate = "[%s, %s]";
		String xBounds = String.format(boundTemplate, loc.startX, loc.endX);
		String yBounds = String.format(boundTemplate, loc.startY, loc.endY);
		fr.drawString(xBounds + ", " + yBounds, x+ 2, y + 13, 0xffaaaaaa);
		fr.drawString(distance + "m", x + 2, y + 24, 0xff888888);
	}

	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
		parent.setSelected(slotIndex);
        if (Minecraft.getSystemTime() - this.lastClickTime < 250L)
        {
            parent.selectTerritory(territory);
            return true;
        }
        else
        {
            this.lastClickTime = Minecraft.getSystemTime();
            return false;
        }
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
	}

}
