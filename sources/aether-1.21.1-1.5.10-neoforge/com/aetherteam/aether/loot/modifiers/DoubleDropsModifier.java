package com.aetherteam.aether.loot.modifiers;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.abilities.weapon.SkyrootWeapon;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class DoubleDropsModifier extends LootModifier {
   public static final MapCodec<DoubleDropsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> LootModifier.codecStart(instance).apply(instance, DoubleDropsModifier::new)
   );

   public DoubleDropsModifier(LootItemCondition[] conditions) {
      super(conditions);
   }

   public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> lootStacks, LootContext context) {
      Entity entity = (Entity)context.getParamOrNull(LootContextParams.DIRECT_ATTACKING_ENTITY);
      Entity target = (Entity)context.getParamOrNull(LootContextParams.THIS_ENTITY);
      ObjectArrayList<ItemStack> newStacks = new ObjectArrayList(lootStacks);
      if (entity instanceof LivingEntity livingEntity
         && target != null
         && EquipmentUtil.isFullStrength(livingEntity)
         && livingEntity.getMainHandItem().getItem() instanceof SkyrootWeapon
         && !target.getType().is(AetherTags.Entities.NO_SKYROOT_DOUBLE_DROPS)) {
         ObjectListIterator var7 = lootStacks.iterator();

         while (var7.hasNext()) {
            ItemStack stack = (ItemStack)var7.next();
            if (!stack.is(AetherTags.Items.NO_SKYROOT_DOUBLE_DROPS)) {
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
