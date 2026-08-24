package traben.entity_model_features.models.animation.state;

public class EMFSubmitData {
   public EMFEntityRenderState backupState = null;
   public int modelVariant = -1;
   public EMFBipedPose bipedPose = null;
   public boolean onShoulder = false;
   public boolean isMainModelPhase = false;
   public boolean isLayerModelPhase = false;
   public static EMFEntityRenderState AWAITING_backupState = null;
   public static EMFBipedPose AWAITING_bipedPose = null;
   public static boolean AWAITING_isMainModelPhase = false;
   public static boolean AWAITING_isLayerModelPhase = false;
}
