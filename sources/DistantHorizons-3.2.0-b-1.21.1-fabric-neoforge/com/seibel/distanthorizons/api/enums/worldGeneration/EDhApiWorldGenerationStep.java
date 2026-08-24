package com.seibel.distanthorizons.api.enums.worldGeneration;

public enum EDhApiWorldGenerationStep {
   DOWN_SAMPLED(-1, "down_sampled"),
   EMPTY(0, "empty"),
   STRUCTURE_START(1, "structure_start"),
   STRUCTURE_REFERENCE(2, "structure_reference"),
   BIOMES(3, "biomes"),
   NOISE(4, "noise"),
   SURFACE(5, "surface"),
   CARVERS(6, "carvers"),
   LIQUID_CARVERS(7, "liquid_carvers"),
   FEATURES(8, "features"),
   LIGHT(9, "light");

   public final String name;
   public final byte value;

   private EDhApiWorldGenerationStep(int value, String name) {
      this.value = (byte)value;
      this.name = name;
   }

   public static EDhApiWorldGenerationStep fromValue(int value) {
      for (EDhApiWorldGenerationStep genStep : values()) {
         if (genStep.value == value) {
            return genStep;
         }
      }

      return null;
   }

   public static EDhApiWorldGenerationStep fromName(String name) {
      for (EDhApiWorldGenerationStep genStep : values()) {
         if (genStep.name.equals(name)) {
            return genStep;
         }
      }

      return null;
   }
}
