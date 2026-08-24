package vazkii.psi.api.spell.detonator;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import vazkii.psi.api.PsiAPI;

public interface IDetonationHandler {
   static IDetonationHandler detonator(Entity entity) {
      return (IDetonationHandler)entity.getCapability(PsiAPI.DETONATION_HANDLER_CAPABILITY);
   }

   static void performDetonation(Level world, Player player) {
      performDetonation(world, player, player, 32.0, e -> true);
   }

   static void performDetonation(Level world, Player player, double range) {
      performDetonation(world, player, player, range, e -> true);
   }

   static void performDetonation(Level world, Player player, Predicate<Entity> filter) {
      performDetonation(world, player, player, 32.0, filter);
   }

   static void performDetonation(Level world, Player player, double range, Predicate<Entity> filter) {
      performDetonation(world, player, player, range, filter);
   }

   static void performDetonation(Level world, Player player, Entity center) {
      performDetonation(world, player, center, 32.0, e -> true);
   }

   static void performDetonation(Level world, Player player, Entity center, double range) {
      performDetonation(world, player, center, range, e -> true);
   }

   static void performDetonation(Level world, Player player, Entity center, Predicate<Entity> filter) {
      performDetonation(world, player, center, 32.0, filter);
   }

   static void performDetonation(Level world, Player player, Entity center, double range, Predicate<Entity> filter) {
      List<Entity> charges = world.getEntitiesOfClass(
         Entity.class,
         center.getBoundingBox().inflate(range),
         entity -> {
            if (entity == null) {
               return false;
            } else {
               IDetonationHandler detonator = (IDetonationHandler)entity.getCapability(PsiAPI.DETONATION_HANDLER_CAPABILITY);
               if (detonator == null) {
                  return false;
               } else {
                  Vec3 locus = detonator.objectLocus();
                  return locus != null && !(locus.distanceToSqr(center.getX(), center.getY(), center.getZ()) > range * range)
                     ? filter == null || filter.test(entity)
                     : false;
               }
            }
         }
      );
      List<IDetonationHandler> handlers = charges.stream()
         .map(e -> Objects.requireNonNull((IDetonationHandler)e.getCapability(PsiAPI.DETONATION_HANDLER_CAPABILITY)))
         .collect(Collectors.toList());
      if (!((DetonationEvent)NeoForge.EVENT_BUS.post(new DetonationEvent(player, center, range, handlers))).isCanceled() && !handlers.isEmpty()) {
         for (IDetonationHandler handler : handlers) {
            handler.detonate();
         }
      }
   }

   default Vec3 objectLocus() {
      return null;
   }

   void detonate();
}
