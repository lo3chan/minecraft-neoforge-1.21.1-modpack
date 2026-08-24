package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class QuadWrapper_neoforge {
   private static final Minecraft MC = Minecraft.getInstance();
   private static final RandomSource RANDOM = RandomSource.create();
   private static final ReentrantLock GETTER_LOCK = new ReentrantLock();

   @Nullable
   public static List<BakedQuad> getUnculledQuads(BlockState blockState) throws Exception {
      return getQuadsForDirection(blockState, null);
   }

   @Nullable
   public static List<BakedQuad> getQuadsForDirection(BlockState blockState, @Nullable EDhDirection dhDirection) throws Exception {
      GETTER_LOCK.lock();

      List var4;
      try {
         Direction direction = McObjectConverter_neoforge.convert(dhDirection);
         List<BakedQuad> quads = MC.getModelManager().getBlockModelShaper().getBlockModel(blockState).getQuads(blockState, direction, RANDOM);
         var4 = quads;
      } finally {
         GETTER_LOCK.unlock();
      }

      return var4;
   }
}
