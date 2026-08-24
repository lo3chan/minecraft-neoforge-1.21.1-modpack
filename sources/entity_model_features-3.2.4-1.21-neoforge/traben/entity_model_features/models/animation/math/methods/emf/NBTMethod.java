package traben.entity_model_features.models.animation.math.methods.emf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NBTProperty;

public class NBTMethod extends MathMethod {
   public static Map<String, NBTProperty> CACHE = new HashMap<>();
   private static final NBTProperty NULL = nullz();

   public NBTMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      String nbtKey = args.get(0);
      String nbtQuery = args.get(1);
      Properties properties = new Properties();
      properties.setProperty("nbt.1." + nbtKey, nbtQuery);
      NBTProperty propertyTester = NBTProperty.getPropertyOrNull(properties, 1);
      if (propertyTester == null) {
         EMFUtils.logError("nbt() animation function did not parse: nbt(" + nbtKey + " " + nbtQuery + "), please check your syntax.");
         throw new EMFMathException("nbt() animation function did not parse: nbt(" + nbtKey + " " + nbtQuery + "), please check your syntax.");
      } else {
         this.setSupplierAndOptimize(
            () -> EMFAnimationEntityContext.getEMFEntity() == null
               ? -1.0F / 0.0F
               : MathValue.fromBoolean(propertyTester.testEntity(EMFAnimationEntityContext.getEmfState(), false))
         );
      }
   }

   private static NBTProperty nullz() {
      Properties properties = new Properties();
      properties.setProperty("nbt.1.null", "null");
      return NBTProperty.getPropertyOrNull(properties, 1);
   }

   public static boolean nbtMethodStatic(String nbtKey, String nbtQuery) throws EMFMathException {
      NBTProperty propertyTester = CACHE.computeIfAbsent(nbtKey + "_" + nbtQuery, k -> {
         Properties properties = new Properties();
         properties.setProperty("nbt.1." + nbtKey, nbtQuery);
         NBTProperty v = NBTProperty.getPropertyOrNull(properties, 1);
         if (v == null) {
            EMFUtils.logError("nbt() animation function did not parse: nbt(" + nbtKey + " " + nbtQuery + "), please check your syntax.");
            return NULL;
         } else {
            return v;
         }
      });
      return propertyTester != NULL && EMFAnimationEntityContext.getEMFEntity() != null
         ? propertyTester.testEntity(EMFAnimationEntityContext.getEmfState(), false)
         : false;
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      mv.visitLdcInsn(this.rawArgs.get(0));
      mv.visitLdcInsn(this.rawArgs.get(1));
      ASMHelper.visitStaticFunctionASM(mv, Arrays.stream(NBTMethod.class.getMethods()).filter(it -> it.getName().equals("nbtMethodStatic")).findFirst().get());
   }

   @Override
   protected boolean canOptimizeForConstantArgs() {
      return false;
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount == 2;
   }

   @Override
   protected boolean isRawStringArg(int index) {
      return true;
   }
}
