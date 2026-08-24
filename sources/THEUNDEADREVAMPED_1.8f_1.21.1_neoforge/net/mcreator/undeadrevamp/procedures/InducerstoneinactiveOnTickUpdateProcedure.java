package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class InducerstoneinactiveOnTickUpdateProcedure {
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
            UndeadRevamp2Mod.queueServerWork(
               1,
               () -> {
                  for (int index0 = 0; index0 < 1; index0++) {
                     if (world.isEmptyBlock(
                        BlockPos.containing(
                           x + entityiterator.getPersistentData().getDouble("aoe_x") * entityiterator.getPersistentData().getDouble("distance"),
                           y + 1.0 + entityiterator.getPersistentData().getDouble("aoe_y") * entityiterator.getPersistentData().getDouble("distance"),
                           z + entityiterator.getPersistentData().getDouble("aoe_z") * entityiterator.getPersistentData().getDouble("distance")
                        )
                     )) {
                        entityiterator.getPersistentData().putBoolean("behind_wall", false);
                        entityiterator.getPersistentData().putDouble("distance", entityiterator.getPersistentData().getDouble("distance") - 0.05);
                     } else {
                        entityiterator.getPersistentData().putBoolean("behind_wall", true);
                     }

                     UndeadRevamp2Mod.queueServerWork(
                        1,
                        () -> {
                           if (!entityiterator.getPersistentData().getBoolean("behind_wall")) {
                              if (world instanceof Level _level) {
                                 if (!_level.isClientSide()) {
                                    _level.playSound(
                                       null,
                                       BlockPos.containing(x, y, z),
                                       (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.grindstone.use")),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       1.0F
                                    );
                                 } else {
                                    _level.playLocalSound(
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
                              BlockState _bs = ((Block)UndeadRevamp2ModBlocks.INDUCERSTONE.get()).defaultBlockState();
                              BlockState _bso = world.getBlockState(_bp);

                              for (Property<?> _propertyOld : _bso.getProperties()) {
                                 Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
                                 if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                                    try {
                                       _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
                                    } catch (Exception var15) {
                                    }
                                 }
                              }

                              world.setBlock(_bp, _bs, 3);
                           }
                        }
                     );
                  }
               }
            );
         }
      }
   }
}
