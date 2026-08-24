package org.dimdev.limlib.api.world.chunk;

import org.dimdev.limlib.api.world.FunctionMap;
import org.dimdev.limlib.api.world.NbtGroup;
import org.dimdev.limlib.api.world.NbtPlacerUtil;

public interface DynamicNbtUpdater {
   NbtGroup getGroup();

   default void update(AbstractNbtChunkGenerator chunkGenerator) {
      if (chunkGenerator.nbtGroup != this.getGroup()) {
         chunkGenerator.nbtGroup = this.getGroup();
         chunkGenerator.structures = new FunctionMap<>(NbtPlacerUtil::load);
         chunkGenerator.nbtGroup.fill(chunkGenerator.structures);
      }
   }
}
