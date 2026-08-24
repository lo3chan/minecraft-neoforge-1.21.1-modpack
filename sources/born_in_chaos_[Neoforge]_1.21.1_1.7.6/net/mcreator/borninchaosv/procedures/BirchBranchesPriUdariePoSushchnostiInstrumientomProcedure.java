package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class BirchBranchesPriUdariePoSushchnostiInstrumientomProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity, ItemStack itemstack) {
      if (entity != null && sourceentity != null) {
         if (!(sourceentity instanceof Player _plrCldCheck1 && _plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
            entity.hurt(
               new DamageSource(
                  world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("born_in_chaos_v1:punishment"))), sourceentity
               ),
               (float)(((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness * 0.3 + 3.0)
            );
            if (sourceentity instanceof Player _player) {
               _player.getCooldowns().addCooldown(itemstack.getItem(), 8);
            }
         }
      }
   }
}
