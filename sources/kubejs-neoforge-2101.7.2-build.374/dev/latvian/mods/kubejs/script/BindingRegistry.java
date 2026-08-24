package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.Scriptable;

public record BindingRegistry(KubeJSContext context, Scriptable scope) {
   public ScriptType type() {
      return this.context.getType();
   }

   public void add(String name, Object value) {
      if (value != null) {
         this.context.addToScope(this.scope, name, value);
      }
   }
}
