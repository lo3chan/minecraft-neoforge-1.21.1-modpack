package io.wispforest.owo.config;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.util.Observable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public final class Option<T> {
   private final String configName;
   private final Option.Key key;
   private final String translationKey;
   private final T defaultValue;
   private final Observable<T> mirror;
   private final Option.BoundField<T> backingField;
   private final Class<T> clazz;
   @Nullable
   private final ConfigWrapper.Constraint constraint;
   @Nullable
   private final Endec<T> endec;
   private final Option.SyncMode syncMode;
   private boolean detached = false;

   public Option(
      String configName,
      Option.Key key,
      T defaultValue,
      Observable<T> mirror,
      Option.BoundField<T> backingField,
      @Nullable ConfigWrapper.Constraint constraint,
      Option.SyncMode syncMode,
      ReflectiveEndecBuilder builder
   ) {
      this.configName = configName;
      this.key = key;
      this.translationKey = "text.config." + this.configName + ".option." + this.key.asString();
      this.defaultValue = defaultValue;
      this.mirror = mirror;
      this.backingField = backingField;
      this.clazz = (Class<T>)backingField.field().getType();
      this.constraint = constraint;
      this.syncMode = syncMode;
      this.endec = syncMode.isNone() ? null : builder.get(this.backingField.field.getGenericType());
   }

   public void set(T value) {
      if (!this.detached) {
         if (this.verifyConstraint(value)) {
            this.backingField.setValue(value);
            this.mirror.set(value);
         }
      }
   }

   public T value() {
      return this.mirror.get();
   }

   public Class<T> clazz() {
      return this.clazz;
   }

   public void synchronizeWithBackingField() {
      if (!this.detached) {
         T fieldValue = this.backingField.getValue();
         if (this.verifyConstraint(fieldValue)) {
            this.mirror.set(fieldValue);
         } else {
            this.backingField.setValue(this.mirror.get());
         }
      }
   }

   public boolean verifyConstraint(T value) {
      if (this.constraint == null) {
         return true;
      } else {
         boolean matched = this.constraint.test(value);
         if (!matched) {
            Owo.LOGGER
               .warn(
                  "Option {} in config '{}' could not be updated, as the given value '{}' does not match its constraint: {}",
                  this.key,
                  this.configName,
                  value,
                  this.constraint.formatted()
               );
         }

         return matched;
      }
   }

   public void observe(Consumer<T> observer) {
      this.mirror.observe(observer);
   }

   void write(FriendlyByteBuf buf) {
      buf.write(this.endec, this.value());
   }

   T read(FriendlyByteBuf buf) {
      T newValue = (T)buf.read(this.endec);
      if (!Objects.equals(newValue, this.value()) && this.backingField.hasAnnotation(RestartRequired.class)) {
         return newValue;
      } else {
         this.mirror.set(newValue);
         this.detached = true;
         return null;
      }
   }

   Endec<T> endec() {
      return this.endec;
   }

   void reattach() {
      if (this.detached) {
         this.detached = false;
         this.synchronizeWithBackingField();
      }
   }

   public String translationKey() {
      return this.translationKey;
   }

   public String configName() {
      return this.configName;
   }

   public Option.Key key() {
      return this.key;
   }

   public T defaultValue() {
      return this.defaultValue;
   }

   public Option.BoundField<T> backingField() {
      return this.backingField;
   }

   @Nullable
   public ConfigWrapper.Constraint constraint() {
      return this.constraint;
   }

   public boolean detached() {
      return this.detached;
   }

   public Option.SyncMode syncMode() {
      return this.syncMode;
   }

   @Override
   public String toString() {
      return "Option[configName="
         + this.configName
         + ", key="
         + this.key
         + ", defaultValue="
         + this.defaultValue
         + ", constraint="
         + (this.constraint == null ? null : this.constraint.formatted())
         + "]";
   }

   public record BoundField<T>(Object owner, Field field) {
      public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
         return this.field.isAnnotationPresent(annotationClass);
      }

      public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
         return this.field.getAnnotation(annotationClass);
      }

      public T getValue() {
         try {
            return (T)this.field.get(this.owner);
         } catch (IllegalAccessException var2) {
            throw new RuntimeException("Could not access config option field " + this.field.getName(), var2);
         }
      }

      public void setValue(T value) {
         try {
            this.field.set(this.owner, value);
         } catch (IllegalAccessException var3) {
            throw new RuntimeException("Could not set config option field " + this.field.getName(), var3);
         }
      }
   }

   public record Key(String[] path) {
      public static final Option.Key ROOT = new Option.Key(new String[0]);

      public Key(List<String> path) {
         this(path.toArray(String[]::new));
      }

      public Key(String key) {
         this(key.split("\\."));
      }

      public Option.Key parent() {
         if (this.path.length <= 1) {
            return ROOT;
         } else {
            String[] newPath = new String[this.path.length - 1];
            System.arraycopy(this.path, 0, newPath, 0, this.path.length - 1);
            return new Option.Key(newPath);
         }
      }

      public Option.Key child(String childName) {
         String[] newPath = new String[this.path.length + 1];
         System.arraycopy(this.path, 0, newPath, 0, this.path.length);
         newPath[this.path.length] = childName;
         return new Option.Key(newPath);
      }

      public String asString() {
         return String.join(".", this.path);
      }

      public String name() {
         return this.path.length < 1 ? "" : this.path[this.path.length - 1];
      }

      public boolean isRoot() {
         return this == ROOT;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Option.Key key = (Option.Key)o;
            return Arrays.equals((Object[])this.path, (Object[])key.path);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode((Object[])this.path);
      }

      @Override
      public String toString() {
         return "Key{path=" + Arrays.toString((Object[])this.path) + "}";
      }
   }

   public static enum SyncMode {
      NONE,
      INFORM_SERVER,
      OVERRIDE_CLIENT;

      public boolean isNone() {
         return this == NONE;
      }
   }
}
