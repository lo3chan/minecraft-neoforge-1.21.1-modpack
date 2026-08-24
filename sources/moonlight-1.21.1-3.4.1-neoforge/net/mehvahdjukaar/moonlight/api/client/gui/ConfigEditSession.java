package net.mehvahdjukaar.moonlight.api.client.gui;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public final class ConfigEditSession {
   @Nullable
   private final ModConfigHolder holder;
   private final Screen returnScreen;
   private final Map<ConfigOption<?>, Object> pending = new IdentityHashMap<>();
   private final Set<ConfigOption<?>> expanded = Collections.newSetFromMap(new IdentityHashMap<>());
   private ConfigReloadType appliedReload = ConfigReloadType.NONE;

   public ConfigEditSession(ModConfigHolder holder, Screen returnScreen) {
      this.holder = holder;
      this.returnScreen = returnScreen;
   }

   private ConfigEditSession(Screen returnScreen) {
      this.holder = null;
      this.returnScreen = returnScreen;
   }

   public static ConfigEditSession scratch(Screen returnScreen) {
      return new ConfigEditSession(returnScreen);
   }

   public ModConfigHolder holder() {
      return this.holder;
   }

   public Screen returnScreen() {
      return this.returnScreen;
   }

   public <T> T current(ConfigOption<T> v) {
      return (T)(this.pending.containsKey(v) ? this.pending.get(v) : v.get());
   }

   public Object currentRaw(ConfigOption<?> v) {
      return this.pending.containsKey(v) ? this.pending.get(v) : v.get();
   }

   public void put(ConfigOption<?> v, Object value) {
      this.pending.put(v, value);
   }

   public int unsavedCount() {
      int count = 0;

      for (Entry<ConfigOption<?>, Object> e : this.pending.entrySet()) {
         if (!Objects.equals(e.getValue(), e.getKey().get())) {
            count++;
         }
      }

      return count;
   }

   public void apply() {
      if (this.holder != null) {
         this.pending.forEach((v, value) -> {
            if (!Objects.equals(value, v.get())) {
               v.apply(this.holder, value);
               if (v.reloadType().ordinal() > this.appliedReload.ordinal()) {
                  this.appliedReload = v.reloadType();
               }
            }
         });
      }
   }

   public ConfigReloadType appliedReload() {
      return this.appliedReload;
   }

   public void clearPending() {
      this.pending.clear();
   }

   public boolean isExpanded(ConfigOption<?> v) {
      return this.expanded.contains(v);
   }

   public void toggleExpanded(ConfigOption<?> v) {
      if (!this.expanded.remove(v)) {
         this.expanded.add(v);
      }
   }
}
