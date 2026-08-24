package net.diebuddies.opengl;

public class Replacement {
   public static final int ALL_STAGES = 0;
   public static final int VERTEX_STAGE = 1;
   public static final int GEOMETRY_STAGE = 2;
   public static final int FRAGMENT_STAGE = 3;
   public String key;
   public String value;
   public int stage;

   public Replacement(String key, String replacement, int stage) {
      this.key = key;
      this.value = replacement;
      this.stage = stage;
   }

   public Replacement(String key, String replacement) {
      this.key = key;
      this.value = replacement;
      this.stage = 0;
   }
}
