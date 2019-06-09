package net.tiffit.wynnforge.gui.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.tiffit.wynnforge.Wynnforge;

import java.util.List;

public class WynnforgeConfigGui extends GuiConfig {

	public WynnforgeConfigGui(GuiScreen parent, List<IConfigElement> configElements) {
		super(parent, configElements, Wynnforge.MODID, false, false, "Wynnforge Configuration Editor");	
	}

	
	
}
