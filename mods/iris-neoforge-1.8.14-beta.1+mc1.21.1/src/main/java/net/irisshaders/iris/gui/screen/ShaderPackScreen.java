/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.Util$OS
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ConfirmLinkScreen
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.PostChain
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.FormattedCharSequence
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.OldImageButton;
import net.irisshaders.iris.gui.element.ShaderPackOptionList;
import net.irisshaders.iris.gui.element.ShaderPackSelectionList;
import net.irisshaders.iris.gui.element.screen.IrisButton;
import net.irisshaders.iris.gui.element.widget.AbstractElementWidget;
import net.irisshaders.iris.gui.element.widget.CommentedElementWidget;
import net.irisshaders.iris.gui.screen.HudHideable;
import net.irisshaders.iris.mixin.GameRendererAccessor;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.transforms.SmoothedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public class ShaderPackScreen
extends Screen
implements HudHideable {
    public static final Set<Runnable> TOP_LAYER_RENDER_QUEUE = new HashSet<Runnable>();
    private static final Component SELECT_TITLE = Component.translatable((String)"pack.iris.select.title").withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
    private static final Component CONFIGURE_TITLE = Component.translatable((String)"pack.iris.configure.title").withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
    private static final int COMMENT_PANEL_WIDTH = 314;
    private static final String development = "Development Environment";
    private final Screen parent;
    private final MutableComponent irisTextComponent;
    private final FrameUpdateNotifier notifier = new FrameUpdateNotifier();
    private ShaderPackSelectionList shaderPackList;
    @Nullable
    private ShaderPackOptionList shaderOptionList = null;
    @Nullable
    private NavigationController navigation = null;
    private Button screenSwitchButton;
    private Component notificationDialog = null;
    private int notificationDialogTimer = 0;
    @Nullable
    private AbstractElementWidget<?> hoveredElement = null;
    private Optional<Component> hoveredElementCommentTitle = Optional.empty();
    private List<FormattedCharSequence> hoveredElementCommentBody = new ArrayList<FormattedCharSequence>();
    private int hoveredElementCommentTimer = 0;
    private boolean optionMenuOpen = false;
    private boolean dropChanges = false;
    private MutableComponent developmentComponent;
    private MutableComponent updateComponent;
    private boolean guiHidden = false;
    public final SmoothedFloat blurTransition = new SmoothedFloat(2.0f, 2.0f, () -> {
        if (this.guiHidden) {
            return 0.0f;
        }
        if (this.optionMenuOpen) {
            return 0.1f;
        }
        return this.minecraft.options.getMenuBackgroundBlurriness();
    }, this.notifier);
    private float guiButtonHoverTimer = 0.0f;
    private Button openFolderButton;
    private float backgroundInit = 0.0f;
    public final SmoothedFloat listTransition = new SmoothedFloat(1.0f, 1.0f, () -> {
        if (this.guiHidden || this.optionMenuOpen) {
            return 0.0f;
        }
        return this.backgroundInit;
    }, this.notifier);
    public final SmoothedFloat buttonTransition = new SmoothedFloat(1.0f, 1.0f, () -> {
        if (this.guiHidden) {
            return 0.0f;
        }
        return this.backgroundInit;
    }, this.notifier);
    private OldImageButton showHideButton;

    public ShaderPackScreen(Screen parent) {
        super((Component)Component.translatable((String)"options.iris.shaderPackSelection.title"));
        this.parent = parent;
        String irisName = "Iris " + Iris.getVersion();
        if (IrisPlatformHelpers.getInstance().isDevelopmentEnvironment()) {
            this.developmentComponent = Component.literal((String)development).withStyle(ChatFormatting.GOLD);
        }
        this.irisTextComponent = Component.literal((String)irisName).withStyle(ChatFormatting.GRAY);
        if (Iris.getUpdateChecker().getUpdateMessage().isPresent()) {
            this.updateComponent = Component.literal((String)"New update available!").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.UNDERLINE);
            this.irisTextComponent.append((Component)Component.literal((String)" (outdated)").withStyle(ChatFormatting.RED));
        }
        this.refreshForChangedPack();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.notifier.onNewFrame();
        this.backgroundInit = 1.0f;
        if (Screen.hasControlDown() && InputConstants.isKeyDown((long)Minecraft.getInstance().getWindow().getWindow(), (int)68)) {
            Minecraft.getInstance().setScreen((Screen)new ConfirmScreen(option -> {
                Iris.setDebug(option);
                Minecraft.getInstance().setScreen((Screen)this);
            }, (Component)Component.literal((String)"Shader debug mode toggle"), (Component)Component.literal((String)"Debug mode helps investigate problems and shows shader errors. Would you like to enable it?"), (Component)Component.literal((String)"Yes"), (Component)Component.literal((String)"No")));
        }
        if (Screen.hasControlDown() && InputConstants.isKeyDown((long)Minecraft.getInstance().getWindow().getWindow(), (int)71)) {
            Minecraft.getInstance().setScreen((Screen)new ConfirmScreen(option -> {
                try {
                    Iris.getIrisConfig().setUnknown(option);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Minecraft.getInstance().setScreen((Screen)this);
            }, (Component)Component.literal((String)"Unknown shader toggle"), (Component)Component.literal((String)"This allows unknown shaders to load in."), (Component)Component.literal((String)"Enable"), (Component)Component.literal((String)"Disable")));
        }
        if (!this.guiHidden) {
            super.render(guiGraphics, mouseX, mouseY, delta);
        } else {
            this.renderBlurredBackground(delta);
            this.showHideButton.render(guiGraphics, mouseX, mouseY, delta);
        }
        float previousHoverTimer = this.guiButtonHoverTimer;
        if (previousHoverTimer == this.guiButtonHoverTimer) {
            this.guiButtonHoverTimer = 0.0f;
        }
        if (!this.guiHidden) {
            guiGraphics.drawCenteredString(this.font, this.title, (int)((double)this.width * 0.5), 8, 0xFFFFFF);
            if (this.notificationDialog != null && this.notificationDialogTimer > 0) {
                guiGraphics.drawCenteredString(this.font, this.notificationDialog, (int)((double)this.width * 0.5), 21, 0xFFFFFF);
            } else if (this.optionMenuOpen) {
                guiGraphics.drawCenteredString(this.font, CONFIGURE_TITLE, (int)((double)this.width * 0.5), 21, 0xFFFFFF);
            } else {
                guiGraphics.drawCenteredString(this.font, SELECT_TITLE, (int)((double)this.width * 0.5), 21, 0xFFFFFF);
            }
            if (this.isDisplayingComment()) {
                int panelHeight = Math.max(50, 18 + this.hoveredElementCommentBody.size() * 10);
                int x = (int)(0.5 * (double)this.width) - 157;
                int y = this.height - (panelHeight + 4);
                GuiUtil.drawPanel(guiGraphics, x, y, 314, panelHeight);
                guiGraphics.drawString(this.font, this.hoveredElementCommentTitle.orElse((Component)Component.empty()), x + 4, y + 4, 0xFFFFFF);
                for (int i = 0; i < this.hoveredElementCommentBody.size(); ++i) {
                    guiGraphics.drawString(this.font, this.hoveredElementCommentBody.get(i), x + 4, y + 16 + i * 10, 0xFFFFFF);
                }
            }
        }
        for (Runnable render : TOP_LAYER_RENDER_QUEUE) {
            render.run();
        }
        TOP_LAYER_RENDER_QUEUE.clear();
        if (this.developmentComponent != null) {
            guiGraphics.drawString(this.font, (Component)this.developmentComponent, 2, this.height - 10, 0xFFFFFF);
            guiGraphics.drawString(this.font, (Component)this.irisTextComponent, 2, this.height - 20, 0xFFFFFF);
        } else if (this.updateComponent != null) {
            guiGraphics.drawString(this.font, (Component)this.updateComponent, 2, this.height - 10, 0xFFFFFF);
            guiGraphics.drawString(this.font, (Component)this.irisTextComponent, 2, this.height - 20, 0xFFFFFF);
        } else {
            guiGraphics.drawString(this.font, (Component)this.irisTextComponent, 2, this.height - 10, 0xFFFFFF);
        }
    }

    public boolean mouseClicked(double d, double e, int i) {
        int widthValue = this.font.width("New update available!");
        if (this.updateComponent != null && d < (double)widthValue && e > (double)(this.height - 10) && e < (double)this.height) {
            this.minecraft.setScreen((Screen)new ConfirmLinkScreen(bl -> {
                if (bl) {
                    Iris.getUpdateChecker().getUpdateLink().ifPresent(arg_0 -> ((Util.OS)Util.getPlatform()).openUri(arg_0));
                }
                this.minecraft.setScreen((Screen)this);
            }, Iris.getUpdateChecker().getUpdateLink().orElse(""), true));
        }
        return super.mouseClicked(d, e, i);
    }

    protected void init() {
        super.init();
        int bottomCenter = this.width / 2 - 50;
        int topCenter = this.width / 2 - 76;
        boolean inWorld = this.minecraft.level != null;
        this.removeWidget((GuiEventListener)this.shaderPackList);
        this.removeWidget((GuiEventListener)this.shaderOptionList);
        this.shaderPackList = new ShaderPackSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 58 - 32, 0, this.width);
        if (Iris.getCurrentPack().isPresent() && this.navigation != null) {
            ShaderPack currentPack = Iris.getCurrentPack().get();
            this.shaderOptionList = new ShaderPackOptionList(this, this.navigation, currentPack, this.minecraft, this.width, this.height, 32, this.height - 58 - 32, 0, this.width);
            this.navigation.setActiveOptionList(this.shaderOptionList);
            this.shaderOptionList.rebuild();
        } else {
            this.optionMenuOpen = false;
            this.shaderOptionList = null;
        }
        this.clearWidgets();
        if (!this.guiHidden) {
            if (this.optionMenuOpen && this.shaderOptionList != null) {
                this.addRenderableWidget((GuiEventListener)this.shaderOptionList);
            } else {
                this.addRenderableWidget((GuiEventListener)this.shaderPackList);
            }
            this.addRenderableWidget((GuiEventListener)IrisButton.iris$builder(CommonComponents.GUI_DONE, button -> this.onClose(), this.buttonTransition).bounds(bottomCenter + 104, this.height - 27, 100, 20).build());
            this.addRenderableWidget((GuiEventListener)IrisButton.iris$builder((Component)Component.translatable((String)"options.iris.apply"), button -> this.applyChanges(), this.buttonTransition).bounds(bottomCenter, this.height - 27, 100, 20).build());
            this.addRenderableWidget((GuiEventListener)IrisButton.iris$builder(CommonComponents.GUI_CANCEL, button -> this.dropChangesAndClose(), this.buttonTransition).bounds(bottomCenter - 104, this.height - 27, 100, 20).build());
            this.openFolderButton = IrisButton.iris$builder((Component)Component.translatable((String)"options.iris.openShaderPackFolder"), button -> this.openShaderPackFolder(), this.buttonTransition).bounds(topCenter - 78, this.height - 51, 152, 20).build();
            this.addRenderableWidget((GuiEventListener)this.openFolderButton);
            this.screenSwitchButton = (Button)this.addRenderableWidget((GuiEventListener)IrisButton.iris$builder((Component)Component.translatable((String)"options.iris.shaderPackList"), button -> {
                this.optionMenuOpen = !this.optionMenuOpen;
                this.applyChanges();
                this.setFocused((GuiEventListener)this.shaderPackList.getFocused());
                this.init();
            }, this.buttonTransition).bounds(topCenter + 78, this.height - 51, 152, 20).build());
            this.refreshScreenSwitchButton();
        }
        if (inWorld) {
            MutableComponent showOrHide = this.guiHidden ? Component.translatable((String)"options.iris.gui.show") : Component.translatable((String)"options.iris.gui.hide");
            float endOfLastButton = (float)this.width / 2.0f + 154.0f;
            float freeSpace = (float)this.width - endOfLastButton;
            int x = freeSpace > 100.0f ? this.width - 50 : (freeSpace < 20.0f ? this.width - 20 : (int)(endOfLastButton + freeSpace / 2.0f) - 10);
            this.showHideButton = new OldImageButton(x, this.height - 39, 20, 20, this.guiHidden ? 20 : 0, 146, 20, GuiUtil.IRIS_WIDGETS_TEX, 256, 256, button -> {
                this.guiHidden = !this.guiHidden;
                this.init();
            }, (Component)showOrHide);
            this.showHideButton.setTooltip(Tooltip.create((Component)showOrHide));
            this.showHideButton.setTooltipDelay(Duration.ofSeconds(10L));
            this.addRenderableWidget((GuiEventListener)this.showHideButton);
        }
        this.hoveredElement = null;
        this.hoveredElementCommentTimer = 0;
    }

    public void refreshForChangedPack() {
        if (Iris.getCurrentPack().isPresent()) {
            ShaderPack currentPack = Iris.getCurrentPack().get();
            this.navigation = new NavigationController(currentPack.getMenuContainer());
            if (this.shaderOptionList != null) {
                this.shaderOptionList.applyShaderPack(currentPack);
                this.shaderOptionList.rebuild();
            }
        } else {
            this.navigation = null;
        }
        this.refreshScreenSwitchButton();
    }

    public void refreshScreenSwitchButton() {
        if (this.screenSwitchButton != null) {
            this.screenSwitchButton.setMessage((Component)(this.optionMenuOpen ? Component.translatable((String)"options.iris.shaderPackList") : Component.translatable((String)"options.iris.shaderPackSettings")));
            this.screenSwitchButton.active = this.optionMenuOpen || this.shaderPackList.getTopButtonRow().shadersEnabled;
        }
    }

    private void processFixedBlur(float tick) {
        PostChain blurEffect = ((GameRendererAccessor)this.minecraft.gameRenderer).getBlurEffect();
        float g = Math.min((float)this.minecraft.options.getMenuBackgroundBlurriness(), this.blurTransition.getAsFloat());
        if (blurEffect != null && g >= 1.0f) {
            blurEffect.setUniform("Radius", g);
            blurEffect.process(tick);
        }
    }

    protected void renderBlurredBackground(float pScreen0) {
        RenderSystem.disableDepthTest();
        this.processFixedBlur(pScreen0);
        this.minecraft.getMainRenderTarget().bindWrite(false);
    }

    public void tick() {
        super.tick();
        if (this.notificationDialogTimer > 0) {
            --this.notificationDialogTimer;
        }
        this.hoveredElementCommentTimer = this.hoveredElement != null ? ++this.hoveredElementCommentTimer : 0;
    }

    public boolean keyPressed(int key, int j, int k) {
        if (key == 256) {
            if (this.guiHidden) {
                this.guiHidden = false;
                this.init();
                return true;
            }
            if (this.navigation != null && this.navigation.hasHistory()) {
                this.navigation.back();
                return true;
            }
            if (this.optionMenuOpen) {
                this.optionMenuOpen = false;
                this.init();
                return true;
            }
        } else if (key == 258) {
            if (!this.optionMenuOpen) {
                this.shaderPackList.keyPressed(257, 0, 0);
            }
            this.optionMenuOpen = !this.optionMenuOpen;
            this.applyChanges();
            this.init();
            this.setFocused(null);
        } else if (key == 290 && this.showHideButton != null) {
            this.guiHidden = !this.guiHidden;
            this.init();
        }
        return this.guiHidden || super.keyPressed(key, j, k);
    }

    public void onFilesDrop(List<Path> paths) {
        if (this.optionMenuOpen) {
            this.onOptionMenuFilesDrop(paths);
        } else {
            this.onPackListFilesDrop(paths);
        }
    }

    public void onPackListFilesDrop(List<Path> paths) {
        List<Path> packs = paths.stream().filter(Iris::isValidShaderpack).toList();
        for (Path pack : packs) {
            String fileName = pack.getFileName().toString();
            try {
                Iris.getShaderpacksDirectoryManager().copyPackIntoDirectory(fileName, pack);
            }
            catch (FileAlreadyExistsException e) {
                this.notificationDialog = Component.translatable((String)"options.iris.shaderPackSelection.copyErrorAlreadyExists", (Object[])new Object[]{fileName}).withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
                this.notificationDialogTimer = 100;
                this.shaderPackList.refresh();
                return;
            }
            catch (IOException e) {
                Iris.logger.warn("Error copying dragged shader pack", e);
                this.notificationDialog = Component.translatable((String)"options.iris.shaderPackSelection.copyError", (Object[])new Object[]{fileName}).withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
                this.notificationDialogTimer = 100;
                this.shaderPackList.refresh();
                return;
            }
        }
        this.shaderPackList.refresh();
        if (packs.isEmpty()) {
            if (paths.size() == 1) {
                String fileName = ((Path)paths.getFirst()).getFileName().toString();
                this.notificationDialog = Component.translatable((String)"options.iris.shaderPackSelection.failedAddSingle", (Object[])new Object[]{fileName}).withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            } else {
                this.notificationDialog = Component.translatable((String)"options.iris.shaderPackSelection.failedAdd").withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            }
        } else if (packs.size() == 1) {
            String packName = ((Path)packs.getFirst()).getFileName().toString();
            this.notificationDialog = Component.translatable((String)"options.iris.shaderPackSelection.addedPack", (Object[])new Object[]{packName}).withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.YELLOW});
            this.shaderPackList.select(packName);
        } else {
            this.notificationDialog = Component.translatable((String)"options.iris.shaderPackSelection.addedPacks", (Object[])new Object[]{packs.size()}).withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.YELLOW});
        }
        this.notificationDialogTimer = 100;
    }

    public void displayNotification(Component component) {
        this.notificationDialog = component;
        this.notificationDialogTimer = 100;
    }

    public void onOptionMenuFilesDrop(List<Path> paths) {
        if (paths.size() != 1) {
            this.notificationDialog = Component.translatable((String)"options.iris.shaderPackOptions.tooManyFiles").withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            this.notificationDialogTimer = 100;
            return;
        }
        this.importPackOptions((Path)paths.getFirst());
    }

    public void importPackOptions(Path settingFile) {
        try (InputStream in = Files.newInputStream(settingFile, new OpenOption[0]);){
            Properties properties = new Properties();
            properties.load(in);
            Iris.queueShaderPackOptionsFromProperties(properties);
            this.notificationDialog = Component.translatable((String)"options.iris.shaderPackOptions.importedSettings", (Object[])new Object[]{settingFile.getFileName().toString()}).withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.YELLOW});
            this.notificationDialogTimer = 100;
            if (this.navigation != null) {
                this.navigation.refresh();
            }
        }
        catch (Exception e) {
            Iris.logger.error("Error importing shader settings file \"" + settingFile.toString() + "\"", e);
            this.notificationDialog = Component.translatable((String)"options.iris.shaderPackOptions.failedImport", (Object[])new Object[]{settingFile.getFileName().toString()}).withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.RED});
            this.notificationDialogTimer = 100;
        }
    }

    public void onClose() {
        if (!this.dropChanges) {
            this.applyChanges();
        } else {
            this.discardChanges();
        }
        try {
            this.shaderPackList.close();
        }
        catch (IOException e) {
            Iris.logger.error("Failed to safely close shaderpack selection!", e);
        }
        this.minecraft.setScreen(this.parent);
    }

    private void dropChangesAndClose() {
        this.dropChanges = true;
        this.onClose();
    }

    public void applyChanges() {
        String previousPackName;
        ShaderPackSelectionList.BaseEntry base = (ShaderPackSelectionList.BaseEntry)this.shaderPackList.getSelected();
        boolean enabled = this.shaderPackList.getTopButtonRow().shadersEnabled;
        boolean previousShadersEnabled = Iris.getIrisConfig().areShadersEnabled();
        if (enabled != previousShadersEnabled) {
            IrisApi.getInstance().getConfig().setShadersEnabledAndApply(enabled);
        }
        if (!(base instanceof ShaderPackSelectionList.ShaderPackEntry)) {
            return;
        }
        ShaderPackSelectionList.ShaderPackEntry entry = (ShaderPackSelectionList.ShaderPackEntry)base;
        this.shaderPackList.setApplied(entry);
        String name = entry.getPackName();
        if (!name.equals(Iris.getCurrentPackName())) {
            Iris.clearShaderPackOptionQueue();
        }
        if (!name.equals(previousPackName = (String)Iris.getIrisConfig().getShaderPackName().orElse(null)) || !Iris.getShaderPackOptionQueue().isEmpty() || Iris.shouldResetShaderPackOptionsOnNextReload()) {
            Iris.getIrisConfig().setShaderPackName(name);
            IrisApi.getInstance().getConfig().setShadersEnabledAndApply(enabled);
        }
        this.refreshForChangedPack();
    }

    private void discardChanges() {
        Iris.clearShaderPackOptionQueue();
    }

    private void openShaderPackFolder() {
        CompletableFuture.runAsync(() -> Util.getPlatform().openUri(Iris.getShaderpacksDirectoryManager().getDirectoryUri()));
    }

    public void setElementHoveredStatus(AbstractElementWidget<?> widget, boolean hovered) {
        if (hovered && widget != this.hoveredElement) {
            this.hoveredElement = widget;
            if (widget instanceof CommentedElementWidget) {
                this.hoveredElementCommentTitle = ((CommentedElementWidget)widget).getCommentTitle();
                Optional<Component> commentBody = ((CommentedElementWidget)widget).getCommentBody();
                if (commentBody.isEmpty()) {
                    this.hoveredElementCommentBody.clear();
                } else {
                    String rawCommentBody = commentBody.get().getString();
                    if (rawCommentBody.endsWith(".")) {
                        rawCommentBody = rawCommentBody.substring(0, rawCommentBody.length() - 1);
                    }
                    List<MutableComponent> splitByPeriods = Arrays.stream(rawCommentBody.split("\\. [ ]*")).map(Component::literal).toList();
                    this.hoveredElementCommentBody = new ArrayList<FormattedCharSequence>();
                    for (MutableComponent text : splitByPeriods) {
                        this.hoveredElementCommentBody.addAll(this.font.split((FormattedText)text, 306));
                    }
                }
            } else {
                this.hoveredElementCommentTitle = Optional.empty();
                this.hoveredElementCommentBody.clear();
            }
            this.hoveredElementCommentTimer = 0;
        } else if (!hovered && widget == this.hoveredElement) {
            this.hoveredElement = null;
            this.hoveredElementCommentTitle = Optional.empty();
            this.hoveredElementCommentBody.clear();
            this.hoveredElementCommentTimer = 0;
        }
    }

    public boolean isDisplayingComment() {
        return this.hoveredElementCommentTimer > 20 && this.hoveredElementCommentTitle.isPresent() && !this.hoveredElementCommentBody.isEmpty();
    }

    public Button getBottomRowOption() {
        return this.openFolderButton;
    }
}

