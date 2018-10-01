package net.tiffit.wynnforge.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.tiffit.wynnforge.data.WynnforgeServerConnector;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.WFCommand;

@ModuleClass //Only for testing purposes
public class ModuleWFServer extends ModuleBase {

	public ModuleWFServer() {
		super("WF Server Executor");
	}

	private WFCommand command;

	@Override
	public void loadModule() {
		ClientCommandHandler.instance.registerCommand(command = new ServerCommand());
	}
	
	@Override
	public void unloadModule() {
		unloadCommand(command);
	}
	
	public static class ServerCommand extends WFCommand{

		@Override
		public String getName() {
			return "wfserver";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "wfserver [command]";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if(args.length == 0)throw new WrongUsageException(getUsage(sender), new Object[0]);
			JsonObject obj = WynnforgeServerConnector.sendGet("api/execute?arg=" + args[0]);
			JsonArray arr = obj.getAsJsonArray("msgs");
			for(int i = 0; i < arr.size(); i++){
				sender.sendMessage(new TextComponentString(arr.get(i).getAsString()));
			}
		}
		
	}

}
