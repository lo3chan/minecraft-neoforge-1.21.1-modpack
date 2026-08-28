/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
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
        ArmorStand armorStand;
        if (entity instanceof ArmorStand && (armorStand = (ArmorStand)entity).isMarker()) {
            return EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(entity.position());
        }
        return entity.getBoundingBoxForCulling();
    }

    public static Vec3 getRenderOffset(EntityRenderer entityRenderer, Entity entity, float tickDelta) {
        return entityRenderer.getRenderOffset(entity, tickDelta);
    }
}

