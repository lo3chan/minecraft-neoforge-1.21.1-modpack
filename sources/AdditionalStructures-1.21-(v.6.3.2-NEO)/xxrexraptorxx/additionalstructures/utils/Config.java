package xxrexraptorxx.additionalstructures.utils;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class Config {
   public static final String CATEGORY_GENERAL = "general";
   public static ModConfigSpec SERVER_CONFIG;
   public static ModConfigSpec CLIENT_CONFIG;
   public static BooleanValue UPDATE_CHECKER;
   public static BooleanValue PATREON_REWARDS;

   public static void init(ModContainer container) {
      initServer();
      initClient();
      container.registerConfig(Type.SERVER, SERVER_CONFIG);
      container.registerConfig(Type.CLIENT, CLIENT_CONFIG);
   }

   public static void initClient() {
      Builder builder = new Builder();
      builder.comment("General").push("general");
      UPDATE_CHECKER = builder.comment("Activate the Update-Checker").define("update-checker", true);
      builder.pop();
      CLIENT_CONFIG = builder.build();
   }

   public static void initServer() {
      Builder builder = new Builder();
      builder.comment("General").push("general");
      PATREON_REWARDS = builder.comment("Enables ingame rewards on first spawn for Patreons").define("patreon_rewards", true);
      builder.pop();
      SERVER_CONFIG = builder.build();
   }
}
