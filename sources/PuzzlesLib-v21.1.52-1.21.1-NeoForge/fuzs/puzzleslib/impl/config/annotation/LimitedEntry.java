package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.base.Predicates;
import fuzs.puzzleslib.api.config.v3.Config;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.runtime.SwitchBootstraps;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import org.jetbrains.annotations.Nullable;

public abstract class LimitedEntry<T> extends ValueEntry<T> {
   public LimitedEntry(Field field) {
      super(field);
   }

   private Set<String> getAllowedValues() {
      Config.AllowedValues allowedValues = this.field.getDeclaredAnnotation(Config.AllowedValues.class);
      return (Set<String>)(allowedValues != null && allowedValues.values().length != 0
         ? new LinkedHashSet<>(Arrays.asList(allowedValues.values()))
         : this.getAllValues());
   }

   Set<String> getAllValues() {
      return Collections.emptySet();
   }

   public final Set<String> getAllowedValueStrings() {
      Set<String> allowedValues = this.getAllowedValues();
      if (!allowedValues.isEmpty()) {
         Set<String> allValues = this.getAllValues();
         if (!allValues.isEmpty()) {
            for (String s : allowedValues) {
               if (!allValues.contains(s)) {
                  throw new IllegalArgumentException(s + " is not contained in " + allValues);
               }
            }
         }
      }

      return allowedValues;
   }

   @Override
   public List<String> getComments(@Nullable Object o) {
      List<String> comments = super.getComments(o);
      this.addAllowedValuesComment(comments);
      return comments;
   }

   public void addAllowedValuesComment(List<String> comments) {
      Set<String> allowedValues = this.getAllowedValueStrings();
      if (!allowedValues.isEmpty()) {
         comments.add("Allowed Values: " + String.join(", ", allowedValues));
      }
   }

   public Predicate<Object> getValidator() {
      Set<String> allowedValues = this.getAllowedValueStrings();
      return !allowedValues.isEmpty() ? o -> {
         if (o != null) {
            String string = o instanceof Enum ? ((Enum)o).name() : o.toString();
            return allowedValues.contains(string);
         } else {
            return false;
         }
      } : this.getEmptyValidator();
   }

   public Predicate<Object> getEmptyValidator() {
      return Predicates.alwaysTrue();
   }

   public static final class EnumEntry<E extends Enum<E>> extends LimitedEntry<E> {
      public EnumEntry(Field field) {
         super(field);
      }

      protected String getValueString(E value) {
         return value.name();
      }

      public EnumValue<E> getConfigValue(Builder builder, @Nullable Object o) {
         return builder.defineEnum(this.getName(), this.getDefaultValue(o), this.getValidator());
      }

      @Override
      Set<String> getAllValues() {
         return Arrays.stream(this.field.getType().getEnumConstants())
            .map(value -> (Enum)value)
            .map(this::getValueString)
            .collect(Collectors.toCollection(LinkedHashSet::new));
      }

      @Override
      public void addAllowedValuesComment(List<String> comments) {
      }
   }

   public static final class ListEntry extends LimitedEntry<List<?>> {
      public ListEntry(Field field) {
         super(field);
      }

      @Nullable
      public Type getListType() {
         return this.field.getGenericType() instanceof ParameterizedType type && type.getActualTypeArguments().length > 0
            ? type.getActualTypeArguments()[0]
            : null;
      }

      @Nullable
      private Class<Enum<?>> getEnumType() {
         return (Class<Enum<?>>)(this.getListType() instanceof Class<?> clazz && clazz.isEnum() ? clazz : null);
      }

      protected String getValueString(List<?> value) {
         Class<Enum<?>> clazz = this.getEnumType();
         return clazz != null ? value.stream().map(Enum.class::cast).map(Enum::name).toList().toString() : super.getValueString(value);
      }

      @Override
      public ConfigValue<List<?>> getConfigValue(Builder builder, @Nullable Object o) {
         Supplier<?> elementSupplier = this.getElementSupplier(this.getListType());
         return builder.defineListAllowEmpty(this.getName(), (List)this.getDefaultValue(o), elementSupplier, this.getValidator());
      }

      @Override
      public Set<String> getAllValues() {
         Class<Enum<?>> clazz = this.getEnumType();
         return clazz != null
            ? Arrays.stream(clazz.getEnumConstants()).map(Enum.class::cast).map(Enum::name).collect(Collectors.toCollection(LinkedHashSet::new))
            : super.getAllValues();
      }

      private Supplier<?> getElementSupplier(Type type) {
         Objects.requireNonNull(type, "type is null");
         return () -> {
            Objects.requireNonNull(type);
            Type selector0$temp = type;
            int index$1 = 0;

            while (true) {
               Object var10000;
               switch (SwitchBootstraps.typeSwitch<"typeSwitch",Class,Class,Class,Class,Class,Class>(selector0$temp, index$1)) {
                  case 0:
                     Class<?> clazz = (Class<?>)selector0$temp;
                     if (clazz != String.class) {
                        index$1 = 1;
                        continue;
                     }

                     var10000 = "";
                     break;
                  case 1:
                     Class<?> clazz = (Class<?>)selector0$temp;
                     if (!clazz.isEnum()) {
                        index$1 = 2;
                        continue;
                     }

                     var10000 = clazz.getEnumConstants()[0];
                     break;
                  case 2:
                     Class<?> clazz = (Class<?>)selector0$temp;
                     if (clazz != Boolean.class) {
                        index$1 = 3;
                        continue;
                     }

                     var10000 = false;
                     break;
                  case 3:
                     Class<?> clazz = (Class<?>)selector0$temp;
                     if (clazz != Integer.class) {
                        index$1 = 4;
                        continue;
                     }

                     var10000 = 0;
                     break;
                  case 4:
                     Class<?> clazz = (Class<?>)selector0$temp;
                     if (clazz != Long.class) {
                        index$1 = 5;
                        continue;
                     }

                     var10000 = 0L;
                     break;
                  case 5:
                     Class<?> clazz = (Class<?>)selector0$temp;
                     if (clazz != Double.class) {
                        index$1 = 6;
                        continue;
                     }

                     var10000 = 0.0;
                     break;
                  default:
                     throw new IllegalArgumentException("Unsupported list type: " + type);
               }

               return var10000;
            }
         };
      }
   }

   public static final class StringEntry extends LimitedEntry<String> {
      public StringEntry(Field field) {
         super(field);
      }

      @Override
      public ConfigValue<String> getConfigValue(Builder builder, @Nullable Object o) {
         return builder.define(this.getName(), (String)this.getDefaultValue(o), this.getValidator());
      }

      @Override
      public Predicate<Object> getEmptyValidator() {
         return String.class::isInstance;
      }
   }
}
