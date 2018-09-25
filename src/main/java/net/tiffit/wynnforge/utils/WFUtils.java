package net.tiffit.wynnforge.utils;

import org.lwjgl.opencl.api.Filter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class WFUtils {

	public static boolean isInWorld() {
		return !Minecraft.getMinecraft().player.inventory.getStackInSlot(6).isEmpty();
	}

	public static int getCurrentEmeralds() {
		int emeralds = 0;
		emeralds += getCountInInv("Emerald");
		emeralds += getCountInInv("Emerald Block") * 64;
		emeralds += getCountInInv("Liquid Emerald") * 64 * 64;
		return emeralds;
	}

	public static int getCountInInv(String name){
		return getCountInInv(new Filter<String>() {
			@Override
			public boolean accept(String object) {
				return name.equals(object);
			}
		});
	}
	
	public static int getCountInInv(Filter<String> filter){
		int count = 0;
		for (ItemStack s : Minecraft.getMinecraft().player.inventory.mainInventory) {
			if(filter.accept(TextFormatting.getTextWithoutFormattingCodes(s.getDisplayName())))count += s.getCount();
		}
		return count;
	}
	
	public static int getCurrentTokens(){
		return getCountInInv(new Filter<String>() {
			@Override
			public boolean accept(String object) {
				return object.endsWith(" Token");
			}
		});
	}
	
	public static int getTreasureValue(){
		int value = 0;
		value += getCountInInv("Small Pearl")*2;
		value += getCountInInv("Glowing Pearl")*6;
		value += getCountInInv("Large Glowing Pearl")*10;
		value += getCountInInv("Huge Pearl")*18;
		value += getCountInInv("Coral Ruby")*10;
		value += getCountInInv("Coral Amethyst")*12;
		value += getCountInInv("Coral Topaz")*14;
		value += getCountInInv("Coral Sapphire")*28;
		value += getCountInInv("Coral Diamond")*128;
		value += getCountInInv("Sunken Silver Nugget")*2;
		value += getCountInInv("Sunken Gold Nugget")*3;
		value += getCountInInv("Sunken Silver Bar")*5;
		value += getCountInInv("Sunken Gold Bar")*7;
		value += getCountInInv("Sunken Silver Bundle")*10;
		value += getCountInInv("Sunken Gold Bundle")*14;
		value += getCountInInv("Sunken Block of Silver")*24;
		value += getCountInInv("Sunken Block of Gold")*32;
		value += getCountInInv("Sunken Artifact")*192;
		return value;
	}
	
	// stolen from the GUI class
	public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) right, (double) top, 0).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) left, (double) top, 0).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) left, (double) bottom, 0).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, 0).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static Thread runOnNewThread(Runnable run) {
		Thread thread = new Thread(run);
		thread.start();
		return thread;
	}
}
