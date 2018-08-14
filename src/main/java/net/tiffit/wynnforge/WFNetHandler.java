package net.tiffit.wynnforge;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class WFNetHandler extends NetHandlerPlayClient {

	public static WFNetHandler INSTANCE;
	private EventBus eb;
	
	public WFNetHandler(Minecraft mc, GuiScreen gui, NetworkManager manager, GameProfile profile) {
		super(mc, gui, manager, profile);
		INSTANCE = this;
		eb = MinecraftForge.EVENT_BUS;
	}
	
	@Override
	public void handleSoundEffect(SPacketSoundEffect p) {
		boolean canceled = eb.post(new PacketRecieveEvent(p, true));
		if(!canceled)super.handleSoundEffect(p);
		eb.post(new PacketRecieveEvent(p, false));
	}
	
	@Override
	public void handleSpawnPlayer(SPacketSpawnPlayer p) {
		boolean canceled = eb.post(new PacketRecieveEvent(p, true));
		if(!canceled)super.handleSpawnPlayer(p);
		eb.post(new PacketRecieveEvent(p, false));
	}
	
	@Override
	public void handleOpenWindow(SPacketOpenWindow p) {
		boolean canceled = eb.post(new PacketRecieveEvent(p, true));
		if(!canceled)super.handleOpenWindow(p);
		eb.post(new PacketRecieveEvent(p, false));
	}
	
	@Override
	public void handleWindowItems(SPacketWindowItems p) {
		boolean canceled = eb.post(new PacketRecieveEvent(p, true));
		if(!canceled)super.handleWindowItems(p);
		eb.post(new PacketRecieveEvent(p, false));
	}
	
	@Override
	public void handleChat(SPacketChat packetIn) {
		super.handleChat(packetIn);
	}
	
	@Override
	public void handlePlayerListItem(SPacketPlayerListItem p) {
		boolean canceled = eb.post(new PacketRecieveEvent(p, true));
		if(!canceled)super.handlePlayerListItem(p);
		eb.post(new PacketRecieveEvent(p, false));
	}
	
	@Override
	public void sendPacket(Packet<?> p) {
		boolean canceled = eb.post(new PacketSendEvent(p));
		if(!canceled)super.sendPacket(p);
	}
	
}
