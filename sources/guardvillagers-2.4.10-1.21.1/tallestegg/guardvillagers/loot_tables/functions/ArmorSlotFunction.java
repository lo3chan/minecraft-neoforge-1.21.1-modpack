package tallestegg.guardvillagers.loot_tables.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import tallestegg.guardvillagers.loot_tables.GuardLootTables;

public class ArmorSlotFunction extends LootItemConditionalFunction {
   final EquipmentSlot slot;
   public static final MapCodec<ArmorSlotFunction> CODEC = RecordCodecBuilder.mapCodec(
      p_298087_ -> commonFields(p_298087_)
         .and(EquipmentSlot.CODEC.fieldOf("slot").forGetter(p_298086_ -> p_298086_.slot))
         .apply(p_298087_, ArmorSlotFunction::new)
   );

   ArmorSlotFunction(List<LootItemCondition> pConditions, EquipmentSlot slot) {
      super(pConditions);
      this.slot = slot;
   }

   protected ItemStack run(ItemStack pStack, LootContext pContext) {
      LivingEntity livingEntity = (LivingEntity)pContext.getParamOrNull(LootContextParams.THIS_ENTITY);
      livingEntity.setItemSlot(this.slot, pStack);
      return pStack;
   }

   public LootItemFunctionType getType() {
      return (LootItemFunctionType)GuardLootTables.ARMOR_SLOT.get();
   }

   public static Builder<?> armorSlotFunction(EquipmentSlot slot) {
      return simpleBuilder(conditions -> new ArmorSlotFunction(conditions, slot));
   }
}
