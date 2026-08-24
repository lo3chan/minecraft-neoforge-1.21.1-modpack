package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.ParameterizedTypeInfo;
import dev.latvian.mods.rhino.type.TypeConsolidator;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NativeJavaMethod extends BaseFunction {
   private static final int PREFERENCE_EQUAL = 0;
   private static final int PREFERENCE_FIRST_ARG = 1;
   private static final int PREFERENCE_SECOND_ARG = 2;
   private static final int PREFERENCE_AMBIGUOUS = 3;
   private final String functionName;
   private final transient CopyOnWriteArrayList<ResolvedOverload> overloadCache = new CopyOnWriteArrayList<>();
   public transient MemberBox[] methods;

   static String scriptSignature(Object[] values) {
      StringBuilder sig = new StringBuilder();

      for (int i = 0; i != values.length; i++) {
         Object value = values[i];
         String s;
         if (value == null) {
            s = "null";
         } else if (value instanceof Boolean) {
            s = "boolean";
         } else if (value instanceof String) {
            s = "string";
         } else if (value instanceof Number) {
            s = "number";
         } else if (value instanceof Scriptable) {
            if (value instanceof Undefined) {
               s = "undefined";
            } else if (value instanceof Wrapper) {
               Object wrapped = ((Wrapper)value).unwrap();
               s = wrapped.getClass().getName();
            } else if (value instanceof Function) {
               s = "function";
            } else {
               s = "object";
            }
         } else {
            s = JavaMembers.javaSignature(value.getClass());
         }

         if (i != 0) {
            sig.append(',');
         }

         sig.append(s);
      }

      return sig.toString();
   }

   static int findFunction(Context cx, MemberBox[] methodsOrCtors, Object[] args) {
      if (methodsOrCtors.length == 0) {
         return -1;
      } else if (methodsOrCtors.length == 1) {
         MemberBox member = methodsOrCtors[0];
         CachedParameters pars = member.parameters();
         int alength = member.parameters().count();
         if (pars.isVarArg()) {
            if (--alength > args.length) {
               return -1;
            }
         } else if (alength != args.length) {
            return -1;
         }

         for (int j = 0; j != alength; j++) {
            if (!cx.canConvert(args[j], pars.typeInfos().get(j))) {
               return -1;
            }
         }

         return 0;
      } else {
         int firstBestFit = -1;
         int[] extraBestFits = null;
         int extraBestFitsCount = 0;

         label139:
         for (int i = 0; i < methodsOrCtors.length; i++) {
            MemberBox member = methodsOrCtors[i];
            CachedParameters pars = member.parameters();
            int alength = pars.count();
            if (pars.isVarArg() ? --alength <= args.length : alength == args.length) {
               for (int jx = 0; jx < alength; jx++) {
                  if (!cx.canConvert(args[jx], pars.typeInfos().get(jx))) {
                     continue label139;
                  }
               }

               if (firstBestFit < 0) {
                  firstBestFit = i;
               } else {
                  int betterCount = 0;
                  int worseCount = 0;

                  for (int jxx = -1; jxx != extraBestFitsCount; jxx++) {
                     int bestFitIndex;
                     if (jxx == -1) {
                        bestFitIndex = firstBestFit;
                     } else {
                        bestFitIndex = extraBestFits[jxx];
                     }

                     MemberBox bestFit = methodsOrCtors[bestFitIndex];
                     CachedParameters bestFitPars = bestFit.parameters();
                     int preference = preferSignature(cx, args, pars.typeInfos(), pars.isVarArg(), bestFitPars.typeInfos(), bestFitPars.isVarArg());
                     if (preference == 3) {
                        break;
                     }

                     if (preference == 1) {
                        betterCount++;
                     } else {
                        if (preference != 2) {
                           if (preference != 0) {
                              Kit.codeBug();
                           }

                           if (bestFit.isStatic()
                              && bestFit.executableInfo.getDeclaringClass().type.isAssignableFrom(member.executableInfo.getDeclaringClass().type)) {
                              if (jxx == -1) {
                                 firstBestFit = i;
                              } else {
                                 extraBestFits[jxx] = i;
                              }
                           }
                           continue label139;
                        }

                        worseCount++;
                     }
                  }

                  if (betterCount == 1 + extraBestFitsCount) {
                     firstBestFit = i;
                     extraBestFitsCount = 0;
                  } else if (worseCount != 1 + extraBestFitsCount) {
                     if (extraBestFits == null) {
                        extraBestFits = new int[methodsOrCtors.length - 1];
                     }

                     extraBestFits[extraBestFitsCount] = i;
                     extraBestFitsCount++;
                  }
               }
            }
         }

         if (firstBestFit < 0) {
            return -1;
         } else if (extraBestFitsCount == 0) {
            return firstBestFit;
         } else {
            StringBuilder buf = new StringBuilder();

            for (int jxx = -1; jxx != extraBestFitsCount; jxx++) {
               int bestFitIndexx;
               if (jxx == -1) {
                  bestFitIndexx = firstBestFit;
               } else {
                  bestFitIndexx = extraBestFits[jxx];
               }

               buf.append("\n    ");
               buf.append(methodsOrCtors[bestFitIndexx].toJavaDeclaration());
            }

            MemberBox firstFitMember = methodsOrCtors[firstBestFit];
            String memberName = firstFitMember.getName();
            String memberClass = firstFitMember.executableInfo.getDeclaringClass().type.getName();
            if (methodsOrCtors[0].isCtor()) {
               throw Context.reportRuntimeError3("msg.constructor.ambiguous", memberName, scriptSignature(args), buf.toString(), cx);
            } else {
               throw Context.reportRuntimeError4("msg.method.ambiguous", memberClass, memberName, scriptSignature(args), buf.toString(), cx);
            }
         }
      }
   }

   private static int preferSignature(Context cx, Object[] args, List<TypeInfo> sig1, boolean vararg1, List<TypeInfo> sig2, boolean vararg2) {
      int totalPreference = 0;

      for (int j = 0; j < args.length; j++) {
         TypeInfo type1 = vararg1 && j >= sig1.size() ? (TypeInfo)sig1.getLast() : sig1.get(j);
         TypeInfo type2 = vararg2 && j >= sig2.size() ? (TypeInfo)sig2.getLast() : sig2.get(j);
         if (!type1.equals(type2)) {
            Object arg = args[j];
            int rank1 = cx.getConversionWeight(arg, type1);
            int rank2 = cx.getConversionWeight(arg, type2);
            int preference;
            if (rank1 < rank2) {
               preference = 1;
            } else if (rank1 > rank2) {
               preference = 2;
            } else if (rank1 == 0) {
               if (type1.asClass().isAssignableFrom(type2.asClass())) {
                  preference = 2;
               } else if (type2.asClass().isAssignableFrom(type1.asClass())) {
                  preference = 1;
               } else {
                  preference = 3;
               }
            } else {
               preference = 3;
            }

            totalPreference |= preference;
            if (totalPreference == 3) {
               break;
            }
         }
      }

      return totalPreference;
   }

   NativeJavaMethod(MemberBox[] methods) {
      this.functionName = methods[0].getName();
      this.methods = methods;
   }

   NativeJavaMethod(MemberBox[] methods, String name) {
      this.functionName = name;
      this.methods = methods;
   }

   NativeJavaMethod(MemberBox method, String name) {
      this.functionName = name;
      this.methods = new MemberBox[]{method};
   }

   public NativeJavaMethod(CachedMethodInfo method, String name) {
      this(new MemberBox(method), name);
   }

   @Override
   public String getFunctionName() {
      return this.functionName;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      int i = 0;

      for (int N = this.methods.length; i != N; i++) {
         if (i > 0) {
            sb.append('\n');
         }

         if (this.methods[i].isMethod()) {
            sb.append(JavaMembers.javaSignature(this.methods[i].getReturnType().asClass()));
            sb.append(' ');
            sb.append(this.methods[i].getName());
         } else {
            sb.append(this.methods[i].getName());
         }

         sb.append(JavaMembers.liveConnectSignature(this.methods[i].parameters().types()));
      }

      return sb.toString();
   }

   @Override
   public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if (this.methods.length == 0) {
         throw new RuntimeException("No methods defined for call");
      } else {
         int index = this.findCachedFunction(cx, args);
         if (index < 0) {
            Class<?> c = this.methods[0].executableInfo.getDeclaringClass().type;
            String sig = c.getName() + "." + this.getFunctionName() + "(" + scriptSignature(args) + ")";
            throw Context.reportRuntimeError1("msg.java.no_such_method", sig, cx);
         } else {
            MemberBox meth = this.methods[index];
            CachedParameters pars = meth.parameters();
            List<TypeInfo> argTypes = pars.typeInfos();
            if (thisObj instanceof NativeJavaObject nativeJavaObject && nativeJavaObject.typeInfo instanceof ParameterizedTypeInfo ignored) {
               argTypes = TypeConsolidator.consolidateAll(argTypes, nativeJavaObject.getTypeMapping());
            }

            if (pars.isVarArg()) {
               Object[] newArgs = new Object[argTypes.size()];

               for (int i = 0; i < argTypes.size() - 1; i++) {
                  newArgs[i] = cx.jsToJava(args[i], argTypes.get(i));
               }

               Object varArgs;
               if (args.length != argTypes.size()
                  || args[args.length - 1] != null && !(args[args.length - 1] instanceof NativeArray) && !(args[args.length - 1] instanceof NativeJavaArray)) {
                  TypeInfo componentType = ((TypeInfo)argTypes.getLast()).componentType();
                  varArgs = Array.newInstance(componentType.asClass(), args.length - argTypes.size() + 1);
                  int len = Array.getLength(varArgs);

                  for (int i = 0; i < len; i++) {
                     Object value = cx.jsToJava(args[argTypes.size() - 1 + i], componentType);
                     Array.set(varArgs, i, value);
                  }
               } else {
                  varArgs = cx.jsToJava(args[args.length - 1], (TypeInfo)argTypes.getLast());
               }

               newArgs[argTypes.size() - 1] = varArgs;
               args = newArgs;
            } else {
               Object[] origArgs = args;

               for (int i = 0; i < args.length; i++) {
                  Object arg = args[i];
                  Object var29 = cx.jsToJava(arg, argTypes.get(i));
                  if (var29 != arg) {
                     if (origArgs == args) {
                        args = (Object[])args.clone();
                     }

                     args[i] = var29;
                  }
               }
            }

            Object javaObject;
            if (meth.isStatic()) {
               javaObject = null;
            } else {
               Scriptable o = thisObj;
               Class<?> c = meth.executableInfo.getDeclaringClass().type;

               while (true) {
                  if (o == null) {
                     throw Context.reportRuntimeError3("msg.nonjava.method", this.getFunctionName(), ScriptRuntime.toString(cx, thisObj), c.getName(), cx);
                  }

                  if (o instanceof Wrapper) {
                     javaObject = ((Wrapper)o).unwrap();
                     if (c.isInstance(javaObject)) {
                        break;
                     }
                  }

                  o = o.getPrototype(cx);
               }
            }

            Object retval = meth.invoke(javaObject, args, cx, scope);
            TypeInfo returnType = meth.getReturnType();
            if (thisObj instanceof NativeJavaObject nativeJavaObject) {
               returnType = returnType.consolidate(nativeJavaObject.getTypeMapping());
            }

            Object wrapped = cx.wrap(scope, retval, returnType);
            if (wrapped == null && returnType.isVoid()) {
               wrapped = Undefined.INSTANCE;
            }

            return wrapped;
         }
      }
   }

   int findCachedFunction(Context cx, Object[] args) {
      if (this.methods.length > 1) {
         for (ResolvedOverload ovl : this.overloadCache) {
            if (ovl.matches(args)) {
               return ovl.index;
            }
         }

         int index = findFunction(cx, this.methods, args);
         if (this.overloadCache.size() < this.methods.length * 2) {
            ResolvedOverload ovlx = new ResolvedOverload(args, index);
            this.overloadCache.addIfAbsent(ovlx);
         }

         return index;
      } else {
         return findFunction(cx, this.methods, args);
      }
   }
}
