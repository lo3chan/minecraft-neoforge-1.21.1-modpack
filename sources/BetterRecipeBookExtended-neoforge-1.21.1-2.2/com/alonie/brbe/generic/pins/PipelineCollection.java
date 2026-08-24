package com.alonie.brbe.generic.pins;

import java.util.List;

public interface PipelineCollection extends Pinnable {
   List<?> getRecipes();

   boolean hasAnyCraftable();

   boolean hasAnyPartiallyCraftable();
}
