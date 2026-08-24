package io.wispforest.owo.text;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentContents.Type;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class CustomTextRegistry {
   private static final Map<String, CustomTextRegistry.Entry<?>> TYPES = new HashMap<>();

   private CustomTextRegistry() {
   }

   public static void register(Type<?> type, String triggerField) {
      TYPES.put(type.id(), new CustomTextRegistry.Entry(triggerField, type));
   }

   @Internal
   public static Map<String, CustomTextRegistry.Entry<?>> typesMap() {
      return TYPES;
   }

   public record Entry<C extends ComponentContents>(String triggerField, Type<C> type) {
   }
}
