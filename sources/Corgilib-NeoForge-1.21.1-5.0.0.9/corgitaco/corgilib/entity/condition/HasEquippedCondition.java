package corgitaco.corgilib.entity.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.entity.ItemStackCheck;
import corgitaco.corgilib.serialization.codec.CodecUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HasEquippedCondition implements Condition {
   public static final Codec<HasEquippedCondition> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.unboundedMap(CodecUtil.EQUIPMENT_SLOT_CODEC, ItemStackCheck.CODEC.listOf())
               .fieldOf("has_equipped")
               .forGetter(hasEquippedCondition -> hasEquippedCondition.stackChecks)
         )
         .apply(builder, HasEquippedCondition::new)
   );
   private final Map<EquipmentSlot, List<ItemStackCheck>> stackChecks;
   private final Set<Entry<EquipmentSlot, List<ItemStackCheck>>> stackChecksEntries;

   public HasEquippedCondition(Map<EquipmentSlot, List<ItemStackCheck>> stackChecksBySlot) {
      this.stackChecks = new Object2ObjectOpenHashMap(stackChecksBySlot);
      this.stackChecksEntries = this.stackChecks.entrySet();
   }

   @Override
   public boolean passes(ConditionContext conditionContext) {
      int hits = 0;

      for (Entry<EquipmentSlot, List<ItemStackCheck>> stackChecksEntry : this.stackChecksEntries) {
         ItemStack slotItemStack = conditionContext.entity().getItemBySlot(stackChecksEntry.getKey());
         Item slotItem = slotItemStack.getItem();

         for (ItemStackCheck itemStackCheck : stackChecksEntry.getValue()) {
            if (slotItem == itemStackCheck.getItem() && itemStackCheck.test(slotItemStack)) {
               hits++;
               break;
            }
         }
      }

      return hits == this.stackChecksEntries.size();
   }

   @Override
   public Codec<? extends Condition> codec() {
      return CODEC;
   }
}
