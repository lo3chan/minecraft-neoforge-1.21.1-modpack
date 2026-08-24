package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EventTargetType<T> {
   public static final EventTargetType<String> STRING = create(String.class).transformer(EventTargetType::toString).describeType(TypeInfo.STRING);
   public static final EventTargetType<ResourceLocation> ID = create(ResourceLocation.class)
      .transformer(EventTargetType::toResourceLocation)
      .describeType(TypeInfo.of(ResourceLocation.class));
   public static final EventTargetType<ResourceKey<Registry<?>>> REGISTRY = Cast.to(
      create(ResourceKey.class)
         .transformer(EventTargetType::toRegistryKey)
         .identity()
         .describeType(TypeInfo.of(ResourceKey.class).withParams(new TypeInfo[]{TypeInfo.of(Registry.class)}))
   );
   public final Class<T> type;
   public EventTargetType.Transformer transformer;
   public boolean identity;
   public Predicate<Object> validator;
   public EventTargetType.Transformer toString;
   public TypeInfo describeType;

   public static <T> EventTargetType<T> create(Class<T> type) {
      return new EventTargetType<>(type);
   }

   public static <T> EventTargetType<ResourceKey<T>> registryKey(ResourceKey<Registry<T>> registry, Class<?> type) {
      return Cast.to(
         create(ResourceKey.class)
            .identity()
            .transformer(o -> toKey(registry, o))
            .describeType(TypeInfo.of(ResourceKey.class).withParams(new TypeInfo[]{TypeInfo.of(type)}))
      );
   }

   public static <T extends Enum<T>> EventTargetType<T> fromEnum(Class<T> type) {
      EnumTypeInfo typeInfo = (EnumTypeInfo)TypeInfo.of(type);
      return create(type).transformer(o -> {
         if (o == null) {
            return null;
         } else if (type.isInstance(o)) {
            return o;
         } else if (o instanceof CharSequence) {
            String s = o.toString();
            if (s.isEmpty()) {
               return null;
            } else {
               for (Object entry : typeInfo.enumConstants()) {
                  if (EnumTypeInfo.getName(entry).equalsIgnoreCase(s)) {
                     return entry;
                  }
               }

               return null;
            }
         } else {
            return null;
         }
      }).describeType(typeInfo);
   }

   private static String toString(Object object) {
      if (object == null) {
         return null;
      } else {
         String s = object.toString();
         return s.isBlank() ? null : s;
      }
   }

   private static ResourceLocation toResourceLocation(Object object) {
      if (object == null) {
         return null;
      } else if (object instanceof ResourceLocation rl) {
         return rl;
      } else {
         String s = object.toString();
         return s.isBlank() ? null : ResourceLocation.tryParse(s);
      }
   }

   private static ResourceKey<?> toKey(ResourceKey registry, Object object) {
      return switch (object) {
         case null -> null;
         case ResourceKey<?> rl -> rl;
         case RegistryObjectKJS<?> wrk -> wrk.kjs$getKey();
         case ResourceLocation rlx -> ResourceKey.create(registry, rlx);
         default -> {
            String s = object.toString();
            yield s.isBlank() ? null : ResourceKey.create(registry, ResourceLocation.parse(s));
         }
      };
   }

   private static ResourceKey<? extends Registry<?>> toRegistryKey(Object object) {
      return switch (object) {
         case null -> null;
         case ResourceKey rl -> rl;
         case ResourceLocation rlx -> ResourceKey.createRegistryKey(rlx);
         default -> {
            String s = object.toString();
            yield s.isBlank() ? null : ResourceKey.createRegistryKey(ResourceLocation.parse(s));
         }
      };
   }

   private EventTargetType(Class<T> type) {
      this.type = type;
      this.transformer = EventTargetType.Transformer.IDENTITY;
      this.identity = false;
      this.validator = UtilsJS.ALWAYS_TRUE;
      this.toString = EventTargetType.Transformer.IDENTITY;
      this.describeType = TypeInfo.STRING;
   }

   public EventTargetType<T> transformer(EventTargetType.Transformer factory) {
      this.transformer = factory;
      return this;
   }

   public EventTargetType<T> identity() {
      this.identity = true;
      return this;
   }

   public EventTargetType<T> validator(Predicate<Object> validator) {
      this.validator = validator;
      return this;
   }

   public EventTargetType<T> describeType(TypeInfo describeType) {
      this.describeType = describeType;
      return this;
   }

   public EventTargetType<T> toString(EventTargetType.Transformer factory) {
      this.toString = factory;
      return this;
   }

   @FunctionalInterface
   public interface Transformer {
      EventTargetType.Transformer IDENTITY = o -> o;

      @Nullable
      Object transform(Object source);
   }
}
