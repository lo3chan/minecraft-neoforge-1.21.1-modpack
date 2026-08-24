package net.blay09.mods.balm.api;

import java.util.ServiceLoader;

public class BalmRuntimeSpi {
   public static BalmRuntime<BalmRuntimeLoadContext> create() {
      ServiceLoader<BalmRuntimeFactory> loader = ServiceLoader.load(BalmRuntimeFactory.class);
      BalmRuntimeFactory factory = loader.findFirst().orElseThrow();
      return (BalmRuntime<BalmRuntimeLoadContext>)factory.create();
   }
}
