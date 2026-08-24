package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({RenderTypeHelper.class})
public class RenderTypeHelperMixin {
   @WrapOperation(
      method = {"getFallbackItemRenderType"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/neoforged/neoforge/client/ChunkRenderTypeSet;contains(Lnet/minecraft/client/renderer/RenderType;)Z"
      )}
   )
   private static boolean isTranslucent(
      ChunkRenderTypeSet instance, RenderType renderType, Operation<Boolean> original, @Local(argsOnly = true) ItemStack stack
   ) {
      return !original.call(new Object[]{instance, renderType}) ? CommonUtils.hasPropertyDrivenAlpha(stack) : true;
   }
}
