package corgitaco.corgilib.serialization.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class CodecUtil {
   public static final Codec<Block> BLOCK_CODEC = createLoggedExceptionRegistryCodec(BuiltInRegistries.BLOCK);
   public static final Codec<MobEffect> MOB_EFFECT = createLoggedExceptionRegistryCodec(BuiltInRegistries.MOB_EFFECT);
   public static final Codec<EntityType<?>> ENTITY_TYPE = createLoggedExceptionRegistryCodec(BuiltInRegistries.ENTITY_TYPE);
   public static final Codec<EntityType<?>> ENTITY_TYPE_CODEC = createLoggedExceptionRegistryCodec(BuiltInRegistries.ENTITY_TYPE);
   public static final Codec<Attribute> ATTRIBUTE_CODEC = createLoggedExceptionRegistryCodec(BuiltInRegistries.ATTRIBUTE);
   public static final Codec<Item> ITEM_CODEC = createLoggedExceptionRegistryCodec(BuiltInRegistries.ITEM);
   public static final Codec<MobEffect> EFFECT_CODEC = createLoggedExceptionRegistryCodec(BuiltInRegistries.MOB_EFFECT);
   public static final Codec<ResourceKey<Biome>> BIOME_CODEC = ResourceLocation.CODEC
      .comapFlatMap(resourceLocation -> DataResult.success(ResourceKey.create(Registries.BIOME, resourceLocation)), ResourceKey::location);
   public static final Codec<EquipmentSlot> EQUIPMENT_SLOT_CODEC = Codec.STRING
      .comapFlatMap(
         s -> {
            EquipmentSlot equipmentSlotType = EquipmentSlot.byName(s.toLowerCase());
            if (equipmentSlotType == null) {
               throw new IllegalArgumentException(
                  String.format(
                     "\"%s\" is not a valid equipmentSlotType. Valid equipmentSlotTypes: %s",
                     s,
                     Arrays.toString(Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::getName).toArray())
                  )
               );
            } else {
               return DataResult.success(equipmentSlotType);
            }
         },
         EquipmentSlot::getName
      );
   public static final Codec<Difficulty> DIFFICULTY_CODEC = Codec.STRING
      .comapFlatMap(
         s -> {
            Difficulty difficulty = Difficulty.byName(s.toLowerCase());
            if (difficulty == null) {
               throw new IllegalArgumentException(
                  String.format(
                     "\"%s\" is not a valid difficulty. Valid difficulties: %s",
                     s,
                     Arrays.toString(Arrays.stream(Difficulty.values()).map(Difficulty::getKey).toArray())
                  )
               );
            } else {
               return DataResult.success(difficulty);
            }
         },
         Difficulty::getKey
      );
   public static final Codec<Action> CLICK_EVENT_ACTION_CODEC = Codec.STRING.comapFlatMap(s -> {
      try {
         return DataResult.success(Action.valueOf(s));
      } catch (Exception var2) {
         return DataResult.error(var2::getMessage);
      }
   }, Enum::name);
   public static final Codec<ClickEvent> CLICK_EVENT_CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            CLICK_EVENT_ACTION_CODEC.fieldOf("action").forGetter(ClickEvent::getAction), Codec.STRING.fieldOf("value").forGetter(ClickEvent::getValue)
         )
         .apply(builder, ClickEvent::new)
   );
   public static Codec<Integer> COLOR_FROM_HEX = Codec.STRING.comapFlatMap(validateColorHex(), Integer::toHexString);
   public static final Codec<Integer> INTEGER_KEY_CODEC = Codec.STRING.comapFlatMap(s -> DataResult.success(Integer.valueOf(s)), Object::toString);

   public static Function<String, DataResult<Integer>> validateColorHex() {
      return input -> {
         if (input.isEmpty()) {
            return DataResult.success(ChatFormatting.WHITE.getColor());
         } else {
            try {
               return DataResult.success((int)Long.parseLong(input.replace("#", "").replace("0x", ""), 16));
            } catch (NumberFormatException var2) {
               var2.printStackTrace();
               return DataResult.error(var2::getMessage);
            }
         }
      };
   }

   public static Codec<Integer> intKeyRangeCodec(int min, int max) {
      Function<Integer, DataResult<Integer>> check = Codec.checkRange(min, max);
      return INTEGER_KEY_CODEC.flatXmap(check, check);
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
                        "\"%s\" is not a valid id in registry: %s.\nCurrent Registry Values:\n\n%s\n", location.toString(), registry, registryElements
                     )
                  );
               }
            },
            registry::getKey
         );
   }

   public static <T> Codec<CodecUtil.WrapForSerialization<T>> wrapCodecForCollectionSerializing(Codec<T> codec) {
      return RecordCodecBuilder.create(
         builder -> builder.group(codec.fieldOf("value").forGetter(tWrapForSerialization -> tWrapForSerialization.value))
            .apply(builder, CodecUtil.WrapForSerialization::new)
      );
   }

   public static <T> CodecUtil.WrapForSerialization<T> wrap(T toWrap) {
      return new CodecUtil.WrapForSerialization<>(toWrap);
   }

   public record LazyCodec<TYPE>(Supplier<Codec<TYPE>> delegate) implements Codec<TYPE> {
      public <T> DataResult<T> encode(TYPE input, DynamicOps<T> ops, T prefix) {
         return this.delegate().get().encode(input, ops, prefix);
      }

      public <T> DataResult<Pair<TYPE, T>> decode(DynamicOps<T> ops, T input) {
         return this.delegate().get().decode(ops, input);
      }
   }

   public record WrapForSerialization<T>(T value) {
   }
}
