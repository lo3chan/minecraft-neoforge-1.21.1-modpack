package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class CharmofPowerPriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (entity instanceof Player _playerHasItem
            && _playerHasItem.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.ETHEREAL_SPIRIT.get()))) {
            if (itemstack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING)) != 0) {
               if (Math.random() < 0.5 && world instanceof ServerLevel _level) {
                  itemstack.hurtAndBreak(1, _level, null, _stkprov -> {});
               }
            } else if (world instanceof ServerLevel _level) {
               itemstack.hurtAndBreak(1, _level, null, _stkprov -> {});
            }

            if (entity instanceof Player _player) {
               ItemStack _stktoremove = new ItemStack((ItemLike)BornInChaosV1ModItems.ETHEREAL_SPIRIT.get());
               _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
            }

            if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown(itemstack.getItem(), 1200);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:charm_of_strenght_use")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:charm_of_strenght_use")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     1.0F,
                     false
                  );
               }
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 2));
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.CHARMOF_POWER.get()) {
               if (entity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.MAIN_HAND, true);
               }
            } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.CHARMOF_POWER.get()
               && entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.OFF_HAND, true);
            }
         }
      }
   }
}
