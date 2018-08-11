package net.tiffit.wynnforge.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.tiffit.wynnforge.TimedRunnables;
import net.tiffit.wynnforge.WFNetHandler;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.data.FriendsManager;
import net.tiffit.wynnforge.gui.compass.GuiCompass;
import net.tiffit.wynnforge.module.ModuleQuickParty.QuickPartyCommand;
import net.tiffit.wynnforge.utils.PotionInfo;
import net.tiffit.wynnforge.utils.WFUtils;

public class ModuleUsefulCompass extends ModuleBase {

	public ModuleUsefulCompass() {
		super("Useful Compass");
	}

	public static KeyBinding openGui;

	@Override
	public void init(FMLInitializationEvent e) {
		openGui = new KeyBinding("Useful Compass", Keyboard.KEY_C, "Wynnforge");
		ClientRegistry.registerKeyBinding(openGui);
		
		ClientCommandHandler.instance.registerCommand(new CompassCommand());
	}

	@SubscribeEvent
	public void onOpenGui(KeyInputEvent e) {
		if (openGui.isPressed() && WFUtils.isInWorld()) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.displayGuiScreen(new GuiCompass());
		}
	}
	
	public static class CompassCommand extends CommandBase implements IClientCommand{

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
			if(args.length != 2)throw new WrongUsageException(getUsage(sender), new Object[0]);
			int x = 0;
			int z = 0;
			try {
				x = Integer.valueOf(args[0]);
				z = Integer.valueOf(args[1]);
			}catch(NumberFormatException e) {
				throw new WrongUsageException(getUsage(sender), new Object[0]);
			}
			Minecraft.getMinecraft().world.setSpawnPoint(new BlockPos(x, 64, z));
			Wynnforge.addChatMessage("Compass now points towards " + TextFormatting.DARK_GREEN + String.format("(%s, %s)", x, z));
		}
		
		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

		@Override
		public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
			return false;
		}
		
	}

}
