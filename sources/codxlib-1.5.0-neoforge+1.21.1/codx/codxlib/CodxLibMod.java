package codx.codxlib;

import codx.codxlib.api.CodxLib;
import codx.codxlib.api.CodxLibConfig;
import codx.codxlib.api.JsonConfig;
import codx.codxlib.api.ModInfo;
import codx.codxlib.api.UpdateChecker;

public final class CodxLibMod {
   public static final String MOD_ID = "codxlib";
   public static final String MODRINTH_SLUG = "codxlib";
   public static final String MOD_SAYS = "codxlib: ";
   public static final JsonConfig<CodxLibConfig> CONFIG = JsonConfig.of("codxlib.json", CodxLibConfig.class, CodxLibConfig::new);

   private CodxLibMod() {
   }

   public static ModInfo selfInfo() {
      return new ModInfo("codxlib", "codxlib", CodxLib.version("codxlib"), "[CodxLib]");
   }

   public static void commonInit() {
      UpdateChecker.setEnabled(CONFIG.get().updateNotifications);
      UpdateChecker.register(selfInfo());
      System.out.println("codxlib: common init complete (v" + CodxLib.version("codxlib") + ").");
   }
}
