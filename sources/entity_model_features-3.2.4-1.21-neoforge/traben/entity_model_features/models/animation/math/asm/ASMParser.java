package traben.entity_model_features.models.animation.math.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.OldEMFAnimationHandler;
import traben.entity_model_features.utils.EMFUtils;

public class ASMParser {
   private static final AtomicLong id = new AtomicLong();
   private static final Pattern P_BASTORE_FLOAT_TO_INT = Pattern.compile("bastore.*Reason:.*Type float .* is not assignable to integer", 32);
   private static final Pattern P_FASTORE_INT_TO_FLOAT = Pattern.compile("fastore.*Reason:.*Type integer .* is not assignable to float", 32);
   private static final Pattern P_FLOAT_TO_INT = Pattern.compile("Reason:.*Type float .* is not assignable to integer", 32);
   private static final Pattern P_INT_TO_FLOAT = Pattern.compile("Reason:.*Type integer .* is not assignable to float", 32);

   public static ASMParser.ASMExecutor compileOrNull(OldEMFAnimationHandler animHandler, ASMVariableHandler varNames) {
      try {
         final String className = "traben.asm_generated.EMF_ASM_Parsed_" + id.incrementAndGet();
         ClassWriter cw = setupClass(className);
         MethodVisitor mv = cw.visitMethod(9, "eval", "([F[Z)V", null, null);
         mv.visitCode();

         for (int i = 0; i < animHandler.lines().size(); i++) {
            EMFAnimationHandler.AnimLineData line = animHandler.lines().get(i);
            MathComponent oldAnim = animHandler.oldAnimLines.get(line);

            assert oldAnim != null;

            varNames.scope(line.isBoolean);
            oldAnim.asmVisit(mv, varNames);
            line.asmIndex = varNames.asmStoreVar(mv, line.animKey);
            varNames.scopePop();
            varNames.verifyEndOfParse();
         }

         mv.visitInsn(177);
         mv.visitMaxs(0, 0);
         mv.visitEnd();
         cw.visitEnd();
         final byte[] bytes = cw.toByteArray();
         var loader = new ClassLoader(ASMHelper.class.getClassLoader()) {
            public Class<?> define() {
               return this.defineClass(className, bytes, 0, bytes.length);
            }
         };
         Class<?> clazz = loader.define();
         Lookup lookup = MethodHandles.lookup();
         MethodHandle mh = lookup.findStatic(clazz, "eval", MethodType.methodType(void.class, float[].class, boolean[].class));
         return (f, b) -> {
            try {
               mh.invokeExact((float[])f, (boolean[])b);
            } catch (Throwable var5x) {
               EMFUtils.logError(" Math error: " + animHandler + " = " + var5x.getMessage());
               var5x.printStackTrace();
               throw var5x;
            }
         };
      } catch (Throwable var10) {
         handleParseException(animHandler, var10);
         return null;
      }
   }

   @NotNull
   private static ClassWriter setupClass(String className) {
      String internal = className.replace('.', '/');
      ClassWriter cw = new ClassWriter(3);
      cw.visit(61, 33, internal, null, "java/lang/Object", null);
      MethodVisitor mv0 = cw.visitMethod(1, "<init>", "()V", null, null);
      mv0.visitCode();
      mv0.visitVarInsn(25, 0);
      mv0.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
      mv0.visitInsn(177);
      mv0.visitMaxs(1, 1);
      mv0.visitEnd();
      return cw;
   }

   private static void handleParseException(EMFAnimationHandler expression, Throwable e) {
      EMFUtils.logError("Failure parsing ASM:");
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      try {
         e.printStackTrace(pw);
      } catch (Throwable var9) {
         try {
            pw.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      pw.close();
      String var10 = sw.toString();
      boolean hadSimpleReason = true;
      if (P_BASTORE_FLOAT_TO_INT.matcher(var10).find()) {
         EMFUtils.logWarn(" - expected a boolean but found a number at the end of the expression?!");
      } else if (P_FASTORE_INT_TO_FLOAT.matcher(var10).find()) {
         EMFUtils.logWarn(" - expected a number but found a boolean at the end of the expression?!");
      } else if (P_FLOAT_TO_INT.matcher(var10).find()) {
         EMFUtils.logWarn(" - expected a boolean but found a number within the expression?!");
      } else if (P_INT_TO_FLOAT.matcher(var10).find()) {
         EMFUtils.logWarn(" - expected a number but found a boolean within the expression?!");
      } else {
         hadSimpleReason = false;
      }

      if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
         StringBuilder sb = new StringBuilder("Animations:\n");

         for (EMFAnimationHandler.AnimLineData it : expression.lines()) {
            sb.append("  - ").append(it.animKey).append(" : ").append(it.expression).append('\n');
         }

         EMFUtils.logWarn(sb.toString());
      }

      if (!hadSimpleReason || ((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
         e.printStackTrace();
      }
   }

   public interface ASMExecutor {
      void execute(float[] var1, boolean[] var2) throws Throwable;
   }
}
