package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.entity.condition.Condition;
import corgitaco.corgilib.entity.condition.ConditionContext;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.util.CodecUtil;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public final class MobEquipment {
   private MobEquipment() {
   }

   public static void apply(Mob mob, List<MobEquipment.EquipmentCombination> combinations) {
      RandomSource random = mob.level().getRandom();

      for (MobEquipment.EquipmentCombination combination : combinations) {
         if (combination.filter().passes(new ConditionContext(mob.level(), mob, mob.isDeadOrDying(), 0))
            && (!(combination.chance() < 1.0F) || !(random.nextFloat() >= combination.chance()))) {
            combination.equipment()
               .getRandomValue(random)
               .ifPresent(equipmentSet -> ((EquipmentSet)equipmentSet.value()).equipment().forEach((slot, entry) -> mob.setItemSlot(slot, entry.roll(random))));
         }
      }
   }

   public static boolean canEquip(EquipmentSlot slot, ItemStack stack) {
      if (slot.getType() == Type.HAND) {
         return true;
      } else {
         Equipable equipable = Equipable.get(stack);
         return equipable != null && equipable.getEquipmentSlot() == slot;
      }
   }

   public static Component describe(String translationKey, List<MobEquipment.EquipmentCombination> combinations) {
      if (combinations.isEmpty()) {
         return Component.translatable(translationKey, new Object[]{Component.translatable("enhancedcelestials2core.description.none")});
      } else {
         MutableComponent joined = null;

         for (MobEquipment.EquipmentCombination combination : combinations) {
            Component entryComponent = Component.translatable(
               "enhancedcelestials2core.lunar_event_modifier.mob_equipment.entry",
               new Object[]{describeEquipment(combination.equipment()), combination.filter().toString(), Math.round(combination.chance() * 100.0F)}
            );
            joined = joined == null ? entryComponent.copy() : joined.append(", ").append(entryComponent);
         }

         return Component.translatable(translationKey, new Object[]{joined});
      }
   }

   private static Component describeEquipment(SimpleWeightedRandomList<Holder<EquipmentSet>> equipment) {
      if (equipment.isEmpty()) {
         return Component.translatable("enhancedcelestials2core.description.none");
      } else {
         MutableComponent joined = null;

         for (Wrapper<Holder<EquipmentSet>> wrapper : equipment.unwrap()) {
            Component setComponent = Component.translatable(
               "enhancedcelestials2core.lunar_event_modifier.mob_equipment.set",
               new Object[]{describeSlots(((EquipmentSet)((Holder)wrapper.data()).value()).equipment()), wrapper.weight().asInt()}
            );
            joined = joined == null ? setComponent.copy() : joined.append(", ").append(setComponent);
         }

         return joined;
      }
   }

   private static Component describeSlots(EnumMap<EquipmentSlot, MobEquipment.EquipmentEntry> slots) {
      if (slots.isEmpty()) {
         return Component.translatable("enhancedcelestials2core.description.none");
      } else {
         MutableComponent joined = null;

         for (Entry<EquipmentSlot, MobEquipment.EquipmentEntry> entry : slots.entrySet()) {
            MobEquipment.EquipmentEntry equipmentEntry = entry.getValue();
            Component slotComponent = Component.translatable(
               "enhancedcelestials2core.lunar_event_modifier.mob_equipment.slot",
               new Object[]{entry.getKey().getName(), equipmentEntry.item().getHoverName(), describeCount(equipmentEntry.count())}
            );
            joined = joined == null ? slotComponent.copy() : joined.append(", ").append(slotComponent);
         }

         return joined;
      }
   }

   private static String describeCount(IntProvider count) {
      if (count instanceof ConstantInt constant) {
         return Integer.toString(constant.getValue());
      } else {
         return count instanceof UniformInt uniform ? uniform.getMinValue() + "-" + uniform.getMaxValue() : count.getMinValue() + "-" + count.getMaxValue();
      }
   }

   public record EquipmentCombination(Condition filter, float chance, SimpleWeightedRandomList<Holder<EquipmentSet>> equipment) {
      public static final Codec<MobEquipment.EquipmentCombination> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               Condition.CODEC.fieldOf("filter").forGetter(MobEquipment.EquipmentCombination::filter),
               Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 1.0F).forGetter(MobEquipment.EquipmentCombination::chance),
               SimpleWeightedRandomList.wrappedCodecAllowingEmpty(
                     CodecUtil.networkSafeHolder(EnhancedCelestialsRegistry.EQUIPMENT_SET_KEY, EquipmentSet.DIRECT_CODEC)
                  )
                  .fieldOf("equipment")
                  .forGetter(MobEquipment.EquipmentCombination::equipment)
            )
            .apply(builder, MobEquipment.EquipmentCombination::new)
      );
   }

   public record EquipmentEntry(ItemStack item, IntProvider count) {
      public static final Codec<MobEquipment.EquipmentEntry> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               ItemStack.CODEC.fieldOf("item").forGetter(MobEquipment.EquipmentEntry::item),
               IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("count", ConstantInt.of(1)).forGetter(MobEquipment.EquipmentEntry::count)
            )
            .apply(builder, MobEquipment.EquipmentEntry::new)
      );

      public ItemStack roll(RandomSource random) {
         int amount = Mth.clamp(this.count.sample(random), 0, this.item.getMaxStackSize());
         return amount <= 0 ? ItemStack.EMPTY : this.item.copyWithCount(amount);
      }
   }
}
