/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.gui.screens.ConfirmLinkScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.element.IrisElementRow;
import net.irisshaders.iris.gui.element.IrisObjectSelectionList;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ShaderPackSelectionList
extends IrisObjectSelectionList<BaseEntry> {
    private static final Component PACK_LIST_LABEL = Component.translatable((String)"pack.iris.list.label").withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY});
    private static final ResourceLocation MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace((String)"textures/gui/menu_background.png");
    private final ShaderPackScreen screen;
    private final TopButtonRowEntry topButtonRow;
    private final WatchService watcher;
    private final WatchKey key;
    private final PinnedEntry downloadButton;
    private boolean keyValid;
    private ShaderPackEntry applied = null;

    public ShaderPackSelectionList(ShaderPackScreen screen, Minecraft client, int width, int height, int top, int bottom, int left, int right) {
        super(client, width, bottom, top + 4, bottom, left, right, 20);
        WatchKey key1;
        WatchService watcher1;
        this.screen = screen;
        this.topButtonRow = new TopButtonRowEntry(this, Iris.getIrisConfig().areShadersEnabled());
        this.downloadButton = new PinnedEntry((Component)Component.literal((String)"Download Shaders"), () -> this.minecraft.setScreen((Screen)new ConfirmLinkScreen(bl -> {
            if (bl) {
                Util.getPlatform().openUri("https://modrinth.com/shaders");
            }
            this.minecraft.setScreen((Screen)this.screen);
        }, "https://modrinth.com/shaders", true)), this);
        try {
            watcher1 = FileSystems.getDefault().newWatchService();
            key1 = Iris.getShaderpacksDirectory().register(watcher1, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            this.keyValid = true;
        }
        catch (IOException e) {
            Iris.logger.error("Couldn't register file watcher!", e);
            watcher1 = null;
            key1 = null;
            this.keyValid = false;
        }
        this.key = key1;
        this.watcher = watcher1;
        this.refresh();
    }

    public boolean keyPressed(int pContainerEventHandler0, int pInt1, int pInt2) {
        if (pContainerEventHandler0 == 265 && this.getFocused() == this.getFirstElement()) {
            return true;
        }
        return super.keyPressed(pContainerEventHandler0, pInt1, pInt2);
    }

    public void renderWidget(GuiGraphics pAbstractSelectionList0, int pInt1, int pInt2, float pFloat3) {
        if (this.keyValid) {
            for (WatchEvent<?> event : this.key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                this.refresh();
                break;
            }
            this.keyValid = this.key.reset();
        }
        super.renderWidget(pAbstractSelectionList0, pInt1, pInt2, pFloat3);
    }

    public void close() throws IOException {
        if (this.key != null) {
            this.key.cancel();
        }
        if (this.watcher != null) {
            this.watcher.close();
        }
    }

    protected void renderListBackground(GuiGraphics pAbstractSelectionList0) {
        if (this.screen.listTransition.getAsFloat() < 0.02f) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.screen.listTransition.getAsFloat());
        pAbstractSelectionList0.blit(MENU_LIST_BACKGROUND, this.getX(), this.getY() - 2, (float)this.getRight(), (float)(this.getBottom() + (int)this.getScrollAmount()), this.getWidth(), this.getHeight(), 32, 32);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    protected void renderListSeparators(GuiGraphics pAbstractSelectionList0) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.screen.listTransition.getAsFloat());
        pAbstractSelectionList0.blit(CreateWorldScreen.HEADER_SEPARATOR, this.getX(), this.getY() - 2, 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
        pAbstractSelectionList0.blit(CreateWorldScreen.FOOTER_SEPARATOR, this.getX(), this.getBottom(), 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableBlend();
    }

    public int getRowWidth() {
        return Math.min(308, this.width - 50);
    }

    protected int getRowTop(int index) {
        return super.getRowTop(index) + 2;
    }

    public void refresh() {
        List<String> names;
        this.clearEntries();
        try {
            names = Iris.getShaderpacksDirectoryManager().enumerate();
        }
        catch (Throwable e) {
            Iris.logger.error("Error reading files while constructing selection UI", e);
            this.addLabelEntries(new Component[]{Component.empty(), Component.literal((String)"There was an error reading your shaderpacks directory").withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}), Component.empty(), Component.literal((String)"Check your logs for more information."), Component.literal((String)"Please file an issue report including a log file."), Component.literal((String)"If you are able to identify the file causing this, please include it in your report as well."), Component.literal((String)"Note that this might be an issue with folder permissions; ensure those are correct first.")});
            return;
        }
        this.addEntry(this.topButtonRow);
        if (names.isEmpty()) {
            this.addEntry(this.downloadButton);
        }
        this.topButtonRow.allowEnableShadersButton = !names.isEmpty();
        int index = 0;
        String selectedName = Iris.getIrisConfig().getShaderPackName().orElse(null);
        ShaderPackEntry selectedPack = null;
        for (String name : names) {
            ShaderPackEntry entry = this.addPackEntry(++index, name);
            if (!name.equals(selectedName)) continue;
            selectedPack = entry;
        }
        if (selectedPack != null) {
            this.setSelected(selectedPack);
            this.setFocused((GuiEventListener)selectedPack);
            this.centerScrollOn(selectedPack);
            this.setApplied(selectedPack);
        }
        this.addLabelEntries(PACK_LIST_LABEL);
    }

    public ShaderPackEntry addPackEntry(int index, String name) {
        ShaderPackEntry entry = new ShaderPackEntry(index, this, name);
        this.addEntry(entry);
        return entry;
    }

    public void addLabelEntries(Component ... lines) {
        for (Component text : lines) {
            this.addEntry(new LabelEntry(text));
        }
    }

    public void select(String name) {
        for (int i = 0; i < this.getItemCount(); ++i) {
            BaseEntry entry = (BaseEntry)this.getEntry(i);
            if (!(entry instanceof ShaderPackEntry) || !((ShaderPackEntry)entry).packName.equals(name)) continue;
            this.setSelected(entry);
            return;
        }
    }

    public ShaderPackEntry getApplied() {
        return this.applied;
    }

    public void setApplied(ShaderPackEntry entry) {
        this.applied = entry;
    }

    public TopButtonRowEntry getTopButtonRow() {
        return this.topButtonRow;
    }

    public class ShaderPackEntry
    extends BaseEntry {
        private final String packName;
        private final ShaderPackSelectionList list;
        private final int index;
        private ScreenRectangle bounds = ScreenRectangle.empty();
        private boolean focused;

        public ShaderPackEntry(int index, ShaderPackSelectionList list, String packName) {
            this.packName = packName;
            this.list = list;
            this.index = index;
        }

        public ScreenRectangle getRectangle() {
            return this.bounds;
        }

        public boolean isApplied() {
            return this.list.getApplied() == this;
        }

        public boolean isSelected() {
            return this.list.getSelected() == this;
        }

        public String getPackName() {
            return this.packName;
        }

        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.bounds = new ScreenRectangle(x, y, entryWidth, entryHeight);
            Font font = Minecraft.getInstance().font;
            int color = 0xFFFFFF;
            Object name = this.packName;
            if (hovered) {
                GuiUtil.bindIrisWidgetsTexture();
                GuiUtil.drawButton(guiGraphics, x - 2, y - 2, entryWidth, entryHeight + 4, hovered, false);
            }
            boolean shadersEnabled = this.list.getTopButtonRow().shadersEnabled;
            if (font.width((FormattedText)Component.literal((String)name).withStyle(ChatFormatting.BOLD)) > this.list.getRowWidth() - 3) {
                name = font.plainSubstrByWidth((String)name, this.list.getRowWidth() - 8) + "...";
            }
            MutableComponent text = Component.literal((String)name);
            if (this.isMouseOver(mouseX, mouseY)) {
                text = text.withStyle(ChatFormatting.BOLD);
            }
            if (shadersEnabled && this.isApplied()) {
                color = 16773731;
            }
            if (!shadersEnabled && !this.isMouseOver(mouseX, mouseY)) {
                color = 0xA2A2A2;
            }
            guiGraphics.drawCenteredString(font, (Component)text, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, color);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            return this.doThing();
        }

        public boolean keyPressed(int keycode, int pInt1, int pInt2) {
            if (keycode != 257) {
                return false;
            }
            return this.doThing();
        }

        private boolean doThing() {
            boolean didAnything = false;
            if (!this.list.getTopButtonRow().shadersEnabled) {
                this.list.getTopButtonRow().setShadersEnabled(true);
                didAnything = true;
            }
            if (!this.isSelected()) {
                this.list.select(this.index);
                didAnything = true;
            }
            ShaderPackSelectionList.this.screen.setFocused((GuiEventListener)ShaderPackSelectionList.this.screen.getBottomRowOption());
            return didAnything;
        }

        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent pGuiEventListener0) {
            return !this.isFocused() ? ComponentPath.leaf((GuiEventListener)this) : null;
        }

        public boolean isFocused() {
            return this.list.getFocused() == this;
        }
    }

    public static class TopButtonRowEntry
    extends BaseEntry {
        private static final Component NONE_PRESENT_LABEL = Component.translatable((String)"options.iris.shaders.nonePresent").withStyle(ChatFormatting.GRAY);
        private static final Component SHADERS_DISABLED_LABEL = Component.translatable((String)"options.iris.shaders.disabled");
        private static final Component SHADERS_ENABLED_LABEL = Component.translatable((String)"options.iris.shaders.enabled");
        private final ShaderPackSelectionList list;
        public boolean allowEnableShadersButton = true;
        public boolean shadersEnabled;

        public TopButtonRowEntry(ShaderPackSelectionList list, boolean shadersEnabled) {
            this.list = list;
            this.shadersEnabled = shadersEnabled;
        }

        public void setShadersEnabled(boolean shadersEnabled) {
            this.shadersEnabled = shadersEnabled;
            this.list.screen.refreshScreenSwitchButton();
        }

        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            GuiUtil.bindIrisWidgetsTexture();
            GuiUtil.drawButton(guiGraphics, x - 2, y - 2, entryWidth, entryHeight + 2, hovered, !this.allowEnableShadersButton);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getEnableDisableLabel(), x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xFFFFFF);
        }

        private Component getEnableDisableLabel() {
            return this.allowEnableShadersButton ? (this.shadersEnabled ? SHADERS_ENABLED_LABEL : SHADERS_DISABLED_LABEL) : NONE_PRESENT_LABEL;
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.allowEnableShadersButton) {
                this.setShadersEnabled(!this.shadersEnabled);
                GuiUtil.playButtonClickSound();
                return true;
            }
            return false;
        }

        public boolean keyPressed(int keycode, int scancode, int modifiers) {
            if (keycode == 257 && this.allowEnableShadersButton) {
                this.setShadersEnabled(!this.shadersEnabled);
                GuiUtil.playButtonClickSound();
                return true;
            }
            return false;
        }

        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent pGuiEventListener0) {
            return !this.isFocused() ? ComponentPath.leaf((GuiEventListener)this) : null;
        }

        public boolean isFocused() {
            return this.list.getFocused() == this;
        }

        public static class EnableShadersButtonElement
        extends IrisElementRow.TextButtonElement {
            private int centerX;

            public EnableShadersButtonElement(Component text, Function<IrisElementRow.TextButtonElement, Boolean> onClick) {
                super(text, onClick);
            }

            @Override
            public void renderLabel(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta, boolean hovered) {
                int textX = this.centerX - (int)((double)this.font.width((FormattedText)this.text) * 0.5);
                int textY = y + (int)((double)(height - 8) * 0.5);
                guiGraphics.drawString(this.font, this.text, textX, textY, 0xFFFFFF);
            }
        }
    }

    private static class PinnedEntry
    extends BaseEntry {
        public final boolean allowPressButton = true;
        private final Component label;
        private final Runnable onClick;

        public PinnedEntry(Component label, Runnable onClick, ShaderPackSelectionList list) {
            this.label = label;
            this.onClick = onClick;
        }

        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            GuiUtil.bindIrisWidgetsTexture();
            GuiUtil.drawButton(guiGraphics, x - 2, y - 2, entryWidth, entryHeight + 2, hovered, false);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xFFFFFF);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Objects.requireNonNull(this);
            GuiUtil.playButtonClickSound();
            this.onClick.run();
            return false;
        }

        public boolean keyPressed(int keycode, int scancode, int modifiers) {
            if (keycode == 257) {
                Objects.requireNonNull(this);
                GuiUtil.playButtonClickSound();
                this.onClick.run();
                return false;
            }
            return false;
        }
    }

    public static class LabelEntry
    extends BaseEntry {
        private final Component label;

        public LabelEntry(Component label) {
            this.label = label;
        }

        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xC2C2C2);
        }
    }

    public static abstract class BaseEntry
    extends AbstractSelectionList.Entry<BaseEntry> {
        protected BaseEntry() {
        }
    }
}

