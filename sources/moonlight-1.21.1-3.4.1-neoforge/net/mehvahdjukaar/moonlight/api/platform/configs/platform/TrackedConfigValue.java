package net.mehvahdjukaar.moonlight.api.platform.configs.platform;

import net.mehvahdjukaar.moonlight.api.platform.configs.IConfigValue;

public interface TrackedConfigValue<T> extends IConfigValue<T> {
   boolean pollChanged();
}
