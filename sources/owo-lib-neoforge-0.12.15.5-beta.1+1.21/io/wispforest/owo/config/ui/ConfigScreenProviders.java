package io.wispforest.owo.config.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ConfigScreenProviders {
   private static final Map<String, Function<Screen, ? extends Screen>> PROVIDERS = new HashMap<>();
   private static final Map<String, Function<Screen, ? extends ConfigScreen>> OWO_SCREEN_PROVIDERS = new HashMap<>();

   public static <S extends Screen> void register(String modId, Function<Screen, S> supplier) {
      if (PROVIDERS.put(modId, supplier) != null) {
         throw new IllegalArgumentException("Tried to register config screen provider for mod id " + modId + " twice");
      }
   }

   @Nullable
   public static Function<Screen, ? extends Screen> get(String modId) {
      return PROVIDERS.get(modId);
   }

   public static void forEach(BiConsumer<String, Function<Screen, ? extends Screen>> action) {
      PROVIDERS.forEach(action);
   }

   @Internal
   public static <S extends ConfigScreen> void registerOwoConfigScreen(String modId, Function<Screen, S> supplier) {
      register(modId, supplier);
      OWO_SCREEN_PROVIDERS.put(modId, supplier);
   }

   @Nullable
   static Function<Screen, ? extends ConfigScreen> getOwoProvider(String modId) {
      return OWO_SCREEN_PROVIDERS.get(modId);
   }

   static void forEachOwoProvider(BiConsumer<String, Function<Screen, ? extends ConfigScreen>> action) {
      OWO_SCREEN_PROVIDERS.forEach(action);
   }
}
