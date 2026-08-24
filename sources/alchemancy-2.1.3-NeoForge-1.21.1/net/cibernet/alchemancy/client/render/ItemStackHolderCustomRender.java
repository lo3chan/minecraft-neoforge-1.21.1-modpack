package net.cibernet.alchemancy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ItemStackHolderCustomRender {
   @OnlyIn(Dist.CLIENT)
   void render(ItemRenderer var1, ItemStackHolderBlockEntity var2, float var3, PoseStack var4, MultiBufferSource var5, int var6, int var7);
}
