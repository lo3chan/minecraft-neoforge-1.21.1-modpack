package com.anthonyhilyard.iceberg.services;

import java.util.List;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   List<String> getAllModIds();

   boolean modVersionMeets(String var1, String var2);
}
