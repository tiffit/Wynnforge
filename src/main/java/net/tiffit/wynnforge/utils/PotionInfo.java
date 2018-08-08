package net.tiffit.wynnforge.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;

public class PotionInfo {

	public final int charges;
	public final int amount;
	public final int lvl;
	
	private PotionInfo(int charges, int amount, int lvl){
		this.charges = charges;
		this.amount = amount;
		this.lvl = lvl;
	}
	
	public static PotionInfo getInfo(ItemStack stack){
		String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
		if(name.startsWith("Potion of Healing")){
			int charges = Integer.valueOf(name.split("\\[")[1].split("/")[0]);
			NBTTagList lore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
			int amount = Integer.valueOf(TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(1).split(" ")[2]));
			int lvl = Integer.valueOf(lore.getStringTagAt(4).split(" ")[3]);
			return new PotionInfo(charges, amount, lvl);
		}
		return null;
	}
	
}
