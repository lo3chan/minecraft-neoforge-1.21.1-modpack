package com.anthonyhilyard.prism.util;

import org.jetbrains.annotations.Nullable;

public interface IColor {
   @Nullable
   String getName();

   int getIntValue();

   boolean isAnimated();
}
