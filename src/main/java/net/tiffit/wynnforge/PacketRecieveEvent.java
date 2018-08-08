package net.tiffit.wynnforge;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PacketRecieveEvent extends Event {

	private Packet<?> packet;
	public final boolean pre;
	
	public PacketRecieveEvent(Packet<?> packet, boolean pre) {
		this.packet = packet;
		this.pre = pre;
	}
	
	public Packet<?> getPacket(){
		return packet;
	}
	
	
	@Override
	public boolean isCancelable() {
		return pre;
	}
	
}
