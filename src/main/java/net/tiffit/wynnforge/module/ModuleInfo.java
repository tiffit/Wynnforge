package net.tiffit.wynnforge.module;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.ConfigHelper;
import net.tiffit.wynnforge.utils.PotionInfo;
import net.tiffit.wynnforge.utils.WFUtils;

@ModuleClass
public class ModuleInfo extends ModuleBase {

	// Config
	private boolean coordinates, direction, inventory, time;

	public ModuleInfo() {
		super("Info");
	}

	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent e) {
		if (e.getType() == ElementType.TEXT) {
			Minecraft mc = Minecraft.getMinecraft();
			if (!mc.gameSettings.showDebugInfo) {
				if (!WFUtils.isInWorld()) {
					return;
				}
				int yPos = -5;
				if (coordinates) {
					mc.fontRenderer.drawStringWithShadow("X: " + ItemStack.DECIMALFORMAT.format(mc.player.posX), 2, yPos += 10, 0xffffffff);
					mc.fontRenderer.drawStringWithShadow("Y: " + ItemStack.DECIMALFORMAT.format(mc.player.posY), 2, yPos += 10, 0xffffffff);
					mc.fontRenderer.drawStringWithShadow("Z: " + ItemStack.DECIMALFORMAT.format(mc.player.posZ), 2, yPos += 10, 0xffffffff);
				}
				if (direction) {
					float deg = mc.player.rotationYaw % 360;
					if (deg < 0)
						deg = 360 + deg;
					String rot = "" + deg;
					if (deg >= 360 - 45 || deg < 45)
						rot = "South";
					if (deg >= 45 && deg < 90 + 45)
						rot = "West";
					if (deg >= 90 + 45 && deg < 90 + 45 * 3)
						rot = "North";
					if (deg >= 90 + 45 * 3 && deg < 90 + 45 * 5)
						rot = "East";
					mc.fontRenderer.drawStringWithShadow("F: " + rot, 2, yPos += 10, 0xffffffff);
				}
				if(time){
					int current_time = (24000 - (int) (mc.world.getWorldTime() % 24000))/20;
					int minutes = current_time/60;
					int seconds = current_time % 60;
					mc.fontRenderer.drawStringWithShadow("Next SP: " + minutes + ":" + (seconds < 10 ? "0" : "") + seconds, 2, yPos += 10, 0xffffffff);
				}
				if (!coordinates && !direction)
					yPos -= 10;
				if (inventory) {
					mc.fontRenderer.drawStringWithShadow(TextFormatting.DARK_AQUA + "Inventory:", 2, yPos += 20, 0xffffffff);
					Map<Integer, List<PotionInfo>> potions = new LinkedHashMap<Integer, List<PotionInfo>>();
					for (int i = 19; i >= 0; i--)
						potions.put(i, new ArrayList<PotionInfo>());
					int potionTotalCharge = 0;
					List<String> horseTexts = new ArrayList<String>();
					for (ItemStack s : mc.player.inventory.mainInventory) {
						PotionInfo pinfo = PotionInfo.getInfo(s);
						if (pinfo != null) {
							if (mc.player.experienceLevel >= pinfo.lvl) {
								potions.get(pinfo.lvl / 5).add(pinfo);
								potionTotalCharge += pinfo.charges;
							}
						} else if (s.getItem() == Items.SADDLE && s.getDisplayName().endsWith("Horse")) {
							NBTTagList lore = s.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
							String horseText = lore.getStringTagAt(1).replace("Speed: ", "") + " " + s.getDisplayName();
							horseText += " (" + lore.getStringTagAt(4).replace("Xp: ", "") + ")";
							horseText = TextFormatting.getTextWithoutFormattingCodes(horseText);
							horseTexts.add(horseText);
						}
					}
					int treasure = WFUtils.getTreasureValue();
					String treasureText = "";
					if(treasure > 0)treasureText = " (+" + treasure + "\u00B2 in Treasure)";
					mc.fontRenderer.drawStringWithShadow(TextFormatting.GREEN.toString() + WFUtils.getCurrentEmeralds() + "\u00B2" + treasureText, 10, yPos += 10, 0xffffffff);
					int tokens = WFUtils.getCurrentTokens();
					if(tokens > 0)mc.fontRenderer.drawStringWithShadow(TextFormatting.AQUA.toString() + tokens + " Dungeon Token" + (tokens == 1 ? "" : "s"), 10, yPos += 10, 0xffffffff);
					for (String horseText : horseTexts)
						mc.fontRenderer.drawStringWithShadow(horseText, 10, yPos += 10, 0xffff9900);
					if (potionTotalCharge > 0) {
						mc.fontRenderer.drawStringWithShadow(potionTotalCharge + " HP Charge" + (potionTotalCharge == 1 ? "" : "s"), 10, yPos += 10, 0xffff5555);
						for (Entry<Integer, List<PotionInfo>> entry : potions.entrySet()) {
							if (!entry.getValue().isEmpty()) {
								int charges = 0;
								for (PotionInfo info : entry.getValue())
									charges += info.charges;
								int amount = entry.getValue().get(0).amount * charges;
								mc.fontRenderer.drawStringWithShadow(charges + "x L" + (entry.getKey() * 5) + " (" + amount + ")", 20, yPos += 10, 0xffff5555);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void loadConfig(ConfigHelper cat) {
		coordinates = cat.getBoolean("coordinates", true, null);
		direction = cat.getBoolean("direction", true, null);
		inventory = cat.getBoolean("inventory", true, null);
		time = cat.getBoolean("time", true, null);
	}

}
