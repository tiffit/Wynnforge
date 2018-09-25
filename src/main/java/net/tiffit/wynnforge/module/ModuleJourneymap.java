package net.tiffit.wynnforge.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.ConfigHelper;

@ModuleClass(reqMod = "journeymap")
public class ModuleJourneymap extends ModuleBase {
	
	//Config
	private boolean change_coordinates;
	public static boolean draw_on_map;
	
	public ModuleJourneymap() {
		super("Journey Map");
	}

	private Pattern coordPattern = Pattern.compile("\\[(.*?)\\]");
	
	@SubscribeEvent
	public void onMessage(ClientChatReceivedEvent e){
		if (e.getType() == ChatType.SYSTEM && change_coordinates){
			String message = e.getMessage().getFormattedText();
			Matcher m = coordPattern.matcher(message);
			boolean found = false;
			while(m.find()){
				String text = m.group();
				String[] coords = text.substring(1, text.length() - 1).replaceAll(" ", "").split(",");
				if(coords.length != 3)continue;
				try{
					int[] vals = new int[3];
					for(int i = 0; i < 3; i++)vals[i] = Integer.valueOf(coords[i]);
					message = message.replace(text, "[x:" + vals[0] + ",y:" + vals[1] + ",z:" + vals[2] + "]");
					found = true;
				}catch(NumberFormatException ex){
					ex.printStackTrace();
					continue;
				}
			}
			if(found)e.setMessage(new TextComponentString(message).setStyle(e.getMessage().getStyle().createShallowCopy()));
		}
	}
	
	@Override
	public void loadConfig(ConfigHelper cat) {
		change_coordinates = cat.getBoolean("change_coordinates", true, null);
		draw_on_map = cat.getBoolean("draw_on_map", true, null);
	}

}
