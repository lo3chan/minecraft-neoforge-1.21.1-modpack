package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.jetbrains.annotations.Nullable;

public final class ConfigBuilder {
   private ConfigBuilder() {
   }

   public static <T extends ConfigCore> void build(Builder builder, ConfigDataHolderImpl<?> context, T o) {
      Objects.requireNonNull(o, "object is null");
      build(builder, context, o.getClass(), o);
   }

   public static <T extends ConfigCore> void build(Builder builder, ConfigDataHolderImpl<?> context, Class<? extends T> clazz) {
      build(builder, context, clazz, null);
   }

   public static <T extends ConfigCore> void build(Builder builder, ConfigDataHolderImpl<?> context, Class<? extends T> clazz, @Nullable T o) {
      Objects.requireNonNull(clazz, "clazz is null");
      Map<List<String>, Collection<Field>> pathToFields = getAllFieldsWithPath(clazz);

      for (Entry<List<String>, Collection<Field>> entry : pathToFields.entrySet()) {
         List<String> path = entry.getKey();
         if (!path.isEmpty()) {
            for (String category : path) {
               builder.push(category);
            }
         }

         for (Field field : entry.getValue()) {
            field.setAccessible(true);
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (!isStatic) {
               Objects.requireNonNull(o, "Null instance for non-static field");
            }

            ConfigEntry<?> configEntry = getConfigEntry(field);
            configEntry.defineValue(builder, context, isStatic ? null : o);
         }

         if (!path.isEmpty()) {
            builder.pop(path.size());
         }
      }

      if (o != null) {
         o.addToBuilder(builder, context);
         context.acceptValueCallback(o::afterConfigReload);
      }
   }

   private static Map<List<String>, Collection<Field>> getAllFieldsWithPath(Class<?> target) {
      Multimap<List<String>, Field> paths = HashMultimap.create();

      for (Field field : collectAllFields(target)) {
         Config config = field.getDeclaredAnnotation(Config.class);
         if (config != null) {
            paths.put(new ArrayList<>(Arrays.asList(config.category())), field);
         }
      }

      return paths.asMap();
   }

   private static List<Field> collectAllFields(Class<?> clazz) {
      List<Field> fields = new ArrayList<>();

      while (clazz != Object.class) {
         fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
         clazz = clazz.getSuperclass();
      }

      return fields;
   }

   private static ConfigEntry<?> getConfigEntry(Field field) {
      Class var10000 = field.getType();
      Objects.requireNonNull(var10000);
      Class var1 = var10000;
      byte var2 = 0;

      while (true) {
         Object var11;
         switch (SwitchBootstraps.typeSwitch<"typeSwitch",Class,Class,Class,Class,Class,Class,Class,Class>(var1, var2)) {
            case 0:
               if (!ConfigCore.class.isAssignableFrom(var1)) {
                  var2 = 1;
                  continue;
               }

               var11 = new ConfigEntry.ChildEntry(field);
               break;
            case 1:
               if (var1 != boolean.class) {
                  var2 = 2;
                  continue;
               }

               var11 = new ValueEntry.BooleanEntry(field);
               break;
            case 2:
               if (var1 != int.class) {
                  var2 = 3;
                  continue;
               }

               var11 = new NumberEntry.IntegerEntry(field);
               break;
            case 3:
               if (var1 != long.class) {
                  var2 = 4;
                  continue;
               }

               var11 = new NumberEntry.LongEntry(field);
               break;
            case 4:
               if (var1 != double.class) {
                  var2 = 5;
                  continue;
               }

               var11 = new NumberEntry.DoubleEntry(field);
               break;
            case 5:
               if (var1 != String.class) {
                  var2 = 6;
                  continue;
               }

               var11 = new LimitedEntry.StringEntry(field);
               break;
            case 6:
               if (!var1.isEnum()) {
                  var2 = 7;
                  continue;
               }

               var11 = new LimitedEntry.EnumEntry(field);
               break;
            case 7:
               if (var1 != List.class) {
                  var2 = 8;
                  continue;
               }

               var11 = new LimitedEntry.ListEntry(field);
               break;
            default:
               throw new IllegalArgumentException("Unsupported config value type: " + field.getType());
         }

         return (ConfigEntry<?>)var11;
      }
   }
}
