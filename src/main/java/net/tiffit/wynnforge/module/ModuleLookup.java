package net.tiffit.wynnforge.module;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.utils.WFCommand;
import net.tiffit.wynnforge.utils.WFUtils;

//Unfinished, so lets not actually used
//@ModuleClass
public class ModuleLookup extends ModuleBase {

	public ModuleLookup() {
		super("Lookup");
	}

	private WFCommand lookupCommand;

	@Override
	public void loadModule() {
		ClientCommandHandler.instance.registerCommand(lookupCommand = new LookupCommand());
	}

	@Override
	public void unloadModule() {
		unloadCommand(lookupCommand);
	}

	public static class LookupCommand extends WFCommand {

		@Override
		public String getName() {
			return "lookup";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "lookup [username]";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0)
				throw new WrongUsageException(getUsage(sender), new Object[0]);
			WFUtils.runOnNewThread(() -> run(args[0]));
		}

		private void run(String player) {
			EntityPlayerSP p = Minecraft.getMinecraft().player;
			try {
				URL url = new URL("https://api.wynncraft.com/public_api.php?action=playerStats&command=" + player);
				JsonObject obj = Wynnforge.gson.fromJson(new InputStreamReader(url.openStream()), JsonObject.class);
				if (obj.has("error")) {
					Wynnforge.addChatMessage("Unknown Player '" + player + "'!");
				}else{
					String msg = "";
					msg += TextFormatting.DARK_AQUA + "Player: " + TextFormatting.AQUA + obj.get("username").getAsString() + "\n";
					if(obj.has("classes")){
						msg += TextFormatting.DARK_AQUA + "Classes: \n";
						JsonObject classes = obj.getAsJsonObject("classes");
						for(Entry<String, JsonElement> cls : classes.entrySet()){
							JsonObject clsInfo = cls.getValue().getAsJsonObject();
							msg += TextFormatting.AQUA + "     - " + cls.getKey() + " (lvl "+ clsInfo.get("level").getAsInt() +")\n";
						}
					}
					p.sendMessage(new TextComponentString(msg));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
