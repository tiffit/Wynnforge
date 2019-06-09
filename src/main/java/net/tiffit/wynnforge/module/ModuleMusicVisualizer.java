package net.tiffit.wynnforge.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;
import net.tiffit.wynnforge.utils.WFUtils;

import java.awt.*;

@ModuleClass
public class ModuleMusicVisualizer extends ModuleBase {

	public ModuleMusicVisualizer() {
		super("Music Visualizer");
	}

	private String lastInstrument;
	private float lastPitch = 0;
	private long lastNoteTime = 0;
	
	@SubscribeEvent
	public void onBeat(SoundSourceEvent e) {
		if(e.getName().startsWith("block.note")){
			String type = e.getName().split("\\.")[2];
			lastInstrument = type;
			lastPitch = e.getSound().getPitch();
			lastNoteTime = System.currentTimeMillis();
		}
	}
	
	@SubscribeEvent
	public void onDrawHud(RenderGameOverlayEvent.Pre e) {
		if(e.getType() == ElementType.ALL)
		if(lastInstrument != null) {
			Color c = new Color(0f, 0f, 0f, 0f);
			switch(lastInstrument) {
			case "harp": c = new Color(1f, 0, 0); break;
			case "snare": c = new Color(0, 1f, 0); break;
			case "basedrum": c = new Color(0, 0, 1f); break;
			case "hat": c = new Color(1f, 1f, 0); break;
			case "bass": c = new Color(1f, 0, 1f); break;
			}
			Minecraft mc = Minecraft.getMinecraft();
			int alpha = (int)(lastPitch*100) + 25;
			long curTime = System.currentTimeMillis();
			if(curTime - lastNoteTime > 1000) {
				if(curTime - lastNoteTime < 2000) {
					alpha = (int) ((alpha/1000f) * (2000 - (curTime - lastNoteTime)));
				}
				else {
					lastInstrument = null;
					return;
				}
			}
			int colorInt = (alpha << 24) | (c.getRed() << 16 ) | (c.getGreen()<<8) | c.getBlue();
			WFUtils.drawGradientRect(0, 0, mc.displayWidth, mc.displayHeight/25, colorInt, 0);
			GlStateManager.color(1, 1, 1);
		}
	}
	
	@Override
	public boolean defaultEnabled() {
		return false;
	}

}
