package net.tiffit.wynnforge.data;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

@Mod.EventBusSubscriber
public class WynnforgeServerConnector {
	
	private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
	private static final String SERVER_URL = "http://tiffit.net/Wynnforge/";
	private static String key = null;
	
	@SubscribeEvent
	public static void connectWynnforgeServer(ClientConnectedToServerEvent e) {

		String serverId = createJoinSession();
		getKey(serverId);
	}
	
	private static String createJoinSession(){
		try {
			HttpClient client = getClient();
			Minecraft mc = Minecraft.getMinecraft();
			String uuid = mc.getSession().getProfile().getId().toString();
			JsonObject request = new JsonObject();
			request.addProperty("accessToken", mc.getSession().getToken());
			request.addProperty("selectedProfile", uuid.replaceAll("-", ""));
			String serverId = DigestUtils.sha1Hex(uuid + UUID.randomUUID().toString());
			request.addProperty("serverId", serverId);
			HttpPost post = new HttpPost(JOIN_URL);
			StringEntity entity = new StringEntity(request.toString(), ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			client.execute(post);
			EntityUtils.consumeQuietly(entity);
			return serverId;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	private static void getKey(String serverId){
		try {
			HttpClient client = getClient();
			Minecraft mc = Minecraft.getMinecraft();
			HttpGet get = new HttpGet(SERVER_URL + "api/getkey?name=" + mc.getSession().getUsername() + "&id=" + serverId);
			HttpResponse resp = client.execute(get);
			HttpEntity entity = resp.getEntity();
			JsonObject obj = new Gson().fromJson(EntityUtils.toString(entity), JsonObject.class);
			if(obj.has("key")){
				key = obj.get("key").getAsString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static int sendPost(String url, JsonObject message){
		try {
			HttpClient client = getClient();
			HttpPost post = new HttpPost(SERVER_URL + url);
			message.addProperty("key", key);
			StringEntity entity = new StringEntity(message.toString(), ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			int code = client.execute(post).getStatusLine().getStatusCode();
			EntityUtils.consumeQuietly(entity);
			return code;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 404;
	}
	
	public static JsonObject sendGet(String url){
		try {
			HttpClient client = getClient();
			url += "&key=" + key;
			HttpGet post = new HttpGet(SERVER_URL + url);
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			JsonObject obj = new Gson().fromJson(EntityUtils.toString(entity), JsonObject.class);
			return obj;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static boolean isConnected(){
		return key != null;
	}
	
	private static HttpClient getClient(){
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(3000).build();
		return HttpClients.custom().setDefaultRequestConfig(config).build();
	}
	
}

