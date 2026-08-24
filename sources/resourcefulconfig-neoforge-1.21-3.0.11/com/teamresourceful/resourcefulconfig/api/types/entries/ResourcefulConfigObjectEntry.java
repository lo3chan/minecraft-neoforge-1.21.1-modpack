package com.teamresourceful.resourcefulconfig.api.types.entries;

import java.util.LinkedHashMap;
import org.jetbrains.annotations.NotNull;

public interface ResourcefulConfigObjectEntry extends ResourcefulConfigEntry {
   @NotNull
   LinkedHashMap<String, ResourcefulConfigEntry> entries();

   default Object instance() {
      return null;
   }
}
