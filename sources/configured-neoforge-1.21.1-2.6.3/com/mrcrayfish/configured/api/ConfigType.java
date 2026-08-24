package com.mrcrayfish.configured.api;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public enum ConfigType {
   CLIENT(Environment.CLIENT, false, false),
   UNIVERSAL(null, false, false),
   SERVER(null, true, false),
   SERVER_SYNC(null, true, true),
   DEDICATED_SERVER(Environment.DEDICATED_SERVER, true, false),
   WORLD(null, true, false),
   WORLD_SYNC(null, true, true),
   MEMORY(null, false, false);

   private final Environment env;
   private final boolean server;
   private final boolean sync;

   private ConfigType(@Nullable Environment env, boolean server, boolean sync) {
      this.env = env;
      this.server = server;
      this.sync = sync;
   }

   public Optional<Environment> getEnv() {
      return Optional.ofNullable(this.env);
   }

   public boolean isServer() {
      return this.server;
   }

   public boolean isSync() {
      return this.sync;
   }

   public boolean isWorld() {
      return this == WORLD || this == WORLD_SYNC;
   }
}
