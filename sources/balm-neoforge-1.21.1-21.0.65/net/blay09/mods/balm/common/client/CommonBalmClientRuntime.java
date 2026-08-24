package net.blay09.mods.balm.common.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.blay09.mods.balm.api.BalmRuntimeLoadContext;
import net.blay09.mods.balm.api.client.BalmClientRuntime;
import net.blay09.mods.balm.api.client.module.BalmClientModule;
import net.blay09.mods.balm.client.BalmClientRegistrars;

public abstract class CommonBalmClientRuntime<TLoadContext extends BalmRuntimeLoadContext> implements BalmClientRuntime<TLoadContext> {
   private static final List<Runnable> initCallbacks = Collections.synchronizedList(new ArrayList<>());
   private static final List<BalmClientModule> modules = Collections.synchronizedList(new ArrayList<>());
   private boolean ready;

   @Override
   public boolean isReady() {
      return this.ready;
   }

   @Override
   public void onRuntimeAvailable(Runnable callback) {
      initCallbacks.add(callback);
      if (this.isReady()) {
         callback.run();
      }
   }

   @Override
   public void registerModule(BalmClientModule module) {
      modules.add(module);
      this.initializeModule(module);
   }

   @Override
   public void registerModule(BalmClientRegistrars registrars, BalmClientModule module) {
      modules.add(module);
      this.initializeModule(module);
   }

   public void initializeRuntime() {
      this.ready = true;

      for (Runnable callback : initCallbacks) {
         callback.run();
      }
   }
}
