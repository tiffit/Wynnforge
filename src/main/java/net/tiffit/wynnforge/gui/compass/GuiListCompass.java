package net.tiffit.wynnforge.gui.compass;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextFormatting;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.wynnapi.territories.TerritoryDB;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory;

import java.util.List;

public class GuiListCompass extends GuiListExtended{

    private final List<GuiListCompassEntry> entries = Lists.<GuiListCompassEntry>newArrayList();
    public int select = -1;
    public int order = 0;
    private static final Ordering<GuiListCompassEntry> order_default = Ordering.from((GuiListCompassEntry f, GuiListCompassEntry s) -> {return 0;});
    private static final Ordering<GuiListCompassEntry> order_alphabetical = Ordering.from((GuiListCompassEntry f, GuiListCompassEntry s) -> {return f.territory.territory.compareTo(s.territory.territory);});
    private static final Ordering<GuiListCompassEntry> order_distance = Ordering.from((GuiListCompassEntry f, GuiListCompassEntry s) -> {return Integer.compare(f.distance, s.distance);});
	
	public GuiListCompass(GuiCompass parent, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
		super(mc, width, height, top, bottom, slotHeight);
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
