package traben.entity_model_features.mod_compat;

import java.util.Objects;
import net.irisshaders.iris.api.v0.IrisApi;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;

public abstract class IrisShadowPassDetection {
   private static IrisShadowPassDetection instance;

   public abstract boolean inShadowPass();

   public static IrisShadowPassDetection getInstance() {
      if (instance == null) {
         try {
            instance = new IrisShadowPassDetection.IrisShadowPassDetectionImpl();
         } catch (Throwable var1) {
            EMFUtils.log("EMF did not find the Iris API, disabling shadow pass detection");
            instance = new IrisShadowPassDetection() {
               @Override
               public boolean inShadowPass() {
                  return false;
               }
            };
         }
      }

      return instance;
   }

   private static class IrisShadowPassDetectionImpl extends IrisShadowPassDetection {
      IrisShadowPassDetectionImpl() {
         if (!ETF.IRIS_DETECTED) {
            throw new RuntimeException("Iris not detected, cannot use this class");
         } else {
            Objects.requireNonNull(IrisApi.getInstance());
         }
      }

      @Override
      public boolean inShadowPass() {
         return IrisApi.getInstance().isRenderingShadowPass();
      }
   }
}
