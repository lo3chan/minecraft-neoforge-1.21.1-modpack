package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.InvisiimmortalEntity;
import net.mcreator.undeadrevamp.entity.TheimmortalEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RareimmortalsummonblockUpdateTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (!world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 8.0, 8.0, 8.0), e -> true).isEmpty()) {
         if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.INVISIIMMORTAL.get())
               .spawn(_level, BlockPos.containing(x, y + 1.0, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
            }
         }

         UndeadRevamp2Mod.queueServerWork(
            5,
            () -> {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiterator instanceof InvisiimmortalEntity) {
                     entityiterator.getPersistentData().putDouble("rare", 1.0);
                  }

                  if (entityiterator instanceof TheimmortalEntity) {
                     entityiterator.getPersistentData().putDouble("decored", 1.0);
                  }
               }
            }
         );
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = Blocks.COARSE_DIRT.defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOld : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
               } catch (Exception var14) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
      }
   }
}
