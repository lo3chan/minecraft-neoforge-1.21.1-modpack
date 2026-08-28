/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 */
package com.sonicether.soundphysics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.utils.RenderTypeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RaycastRenderer {
    private static final List<Ray> rays = Collections.synchronizedList(new ArrayList());
    private static final Minecraft mc = Minecraft.getInstance();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderRays(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double x, double y, double z) {
        if (RaycastRenderer.mc.level == null) {
            return;
        }
        if (!SoundPhysicsMod.CONFIG.renderSoundBounces.get().booleanValue() && !SoundPhysicsMod.CONFIG.renderOcclusion.get().booleanValue()) {
            List<Ray> list = rays;
            synchronized (list) {
                rays.clear();
            }
            return;
        }
        long gameTime = RaycastRenderer.mc.level.getGameTime();
        List<Ray> list = rays;
        synchronized (list) {
            rays.removeIf(ray -> gameTime - ray.tickCreated > ray.lifespan || gameTime - ray.tickCreated < 0L);
            for (Ray ray2 : rays) {
                RaycastRenderer.renderRay(ray2, poseStack, bufferSource, x, y, z);
            }
        }
    }

    public static void addSoundBounceRay(Vec3 start, Vec3 end, int color) {
        if (!SoundPhysicsMod.CONFIG.renderSoundBounces.get().booleanValue()) {
            return;
        }
        RaycastRenderer.addRay(start, end, color, false);
    }

    public static void addOcclusionRay(Vec3 start, Vec3 end, int color) {
        if (!SoundPhysicsMod.CONFIG.renderOcclusion.get().booleanValue()) {
            return;
        }
        RaycastRenderer.addRay(start, end, color, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addRay(Vec3 start, Vec3 end, int color, boolean throughWalls) {
        if (RaycastRenderer.mc.player.position().distanceTo(start) > 32.0 && RaycastRenderer.mc.player.position().distanceTo(end) > 32.0) {
            return;
        }
        List<Ray> list = rays;
        synchronized (list) {
            rays.add(new Ray(start, end, color, throughWalls));
        }
    }

    public static void renderRay(Ray ray, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double x, double y, double z) {
        poseStack.pushPose();
        int red = RaycastRenderer.getRed(ray.color);
        int green = RaycastRenderer.getGreen(ray.color);
        int blue = RaycastRenderer.getBlue(ray.color);
        VertexConsumer consumer = ray.throughWalls ? bufferSource.getBuffer((RenderType)RenderTypeUtils.DEBUG_LINE_STRIP_SEETHROUGH) : bufferSource.getBuffer(RenderTypeUtils.DEBUG_LINE_STRIP);
        Matrix4f matrix4f = poseStack.last().pose();
        consumer.addVertex(matrix4f, (float)(ray.start.x - x), (float)(ray.start.y - y), (float)(ray.start.z - z)).setColor(red, green, blue, 255);
        consumer.addVertex(matrix4f, (float)(ray.end.x - x), (float)(ray.end.y - y), (float)(ray.end.z - z)).setColor(red, green, blue, 255);
        poseStack.popPose();
    }

    private static int getRed(int argb) {
        return argb >> 16 & 0xFF;
    }

    private static int getGreen(int argb) {
        return argb >> 8 & 0xFF;
    }

    private static int getBlue(int argb) {
        return argb & 0xFF;
    }

    private static class Ray {
        private final Vec3 start;
        private final Vec3 end;
        private final int color;
        private final long tickCreated;
        private final long lifespan;
        private final boolean throughWalls;

        public Ray(Vec3 start, Vec3 end, int color, boolean throughWalls) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.throughWalls = throughWalls;
            this.tickCreated = RaycastRenderer.mc.level.getGameTime();
            this.lifespan = 40L;
        }
    }
}

