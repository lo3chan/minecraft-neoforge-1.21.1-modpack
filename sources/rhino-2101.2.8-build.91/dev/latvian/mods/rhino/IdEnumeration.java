package dev.latvian.mods.rhino;

public class IdEnumeration {
   Scriptable obj;
   Object[] ids;
   ObjToIntMap used;
   Object currentId;
   int index;
   int enumType;
   boolean enumNumbers;
   Scriptable iterator;

   public Boolean next(Context cx) {
      if (this.iterator != null) {
         if (this.enumType == 6) {
            return this.enumNextInOrder(cx);
         } else if (ScriptableObject.getProperty(this.iterator, "next", cx) instanceof Callable f) {
            try {
               this.currentId = f.call(cx, this.iterator.getParentScope(), this.iterator, ScriptRuntime.EMPTY_OBJECTS);
               return Boolean.TRUE;
            } catch (JavaScriptException var5) {
               if (var5.getValue() instanceof NativeIterator.StopIteration) {
                  return Boolean.FALSE;
               } else {
                  throw var5;
               }
            }
         } else {
            return Boolean.FALSE;
         }
      } else {
         while (true) {
            if (this.obj == null) {
               return Boolean.FALSE;
            }

            if (this.index != this.ids.length) {
               Object id = this.ids[this.index++];
               if ((this.used == null || !this.used.has(id)) && !(id instanceof Symbol)) {
                  if (id instanceof String strId) {
                     if (this.obj.has(cx, strId, this.obj)) {
                        this.currentId = strId;
                        break;
                     }
                  } else {
                     int intId = ((Number)id).intValue();
                     if (this.obj.has(cx, intId, this.obj)) {
                        this.currentId = this.enumNumbers ? intId : String.valueOf(intId);
                        break;
                     }
                  }
               }
            } else {
               this.obj = this.obj.getPrototype(cx);
               this.changeObject(cx);
            }
         }

         return Boolean.TRUE;
      }
   }

   private Boolean enumNextInOrder(Context cx) {
      if (ScriptableObject.getProperty(this.iterator, "next", cx) instanceof Callable f) {
         Scriptable scope = this.iterator.getParentScope();
         Object r = f.call(cx, scope, this.iterator, ScriptRuntime.EMPTY_OBJECTS);
         Scriptable iteratorResult = ScriptRuntime.toObject(cx, scope, r);
         Object done = ScriptableObject.getProperty(iteratorResult, "done", cx);
         if (done != Scriptable.NOT_FOUND && ScriptRuntime.toBoolean(cx, done)) {
            return Boolean.FALSE;
         } else {
            this.currentId = ScriptableObject.getProperty(iteratorResult, "value", cx);
            return Boolean.TRUE;
         }
      } else {
         throw ScriptRuntime.notFunctionError(cx, this.iterator, "next");
      }
   }

   public void changeObject(Context cx) {
      Object[] nids = null;

      while (this.obj != null) {
         nids = this.obj.getIds(cx);
         if (nids.length != 0) {
            break;
         }

         this.obj = this.obj.getPrototype(cx);
      }

      if (this.obj != null && this.ids != null) {
         Object[] previous = this.ids;
         int L = previous.length;
         if (this.used == null) {
            this.used = new ObjToIntMap(L);
         }

         for (int i = 0; i != L; i++) {
            this.used.intern(previous[i]);
         }
      }

      this.ids = nids;
      this.index = 0;
   }

   public Object getId(Context cx) {
      if (this.iterator != null) {
         return this.currentId;
      } else {
         switch (this.enumType) {
            case 0:
            case 3:
               return this.currentId;
            case 1:
            case 4:
               return this.getValue(cx);
            case 2:
            case 5:
               Object[] elements = new Object[]{this.currentId, this.getValue(cx)};
               return cx.newArray(ScriptableObject.getTopLevelScope(this.obj), elements);
            default:
               throw Kit.codeBug();
         }
      }
   }

   public Object getValue(Context cx) {
      Object result;
      if (ScriptRuntime.isSymbol(this.currentId)) {
         SymbolScriptable so = ScriptableObject.ensureSymbolScriptable(this.obj, cx);
         result = so.get(cx, (Symbol)this.currentId, this.obj);
      } else {
         ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, this.currentId);
         if (s.stringId == null) {
            result = this.obj.get(cx, s.index, this.obj);
         } else {
            result = this.obj.get(cx, s.stringId, this.obj);
         }
      }

      return result;
   }

   public Object nextExec(Context cx, Scriptable scope) {
      Boolean b = this.next(cx);
      if (!b) {
         throw new JavaScriptException(cx, NativeIterator.getStopIterationObject(scope, cx), null, 0);
      } else {
         return this.getId(cx);
      }
   }
}
