package net.tiffit.wynnforge.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ModuleBase {

	protected final String name;
	
	public ModuleBase(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void init(FMLInitializationEvent e){}
	
	public String getConfigName(){
		return name.replace(" ", "_").toLowerCase();
	}
	
	public boolean defaultEnabled() {
		return true;
	}
	
	public void loadConfig(ConfigCategory cat) {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface ModuleClass{
		String reqMod() default "";
	}
	
}
