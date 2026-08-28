/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.IExtensionPoint
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
 *  net.neoforged.neoforge.client.event.RegisterShadersEvent
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent$Stage
 *  net.neoforged.neoforge.client.gui.ConfigurationScreen
 *  net.neoforged.neoforge.client.gui.IConfigScreenFactory
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.leonardoinc22.shortgrass.neoforge;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import com.leonardoinc22.shortgrass.client.render.GrassRenderType;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.leonardoinc22.shortgrass.neoforge.HiddenBakedModel;
import com.leonardoinc22.shortgrass.neoforge.ShortGrassConfig;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value="grassiergrass")
public final class ShortGrassMod {
    public static final String MODID = "grassiergrass";

    public ShortGrassMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)ShortGrassConfig.SPEC);
        modEventBus.addListener(event -> ShortGrassConfig.sync());
        modEventBus.addListener(event -> ShortGrassConfig.sync());
        GrassConfig.setPersistHook(ShortGrassConfig::writeBack);
        modEventBus.addListener(ShortGrassMod::onRegisterShaders);
        modEventBus.addListener(HiddenBakedModel::onModifyBakingResult);
        NeoForge.EVENT_BUS.addListener(ShortGrassMod::onRegisterClientCommands);
        NeoForge.EVENT_BUS.addListener(ShortGrassMod::onRenderLevelStage);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (IExtensionPoint)((IConfigScreenFactory)ConfigurationScreen::new));
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            return;
        }
        GrassRenderPass.render(event.getCamera(), event.getPartialTick().getGameTimeDeltaPartialTick(true), event.getProjectionMatrix(), event.getModelViewMatrix(), event.getFrustum());
    }

    private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)MODID).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"wind").then(((LiteralArgumentBuilder)Commands.literal((String)"dynamic").executes(context -> {
            GrassConfig.setDynamicWind(!GrassConfig.dynamicWind);
            GrassConfig.save();
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass dynamic wind: " + (GrassConfig.dynamicWind ? "on" : "off"))), false);
            return GrassConfig.dynamicWind ? 1 : 0;
        })).then(Commands.argument((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            GrassConfig.setDynamicWind(BoolArgumentType.getBool((CommandContext)context, (String)"enabled"));
            GrassConfig.save();
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass dynamic wind: " + (GrassConfig.dynamicWind ? "on" : "off"))), false);
            return GrassConfig.dynamicWind ? 1 : 0;
        })))).then(Commands.literal((String)"direction").then(Commands.argument((String)"degrees", (ArgumentType)FloatArgumentType.floatArg()).executes(context -> {
            GrassConfig.setWindDirectionDegrees(FloatArgumentType.getFloat((CommandContext)context, (String)"degrees"));
            GrassConfig.save();
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass wind direction: " + GrassConfig.windDirectionDegrees + " degrees")), false);
            return Math.round(GrassConfig.windDirectionDegrees);
        })))).then(Commands.argument((String)"speed", (ArgumentType)IntegerArgumentType.integer((int)0, (int)500)).executes(context -> {
            GrassConfig.setWindSpeed(IntegerArgumentType.getInteger((CommandContext)context, (String)"speed"));
            GrassConfig.save();
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass wind speed: " + GrassConfig.windSpeed)), false);
            return GrassConfig.windSpeed;
        })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"blacklist").then(Commands.literal((String)"list").executes(ShortGrassMod::listBlacklist))).then(Commands.literal((String)"add").then(Commands.argument((String)"block", (ArgumentType)StringArgumentType.greedyString()).suggests((context, builder) -> ShortGrassMod.suggestKnownBlocks(builder)).executes(ShortGrassMod::addBlacklist)))).then(Commands.literal((String)"remove").then(Commands.argument((String)"block", (ArgumentType)StringArgumentType.greedyString()).suggests((context, builder) -> ShortGrassMod.suggestBlacklistedBlocks(builder)).executes(ShortGrassMod::removeBlacklist)))).then(Commands.literal((String)"clear").executes(ShortGrassMod::clearBlacklist))).then(Commands.literal((String)"reset").executes(ShortGrassMod::resetBlacklist)))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"whitelist").then(Commands.literal((String)"list").executes(ShortGrassMod::listWhitelist))).then(Commands.literal((String)"add").then(Commands.argument((String)"block", (ArgumentType)StringArgumentType.greedyString()).suggests((context, builder) -> ShortGrassMod.suggestKnownBlocks(builder)).executes(ShortGrassMod::addWhitelist)))).then(Commands.literal((String)"remove").then(Commands.argument((String)"block", (ArgumentType)StringArgumentType.greedyString()).suggests((context, builder) -> ShortGrassMod.suggestWhitelistedBlocks(builder)).executes(ShortGrassMod::removeWhitelist)))).then(Commands.literal((String)"clear").executes(ShortGrassMod::clearWhitelist))).then(Commands.literal((String)"reset").executes(ShortGrassMod::resetWhitelist))));
    }

    private static int listBlacklist(CommandContext<CommandSourceStack> context) {
        List<String> ids = GrassConfig.plantBlacklistIds();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass plant blacklist: " + (ids.isEmpty() ? "(empty)" : String.join((CharSequence)", ", ids)))), false);
        return ids.size();
    }

    private static int addBlacklist(CommandContext<CommandSourceStack> context) {
        ResourceLocation id = ShortGrassMod.parseCommandBlock(context);
        if (id == null) {
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Unknown block id: " + StringArgumentType.getString((CommandContext)context, (String)"block"))), false);
            return 0;
        }
        boolean changed = GrassConfig.addPlantBlacklist(id);
        ShortGrassMod.saveAndFlushBlacklist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass plant blacklist: " + String.valueOf(id) + (changed ? " added" : " was already present"))), false);
        return changed ? 1 : 0;
    }

    private static int removeBlacklist(CommandContext<CommandSourceStack> context) {
        ResourceLocation id = ShortGrassMod.parseCommandBlock(context);
        if (id == null) {
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Unknown block id: " + StringArgumentType.getString((CommandContext)context, (String)"block"))), false);
            return 0;
        }
        boolean changed = GrassConfig.removePlantBlacklist(id);
        ShortGrassMod.saveAndFlushBlacklist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass plant blacklist: " + String.valueOf(id) + (changed ? " removed" : " was not present"))), false);
        return changed ? 1 : 0;
    }

    private static int clearBlacklist(CommandContext<CommandSourceStack> context) {
        boolean changed = GrassConfig.clearPlantBlacklist();
        ShortGrassMod.saveAndFlushBlacklist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)"Grassier Grass plant blacklist cleared"), false);
        return changed ? 1 : 0;
    }

    private static int resetBlacklist(CommandContext<CommandSourceStack> context) {
        boolean changed = GrassConfig.resetPlantBlacklist();
        ShortGrassMod.saveAndFlushBlacklist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass plant blacklist reset to defaults: " + String.join((CharSequence)", ", GrassConfig.plantBlacklistIds()))), false);
        return changed ? 1 : 0;
    }

    private static int listWhitelist(CommandContext<CommandSourceStack> context) {
        List<String> ids = GrassConfig.plantWhitelistIds();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass plant whitelist: " + (ids.isEmpty() ? "(empty)" : String.join((CharSequence)", ", ids)))), false);
        return ids.size();
    }

    private static int addWhitelist(CommandContext<CommandSourceStack> context) {
        ResourceLocation id = ShortGrassMod.parseCommandBlock(context);
        if (id == null) {
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Unknown block id: " + StringArgumentType.getString((CommandContext)context, (String)"block"))), false);
            return 0;
        }
        boolean changed = GrassConfig.addPlantWhitelist(id);
        ShortGrassMod.saveAndFlushWhitelist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass plant whitelist: " + String.valueOf(id) + (changed ? " added (reloading resources)" : " was already present"))), false);
        return changed ? 1 : 0;
    }

    private static int removeWhitelist(CommandContext<CommandSourceStack> context) {
        ResourceLocation id = ShortGrassMod.parseCommandBlock(context);
        if (id == null) {
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Unknown block id: " + StringArgumentType.getString((CommandContext)context, (String)"block"))), false);
            return 0;
        }
        boolean changed = GrassConfig.removePlantWhitelist(id);
        ShortGrassMod.saveAndFlushWhitelist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Grassier Grass plant whitelist: " + String.valueOf(id) + (changed ? " removed (reloading resources)" : " was not present"))), false);
        return changed ? 1 : 0;
    }

    private static int clearWhitelist(CommandContext<CommandSourceStack> context) {
        boolean changed = GrassConfig.clearPlantWhitelist();
        ShortGrassMod.saveAndFlushWhitelist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)"Grassier Grass plant whitelist cleared (reloading resources)"), false);
        return changed ? 1 : 0;
    }

    private static int resetWhitelist(CommandContext<CommandSourceStack> context) {
        boolean changed = GrassConfig.resetPlantWhitelist();
        ShortGrassMod.saveAndFlushWhitelist();
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)"Grassier Grass plant whitelist reset (reloading resources)"), false);
        return changed ? 1 : 0;
    }

    private static ResourceLocation parseCommandBlock(CommandContext<CommandSourceStack> context) {
        return GrassConfig.parseKnownBlockIdentifier(StringArgumentType.getString(context, (String)"block"));
    }

    private static void saveAndFlushBlacklist() {
        GrassConfig.save();
        GrassRenderPass.flushAllGeometry();
    }

    private static void saveAndFlushWhitelist() {
        GrassConfig.save();
        GrassRenderPass.flushAllGeometry();
        Minecraft.getInstance().reloadResourcePacks();
    }

    private static CompletableFuture<Suggestions> suggestKnownBlocks(SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String id : GrassConfig.knownBlockIds()) {
            if (!id.startsWith(remaining)) continue;
            builder.suggest(id);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestBlacklistedBlocks(SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String id : GrassConfig.plantBlacklistIds()) {
            if (!id.startsWith(remaining)) continue;
            builder.suggest(id);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestWhitelistedBlocks(SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String id : GrassConfig.plantWhitelistIds()) {
            if (!id.startsWith(remaining)) continue;
            builder.suggest(id);
        }
        return builder.buildFuture();
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath((String)MODID, (String)"grass_blades"), DefaultVertexFormat.BLOCK), GrassRenderType::setGrassShader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath((String)MODID, (String)"grass_plant"), DefaultVertexFormat.BLOCK), GrassRenderType::setPlantShader);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Failed to load grassiergrass shaders", exception);
        }
    }
}

