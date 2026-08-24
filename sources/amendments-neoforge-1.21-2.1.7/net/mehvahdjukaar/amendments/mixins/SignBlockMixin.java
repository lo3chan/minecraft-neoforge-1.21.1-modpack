package net.mehvahdjukaar.amendments.mixins;

import net.mehvahdjukaar.amendments.AmendmentsClient;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({StandingSignBlock.class})
public abstract class SignBlockMixin extends Block {
   public SignBlockMixin(Properties properties) {
      super(properties);
   }

   public RenderShape getRenderShape(BlockState state) {
      return PlatHelper.getPhysicalSide().isClient() && AmendmentsClient.WAS_INIT && ClientConfigs.isPixelConsistentSign(state)
         ? RenderShape.MODEL
         : super.getRenderShape(state);
   }
}
