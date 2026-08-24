package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.entity.BabySpiderControlledEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SpiderBiteKazhdyiTikVInvientarieProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator instanceof BabySpiderControlledEntity
               && !(entityiterator instanceof TamableAnimal _tamEnt && _tamEnt.isTame())
               && entityiterator instanceof TamableAnimal _toTame
               && entity instanceof Player _owner) {
               _toTame.tame(_owner);
            }
         }
      }
   }
}
