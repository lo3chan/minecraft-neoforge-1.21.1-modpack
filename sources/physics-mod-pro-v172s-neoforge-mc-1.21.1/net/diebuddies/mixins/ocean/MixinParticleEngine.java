package net.diebuddies.mixins.ocean;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.diebuddies.physics.ocean.OceanSplashParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ParticleEngine.class})
public class MixinParticleEngine {
   @Shadow
   @Final
   private Map<ParticleRenderType, Queue<Particle>> particles;
   @Unique
   private List<OceanSplashParticle> translucent = new ObjectArrayList();

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   public void render(LightTexture lightTexture, final Camera camera, final float renderPercent, CallbackInfo info) {
      Queue<Particle> queue = this.particles.get(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT);
      if (queue != null) {
         this.translucent.clear();
         Iterator<Particle> it = queue.iterator();

         while (it.hasNext()) {
            Particle particle = it.next();
            if (particle instanceof OceanSplashParticle oceanSplash) {
               this.translucent.add(oceanSplash);
               it.remove();
            }
         }

         Collections.sort(
            this.translucent,
            Collections.reverseOrder(
               new Comparator<OceanSplashParticle>() {
                  public int compare(OceanSplashParticle o1, OceanSplashParticle o2) {
                     return Double.compare(
                        MixinParticleEngine.this.distanceToCameraNearPlaneApprox(o1, camera, renderPercent),
                        MixinParticleEngine.this.distanceToCameraNearPlaneApprox(o2, camera, renderPercent)
                     );
                  }
               }
            )
         );
         queue.addAll(this.translucent);
      }
   }

   @Unique
   private double distanceToCameraNearPlaneApprox(OceanSplashParticle particle, Camera camera, float renderPercent) {
      Vector3f forward = camera.getLookVector();
      Vec3 camPos = camera.getPosition();
      return this.distToPlaneApprox(
         forward.x,
         forward.y,
         forward.z,
         0.0F,
         (float)(particle.getX(renderPercent) - camPos.x),
         (float)(particle.getY(renderPercent) - camPos.y),
         (float)(particle.getZ(renderPercent) - camPos.z)
      );
   }

   @Unique
   private float distToPlaneApprox(float planeX, float planeY, float planeZ, float offset, float pointX, float pointY, float pointZ) {
      return planeX * pointX + planeY * pointY + planeZ * pointZ + offset;
   }
}
