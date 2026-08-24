package org.dimdev.limlib.api.world.chunk;

import net.minecraft.world.level.biome.BiomeSource;
import org.dimdev.limlib.api.world.NbtGroup;

public abstract class AbstractDynamicChunkGenerator extends AbstractNbtChunkGenerator implements DynamicNbtUpdater {
   public AbstractDynamicChunkGenerator(BiomeSource biomeSource, NbtGroup defaultNbtGroup) {
      super(biomeSource, defaultNbtGroup);
   }

   @Override
   public NbtGroup getGroup() {
      try {
         return this.getDynamicGroup();
      } catch (Exception var2) {
         return this.getStandardNbtGroup();
      }
   }

   public abstract NbtGroup getDynamicGroup();
}
