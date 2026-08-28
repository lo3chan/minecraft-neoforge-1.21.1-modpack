/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.isxander.controlify.Controlify
 *  dev.isxander.controlify.api.bind.InputBinding
 *  dev.isxander.controlify.api.bind.InputBindingSupplier
 *  dev.isxander.controlify.bindings.ControlifyBindings
 *  dev.isxander.controlify.controller.ControllerEntity
 *  dev.isxander.controlify.screenop.ScreenProcessor
 *  dev.isxander.controlify.virtualmouse.VirtualMouseHandler
 *  java.lang.MatchException
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.ContainerEventHandler
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.controlify;

import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.bindings.ControlifyBindings;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuideProvider;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.TabFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.FlatButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

final class RsoOptionsScreenProcessor
extends ScreenProcessor<SodiumVideoOptionsScreen> {
    private static final int FOOTER_GUIDE_RIGHT_GAP = 8;

    RsoOptionsScreenProcessor(SodiumVideoOptionsScreen screen) {
        super((Screen)screen);
    }

    protected void handleButtons(ControllerEntity controller) {
        boolean promptOpen;
        String previousTabKey = ((SodiumVideoOptionsScreen)this.screen).rso$getSelectedTabKey();
        boolean handled = false;
        boolean bl = promptOpen = ((SodiumVideoOptionsScreen)this.screen).getPrompt() != null;
        if (!promptOpen) {
            if (ControlifyBindings.GUI_ABSTRACT_ACTION_1.on(controller).justPressed()) {
                boolean bl2 = handled = RsoOptionsScreenProcessor.press(((SodiumVideoOptionsScreen)this.screen).rso$getApplyButton()) || handled;
            }
            if (ControlifyBindings.GUI_ABSTRACT_ACTION_2.on(controller).justPressed()) {
                boolean bl3 = handled = RsoOptionsScreenProcessor.press(((SodiumVideoOptionsScreen)this.screen).rso$getUndoButton()) || handled;
            }
            if (ControlifyBindings.GUI_NEXT_TAB.on(controller).justPressed()) {
                boolean bl4 = handled = ((SodiumVideoOptionsScreen)this.screen).rso$cycleTab(1) || handled;
            }
            if (ControlifyBindings.GUI_PREV_TAB.on(controller).justPressed()) {
                boolean bl5 = handled = ((SodiumVideoOptionsScreen)this.screen).rso$cycleTab(-1) || handled;
            }
        }
        if (ControlifyBindings.GUI_PRESS.on(controller).guiPressed().get()) {
            GuiEventListener focusedLeaf = ((SodiumVideoOptionsScreen)this.screen).rso$getFocusedLeaf();
            boolean bl6 = handled = this.tryOpenKeyboard(controller, focusedLeaf) || ((SodiumVideoOptionsScreen)this.screen).rso$handleControllerPress() || handled;
        }
        if (ControlifyBindings.GUI_BACK.on(controller).guiPressed().get()) {
            boolean bl7 = handled = ((SodiumVideoOptionsScreen)this.screen).rso$handleControllerBack() || handled;
        }
        if (handled) {
            ((SodiumVideoOptionsScreen)this.screen).rso$afterControllerInput(previousTabKey);
        }
    }

    @Nullable
    protected Supplier<Boolean> createScreenNavigationFunc(ScreenDirection direction) {
        return () -> ((SodiumVideoOptionsScreen)this.screen).rso$navigateController(direction);
    }

    protected Queue<GuiEventListener> getFocusTree() {
        ArrayDeque<GuiEventListener> tree = new ArrayDeque<GuiEventListener>();
        GuiEventListener focused = ((SodiumVideoOptionsScreen)this.screen).getFocused();
        while (focused != null) {
            tree.addFirst(focused);
            if (focused instanceof ContainerEventHandler) {
                ContainerEventHandler container = (ContainerEventHandler)focused;
                focused = container.getFocused();
                continue;
            }
            focused = null;
        }
        return tree;
    }

    public void onWidgetRebuild() {
        super.onWidgetRebuild();
        this.decorateButton(((SodiumVideoOptionsScreen)this.screen).rso$getApplyButton(), ControlifyBindings.GUI_ABSTRACT_ACTION_1);
        this.decorateButton(((SodiumVideoOptionsScreen)this.screen).rso$getUndoButton(), ControlifyBindings.GUI_ABSTRACT_ACTION_2);
        this.decorateButton(((SodiumVideoOptionsScreen)this.screen).rso$getCloseButton(), ControlifyBindings.GUI_BACK);
        if (Controlify.instance().currentInputMode().isController()) {
            ((SodiumVideoOptionsScreen)this.screen).rso$focusFirstOptionInSelectedTab();
        }
    }

    protected void render(ControllerEntity controller, GuiGraphics graphics, float tickDelta, Optional<VirtualMouseHandler> vmouse) {
        if (((SodiumVideoOptionsScreen)this.screen).getPrompt() != null || !ReeseSodiumOptionsConfig.config().isControllerGuides() || !RsoOptionsScreenProcessor.shouldShowGuides(controller)) {
            return;
        }
        TabFrame tabFrame = ((SodiumVideoOptionsScreen)this.screen).rso$getTabFrame();
        if (tabFrame == null) {
            return;
        }
        FlatButtonWidget closeButton = ((SodiumVideoOptionsScreen)this.screen).rso$getCloseButton();
        if (closeButton == null) {
            return;
        }
        int x = tabFrame.getX();
        int availableWidth = this.footerButtonLimitX() - x;
        if (availableWidth <= 0) {
            return;
        }
        Optional<Component> hint = this.footerHint(controller, availableWidth);
        if (hint.isEmpty()) {
            return;
        }
        int n = closeButton.getCenterY();
        Objects.requireNonNull(RsoOptionsScreenProcessor.minecraft.font);
        int y = n - 9 / 2;
        graphics.drawString(RsoOptionsScreenProcessor.minecraft.font, hint.get(), x, y, -1);
    }

    private Optional<Component> footerHint(ControllerEntity controller, int availableWidth) {
        ArrayList<Component> guides = new ArrayList<Component>();
        GuiEventListener focusedLeaf = ((SodiumVideoOptionsScreen)this.screen).rso$getFocusedLeaf();
        if (focusedLeaf instanceof ControlGuideProvider) {
            ControlGuideProvider guideProvider = (ControlGuideProvider)focusedLeaf;
            guideProvider.controlGuides().stream().map(guide -> this.guideComponent(controller, (ControlGuide)guide)).flatMap(Optional::stream).forEach(guides::add);
        }
        if (((SodiumVideoOptionsScreen)this.screen).rso$getTabFrame() != null && ((SodiumVideoOptionsScreen)this.screen).rso$getTabFrame().getTabs().size() > 1) {
            this.guideComponent(controller, ControlGuide.previousTab((Component)Component.translatable((String)"rso.controller.guide.previous_tab"))).ifPresent(guides::add);
            this.guideComponent(controller, ControlGuide.nextTab((Component)Component.translatable((String)"rso.controller.guide.next_tab"))).ifPresent(guides::add);
        }
        return this.fitGuides(guides, availableWidth);
    }

    private Optional<Component> guideComponent(ControllerEntity controller, ControlGuide guide) {
        return switch (guide.input()) {
            default -> throw new MatchException(null, null);
            case ControlGuide.Input.PRESS -> this.bindingGuide(controller, ControlifyBindings.GUI_PRESS, guide.label());
            case ControlGuide.Input.PREVIOUS_TAB -> this.bindingGuide(controller, ControlifyBindings.GUI_PREV_TAB, guide.label());
            case ControlGuide.Input.NEXT_TAB -> this.bindingGuide(controller, ControlifyBindings.GUI_NEXT_TAB, guide.label());
            case ControlGuide.Input.NAVIGATION_LEFT_RIGHT -> this.navigationLeftRightGuide(controller, guide.label());
        };
    }

    private Optional<Component> bindingGuide(ControllerEntity controller, InputBindingSupplier supplier, Component label) {
        return RsoOptionsScreenProcessor.activeBinding(controller, supplier).map(binding -> Component.empty().append(binding.inputGlyph()).append(CommonComponents.SPACE).append(label));
    }

    private Optional<Component> navigationLeftRightGuide(ControllerEntity controller, Component label) {
        Optional<InputBinding> left = RsoOptionsScreenProcessor.activeBinding(controller, ControlifyBindings.GUI_NAVI_LEFT);
        Optional<InputBinding> right = RsoOptionsScreenProcessor.activeBinding(controller, ControlifyBindings.GUI_NAVI_RIGHT);
        if (left.isEmpty() || right.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Component.empty().append(left.get().inputGlyph()).append((Component)Component.literal((String)"/")).append(right.get().inputGlyph()).append(CommonComponents.SPACE).append(label));
    }

    private Optional<Component> fitGuides(List<Component> guides, int availableWidth) {
        for (int count = guides.size(); count > 0; --count) {
            Component hint = RsoOptionsScreenProcessor.joinGuides(guides.subList(0, count));
            if (RsoOptionsScreenProcessor.minecraft.font.width((FormattedText)hint) > availableWidth) continue;
            return Optional.of(hint);
        }
        return Optional.empty();
    }

    private static Component joinGuides(List<Component> guides) {
        MutableComponent hint = Component.empty();
        for (int i = 0; i < guides.size(); ++i) {
            if (i > 0) {
                hint.append(CommonComponents.SPACE).append(CommonComponents.SPACE).append(CommonComponents.SPACE);
            }
            hint.append(guides.get(i));
        }
        return hint;
    }

    private int footerButtonLimitX() {
        int limit = Integer.MAX_VALUE;
        limit = Math.min(limit, RsoOptionsScreenProcessor.buttonX(((SodiumVideoOptionsScreen)this.screen).rso$getUndoButton()));
        limit = Math.min(limit, RsoOptionsScreenProcessor.buttonX(((SodiumVideoOptionsScreen)this.screen).rso$getApplyButton()));
        return (limit = Math.min(limit, RsoOptionsScreenProcessor.buttonX(((SodiumVideoOptionsScreen)this.screen).rso$getCloseButton()))) == Integer.MAX_VALUE ? ((SodiumVideoOptionsScreen)this.screen).width : limit - 8;
    }

    private static int buttonX(@Nullable FlatButtonWidget button) {
        return button == null ? Integer.MAX_VALUE : button.getX();
    }

    private void decorateButton(@Nullable FlatButtonWidget button, InputBindingSupplier binding) {
        if (button == null) {
            return;
        }
        button.setLabelDecorator(label -> Controlify.instance().getCurrentController().filter(RsoOptionsScreenProcessor::shouldShowGuides).map(controller -> binding.on(controller)).filter(bind -> !bind.isUnbound()).map(bind -> Component.empty().append(bind.inputGlyph()).append(CommonComponents.SPACE).append(label)).orElse((Component)label));
    }

    private static boolean press(@Nullable FlatButtonWidget button) {
        return button != null && button.tryPress();
    }

    private static Optional<InputBinding> activeBinding(ControllerEntity controller, InputBindingSupplier supplier) {
        InputBinding binding = supplier.on(controller);
        return binding.isUnbound() ? Optional.empty() : Optional.of(binding);
    }

    private static boolean shouldShowGuides(ControllerEntity controller) {
        return Controlify.instance().currentInputMode().isController() && controller.settings().generic.guide.showScreenGuides;
    }
}

