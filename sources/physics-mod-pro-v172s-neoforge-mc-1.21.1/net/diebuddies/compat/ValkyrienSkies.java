package net.diebuddies.compat;

import net.diebuddies.minecraft.ShipRotation;
import net.diebuddies.physics.ocean.EntityOcean;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

public class ValkyrienSkies {
   public static EntityOcean hasShipMount(Entity entity) {
      for (Object var3 : VSGameUtilsKt.getShipsIntersecting(entity.level(), entity.getBoundingBox().inflate(0.5, 2.0, 0.5))) {
         if (var3 instanceof EntityOcean entityOcean) {
            ((IEntityDraggingInformationProvider)entity).getDraggingInformation().setLastShipStoodOn(((ClientShip)entityOcean).getId());
            return entityOcean;
         }
      }

      return null;
   }

   public static void doEntityOnShipTransformation(Matrix4f transformation, Entity entity, float renderPercent) {
      EntityOcean vehicle = hasShipMount(entity);
      ClientShip ship = (ClientShip)vehicle;
      ShipTransform renderTransform = ship.getRenderTransform();
      Quaterniondc originalRotation = ((ShipRotation)ship).getOriginalRotation();
      Vector3dc worldPos = renderTransform.getPositionInWorld();
      double ex = Mth.lerp(renderPercent, entity.xo, entity.getX());
      double ey = Mth.lerp(renderPercent, entity.yo, entity.getY());
      double ez = Mth.lerp(renderPercent, entity.zo, entity.getZ());
      Vector3d relPos = worldPos.sub(ex, ey, ez, new Vector3d());
      Vector4f offset1 = originalRotation.invert(new Quaterniond()).transform(new Vector4f((float)relPos.x(), (float)relPos.y(), (float)relPos.z(), 1.0F));
      Vector4f offset2 = renderTransform.getShipToWorldRotation().transform(new Vector4f(offset1.x(), offset1.y(), offset1.z(), 1.0F));
      Vector4f offset = offset2.sub(new Vector4f((float)relPos.x, (float)relPos.y, (float)relPos.z, 1.0F), new Vector4f());
      transformation.translate(-offset.x, -offset.y + (float)vehicle.getPhysicsYOffset(renderPercent), -offset.z);
   }

   public static float getEntityOffset(Entity entity, float renderPercent) {
      EntityOcean vehicle = hasShipMount(entity);
      ClientShip ship = (ClientShip)vehicle;
      ShipTransform renderTransform = ship.getRenderTransform();
      Quaterniondc originalRotation = ((ShipRotation)ship).getOriginalRotation();
      Vector3dc worldPos = renderTransform.getPositionInWorld();
      double ex = Mth.lerp(renderPercent, entity.xo, entity.getX());
      double ey = Mth.lerp(renderPercent, entity.yo, entity.getY());
      double ez = Mth.lerp(renderPercent, entity.zo, entity.getZ());
      Vector3d relPos = worldPos.sub(ex, ey, ez, new Vector3d());
      Vector4f offset1 = originalRotation.invert(new Quaterniond()).transform(new Vector4f((float)relPos.x(), (float)relPos.y(), (float)relPos.z(), 1.0F));
      Vector4f offset2 = renderTransform.getShipToWorldRotation().transform(new Vector4f(offset1.x(), offset1.y(), offset1.z(), 1.0F));
      Vector4f offset = offset2.sub(new Vector4f((float)relPos.x, (float)relPos.y, (float)relPos.z, 1.0F), new Vector4f());
      return -offset.y + (float)vehicle.getPhysicsYOffset(renderPercent);
   }

   public static Vector3d getEntityOffset3D(Entity entity, float renderPercent) {
      EntityOcean vehicle = hasShipMount(entity);
      ClientShip ship = (ClientShip)vehicle;
      ShipTransform renderTransform = ship.getRenderTransform();
      Quaterniondc originalRotation = ((ShipRotation)ship).getOriginalRotation();
      Vector3dc worldPos = renderTransform.getPositionInWorld();
      double ex = Mth.lerp(renderPercent, entity.xo, entity.getX());
      double ey = Mth.lerp(renderPercent, entity.yo, entity.getY());
      double ez = Mth.lerp(renderPercent, entity.zo, entity.getZ());
      Vector3d relPos = worldPos.sub(ex, ey, ez, new Vector3d());
      Vector4f offset1 = originalRotation.invert(new Quaterniond()).transform(new Vector4f((float)relPos.x(), (float)relPos.y(), (float)relPos.z(), 1.0F));
      Vector4f offset2 = renderTransform.getShipToWorldRotation().transform(new Vector4f(offset1.x(), offset1.y(), offset1.z(), 1.0F));
      Vector4f offset = offset2.sub(new Vector4f((float)relPos.x, (float)relPos.y, (float)relPos.z, 1.0F), new Vector4f());
      return new Vector3d(-offset.x, -offset.y + (float)vehicle.getPhysicsYOffset(renderPercent), -offset.z);
   }
}
