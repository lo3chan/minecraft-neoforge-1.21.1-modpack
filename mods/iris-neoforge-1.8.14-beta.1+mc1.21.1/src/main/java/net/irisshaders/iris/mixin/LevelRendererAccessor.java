/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.server.level.BlockDestructionProgress
 *  net.minecraft.world.entity.Entity
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.SortedSet;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={LevelRenderer.class})
public interface LevelRendererAccessor {
    @Accessor(value="entityRenderDispatcher")
    public EntityRenderDispatcher getEntityRenderDispatcher();

    @Invoker(value="renderSectionLayer")
    public void invokeRenderSectionLayer(RenderType var1, double var2, double var4, double var6, Matrix4f var8, Matrix4f var9);

    @Invoker(value="setupRender")
    public void invokeSetupRender(Camera var1, Frustum var2, boolean var3, boolean var4);

    @Invoker(value="renderEntity")
    public void invokeRenderEntity(Entity var1, double var2, double var4, double var6, float var8, PoseStack var9, MultiBufferSource var10);

    @Accessor(value="level")
    public ClientLevel getLevel();

    @Accessor(value="renderBuffers")
    public RenderBuffers getRenderBuffers();

    @Accessor(value="renderBuffers")
    public void setRenderBuffers(RenderBuffers var1);

    @Accessor(value="generateClouds")
    public boolean shouldRegenerateClouds();

    @Accessor(value="generateClouds")
    public void setShouldRegenerateClouds(boolean var1);

    @Invoker
    public boolean invokeDoesMobEffectBlockSky(Camera var1);

    @Accessor
    public Long2ObjectMap<SortedSet<BlockDestructionProgress>> getDestructionProgress();
}

