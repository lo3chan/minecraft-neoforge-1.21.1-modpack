package fuzs.puzzleslib.impl.config.annotation;

import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Consumer;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.jetbrains.annotations.Nullable;

public abstract class ValueEntry<T> extends ConfigEntry<T> {
   public ValueEntry(Field field) {
      super(field);
   }

   @Override
   public List<String> getComments(@Nullable Object o) {
      List<String> comments = super.getComments(o);
      T defaultValue = this.getDefaultValue(o);
      comments.add("Default Value: " + (defaultValue != null ? this.getValueString(defaultValue) : null));
      if (this.requiresWorldRestart()) {
         comments.add("Requires Restart: World");
      }

      if (this.requiresGameRestart()) {
         comments.add("Requires Restart: Game");
      }

      return comments;
   }

   protected String getValueString(T value) {
      return value.toString();
   }

   @Override
   public final void defineValue(Builder builder, ConfigDataHolderImpl<?> context, @Nullable Object o) {
      if (Modifier.isFinal(this.field.getModifiers())) {
         throw new RuntimeException("Field must not be final");
      } else {
         List<String> comments = this.getComments(o);
         builder.comment(comments.toArray(String[]::new));
         if (this.requiresWorldRestart()) {
            builder.worldRestart();
         }

         if (this.requiresGameRestart()) {
            builder.gameRestart();
         }

         ConfigValue<T> configValue = this.getConfigValue(builder, o);
         context.accept(configValue, this.getValueCallback(configValue, o));
      }
   }

   private boolean requiresGameRestart() {
      return this.getAnnotation().gameRestart();
   }

   private boolean requiresWorldRestart() {
      return this.getAnnotation().worldRestart();
   }

   public abstract ConfigValue<T> getConfigValue(Builder var1, @Nullable Object var2);

   private Consumer<T> getValueCallback(ConfigValue<T> configValue, @Nullable Object o) {
      try {
         MethodHandle methodHandle = MethodHandles.lookup().unreflectSetter(this.field);
         return value -> {
            try {
               methodHandle.invoke((Object)o, (Object)configValue.get());
            } catch (Throwable var5) {
               throw new RuntimeException(var5);
            }
         };
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static final class BooleanEntry extends ValueEntry<Boolean> {
      public BooleanEntry(Field field) {
         super(field);
      }

      public BooleanValue getConfigValue(Builder builder, @Nullable Object o) {
         return builder.define(this.getName(), (Boolean)this.getDefaultValue(o));
      }
   }
}
