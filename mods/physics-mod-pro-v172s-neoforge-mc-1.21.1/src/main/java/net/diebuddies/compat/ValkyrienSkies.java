/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  org.joml.Matrix4f
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.valkyrienskies.core.api.ships.ClientShip
 *  org.valkyrienskies.core.api.ships.properties.ShipTransform
 *  org.valkyrienskies.mod.common.VSGameUtilsKt
 *  org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider
 */
package net.diebuddies.compat;

import net.diebuddies.minecraft.ShipRotation;
import net.diebuddies.physics.ocean.EntityOcean;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

public class ValkyrienSkies {
    public static EntityOcean hasShipMount(Entity entity) {
        for (Object t : VSGameUtilsKt.getShipsIntersecting((Level)entity.level(), (AABB)entity.getBoundingBox().inflate(0.5, 2.0, 0.5))) {
            if (!(t instanceof EntityOcean)) continue;
            EntityOcean entityOcean = (EntityOcean)t;
            ((IEntityDraggingInformationProvider)entity).getDraggingInformation().setLastShipStoodOn(Long.valueOf(((ClientShip)entityOcean).getId()));
            return entityOcean;
        }
        return null;
    }

    public static void doEntityOnShipTransformation(Matrix4f transformation, Entity entity, float renderPercent) {
        EntityOcean vehicle = ValkyrienSkies.hasShipMount(entity);
        ClientShip ship = (ClientShip)vehicle;
        ShipTransform renderTransform = ship.getRenderTransform();
        Quaterniondc originalRotation = ((ShipRotation)ship).getOriginalRotation();
        Vector3dc worldPos = renderTransform.getPositionInWorld();
        double ex = Mth.lerp((double)renderPercent, (double)entity.xo, (double)entity.getX());
        double ey = Mth.lerp((double)renderPercent, (double)entity.yo, (double)entity.getY());
        double ez = Mth.lerp((double)renderPercent, (double)entity.zo, (double)entity.getZ());
        Vector3d relPos = worldPos.sub(ex, ey, ez, new Vector3d());
        Vector4f offset1 = originalRotation.invert(new Quaterniond()).transform(new Vector4f((float)relPos.x(), (float)relPos.y(), (float)relPos.z(), 1.0f));
        Vector4f offset2 = renderTransform.getShipToWorldRotation().transform(new Vector4f(offset1.x(), offset1.y(), offset1.z(), 1.0f));
        Vector4f offset = offset2.sub((Vector4fc)new Vector4f((float)relPos.x, (float)relPos.y, (float)relPos.z, 1.0f), new Vector4f());
        transformation.translate(-offset.x, -offset.y + (float)vehicle.getPhysicsYOffset(renderPercent), -offset.z);
    }

    public static float getEntityOffset(Entity entity, float renderPercent) {
        EntityOcean vehicle = ValkyrienSkies.hasShipMount(entity);
        ClientShip ship = (ClientShip)vehicle;
        ShipTransform renderTransform = ship.getRenderTransform();
        Quaterniondc originalRotation = ((ShipRotation)ship).getOriginalRotation();
        Vector3dc worldPos = renderTransform.getPositionInWorld();
        double ex = Mth.lerp((double)renderPercent, (double)entity.xo, (double)entity.getX());
        double ey = Mth.lerp((double)renderPercent, (double)entity.yo, (double)entity.getY());
        double ez = Mth.lerp((double)renderPercent, (double)entity.zo, (double)entity.getZ());
        Vector3d relPos = worldPos.sub(ex, ey, ez, new Vector3d());
        Vector4f offset1 = originalRotation.invert(new Quaterniond()).transform(new Vector4f((float)relPos.x(), (float)relPos.y(), (float)relPos.z(), 1.0f));
        Vector4f offset2 = renderTransform.getShipToWorldRotation().transform(new Vector4f(offset1.x(), offset1.y(), offset1.z(), 1.0f));
        Vector4f offset = offset2.sub((Vector4fc)new Vector4f((float)relPos.x, (float)relPos.y, (float)relPos.z, 1.0f), new Vector4f());
        return -offset.y + (float)vehicle.getPhysicsYOffset(renderPercent);
    }

    public static Vector3d getEntityOffset3D(Entity entity, float renderPercent) {
        EntityOcean vehicle = ValkyrienSkies.hasShipMount(entity);
        ClientShip ship = (ClientShip)vehicle;
        ShipTransform renderTransform = ship.getRenderTransform();
        Quaterniondc originalRotation = ((ShipRotation)ship).getOriginalRotation();
        Vector3dc worldPos = renderTransform.getPositionInWorld();
        double ex = Mth.lerp((double)renderPercent, (double)entity.xo, (double)entity.getX());
        double ey = Mth.lerp((double)renderPercent, (double)entity.yo, (double)entity.getY());
        double ez = Mth.lerp((double)renderPercent, (double)entity.zo, (double)entity.getZ());
        Vector3d relPos = worldPos.sub(ex, ey, ez, new Vector3d());
        Vector4f offset1 = originalRotation.invert(new Quaterniond()).transform(new Vector4f((float)relPos.x(), (float)relPos.y(), (float)relPos.z(), 1.0f));
        Vector4f offset2 = renderTransform.getShipToWorldRotation().transform(new Vector4f(offset1.x(), offset1.y(), offset1.z(), 1.0f));
        Vector4f offset = offset2.sub((Vector4fc)new Vector4f((float)relPos.x, (float)relPos.y, (float)relPos.z, 1.0f), new Vector4f());
        return new Vector3d((double)(-offset.x), (double)(-offset.y + (float)vehicle.getPhysicsYOffset(renderPercent)), (double)(-offset.z));
    }
}

