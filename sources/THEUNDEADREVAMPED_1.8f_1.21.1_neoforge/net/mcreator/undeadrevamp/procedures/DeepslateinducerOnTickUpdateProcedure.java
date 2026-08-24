package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DeepslateinducerOnTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiterator instanceof Player) {
            entityiterator.getPersistentData().putDouble("aoe_x", x - entityiterator.getX());
            entityiterator.getPersistentData().putDouble("aoe_y", y + 1.0 - (entityiterator.getY() + entityiterator.getBbHeight()));
            entityiterator.getPersistentData().putDouble("aoe_z", z - entityiterator.getZ());
            entityiterator.getPersistentData().putDouble("distance", 0.0);
            if (!entityiterator.getPersistentData().getBoolean("behind_wall")) {
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.INVISILEHCERY.get())
                     .spawn(_level, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                  }
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.grindstone.use")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.grindstone.use")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockState _bs = Blocks.DEEPSLATE.defaultBlockState();
               BlockState _bso = world.getBlockState(_bp);

               for (Property<?> _propertyOld : _bso.getProperties()) {
                  Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
                  if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                     try {
                        _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
                     } catch (Exception var18) {
                     }
                  }
               }

               world.setBlock(_bp, _bs, 3);
               if (Math.random() < 0.7) {
                  if (entityiterator instanceof Player _player && !_player.level().isClientSide()) {
                     _player.displayClientMessage(Component.literal("quite hot are we?"), true);
                  }
               } else if (Math.random() < 0.7) {
                  if (entityiterator instanceof Player _player && !_player.level().isClientSide()) {
                     _player.displayClientMessage(Component.literal("You look like you could satiate me"), true);
                  }
               } else if (entityiterator instanceof Player _player && !_player.level().isClientSide()) {
                  _player.displayClientMessage(Component.literal("You can feel voluptuous surging from your \"feet\" below"), true);
               }
               break;
            }
         }
      }
   }
}
