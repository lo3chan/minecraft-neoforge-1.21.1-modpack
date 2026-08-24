package com.aetherteam.aether.loot.modifiers;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class PigDropsModifier extends LootModifier {
   public static final MapCodec<PigDropsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> LootModifier.codecStart(instance).apply(instance, PigDropsModifier::new)
   );

   public PigDropsModifier(LootItemCondition[] conditions) {
      super(conditions);
   }

   public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> lootStacks, LootContext context) {
      Entity entity = (Entity)context.getParamOrNull(LootContextParams.DIRECT_ATTACKING_ENTITY);
      Entity target = (Entity)context.getParamOrNull(LootContextParams.THIS_ENTITY);
      ObjectArrayList<ItemStack> newStacks = new ObjectArrayList(lootStacks);
      if (entity instanceof LivingEntity livingEntity
         && target instanceof LivingEntity livingTarget
         && EquipmentUtil.isFullStrength(livingEntity)
         && livingEntity.getMainHandItem().is((Item)AetherItems.PIG_SLAYER.get())
         && livingTarget.getType().is(AetherTags.Entities.PIGS)
         && livingTarget.getRandom().nextInt(4) == 0) {
         ObjectListIterator var8 = lootStacks.iterator();

         while (var8.hasNext()) {
            ItemStack stack = (ItemStack)var8.next();
            if (stack.is(AetherTags.Items.PIG_DROPS)) {
               newStacks.add(stack);
            }
         }
      }

      return newStacks;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC;
   }
}
