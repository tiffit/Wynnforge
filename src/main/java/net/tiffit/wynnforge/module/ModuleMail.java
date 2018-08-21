package net.tiffit.wynnforge.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.data.WynnforgeServerConnector;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.WFCommand;
import net.tiffit.wynnforge.utils.WFUtils;

@ModuleClass
public class ModuleMail extends ModuleBase {

	public ModuleMail() {
		super("Mail");
	}
	
	private WFCommand mailCommand, getMailCommand, clearMailCommand;

	@Override
	public void loadModule() {
		ClientCommandHandler.instance.registerCommand(mailCommand = new MailCommand());
		ClientCommandHandler.instance.registerCommand(getMailCommand = new GetMailCommand());
		ClientCommandHandler.instance.registerCommand(clearMailCommand = new ClearMailCommand());
	}
	
	@Override
	public void unloadModule() {
		unloadCommand(mailCommand);
		unloadCommand(getMailCommand);
		unloadCommand(clearMailCommand);
	}
	
	private static boolean serverJoinMsg = false;
	
	@SubscribeEvent
	public void serverJoin(EntityJoinWorldEvent e){
		if(!serverJoinMsg && e.getEntity() instanceof EntityPlayerSP){
			serverJoinMsg = true;
			WFUtils.runOnNewThread(() -> runOnJoin());
		}
	}
	
	private void runOnJoin(){
		JsonObject obj = WynnforgeServerConnector.sendGet("api/getmail?");
		if(obj != null){
			JsonArray mails = obj.get("mails").getAsJsonArray();
			if(mails.size() > 0){
				Wynnforge.addChatMessage("You have " + mails.size() + " mail. Read them using " + TextFormatting.DARK_GREEN + "/getmail");
				return;
			}
		}else{
			Wynnforge.addChatMessage("Unable to connect to the Wynnforge server!");
		}
	}
	
	public static class MailCommand extends WFCommand{

		@Override
		public String getName() {
			return "mail";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/mail [player] [message]";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if(args.length < 2)throw new WrongUsageException(getUsage(sender), new Object[0]);
			if(WynnforgeServerConnector.isConnected()){
				WFUtils.runOnNewThread(() -> run(args));
			}else{
				Wynnforge.addChatMessage("Unable to connect to the Wynnforge server!");
			}
		}
		
		private void run(String[] args){
			JsonObject obj = new JsonObject();
			String total = "";
			for(int i = 1; i < args.length; i++)total += args[i] + " ";
			obj.addProperty("message", total.trim());
			obj.addProperty("to", args[0]);
			int response = WynnforgeServerConnector.sendPost("api/sendmail", obj);
			if(response == 200)Wynnforge.addChatMessage("Sent!");
			if(response == 400)Wynnforge.addChatMessage("Unknown player!");
			if(response == 403 || response == 404)Wynnforge.addChatMessage("An error occurred! Please restart your game!");
		}
	}
	
	public static class GetMailCommand extends WFCommand{

		@Override
		public String getName() {
			return "getmail";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/getmail";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if(WynnforgeServerConnector.isConnected()){
				WFUtils.runOnNewThread(() -> run(sender));
			}else{
				Wynnforge.addChatMessage("Unable to connect to the Wynnforge server!");
			}
		}
		
		private void run(ICommandSender sender){
			JsonObject obj = WynnforgeServerConnector.sendGet("api/getmail?");
			if(obj != null){
				JsonArray mails = obj.get("mails").getAsJsonArray();
				if(mails.size() == 0){
					Wynnforge.addChatMessage("No mail found!");
					return;
				}
				Wynnforge.addChatMessage("Retrieved " + mails.size() + " messages:");
				for(int i = 0; i < mails.size(); i++){
					JsonObject mail = mails.get(i).getAsJsonObject();
					String messageTemplate = TextFormatting.DARK_AQUA + "%s) " + TextFormatting.DARK_GRAY + "%s: " + TextFormatting.GRAY + "%s";
					sender.sendMessage(new TextComponentString(String.format(messageTemplate, i+1, mail.get("from").getAsString(), mail.get("message").getAsString())));
				}
				sender.sendMessage(new TextComponentString("\n" + TextFormatting.DARK_RED + "/clearmail"+ TextFormatting.GRAY +" to clear all of your mail!"));
			}else{
				Wynnforge.addChatMessage("Unable to connect to the Wynnforge server!");
			}
		}
	}
	
	public static class ClearMailCommand extends WFCommand{

		@Override
		public String getName() {
			return "clearmail";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/clearmail";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if(WynnforgeServerConnector.isConnected()){
				WFUtils.runOnNewThread(() -> run());
			}else{
				Wynnforge.addChatMessage("Unable to connect to the Wynnforge server!");
			}
		}
		
		private void run(){
			int response = WynnforgeServerConnector.sendPost("api/clearmail", new JsonObject());
			if(response == 200)Wynnforge.addChatMessage("Cleared!");
			if(response == 403 || response == 404 || response == 400)Wynnforge.addChatMessage("An error occurred! Please restart your game!");
		}
	}

}
