/*
 * Decompiled with CFR 0.152.
 */
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
        Constants.LOG.info("{} \u267b Init ...", (Object)"[Mods Optimizer]");
        Constants.LOG.info("Game Directory: {}", (Object)this.gameDir);
        Constants.LOG.info("Mods Directory: {}", (Object)this.modsDir);
        Constants.LOG.info("Game Environment: {}", (Object)this.environment);
        long startTime = System.nanoTime();
        if (ModsDatabaseConfig.isDebugEnabled()) {
            Constants.LOG.warn("\u26a0 Debug mode is enabled!");
            SemanticVersionUtils.enableDebug();
        }
        GameEnvironment gameEnvironment = this.environment;
        if (ModsDatabaseConfig.isDebugEnabled() && !Objects.equals(ModsDatabaseConfig.getDebugForceSide(), "default")) {
            if (Objects.equals(ModsDatabaseConfig.getDebugForceSide(), "server")) {
                Constants.LOG.info("\u26a0 Forced server side environment ...");
                gameEnvironment = GameEnvironment.SERVER;
            } else if (Objects.equals(ModsDatabaseConfig.getDebugForceSide(), "client")) {
                Constants.LOG.info("\u26a0 Forced client side environment ...");
                gameEnvironment = GameEnvironment.CLIENT;
            }
        }
        if (gameEnvironment == GameEnvironment.UNKNOWN) {
            Constants.LOG.warn("\u26a0 Unable to detect environment will check game dir for additional hints ...");
            File[] gameFiles = this.gameDir.listFiles();
            if (gameFiles == null) {
                Constants.LOG.warn("\u26a0 Unable to detect game files in game dir {}", (Object)this.gameDir);
            } else {
                for (File gameFile : gameFiles) {
                    if (!gameFile.getName().contains("server")) continue;
                    Constants.LOG.info("\u26a0 Detected server side environment file ...");
                    gameEnvironment = GameEnvironment.SERVER;
                    break;
                }
            }
        }
        this.gameEnvironment = gameEnvironment;
        Constants.LOG.info("\u267b init with game dir {} and mods dir {} for target {} in {} ms.", new Object[]{this.gameDir, this.modsDir, gameEnvironment, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)});
        return this;
    }

    public void enableClientSideMods() {
        if (this.gameEnvironment != GameEnvironment.CLIENT) {
            return;
        }
        long startTime = System.nanoTime();
        Constants.LOG.info("\u2714 Re-Enable possible client side mods ...");
        int numClientSideModsEnabled = ClientSideModsUtils.enable(this.modsDir);
        if (numClientSideModsEnabled > 0) {
            Constants.LOG.info("\u2714 Re-Enabled {} client side mods in {} ms.", (Object)numClientSideModsEnabled, (Object)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
        }
    }

    public void disableClientSideMods() {
        if (this.gameEnvironment == GameEnvironment.CLIENT) {
            Constants.LOG.info("\u2714 Client side mods are enabled.");
            return;
        }
        if (ModData.getClientMods().isEmpty()) {
            Constants.LOG.warn("\u2714 No mods for client-side checks found!");
            return;
        }
        if (this.gameEnvironment != GameEnvironment.SERVER) {
            Constants.LOG.warn("\u2714 Unknown environment {} for client-side checks!", (Object)this.gameEnvironment);
            return;
        }
        long startTime = System.nanoTime();
        Constants.LOG.info("\u274c Disable possible {} client side mods ...", (Object)ModData.getClientMods().size());
        int numClientSideModsDisabled = ClientSideModsUtils.disable(ModData.getClientMods());
        if (numClientSideModsDisabled > 0) {
            Constants.LOG.info("\u274c Disabled {} client side mods in {} ms.", (Object)numClientSideModsDisabled, (Object)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
        } else {
            Constants.LOG.info("\u274c Unable to disable any client side mods.");
        }
    }

    public void parseMods() {
        long startTime = System.nanoTime();
        Constants.LOG.info("\u267b Parsing Mods data ...");
        ModData.parseMods(this.modsDir, ".jar");
        if (ModData.getKnownMods().isEmpty()) {
            Constants.LOG.error("\u26a0 Unable to find any mods in {}", (Object)this.modsDir);
            return;
        }
        Constants.LOG.info("\u267b Parsed {} mods in {} ms.", (Object)ModData.getKnownMods().size(), (Object)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
    }

    public void optimizeDuplicatedMods() {
        if (!ModData.getDuplicatedMods().isEmpty()) {
            long startTime = System.nanoTime();
            DuplicatedModsUtils.optimize(ModData.getDuplicatedMods());
            Constants.LOG.info("\u267b Optimized {} duplicated mods in {} ms.", (Object)ModData.getDuplicatedMods().size(), (Object)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
        } else {
            Constants.LOG.info("\u2714 No duplicated mods found.");
        }
    }

    public long getTotalStartTime() {
        return this.totalStartTime;
    }

    public void cleanup() {
        Constants.LOG.info("\u267b Cleanup resources ...");
        ModData.clear();
    }
}

