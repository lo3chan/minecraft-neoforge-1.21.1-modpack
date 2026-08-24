package fuzs.puzzleslib.neoforge.api.event.v1.entity.living;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ComputeEnchantedLootBonusEvent extends LivingEvent {
   @Nullable
   private final DamageSource damageSource;
   private final Holder<Enchantment> enchantment;
   private int enchantmentLevel;

   @Internal
   public ComputeEnchantedLootBonusEvent(LivingEntity entity, @Nullable DamageSource damageSource, Holder<Enchantment> enchantment, int enchantmentLevel) {
      super(entity);
      this.damageSource = damageSource;
      this.enchantment = enchantment;
      this.enchantmentLevel = enchantmentLevel;
   }

   @Nullable
   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public Holder<Enchantment> getEnchantment() {
      return this.enchantment;
   }

   public int getEnchantmentLevel() {
      return this.enchantmentLevel;
   }

   public void setEnchantmentLevel(int enchantmentLevel) {
      this.enchantmentLevel = enchantmentLevel;
   }

   @Internal
   public static int onComputeEnchantedLootBonus(Holder<Enchantment> enchantment, int enchantmentLevel, LootContext lootContext) {
      Entity entity = (Entity)lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
      if (entity instanceof LivingEntity livingEntity) {
         DamageSource damageSource = (DamageSource)lootContext.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
         return onComputeEnchantedLootBonus(enchantment, enchantmentLevel, livingEntity, damageSource);
      } else {
         return enchantmentLevel;
      }
   }

   @Internal
   public static int onComputeEnchantedLootBonus(
      Holder<Enchantment> enchantment, int enchantmentLevel, LivingEntity livingEntity, @Nullable DamageSource damageSource
   ) {
      return ((ComputeEnchantedLootBonusEvent)NeoForge.EVENT_BUS
            .post(new ComputeEnchantedLootBonusEvent(livingEntity, damageSource, enchantment, enchantmentLevel)))
         .getEnchantmentLevel();
   }
}
