package net.tiffit.wynnforge.gui.compass;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import akka.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.ItemCompass;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextFormatting;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.wynnapi.territories.TerritoryDB;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory;

public class GuiListCompass extends GuiListExtended{

    private final GuiCompass parent;
    private final List<GuiListCompassEntry> entries = Lists.<GuiListCompassEntry>newArrayList();
    public int select = -1;
    public int order = 0;
    private static final Ordering<GuiListCompassEntry> order_default = Ordering.from((GuiListCompassEntry f, GuiListCompassEntry s) -> {return 0;});
    private static final Ordering<GuiListCompassEntry> order_alphabetical = Ordering.from((GuiListCompassEntry f, GuiListCompassEntry s) -> {return f.territory.territory.compareTo(s.territory.territory);});
    private static final Ordering<GuiListCompassEntry> order_distance = Ordering.from((GuiListCompassEntry f, GuiListCompassEntry s) -> {return Integer.compare(f.distance, s.distance);});
	
	public GuiListCompass(GuiCompass parent, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
		super(mc, width, height, top, bottom, slotHeight);
		this.parent = parent;
		GuiPlayerTabOverlay e;
		refreshEntries("");
	}
	
	public String changeOrder() {
		order++;
		if(order >= 3)order = 0;
		if(order == 0)return "Default";
		if(order == 1)return "Alphabetical";
		if(order == 2)return "Distance";
		return "Error";
	}
	
	public void refreshEntries(String search) {
		entries.clear();
		select = -1;
		Ordering<GuiListCompassEntry> ordering = order_default;
		switch(order) {
		case 1:  ordering = order_alphabetical; break;
		case 2:  ordering = order_distance; break;
		default: ordering = order_default; break;
		}
		for(WynnTerritory t : TerritoryDB.territories) {
			if(t.location != null && t.territory.toLowerCase().contains(search.toLowerCase())) {
				entries.add(new GuiListCompassEntry(t, this));
			}
		}

		List<GuiListCompassEntry> sortedList = ordering.<GuiListCompassEntry>immutableSortedCopy(entries);
		entries.clear();
		entries.addAll(sortedList);
	}

	public void selectTerritory(WynnTerritory territory) {
		mc.displayGuiScreen(null);
		Vec2f center = territory.location.findCenter();
		mc.world.setSpawnPoint(new BlockPos(center.x, 64, center.y));
		Wynnforge.addChatMessage("Compass now points towards " + TextFormatting.DARK_GREEN + territory.territory);
	}
	
	@Override
	public IGuiListEntry getListEntry(int index) {
		return entries.get(index);
	}

	@Override
	protected int getSize() {
		return entries.size();
	}
	
	public void setSelected(int index) {
		select = index;
	}
	
    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.select;
    }

}
