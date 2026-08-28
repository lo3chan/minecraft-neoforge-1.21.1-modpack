/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.tr7zw.transition.mc.GeneralUtil
 *  dev.tr7zw.transition.mc.MathUtil
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.MapItem
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.saveddata.maps.MapId
 *  net.minecraft.world.level.saveddata.maps.MapItemSavedData
 *  org.joml.Matrix4f
 */
package dev.tr7zw.notenoughanimations.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.transition.mc.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;

public class MapRenderer {
    private static final RenderType MAP_BACKGROUND = RenderType.text((ResourceLocation)GeneralUtil.getResourceLocation((String)"textures/map/map_background.png"));
    private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text((ResourceLocation)GeneralUtil.getResourceLocation((String)"textures/map/map_background_checkerboard.png"));

    public static void renderFirstPersonMap(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, boolean small, boolean leftHanded) {
        Minecraft client = Minecraft.getInstance();
        if (small) {
            matrices.mulPose(MathUtil.YP.rotationDegrees(160.0f));
            matrices.mulPose(MathUtil.ZP.rotationDegrees(180.0f));
            matrices.scale(0.38f, 0.38f, 0.38f);
            matrices.translate(-0.1, -1.2, 0.0);
            matrices.scale(0.0098125f, 0.0098125f, 0.0098125f);
        } else {
            if (leftHanded) {
                matrices.mulPose(MathUtil.YP.rotationDegrees(154.5f));
                matrices.mulPose(MathUtil.ZP.rotationDegrees(166.5f));
                matrices.scale(0.38f, 0.38f, 0.38f);
                matrices.translate(0.585, -1.225, 0.15);
            } else {
                matrices.mulPose(MathUtil.YP.rotationDegrees(155.0f));
                matrices.mulPose(MathUtil.ZP.rotationDegrees(213.5f));
                matrices.scale(0.38f, 0.38f, 0.38f);
                matrices.translate(-0.955, -1.8, 0.0);
            }
            matrices.scale(0.0138125f, 0.0138125f, 0.0138125f);
        }
        MapId mapid = (MapId)stack.get(DataComponents.MAP_ID);
        MapItemSavedData mapState = MapItem.getSavedData((ItemStack)stack, (Level)client.level);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = matrices.last().pose();
        MapRenderer.addVertex(vertexConsumer, matrix4f, -7.0f, 135.0f, 0.0f, 0.0f, 1.0f, light);
        MapRenderer.addVertex(vertexConsumer, matrix4f, 135.0f, 135.0f, 0.0f, 1.0f, 1.0f, light);
        MapRenderer.addVertex(vertexConsumer, matrix4f, 135.0f, -7.0f, 0.0f, 1.0f, 0.0f, light);
        MapRenderer.addVertex(vertexConsumer, matrix4f, -7.0f, -7.0f, 0.0f, 0.0f, 0.0f, light);
        vertexConsumer = vertexConsumers.getBuffer(MAP_BACKGROUND);
        MapRenderer.addVertex(vertexConsumer, matrix4f, -7.0f, -7.0f, 0.0f, 0.0f, 0.0f, light);
        MapRenderer.addVertex(vertexConsumer, matrix4f, 135.0f, -7.0f, 0.0f, 1.0f, 0.0f, light);
        MapRenderer.addVertex(vertexConsumer, matrix4f, 135.0f, 135.0f, 0.0f, 1.0f, 1.0f, light);
        MapRenderer.addVertex(vertexConsumer, matrix4f, -7.0f, 135.0f, 0.0f, 0.0f, 1.0f, light);
        if (mapState != null) {
            client.gameRenderer.getMapRenderer().render(matrices, vertexConsumers, mapid, mapState, false, light);
        }
    }

    public static void addVertex(VertexConsumer cons, Matrix4f matrix4f, float x, float y, float z, float u, float v, int lightmapUV) {
        cons.addVertex(matrix4f, x, y, z).setColor(-1).setUv(u, v).setLight(lightmapUV);
    }
}

