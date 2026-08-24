package dev.tr7zw.entityculling.mixin;

import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.NMSCullingHelper;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import dev.tr7zw.transition.mc.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public class ClientWorldMixin {
   private Minecraft mc = Minecraft.getInstance();

   @Inject(
      method = {"tickNonPassenger(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tickEntity(Entity entity, CallbackInfo info) {
      if (EntityCullingModBase.instance.config.tickCulling && !EntityCullingModBase.instance.config.skipEntityCulling) {
         if (EntityCullingModBase.instance.config.forceDisplayCulling && entity instanceof Display display) {
            this.processDisplay(display);
         }

         if (!NMSCullingHelper.ignoresCulling(entity)
            && entity != GeneralUtil.getPlayer()
            && entity != GeneralUtil.getCameraEntity()
            && !entity.isPassenger()
            && !entity.isVehicle()
            && !(entity instanceof AbstractMinecart)) {
            if (!EntityCullingModBase.instance.tickCullWhitelists.contains(entity.getType())
               && !EntityCullingModBase.instance.entityWhitelist.contains(entity.getType())) {
               if (entity instanceof Cullable cull) {
                  if (cull.isCulled() || cull.isOutOfCamera()) {
                     this.basicTick(entity);
                     EntityCullingModBase.instance.skippedEntityTicks++;
                     EntityCullingModBase.instance.debugCollector.getDataHolder().skippedEntityTicks++;
                     info.cancel();
                     return;
                  }

                  cull.setOutOfCamera(true);
               }

               EntityCullingModBase.instance.tickedEntities++;
               EntityCullingModBase.instance.debugCollector.getDataHolder().tickedEntities++;
            } else {
               EntityCullingModBase.instance.tickedEntities++;
            }
         } else {
            EntityCullingModBase.instance.tickedEntities++;
         }
      } else {
         EntityCullingModBase.instance.tickedEntities++;
      }
   }

   private void processDisplay(Display display) {
      if (display.getBoundingBoxForCulling().getSize() == 0.0 && display instanceof DisplayAccessor accessor) {
         accessor.invokeSetWidth(3.0F);
         accessor.invokeSetHeight(3.0F);
         display.setPos(display.getX(), display.getY(), display.getZ());
      }
   }

   private void basicTick(Entity entity) {
      entity.setOldPosAndRot();
      entity.tickCount++;
      if (entity instanceof LivingEntity living) {
         living.aiStep();
         if (living.hurtTime > 0) {
            living.hurtTime--;
         }
      }

      if (entity instanceof Warden warden && this.mc.level.isClientSide() && !warden.isSilent() && warden.tickCount % this.getWardenHeartBeatDelay(warden) == 0
         )
       {
         this.mc
            .level
            .playLocalSound(
               warden.getX(), warden.getY(), warden.getZ(), SoundEvents.WARDEN_HEARTBEAT, warden.getSoundSource(), 5.0F, warden.getVoicePitch(), false
            );
      }
   }

   private int getWardenHeartBeatDelay(Warden warden) {
      float f = warden.getClientAngerLevel() / AngerLevel.ANGRY.getMinimumAnger();
      return 40 - Mth.floor(Mth.clamp(f, 0.0F, 1.0F) * 30.0F);
   }
}
