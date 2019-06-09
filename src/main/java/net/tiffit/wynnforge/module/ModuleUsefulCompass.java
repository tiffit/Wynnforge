package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.gui.compass.GuiCompass;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.WFCommand;
import net.tiffit.wynnforge.utils.WFUtils;
import org.lwjgl.input.Keyboard;

@ModuleClass
public class ModuleUsefulCompass extends ModuleBase {

	public ModuleUsefulCompass() {
		super("Useful Compass");
	}

	public static KeyBinding openGui;
	private WFCommand compassCommand;

	@Override
	public void loadModule() {
		if (openGui == null) {
			openGui = new KeyBinding("Useful Compass", Keyboard.KEY_C, "Wynnforge");
			ClientRegistry.registerKeyBinding(openGui);
		}

		ClientCommandHandler.instance.registerCommand(compassCommand = new CompassCommand());
	}

	@Override
	public void unloadModule() {
		unloadCommand(compassCommand);
	}

	@SubscribeEvent
	public void onOpenGui(KeyInputEvent e) {
		if (openGui.isPressed() && WFUtils.isInWorld()) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.displayGuiScreen(new GuiCompass());
		}
	}

	public static class CompassCommand extends WFCommand {

		@Override
		public String getName() {
			return "compass";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/compass [x] [z]";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length != 2)
				throw new WrongUsageException(getUsage(sender), new Object[0]);
			int x = 0;
			int z = 0;
			try {
				x = Integer.valueOf(args[0]);
				z = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
				throw new WrongUsageException(getUsage(sender), new Object[0]);
			}
			Minecraft.getMinecraft().world.setSpawnPoint(new BlockPos(x, 64, z));
			Wynnforge.addChatMessage("Compass now points towards " + TextFormatting.DARK_GREEN + String.format("(%s, %s)", x, z));
		}

	}

}
