/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonParseException
 *  net.caffeinemc.mods.sodium.api.config.ConfigState
 *  net.caffeinemc.mods.sodium.api.config.StorageEventHandler
 *  net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen
 *  net.caffeinemc.mods.sodium.client.services.PlatformRuntimeInformation
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package me.flashyreese.mods.reeses_sodium_options.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import me.flashyreese.mods.reeses_sodium_options.client.gui.PreviousScreenHolder;
import me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.caffeinemc.mods.sodium.client.services.PlatformRuntimeInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReeseSodiumOptionsConfig {
    static final StorageEventHandler STORAGE_HANDLER = ReeseSodiumOptionsConfig::writeToDisk;
    static final int DEFAULT_TOOLTIP_DELAY_MS = 500;
    static final int MIN_TOOLTIP_DELAY_MS = 0;
    static final int MAX_TOOLTIP_DELAY_MS = 5000;
    static final int DEFAULT_SEARCH_RESULT_LIMIT = 15;
    static final int MIN_SEARCH_RESULT_LIMIT = 1;
    static final int MAX_SEARCH_RESULT_LIMIT = 50;
    static final TabHeaderCollapseMode DEFAULT_TAB_HEADER_COLLAPSE_MODE = TabHeaderCollapseMode.ALL_EXPANDED;
    static final DisabledOptionVisibility DEFAULT_DISABLED_OPTION_VISIBILITY = DisabledOptionVisibility.SHOWN;
    static final FocusBorderMode DEFAULT_FOCUS_BORDER_MODE = FocusBorderMode.KEYBOARD;
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Reese's Sodium Options");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = PlatformRuntimeInformation.getInstance().getConfigDirectory().resolve("reeses_sodium_options.json");
    private static ConfigData config = new ConfigData();

    public static ConfigData config() {
        return config;
    }

    static void rebuildCurrentScreen(ConfigState ignored) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof SodiumVideoOptionsScreen) {
            SodiumVideoOptionsScreen sodiumVideoOptionsScreen = (SodiumVideoOptionsScreen)screen;
            sodiumVideoOptionsScreen.rebuildUI();
        }
    }

    static void reopenScreen(ConfigState ignored) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (!(screen instanceof PreviousScreenHolder)) {
            return;
        }
        PreviousScreenHolder holder = (PreviousScreenHolder)screen;
        Screen previousScreen = holder.rso$previousScreen();
        minecraft.execute(() -> minecraft.setScreen(VideoSettingsScreen.createScreen((Screen)previousScreen)));
    }

    private static void readFromDisk() {
        if (!Files.exists(CONFIG_PATH, new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8);){
            ConfigData loadedConfig = (ConfigData)GSON.fromJson((Reader)reader, ConfigData.class);
            if (loadedConfig == null) {
                throw new JsonParseException("Root element must be a JSON object");
            }
            config = loadedConfig.validate();
        }
        catch (JsonParseException | IOException | IllegalStateException e) {
            LOGGER.warn("Failed to read configuration file, using defaults", e);
            config = new ConfigData();
            ReeseSodiumOptionsConfig.moveCorruptConfig();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void writeToDisk() {
        Path tempPath = null;
        try {
            Files.createDirectories(CONFIG_PATH.getParent(), new FileAttribute[0]);
            tempPath = Files.createTempFile(CONFIG_PATH.getParent(), CONFIG_PATH.getFileName().toString(), ".tmp", new FileAttribute[0]);
            ReeseSodiumOptionsConfig.writeConfigToTempFile(tempPath);
            ReeseSodiumOptionsConfig.moveTempFileIntoPlace(tempPath);
            tempPath = null;
            ReeseSodiumOptionsConfig.forceDirectory(CONFIG_PATH.getParent());
            if (tempPath == null) return;
        }
        catch (IOException e) {
            LOGGER.warn("Failed to write configuration file", (Throwable)e);
            return;
        }
        finally {
            if (tempPath != null) {
                ReeseSodiumOptionsConfig.deleteTempFile(tempPath);
            }
        }
        ReeseSodiumOptionsConfig.deleteTempFile(tempPath);
        return;
    }

    private static void writeConfigToTempFile(Path tempPath) throws IOException {
        config.validate();
        byte[] bytes = (GSON.toJson((Object)config) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
        try (FileChannel channel = FileChannel.open(tempPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);){
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            channel.force(true);
        }
    }

    private static void moveTempFileIntoPlace(Path tempPath) throws IOException {
        Files.move(tempPath, CONFIG_PATH, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void moveCorruptConfig() {
        Path corruptPath = ReeseSodiumOptionsConfig.nextCorruptConfigPath();
        try {
            Files.move(CONFIG_PATH, corruptPath, new CopyOption[0]);
            LOGGER.warn("Moved corrupt configuration file to {}", (Object)corruptPath);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to move corrupt configuration file", (Throwable)e);
        }
    }

    private static Path nextCorruptConfigPath() {
        Path basePath = CONFIG_PATH.resolveSibling(String.valueOf(CONFIG_PATH.getFileName()) + ".corrupt");
        if (!Files.exists(basePath, new LinkOption[0])) {
            return basePath;
        }
        int i = 1;
        Path path;
        while (Files.exists(path = CONFIG_PATH.resolveSibling(String.valueOf(CONFIG_PATH.getFileName()) + ".corrupt." + i), new LinkOption[0])) {
            ++i;
        }
        return path;
    }

    private static void deleteTempFile(Path tempPath) {
        try {
            Files.deleteIfExists(tempPath);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to delete temporary configuration file {}", (Object)tempPath, (Object)e);
        }
    }

    private static void forceDirectory(Path directory) {
        try (FileChannel channel = FileChannel.open(directory, StandardOpenOption.READ);){
            channel.force(true);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    static {
        ReeseSodiumOptionsConfig.readFromDisk();
    }

    public static final class ConfigData {
        private boolean enabled = true;
        private boolean tabHeaderIcons = true;
        private boolean tabHeaderVersionLabels = true;
        private TabHeaderCollapseMode tabHeaderCollapseMode = DEFAULT_TAB_HEADER_COLLAPSE_MODE;
        private boolean tabHeaders = true;
        private boolean collapseSinglePageGroups = true;
        private boolean collapsibleGroups = true;
        private int tooltipDelayMs = 500;
        private boolean tooltipOptionIds = false;
        private boolean colorThemes = true;
        private boolean themedHeadersAndLabels = true;
        private boolean themedTooltipBorders = true;
        private boolean reducedMotion = false;
        private boolean reverseCyclingControls = true;
        private boolean shiftScrollSliderAdjustments = true;
        private int searchResultLimit = 15;
        private boolean hideNonMatchingOptions = true;
        private Boolean hideNonMatchingTabs = null;
        private DisabledOptionVisibility disabledOptionVisibility = DEFAULT_DISABLED_OPTION_VISIBILITY;
        private FocusBorderMode focusBorderMode = DEFAULT_FOCUS_BORDER_MODE;
        private boolean controllerGuides = true;
        private boolean resetButtonOverlay = true;
        private boolean undoButtonOverlay = true;
        private boolean alwaysShowActionButtons = false;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isTabHeaderIcons() {
            return this.tabHeaderIcons;
        }

        public void setTabHeaderIcons(boolean tabHeaderIcons) {
            this.tabHeaderIcons = tabHeaderIcons;
        }

        public boolean isTabHeaderVersionLabels() {
            return this.tabHeaderVersionLabels;
        }

        public void setTabHeaderVersionLabels(boolean tabHeaderVersionLabels) {
            this.tabHeaderVersionLabels = tabHeaderVersionLabels;
        }

        public TabHeaderCollapseMode getTabHeaderCollapseMode() {
            return this.tabHeaderCollapseMode;
        }

        public void setTabHeaderCollapseMode(TabHeaderCollapseMode tabHeaderCollapseMode) {
            this.tabHeaderCollapseMode = tabHeaderCollapseMode == null ? DEFAULT_TAB_HEADER_COLLAPSE_MODE : tabHeaderCollapseMode;
        }

        public boolean isTabHeaders() {
            return this.tabHeaders;
        }

        public void setTabHeaders(boolean tabHeaders) {
            this.tabHeaders = tabHeaders;
        }

        public boolean isCollapseSinglePageGroups() {
            return this.collapseSinglePageGroups;
        }

        public void setCollapseSinglePageGroups(boolean collapseSinglePageGroups) {
            this.collapseSinglePageGroups = collapseSinglePageGroups;
        }

        public boolean isCollapsibleGroups() {
            return this.collapsibleGroups;
        }

        public void setCollapsibleGroups(boolean collapsibleGroups) {
            this.collapsibleGroups = collapsibleGroups;
        }

        public int getTooltipDelayMs() {
            return this.tooltipDelayMs;
        }

        public void setTooltipDelayMs(int tooltipDelayMs) {
            this.tooltipDelayMs = Math.clamp((long)tooltipDelayMs, (int)0, (int)5000);
        }

        public boolean isTooltipOptionIds() {
            return this.tooltipOptionIds;
        }

        public void setTooltipOptionIds(boolean tooltipOptionIds) {
            this.tooltipOptionIds = tooltipOptionIds;
        }

        public boolean isColorThemes() {
            return this.colorThemes;
        }

        public void setColorThemes(boolean colorThemes) {
            this.colorThemes = colorThemes;
        }

        public boolean isThemedHeadersAndLabels() {
            return this.themedHeadersAndLabels;
        }

        public void setThemedHeadersAndLabels(boolean themedHeadersAndLabels) {
            this.themedHeadersAndLabels = themedHeadersAndLabels;
        }

        public boolean isThemedTooltipBorders() {
            return this.themedTooltipBorders;
        }

        public void setThemedTooltipBorders(boolean themedTooltipBorders) {
            this.themedTooltipBorders = themedTooltipBorders;
        }

        public boolean isReducedMotion() {
            return this.reducedMotion;
        }

        public void setReducedMotion(boolean reducedMotion) {
            this.reducedMotion = reducedMotion;
        }

        public boolean isReverseCyclingControls() {
            return this.reverseCyclingControls;
        }

        public void setReverseCyclingControls(boolean reverseCyclingControls) {
            this.reverseCyclingControls = reverseCyclingControls;
        }

        public boolean isShiftScrollSliderAdjustments() {
            return this.shiftScrollSliderAdjustments;
        }

        public void setShiftScrollSliderAdjustments(boolean shiftScrollSliderAdjustments) {
            this.shiftScrollSliderAdjustments = shiftScrollSliderAdjustments;
        }

        public int getSearchResultLimit() {
            return this.searchResultLimit;
        }

        public void setSearchResultLimit(int searchResultLimit) {
            this.searchResultLimit = Math.clamp((long)searchResultLimit, (int)1, (int)50);
        }

        public boolean isHideNonMatchingOptions() {
            return this.hideNonMatchingOptions;
        }

        public void setHideNonMatchingOptions(boolean hideNonMatchingOptions) {
            this.hideNonMatchingOptions = hideNonMatchingOptions;
        }

        public boolean isHideNonMatchingTabs() {
            return this.hideNonMatchingTabs == null ? this.hideNonMatchingOptions : this.hideNonMatchingTabs;
        }

        public void setHideNonMatchingTabs(boolean hideNonMatchingTabs) {
            this.hideNonMatchingTabs = hideNonMatchingTabs;
        }

        public DisabledOptionVisibility getDisabledOptionVisibility() {
            return this.disabledOptionVisibility;
        }

        public void setDisabledOptionVisibility(DisabledOptionVisibility disabledOptionVisibility) {
            this.disabledOptionVisibility = disabledOptionVisibility == null ? DEFAULT_DISABLED_OPTION_VISIBILITY : disabledOptionVisibility;
        }

        public FocusBorderMode getFocusBorderMode() {
            return this.focusBorderMode;
        }

        public void setFocusBorderMode(FocusBorderMode focusBorderMode) {
            this.focusBorderMode = focusBorderMode == null ? DEFAULT_FOCUS_BORDER_MODE : focusBorderMode;
        }

        public boolean isControllerGuides() {
            return this.controllerGuides;
        }

        public void setControllerGuides(boolean controllerGuides) {
            this.controllerGuides = controllerGuides;
        }

        public boolean isResetButtonOverlay() {
            return this.resetButtonOverlay;
        }

        public void setResetButtonOverlay(boolean resetButtonOverlay) {
            this.resetButtonOverlay = resetButtonOverlay;
        }

        public boolean isUndoButtonOverlay() {
            return this.undoButtonOverlay;
        }

        public void setUndoButtonOverlay(boolean undoButtonOverlay) {
            this.undoButtonOverlay = undoButtonOverlay;
        }

        public boolean isAlwaysShowActionButtons() {
            return this.alwaysShowActionButtons;
        }

        public void setAlwaysShowActionButtons(boolean alwaysShowActionButtons) {
            this.alwaysShowActionButtons = alwaysShowActionButtons;
        }

        private ConfigData validate() {
            if (this.tabHeaderCollapseMode == null) {
                this.tabHeaderCollapseMode = DEFAULT_TAB_HEADER_COLLAPSE_MODE;
            }
            if (this.hideNonMatchingTabs == null) {
                this.hideNonMatchingTabs = this.hideNonMatchingOptions;
            }
            if (this.disabledOptionVisibility == null) {
                this.disabledOptionVisibility = DEFAULT_DISABLED_OPTION_VISIBILITY;
            }
            if (this.focusBorderMode == null) {
                this.focusBorderMode = DEFAULT_FOCUS_BORDER_MODE;
            }
            this.tooltipDelayMs = Math.clamp((long)this.tooltipDelayMs, (int)0, (int)5000);
            this.searchResultLimit = Math.clamp((long)this.searchResultLimit, (int)1, (int)50);
            return this;
        }
    }

    public static enum TabHeaderCollapseMode {
        SELECTED_GROUP("selected_group"),
        ALL_EXPANDED("all_expanded"),
        MANUAL("manual");

        private final String id;

        private TabHeaderCollapseMode(String id) {
            this.id = id;
        }

        public String id() {
            return this.id;
        }
    }

    public static enum DisabledOptionVisibility {
        SHOWN("shown"),
        HIDDEN("hidden");

        private final String id;

        private DisabledOptionVisibility(String id) {
            this.id = id;
        }

        public String id() {
            return this.id;
        }
    }

    public static enum FocusBorderMode {
        KEYBOARD("keyboard"),
        ALWAYS("always"),
        NEVER("never");

        private final String id;

        private FocusBorderMode(String id) {
            this.id = id;
        }

        public String id() {
            return this.id;
        }
    }
}

