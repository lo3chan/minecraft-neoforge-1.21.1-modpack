package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.ContextFactory;

public class KubeJSContextFactory extends ContextFactory {
   public final ScriptManager manager;

   public KubeJSContextFactory(ScriptManager manager) {
      this.manager = manager;
   }

   protected KubeJSContext createContext() {
      return (KubeJSContext)(this.manager.scriptType.isServer() ? new KubeJSServerContext(this) : new KubeJSContext(this));
   }
}
