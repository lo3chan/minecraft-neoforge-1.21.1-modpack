package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.util.Description;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record DropSettings(List<Pair<Integer, Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>>> dropEnhancer) implements Description {
   public static final DropSettings EMPTY = new DropSettings(List.of());
   public static final int LEGACY_MINIMUM_COUNT = 2;
   public static final Codec<Either<TagKey<Item>, ResourceKey<Item>>> TARGET_CODEC = Codec.either(
      TagKey.hashedCodec(Registries.ITEM), ResourceKey.codec(Registries.ITEM)
   );
   public static final Codec<Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>> MULTIPLIERS_CODEC = Codec.unboundedMap(TARGET_CODEC, Codec.DOUBLE);
   public static final Codec<Pair<Integer, Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>>> DROP_ENHANCER_ENTRY_CODEC = Codec.pair(
      Codec.intRange(1, 2147483647).fieldOf("minimum_count").codec(), MULTIPLIERS_CODEC.fieldOf("multipliers").codec()
   );
   public static final Codec<List<Pair<Integer, Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>>>> LEGACY_DROP_ENHANCER_CODEC = MULTIPLIERS_CODEC.xmap(
      multipliers -> List.of(Pair.of(2, multipliers)), groups -> groups.isEmpty() ? Map.of() : (Map)((Pair)groups.getFirst()).getSecond()
   );
   public static final Codec<List<Pair<Integer, Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>>>> DROP_ENHANCER_CODEC = Codec.withAlternative(
      DROP_ENHANCER_ENTRY_CODEC.listOf(), LEGACY_DROP_ENHANCER_CODEC
   );
   public static Codec<DropSettings> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(DROP_ENHANCER_CODEC.fieldOf("drop_enhancer").forGetter(dropSettings -> dropSettings.dropEnhancer))
         .apply(builder, DropSettings::new)
   );

   public static boolean matches(ItemStack stack, Either<TagKey<Item>, ResourceKey<Item>> target) {
      return (Boolean)target.map(stack::is, itemKey -> stack.getItemHolder().is(itemKey));
   }

   public static String targetName(Either<TagKey<Item>, ResourceKey<Item>> target) {
      return (String)target.map(tag -> "#" + tag.location(), itemKey -> itemKey.location().toString());
   }

   @Override
   public Component description() {
      if (this.dropEnhancer.isEmpty()) {
         return Component.translatable("enhancedcelestials2core.description.none");
      } else {
         MutableComponent result = null;

         for (Pair<Integer, Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>> group : this.dropEnhancer) {
            MutableComponent multipliers = null;

            for (Entry<Either<TagKey<Item>, ResourceKey<Item>>, Double> entry : ((Map)group.getSecond()).entrySet()) {
               Component entryComponent = Component.translatable(
                  "enhancedcelestials2core.drop_settings.entry", new Object[]{targetName(entry.getKey()), entry.getValue()}
               );
               multipliers = multipliers == null ? entryComponent.copy() : multipliers.append(", ").append(entryComponent);
            }

            if (multipliers != null) {
               Component groupComponent = Component.translatable("enhancedcelestials2core.drop_settings.group", new Object[]{multipliers, group.getFirst()});
               result = result == null ? groupComponent.copy() : result.append(", ").append(groupComponent);
            }
         }

         return result == null ? Component.translatable("enhancedcelestials2core.description.none") : result;
      }
   }
}
