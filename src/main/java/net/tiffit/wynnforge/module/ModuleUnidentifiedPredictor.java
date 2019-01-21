package net.tiffit.wynnforge.module;

import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.WFUtils;
import net.tiffit.wynnforge.wynnapi.items.ItemDB;
import net.tiffit.wynnforge.wynnapi.items.WynnItem;

@ModuleClass
public class ModuleUnidentifiedPredictor extends ModuleBase {

	public ModuleUnidentifiedPredictor() {
		super("Unidentified Predictor");
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onTooltip(ItemTooltipEvent e) {
		if (!WFUtils.isInWorld())
			return;
		ItemStack stack = e.getItemStack();
		String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
		if (name.startsWith("Unidentified")) {
			String type = name.replace("Unidentified ", "");
			NBTTagList lore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);

			String lvlRangeLine = TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(6));
			String lvlRange = lvlRangeLine.substring(13).trim();
			String rangeBounds[] = lvlRange.split("-");
			int lowerRange = Integer.valueOf(rangeBounds[0]);
			int upperRange = Integer.valueOf(rangeBounds[1]);

			String rarity = TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(7)).substring(8).trim();
			List<WynnItem> possibilities = Lists.newArrayList();
			for (WynnItem item : ItemDB.items) {
				if (item != null && item.level >= lowerRange && item.level <= upperRange && rarity.equals(item.tier)
						&& type.equals(item.type)) {
					possibilities.add(item);
				}
			}
			int offset = e.getToolTip().size() - (e.getFlags().isAdvanced() ? 2 : 0);
			e.getToolTip().add(offset++, "");
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
				e.getToolTip().add(offset++, TextFormatting.DARK_AQUA + "Predictions:");
				possibilities.sort((o1, o2) -> {return o1.level > o2.level ? 1 : 0;});
				for (WynnItem item : possibilities) {
					e.getToolTip().add(offset++, TextFormatting.DARK_AQUA + "- " + TextFormatting.GRAY + item.name + " (Lv. " + item.level + ")");
				}
			}else {
				e.getToolTip().add(offset++, TextFormatting.DARK_AQUA + "Hold CTRL to see ID predictions. (" + possibilities.size() + ")");
			}
		}
	}

}
