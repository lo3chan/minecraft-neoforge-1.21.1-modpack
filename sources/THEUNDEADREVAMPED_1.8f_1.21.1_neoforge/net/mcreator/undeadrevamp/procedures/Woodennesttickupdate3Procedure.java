package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Woodennesttickupdate3Procedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (!world.getEntitiesOfClass(Animal.class, AABB.ofSize(new Vec3(x, y, z), 20.0, 20.0, 20.0), e -> true).isEmpty() && Math.random() < 0.04) {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)UndeadRevamp2ModBlocks.WOODENNESTHARVEST_3.get()).defaultBlockState();
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

         BlockEntity _be = world.getBlockEntity(_bp);
         CompoundTag _bnbt = null;
         if (_be != null) {
            _bnbt = _be.saveWithFullMetadata(world.registryAccess());
            _be.setRemoved();
         }

         world.setBlock(_bp, _bs, 3);
         if (_bnbt != null) {
            _be = world.getBlockEntity(_bp);
            if (_be != null) {
               try {
                  _be.loadWithComponents(_bnbt, world.registryAccess());
               } catch (Exception var14) {
               }
            }
         }
      }

      if (Math.random() < 0.03 && world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolenceambt")),
               SoundSource.NEUTRAL,
               0.1F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolenceambt")),
               SoundSource.NEUTRAL,
               0.1F,
               1.0F,
               false
            );
         }
      }

      if (Math.random() < 0.15 && world instanceof ServerLevel _levelx) {
         _levelx.sendParticles(ParticleTypes.SNEEZE, x, y, z, 5, 1.0, 1.0, 1.0, 0.0);
      }
   }
}
