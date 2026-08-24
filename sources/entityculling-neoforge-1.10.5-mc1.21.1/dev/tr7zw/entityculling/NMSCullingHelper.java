package dev.tr7zw.entityculling;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NMSCullingHelper {
   private static final Minecraft MC = Minecraft.getInstance();

   public static boolean ignoresCulling(Entity entity) {
      return entity.noCulling;
   }

   public static AABB getCullingBox(Entity entity) {
      return entity instanceof ArmorStand armorStand && armorStand.isMarker()
         ? EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(entity.position())
         : entity.getBoundingBoxForCulling();
   }

   public static Vec3 getRenderOffset(EntityRenderer entityRenderer, Entity entity, float tickDelta) {
      return entityRenderer.getRenderOffset(entity, tickDelta);
   }
}
