package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.ThedungeonEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AltarnomUpdateTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (!world.getEntitiesOfClass(ThedungeonEntity.class, AABB.ofSize(new Vec3(x, y, z), 8.0, 8.0, 8.0), e -> true).isEmpty()) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.SMOKE, x, y + 1.0, z, 10, 1.0, 1.0, 1.0, 1.0E-5);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.campfire.crackle")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.campfire.crackle")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }

      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiterator instanceof ThedungeonEntity) {
            entityiterator.lookAt(Anchor.EYES, new Vec3(x, y, z));
            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockState _bs = ((Block)UndeadRevamp2ModBlocks.ALTARACTIVE.get()).defaultBlockState();
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
         }
      }
   }
}
