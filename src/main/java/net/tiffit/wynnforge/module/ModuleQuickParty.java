package net.tiffit.wynnforge.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.tiffit.wynnforge.data.FriendsManager;

public class ModuleQuickParty extends ModuleBase {

	public ModuleQuickParty() {
		super("Quick Party");
	}

	@Override
	public void init(FMLInitializationEvent e) {
		ClientCommandHandler.instance.registerCommand(new QuickPartyCommand());
	}
	
	public static class QuickPartyCommand extends CommandBase implements IClientCommand{

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
		public int getRequiredPermissionLevel() {
			return 0;
		}

		@Override
		public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
			return false;
		}
		
		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
			return new ArrayList<String>(FriendsManager.FRIEND_LIST);
		}
		
	}

}
