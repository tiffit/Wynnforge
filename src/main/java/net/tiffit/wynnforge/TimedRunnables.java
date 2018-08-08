package net.tiffit.wynnforge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod.EventBusSubscriber
public class TimedRunnables {

	private static LinkedHashMap<Runnable, Integer> TICK_RUNNABLES = new LinkedHashMap<Runnable, Integer>();

	public static void addRunnable(Runnable run, int time){
		TICK_RUNNABLES.put(run, time);
	}
	
	@SubscribeEvent
	public static void onTick(ClientTickEvent e){
		if(e.phase == Phase.START){
			List<Runnable> removes = new ArrayList<Runnable>();
			for(Entry<Runnable, Integer> entry : TICK_RUNNABLES.entrySet()){
				entry.setValue(entry.getValue() - 1);
				if(entry.getValue() <= 0){
					entry.getKey().run();
					removes.add(entry.getKey());
				}
			}
			for(Runnable run : removes)TICK_RUNNABLES.remove(run);
		}
	}
	
}
