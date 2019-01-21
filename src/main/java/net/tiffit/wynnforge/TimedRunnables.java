package net.tiffit.wynnforge;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod.EventBusSubscriber
public class TimedRunnables {

	private static ArrayList<TimedRunnable> TICK_RUNNABLES = new ArrayList<TimedRunnable>();

	public static void addRunnable(Runnable run, int time){
		TICK_RUNNABLES.add(new TimedRunnable(run, time));
	}
	
	@SubscribeEvent
	public static void onTick(ClientTickEvent e){
		if(e.phase == Phase.START){
			List<TimedRunnable> removes = new ArrayList<TimedRunnable>();
			for(TimedRunnable entry : TICK_RUNNABLES){
				entry.time--;
				if(entry.time <= 0){
					entry.run.run();
					removes.add(entry);
				}
			}
			for(TimedRunnable run : removes)TICK_RUNNABLES.remove(run);
		}
	}
	
	private static class TimedRunnable{
		Runnable run;
		int time;
		TimedRunnable(Runnable run, int time){
			this.run = run;
			this.time = time;
		}
	}
	
}
