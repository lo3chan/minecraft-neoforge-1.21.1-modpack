package net.irisshaders.iris.mixin.vertices.block_rendering;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection.RebuildTask;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(
   value = {RebuildTask.class},
   priority = 999
)
public class MixinChunkRebuildTask {
   @Unique
   private final Object2IntMap<BlockState> blockStateIds = this.getBlockStateIds();

   @Unique
   private Object2IntMap<BlockState> getBlockStateIds() {
      return WorldRenderingSettings.INSTANCE.getBlockStateIds();
   }
}
