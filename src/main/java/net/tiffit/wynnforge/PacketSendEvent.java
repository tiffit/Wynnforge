package net.tiffit.wynnforge;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PacketSendEvent extends Event {

	private Packet<?> packet;
	
	public PacketSendEvent(Packet<?> packet) {
		this.packet = packet;
	}
	
	public Packet<?> getPacket(){
		return packet;
	}
	
	
	@Override
	public boolean isCancelable() {
		return true;
	}
	
}
