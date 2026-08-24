package com.finndog.moogs_structures.world.structures.terrainadaptation.beardifier;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;

public interface EnhancedBeardifierData {
   ObjectListIterator<EnhancedBeardifierRigid> moogs_structures_getEnhancedPieceIterator();

   void moogs_structures_setEnhancedPieceIterator(ObjectListIterator<EnhancedBeardifierRigid> var1);

   ObjectListIterator<EnhancedJigsawJunction> moogs_structures_getEnhancedJunctionIterator();

   void moogs_structures_setEnhancedJunctionIterator(ObjectListIterator<EnhancedJigsawJunction> var1);
}
