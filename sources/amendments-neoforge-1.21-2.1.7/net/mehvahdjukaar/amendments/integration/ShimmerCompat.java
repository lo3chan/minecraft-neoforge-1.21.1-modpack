package net.mehvahdjukaar.amendments.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;

public class ShimmerCompat {
   public static void renderWithBloom(PoseStack poseStack, BiConsumer<PoseStack, MultiBufferSource> renderFunction) {
   }
}
