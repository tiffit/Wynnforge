package net.tiffit.wynnforge.module;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.data.FriendsManager;
import net.tiffit.wynnforge.module.ModuleBase.ModuleClass;

@ModuleClass
public class ModulePlayerInfo extends ModuleBase {

	public ModulePlayerInfo() {
		super("Player Info");
	}

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post e) {
		EntityPlayer p = e.getEntityPlayer();
		if(Minecraft.getMinecraft().player.getUniqueID().equals(p.getUniqueID()))return;
		List<String> tags = new ArrayList<String>();
		if (FriendsManager.isFriend(p.getName())) {
			tags.add(TextFormatting.GREEN + "Friend");
		}
		if(p.getName().equals("tiffit"))tags.add(TextFormatting.DARK_AQUA + "WFDev");
		String nameplate = "";
		for (String tag : tags)
			nameplate += tag + TextFormatting.RESET + " ";

		if (!nameplate.trim().isEmpty()) {
			Minecraft mc = Minecraft.getMinecraft();
			RenderManager rm = mc.getRenderManager();
			boolean flag = p.isSneaking();
			float f = rm.playerViewY;
			float f1 = rm.playerViewX;
			boolean flag1 = rm.options.thirdPersonView == 2;
			float f2 = p.height + 0.5F - (flag ? 0.25F : 0.0F);
			EntityRenderer.drawNameplate(mc.fontRenderer, nameplate.trim(), (float) e.getX(), (float) e.getY() + f2, (float) e.getZ(), -10, f, f1, flag1, flag);
		}

	}

}
