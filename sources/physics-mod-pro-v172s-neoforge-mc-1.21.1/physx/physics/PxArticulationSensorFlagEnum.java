package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationSensorFlagEnum {
   eFORWARD_DYNAMICS_FORCES(geteFORWARD_DYNAMICS_FORCES()),
   eCONSTRAINT_SOLVER_FORCES(geteCONSTRAINT_SOLVER_FORCES()),
   eWORLD_FRAME(geteWORLD_FRAME());

   public final int value;

   private PxArticulationSensorFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteFORWARD_DYNAMICS_FORCES();

   private static int geteFORWARD_DYNAMICS_FORCES() {
      Loader.load();
      return _geteFORWARD_DYNAMICS_FORCES();
   }

   private static native int _geteCONSTRAINT_SOLVER_FORCES();

   private static int geteCONSTRAINT_SOLVER_FORCES() {
      Loader.load();
      return _geteCONSTRAINT_SOLVER_FORCES();
   }

   private static native int _geteWORLD_FRAME();

   private static int geteWORLD_FRAME() {
      Loader.load();
      return _geteWORLD_FRAME();
   }

   public static PxArticulationSensorFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationSensorFlagEnum: " + value);
   }
}
