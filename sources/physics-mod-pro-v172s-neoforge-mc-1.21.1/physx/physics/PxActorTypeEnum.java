package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxActorTypeEnum {
   eRIGID_STATIC(geteRIGID_STATIC()),
   eRIGID_DYNAMIC(geteRIGID_DYNAMIC()),
   eARTICULATION_LINK(geteARTICULATION_LINK()),
   eSOFTBODY(geteSOFTBODY()),
   eFEMCLOTH(geteFEMCLOTH()),
   ePBD_PARTICLESYSTEM(getePBD_PARTICLESYSTEM()),
   eFLIP_PARTICLESYSTEM(geteFLIP_PARTICLESYSTEM()),
   eMPM_PARTICLESYSTEM(geteMPM_PARTICLESYSTEM()),
   eHAIRSYSTEM(geteHAIRSYSTEM());

   public final int value;

   private PxActorTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteRIGID_STATIC();

   private static int geteRIGID_STATIC() {
      Loader.load();
      return _geteRIGID_STATIC();
   }

   private static native int _geteRIGID_DYNAMIC();

   private static int geteRIGID_DYNAMIC() {
      Loader.load();
      return _geteRIGID_DYNAMIC();
   }

   private static native int _geteARTICULATION_LINK();

   private static int geteARTICULATION_LINK() {
      Loader.load();
      return _geteARTICULATION_LINK();
   }

   private static native int _geteSOFTBODY();

   private static int geteSOFTBODY() {
      Loader.load();
      return _geteSOFTBODY();
   }

   private static native int _geteFEMCLOTH();

   private static int geteFEMCLOTH() {
      Loader.load();
      return _geteFEMCLOTH();
   }

   private static native int _getePBD_PARTICLESYSTEM();

   private static int getePBD_PARTICLESYSTEM() {
      Loader.load();
      return _getePBD_PARTICLESYSTEM();
   }

   private static native int _geteFLIP_PARTICLESYSTEM();

   private static int geteFLIP_PARTICLESYSTEM() {
      Loader.load();
      return _geteFLIP_PARTICLESYSTEM();
   }

   private static native int _geteMPM_PARTICLESYSTEM();

   private static int geteMPM_PARTICLESYSTEM() {
      Loader.load();
      return _geteMPM_PARTICLESYSTEM();
   }

   private static native int _geteHAIRSYSTEM();

   private static int geteHAIRSYSTEM() {
      Loader.load();
      return _geteHAIRSYSTEM();
   }

   public static PxActorTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxActorTypeEnum: " + value);
   }
}
