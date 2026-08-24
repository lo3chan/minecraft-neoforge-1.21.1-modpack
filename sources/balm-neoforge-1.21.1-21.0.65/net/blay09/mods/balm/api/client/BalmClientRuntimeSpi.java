package net.blay09.mods.balm.api.client;

import java.util.ServiceLoader;
import net.blay09.mods.balm.api.BalmRuntimeLoadContext;

public class BalmClientRuntimeSpi {
   public static BalmClientRuntime<BalmRuntimeLoadContext> create() {
      ServiceLoader<BalmClientRuntimeFactory> loader = ServiceLoader.load(BalmClientRuntimeFactory.class);
      BalmClientRuntimeFactory factory = loader.findFirst().orElseThrow();
      return (BalmClientRuntime<BalmRuntimeLoadContext>)factory.create();
   }
}
