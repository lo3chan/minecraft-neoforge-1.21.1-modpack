package net.raphimc.immediatelyfastapi;

public interface ApiAccess {
   @Deprecated
   BatchingAccess getBatching();

   ConfigAccess getConfig();

   ConfigAccess getRuntimeConfig();
}
