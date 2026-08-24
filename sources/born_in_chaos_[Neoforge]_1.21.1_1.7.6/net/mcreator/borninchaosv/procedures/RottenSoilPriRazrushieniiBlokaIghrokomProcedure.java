package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;

public class RottenSoilPriRazrushieniiBlokaIghrokomProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((new Object() {
               public boolean checkGamemode(Entity _ent) {
                  if (_ent instanceof ServerPlayer _serverPlayer) {
                     return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
                  } else {
                     return _ent.level().isClientSide() && _ent instanceof Player _player
                        ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                           && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SURVIVAL
                        : false;
                  }
               }
            })
            .checkGamemode(entity)) {
            if (Math.random() < 0.25) {
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
                     .spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }
            } else if (Math.random() < 0.25) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, x, y, z, new ItemStack(Items.ROTTEN_FLESH));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.25 && world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelx, x, y, z, new ItemStack(Items.BONE));
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }
      }
   }
}
