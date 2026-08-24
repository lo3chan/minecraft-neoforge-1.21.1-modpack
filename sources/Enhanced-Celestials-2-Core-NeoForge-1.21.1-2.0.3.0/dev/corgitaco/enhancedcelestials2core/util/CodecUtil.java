package dev.corgitaco.enhancedcelestials2core.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.core.EC2Constants;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Style.Serializer;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;

public class CodecUtil {
   public static final Codec<MobEffect> MOB_EFFECT = createLoggedExceptionRegistryCodec(BuiltInRegistries.MOB_EFFECT);
   public static final Codec<EntityType<?>> ENTITY_TYPE = createLoggedExceptionRegistryCodec(BuiltInRegistries.ENTITY_TYPE);
   public static final Codec<Action> CLICK_EVENT_ACTION_CODEC = Codec.STRING.comapFlatMap(s -> {
      try {
         return DataResult.success(Action.valueOf(s));
      } catch (Exception var2) {
         EC2Constants.LOGGER.error(var2.getMessage());
         return DataResult.error(var2::getMessage);
      }
   }, Enum::name);
   public static final Codec<ClickEvent> CLICK_EVENT_CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            CLICK_EVENT_ACTION_CODEC.fieldOf("action").forGetter(ClickEvent::getAction), Codec.STRING.fieldOf("value").forGetter(ClickEvent::getValue)
         )
         .apply(builder, ClickEvent::new)
   );
   public static final Codec<Style> STYLE_CODEC = Serializer.CODEC;

   public static <E> Codec<Holder<E>> networkSafeHolder(final ResourceKey<? extends Registry<E>> registryKey, Codec<E> elementCodec) {
      final Codec<Holder<E>> byIdOrInline = RegistryFileCodec.create(registryKey, elementCodec);
      final Codec<Holder<E>> inlineOnly = elementCodec.xmap(Holder::direct, Holder::value);
      return new Codec<Holder<E>>() {
         public <T> DataResult<T> encode(Holder<E> input, DynamicOps<T> ops, T prefix) {
            return byIdOrInline.encode(input, ops, prefix);
         }

         public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> ops, T input) {
            return ops instanceof RegistryOps<?> registryOps && registryOps.getter(registryKey).isEmpty()
               ? inlineOnly.decode(ops, input)
               : byIdOrInline.decode(ops, input);
         }
      };
   }

   public static <T> Codec<T> createLoggedExceptionRegistryCodec(Registry<T> registry) {
      return ResourceLocation.CODEC
         .comapFlatMap(
            location -> {
               Optional<T> result = registry.getOptional(location);
               if (!result.isEmpty()) {
                  return DataResult.success(result.get());
               } else {
                  StringBuilder registryElements = new StringBuilder();

                  for (int i = 0; i < registry.entrySet().size(); i++) {
                     T object = (T)registry.byId(i);
                     registryElements.append(i).append(". \"").append(registry.getKey(object).toString()).append("\"\n");
                  }

                  return DataResult.error(
                     () -> String.format(
                        "\"%s\" is not a valid entityType in registry: %s.\nCurrent Registry Values:\n\n%s\n",
                        location.toString(),
                        registry.toString(),
                        registryElements.toString()
                     )
                  );
               }
            },
            registry::getKey
         );
   }

   public record LazyCodec<TYPE>(Supplier<Codec<TYPE>> delegate) implements Codec<TYPE> {
      public <T> DataResult<T> encode(TYPE input, DynamicOps<T> ops, T prefix) {
         return this.delegate().get().encode(input, ops, prefix);
      }

      public <T> DataResult<Pair<TYPE, T>> decode(DynamicOps<T> ops, T input) {
         return this.delegate().get().decode(ops, input);
      }
   }
}
