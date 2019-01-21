package net.tiffit.wynnforge.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import net.tiffit.wynnforge.data.FriendsManager;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.WFCommand;

@ModuleClass
public class ModuleQuickParty extends ModuleBase {

	public ModuleQuickParty() {
		super("Quick Party");
	}
	
	private WFCommand qpCommand;

	@Override
	public void loadModule() {
		ClientCommandHandler.instance.registerCommand(qpCommand = new QuickPartyCommand());
	}
	
	@Override
	public void unloadModule() {
		unloadCommand(qpCommand);
	}
	
	public static class QuickPartyCommand extends WFCommand{

		@Override
		public String getName() {
			return "quickparty";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "quickparty [usernames]";
		}
		
		@Override
		public List<String> getAliases() {
			return Arrays.asList("qp");
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			Minecraft mc = Minecraft.getMinecraft();
			mc.player.sendChatMessage("/party create");
			for(String arg : args) {
				mc.player.sendChatMessage("/party invite " + arg);
			}
		}
		
		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
			ArrayList<String> possible = new ArrayList<String>();
			for(String str : FriendsManager.FRIEND_LIST){
				if(str.startsWith(args[args.length - 1]))possible.add(str);
			}
			return possible;
		}
		
	}

}
