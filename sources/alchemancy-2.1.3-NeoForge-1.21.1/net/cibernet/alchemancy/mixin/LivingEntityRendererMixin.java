package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({LivingEntityRenderer.class})
public class LivingEntityRendererMixin {
   @ModifyArg(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
      ),
      index = 4
   )
   public int modifyColor(int color, @Local(argsOnly = true) LivingEntity entity) {
      if (!entity.hasData(AlchemancyDataAttachments.ENTITY_TINT.get())) {
         return color;
      } else {
         List<Integer> tint = (List<Integer>)entity.getData(AlchemancyDataAttachments.ENTITY_TINT.get());
         return tint.isEmpty() ? color : ColorUtils.interpolateColorsOverTime(1.0F, tint.stream().mapToInt(Integer::intValue).toArray());
      }
   }
}
