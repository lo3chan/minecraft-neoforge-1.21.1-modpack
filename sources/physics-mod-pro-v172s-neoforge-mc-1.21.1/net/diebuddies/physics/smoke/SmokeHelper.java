package net.diebuddies.physics.smoke;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SmokeHelper {
   public static boolean addParticle(Level level, double x, double y, double z, float spawnChance) {
      boolean spawnSmoke = ConfigClient.smokePhysics && level.isClientSide();
      boolean inRange = false;
      GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
      if (gameRenderer != null && spawnSmoke) {
         Vec3 view = gameRenderer.getMainCamera().getPosition();
         double distSquared = Vector3d.distanceSquared(view.x, view.y, view.z, x, y, z);
         if (distSquared < ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange) {
            inRange = true;
         }
      }

      if (spawnSmoke && inRange) {
         PhysicsMod mod = PhysicsMod.getInstance(level);
         SmokeDomain smokeDomain = mod.getPhysicsWorld().getSmokeDomain();

         while (spawnChance > 0.0F) {
            float fraction = spawnChance - (int)spawnChance;
            if (spawnChance >= 1.0F || Math.random() < fraction) {
               mod.getPhysicsWorld().queue(() -> smokeDomain.spawnParticle(x, y, z, 1.0F));
            }

            spawnChance--;
         }

         return true;
      } else {
         return false;
      }
   }
}
