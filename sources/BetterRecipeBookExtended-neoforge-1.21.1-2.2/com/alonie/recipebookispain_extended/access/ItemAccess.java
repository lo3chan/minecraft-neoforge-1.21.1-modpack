package com.alonie.recipebookispain_extended.access;

import java.util.Optional;
import net.minecraft.world.item.CreativeModeTab;

public interface ItemAccess {
   Optional<CreativeModeTab> rbip$getPossibleGroup();

   void rbip$setPossibleGroup(CreativeModeTab var1);
}
