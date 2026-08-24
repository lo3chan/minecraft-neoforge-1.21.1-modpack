package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.NoiseChunk;

public interface EnhancedBeardifierData {
   @Nullable
   ObjectListIterator<EnhancedBeardifierRigid> yungsapi_getEnhancedPieceIterator();

   void yungsapi_setEnhancedPieces(ObjectList<EnhancedBeardifierRigid> var1);

   @Nullable
   ObjectListIterator<EnhancedJigsawJunction> yungsapi_getEnhancedJunctionIterator();

   void yungsapi_setEnhancedJunctions(ObjectList<EnhancedJigsawJunction> var1);

   @Nullable
   NoiseChunk yungsapi_getNoiseChunk();

   void yungsapi_setNoiseChunk(NoiseChunk var1);
}
