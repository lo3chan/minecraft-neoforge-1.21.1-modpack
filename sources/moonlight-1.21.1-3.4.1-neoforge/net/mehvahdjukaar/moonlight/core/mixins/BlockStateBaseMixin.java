package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mehvahdjukaar.moonlight.api.block.IOptionalEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({BlockStateBase.class})
public abstract class BlockStateBaseMixin {
   @Shadow
   public abstract Block getBlock();

   @ModifyReturnValue(
      method = {"hasBlockEntity"},
      at = {@At("RETURN")}
   )
   private boolean vista$forceBlockEntity(boolean original) {
      return original && this.getBlock() instanceof IOptionalEntityBlock opt ? opt.shouldHaveBlockEntity((BlockStateBase)this) : original;
   }
}
