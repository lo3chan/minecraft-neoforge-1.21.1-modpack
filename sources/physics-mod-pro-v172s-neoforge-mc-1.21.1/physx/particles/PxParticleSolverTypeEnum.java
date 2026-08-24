package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticleSolverTypeEnum {
   ePBD(getePBD()),
   eFLIP(geteFLIP()),
   eMPM(geteMPM());

   public final int value;

   private PxParticleSolverTypeEnum(int value) {
      this.value = value;
   }

   private static native int _getePBD();

   private static int getePBD() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSolverTypeEnum");
      return _getePBD();
   }

   private static native int _geteFLIP();

   private static int geteFLIP() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSolverTypeEnum");
      return _geteFLIP();
   }

   private static native int _geteMPM();

   private static int geteMPM() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSolverTypeEnum");
      return _geteMPM();
   }

   public static PxParticleSolverTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxParticleSolverTypeEnum: " + value);
   }
}
