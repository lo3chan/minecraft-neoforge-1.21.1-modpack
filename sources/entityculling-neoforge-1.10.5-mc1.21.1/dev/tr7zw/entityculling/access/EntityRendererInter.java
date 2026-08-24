package dev.tr7zw.entityculling.access;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public interface EntityRendererInter<T extends Entity> {
   boolean shadowShouldShowName(T var1);

   void shadowRenderNameTag(T var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5, float var6);

   boolean entityCullingIgnoresCulling(T var1);

   AABB entityCullingGetCullingBox(T var1);
}
