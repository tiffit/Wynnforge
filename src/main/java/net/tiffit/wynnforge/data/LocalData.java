package net.tiffit.wynnforge.data;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.tiffit.wynnforge.module.ModuleBase;

public class LocalData {

	private static NBTTagCompound tag;
	private static File f;
	
	public static void loadData(){
		f = new File(Minecraft.getMinecraft().mcDataDir, "wynnforge.dat");
		if(!f.exists()){
			tag = new NBTTagCompound();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				tag = CompressedStreamTools.read(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static NBTTagCompound getTag(ModuleBase m){
		return getTag(m.getName());
	}
	
	public static NBTTagCompound getTag(String name){
		if(name == null)return tag;
		if(tag.hasKey(name))
			return tag.getCompoundTag(name);
		NBTTagCompound newTag = new NBTTagCompound();
		tag.setTag(name, newTag);
		return newTag;
	}
	
	public static void save(){
		try {
			CompressedStreamTools.write(tag, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
