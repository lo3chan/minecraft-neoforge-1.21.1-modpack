package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CoffinbreadOnBlockRightClickedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((new Object() {
               public boolean checkGamemode(Entity _ent) {
                  if (_ent instanceof ServerPlayer _serverPlayer) {
                     return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                  } else {
                     return _ent.level().isClientSide() && _ent instanceof Player _player
                        ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                           && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
                        : false;
                  }
               }
            })
            .checkGamemode(entity)) {
            if (entity instanceof Player _player) {
               _player.getFoodData().setFoodLevel((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) + 3);
            }

            if (entity instanceof Player _player) {
               _player.getFoodData().setSaturation((entity instanceof Player _plr ? _plr.getFoodData().getSaturationLevel() : 0.0F) + 1.0F);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.eat")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.eat")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(((Block)UndeadRevamp2ModBlocks.COFFINBROAD.get()).defaultBlockState()));
            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockState _bs = ((Block)UndeadRevamp2ModBlocks.COFFINBREADSTAGE_2.get()).defaultBlockState();
            BlockState _bso = world.getBlockState(_bp);

            for (Property<?> _propertyOld : _bso.getProperties()) {
               Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
               if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                  try {
                     _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
                  } catch (Exception var17) {
                  }
               }
            }

            world.setBlock(_bp, _bs, 3);
         } else if ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) < 20) {
            if (entity instanceof Player _player) {
               _player.getFoodData().setFoodLevel((entity instanceof Player _plrx ? _plrx.getFoodData().getFoodLevel() : 0) + 3);
            }

            if (entity instanceof Player _player) {
               _player.getFoodData().setSaturation((entity instanceof Player _plrx ? _plrx.getFoodData().getSaturationLevel() : 0.0F) + 5.0F);
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.eat")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.eat")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(((Block)UndeadRevamp2ModBlocks.COFFINBROAD.get()).defaultBlockState()));
            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockState _bs = ((Block)UndeadRevamp2ModBlocks.COFFINBREADSTAGE_2.get()).defaultBlockState();
            BlockState _bso = world.getBlockState(_bp);

            for (Property<?> _propertyOldx : _bso.getProperties()) {
               Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldx.getName());
               if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                  try {
                     _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldx));
                  } catch (Exception var16) {
                  }
               }
            }

            world.setBlock(_bp, _bs, 3);
         }
      }
   }
}
