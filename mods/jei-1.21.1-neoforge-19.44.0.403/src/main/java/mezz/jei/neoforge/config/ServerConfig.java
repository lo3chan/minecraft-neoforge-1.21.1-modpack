/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 */
package mezz.jei.neoforge.config;

import java.util.function.Supplier;
import mezz.jei.common.config.IServerConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig
implements IServerConfig {
    private final Supplier<Boolean> enableCheatModeForOp;
    private final Supplier<Boolean> enableCheatModeForCreative;
    private final Supplier<Boolean> enableCheatModeForGive;

    public static IServerConfig register(ModLoadingContext modLoadingContext) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ServerConfig instance = new ServerConfig(builder);
        ModConfigSpec config = builder.build();
        ModContainer activeContainer = modLoadingContext.getActiveContainer();
        activeContainer.registerConfig(ModConfig.Type.SERVER, (IConfigSpec)config);
        return instance;
    }

    private ServerConfig(ModConfigSpec.Builder builder) {
        builder.push("cheat mode");
        builder.comment("Enable the cheat mode for players who have an operator status (/op).");
        this.enableCheatModeForOp = builder.define("enableCheatModeForOp", true);
        builder.comment("Enable the cheat mode for players who are in the creative mode.");
        this.enableCheatModeForCreative = builder.define("enableCheatModeForCreative", true);
        builder.comment("Enable the cheat mode for players who can use the \"/give\" command.");
        this.enableCheatModeForGive = builder.define("enableCheatModeForGive", false);
        builder.pop();
    }

    @Override
    public boolean isCheatModeEnabledForOp() {
        return this.enableCheatModeForOp.get();
    }

    @Override
    public boolean isCheatModeEnabledForCreative() {
        return this.enableCheatModeForCreative.get();
    }

    @Override
    public boolean isCheatModeEnabledForGive() {
        return this.enableCheatModeForGive.get();
    }
}

