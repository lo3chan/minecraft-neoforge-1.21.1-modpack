package traben.entity_model_features.models.animation.math.asm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.math.EMFMathException;

public abstract class ASMHelper {
   public static void visitHelperFunctionASM(MethodVisitor mv, String name) throws EMFMathException {
      visitStaticFunctionASM(mv, name, ASMHelper.class);
   }

   public static Method getHelperMethod(String name) throws EMFMathException {
      Optional<Method> method = Arrays.stream(ASMHelper.class.getMethods()).filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
      if (method.isPresent()) {
         return method.get();
      } else {
         throw new EMFMathException("Method [" + name + "] not found in ASMHelper");
      }
   }

   @Nullable
   public static ASMVisitable getHelperMethodCompiler(String name) {
      Optional<Method> method = Arrays.stream(ASMHelper.class.getMethods()).filter(m -> m.getName().equalsIgnoreCase(name + "_ASM")).findFirst();
      if (method.isPresent()) {
         try {
            Lookup lookup = MethodHandles.lookup();
            MethodHandle target = lookup.unreflect(method.get());
            return MethodHandleProxies.asInterfaceInstance(ASMVisitable.class, target);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      return null;
   }

   public static Method getStaticMethod(String name, Class<?> clazz) throws EMFMathException {
      Optional<Method> method = Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
      if (method.isPresent()) {
         return method.get();
      } else {
         throw new EMFMathException("Method [" + name + "] not found in " + clazz.getName());
      }
   }

   public static void visitStaticFunctionASM(MethodVisitor mv, String name, Class<?> clazz) throws EMFMathException {
      String[] func = functionName(name, clazz);
      mv.visitMethodInsn(184, func[0], func[1], func[2], false);
   }

   public static void visitStaticFunctionASM(MethodVisitor mv, Method method) {
      String[] func = fromMethod(method.getDeclaringClass(), method);
      mv.visitMethodInsn(184, func[0], func[1], func[2], false);
   }

   public static void asmIfZeroOrGreater(MethodVisitor mv, ASMHelper.ThrowingRunnable trueBlock, ASMHelper.ThrowingRunnable falseBlock) throws EMFMathException {
      Label labelTrue = new Label();
      Label end = new Label();
      mv.visitInsn(89);
      mv.visitInsn(11);
      mv.visitInsn(150);
      mv.visitJumpInsn(156, labelTrue);
      falseBlock.run();
      mv.visitJumpInsn(167, end);
      mv.visitLabel(labelTrue);
      trueBlock.run();
      mv.visitLabel(end);
   }

   public static void asmIf(MethodVisitor mv, ASMHelper.ThrowingRunnable trueBlock, ASMHelper.ThrowingRunnable falseBlock) throws EMFMathException {
      Label labelTrue = new Label();
      Label end = new Label();
      mv.visitJumpInsn(154, labelTrue);
      falseBlock.run();
      mv.visitJumpInsn(167, end);
      mv.visitLabel(labelTrue);
      trueBlock.run();
      mv.visitLabel(end);
   }

   public static float sin(float f) {
      return Mth.sin(f);
   }

   public static float cos(float f) {
      return Mth.cos(f);
   }

   public static float asin(float f) {
      return (float)Math.asin(f);
   }

   public static float acos(float f) {
      return (float)Math.acos(f);
   }

   public static float tan(float f) {
      return (float)Math.tan(f);
   }

   public static float atan(float f) {
      return (float)Math.atan(f);
   }

   public static float abs(float f) {
      return Mth.abs(f);
   }

   public static float floor(float f) {
      return Mth.floor(f);
   }

   public static float ceil(float f) {
      return Mth.ceil(f);
   }

   public static float round(float f) {
      return Math.round(f);
   }

   public static float log(float f) {
      return f < 0.0F && EMFManager.getInstance().isAnimationValidationPhase ? 0.0F : (float)Math.log(f);
   }

   public static float _log(float f) {
      return (float)Math.log(f);
   }

   public static void log_ASM(MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException {
      asmIfZeroOrGreater(mv, () -> visitHelperFunctionASM(mv, "_log"), () -> {
         mv.visitInsn(87);
         mv.visitInsn(11);
      });
   }

   public static float exp(float f) {
      return (float)Math.exp(f);
   }

   public static float torad(float f) {
      return f * 0.017453292F;
   }

   public static void torad_ASM(MethodVisitor mv, ASMVariableHandler varNames) {
      mv.visitLdcInsn(0.017453292F);
      mv.visitInsn(106);
   }

   public static float todeg(float f) {
      return f * 57.295776F;
   }

   public static void todeg_ASM(MethodVisitor mv, ASMVariableHandler varNames) {
      mv.visitLdcInsn(57.295776F);
      mv.visitInsn(106);
   }

   public static float frac(float f) {
      return Mth.frac(f);
   }

   public static float signum(float f) {
      return Math.signum(f);
   }

   public static float sqrt(float f) {
      return f < 0.0F && EMFManager.getInstance().isAnimationValidationPhase ? 0.0F : Mth.sqrt(f);
   }

   public static float _sqrt(float f) {
      return Mth.sqrt(f);
   }

   public static void sqrt_ASM(MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException {
      asmIfZeroOrGreater(mv, () -> visitHelperFunctionASM(mv, "_sqrt"), () -> {
         mv.visitInsn(87);
         mv.visitInsn(11);
      });
   }

   public static float fmod(float f, float f2) {
      return Math.floorMod((int)f, (int)f2);
   }

   public static float pow(float f, float f2) {
      return (float)Math.pow(f, f2);
   }

   public static float atan2(float f, float f2) {
      return (float)Mth.atan2(f, f2);
   }

   public static float clamp(float f, float f2, float f3) {
      return Mth.clamp(f, f2, f3);
   }

   public static float lerp(float f, float f2, float f3) {
      return Mth.lerp(f, f2, f3);
   }

   public static float wrapdeg(float f) {
      return Mth.wrapDegrees(f);
   }

   public static float wraprad(float f) {
      return torad(Mth.wrapDegrees(todeg(f)));
   }

   public static float degdiff(float f, float f2) {
      return Mth.degreesDifferenceAbs(f, f2);
   }

   public static float raddiff(float f, float f2) {
      return torad(Mth.degreesDifferenceAbs(todeg(f), todeg(f2)));
   }

   public static float catmullrom(float f, float f2, float f3, float f4, float f5) {
      return Mth.catmullrom(f, f2, f3, f4, f5);
   }

   public static boolean between(float a, float b, float c) {
      return !(a > c) && !(a < b);
   }

   public static boolean equals(float x, float y, float epsilon) {
      return Math.abs(y - x) <= epsilon;
   }

   public static boolean notPausedNotValidation() {
      return !Minecraft.getInstance().isPaused();
   }

   public static float quadraticBezier(float t, float p0, float p1, float p2) {
      float oneMinusT = 1.0F - t;
      return oneMinusT * oneMinusT * p0 + 2.0F * oneMinusT * t * p1 + t * t * p2;
   }

   public static float cubicBezier(float t, float p0, float p1, float p2, float p3) {
      float oneMinusT = 1.0F - t;
      float oneMinusTSquared = oneMinusT * oneMinusT;
      float tSquared = t * t;
      return oneMinusTSquared * oneMinusT * p0 + 3.0F * oneMinusTSquared * t * p1 + 3.0F * oneMinusT * tSquared * p2 + tSquared * t * p3;
   }

   public static float hermiteInterpolation(float t, float p0, float p1, float m0, float m1) {
      float tSquared = t * t;
      float tCubed = tSquared * t;
      float h00 = 2.0F * tCubed - 3.0F * tSquared + 1.0F;
      float h10 = tCubed - 2.0F * tSquared + t;
      float h01 = -2.0F * tCubed + 3.0F * tSquared;
      float h11 = tCubed - tSquared;
      return h00 * p0 + h10 * m0 + h01 * p1 + h11 * m1;
   }

   public static float easeInQuad(float t, float start, float end) {
      float delta = end - start;
      return start + delta * t * t;
   }

   public static float easeOutQuad(float t, float start, float end) {
      float delta = end - start;
      return start + delta * -t * (t - 2.0F);
   }

   public static float easeInOutQuad(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      return t < 0.5 ? start + delta * (2.0F * t * t) : start + delta * (-2.0F * t * (t - 2.0F) - 1.0F);
   }

   public static float easeInCubic(float t, float start, float end) {
      float delta = end - start;
      return start + delta * t * t * t;
   }

   public static float easeOutCubic(float t, float start, float end) {
      float delta = end - start;
      return start + delta * --t * t * t + 1.0F;
   }

   public static float easeInOutCubic(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      return t < 0.5 ? start + delta * 4.0F * t * t * t : start + delta * --t * (2.0F * t * t + 2.0F) + 1.0F;
   }

   public static float easeInQuart(float t, float start, float end) {
      float delta = end - start;
      return start + delta * t * t * t * t;
   }

   public static float easeOutQuart(float t, float start, float end) {
      float delta = end - start;
      return start + delta * --t * t * t * t + 1.0F;
   }

   public static float easeInOutQuart(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      return t < 0.5 ? start + delta * 8.0F * t * t * t * t : start + delta * --t * (8.0F * t * t * t + 1.0F) + 1.0F;
   }

   public static float easeInQuint(float t, float start, float end) {
      float delta = end - start;
      return start + delta * t * t * t * t * t;
   }

   public static float easeOutQuint(float t, float start, float end) {
      float delta = end - start;
      return start + delta * --t * t * t * t * t + 1.0F;
   }

   public static float easeInOutQuint(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      return t < 0.5 ? start + delta * 16.0F * t * t * t * t * t : start + delta * --t * (16.0F * t * t * t * t + 1.0F) + 1.0F;
   }

   public static float easeInSine(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (1.0F - (float)Math.cos(t * 3.141592653589793 / 2.0));
   }

   public static float easeOutSine(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (float)Math.sin(t * 3.141592653589793 / 2.0);
   }

   public static float easeInOutSine(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (float)(-0.5 * (Math.cos(3.141592653589793 * t) - 1.0));
   }

   public static float easeInExpo(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (float)Math.pow(2.0, 10.0F * (t - 1.0F));
   }

   public static float easeOutExpo(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (float)(-Math.pow(2.0, -10.0F * t) + 1.0);
   }

   public static float easeInOutExpo(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      return t < 1.0F ? start + delta * (float)(0.5 * Math.pow(2.0, 10.0F * (t - 1.0F))) : start + delta * (float)(0.5 * (-Math.pow(2.0, -10.0F * --t) + 2.0));
   }

   public static float easeInCirc(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (float)(-(Math.sqrt(1.0F - t * t) - 1.0));
   }

   public static float easeOutCirc(float t, float start, float end) {
      float delta = end - start;
      float tMinus1 = t - 1.0F;
      return start + delta * (float)Math.sqrt(1.0F - tMinus1 * tMinus1);
   }

   public static float easeInOutCirc(float t, float start, float end) {
      float delta = end - start;
      float tTimes2 = t * 2.0F;
      if (tTimes2 < 1.0F) {
         return start + delta * (float)(-0.5 * (Math.sqrt(1.0F - tTimes2 * tTimes2) - 1.0));
      } else {
         float tTimes2Minus2 = tTimes2 - 2.0F;
         return start + delta * (float)(0.5 * (Math.sqrt(1.0F - tTimes2Minus2 * tTimes2Minus2) + 1.0));
      }
   }

   public static float easeInElastic(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (float)(-Math.pow(2.0, 10.0F * --t) * Math.sin((t - 0.075) * 6.283185307179586 / 0.3));
   }

   public static float easeOutElastic(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (float)(Math.pow(2.0, -10.0F * t) * Math.sin((t - 0.075) * 6.283185307179586 / 0.3) + 1.0);
   }

   public static float easeInOutElastic(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      return t < 0.5
         ? start + delta * (float)(-0.5 * Math.pow(2.0, 10.0F * --t) * Math.sin((t - 0.05625) * 6.283185307179586 / 0.45))
         : start + delta * (float)(0.5 * Math.pow(2.0, -10.0F * --t) * Math.sin((t - 0.05625) * 6.283185307179586 / 0.45) + 1.0);
   }

   public static float easeInBounce(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (1.0F - easeOutBounce(1.0F - t, 0.0F, 1.0F));
   }

   public static float easeOutBounce(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      if (t < 0.36363636363636365) {
         return start + delta * (7.5625F * t * t);
      } else if (t < 0.7272727272727273) {
         float var7;
         return (float)(start + delta * (7.5625F * (var7 = t - 0.54545456F) * var7 + 0.75));
      } else {
         float var5;
         float var6;
         return t < 0.9090909090909091
            ? (float)(start + delta * (7.5625F * (var5 = t - 0.8181818F) * var5 + 0.9375))
            : (float)(start + delta * (7.5625F * (var6 = t - 0.95454544F) * var6 + 0.984375));
      }
   }

   public static float easeInOutBounce(float t, float start, float end) {
      float delta = end - start;
      return t < 0.5 ? start + delta * (0.5F * easeInBounce(t * 2.0F, 0.0F, 1.0F)) : start + delta * (0.5F * easeOutBounce(t * 2.0F - 1.0F, 0.0F, 1.0F) + 0.5F);
   }

   public static float easeInBack(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (t * t * (2.70158F * t - 1.70158F));
   }

   public static float easeOutBack(float t, float start, float end) {
      float delta = end - start;
      return start + delta * (--t * t * (2.70158F * t + 1.70158F) + 1.0F);
   }

   public static float easeInOutBack(float t, float start, float end) {
      float delta = end - start;
      t /= 1.0F;
      return t < 0.5 ? start + delta * (t * t * (7.0F * t - 2.5F) * 2.0F) : start + delta * ((--t * t * (7.0F * t + 2.5F) + 2.0F) * 2.0F);
   }

   private static String[] functionName(String name, Class<?> clazz) throws EMFMathException {
      Optional<Method> method = Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(name)).findFirst();
      if (method.isPresent()) {
         return fromMethod(clazz, method.get());
      } else {
         throw new EMFMathException("Method [" + name + "] not found in ASMHelper");
      }
   }

   @NotNull
   private static String[] fromMethod(Class<?> clazz, Method method) {
      return new String[]{clazz.getName().replace('.', '/'), method.getName(), methodDescriptor(method)};
   }

   private static String methodDescriptor(Method m) {
      StringBuilder sb = new StringBuilder();
      sb.append('(');

      for (Class<?> p : m.getParameterTypes()) {
         sb.append(typeDescriptor(p));
      }

      sb.append(')');
      sb.append(typeDescriptor(m.getReturnType()));
      return sb.toString();
   }

   private static String typeDescriptor(Class<?> c) {
      if (c.isPrimitive()) {
         if (c == void.class) {
            return "V";
         } else if (c == int.class) {
            return "I";
         } else if (c == boolean.class) {
            return "Z";
         } else if (c == byte.class) {
            return "B";
         } else if (c == char.class) {
            return "C";
         } else if (c == short.class) {
            return "S";
         } else if (c == long.class) {
            return "J";
         } else if (c == float.class) {
            return "F";
         } else if (c == double.class) {
            return "D";
         } else {
            throw new AssertionError();
         }
      } else {
         return c.isArray() ? c.getName().replace('.', '/') : "L" + c.getName().replace('.', '/') + ";";
      }
   }

   public interface ThrowingRunnable {
      void run() throws EMFMathException;
   }
}
