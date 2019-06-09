package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.tiffit.wynnforge.TimedRunnables;
import net.tiffit.wynnforge.WFNetHandler;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.ConfigHelper;
import net.tiffit.wynnforge.utils.PotionInfo;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

@ModuleClass
public class ModuleQuickDrop extends ModuleBase {

	// Config
	private List<String> whitelist, blacklist;
	private int potionDifferenceLevel = 20;

	public ModuleQuickDrop() {
		super("Quick Drop");
	}

	public static KeyBinding quickDrop;

	@Override
	public void loadModule() {
		if (quickDrop == null) {
			quickDrop = new KeyBinding("Quick Drop", Keyboard.KEY_R, "Wynnforge");
			ClientRegistry.registerKeyBinding(quickDrop);
		}
	}

	@SubscribeEvent
	public void onQuickDrop(KeyInputEvent e) {
		if (quickDrop.isPressed()) {
			EntityPlayerSP p = Minecraft.getMinecraft().player;
			Minecraft mc = Minecraft.getMinecraft();
			short action_number = 0;
			int slot = 0;
			int amount = 0;
			for (ItemStack s : p.inventory.mainInventory) {
				boolean slotDropped = false;
				String name = TextFormatting.getTextWithoutFormattingCodes(s.getDisplayName());
				int sl = slot + (slot < 9 ? 36 : 0);
				if (s.hasTagCompound() && s.getTagCompound().hasKey("display") && s.getTagCompound().getCompoundTag("display").hasKey("Lore")) {
					NBTTagList lore = s.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
					if (lore.getStringTagAt(0).equals(TextFormatting.DARK_GRAY + "Junk Item") && !blacklist.contains(name)) {
						short an = action_number++;
						TimedRunnables.addRunnable(() -> WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(0, sl, 1, ClickType.THROW, s, an)), an * 4);
						amount += s.getCount();
						slotDropped = true;
					}
					PotionInfo pinfo = PotionInfo.getInfo(s);
					if (pinfo != null) {
						if (mc.player.experienceLevel < pinfo.lvl || mc.player.experienceLevel - potionDifferenceLevel >= pinfo.lvl) {
							short an = action_number++;
							TimedRunnables.addRunnable(() -> WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(0, sl, 1, ClickType.THROW, s, an)), an * 4);
							amount += s.getCount();
							slotDropped = true;
						}
					}
				}
				if (!slotDropped && whitelist.contains(name)) {
					short an = action_number++;
					TimedRunnables.addRunnable(() -> WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(0, sl, 1, ClickType.THROW, s, an)), an * 4);
					amount += s.getCount();
					slotDropped = true;
				}
				slot++;
			}
			if (amount > 0) {
				Wynnforge.addChatMessage("You dropped " + amount + " trash.");
			}
		}
	}

	@Override
	public void loadConfig(ConfigHelper conf) {
		whitelist = Arrays.asList(conf.getStringList("whitelist", new String[0], null));
		blacklist = Arrays.asList(conf.getStringList("blacklist", new String[0], null));
		potionDifferenceLevel = conf.getInteger("potion_level_difference", 20, "How many levels below a potion has to be to be considered junk.");
		super.loadConfig(conf);
	}

}
