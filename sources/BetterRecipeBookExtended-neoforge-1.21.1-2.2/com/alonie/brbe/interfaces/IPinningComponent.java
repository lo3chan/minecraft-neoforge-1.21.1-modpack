package com.alonie.brbe.interfaces;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.generic.pins.Pinnable;
import com.google.common.collect.Lists;
import java.util.List;

public interface IPinningComponent<T extends Pinnable> {
   default void brbe$sortByPinsInPlace(List<T> results) {
      for (T result : Lists.newArrayList(results)) {
         if (BetterRecipeBook.pinnedRecipeManager.has(result)) {
            results.remove(result);
            results.add(0, result);
         }
      }
   }
}
