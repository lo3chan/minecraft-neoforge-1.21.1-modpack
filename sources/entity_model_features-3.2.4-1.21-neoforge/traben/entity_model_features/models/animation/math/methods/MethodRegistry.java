package traben.entity_model_features.models.animation.math.methods;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVisitable;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.methods.emf.CatchMethod;
import traben.entity_model_features.models.animation.math.methods.emf.IfBMethod;
import traben.entity_model_features.models.animation.math.methods.emf.KeyframeMethod;
import traben.entity_model_features.models.animation.math.methods.emf.KeyframeloopMethod;
import traben.entity_model_features.models.animation.math.methods.emf.NBTMethod;
import traben.entity_model_features.models.animation.math.methods.emf.RandomBMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.IfMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.InMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.MaxMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.MinMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.PrintBMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.PrintMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.RandomMethod;

public final class MethodRegistry {
   private static final MethodRegistry INSTANCE = new MethodRegistry();
   private final Map<String, MethodRegistry.MethodFactory> methodFactories = new HashMap<>();
   private final Map<String, String> methodExplanationTranslationKeys = new HashMap<>();

   private MethodRegistry() {
      try {
         this.registerAndWrapMethodFactory("max", MaxMethod::new);
         this.registerAndWrapMethodFactory("min", MinMethod::new);
         this.registerAndWrapMethodFactory("random", RandomMethod::new);
         this.registerHelperMethodFactory("sin");
         this.registerHelperMethodFactory("asin");
         this.registerHelperMethodFactory("cos");
         this.registerHelperMethodFactory("acos");
         this.registerHelperMethodFactory("tan");
         this.registerHelperMethodFactory("atan");
         this.registerHelperMethodFactory("abs");
         this.registerHelperMethodFactory("floor");
         this.registerHelperMethodFactory("ceil");
         this.registerHelperMethodFactory("round");
         this.registerHelperMethodFactory("log");
         this.registerHelperMethodFactory("exp");
         this.registerHelperMethodFactory("torad");
         this.registerHelperMethodFactory("todeg");
         this.registerHelperMethodFactory("frac");
         this.registerHelperMethodFactory("signum");
         this.registerHelperMethodFactory("sqrt");
         this.registerHelperMethodFactory("fmod");
         this.registerHelperMethodFactory("pow");
         this.registerHelperMethodFactory("atan2");
         this.registerHelperMethodFactory("clamp");
         this.registerHelperMethodFactory("lerp");
         this.registerAndWrapMethodFactory("print", PrintMethod::new);
         this.registerAndWrapMethodFactory("printb", PrintBMethod::new);
         this.registerAndWrapMethodFactory("catch", CatchMethod::new);
         this.registerAndWrapMethodFactory("if", IfMethod::new);
         this.registerAndWrapMethodFactory("ifb", IfBMethod::new);
         this.registerAndWrapMethodFactory("randomb", RandomBMethod::new);
         this.registerAndWrapMethodFactory("in", InMethod::new);
         this.registerHelperMethodFactory("between");
         this.registerHelperMethodFactory("equals");
         this.registerAndWrapMethodFactory("nbt", NBTMethod::new);
         this.registerAndWrapMethodFactory("keyframe", KeyframeMethod::new);
         this.registerAndWrapMethodFactory("keyframeloop", KeyframeloopMethod::new);
         this.registerHelperMethodFactory("wrapdeg");
         this.registerHelperMethodFactory("wraprad");
         this.registerHelperMethodFactory("degdiff");
         this.registerHelperMethodFactory("raddiff");
         this.registerHelperMethodFactory("easeinout", "easeInOutSine");
         this.registerHelperMethodFactory("easein", "easeInSine");
         this.registerHelperMethodFactory("easeout", "easeOutSine");
         this.registerHelperMethodFactory("cubiceaseinout", "easeInOutCubic");
         this.registerHelperMethodFactory("cubiceasein", "easeInCubic");
         this.registerHelperMethodFactory("cubiceaseout", "easeOutCubic");
         this.registerHelperMethodFactory("catmullrom");
         this.registerHelperMethodFactory("quadbezier", "quadraticBezier");
         this.registerHelperMethodFactory("cubicbezier", "cubicBezier");
         this.registerHelperMethodFactory("hermite", "hermiteInterpolation");
         this.registerHelperMethodFactory("easeinoutexpo");
         this.registerHelperMethodFactory("easeinexpo");
         this.registerHelperMethodFactory("easeoutexpo");
         this.registerHelperMethodFactory("easeinoutcirc");
         this.registerHelperMethodFactory("easeincirc");
         this.registerHelperMethodFactory("easeoutcirc");
         this.registerHelperMethodFactory("easeinoutelastic");
         this.registerHelperMethodFactory("easeinelastic");
         this.registerHelperMethodFactory("easeoutelastic");
         this.registerHelperMethodFactory("easeinoutback");
         this.registerHelperMethodFactory("easeinback");
         this.registerHelperMethodFactory("easeoutback");
         this.registerHelperMethodFactory("easeinoutbounce");
         this.registerHelperMethodFactory("easeinbounce");
         this.registerHelperMethodFactory("easeoutbounce");
         this.registerHelperMethodFactory("easeinquad");
         this.registerHelperMethodFactory("easeoutquad");
         this.registerHelperMethodFactory("easeinoutquad");
         this.registerHelperMethodFactory("easeincubic");
         this.registerHelperMethodFactory("easeoutcubic");
         this.registerHelperMethodFactory("easeinoutcubic");
         this.registerHelperMethodFactory("easeinquart");
         this.registerHelperMethodFactory("easeoutquart");
         this.registerHelperMethodFactory("easeinoutquart");
         this.registerHelperMethodFactory("easeinquint");
         this.registerHelperMethodFactory("easeoutquint");
         this.registerHelperMethodFactory("easeinoutquint");
         this.registerHelperMethodFactory("easeinsine");
         this.registerHelperMethodFactory("easeoutsine");
         this.registerHelperMethodFactory("easeinoutsine");
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   private static String emfTranslationKey(String key) {
      return "entity_model_features.config.function_explanation." + key;
   }

   public static MethodRegistry getInstance() {
      return INSTANCE;
   }

   public Map<String, String> getMethodExplanationTranslationKeys() {
      return this.methodExplanationTranslationKeys;
   }

   private void registerHelperMethodFactory(String methodName) throws EMFMathException {
      this.registerSimpleMethodFactory(
         methodName, emfTranslationKey(methodName), ASMHelper.getHelperMethod(methodName), ASMHelper.getHelperMethodCompiler(methodName)
      );
   }

   private void registerHelperMethodFactory(String methodName, String staticName) throws EMFMathException {
      this.registerSimpleMethodFactory(
         methodName, emfTranslationKey(methodName), ASMHelper.getHelperMethod(staticName), ASMHelper.getHelperMethodCompiler(methodName)
      );
   }

   public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, Method staticMethod, @Nullable ASMVisitable asmCompiler) {
      this.register(methodName, explanationTranslationKey, SimpleMethod.makeFactory(methodName, staticMethod, asmCompiler));
   }

   @Deprecated(
      forRemoval = true
   )
   public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, Function<Float, Float> function) throws EMFMathException {
      throw new UnsupportedOperationException("deprecated");
   }

   @Deprecated(
      forRemoval = true
   )
   public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, BiFunction<Float, Float, Float> function) throws EMFMathException {
      throw new UnsupportedOperationException("deprecated");
   }

   @Deprecated(
      forRemoval = true
   )
   public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, TriFunction<Float, Float, Float, Float> function) throws EMFMathException {
      throw new UnsupportedOperationException("deprecated");
   }

   @Deprecated(
      forRemoval = true
   )
   public void registerSimpleMultiMethodFactory(String methodName, String explanationTranslationKey, Function<List<Float>, Float> function) throws EMFMathException {
      throw new UnsupportedOperationException("deprecated");
   }

   private void register(String methodName, String explanationTranslationKey, MethodRegistry.MethodFactory factory) {
      this.methodExplanationTranslationKeys.put(methodName, explanationTranslationKey);
      this.methodFactories.put(methodName, factory);
   }

   private void registerAndWrapMethodFactory(String methodName, MethodRegistry.MethodFactory factory) {
      this.registerAndWrapMethodFactory(methodName, emfTranslationKey(methodName), factory);
   }

   public void registerAndWrapMethodFactory(String methodName, String explanationTranslationKey, MethodRegistry.MethodFactory factory) {
      this.register(methodName, explanationTranslationKey, (a, b, c) -> {
         try {
            return factory.getMethod(a, b, c);
         } catch (Exception var6) {
            throw new EMFMathException("Failed to create " + methodName + "() method, because:" + var6);
         }
      });
   }

   public boolean containsMethod(String methodName) {
      return this.methodFactories.containsKey(methodName);
   }

   public MethodRegistry.MethodFactory getMethodFactory(String methodName) {
      return this.methodFactories.get(methodName);
   }

   public interface MethodFactory {
      MathMethod getMethod(List<String> var1, boolean var2, AnimSetupContext var3) throws EMFMathException;
   }
}
