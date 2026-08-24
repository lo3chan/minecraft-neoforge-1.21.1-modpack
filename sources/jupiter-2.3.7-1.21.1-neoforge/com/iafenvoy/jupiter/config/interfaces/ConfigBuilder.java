package com.iafenvoy.jupiter.config.interfaces;

import net.minecraft.network.chat.Component;

public interface ConfigBuilder<T, E extends ConfigEntry<T>, B extends ConfigBuilder<T, E, B>> {
   B tooltip(String var1);

   B tooltip(Component var1);

   B callback(ValueChangeCallback<T> var1);

   B value(T var1);

   E build();
}
