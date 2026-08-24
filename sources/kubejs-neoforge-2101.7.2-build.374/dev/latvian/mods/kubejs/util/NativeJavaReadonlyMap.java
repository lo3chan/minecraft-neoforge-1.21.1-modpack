package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.Map;

public class NativeJavaReadonlyMap extends NativeJavaMap {
   private final String errorMessage;

   public NativeJavaReadonlyMap(Context cx, Scriptable scope, Object jo, Map map, TypeInfo type, String errorMessage) {
      super(cx, scope, jo, map, type);
      this.errorMessage = errorMessage;
   }

   public String getClassName() {
      return "JavaReadonlyMap";
   }

   public void put(Context cx, String name, Scriptable start, Object value) {
      throw new KubeRuntimeException(this.errorMessage).source(SourceLine.of(cx));
   }

   public void put(Context cx, int index, Scriptable start, Object value) {
      throw new KubeRuntimeException(this.errorMessage).source(SourceLine.of(cx));
   }

   public void delete(Context cx, String name) {
      throw new KubeRuntimeException(this.errorMessage).source(SourceLine.of(cx));
   }

   public void delete(Context cx, int index) {
      throw new KubeRuntimeException(this.errorMessage).source(SourceLine.of(cx));
   }
}
