package com.iafenvoy.jupiter.compat;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.config.ConfigSide;
import com.iafenvoy.jupiter.config.ConfigSource;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.render.screen.ConfigSelectScreen;
import com.iafenvoy.jupiter.util.TextFormatter;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.Screen;

public final class ExtraConfigManager {
   private static final Map<ConfigSource, Supplier<Map<String, EnumMap<ConfigSide, AbstractConfigContainer>>>> SCANNERS = new LinkedHashMap<>();
   private static final Map<ConfigSource, Map<String, EnumMap<ConfigSide, AbstractConfigContainer>>> CONFIGS = new LinkedHashMap<>();
   private static final List<Runnable> CALLBACKS = new LinkedList<>();

   public static void registerScanner(ConfigSource source, Supplier<Map<String, EnumMap<ConfigSide, AbstractConfigContainer>>> scanner) {
      SCANNERS.put(source, scanner);
   }

   public static void scanConfigs() {
      for (Entry<ConfigSource, Supplier<Map<String, EnumMap<ConfigSide, AbstractConfigContainer>>>> entry : SCANNERS.entrySet()) {
         try {
            CONFIGS.put(entry.getKey(), entry.getValue().get());
         } catch (Exception var3) {
            Jupiter.LOGGER.error("Failed to scan from config source {}", entry.getKey().name().getString(), var3);
         }
      }

      CALLBACKS.forEach(Runnable::run);
   }

   public static Set<String> getProvidedMods() {
      return CONFIGS.values().stream().map(Map::keySet).flatMap(Collection::stream).collect(Collectors.toSet());
   }

   public static Optional<EnumMap<ConfigSide, AbstractConfigContainer>> find(String modId) {
      return CONFIGS.values().stream().map(x -> x.get(modId)).filter(Objects::nonNull).findFirst();
   }

   public static Function<Screen, ConfigSelectScreen> getScreen(String modId) {
      return parent -> {
         Optional<EnumMap<ConfigSide, AbstractConfigContainer>> optional = find(modId);
         if (optional.isEmpty()) {
            return null;
         } else {
            ConfigSelectScreen.Builder builder = ConfigSelectScreen.builder(TextUtil.literal(TextFormatter.formatToTitleCase(modId + "_configs", true)), parent)
               .displayCommon();

            for (Entry<ConfigSide, AbstractConfigContainer> entry : optional.get().entrySet()) {
               AbstractConfigContainer container = entry.getValue();
               switch ((ConfigSide)entry.getKey()) {
                  case CLIENT:
                     builder.client(container);
                     break;
                  case COMMON:
                     builder.common(container);
                     break;
                  case SERVER:
                     builder.server(container);
               }
            }

            return builder.build();
         }
      };
   }

   public static void registerScanCallback(Runnable callback) {
      CALLBACKS.add(callback);
   }
}
