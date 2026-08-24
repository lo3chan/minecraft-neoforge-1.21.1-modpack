package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeConsolidator;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class CachedExecutableInfo extends CachedMemberInfo {
   final Executable executable;
   private MethodSignature signature;
   private final int parameterCount;
   private CachedParameters parameters;

   public CachedExecutableInfo(CachedClassInfo parent, Executable e) {
      super(parent, e, e.getName(), (e instanceof Constructor ? 8 : 0) | e.getModifiers());
      this.executable = e;
      this.parameterCount = e.getParameterCount();
   }

   public Executable getCached() {
      return this.executable;
   }

   public MethodSignature getSignature() {
      if (this.signature == null) {
         this.signature = new MethodSignature(this.executable);
      }

      return this.signature;
   }

   public TypeInfo getReturnType() {
      return this.getDeclaringClass().getTypeInfo();
   }

   public CachedParameters getParameters() {
      CachedParameters v = this.parameters;
      if (v == null) {
         Class<?>[] tc = this.executable.getParameterTypes();
         TypeInfo[] ti = TypeInfo.safeOfArray(this.executable::getGenericParameterTypes);
         boolean fcx = tc.length > 0 && Context.class.isAssignableFrom(tc[0]);
         if (fcx) {
            Class<?>[] ntc = new Class[tc.length - 1];
            System.arraycopy(tc, 1, ntc, 0, ntc.length);
            tc = ntc;
            TypeInfo[] nti = new TypeInfo[ti.length - 1];
            System.arraycopy(ti, 1, nti, 0, nti.length);
            ti = nti;
         }

         boolean varArgs = this.executable.isVarArgs();
         if (fcx && ti.length == 0 && !varArgs) {
            v = this.parameters = CachedParameters.EMPTY_FIRST_CX;
         } else if (ti.length == 0 && !varArgs) {
            v = this.parameters = CachedParameters.EMPTY;
         } else {
            ti = TypeConsolidator.consolidateAll(ti, TypeConsolidator.getMapping(this.parent.type));
            v = this.parameters = new CachedParameters(tc.length, List.of(tc), List.of(ti), fcx, varArgs ? ti[ti.length - 1].componentType() : null);
         }
      }

      return v;
   }

   public Object[] transformArgs(Context cx, @Nullable Object instance, CachedParameters parameters, Object[] args) {
      Object[] iargs = args;
      boolean fcx = parameters.firstArgContext();
      int off = (instance != null ? 1 : 0) + (fcx ? 1 : 0);
      if (off > 0) {
         iargs = new Object[args.length + off];
         int pos = 0;
         if (instance != null) {
            iargs[pos++] = instance;
         }

         if (fcx) {
            iargs[pos] = cx;
         }

         System.arraycopy(args, 0, iargs, off, args.length);
      }

      return iargs;
   }

   public Object invoke(Context cx, Scriptable scope, @Nullable Object instance, Object[] args) throws Throwable {
      return null;
   }

   @Override
   public String toString() {
      return this.parent.getTypeInfo() + "#" + this.originalName + "(" + ".".repeat(this.parameterCount) + ")";
   }

   public void appendDebugParams(StringBuilder builder) {
      builder.append('(');
      CachedParameters params = this.getParameters();

      for (int i = 0; i < params.count(); i++) {
         if (i > 0) {
            builder.append(", ");
         }

         Class<?> type = params.types().get(i);
         if (params.isVarArg() && i == params.count() - 1 && type.isArray()) {
            this.parent.storage.get(type.componentType()).appendDebugType(builder);
            builder.append("...");
         } else {
            this.parent.storage.get(type).appendDebugType(builder);
         }
      }

      builder.append(')');
   }
}
