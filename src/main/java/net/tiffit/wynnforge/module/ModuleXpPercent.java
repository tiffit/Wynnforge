package net.tiffit.wynnforge.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.data.LocalData;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;

@ModuleClass
public class ModuleXpPercent extends ModuleBase {

	public ModuleXpPercent() {
		super("Exact Xp");
	}

	private static List<Integer> LEVEL_XP = new ArrayList<Integer>();

	private static DecimalFormat format = new DecimalFormat("#,###");

	public static KeyBinding xpMode;
	public static int mode = 0;

	private static float lastXp = -1;
	private static int lastXpLevel = -1;
	private static float lastXpOrigin = -1;
	private static long lastXpTime = 0;
	private static boolean trackingXp = false;

	@Override
	public void loadModule() {
		mode = LocalData.getTag(this).getInteger("mode");

		IResourceManager man = Minecraft.getMinecraft().getResourceManager();
		try {
			LEVEL_XP.clear();
			InputStream is = man.getResource(new ResourceLocation(Wynnforge.MODID, "xpvalues.txt")).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				LEVEL_XP.add(Integer.valueOf(line));
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (xpMode == null) {
			xpMode = new KeyBinding("Change XP Mode", Keyboard.KEY_NUMPAD1, "Wynnforge");
			ClientRegistry.registerKeyBinding(xpMode);
		}
	}

	@SubscribeEvent
	public void onKeyPress(KeyInputEvent e) {
		if (xpMode.isPressed()) {
			mode++;
			if (mode >= 3)
				mode = 0;
			LocalData.getTag(this).setInteger("mode", mode);
			LocalData.save();
		}
	}

	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent e) {
		if (e.getType() == ElementType.TEXT) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.player.experienceLevel > 0) {
				ScaledResolution sr = new ScaledResolution(mc);
				int levelXp = LEVEL_XP.get(mc.player.experienceLevel);
				String xp = "";
				if (mode == 0)
					xp = "XP: " + format.format((int) (mc.player.experience * levelXp)) + "/" + format.format(levelXp);
				if (mode == 1)
					xp = "XP: " + ItemStack.DECIMALFORMAT.format(mc.player.experience * 100) + "%";
				if (mode == 2)
					xp = format.format(levelXp - (int) (mc.player.experience * levelXp)) + "XP Remaining";
				if (lastXpLevel == -1) {
					lastXpLevel = mc.player.experienceLevel;
					lastXp = lastXpOrigin = mc.player.experience;
				}
				if (lastXp != mc.player.experience) {
					float xpcache = lastXp;
					lastXp = mc.player.experience;
					lastXpTime = Minecraft.getSystemTime();
					if (!trackingXp) {
						lastXpOrigin = xpcache;
						trackingXp = true;
					}
				}
				if (lastXpLevel != mc.player.experienceLevel || Minecraft.getSystemTime() - lastXpTime > 5000) {
					trackingXp = false;
					lastXpLevel = mc.player.experienceLevel;
				}
				if (trackingXp) {
					xp += TextFormatting.DARK_GREEN + " [+" + format.format((int) ((mc.player.experience * levelXp) - (levelXp * lastXpOrigin))) + "]";
				}
				if (!xp.isEmpty())
					mc.fontRenderer.drawStringWithShadow(xp, sr.getScaledWidth() / 2 - 9, sr.getScaledHeight() - 47, 8453920);
			}
		}
	}

}
