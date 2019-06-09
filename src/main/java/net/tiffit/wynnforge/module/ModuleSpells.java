package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.tiffit.wynnforge.TimedRunnables;
import net.tiffit.wynnforge.WFNetHandler;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.PlayerClass;
import net.tiffit.wynnforge.utils.WFUtils;
import org.lwjgl.input.Keyboard;

@ModuleClass
public class ModuleSpells extends ModuleBase {

	private String spellcombotext = null;
	private long cooldown = 0;

	public ModuleSpells() {
		super("Spells HUD");
	}

	public static KeyBinding[] spell = new KeyBinding[4];

	@Override
	public void loadModule() {
		if (spell[0] == null) {
			spell[0] = new KeyBinding("Use Spell I", Keyboard.KEY_F, "Wynnforge");
			spell[1] = new KeyBinding("Use Spell II", Keyboard.KEY_V, "Wynnforge");
			spell[2] = new KeyBinding("Use Spell III", Keyboard.KEY_G, "Wynnforge");
			spell[3] = new KeyBinding("Use Spell IV", Keyboard.KEY_Y, "Wynnforge");
			for(KeyBinding kb : spell)ClientRegistry.registerKeyBinding(kb);
		}
	}

	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent e) {
		if (WFUtils.isInWorld() && e.getType() == ElementType.TEXT || e.getType() == ElementType.ALL) {
			ItemStack held = Minecraft.getMinecraft().player.getHeldItemMainhand();
			PlayerClass clss = PlayerClass.getClassFromItem(held);
			if (clss == null)
				return;
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc);
			mc.getTextureManager().bindTexture(new ResourceLocation(Wynnforge.MODID, "gui/spells.png"));
			for (int i = 0; i < 4; i++) {
				GlStateManager.pushMatrix();
				GlStateManager.enableAlpha();
				GlStateManager.translate(sr.getScaledWidth() - 48, sr.getScaledHeight() - 210, 0);
				int spelllv = PlayerClass.getAbilityLevel(mc.player.experienceLevel, i);
				int icon_x = clss.spell_index;
				int icon_y = i * 3 + spelllv - 1;
				if (spelllv == 0) {
					icon_x += 5;
					icon_y += 1;
				}
				GlStateManager.color(1, 1, 1);
				if (e.getType() == ElementType.ALL) {
					GlStateManager.scale(.75f, .75f, 0);
					Gui.drawModalRectWithCustomSizedTexture(0, i * 65, icon_x * 65, icon_y * 65, 64, 64, 1024, 1024);
				}
				if (e.getType() == ElementType.TEXT) {
					int textY = (int) (i * 65 * .75) + 15;
					String title = TextFormatting.BOLD + clss.abilities[i] + TextFormatting.RESET + " (" + spell[i].getDisplayName() + ")";
					String combo = null;
					if (i == 0)
						combo = "R-L-R";
					if (i == 1)
						combo = "R-R-R";
					if (i == 2)
						combo = "R-L-L";
					if (i == 3)
						combo = "R-R-L";
					if (clss == PlayerClass.Archer) {
						combo = combo.replaceAll("R", "A");
						combo = combo.replaceAll("L", "R");
						combo = combo.replaceAll("A", "L");
					}
					if (spellcombotext != null && spelllv != 0) {
						String known = spellcombotext.replaceAll("-\\?", "");
						if (combo.startsWith(known))
							combo = combo.replaceFirst(known, TextFormatting.GOLD + known + TextFormatting.WHITE);
					}
					mc.fontRenderer.drawStringWithShadow(title, -mc.fontRenderer.getStringWidth(title), textY, 0xffffffff);
					mc.fontRenderer.drawStringWithShadow(TextFormatting.GRAY + combo, -mc.fontRenderer.getStringWidth(combo), textY + 10, 0xffffffff);
					if (spelllv == 0) {
						String locked = "Locked!";
						mc.fontRenderer.drawStringWithShadow(TextFormatting.DARK_RED + locked, -mc.fontRenderer.getStringWidth(locked), textY + 20, 0xffffffff);
					}
				}
				GlStateManager.popMatrix();

			}
		}
	}

	@SubscribeEvent
	public void onKeyPress(KeyInputEvent e) {
		if (!WFUtils.isInWorld())
			return;
		ItemStack held = Minecraft.getMinecraft().player.getHeldItemMainhand();
		PlayerClass clss = PlayerClass.getClassFromItem(held);
		String combo = null;
		if (spell[0].isPressed())
			combo = "RLR";
		else if (spell[1].isPressed())
			combo = "RRR";
		else if (spell[2].isPressed())
			combo = "RLL";
		else if (spell[3].isPressed())
			combo = "RRL";
		if (System.currentTimeMillis() > cooldown && combo != null && clss != null) {
			if (clss == PlayerClass.Archer) {
				combo = combo.replaceAll("R", "A");
				combo = combo.replaceAll("L", "R");
				combo = combo.replaceAll("A", "L");
			}
			cooldown = System.currentTimeMillis() + 750;
			int delay = 0;
			String done = spellcombotext == null ? "" : spellcombotext.replaceAll("-\\?", "").replaceAll("-", "");
			for (int i = 0; i < 3; i++) {
				char key = combo.charAt(i);
				if (done.length() > i) {
					char doneKey = done.charAt(i);
					if (key == doneKey)
						continue;
					else {
						Wynnforge.addChatMessage("You already have another spell combo active!");
						return;
					}
				}
				if (key == 'R') {
					TimedRunnables.addRunnable(() -> WFNetHandler.INSTANCE.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)), delay += 3);
				} else if (key == 'L') {
					TimedRunnables.addRunnable(() -> WFNetHandler.INSTANCE.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND)), delay += 3);
				}

			}
		}
	}

	@SubscribeEvent
	public void actionListener(ClientChatReceivedEvent e) {
		if (e.getType() == ChatType.GAME_INFO) {
			String allText = TextFormatting.getTextWithoutFormattingCodes(e.getMessage().getUnformattedText());
			String[] sections = allText.split("    ");
			if (sections.length == 3) {
				String[] combokeys = sections[1].split("-");
				if (combokeys.length == 3) {
					spellcombotext = sections[1];
					return;
				}
			}
			spellcombotext = null;
		}
	}

}
