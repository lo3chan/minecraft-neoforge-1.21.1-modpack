package dev.shadowsoffire.placebo.systems.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.json.WeightedItemStack;
import dev.shadowsoffire.placebo.reload.WeightedDynamicRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public record GearSet(
   int weight,
   float quality,
   List<WeightedItemStack> mainhands,
   List<WeightedItemStack> offhands,
   List<WeightedItemStack> boots,
   List<WeightedItemStack> leggings,
   List<WeightedItemStack> chestplates,
   List<WeightedItemStack> helmets,
   Set<String> tags
) implements CodecProvider<GearSet>, WeightedDynamicRegistry.ILuckyWeighted {
   public static EquipmentSlot[] VALID_SLOTS = new EquipmentSlot[]{
      EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
   };
   public static final Codec<GearSet> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            Codec.intRange(0, 2147483647).fieldOf("weight").forGetter(WeightedDynamicRegistry.ILuckyWeighted::getWeight),
            Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("quality", 0.0F).forGetter(WeightedDynamicRegistry.ILuckyWeighted::getQuality),
            WeightedItemStack.LIST_CODEC.optionalFieldOf("mainhands", Collections.emptyList()).forGetter(GearSet::mainhands),
            WeightedItemStack.LIST_CODEC.optionalFieldOf("offhands", Collections.emptyList()).forGetter(GearSet::offhands),
            WeightedItemStack.LIST_CODEC.optionalFieldOf("boots", Collections.emptyList()).forGetter(GearSet::boots),
            WeightedItemStack.LIST_CODEC.optionalFieldOf("leggings", Collections.emptyList()).forGetter(GearSet::leggings),
            WeightedItemStack.LIST_CODEC.optionalFieldOf("chestplates", Collections.emptyList()).forGetter(GearSet::chestplates),
            WeightedItemStack.LIST_CODEC.optionalFieldOf("helmets", Collections.emptyList()).forGetter(GearSet::helmets),
            PlaceboCodecs.setOf(Codec.STRING).fieldOf("tags").forGetter(GearSet::tags)
         )
         .apply(inst, GearSet::new)
   );

   @Override
   public int getWeight() {
      return this.weight;
   }

   @Override
   public float getQuality() {
      return this.quality;
   }

   public LivingEntity apply(LivingEntity entity) {
      for (EquipmentSlot slot : VALID_SLOTS) {
         WeightedRandom.getRandomItem(entity.getRandom(), this.getPotentials(slot)).ifPresent(s -> s.apply(entity, slot));
      }

      return entity;
   }

   public List<WeightedItemStack> getPotentials(EquipmentSlot slot) {
      return switch (slot) {
         case MAINHAND -> this.mainhands;
         case OFFHAND -> this.offhands;
         case FEET -> this.boots;
         case LEGS -> this.leggings;
         case CHEST -> this.chestplates;
         case HEAD -> this.helmets;
         case BODY -> throw new UnsupportedOperationException("Invalid slot type: " + slot);
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   public Codec<? extends GearSet> getCodec() {
      return CODEC;
   }

   public static class SetPredicate implements Predicate<GearSet> {
      public static final Codec<GearSet.SetPredicate> CODEC = Codec.stringResolver(s -> s.key, GearSet.SetPredicate::new);
      protected final String key;
      protected final Predicate<GearSet> internal;

      public SetPredicate(String key) {
         this.key = key;
         if (key.startsWith("#")) {
            String tag = key.substring(1);
            this.internal = t -> t.tags.contains(tag);
         } else {
            ResourceLocation id = ResourceLocation.parse(key);
            this.internal = t -> GearSetRegistry.INSTANCE.getKey(t).equals(id);
         }
      }

      public boolean test(GearSet t) {
         return this.internal.test(t);
      }

      @Override
      public String toString() {
         return "SetPredicate[" + this.key + "]";
      }
   }
}
