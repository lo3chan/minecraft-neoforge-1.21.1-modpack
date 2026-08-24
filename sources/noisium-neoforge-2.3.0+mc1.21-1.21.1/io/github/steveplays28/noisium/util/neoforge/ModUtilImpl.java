package io.github.steveplays28.noisium.util.neoforge;

import net.neoforged.fml.loading.LoadingModList;

public class ModUtilImpl {
   public static boolean isModPresent(String id) {
      return LoadingModList.get().getModFileById(id) != null;
   }
}
