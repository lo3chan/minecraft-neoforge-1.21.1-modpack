package de.markusbordihn.modsoptimizer.service;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.config.ModsDatabaseConfig;
import de.markusbordihn.modsoptimizer.data.GameEnvironment;
import de.markusbordihn.modsoptimizer.data.ModData;
import de.markusbordihn.modsoptimizer.utils.ClientSideModsUtils;
import de.markusbordihn.modsoptimizer.utils.DuplicatedModsUtils;
import de.markusbordihn.modsoptimizer.utils.SemanticVersionUtils;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ModsOptimizerService {
   private final File gameDir;
   private final File modsDir;
   private final GameEnvironment environment;
   private final long totalStartTime;
   private GameEnvironment gameEnvironment;

   public ModsOptimizerService(File gameDir, File modsDir) {
      this(gameDir, modsDir, GameEnvironment.UNKNOWN);
   }

   public ModsOptimizerService(File gameDir, File modsDir, GameEnvironment environment) {
      this.gameDir = gameDir;
      this.modsDir = modsDir;
      this.environment = environment;
      this.gameEnvironment = environment;
      this.totalStartTime = System.nanoTime();
   }

   public ModsOptimizerService init() {
      Constants.LOG.info("{} ♻ Init ...", "[Mods Optimizer]");
      Constants.LOG.info("Game Directory: {}", this.gameDir);
      Constants.LOG.info("Mods Directory: {}", this.modsDir);
      Constants.LOG.info("Game Environment: {}", this.environment);
      long startTime = System.nanoTime();
      if (ModsDatabaseConfig.isDebugEnabled()) {
         Constants.LOG.warn("⚠ Debug mode is enabled!");
         SemanticVersionUtils.enableDebug();
      }

      GameEnvironment gameEnvironment = this.environment;
      if (ModsDatabaseConfig.isDebugEnabled() && !Objects.equals(ModsDatabaseConfig.getDebugForceSide(), "default")) {
         if (Objects.equals(ModsDatabaseConfig.getDebugForceSide(), "server")) {
            Constants.LOG.info("⚠ Forced server side environment ...");
            gameEnvironment = GameEnvironment.SERVER;
         } else if (Objects.equals(ModsDatabaseConfig.getDebugForceSide(), "client")) {
            Constants.LOG.info("⚠ Forced client side environment ...");
            gameEnvironment = GameEnvironment.CLIENT;
         }
      }

      if (gameEnvironment == GameEnvironment.UNKNOWN) {
         Constants.LOG.warn("⚠ Unable to detect environment will check game dir for additional hints ...");
         File[] gameFiles = this.gameDir.listFiles();
         if (gameFiles == null) {
            Constants.LOG.warn("⚠ Unable to detect game files in game dir {}", this.gameDir);
         } else {
            for (File gameFile : gameFiles) {
               if (gameFile.getName().contains("server")) {
                  Constants.LOG.info("⚠ Detected server side environment file ...");
                  gameEnvironment = GameEnvironment.SERVER;
                  break;
               }
            }
         }
      }

      this.gameEnvironment = gameEnvironment;
      Constants.LOG
         .info(
            "♻ init with game dir {} and mods dir {} for target {} in {} ms.",
            new Object[]{this.gameDir, this.modsDir, gameEnvironment, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)}
         );
      return this;
   }

   public void enableClientSideMods() {
      if (this.gameEnvironment == GameEnvironment.CLIENT) {
         long startTime = System.nanoTime();
         Constants.LOG.info("✔ Re-Enable possible client side mods ...");
         int numClientSideModsEnabled = ClientSideModsUtils.enable(this.modsDir);
         if (numClientSideModsEnabled > 0) {
            Constants.LOG
               .info("✔ Re-Enabled {} client side mods in {} ms.", numClientSideModsEnabled, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
         }
      }
   }

   public void disableClientSideMods() {
      if (this.gameEnvironment == GameEnvironment.CLIENT) {
         Constants.LOG.info("✔ Client side mods are enabled.");
      } else if (ModData.getClientMods().isEmpty()) {
         Constants.LOG.warn("✔ No mods for client-side checks found!");
      } else if (this.gameEnvironment != GameEnvironment.SERVER) {
         Constants.LOG.warn("✔ Unknown environment {} for client-side checks!", this.gameEnvironment);
      } else {
         long startTime = System.nanoTime();
         Constants.LOG.info("❌ Disable possible {} client side mods ...", ModData.getClientMods().size());
         int numClientSideModsDisabled = ClientSideModsUtils.disable(ModData.getClientMods());
         if (numClientSideModsDisabled > 0) {
            Constants.LOG
               .info("❌ Disabled {} client side mods in {} ms.", numClientSideModsDisabled, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
         } else {
            Constants.LOG.info("❌ Unable to disable any client side mods.");
         }
      }
   }

   public void parseMods() {
      long startTime = System.nanoTime();
      Constants.LOG.info("♻ Parsing Mods data ...");
      ModData.parseMods(this.modsDir, ".jar");
      if (ModData.getKnownMods().isEmpty()) {
         Constants.LOG.error("⚠ Unable to find any mods in {}", this.modsDir);
      } else {
         Constants.LOG.info("♻ Parsed {} mods in {} ms.", ModData.getKnownMods().size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
      }
   }

   public void optimizeDuplicatedMods() {
      if (!ModData.getDuplicatedMods().isEmpty()) {
         long startTime = System.nanoTime();
         DuplicatedModsUtils.optimize(ModData.getDuplicatedMods());
         Constants.LOG
            .info("♻ Optimized {} duplicated mods in {} ms.", ModData.getDuplicatedMods().size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
      } else {
         Constants.LOG.info("✔ No duplicated mods found.");
      }
   }

   public long getTotalStartTime() {
      return this.totalStartTime;
   }

   public void cleanup() {
      Constants.LOG.info("♻ Cleanup resources ...");
      ModData.clear();
   }
}
