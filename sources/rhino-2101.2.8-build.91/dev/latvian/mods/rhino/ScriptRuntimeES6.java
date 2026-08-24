package dev.latvian.mods.rhino;

public class ScriptRuntimeES6 {
   public static Object requireObjectCoercible(Context cx, Object val, IdFunctionObject idFuncObj) {
      return requireObjectCoercible(cx, val, idFuncObj.getTag(), idFuncObj.getFunctionName());
   }

   public static Object requireObjectCoercible(Context cx, Object val, Object tag, Object methodName) {
      if (val != null && !Undefined.isUndefined(val)) {
         return val;
      } else {
         throw ScriptRuntime.typeError2(cx, "msg.called.null.or.undefined", tag, methodName);
      }
   }

   public static void addSymbolSpecies(Context cx, Scriptable scope, IdScriptableObject constructor) {
      ScriptableObject speciesDescriptor = (ScriptableObject)cx.newObject(scope);
      speciesDescriptor.put(cx, "enumerable", speciesDescriptor, Boolean.FALSE);
      speciesDescriptor.put(cx, "configurable", speciesDescriptor, Boolean.TRUE);
      speciesDescriptor.put(
         cx, "get", speciesDescriptor, new LambdaFunction(cx, scope, "get [Symbol.species]", 0, (lcx, lscope, thisObj, args) -> thisObj, false)
      );
      constructor.defineOwnProperty(cx, SymbolKey.SPECIES, speciesDescriptor, false);
   }
}
