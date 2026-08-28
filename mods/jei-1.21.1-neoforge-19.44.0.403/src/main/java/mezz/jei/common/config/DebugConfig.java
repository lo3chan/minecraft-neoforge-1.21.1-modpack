/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.config;

import java.util.function.Supplier;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchemaBuilder;
import org.jetbrains.annotations.Nullable;

public final class DebugConfig {
    @Nullable
    private static DebugConfig instance;
    private final Supplier<Boolean> debugIngredientsEnabled;
    private final Supplier<Boolean> debugGuisEnabled;
    private final Supplier<Boolean> debugInputsEnabled;
    private final Supplier<Boolean> debugInfoTooltipsEnabled;
    private final Supplier<Boolean> logSuffixTreeStats;

    public static void create(IConfigSchemaBuilder schema) {
        instance = new DebugConfig(schema);
    }

    private DebugConfig(IConfigSchemaBuilder schema) {
        IConfigCategoryBuilder advanced = schema.addCategory("debug");
        this.debugIngredientsEnabled = advanced.addBoolean("debugIngredientsEnabled", false);
        this.debugGuisEnabled = advanced.addBoolean("debugGuis", false);
        this.debugInputsEnabled = advanced.addBoolean("debugInputs", false);
        this.debugInfoTooltipsEnabled = advanced.addBoolean("debugInfoTooltipsEnabled", false);
        this.logSuffixTreeStats = advanced.addBoolean("logSuffixTreeStats", false);
    }

    public static boolean isDebugIngredientsEnabled() {
        if (instance == null) {
            return false;
        }
        return DebugConfig.instance.debugIngredientsEnabled.get();
    }

    public static boolean isDebugGuisEnabled() {
        if (instance == null) {
            return false;
        }
        return DebugConfig.instance.debugGuisEnabled.get();
    }

    public static boolean isDebugInputsEnabled() {
        if (instance == null) {
            return false;
        }
        return DebugConfig.instance.debugInputsEnabled.get();
    }

    public static boolean isDebugInfoTooltipsEnabled() {
        if (instance == null) {
            return false;
        }
        return DebugConfig.instance.debugInfoTooltipsEnabled.get();
    }

    public static boolean isLogSuffixTreeStatsEnabled() {
        if (instance == null) {
            return false;
        }
        return DebugConfig.instance.logSuffixTreeStats.get();
    }
}

