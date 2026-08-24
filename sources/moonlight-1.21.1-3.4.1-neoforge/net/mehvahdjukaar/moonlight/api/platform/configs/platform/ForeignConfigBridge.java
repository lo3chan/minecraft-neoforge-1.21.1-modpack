package net.mehvahdjukaar.moonlight.api.platform.configs.platform;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigMetadata;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.IConfigValue;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.Range;
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import org.jetbrains.annotations.Nullable;

public final class ForeignConfigBridge {
   private static final Map<ModConfig, ForeignConfigHolder> CACHE = new WeakHashMap<>();
   private static final Map<String, List<ModConfig>> CONFIGS_BY_MOD = configsByModField();
   private static final Map<String, Boolean> GENERIC_SCREEN_CACHE = new HashMap<>();
   private static final String CONFIGURED_PACKAGE = "com.mrcrayfish.configured.";

   @Nullable
   public static Screen createScreen(String modId, Screen parent, @Nullable ResourceLocation background) {
      List<ModConfigHolder> holders = holdersFor(modId);
      return holders.isEmpty() ? null : MoonlightConfigSelectScreen.create(modId, holders, parent, background);
   }

   public static boolean hasOnlyGenericScreen(String modId) {
      return GENERIC_SCREEN_CACHE.computeIfAbsent(modId, ForeignConfigBridge::readIsGenericScreen);
   }

   private static boolean readIsGenericScreen(String modId) {
      ModContainer container = (ModContainer)ModList.get().getModContainerById(modId).orElse(null);
      if (container == null) {
         return false;
      } else {
         IConfigScreenFactory factory = (IConfigScreenFactory)container.getCustomExtension(IConfigScreenFactory.class).orElse(null);
         if (factory == null) {
            return true;
         } else {
            try {
               Screen screen = factory.createScreen(container, null);
               return screen instanceof ConfigurationScreen || screen.getClass().getName().startsWith("com.mrcrayfish.configured.");
            } catch (Exception var4) {
               return false;
            }
         }
      }
   }

   public static boolean hasHiddenPerWorldConfig(String modId) {
      for (ModConfig mc : CONFIGS_BY_MOD.getOrDefault(modId, List.of())) {
         if (mc.getType() == Type.SERVER && (!(mc.getSpec() instanceof ModConfigSpec spec) || !spec.isLoaded())) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasConfig(String modId) {
      for (ModConfig mc : CONFIGS_BY_MOD.getOrDefault(modId, List.of())) {
         if (ForgeConfigHolder.getFromForgeConfig(mc) == null && mc.getSpec() instanceof ModConfigSpec spec && spec.isLoaded() && !spec.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private static List<ModConfigHolder> holdersFor(String modId) {
      List<ModConfig> configs = CONFIGS_BY_MOD.getOrDefault(modId, List.of());
      List<ModConfigHolder> out = new ArrayList<>();

      for (ModConfig mc : configs) {
         if (ForgeConfigHolder.getFromForgeConfig(mc) == null && mc.getSpec() instanceof ModConfigSpec spec && spec.isLoaded()) {
            try {
               ForeignConfigHolder holder = CACHE.get(mc);
               if (holder == null) {
                  holder = build(modId, mc, spec);
                  CACHE.put(mc, holder);
               }

               if (holder.getConfigRoot() != null && !holder.getConfigRoot().isEmpty()) {
                  out.add(holder);
               }
            } catch (Exception var7) {
               Moonlight.LOGGER.warn("Failed to adapt config {} of mod {}", mc.getFileName(), modId, var7);
            }
         }
      }

      return out;
   }

   private static ForeignConfigHolder build(String modId, ModConfig mc, ModConfigSpec spec) {
      ConfigType type = mc.getType() == Type.CLIENT ? ConfigType.CLIENT : ConfigType.COMMON;
      String typeName = mc.getType().name().toLowerCase(Locale.ROOT);
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, typeName);
      ConfigCategory root = new ConfigCategory(Component.empty());
      walk(spec, spec.getValues(), List.of(), root);
      Component name = Component.literal(PlatHelper.getModName(modId) + " - " + TextHelper.getReadableName(typeName));
      return new ForeignConfigHolder(id, type, spec, root, name);
   }

   private static void walk(ModConfigSpec spec, UnmodifiableConfig config, List<String> path, ConfigCategory parent) {
      for (Entry entry : config.entrySet()) {
         String key = entry.getKey();
         List<String> childPath = append(path, key);
         Object raw = entry.getRawValue();
         if (raw instanceof UnmodifiableConfig sub) {
            ConfigCategory cat = new ConfigCategory(categoryTitle(spec, childPath, key));
            String comment = spec.getLevelComment(childPath);
            if (comment != null) {
               cat.setDescription(Component.literal(comment));
            }

            walk(spec, sub, childPath, cat);
            if (!cat.isEmpty()) {
               parent.add(cat);
            }
         } else if (raw instanceof ConfigValue<?> cv) {
            ConfigOption<?> option = leaf(spec, cv);
            if (option != null) {
               parent.add(option);
            }
         }
      }
   }

   @Nullable
   private static ConfigOption<?> leaf(ModConfigSpec spec, ConfigValue<?> cv) {
      List<String> path = cv.getPath();
      if (spec.getSpec().get(path) instanceof ValueSpec vs) {
         String key = path.isEmpty() ? "" : path.get(path.size() - 1);
         Component title = leafTitle(vs, key);
         Component desc = vs.getComment() != null ? Component.literal(vs.getComment()) : null;
         ConfigMetadata meta = new ConfigMetadata(reloadType(vs.restartType()), false);
         Object sample = vs.getDefault() != null ? vs.getDefault() : cv.get();
         if (sample instanceof Boolean b) {
            return new ConfigOption.BooleanValue(title, desc, wrap(cv, meta), b);
         } else if (sample instanceof Enum<?> e) {
            Enum<?>[] options = (Enum<?>[])e.getDeclaringClass().getEnumConstants();
            return new ConfigOption.EnumValue<>(title, desc, wrap(cv, meta), e, options);
         } else if (sample instanceof Integer i) {
            int[] r = intRange(vs);
            return new ConfigOption.IntValue(title, desc, wrap(cv, meta), i, r[0], r[1]);
         } else if (sample instanceof Long l) {
            long[] r = longRange(vs);
            return (ConfigOption<?>)(r[0] >= -2147483648L && r[1] <= 2147483647L
               ? new ConfigOption.IntValue(title, desc, longAsInt(cv, meta), l.intValue(), (int)r[0], (int)r[1])
               : new ConfigOption.UnsupportedValue(title, desc, (Supplier<Object>)cv));
         } else if (sample instanceof Double d) {
            double[] r = doubleRange(vs);
            return new ConfigOption.DoubleValue(title, desc, wrap(cv, meta), d, r[0], r[1]);
         } else if (sample instanceof String s) {
            return new ConfigOption.StringValue(title, desc, wrap(cv, meta), s, vs::test);
         } else if (sample instanceof List<?> list && list.stream().allMatch(o -> o instanceof String)) {
            List<String> def = list.stream().map(o -> (String)o).toList();
            return new ConfigOption.ListValue(title, desc, wrap(cv, meta), def, null);
         } else {
            return new ConfigOption.UnsupportedValue(title, desc, (Supplier<Object>)cv);
         }
      } else {
         return null;
      }
   }

   private static IConfigValue wrap(ConfigValue<?> cv, ConfigMetadata meta) {
      return ForgeConfigValue.simple(cv, meta);
   }

   private static IConfigValue<Integer> longAsInt(ConfigValue<?> cvRaw, final ConfigMetadata meta) {
      final ConfigValue<Long> cv = (ConfigValue<Long>)cvRaw;
      return new IConfigValue<Integer>() {
         public Integer get() {
            return ((Long)cv.get()).intValue();
         }

         public boolean setValue(Integer value) {
            boolean changed = (Long)cv.get() != value.longValue();
            cv.set(value.longValue());
            cv.clearCache();
            return changed;
         }

         @Override
         public ConfigReloadType reloadType() {
            return meta.reloadType();
         }

         @Override
         public boolean affectsDynamicPacks() {
            return meta.affectsDynamicPacks();
         }
      };
   }

   private static int[] intRange(ValueSpec vs) {
      Range<?> r = vs.getRange();
      return r != null && r.getMin() instanceof Number min && r.getMax() instanceof Number max
         ? new int[]{min.intValue(), max.intValue()}
         : new int[]{-2147483648, 2147483647};
   }

   private static long[] longRange(ValueSpec vs) {
      Range<?> r = vs.getRange();
      return r != null && r.getMin() instanceof Number min && r.getMax() instanceof Number max
         ? new long[]{min.longValue(), max.longValue()}
         : new long[]{-9223372036854775808L, 9223372036854775807L};
   }

   private static double[] doubleRange(ValueSpec vs) {
      Range<?> r = vs.getRange();
      return r != null && r.getMin() instanceof Number min && r.getMax() instanceof Number max
         ? new double[]{min.doubleValue(), max.doubleValue()}
         : new double[]{-1.7976931348623157E308, 1.7976931348623157E308};
   }

   private static Component leafTitle(ValueSpec vs, String key) {
      String tk = vs.getTranslationKey();
      return tk != null && I18n.exists(tk) ? Component.translatable(tk) : Component.literal(TextHelper.getReadableName(key));
   }

   private static Component categoryTitle(ModConfigSpec spec, List<String> path, String key) {
      String tk = spec.getLevelTranslationKey(path);
      return I18n.exists(tk) ? Component.translatable(tk) : Component.literal(TextHelper.getReadableName(key));
   }

   private static ConfigReloadType reloadType(RestartType rt) {
      return switch (rt) {
         case WORLD -> ConfigReloadType.WORLD_RELOAD;
         case GAME -> ConfigReloadType.GAME_RESTART;
         default -> ConfigReloadType.NONE;
      };
   }

   private static List<String> append(List<String> path, String key) {
      List<String> out = new ArrayList<>(path.size() + 1);
      out.addAll(path);
      out.add(key);
      return out;
   }

   private static Map<String, List<ModConfig>> configsByModField() {
      try {
         Field f = ConfigTracker.class.getDeclaredField("configsByMod");
         f.setAccessible(true);
         return (Map<String, List<ModConfig>>)f.get(ConfigTracker.INSTANCE);
      } catch (Exception var1) {
         Moonlight.LOGGER.error("Could not access NeoForge config registry; foreign config conversion disabled", var1);
         return Map.of();
      }
   }
}
