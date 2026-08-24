package top.theillusivec4.curios.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class CuriosClientConfig {
   public static final ModConfigSpec CLIENT_SPEC;
   public static final CuriosClientConfig.Client CLIENT;
   private static final String CONFIG_PREFIX = "gui.curios.config.";

   static {
      Pair<CuriosClientConfig.Client, ModConfigSpec> specPair = new Builder().configure(CuriosClientConfig.Client::new);
      CLIENT_SPEC = (ModConfigSpec)specPair.getRight();
      CLIENT = (CuriosClientConfig.Client)specPair.getLeft();
   }

   public static class Client {
      public final BooleanValue renderCurios;
      public final BooleanValue enableButton;
      public final IntValue buttonXOffset;
      public final IntValue buttonYOffset;
      public final IntValue creativeButtonXOffset;
      public final IntValue creativeButtonYOffset;
      public final EnumValue<CuriosClientConfig.Client.ButtonCorner> buttonCorner;

      Client(Builder builder) {
         builder.comment("Client only settings, mostly things related to rendering").push("client");
         this.renderCurios = builder.comment("Set to true to enable rendering curios")
            .translation("gui.curios.config.renderCurios")
            .define("renderCurios", true);
         this.enableButton = builder.comment("Set to true to enable the Curios GUI button")
            .translation("gui.curios.config.enableButton")
            .define("enableButton", true);
         this.buttonXOffset = builder.comment("The X-Offset for the Curios GUI button")
            .translation("gui.curios.config.buttonXOffset")
            .defineInRange("buttonXOffset", 0, -100, 100);
         this.buttonYOffset = builder.comment("The Y-Offset for the Curios GUI button")
            .translation("gui.curios.config.buttonYOffset")
            .defineInRange("buttonYOffset", 0, -100, 100);
         this.creativeButtonXOffset = builder.comment("The X-Offset for the Creative Curios GUI button")
            .translation("gui.curios.config.creativeButtonXOffset")
            .defineInRange("creativeButtonXOffset", 0, -100, 100);
         this.creativeButtonYOffset = builder.comment("The Y-Offset for the Creative Curios GUI button")
            .translation("gui.curios.config.creativeButtonYOffset")
            .defineInRange("creativeButtonYOffset", 0, -100, 100);
         this.buttonCorner = builder.comment("The corner for the Curios GUI button")
            .translation("gui.curios.config.buttonCorner")
            .defineEnum("buttonCorner", CuriosClientConfig.Client.ButtonCorner.TOP_LEFT);
         builder.pop();
      }

      public static enum ButtonCorner {
         TOP_LEFT(26, -75, 73, -62),
         TOP_RIGHT(61, -75, 95, -62),
         BOTTOM_LEFT(26, -20, 73, -29),
         BOTTOM_RIGHT(61, -20, 95, -29);

         final int xoffset;
         final int yoffset;
         final int creativeXoffset;
         final int creativeYoffset;

         private ButtonCorner(int x, int y, int creativeX, int creativeY) {
            this.xoffset = x;
            this.yoffset = y;
            this.creativeXoffset = creativeX;
            this.creativeYoffset = creativeY;
         }

         public int getXoffset() {
            return this.xoffset;
         }

         public int getYoffset() {
            return this.yoffset;
         }

         public int getCreativeXoffset() {
            return this.creativeXoffset;
         }

         public int getCreativeYoffset() {
            return this.creativeYoffset;
         }
      }
   }
}
