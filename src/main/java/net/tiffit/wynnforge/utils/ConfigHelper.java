package net.tiffit.wynnforge.utils;

import static net.minecraftforge.common.config.Property.Type.STRING;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

public class ConfigHelper {

	public final ConfigCategory cat;

	public ConfigHelper(ConfigCategory cat) {
		this.cat = cat;
	}

	public boolean getBoolean(String key, boolean defaultValue, String comment) {
		return get(key, defaultValue + "", comment, Type.BOOLEAN).getBoolean();
	}

	public int getInteger(String key, int defaultValue, String comment) {
		return get(key, defaultValue + "", comment, Type.INTEGER).getInt();
	}
	
	public String[] getStringList(String name, String[] defaultValue, String comment) {
		return get(name, defaultValue, comment).getStringList();
	}

	public Property get(String key, String defaultValue, String comment, Property.Type type) {
		if (cat.containsKey(key)) {
			Property prop = cat.get(key);
			if (prop.getType() == null) {
				prop = new Property(prop.getName(), prop.getString(), type);
				cat.put(key, prop);
			}
			prop.setDefaultValue(defaultValue);
			prop.setComment(comment);
			return prop;
		} else if (defaultValue != null) {
			Property prop = new Property(key, defaultValue, type);
			prop.setValue(defaultValue);
			cat.put(key, prop);
			prop.setDefaultValue(defaultValue);
			prop.setComment(comment);
			return prop;
		} else {
			return null;
		}
	}

	public Property get(String key, String[] defaultValues, String comment) {
		Property prop = get(key, defaultValues, comment, STRING);
		return prop;
	}

	public Property get(String key, String[] defaultValues, String comment, Property.Type type) {
		if (cat.containsKey(key)) {
			Property prop = cat.get(key);

			if (prop.getType() == null) {
				prop = new Property(prop.getName(), prop.getString(), type);
				cat.put(key, prop);
			}

			prop.setDefaultValues(defaultValues);
			prop.setComment(comment);

			return prop;
		} else if (defaultValues != null) {
			Property prop = new Property(key, defaultValues, type);
			prop.setDefaultValues(defaultValues);
			prop.setComment(comment);
			cat.put(key, prop);
			return prop;
		} else {
			return null;
		}
	}

}
