package net.tiffit.wynnforge.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.tiffit.wynnforge.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigGuiFactory implements IModGuiFactory {


	@Override
	public void initialize(Minecraft mc) {
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parent) {
		List<IConfigElement> elements = new ArrayList<IConfigElement>();
		Configuration conf = ConfigManager.getConfig();
		Set<String> catNames = conf.getCategoryNames();
		for(String catName : catNames){
			ConfigElement element = new ConfigElement(conf.getCategory(catName));
			elements.add(element);
		}
		return new WynnforgeConfigGui(parent, elements);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}


}
