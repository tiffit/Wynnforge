package net.tiffit.wynnforge.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.text.TextFormatting;
import net.tiffit.wynnforge.WFNetHandler;

public class GuiStats extends GuiScreen {

	private SPacketWindowItems packet;
	private EntityPlayer player;

	private SkillInfo[] skills = new SkillInfo[5];
	private ProfessionInfo[] professions = new ProfessionInfo[12];
	private List<ItemStack> items;
	
	public GuiStats(SPacketWindowItems packet, EntityPlayer player) {
		this.packet = packet;
		this.player = player;
		items = packet.getItemStacks();
		for(int i = 0; i < 5; i++){
			skills[i] = new SkillInfo(items, 9 + i);
		}
		try{
			for(int i = 0; i < 3; i++)professions[i] = new ProfessionInfo(items, 15 + i);
			for(int i = 0; i < 3; i++)professions[i+3] = new ProfessionInfo(items, 15 + i+9);
			for(int i = 0; i < 3; i++)professions[i+6] = new ProfessionInfo(items, 15 + i+18);
			for(int i = 0; i < 3; i++)professions[i+9] = new ProfessionInfo(items, 15 + i+27);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initGui() {
		buttonList.clear();
		for(int i = 0 ; i < skills.length; i++){
			int dx = width/2 - 50;
			int dy = this.height/2 + (i * 75) - (5*75)/2;
			buttonList.add(new GuiButton(i*2, dx - 26, dy + 40, 25, 20, "+1"));
			buttonList.add(new GuiButton(i*2+1, dx + 1, dy + 40, 25, 20, "+5"));
		}
		int infox = width/3*2;
		buttonList.add(new ItemStackButton(items, 6, infox, 45, false));
		buttonList.add(new ItemStackButton(items, 7, infox + 25, 45, false));
		buttonList.add(new ItemStackButton(items, 8, infox + 25*2, 45, false));
		
		buttonList.add(new ItemStackButton(items, 37, infox, height-90, true));
		buttonList.add(new ItemStackButton(items, 38, infox + 25, height-90, true));
		buttonList.add(new ItemStackButton(items, 39, infox + 25*2, height-90, true));
		if(items.get(20).getItem() == Item.getItemFromBlock(Blocks.CHEST)){
			buttonList.add(new ItemStackButton(items, 20, infox + 25, height-65, true));
		}
		buttonList.add(new ItemStackButton(items, 2, width/2 - 65, 13, true));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button instanceof ItemStackButton){
			ItemStackButton isb = (ItemStackButton) button;
			if(isb.clickable){
				Container con = mc.player.openContainer;
				ItemStack click = con.slotClick(isb.id, 0, ClickType.PICKUP, player);
				short nextId = con.getNextTransactionID(player.inventory);
				WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(packet.getWindowId(), isb.id, 0, ClickType.PICKUP, click, nextId));
			}
		}else{
			int index = button.id/2;
			int type = button.id%2;
			int slot = skills[index].index;
			Container con = mc.player.openContainer;
			ItemStack click = con.slotClick(slot, type, ClickType.PICKUP, player);
			short nextId = con.getNextTransactionID(player.inventory);
			WFNetHandler.INSTANCE.sendPacket(new CPacketClickWindow(packet.getWindowId(), slot, type, ClickType.PICKUP, click, nextId));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.pushMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.popMatrix();
		drawVerticalLine(width/2, 40, height-40, 0xffcccccc);
		
		for(int i = 0 ; i < skills.length; i++){
			SkillInfo skill = skills[i];
			int dx = width/2 - 50;
			int dy = this.height/2 + (i * 75) - (5*75)/2;
			drawCenteredString(fontRenderer, skill.skill, dx, dy, 0xffffffff);
			drawCenteredString(fontRenderer, TextFormatting.GRAY.toString() + skill.current + " points", dx, dy + 10, 0xffffffff);
			drawCenteredString(fontRenderer, TextFormatting.GRAY.toString() + "Now: " + skill.percent_now + "%", dx, dy + 20, 0xffffffff);
			drawCenteredString(fontRenderer, TextFormatting.DARK_GRAY.toString() + "Next: " + skill.percent_next + "%", dx, dy + 30, 0xffffffff);
			
			String[] descLines = skill.description.split("\n");
			for(int l = 0; l < descLines.length; l++){
				String line = descLines[l];
				drawString(fontRenderer, line, dx - 50 - fontRenderer.getStringWidth(line), dy + 10 + 10*l, 0xffffffff);
			}
		}
		for(int i = 0 ; i < professions.length; i++){
			ProfessionInfo prof = professions[i];
			int dx = width/2 + 57 + (i/4)*(width/2/3);
			int dy = this.height/2 + (i%4 * 75) - (4*75)/2;
			drawCenteredString(fontRenderer, TextFormatting.GOLD + TextFormatting.BOLD.toString() + prof.profession.trim(), dx, dy, 0xffffffff);
			drawCenteredString(fontRenderer, TextFormatting.GRAY + prof.description.replace("Can ", "").replace("make ", " ").replace("gather", " ").trim(), dx, dy + 10, 0xffffffff);
			drawRect(dx-45, dy + 22, dx+45, dy+35, 0xffffffff);
			drawRect(dx-45 + 1, dy + 22 + 1, dx+45 - 1, dy+35 - 1, 0xff444444);
			int barleft = dx+(int)(90*(prof.percent/100f)) - 45 + 1;
			drawRect(dx-45 + 1, dy + 22 + 1, barleft, dy+35 - 1, 0xff00cc00);
			drawString(fontRenderer, prof.level + "", dx-45, dy+37, 0xffffffff);
			drawString(fontRenderer, (prof.level+1) + "", dx+45 - fontRenderer.getStringWidth((prof.level+1) + ""), dy+37, 0xffffffff);
			drawCenteredString(fontRenderer, prof.percent + "%", dx, dy+37, 0xffaaaaaa);
		}
		ContainerChest container =  (ContainerChest)mc.player.openContainer;
		
		drawCenteredString(fontRenderer, TextFormatting.DARK_AQUA.toString() + TextFormatting.BOLD + "Info", width/3*2 + 35, 35, 0xffffffff);
		drawCenteredString(fontRenderer, TextFormatting.DARK_AQUA.toString() + TextFormatting.BOLD + "Misc", width/3*2 + 35, height-100, 0xffffffff);
		drawCenteredString(fontRenderer, container.getLowerChestInventory().getName(), width/2 - 125, 20, 0xffffffff);
		for(GuiButton button : buttonList){
			if(button instanceof ItemStackButton){
				ItemStackButton isb = (ItemStackButton) button;
				if(isb.isMouseOver()){
					renderToolTip(isb.stack, mouseX, mouseY);
				}
			}
		}

	}

	private class SkillInfo{
		final String skill;
		final int current;
		final double percent_now, percent_next;
		final String description;
		final int index;
		
		SkillInfo(List<ItemStack> items, int index){
			this.index = index;
			ItemStack stack = items.get(index);

			
			String[] itemname = stack.getDisplayName().split(" ");
			skill = itemname[2] + " " + TextFormatting.BOLD.toString() + itemname[3];
			
			NBTTagList lore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
			
			String pointLine = TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(3)).trim();
			current = Integer.valueOf(pointLine.split(" ")[0]);
			
			String percentLine = TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(2)).trim();
			String[] percentLineWords = percentLine.split(" ");
			percent_now = Double.valueOf(percentLineWords[0].replace("%", ""));
			percent_next = Double.valueOf(percentLineWords[percentLineWords.length - 1].replace("%", ""));
			
			String description = "";
			for(int i = 5; i < lore.tagCount(); i++){
				String text = lore.getStringTagAt(i);
				if(text.trim().isEmpty())break;
				description += text + "\n";
			}
			this.description = description;
		}
	}
	
	private class ProfessionInfo{
		final String profession;
		final int percent, level;
		final String description;
		
		ProfessionInfo(List<ItemStack> items, int index){
			ItemStack stack = items.get(index);

			String[] itemname = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()).split(" ");
			profession = itemname[0] + " " + itemname[1];
			
			NBTTagList lore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
			description = TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(0));
			
			level = Integer.valueOf(TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(2)).substring(7));
			percent = Integer.valueOf(TextFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(3)).substring(4).replace("%", ""));
		}
	}
	
	private class ItemStackButton extends GuiButton {

		private ItemStack stack;
		private boolean clickable;

		public ItemStackButton(List<ItemStack> items, int index, int x, int y, boolean clickable) {
			super(index, x, y, 20, 20, "");
			this.stack = items.get(index);
			this.clickable = clickable;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			super.drawButton(mc, mouseX, mouseY, partialTicks);
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			RenderHelper.enableGUIStandardItemLighting();
			mc.getRenderItem().renderItemIntoGUI(stack, x + 2, y + 2);
			GlStateManager.popMatrix();
		}

	}
	
}
