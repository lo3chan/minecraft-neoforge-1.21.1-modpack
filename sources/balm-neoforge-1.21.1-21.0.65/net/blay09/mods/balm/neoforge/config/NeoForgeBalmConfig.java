package net.blay09.mods.balm.neoforge.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredBoolean;
import net.blay09.mods.balm.api.config.schema.ConfiguredDouble;
import net.blay09.mods.balm.api.config.schema.ConfiguredEnum;
import net.blay09.mods.balm.api.config.schema.ConfiguredFloat;
import net.blay09.mods.balm.api.config.schema.ConfiguredInt;
import net.blay09.mods.balm.api.config.schema.ConfiguredList;
import net.blay09.mods.balm.api.config.schema.ConfiguredLong;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.ConfiguredResourceLocation;
import net.blay09.mods.balm.api.config.schema.ConfiguredSet;
import net.blay09.mods.balm.api.config.schema.ConfiguredString;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;
import net.blay09.mods.balm.api.event.ConfigLoadedEvent;
import net.blay09.mods.balm.api.event.ConfigReloadedEvent;
import net.blay09.mods.balm.common.config.AbstractBalmConfig;
import net.blay09.mods.balm.common.config.ConfigLocalization;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoForgeBalmConfig extends AbstractBalmConfig {
   private static final Map<ResourceLocation, Table<String, String, ConfigValue<?>>> properties = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, ModConfig> modConfigs = new ConcurrentHashMap<>();
   private static final Logger logger = LoggerFactory.getLogger(NeoForgeBalmConfig.class);

   private static ConfigValue<?> addPropertyToSpec(ConfiguredProperty<?> property, Builder spec) {
      if (!property.comment().isBlank()) {
         spec.comment(property.comment());
      }

      spec.translation(ConfigLocalization.forProperty(property));

      return (ConfigValue<?>)(switch (property) {
         case ConfiguredBoolean configuredBoolean -> spec.define(configuredBoolean.name(), configuredBoolean.defaultValue());
         case ConfiguredDouble configuredDouble -> spec.define(configuredDouble.name(), configuredDouble.defaultValue());
         case ConfiguredEnum<?> configuredEnum -> defineEnum(spec, configuredEnum);
         case ConfiguredFloat configuredFloat -> spec.define(configuredFloat.name(), configuredFloat.defaultValue().doubleValue());
         case ConfiguredInt configuredInt -> spec.define(configuredInt.name(), configuredInt.defaultValue());
         case ConfiguredList<?> configuredList -> spec.defineListAllowEmpty(
            configuredList.name(),
            mapConfigCollectionToNeoForge(configuredList.defaultValue()),
            () -> newListElement(configuredList),
            it -> validateListElement(configuredList, it)
         );
         case ConfiguredLong configuredLong -> spec.define(configuredLong.name(), configuredLong.defaultValue());
         case ConfiguredResourceLocation configuredResourceLocation -> spec.define(
            configuredResourceLocation.name(), configuredResourceLocation.defaultValue().toString()
         );
         case ConfiguredSet<?> configuredSet -> spec.defineListAllowEmpty(
            configuredSet.name(),
            mapConfigCollectionToNeoForge(configuredSet.defaultValue()),
            () -> newSetElement(configuredSet),
            it -> validateSetElement(configuredSet, it)
         );
         case ConfiguredString configuredString -> spec.define(configuredString.name(), configuredString.defaultValue());
         default -> throw new IllegalStateException("Unexpected value: " + property);
      });
   }

   public static List<?> mapConfigCollectionToNeoForge(Collection<?> values) {
      return values.stream().map(NeoForgeBalmConfig::mapConfigValueToNeoForge).toList();
   }

   public static Object mapConfigValueToNeoForge(Object param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: iload 2
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/resources/ResourceLocation, java/lang/Float, java/util/Set, java/util/List ]
      // 0b: tableswitch 90 -1 3 90 33 45 62 76
      // 2c: aload 1
      // 2d: checkcast net/minecraft/resources/ResourceLocation
      // 30: astore 3
      // 31: aload 3
      // 32: invokevirtual net/minecraft/resources/ResourceLocation.toString ()Ljava/lang/String;
      // 35: goto 66
      // 38: aload 1
      // 39: checkcast java/lang/Float
      // 3c: astore 4
      // 3e: aload 4
      // 40: invokevirtual java/lang/Float.doubleValue ()D
      // 43: invokestatic java/lang/Double.valueOf (D)Ljava/lang/Double;
      // 46: goto 66
      // 49: aload 1
      // 4a: checkcast java/util/Set
      // 4d: astore 5
      // 4f: aload 5
      // 51: invokestatic net/blay09/mods/balm/neoforge/config/NeoForgeBalmConfig.mapConfigCollectionToNeoForge (Ljava/util/Collection;)Ljava/util/List;
      // 54: goto 66
      // 57: aload 1
      // 58: checkcast java/util/List
      // 5b: astore 6
      // 5d: aload 6
      // 5f: invokestatic net/blay09/mods/balm/neoforge/config/NeoForgeBalmConfig.mapConfigCollectionToNeoForge (Ljava/util/Collection;)Ljava/util/List;
      // 62: goto 66
      // 65: aload 0
      // 66: areturn
   }

   public static List<?> mapConfigListFromNeoForge(ConfiguredList<?> property, List<?> value) {
      return value.stream().map(it -> mapConfigValueFromNeoForge(property.nestedType(), it)).toList();
   }

   public static Set<?> mapConfigSetFromNeoForge(ConfiguredSet<?> property, List<?> value) {
      return value.stream().map(it -> mapConfigValueFromNeoForge(property.nestedType(), it)).collect(Collectors.toSet());
   }

   public static Object mapConfigValueFromNeoForge(ConfiguredProperty<?> param0, Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 2
      // 02: bipush 0
      // 03: istore 3
      // 04: aload 2
      // 05: iload 3
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/blay09/mods/balm/api/config/schema/ConfiguredResourceLocation, net/blay09/mods/balm/api/config/schema/ConfiguredFloat, net/blay09/mods/balm/api/config/schema/ConfiguredList, net/blay09/mods/balm/api/config/schema/ConfiguredSet ]
      // 0b: tableswitch 104 -1 3 104 33 49 68 86
      // 2c: aload 2
      // 2d: checkcast net/blay09/mods/balm/api/config/schema/ConfiguredResourceLocation
      // 30: astore 4
      // 32: aload 1
      // 33: checkcast java/lang/String
      // 36: invokestatic net/minecraft/resources/ResourceLocation.parse (Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;
      // 39: goto 74
      // 3c: aload 2
      // 3d: checkcast net/blay09/mods/balm/api/config/schema/ConfiguredFloat
      // 40: astore 5
      // 42: aload 1
      // 43: checkcast java/lang/Double
      // 46: invokevirtual java/lang/Double.floatValue ()F
      // 49: invokestatic java/lang/Float.valueOf (F)Ljava/lang/Float;
      // 4c: goto 74
      // 4f: aload 2
      // 50: checkcast net/blay09/mods/balm/api/config/schema/ConfiguredList
      // 53: astore 6
      // 55: aload 6
      // 57: aload 1
      // 58: checkcast java/util/List
      // 5b: invokestatic net/blay09/mods/balm/neoforge/config/NeoForgeBalmConfig.mapConfigListFromNeoForge (Lnet/blay09/mods/balm/api/config/schema/ConfiguredList;Ljava/util/List;)Ljava/util/List;
      // 5e: goto 74
      // 61: aload 2
      // 62: checkcast net/blay09/mods/balm/api/config/schema/ConfiguredSet
      // 65: astore 7
      // 67: aload 7
      // 69: aload 1
      // 6a: checkcast java/util/List
      // 6d: invokestatic net/blay09/mods/balm/neoforge/config/NeoForgeBalmConfig.mapConfigSetFromNeoForge (Lnet/blay09/mods/balm/api/config/schema/ConfiguredSet;Ljava/util/List;)Ljava/util/Set;
      // 70: goto 74
      // 73: aload 1
      // 74: areturn
   }

   private static Object mapConfigValueFromNeoForge(Class<?> nestedType, Object value) {
      if (nestedType == ResourceLocation.class) {
         return ResourceLocation.parse((String)value);
      } else if (nestedType == Float.class) {
         return ((Double)value).floatValue();
      } else {
         return nestedType.isEnum() && value instanceof String ? stringToEnum(value, nestedType) : value;
      }
   }

   private static <T> T newListElement(ConfiguredList<T> configuredList) {
      return newCollectionElement(configuredList.nestedType());
   }

   private static <T> T newSetElement(ConfiguredSet<T> configuredSet) {
      return newCollectionElement(configuredSet.nestedType());
   }

   private static <T> T newCollectionElement(Class<T> nestedType) {
      if (nestedType == Boolean.class) {
         return (T)Boolean.FALSE;
      } else if (nestedType == Double.class) {
         return (T)0.0;
      } else if (nestedType == Float.class) {
         return (T)0.0;
      } else if (nestedType == Integer.class) {
         return (T)0;
      } else if (nestedType == Long.class) {
         return (T)0L;
      } else if (nestedType == ResourceLocation.class) {
         return (T)ResourceLocation.fromNamespaceAndPath("minecraft", "air").toString();
      } else if (nestedType == String.class) {
         return (T)"";
      } else if (nestedType.isEnum()) {
         return nestedType.getEnumConstants()[0];
      } else {
         throw new IllegalArgumentException("Unsupported type " + nestedType);
      }
   }

   private static <T> boolean validateListElement(ConfiguredList<T> configuredList, Object value) {
      return validateCollectionElement(configuredList.nestedType(), value);
   }

   private static <T> boolean validateSetElement(ConfiguredSet<T> configuredSet, Object value) {
      return validateCollectionElement(configuredSet.nestedType(), value);
   }

   private static <T> boolean validateCollectionElement(Class<T> nestedType, Object value) {
      if (nestedType == Boolean.class) {
         return value instanceof Boolean || "true".equals(value) || "false".equals(value);
      } else if (nestedType == Double.class) {
         try {
            return value instanceof Double || !Double.isNaN(Double.parseDouble(value.toString()));
         } catch (NumberFormatException var5) {
            return false;
         }
      } else if (nestedType == Float.class) {
         try {
            return value instanceof Float || !Float.isNaN(Float.parseFloat(value.toString()));
         } catch (NumberFormatException var6) {
            return false;
         }
      } else if (nestedType == Integer.class) {
         try {
            if (value instanceof Integer) {
               return true;
            } else {
               Integer.parseInt(value.toString());
               return true;
            }
         } catch (NumberFormatException var3) {
            return false;
         }
      } else if (nestedType == Long.class) {
         try {
            if (value instanceof Long) {
               return true;
            } else {
               Long.parseLong(value.toString());
               return true;
            }
         } catch (NumberFormatException var4) {
            return false;
         }
      } else if (nestedType == ResourceLocation.class) {
         return value instanceof String && ResourceLocation.tryParse(value.toString()) != null;
      } else if (nestedType == String.class) {
         return value instanceof String;
      } else if (!nestedType.isEnum()) {
         throw new IllegalArgumentException("Unsupported type " + nestedType);
      } else {
         return value instanceof String && validateEnum(value, nestedType);
      }
   }

   private static <T extends Enum<T>> boolean validateEnum(Object value, Class<?> unknownClass) {
      if (unknownClass.isEnum()) {
         return EnumGetMethod.NAME_IGNORECASE.validate(value, unknownClass);
      } else {
         throw new IllegalArgumentException("Not an enum class: " + unknownClass.getName());
      }
   }

   private static <T extends Enum<T>> T stringToEnum(Object value, Class<?> unknownClass) {
      if (unknownClass.isEnum()) {
         return (T)EnumGetMethod.NAME_IGNORECASE.get(value, unknownClass);
      } else {
         throw new IllegalArgumentException("Not an enum class: " + unknownClass.getName());
      }
   }

   private static <T extends Enum<T>> ConfigValue<T> defineEnum(Builder spec, ConfiguredEnum<T> configuredEnum) {
      return spec.defineEnum(configuredEnum.name(), configuredEnum.defaultValue(), EnumGetMethod.NAME_IGNORECASE);
   }

   @Override
   public File getConfigDir() {
      return FMLPaths.CONFIGDIR.get().toFile();
   }

   @Override
   public void registerConfig(BalmConfigSchema schema) {
      super.registerConfig(schema);
      String namespace = schema.identifier().getNamespace();
      ModContainer modContainer = (ModContainer)ModList.get()
         .getModContainerById(namespace)
         .orElseThrow(() -> new IllegalStateException("Mod container for " + namespace + " not found when registering config."));
      IEventBus eventBus = modContainer.getEventBus();
      if (eventBus == null) {
         throw new IllegalStateException("Missing event bus for " + schema.identifier().getNamespace() + " when registering config.");
      } else {
         eventBus.addListener(
            event -> {
               ModConfig modConfig = event.getConfig();
               ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(modConfig.getModId(), modConfig.getType().extension());
               if (schema.identifier().equals(identifier)) {
                  modConfigs.put(schema.identifier(), modConfig);
                  Table<String, String, ConfigValue<?>> modConfigProperties = properties.get(schema.identifier());
                  if (modConfigProperties == null) {
                     throw new IllegalStateException(
                        "Missing config properties for " + schema.identifier() + " when loading config. Properties present: " + properties.keySet()
                     );
                  }

                  LoadedNeoForgeConfig wrappedConfig = new LoadedNeoForgeConfig(schema, modConfig, modConfigProperties);
                  this.setLocalConfig(schema, wrappedConfig);
                  this.setActiveConfig(schema, wrappedConfig);
                  this.fireConfigLoadHandlers(schema, wrappedConfig);
                  Balm.getEvents().fireEvent(new ConfigLoadedEvent(schema));
               }
            }
         );
         eventBus.addListener(
            event -> {
               ModConfig modConfig = event.getConfig();
               ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(modConfig.getModId(), modConfig.getType().extension());
               if (schema.identifier().equals(identifier)) {
                  modConfigs.put(schema.identifier(), modConfig);
                  Table<String, String, ConfigValue<?>> modConfigProperties = properties.get(schema.identifier());
                  if (modConfigProperties == null) {
                     throw new IllegalStateException(
                        "Missing config properties for " + schema.identifier() + " when loading config. Properties present: " + properties.keySet()
                     );
                  }

                  LoadedNeoForgeConfig wrappedConfig = new LoadedNeoForgeConfig(schema, modConfig, modConfigProperties);
                  this.setLocalConfig(schema, wrappedConfig);
                  this.updateActiveFromLocal(schema, wrappedConfig);
                  Balm.getEvents().fireEvent(new ConfigReloadedEvent(schema));
               }
            }
         );
         String stringType = schema.identifier().getPath();

         Type configType = switch (stringType) {
            case "common" -> Type.COMMON;
            case "client" -> Type.CLIENT;
            case "startup" -> Type.STARTUP;
            default -> throw new IllegalArgumentException("Unsupported config type: " + stringType + " - only 'common', 'client' and 'startup' are supported.");
         };
         Pair<ModConfigSpec, HashBasedTable<String, String, ConfigValue<?>>> mappedConfigSpec = this.mapToConfigSpec(schema);
         properties.put(schema.identifier(), (Table<String, String, ConfigValue<?>>)mappedConfigSpec.getSecond());
         logger.info(
            "Registering config for {} ({}) with {} properties.",
            new Object[]{schema.identifier(), configType, ((HashBasedTable)mappedConfigSpec.getSecond()).size()}
         );
         modContainer.registerConfig(configType, (IConfigSpec)mappedConfigSpec.getFirst());
         if (FMLEnvironment.dist == Dist.CLIENT) {
            this.initializeConfigurationScreen(modContainer);
         }
      }
   }

   @Override
   public void saveLocalConfig(BalmConfigSchema schema, MutableLoadedConfig config) {
      super.saveLocalConfig(schema, config);
      ModConfig modConfig = modConfigs.get(schema.identifier());
      if (modConfig == null) {
         throw new IllegalStateException("Backing config not available for " + schema.identifier());
      } else {
         Table<String, String, ConfigValue<?>> modConfigProperties = properties.get(schema.identifier());
         if (modConfigProperties == null) {
            throw new IllegalStateException(
               "Missing config properties for " + schema.identifier() + " when loading config. Properties present: " + properties.keySet()
            );
         } else {
            LoadedNeoForgeConfig wrappedConfig = new LoadedNeoForgeConfig(schema, modConfig, modConfigProperties);
            wrappedConfig.applyFrom(schema, config);
            ((ModConfigSpec)modConfig.getSpec()).save();
         }
      }
   }

   private void initializeConfigurationScreen(ModContainer modContainer) {
      modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
   }

   private Pair<ModConfigSpec, HashBasedTable<String, String, ConfigValue<?>>> mapToConfigSpec(BalmConfigSchema schema) {
      Builder spec = new Builder();
      HashBasedTable<String, String, ConfigValue<?>> properties = HashBasedTable.create();

      for (ConfiguredProperty<?> rootProperty : schema.rootProperties()) {
         properties.put("", rootProperty.name(), addPropertyToSpec(rootProperty, spec));
      }

      for (ConfigCategory category : schema.categories()) {
         spec.push(category.name());

         for (ConfiguredProperty<?> property : category.properties()) {
            properties.put(category.name(), property.name(), addPropertyToSpec(property, spec));
         }

         spec.pop();
      }

      return Pair.of(spec.build(), properties);
   }
}
