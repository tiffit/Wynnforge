package net.tiffit.wynnforge.gui.compass;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiWorldSelection;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory;

public class GuiCompass extends GuiScreen {

	private GuiListCompass list;
	private GuiTextField search;
	private String prevSearch = "";
	
	private GuiButton selectTerritory;
	private GuiButton changeOrder;

	public GuiCompass() {
	}

	public void initGui() {
		this.list = new GuiListCompass(this, mc, width, height, 32, height - 64, 36);
		search = new GuiTextField(0, mc.fontRenderer, width / 2 - 154, height - 52, 150, 20);
		(selectTerritory = new GuiButton(0, width / 2 - 125, height - 27, 250, 20, "Select Territory")).enabled = false;
		addButton(selectTerritory);
		addButton(changeOrder = new GuiButton(1, width / 2, height - 52, 150, 20, "Order: Default"));
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		list.handleMouseInput();
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		if(!prevSearch.equals(search.getText())) {
			prevSearch = search.getText();
			list.refreshEntries(prevSearch);
		}
		selectTerritory.enabled = list.select != -1;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0) {
			if(list.select != -1) {
				list.selectTerritory(((GuiListCompassEntry)list.getListEntry(list.select)).territory);
			}
		}else if(button.id == 1) {
			changeOrder.displayString = "Order: " + list.changeOrder();
			list.refreshEntries(search.getText());
		}
		
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		list.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRenderer, "Select Territory", this.width / 2, 20, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
		search.drawTextBox();
		if(!search.isFocused() && search.getText().isEmpty())fontRenderer.drawString("Search", search.x + 4, search.y + 6, 0xff777777);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		search.textboxKeyTyped(typedChar, keyCode);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		list.mouseClicked(mouseX, mouseY, mouseButton);
		search.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		list.mouseReleased(mouseX, mouseY, state);
	}

}
