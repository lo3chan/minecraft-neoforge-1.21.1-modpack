package vazkii.psi.common.core.handler;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {
   public static final ConfigHandler.Client CLIENT;
   public static final ModConfigSpec CLIENT_SPEC;
   public static final ConfigHandler.Common COMMON;
   public static final ModConfigSpec COMMON_SPEC;

   static {
      Pair<ConfigHandler.Client, ModConfigSpec> specPair = new Builder().configure(ConfigHandler.Client::new);
      CLIENT_SPEC = (ModConfigSpec)specPair.getRight();
      CLIENT = (ConfigHandler.Client)specPair.getLeft();
      specPair = new Builder().configure(ConfigHandler.Common::new);
      COMMON_SPEC = (ModConfigSpec)specPair.getRight();
      COMMON = (ConfigHandler.Common)specPair.getLeft();
   }

   public static class Client {
      public final BooleanValue useShaders;
      public final BooleanValue psiBarOnRight;
      public final BooleanValue contextSensitiveBar;
      public final BooleanValue pauseGameInProgrammer;
      public final IntValue maxPsiBarScale;
      public final BooleanValue changeGridCoordinatesToLetterNumber;

      public Client(Builder builder) {
         this.useShaders = builder.comment(
               "Controls whether Psi's shaders are used. If you're using the GLSL Shaders mod and are having graphical troubles with Psi stuff, you may want to turn this off."
            )
            .define("client.useShaders", true);
         this.psiBarOnRight = builder.comment("Controls whether the Psi Bar should be rendered on the right of the screen or not.")
            .define("client.psiBarOnRight", true);
         this.contextSensitiveBar = builder.comment(
               "Controls whether the Psi Bar should be hidden if it's full and the player is holding an item that uses Psi."
            )
            .define("client.contextSensitiveBar", true);
         this.maxPsiBarScale = builder.comment(
               "The maximum scale your Psi bar can be. This prevents it from being too large on a bigger GUI scale. This is maximum amount of \\\"on screen pixels\\\" each actual pixel can take."
            )
            .defineInRange("client.maxPsiBarScale", 3, 1, 5);
         this.pauseGameInProgrammer = builder.comment("Controls whether the Spell Programmer screen will pause the game in singleplayer.")
            .define("client.pauseGameInProgrammer", true);
         this.changeGridCoordinatesToLetterNumber = builder.comment(
               "Controls whether or not the Programmer will display the coordinates as a pair of two numbers or as a letter and a number"
            )
            .define("client.changeGridCoordinatesToLetterNumber", false);
      }
   }

   public static class Common {
      public final BooleanValue magiPsiClientSide;
      public final IntValue spellCacheSize;
      public final IntValue cadHarvestLevel;

      public Common(Builder builder) {
         this.magiPsiClientSide = builder.comment(
               "Set this to true to disable all server side features from Magical Psi, to allow you to use it purely as a client side mod"
            )
            .define("common.magiPsiClientSide", false);
         this.spellCacheSize = builder.comment(
               "How many compiled spells should be kept in a cache. Probably best not to mess with it if you don't know what you're doing."
            )
            .defineInRange("common.spellCacheSize", 200, 0, 2147483647);
         this.cadHarvestLevel = builder.comment("The harvest level of a CAD for the purposes of block breaking spells. Defaults to 3 (diamond level)")
            .defineInRange("common.cadHarvestLevel", 3, 0, 255);
      }
   }
}
