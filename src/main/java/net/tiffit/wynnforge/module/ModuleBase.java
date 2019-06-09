package net.tiffit.wynnforge.module;

import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.tiffit.wynnforge.utils.ConfigHelper;
import net.tiffit.wynnforge.utils.WFCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

public class ModuleBase {

	protected final String name;

	public ModuleBase(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void loadModule() {
	}

	public void unloadModule() {
	}

	public String getConfigName() {
		return name.replace(" ", "_").toLowerCase();
	}

	protected void unloadCommand(WFCommand command) {
		Map<String, ICommand> commands = ClientCommandHandler.instance.getCommands();
		commands.remove(command.getName());
		for (String s : command.getAliases())commands.remove(s);
		ClientCommandHandler.instance.commandSet.remove(command);
	}

	public boolean defaultEnabled() {
		return true;
	}

	public void loadConfig(ConfigHelper conf) {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface ModuleClass {
		String reqMod() default "";
	}

}
