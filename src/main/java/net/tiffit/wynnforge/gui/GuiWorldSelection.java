package net.tiffit.wynnforge.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.text.TextFormatting;
import net.tiffit.wynnforge.WFNetHandler;
import net.tiffit.wynnforge.data.FriendsManager;
import net.tiffit.wynnforge.wynnapi.PlayerList;

public class GuiWorldSelection extends GuiScreen {

	private SPacketWindowItems packet;
	private EntityPlayer player;

	private static int prevServer = -1;
	private boolean prevWorldThisInstance = false;

	private LinkedHashMap<Integer, WorldInfo> worlds = new LinkedHashMap<Integer, WorldInfo>();
	private LinkedHashMap<Integer, List<String>> friends = new LinkedHashMap<Integer, List<String>>();

	public GuiWorldSelection(SPacketWindowItems packet, EntityPlayer player) {
		this.packet = packet;
		this.player = player;
		List<ItemStack> items = packet.getItemStacks();
		for (int i = 0; i < items.size(); i++) {
			ItemStack stack = items.get(i);
			if (i % 9 < 7 && !stack.isEmpty() && i < 36) {
				WorldInfo info = new WorldInfo(stack);
				worlds.put(i, info);
				String[] players = PlayerList.getPlayersForWorld("WC" + info.world);
				List<String> friends = new ArrayList<String>();
				if (players != null) {
					for (String s : players) {
						if (FriendsManager.isFriend(s))
							friends.add(s);
					}
				}
				this.friends.put(info.world, friends);
			}
		}
	}

	@Override
	public void initGui() {
		Iterator<Entry<Integer, WorldInfo>> entries = worlds.entrySet().iterator();
		for (int i = 0; i < worlds.size(); i++) {
			Entry<Integer, WorldInfo> entry = entries.next();
			buttonList.add(new WorldButton(width / 2 - 100, height / 2 + i * 21 - worlds.size() * 20 / 2, 200, 20, entry.getValue(), entry.getKey()));
		}
		List<ItemStack> allItems = packet.getItemStacks();
		buttonList.add(new CategoryButton(width/2 - 25, allItems.get(35), 35));
		buttonList.add(new CategoryButton(width/2, allItems.get(35 + 9), 35 + 9));
		buttonList.add(new CategoryButton(width/2 + 25, allItems.get(35 + 9*2), 35 + 9*2));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof WorldButton) {
			WorldButton wb = (WorldButton) button;
			Container con = mc.player.openContainer;
			ItemStack click = con.slotClick(wb.id, 0, ClickType.PICKUP, player);
			short nextId = con.getNextTransactionID(player.inventory);
			prevWorldThisInstance = true;
			prevServer = wb.info.world;
			WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(packet.getWindowId(), wb.id, 0, ClickType.PICKUP, click, nextId));
		}else if(button instanceof CategoryButton){
			Container con = mc.player.openContainer;
			ItemStack click = con.slotClick(button.id, 0, ClickType.PICKUP, player);
			short nextId = con.getNextTransactionID(player.inventory);
			WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(packet.getWindowId(), button.id, 0, ClickType.PICKUP, click, nextId));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "World Selection", width / 2, height / 2 - worlds.size() * 20 / 2 - 12, 0xffffffff);
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (GuiButton button : buttonList) {
			if (button instanceof WorldButton && button.isMouseOver()) {
				WorldButton b = (WorldButton) button;
				List<String> friends = this.friends.get(b.info.world);
				if (!friends.isEmpty()) {
					List<String> display = new ArrayList<String>();
					display.add("Friends:");
					for (String f : friends)
						display.add(TextFormatting.GRAY + " - " + f);
					drawHoveringText(display, mouseX, mouseY);
				}
			}
		}
	}

	private static class WorldInfo {
		public int world;
		public int players;
		public int lag;

		public WorldInfo(ItemStack stack) {
			world = stack.getCount();
			NBTTagList lore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
			players = Integer.valueOf(TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(2)).replace("Players: ", "").split("/")[0]);
			lag = Integer.valueOf(TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(4)).replace("Lag: ", "").replace("%", ""));
		}
	}

	private class WorldButton extends GuiButton {

		private WorldInfo info;

		public WorldButton(int x, int y, int width, int height, WorldInfo info, int index) {
			super(index, x, y, width, height, "");
			this.info = info;
			enabled = info.players != 70;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			super.drawButton(mc, mouseX, mouseY, partialTicks);
			FontRenderer fr = mc.fontRenderer;
			drawString(fr, "World " + info.world, x + 4, y + 6, 14737632);
			int color = 0xff03b72a;
			if (info.players >= 55)
				color = 0xffd3a421;
			if (info.players == 70)
				color = 0xffbc0707;
			drawCenteredString(fr, info.players + "/70", x + width / 2, y + 6, color);

			color = 0xff03b72a;
			if (info.lag > 0)
				color = 0xffd3a421;
			if (info.lag >= 20)
				color = 0xffbc0707;
			drawString(fr, "Lag: " + info.lag + "%", x + width - fr.getStringWidth("Lag: " + info.lag + "%") - 4, y + 6, color);

			List<String> friends = GuiWorldSelection.this.friends.get(info.world);
			if (!friends.isEmpty()) {
				drawString(fr, friends.size() + " Friend" + (friends.size() == 1 ? "" : "s"), x + width + 4, y + 6, 0xff00dd00);
			}
			if (prevServer == info.world) {
				String text = "Prev World";
				if (prevWorldThisInstance)
					text = "Connecting";
				drawString(fr, text, x - 60, y + 6, 0xff55aaff);
			}
		}

	}
	
	private class CategoryButton extends GuiButton {

		private ItemStack stack;

		public CategoryButton(int x, ItemStack stack, int index) {
			super(index, x - 10, 5, 20, 20, "");
			this.stack = stack;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			super.drawButton(mc, mouseX, mouseY, partialTicks);
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			mc.getRenderItem().renderItemIntoGUI(stack, x + 2, y + 2);
			if(hovered){
				drawCenteredString(fontRenderer, stack.getDisplayName(), GuiWorldSelection.this.width/2, 30, 0xffffffff);
			}
			GlStateManager.popMatrix();
		}

	}

}
