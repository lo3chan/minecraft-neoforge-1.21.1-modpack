package com.aetherteam.aether.mixin.mixins.client.accessor;

import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LevelRenderer.class})
public interface LevelRendererAccessor {
   @Accessor("starBuffer")
   VertexBuffer aether$getStarBuffer();

   @Accessor("skyBuffer")
   VertexBuffer aether$getSkyBuffer();

   @Accessor("cloudBuffer")
   VertexBuffer aether$getCloudBuffer();

   @Accessor("cloudBuffer")
   void aether$setCloudBuffer(VertexBuffer var1);

   @Accessor("prevCloudsType")
   CloudStatus aether$getPrevCloudsType();

   @Accessor("prevCloudsType")
   void aether$setPrevCloudsType(CloudStatus var1);

   @Accessor("generateClouds")
   boolean aether$isGenerateClouds();

   @Accessor("generateClouds")
   void aether$setGenerateClouds(boolean var1);

   @Invoker
   MeshData callBuildClouds(Tesselator var1, double var2, double var4, double var6, Vec3 var8);
}
