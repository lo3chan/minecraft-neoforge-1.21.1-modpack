/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  com.mojang.blaze3d.platform.GlDebug
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializerRegistry
 *  net.minecraft.ChatFormatting
 *  net.minecraft.SharedConstants
 *  net.minecraft.Util
 *  net.minecraft.Util$OS
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package net.irisshaders.iris;

import com.google.common.base.Throwables;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializerRegistry;
import net.irisshaders.iris.IrisLogging;
import net.irisshaders.iris.UpdateChecker;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.config.IrisConfig;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.gui.debug.DebugLoadFailedGridScreen;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.pbr.texture.PBRTextureManager;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.VanillaRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.shaderpack.DimensionId;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.discovery.ShaderpackDirectoryManager;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.Profile;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.sodium.EntityToTerrainVertexSerializer;
import net.irisshaders.iris.vertices.sodium.GlyphExtVertexSerializer;
import net.irisshaders.iris.vertices.sodium.IrisEntityToTerrainVertexSerializer;
import net.irisshaders.iris.vertices.sodium.ModelToEntityVertexSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Iris {
    public static final String MODID = "iris";
    public static final String MODNAME = "Iris";
    public static final IrisLogging logger = new IrisLogging("Iris");
    private static final Map<String, String> shaderPackOptionQueue = new HashMap<String, String>();
    private static final String backupVersionNumber = "1.21";
    public static NamespacedId lastDimension = null;
    public static boolean testing = false;
    private static Path shaderpacksDirectory;
    private static ShaderpackDirectoryManager shaderpacksDirectoryManager;
    private static ShaderPack currentPack;
    private static String currentPackName;
    private static Optional<Exception> storedError;
    private static boolean initialized;
    private static PipelineManager pipelineManager;
    private static IrisConfig irisConfig;
    private static FileSystem zipFileSystem;
    private static KeyMapping reloadKeybind;
    private static KeyMapping toggleShadersKeybind;
    private static KeyMapping shaderpackScreenKeybind;
    private static KeyMapping wireframeKeybind;
    private static boolean resetShaderPackOptions;
    private static String IRIS_VERSION;
    private static UpdateChecker updateChecker;
    private static boolean fallback;
    private static boolean loadShaderPackWhenPossible;

    public static void onRenderSystemInit() {
        if (!initialized) {
            logger.warn("Iris::onRenderSystemInit was called, but Iris::onEarlyInitialize was not called. Trying to avoid a crash but this is an odd state.");
            return;
        }
        PBRTextureManager.INSTANCE.init();
        VertexSerializerRegistry.instance().registerSerializer(DefaultVertexFormat.NEW_ENTITY, IrisVertexFormats.TERRAIN, (VertexSerializer)new EntityToTerrainVertexSerializer());
        VertexSerializerRegistry.instance().registerSerializer(IrisVertexFormats.ENTITY, IrisVertexFormats.TERRAIN, (VertexSerializer)new IrisEntityToTerrainVertexSerializer());
        VertexSerializerRegistry.instance().registerSerializer(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, IrisVertexFormats.GLYPH, (VertexSerializer)new GlyphExtVertexSerializer());
        VertexSerializerRegistry.instance().registerSerializer(DefaultVertexFormat.NEW_ENTITY, IrisVertexFormats.ENTITY, (VertexSerializer)new ModelToEntityVertexSerializer());
        if (!IrisPlatformHelpers.getInstance().isModLoaded("distanthorizons")) {
            Iris.loadShaderpack();
        }
    }

    public static void duringRenderSystemInit() {
        Iris.setDebug(irisConfig.areDebugOptionsEnabled());
    }

    public static void onLoadingComplete() {
        if (!initialized) {
            logger.warn("Iris::onLoadingComplete was called, but Iris::onEarlyInitialize was not called. Trying to avoid a crash but this is an odd state.");
            return;
        }
        lastDimension = DimensionId.OVERWORLD;
        Iris.getPipelineManager().preparePipeline(DimensionId.OVERWORLD);
    }

    public static void handleKeybinds(Minecraft minecraft) {
        block14: {
            if (loadShaderPackWhenPossible) {
                loadShaderPackWhenPossible = false;
                Iris.loadShaderpack();
            }
            if (reloadKeybind.consumeClick()) {
                try {
                    Iris.reload();
                    if (minecraft.player != null) {
                        minecraft.player.displayClientMessage((Component)Component.translatable((String)"iris.shaders.reloaded"), false);
                    }
                    break block14;
                }
                catch (Exception e) {
                    logger.error("Error while reloading Shaders for Iris!", e);
                    if (minecraft.player != null) {
                        minecraft.player.displayClientMessage((Component)Component.translatable((String)"iris.shaders.reloaded.failure", (Object[])new Object[]{Throwables.getRootCause((Throwable)e).getMessage()}).withStyle(ChatFormatting.RED), false);
                    }
                    break block14;
                }
            }
            if (toggleShadersKeybind.consumeClick()) {
                try {
                    Iris.toggleShaders(minecraft, !irisConfig.areShadersEnabled());
                }
                catch (Exception e) {
                    logger.error("Error while toggling shaders!", e);
                    if (minecraft.player != null) {
                        minecraft.player.displayClientMessage((Component)Component.translatable((String)"iris.shaders.toggled.failure", (Object[])new Object[]{Throwables.getRootCause((Throwable)e).getMessage()}).withStyle(ChatFormatting.RED), false);
                    }
                    Iris.setShadersDisabled();
                    fallback = true;
                }
            } else if (shaderpackScreenKeybind.consumeClick()) {
                minecraft.setScreen((Screen)new ShaderPackScreen(null));
            } else if (wireframeKeybind.consumeClick() && irisConfig.areDebugOptionsEnabled() && minecraft.player != null && !Minecraft.getInstance().isLocalServer()) {
                minecraft.player.displayClientMessage((Component)Component.literal((String)"No cheating; wireframe only in singleplayer!"), false);
            }
        }
    }

    public static boolean shouldActivateWireframe() {
        return irisConfig.areDebugOptionsEnabled() && wireframeKeybind.isDown();
    }

    public static void toggleShaders(Minecraft minecraft, boolean enabled) throws IOException {
        irisConfig.setShadersEnabled(enabled);
        irisConfig.save();
        Iris.reload();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage((Component)(enabled ? Component.translatable((String)"iris.shaders.toggled", (Object[])new Object[]{currentPackName}) : Component.translatable((String)"iris.shaders.disabled")), false);
        }
    }

    public static void loadShaderpack() {
        if (irisConfig == null) {
            if (!initialized) {
                throw new IllegalStateException("Iris::loadShaderpack was called, but Iris::onInitializeClient wasn't called yet. How did this happen?");
            }
            throw new NullPointerException("Iris.irisConfig was null unexpectedly");
        }
        if (!irisConfig.areShadersEnabled()) {
            logger.info("Shaders are disabled because enableShaders is set to false in iris.properties");
            Iris.setShadersDisabled();
            return;
        }
        Optional<String> externalName = irisConfig.getShaderPackName();
        if (externalName.isEmpty()) {
            logger.info("Shaders are disabled because no valid shaderpack is selected");
            Iris.setShadersDisabled();
            return;
        }
        if (!Iris.loadExternalShaderpack(externalName.get())) {
            logger.warn("Falling back to normal rendering without shaders because the shaderpack could not be loaded");
            Iris.setShadersDisabled();
            fallback = true;
        }
    }

    private static boolean loadExternalShaderpack(String name) {
        Path shaderPackPath;
        Path shaderPackConfigTxt;
        Path shaderPackRoot;
        try {
            shaderPackRoot = Iris.getShaderpacksDirectory().resolve(name);
            shaderPackConfigTxt = Iris.getShaderpacksDirectory().resolve(name + ".txt");
        }
        catch (InvalidPathException e) {
            logger.error("Failed to load the shaderpack \"{}\" because it contains invalid characters in its path", name);
            return false;
        }
        if (!Iris.isValidShaderpack(shaderPackRoot)) {
            logger.error("Pack \"{}\" is not valid! Can't load it.", name);
            return false;
        }
        boolean isZip = false;
        if (!Files.isDirectory(shaderPackRoot, new LinkOption[0]) && shaderPackRoot.toString().endsWith(".zip")) {
            Optional<Path> optionalPath;
            try {
                optionalPath = Iris.loadExternalZipShaderpack(shaderPackRoot);
            }
            catch (FileSystemNotFoundException | NoSuchFileException e) {
                logger.error("Failed to load the shaderpack \"{}\" because it does not exist in your shaderpacks folder!", name);
                return false;
            }
            catch (ZipException e) {
                logger.error("The shaderpack \"{}\" appears to be corrupted, please try downloading it again!", name);
                return false;
            }
            catch (IOException e) {
                logger.error("Failed to load the shaderpack \"{}\"!", name);
                logger.error("", e);
                return false;
            }
            if (!optionalPath.isPresent()) {
                logger.error("Could not load the shaderpack \"{}\" because it appears to lack a \"shaders\" directory", name);
                return false;
            }
            shaderPackPath = optionalPath.get();
            isZip = true;
        } else {
            if (!Files.exists(shaderPackRoot, new LinkOption[0])) {
                logger.error("Failed to load the shaderpack \"{}\" because it does not exist!", name);
                return false;
            }
            shaderPackPath = shaderPackRoot.resolve("shaders");
        }
        if (!Files.exists(shaderPackPath, new LinkOption[0])) {
            logger.error("Could not load the shaderpack \"{}\" because it appears to lack a \"shaders\" directory", name);
            return false;
        }
        Map changedConfigs = Iris.tryReadConfigProperties(shaderPackConfigTxt).map(properties -> properties).orElse(new HashMap());
        changedConfigs.putAll(shaderPackOptionQueue);
        Iris.clearShaderPackOptionQueue();
        if (resetShaderPackOptions) {
            changedConfigs.clear();
        }
        resetShaderPackOptions = false;
        try {
            currentPack = new ShaderPack(shaderPackPath, changedConfigs, StandardMacros.createStandardEnvironmentDefines(), isZip);
            MutableOptionValues changedConfigsValues = currentPack.getShaderPackOptions().getOptionValues().mutableCopy();
            Properties configsToSave = new Properties();
            changedConfigsValues.getBooleanValues().forEach((k, v) -> configsToSave.setProperty((String)k, Boolean.toString(v)));
            changedConfigsValues.getStringValues().forEach(configsToSave::setProperty);
            Iris.tryUpdateConfigPropertiesFile(shaderPackConfigTxt, configsToSave);
        }
        catch (Exception e) {
            logger.error("Failed to load the shaderpack \"{}\"!", name);
            logger.error("", e);
            Iris.handleException(e);
            return false;
        }
        fallback = false;
        currentPackName = name;
        logger.info("Using shaderpack: " + name);
        return true;
    }

    private static void handleException(Exception e) {
        if (lastDimension != null && irisConfig.areDebugOptionsEnabled()) {
            Minecraft.getInstance().setScreen((Screen)new DebugLoadFailedGridScreen(Minecraft.getInstance().screen, (Component)Component.literal((String)(e instanceof ShaderCompileException ? "Failed to compile shaders" : "Exception")), e));
        } else if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage((Component)Component.translatable((String)(e instanceof ShaderCompileException ? "iris.load.failure.shader" : "iris.load.failure.generic")).append((Component)Component.literal((String)"Copy Info").withStyle(arg -> arg.withUnderlined(Boolean.valueOf(true)).withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, e.getMessage())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Object)Component.translatable((String)"chat.copy.click"))))), false);
        } else {
            storedError = Optional.of(e);
        }
    }

    private static Optional<Path> loadExternalZipShaderpack(Path shaderpackPath) throws IOException {
        FileSystem zipSystem;
        zipFileSystem = zipSystem = FileSystems.newFileSystem(shaderpackPath, Iris.class.getClassLoader());
        Path root = zipSystem.getRootDirectories().iterator().next();
        Path potentialShaderDir = zipSystem.getPath("shaders", new String[0]);
        if (Files.exists(potentialShaderDir, new LinkOption[0])) {
            return Optional.of(potentialShaderDir);
        }
        try (Stream<Path> stream = Files.walk(root, new FileVisitOption[0]);){
            Optional<Path> optional = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).filter(path -> path.endsWith("shaders")).findFirst();
            return optional;
        }
    }

    private static void setShadersDisabled() {
        currentPack = null;
        fallback = false;
        currentPackName = "(off)";
    }

    public static void setDebug(boolean enable) {
        int success;
        try {
            irisConfig.setDebugEnabled(enable);
            irisConfig.save();
        }
        catch (IOException e) {
            logger.fatal("Failed to save config!", e);
        }
        if (enable) {
            success = GLDebug.setupDebugMessageCallback();
        } else {
            GLDebug.reloadDebugState();
            GlDebug.enableDebugCallback((int)Minecraft.getInstance().options.glDebugVerbosity, (boolean)false);
            success = 1;
        }
        logger.info("Debug functionality is " + (enable ? "enabled, logging will be more verbose!" : "disabled."));
        if (Minecraft.getInstance().player != null) {
            if (IrisPlatformHelpers.getInstance().useELS()) {
                Minecraft.getInstance().player.displayClientMessage((Component)Component.translatable((String)"iris.shaders.debug.restartNoDebug"), false);
            } else {
                Minecraft.getInstance().player.displayClientMessage((Component)Component.translatable((String)(success != 0 ? (enable ? "iris.shaders.debug.enabled" : "iris.shaders.debug.disabled") : "iris.shaders.debug.failure")), false);
            }
            if (success == 2 && !IrisPlatformHelpers.getInstance().useELS()) {
                Minecraft.getInstance().player.displayClientMessage((Component)Component.translatable((String)"iris.shaders.debug.restart"), false);
            }
        }
    }

    private static Optional<Properties> tryReadConfigProperties(Path path) {
        Properties properties = new Properties();
        if (Files.exists(path, new LinkOption[0])) {
            try (InputStream is = Files.newInputStream(path, new OpenOption[0]);){
                properties.load(is);
            }
            catch (IOException e) {
                return Optional.empty();
            }
        }
        return Optional.of(properties);
    }

    private static void tryUpdateConfigPropertiesFile(Path path, Properties properties) {
        try {
            if (properties.isEmpty()) {
                if (Files.exists(path, new LinkOption[0])) {
                    Files.delete(path);
                }
                return;
            }
            try (OutputStream out = Files.newOutputStream(path, new OpenOption[0]);){
                properties.store(out, null);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static boolean isValidToShowPack(Path pack) {
        return Files.isDirectory(pack, new LinkOption[0]) || pack.toString().endsWith(".zip");
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static boolean isValidShaderpack(Path pack) {
        if (Files.isDirectory(pack, new LinkOption[0])) {
            if (pack.equals(Iris.getShaderpacksDirectory())) {
                return false;
            }
            try (Stream<Path> stream = Files.walk(pack, new FileVisitOption[0]);){
                boolean bl = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).filter(path -> !path.equals(pack)).anyMatch(path -> path.endsWith("shaders"));
                return bl;
            }
            catch (IOException ignored) {
                return false;
            }
        }
        if (pack.toString().endsWith(".zip")) {
            try (FileSystem zipSystem = FileSystems.newFileSystem(pack, Iris.class.getClassLoader());){
                boolean bl;
                block26: {
                    Path root = zipSystem.getRootDirectories().iterator().next();
                    Stream<Path> stream = Files.walk(root, new FileVisitOption[0]);
                    try {
                        bl = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).anyMatch(path -> path.endsWith("shaders"));
                        if (stream == null) break block26;
                    }
                    catch (Throwable throwable) {
                        if (stream != null) {
                            try {
                                stream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    stream.close();
                }
                return bl;
            }
            catch (ZipError zipError) {
                logger.warn("The ZIP at " + String.valueOf(pack) + " is corrupt");
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return false;
    }

    public static Map<String, String> getShaderPackOptionQueue() {
        return shaderPackOptionQueue;
    }

    public static void queueShaderPackOptionsFromProfile(Profile profile) {
        Iris.getShaderPackOptionQueue().putAll(profile.optionValues);
    }

    public static void queueShaderPackOptionsFromProperties(Properties properties) {
        Iris.queueDefaultShaderPackOptionValues();
        properties.stringPropertyNames().forEach(key -> Iris.getShaderPackOptionQueue().put((String)key, properties.getProperty((String)key)));
    }

    public static void queueDefaultShaderPackOptionValues() {
        Iris.clearShaderPackOptionQueue();
        Iris.getCurrentPack().ifPresent(pack -> {
            OptionSet options = pack.getShaderPackOptions().getOptionSet();
            OptionValues values = pack.getShaderPackOptions().getOptionValues();
            options.getStringOptions().forEach((key, mOpt) -> {
                if (values.getStringValue((String)key).isPresent()) {
                    Iris.getShaderPackOptionQueue().put((String)key, mOpt.getOption().getDefaultValue());
                }
            });
            options.getBooleanOptions().forEach((key, mOpt) -> {
                if (values.getBooleanValue((String)key) != OptionalBoolean.DEFAULT) {
                    Iris.getShaderPackOptionQueue().put((String)key, Boolean.toString(mOpt.getOption().getDefaultValue()));
                }
            });
        });
    }

    public static void clearShaderPackOptionQueue() {
        Iris.getShaderPackOptionQueue().clear();
    }

    public static void resetShaderPackOptionsOnNextReload() {
        resetShaderPackOptions = true;
    }

    public static boolean shouldResetShaderPackOptionsOnNextReload() {
        return resetShaderPackOptions;
    }

    public static void reload() throws IOException {
        irisConfig.initialize();
        Iris.destroyEverything();
        Iris.loadShaderpack();
        if (Minecraft.getInstance().level != null) {
            Iris.getPipelineManager().preparePipeline(Iris.getCurrentDimension());
        }
    }

    private static void destroyEverything() {
        currentPack = null;
        Iris.getPipelineManager().destroyPipeline();
        if (zipFileSystem != null) {
            try {
                zipFileSystem.close();
            }
            catch (NoSuchFileException e) {
                logger.warn("Failed to close the shaderpack zip when reloading because it was deleted, proceeding anyways.");
            }
            catch (IOException e) {
                logger.error("Failed to close zip file system?", e);
            }
        }
    }

    public static NamespacedId getCurrentDimension() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            NamespacedId dimensionId = new NamespacedId(level.dimension().location().getNamespace(), level.dimension().location().getPath());
            ShaderPack pack = Iris.getCurrentPack().orElse(null);
            if (pack != null && pack.getDimensionMap().containsKey(dimensionId)) {
                return dimensionId;
            }
            ResourceLocation effects = level.dimensionType().effectsLocation();
            if (Level.END.location().equals((Object)effects)) {
                return DimensionId.END;
            }
            if (Level.NETHER.location().equals((Object)effects)) {
                return DimensionId.NETHER;
            }
            return dimensionId;
        }
        return lastDimension;
    }

    private static WorldRenderingPipeline createPipeline(NamespacedId dimensionId) {
        if (currentPack == null) {
            return new VanillaRenderingPipeline();
        }
        ProgramSet programs = currentPack.getProgramSet(dimensionId);
        try {
            return new IrisRenderingPipeline(programs);
        }
        catch (Exception e) {
            Iris.handleException(e);
            ShaderStorageBufferHolder.forceDeleteBuffers();
            logger.error("Failed to create shader rendering pipeline, disabling shaders!", e);
            fallback = true;
            return new VanillaRenderingPipeline();
        }
    }

    @NotNull
    public static PipelineManager getPipelineManager() {
        if (pipelineManager == null) {
            pipelineManager = new PipelineManager(Iris::createPipeline);
        }
        return pipelineManager;
    }

    public static Optional<Exception> getStoredError() {
        Optional<Exception> stored = storedError;
        storedError = Optional.empty();
        return stored;
    }

    @NotNull
    public static Optional<ShaderPack> getCurrentPack() {
        return Optional.ofNullable(currentPack);
    }

    public static String getCurrentPackName() {
        return currentPackName;
    }

    public static IrisConfig getIrisConfig() {
        return irisConfig;
    }

    public static UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public static boolean isFallback() {
        return fallback;
    }

    public static String getVersion() {
        if (IRIS_VERSION == null) {
            return "Version info unknown!";
        }
        return IRIS_VERSION;
    }

    public static String getFormattedVersion() {
        ChatFormatting color;
        Object version = Iris.getVersion();
        if (IrisPlatformHelpers.getInstance().isDevelopmentEnvironment()) {
            color = ChatFormatting.GOLD;
            version = (String)version + " (Development Environment)";
        } else {
            color = ((String)version).endsWith("-dirty") || ((String)version).contains("unknown") || ((String)version).endsWith("-nogit") ? ChatFormatting.RED : (((String)version).contains("+rev.") ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GREEN);
        }
        return String.valueOf(color) + (String)version;
    }

    public static String getReleaseTarget() {
        SharedConstants.tryDetectVersion();
        return SharedConstants.getCurrentVersion().isStable() ? SharedConstants.getCurrentVersion().getName() : backupVersionNumber;
    }

    public static String getBackupVersionNumber() {
        return backupVersionNumber;
    }

    public static Path getShaderpacksDirectory() {
        if (shaderpacksDirectory == null) {
            shaderpacksDirectory = IrisPlatformHelpers.getInstance().getGameDir().resolve("shaderpacks");
        }
        return shaderpacksDirectory;
    }

    public static ShaderpackDirectoryManager getShaderpacksDirectoryManager() {
        if (shaderpacksDirectoryManager == null) {
            shaderpacksDirectoryManager = new ShaderpackDirectoryManager(Iris.getShaderpacksDirectory());
        }
        return shaderpacksDirectoryManager;
    }

    public static boolean loadedIncompatiblePack() {
        return DHCompat.lastPackIncompatible();
    }

    public static boolean isPackInUseQuick() {
        return Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline;
    }

    public static void loadShaderpackWhenPossible() {
        loadShaderPackWhenPossible = true;
    }

    public static String getVersionSimple() {
        return Iris.getVersion().split("\\+")[0];
    }

    public void onEarlyInitialize() {
        IRIS_VERSION = IrisPlatformHelpers.getInstance().getVersion();
        updateChecker = new UpdateChecker(IRIS_VERSION);
        reloadKeybind = IrisPlatformHelpers.getInstance().registerKeyBinding(new KeyMapping("iris.keybind.reload", InputConstants.Type.KEYSYM, 82, "iris.keybinds"));
        toggleShadersKeybind = IrisPlatformHelpers.getInstance().registerKeyBinding(new KeyMapping("iris.keybind.toggleShaders", InputConstants.Type.KEYSYM, 75, "iris.keybinds"));
        shaderpackScreenKeybind = IrisPlatformHelpers.getInstance().registerKeyBinding(new KeyMapping("iris.keybind.shaderPackSelection", InputConstants.Type.KEYSYM, 79, "iris.keybinds"));
        wireframeKeybind = IrisPlatformHelpers.getInstance().registerKeyBinding(new KeyMapping("iris.keybind.wireframe", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "iris.keybinds"));
        DHCompat.run();
        try {
            if (!Files.exists(Iris.getShaderpacksDirectory(), new LinkOption[0])) {
                Files.createDirectories(Iris.getShaderpacksDirectory(), new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            logger.warn("Failed to create the shaderpacks directory!");
            logger.warn("", e);
        }
        irisConfig = new IrisConfig(IrisPlatformHelpers.getInstance().getConfigDir().resolve("iris.properties"), IrisPlatformHelpers.getInstance().getConfigDir().resolve("iris-excluded.json"));
        try {
            irisConfig.initialize();
        }
        catch (IOException e) {
            logger.error("Failed to initialize Iris configuration, default values will be used instead");
            logger.error("", e);
        }
        updateChecker.checkForUpdates(irisConfig);
        initialized = true;
    }

    static {
        storedError = Optional.empty();
        resetShaderPackOptions = false;
        if (!IrisPlatformHelpers.getInstance().isDevelopmentEnvironment() || !System.getProperty("user.name").contains("ims") || Util.getPlatform() == Util.OS.LINUX) {
            // empty if block
        }
    }
}

