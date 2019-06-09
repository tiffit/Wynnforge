package net.tiffit.wynnforge.journeymap;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import net.minecraft.util.math.BlockPos;
import net.tiffit.wynnforge.ConfigManager;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.module.ModuleJourneymap;
import net.tiffit.wynnforge.wynnapi.territories.TerritoryDB;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory;
import net.tiffit.wynnforge.wynnapi.territories.WynnTerritory.TerritoryLoc;

import java.util.Random;

@ClientPlugin
public class JMPlugin implements IClientPlugin {

	public IClientAPI api;
	
	@Override
	public void initialize(IClientAPI api) {
		if(!ConfigManager.isModuleLoaded("journey_map", true) || !ModuleJourneymap.draw_on_map)return;
		this.api = api;
		Random r = new Random();
		for(WynnTerritory t : TerritoryDB.territories){
			if(t.location == null)continue;
			TerritoryLoc l = t.location;
			MapPolygon poly = new MapPolygon(new BlockPos(l.startX, 64, l.startY), new BlockPos(l.startX, 64, l.endY), new BlockPos(l.endX, 64, l.endY), new BlockPos(l.endX, 64, l.startY));
			PolygonOverlay overlay = new PolygonOverlay(Wynnforge.MODID, t.territory, 0, new ShapeProperties().setFillColor(r.nextInt(256*256*256)).setFillOpacity(0.2f), poly);
			overlay.setTitle(t.territory);
			try {
				api.show(overlay);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getModId() {
		return Wynnforge.MODID;
	}

	@Override
	public void onEvent(ClientEvent event) {
	}

}
