package net.tiffit.wynnforge.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.tiffit.wynnforge.wynnapi.items.ItemDB;
import net.tiffit.wynnforge.wynnapi.items.WynnItem;

public enum PlayerClass {

	Archer(1, "Arrow Storm", "Escape", "Bomb Arrow", "Arrow Shield"),
	Warrior(3, "Bash", "Charge", "Uppercut", "War Scream"),
	Mage(2, "Healing", "Teleport", "Meteor", "Ice Snake"),
	Assassin(4, "Spin Attack", "Vanish", "Multihits", "Smoke Bomb");
	
	public final int spell_index;
	public final String[] abilities;

	PlayerClass(int spell_index, String... abilities){
		this.spell_index = spell_index;
		this.abilities = abilities;
	}
	
	public static PlayerClass getClassFromItem(ItemStack stack){
		WynnItem item = ItemDB.findItem(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()), true);
		if(item != null){
			if(item.type.equals("Wand"))return PlayerClass.Mage;
			if(item.type.equals("Spear"))return PlayerClass.Warrior;
			if(item.type.equals("Bow"))return PlayerClass.Archer;
			if(item.type.equals("Dagger"))return PlayerClass.Assassin;
		}else if(stack.hasTagCompound() && stack.getTagCompound().hasKey("display") && stack.getTagCompound().getCompoundTag("display").hasKey("Lore")){
			NBTTagList lore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
			String last_line = TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(lore.tagCount() - 1));
			String[] words = last_line.split(" ");
			if(words.length > 2 && words[0].equals("Crafted")){
				if(words[1].equals("Wand"))return PlayerClass.Mage;
				if(words[1].equals("Spear"))return PlayerClass.Warrior;
				if(words[1].equals("Bow"))return PlayerClass.Archer;
				if(words[1].equals("Dagger"))return PlayerClass.Assassin;
			}
			for(int i = 0; i < lore.tagCount(); i++){
				String line = TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(i));
				String[] line_words = line.split(" ");
				if(line_words.length > 3){
					if(line_words[1].equals("Class") && line_words[2].equals("Req:")){
						System.out.println(line_words[3]);
						if(line_words[3].equals("Mage/Dark"))return PlayerClass.Mage;
						if(line_words[3].equals("Warrior/Knight"))return PlayerClass.Warrior;
						if(line_words[3].equals("Archer/Hunter"))return PlayerClass.Archer;
						if(line_words[3].equals("Assassin/Ninja"))return PlayerClass.Assassin;
					}
				}
			}
		}
		return null;
	}
	
	public static int getAbilityLevel(int playerlevel, int type){
		if(type == 0){
			if(playerlevel >= 36)return 3;
			if(playerlevel >= 16)return 2;
			if(playerlevel >= 1)return 1;
		}
		if(type == 1){
			if(playerlevel >= 46)return 3;
			if(playerlevel >= 26)return 2;
			if(playerlevel >= 11)return 1;
		}
		if(type == 2){
			if(playerlevel >= 56)return 3;
			if(playerlevel >= 36)return 2;
			if(playerlevel >= 21)return 1;
		}
		if(type == 3){
			if(playerlevel >= 66)return 3;
			if(playerlevel >= 46)return 2;
			if(playerlevel >= 31)return 1;
		}
		return 0;
	}
	
}
