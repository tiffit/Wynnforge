package net.tiffit.wynnforge.utils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.IClientCommand;

public abstract class WFCommand extends CommandBase implements IClientCommand {

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		return false;
	}

}
