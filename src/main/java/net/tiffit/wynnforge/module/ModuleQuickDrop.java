package net.tiffit.wynnforge.module;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.tiffit.wynnforge.TimedRunnables;
import net.tiffit.wynnforge.WFNetHandler;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.PotionInfo;

@ModuleClass
public class ModuleQuickDrop extends ModuleBase {

	public ModuleQuickDrop() {
		super("Quick Drop");
	}

	public static KeyBinding quickDrop;

	@Override
	public void init(FMLInitializationEvent e) {
		quickDrop = new KeyBinding("Quick Drop", Keyboard.KEY_R, "Wynnforge");
		ClientRegistry.registerKeyBinding(quickDrop);
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
				int sl = slot + (slot < 9 ? 36 : 0);
				if (s.hasTagCompound() && s.getTagCompound().hasKey("display") && s.getTagCompound().getCompoundTag("display").hasKey("Lore")) {
					NBTTagList lore = s.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
					if (lore.getStringTagAt(0).equals(TextFormatting.DARK_GRAY + "Junk Item")) {
						short an = action_number++;
						TimedRunnables.addRunnable(() -> WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(0, sl, 1, ClickType.THROW, s, an)), an*4);
						amount += s.getCount();
					}
					PotionInfo pinfo = PotionInfo.getInfo(s);
					if(pinfo != null){
						if(mc.player.experienceLevel < pinfo.lvl || mc.player.experienceLevel - 20 >= pinfo.lvl){
							short an = action_number++;
							TimedRunnables.addRunnable(() -> WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(0, sl, 1, ClickType.THROW, s, an)), an*4);
							amount += s.getCount();
						}
					}
				}
				slot++;
			}
			if (amount > 0) {
				Wynnforge.addChatMessage("You dropped " + amount + " trash.");
			}
		}
	}

}
