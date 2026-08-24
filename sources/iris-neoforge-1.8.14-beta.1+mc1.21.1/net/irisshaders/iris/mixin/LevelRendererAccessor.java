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

@Mixin({LevelRenderer.class})
public interface LevelRendererAccessor {
   @Accessor("entityRenderDispatcher")
   EntityRenderDispatcher getEntityRenderDispatcher();

   @Invoker("renderSectionLayer")
   void invokeRenderSectionLayer(RenderType var1, double var2, double var4, double var6, Matrix4f var8, Matrix4f var9);

   @Invoker("setupRender")
   void invokeSetupRender(Camera var1, Frustum var2, boolean var3, boolean var4);

   @Invoker("renderEntity")
   void invokeRenderEntity(Entity var1, double var2, double var4, double var6, float var8, PoseStack var9, MultiBufferSource var10);

   @Accessor("level")
   ClientLevel getLevel();

   @Accessor("renderBuffers")
   RenderBuffers getRenderBuffers();

   @Accessor("renderBuffers")
   void setRenderBuffers(RenderBuffers var1);

   @Accessor("generateClouds")
   boolean shouldRegenerateClouds();

   @Accessor("generateClouds")
   void setShouldRegenerateClouds(boolean var1);

   @Invoker
   boolean invokeDoesMobEffectBlockSky(Camera var1);

   @Accessor
   Long2ObjectMap<SortedSet<BlockDestructionProgress>> getDestructionProgress();
}
