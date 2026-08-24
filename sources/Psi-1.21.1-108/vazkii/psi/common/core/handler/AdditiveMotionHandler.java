package vazkii.psi.common.core.handler;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Post;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageAdditiveMotion;

@EventBusSubscriber(
   modid = "psi"
)
public class AdditiveMotionHandler {
   private static final Map<Entity, Vec3> toUpdate = new WeakHashMap<>();

   public static void addMotion(Entity entity, double x, double y, double z) {
      if (x != 0.0 || y != 0.0 || z != 0.0) {
         if (!entity.level().isClientSide) {
            Vec3 base = toUpdate.getOrDefault(entity, Vec3.ZERO);
            toUpdate.put(entity, base.add(x, y, z));
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(Post e) {
      if (!e.getLevel().isClientSide()) {
         for (Entity entity : toUpdate.keySet()) {
            if (!entity.hurtMarked) {
               Vec3 vec = toUpdate.get(entity);
               if (vec != null) {
                  MessageAdditiveMotion motion = new MessageAdditiveMotion(entity.getId(), vec.x, vec.y, vec.z);
                  if (entity instanceof ServerPlayer) {
                     ((ServerPlayer)entity).connection.aboveGroundTickCount = ((ServerPlayer)entity).connection.aboveGroundTickCount
                        + -2 * getMaximumFlyingTicks(entity);
                     MessageRegister.sendToPlayer((ServerPlayer)entity, motion);
                     ((ServerPlayer)entity).connection.aboveGroundTickCount = ((ServerPlayer)entity).connection.aboveGroundTickCount
                        + -2 * getMaximumFlyingTicks(entity);
                  } else {
                     entity.push(vec.x, vec.y, vec.z);
                  }

                  if (entity.level() instanceof ServerLevel) {
                     MessageRegister.sendToPlayersTrackingEntity(entity, motion);
                  }
               }
            }
         }

         toUpdate.clear();
      }
   }

   private static int getMaximumFlyingTicks(Entity entity) {
      double d0 = entity.getGravity();
      if (d0 < 9.999999747378752E-6) {
         return 2147483647;
      } else {
         double d1 = 0.08 / d0;
         return Mth.ceil(80.0 * Math.max(d1, 1.0));
      }
   }
}
