package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;

public final class EnchantingHelper {
   private EnchantingHelper() {
   }

   public static float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos) {
      return ProxyImpl.get().getEnchantPowerBonus(blockState, level, blockPos);
   }

   public static boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
      return ProxyImpl.get().canApplyAtEnchantingTable(enchantment, itemStack);
   }

   public static Holder<Enchantment> lookup(Entity entity, ResourceKey<Enchantment> resourceKey) {
      return LookupHelper.lookup(entity, Registries.ENCHANTMENT, resourceKey);
   }

   public static Holder<Enchantment> lookup(LevelReader levelReader, ResourceKey<Enchantment> resourceKey) {
      return LookupHelper.lookup(levelReader, Registries.ENCHANTMENT, resourceKey);
   }

   public static Holder<Enchantment> lookup(Provider registries, ResourceKey<Enchantment> resourceKey) {
      return LookupHelper.lookup(registries, Registries.ENCHANTMENT, resourceKey);
   }

   public static int getItemEnchantmentLevel(Provider registries, ResourceKey<Enchantment> enchantment, ItemStack itemStack) {
      Holder<Enchantment> holder = lookup(registries, enchantment);
      return EnchantmentHelper.getItemEnchantmentLevel(holder, itemStack);
   }

   public static int getEnchantmentLevel(ResourceKey<Enchantment> enchantment, LivingEntity livingEntity) {
      Holder<Enchantment> holder = lookup(livingEntity, enchantment);
      return EnchantmentHelper.getEnchantmentLevel(holder, livingEntity);
   }

   public static boolean has(LivingEntity livingEntity, DataComponentType<?> componentType) {
      MutableBoolean mutableBoolean = new MutableBoolean(false);
      EnchantmentHelper.runIterationOnEquipment(livingEntity, (holder, enchantmentLevel, enchantedItemInUse) -> {
         if (((Enchantment)holder.value()).effects().has(componentType)) {
            mutableBoolean.setTrue();
         }
      });
      return mutableBoolean.booleanValue();
   }

   public static float getUnfilteredValueEffectBonus(ItemStack itemStack, Entity entity, DataComponentType<EnchantmentValueEffect> componentType) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnItem(
         itemStack,
         (holder, enchantmentLevel) -> ((Enchantment)holder.value()).modifyUnfilteredValue(componentType, entity.getRandom(), enchantmentLevel, mutableFloat)
      );
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getUnfilteredValueEffectBonus(LivingEntity livingEntity, DataComponentType<EnchantmentValueEffect> componentType) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnEquipment(livingEntity, (holder, enchantmentLevel, enchantedItemInUse) -> {
         Enchantment enchantment = (Enchantment)holder.value();
         enchantment.modifyUnfilteredValue(componentType, livingEntity.getRandom(), enchantmentLevel, mutableFloat);
      });
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getItemFilteredValueEffectBonus(
      ServerLevel serverLevel, ItemStack itemStack, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType
   ) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnItem(
         itemStack,
         (holder, enchantmentLevel) -> ((Enchantment)holder.value())
            .modifyItemFilteredCount(componentType, serverLevel, enchantmentLevel, itemStack, mutableFloat)
      );
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getItemFilteredValueEffectBonus(
      ServerLevel serverLevel, LivingEntity livingEntity, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType
   ) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnEquipment(
         livingEntity,
         (holder, enchantmentLevel, enchantedItemInUse) -> ((Enchantment)holder.value())
            .modifyItemFilteredCount(componentType, serverLevel, enchantmentLevel, enchantedItemInUse.itemStack(), mutableFloat)
      );
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getEntityFilteredValueEffectBonus(
      ServerLevel serverLevel, ItemStack itemStack, Entity entity, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType
   ) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnItem(
         itemStack,
         (holder, enchantmentLevel) -> ((Enchantment)holder.value())
            .modifyEntityFilteredValue(componentType, serverLevel, enchantmentLevel, itemStack, entity, mutableFloat)
      );
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getEntityFilteredValueEffectBonus(
      ServerLevel serverLevel, LivingEntity livingEntity, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType
   ) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnEquipment(
         livingEntity,
         (holder, enchantmentLevel, enchantedItemInUse) -> ((Enchantment)holder.value())
            .modifyEntityFilteredValue(componentType, serverLevel, enchantmentLevel, enchantedItemInUse.itemStack(), livingEntity, mutableFloat)
      );
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getDamageFilteredValueEffectBonus(
      ServerLevel serverLevel,
      ItemStack itemStack,
      Entity entity,
      DamageSource damageSource,
      DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType
   ) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnItem(
         itemStack,
         (holder, enchantmentLevel) -> ((Enchantment)holder.value())
            .modifyDamageFilteredValue(componentType, serverLevel, enchantmentLevel, itemStack, entity, damageSource, mutableFloat)
      );
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getDamageFilteredValueEffectBonus(
      ServerLevel serverLevel,
      LivingEntity livingEntity,
      DamageSource damageSource,
      DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType
   ) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      EnchantmentHelper.runIterationOnEquipment(
         livingEntity,
         (holder, enchantmentLevel, enchantedItemInUse) -> ((Enchantment)holder.value())
            .modifyDamageFilteredValue(componentType, serverLevel, enchantmentLevel, enchantedItemInUse.itemStack(), livingEntity, damageSource, mutableFloat)
      );
      return Math.max(0.0F, mutableFloat.floatValue());
   }
}
