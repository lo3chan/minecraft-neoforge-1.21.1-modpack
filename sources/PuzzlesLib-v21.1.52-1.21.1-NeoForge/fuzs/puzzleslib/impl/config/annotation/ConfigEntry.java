package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.base.CaseFormat;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigEntry<T> {
   final Field field;

   public ConfigEntry(Field field) {
      this.field = field;
   }

   public String getName() {
      return StringUtils.isBlank(this.getAnnotation().name())
         ? CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.field.getName())
         : this.getAnnotation().name();
   }

   public List<String> getComments(@Nullable Object o) {
      return new ArrayList<>(Arrays.asList(this.getAnnotation().description()));
   }

   public T getDefaultValue(@Nullable Object o) {
      try {
         return (T)this.field.get(o);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public Config getAnnotation() {
      return this.field.getAnnotation(Config.class);
   }

   public abstract void defineValue(Builder var1, ConfigDataHolderImpl<?> var2, @Nullable Object var3);

   public static final class ChildEntry extends ConfigEntry<ConfigCore> {
      public ChildEntry(Field field) {
         super(field);
      }

      @Override
      public void defineValue(Builder builder, ConfigDataHolderImpl<?> context, @Nullable Object o) {
         List<String> comments = this.getComments(o);
         builder.comment(comments.toArray(String[]::new));
         builder.push(this.getName());
         ConfigBuilder.build(builder, context, this.field.getType(), (ConfigCore)this.getDefaultValue(o));
         builder.pop();
      }
   }
}
