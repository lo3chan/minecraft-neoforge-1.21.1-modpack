package com.aetherteam.aether.loot.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class GlovesLootModifier extends LootModifier {
   public static final MapCodec<GlovesLootModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> codecStart(instance)
         .and(ItemStack.CODEC.fieldOf("gloves").forGetter(modifier -> modifier.glovesStack))
         .and(BuiltInRegistries.ARMOR_MATERIAL.holderByNameCodec().fieldOf("armor_material").forGetter(modifier -> modifier.armorMaterial))
         .apply(instance, GlovesLootModifier::new)
   );
   public final ItemStack glovesStack;
   public final Holder<ArmorMaterial> armorMaterial;

   public GlovesLootModifier(LootItemCondition[] conditionsIn, ItemStack glovesStack, Holder<ArmorMaterial> armorMaterial) {
      super(conditionsIn);
      this.glovesStack = glovesStack;
      this.armorMaterial = armorMaterial;
   }

   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> lootStacks, LootContext context) {
      Level level = context.getLevel();
      RandomSource randomSource = context.getRandom();
      Vec3 vec3 = (Vec3)context.getParamOrNull(LootContextParams.ORIGIN);
      Entity entity = (Entity)context.getParamOrNull(LootContextParams.THIS_ENTITY);
      if (vec3 != null && entity != null) {
         BlockPos pos = BlockPos.containing(vec3);
         BlockEntity blockEntity = context.getLevel().getBlockEntity(pos);
         if (blockEntity instanceof BaseContainerBlockEntity) {
            for (ItemStack armorStack : lootStacks.stream()
               .filter(itemStack -> itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(this.armorMaterial))
               .toList()) {
               if (randomSource.nextInt(4) < 1) {
                  ItemStack gloves = this.glovesStack.copy();
                  int cost = 0;
                  boolean isTreasure = false;

                  for (Entry<Holder<Enchantment>> enchantmentInfo : armorStack.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT))
                     .entrySet()) {
                     Holder<Enchantment> enchantment = (Holder<Enchantment>)enchantmentInfo.getKey();
                     int enchantmentValue = enchantmentInfo.getIntValue();
                     cost = Math.max(cost, ((Enchantment)enchantment.value()).getMinCost(enchantmentValue));
                     if (!isTreasure) {
                        isTreasure = enchantment.is(EnchantmentTags.TREASURE);
                     }

                     if (gloves.isPrimaryItemFor(enchantment)) {
                        gloves.enchant(enchantment, enchantmentInfo.getIntValue());
                     }
                  }

                  if (!armorStack.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).isEmpty()
                     && gloves.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).isEmpty()) {
                     EnchantmentHelper.enchantItem(
                        randomSource,
                        gloves,
                        cost,
                        level.registryAccess(),
                        Optional.of(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT))
                     );
                  }

                  if (armorStack.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).isEmpty()
                     || !gloves.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).isEmpty()) {
                     lootStacks.replaceAll(stack -> stack.equals(armorStack) ? gloves : stack);
                  }
               }
            }
         }
      }

      return lootStacks;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC;
   }
}
