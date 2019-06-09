package net.tiffit.wynnforge.data;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tiffit.wynnforge.PacketRecieveEvent;
import net.tiffit.wynnforge.WFNetHandler;
import net.tiffit.wynnforge.Wynnforge;
import net.tiffit.wynnforge.utils.WFUtils;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class FriendsManager {

	public static List<String> FRIEND_LIST = new ArrayList<String>();

	private static boolean intercept = false;
	private static boolean interceptRequest = false;
	private static long lastIntercept = -1;

	public static boolean isFriend(String friend) {
		return FRIEND_LIST.contains(friend.toLowerCase());
	}

	public static void load(){
		FRIEND_LIST.clear();
		NBTTagCompound tag = LocalData.getTag("friends");
		if(tag.hasKey("list")){
			NBTTagList list = tag.getTagList("list", 8);
			for(int i = 0; i < list.tagCount(); i++){
				FRIEND_LIST.add(list.getStringTagAt(i));
			}
		}
	}
	
	@SubscribeEvent
	public static void handleRecieveMessage(ClientChatReceivedEvent e) {
		String text = TextFormatting.getTextWithoutFormattingCodes(e.getMessage().getUnformattedText());
		if(interceptRequest && text.equals("Try typing /friend add Username!")){
			interceptRequest = false;
			e.setCanceled(true);
		}else if (e.getType() == ChatType.SYSTEM && intercept) {
			if (text.startsWith(Minecraft.getMinecraft().player.getName() + "'s friends (")) {
				intercept = false;
				e.setCanceled(true);
				boolean changed = false;
				String listFull = text.split(":")[1].trim();
				String[] list = listFull.toLowerCase().split(", ");
				List<String> newList = new ArrayList<String>();
				for (String friend : list) {
					newList.add(friend);
					changed = true;
					if (!FRIEND_LIST.contains(friend))
						Wynnforge.addChatMessage("Added " + friend + " to local friend list.");
				}
				for (String friend : FRIEND_LIST) {
					if (!newList.contains(friend)) {
						changed = true;
						Wynnforge.addChatMessage(TextFormatting.RED + "Removed " + friend + " from local friend list.");
					}
				}
				FRIEND_LIST = newList;
				if(changed){
					NBTTagList tagList = new NBTTagList();
					for(String s : FRIEND_LIST)tagList.appendTag(new NBTTagString(s));
					LocalData.getTag("friends").setTag("list", tagList);
					LocalData.save();
				}
			}else if(text.startsWith("We couldn't find any friends.")){
				intercept = false;
				interceptRequest = true;
				e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void handlePlayerList(PacketRecieveEvent e) {
		if (e.getPacket() instanceof SPacketPlayerListItem && !e.pre) {
			if (WFNetHandler.INSTANCE.getPlayerInfoMap().isEmpty())return;
			if (WFUtils.isInWorld()) {
				if (System.currentTimeMillis() - lastIntercept > 1000 * 60) {
					intercept = true;
					lastIntercept = System.currentTimeMillis();
					Minecraft.getMinecraft().player.sendChatMessage("/friend list");
				}
			}
		}
	}

}
