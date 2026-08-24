package fabric.me.thosea.badoptimizations.config;

import java.nio.file.Files;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_437;

public final class BOConfigScreen extends class_437 {
   private final class_437 parent;

   public BOConfigScreen(class_437 parent) {
      super(class_2561.method_43473());
      this.parent = parent;
   }

   protected void method_25426() {
      if (!Files.exists(Config.FILE)) {
         try {
            Config.writeConfig();
         } catch (Exception var2) {
            throw new RuntimeException("Failed to generate BadOptimizations config", var2);
         }
      }

      class_156.method_668().method_673(Config.FILE.toUri());
      this.field_22787.method_1507(this.parent);
   }
}
