/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.ExternalPage
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.config.structure.Page
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.ScrollableFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.Tab;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.Holder;
import net.caffeinemc.mods.sodium.client.config.structure.ExternalPage;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.config.structure.Page;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

final class TabSelectionState {
    private final List<Tab<?>> tabs;
    private final Holder<String> persistedTabKey;
    private final Screen screen;
    private final Map<Tab<?>, AbstractFrame> framesByTab = new HashMap();
    private Optional<Tab<?>> selectedTab = Optional.empty();
    @Nullable
    private AbstractFrame selectedFrame;

    TabSelectionState(List<Tab<?>> tabs, Holder<String> persistedTabKey, Screen screen) {
        this.tabs = tabs;
        this.persistedTabKey = persistedTabKey;
        this.screen = screen;
    }

    void restorePersistedTab(Consumer<Tab<?>> restoredTabConsumer) {
        if (this.persistedTabKey.get() == null) {
            return;
        }
        this.selectedTab = this.tabs.stream().filter(tab -> TabSelectionState.getTabKey(tab).equals(this.persistedTabKey.get())).findAny();
        this.selectedTab.ifPresent(restoredTabConsumer);
    }

    void ensureSelectedTab(Consumer<Tab<?>> selectedTabConsumer) {
        if (this.selectedTab.isPresent() || this.tabs.isEmpty()) {
            return;
        }
        this.selectedTab = Optional.ofNullable((Tab)this.tabs.getFirst());
        this.selectedTab.ifPresent(selectedTabConsumer);
    }

    void setSelectedTab(Optional<Tab<?>> tab) {
        this.selectedTab = tab;
    }

    void activateSelectedTab() {
        this.selectedTab.ifPresent(value -> {
            Page patt0$temp = value.getPage();
            if (patt0$temp instanceof ExternalPage) {
                ExternalPage externalPage = (ExternalPage)patt0$temp;
                externalPage.currentScreenConsumer().accept(this.screen);
            } else {
                this.persistedTabKey.set(TabSelectionState.getTabKey(value));
            }
        });
    }

    void warmInactiveFrames(LayoutBounds frameSection) {
        this.tabs.stream().filter(tab -> this.selectedTab.filter(value -> value != tab).isPresent()).forEach(tab -> this.frameFor((Tab<?>)tab, frameSection));
    }

    void refreshFrames(LayoutBounds frameSection) {
        for (Tab<?> tab : this.tabs) {
            AbstractFrame frame = this.frameFor(tab, frameSection);
            if (frame == null) continue;
            frame.rebuildFrameContent();
        }
    }

    void rebuildSelectedFrame(LayoutBounds frameSection, List<GuiEventListener> children) {
        if (this.selectedTab.isEmpty()) {
            this.selectedFrame = null;
            return;
        }
        AbstractFrame frame = this.frameFor(this.selectedTab.get(), frameSection);
        if (frame != null) {
            this.selectedFrame = frame;
            frame.buildFrame();
            children.add(frame);
        }
    }

    Optional<Tab<?>> selectedTab() {
        return this.selectedTab;
    }

    @Nullable
    AbstractFrame selectedFrame() {
        return this.selectedFrame;
    }

    Optional<String> selectedTabKey() {
        return this.selectedTab.map(TabSelectionState::getTabKey);
    }

    @Nullable
    OptionRow findSelectedOptionRow(ResourceLocation optionId) {
        if (this.selectedFrame == null) {
            return null;
        }
        return this.selectedFrame.findFirstOptionRow(control -> {
            OptionExtended optionExtended;
            Option patt0$temp = control.getOption();
            return patt0$temp instanceof OptionExtended && (optionExtended = (OptionExtended)patt0$temp).rso$getId().equals((Object)optionId);
        });
    }

    @Nullable
    OptionRow findFirstSelectedOptionRow() {
        AbstractFrame abstractFrame = this.selectedFrame;
        if (abstractFrame instanceof ScrollableFrame) {
            ScrollableFrame scrollableFrame = (ScrollableFrame)abstractFrame;
            return scrollableFrame.findFirstOptionRow();
        }
        return this.selectedFrame == null ? null : this.selectedFrame.findFirstOptionRow(control -> true);
    }

    @Nullable
    OptionRow findLastSelectedOptionRow() {
        AbstractFrame abstractFrame = this.selectedFrame;
        if (abstractFrame instanceof ScrollableFrame) {
            ScrollableFrame scrollableFrame = (ScrollableFrame)abstractFrame;
            return scrollableFrame.findLastOptionRow();
        }
        return this.selectedFrame == null ? null : this.selectedFrame.findLastOptionRow(control -> true);
    }

    @Nullable
    OptionRow findFirstVisibleSelectedOptionRow() {
        AbstractFrame abstractFrame = this.selectedFrame;
        if (abstractFrame instanceof ScrollableFrame) {
            ScrollableFrame scrollableFrame = (ScrollableFrame)abstractFrame;
            return scrollableFrame.findFirstVisibleOptionRow();
        }
        return this.findFirstSelectedOptionRow();
    }

    @Nullable
    OptionRow findLastVisibleSelectedOptionRow() {
        AbstractFrame abstractFrame = this.selectedFrame;
        if (abstractFrame instanceof ScrollableFrame) {
            ScrollableFrame scrollableFrame = (ScrollableFrame)abstractFrame;
            return scrollableFrame.findLastVisibleOptionRow();
        }
        return this.findLastSelectedOptionRow();
    }

    boolean scrollSelectedPageToStart() {
        ScrollableFrame scrollableFrame;
        AbstractFrame abstractFrame = this.selectedFrame;
        return abstractFrame instanceof ScrollableFrame && (scrollableFrame = (ScrollableFrame)abstractFrame).scrollToStart();
    }

    boolean scrollSelectedPageToEnd() {
        ScrollableFrame scrollableFrame;
        AbstractFrame abstractFrame = this.selectedFrame;
        return abstractFrame instanceof ScrollableFrame && (scrollableFrame = (ScrollableFrame)abstractFrame).scrollToEnd();
    }

    boolean scrollSelectedPage(int direction) {
        ScrollableFrame scrollableFrame;
        AbstractFrame abstractFrame = this.selectedFrame;
        return abstractFrame instanceof ScrollableFrame && (scrollableFrame = (ScrollableFrame)abstractFrame).scrollPage(direction);
    }

    private static String getTabKey(Tab<?> tab) {
        return tab.key();
    }

    @Nullable
    private AbstractFrame frameFor(Tab<?> tab, LayoutBounds frameSection) {
        AbstractFrame frame = this.framesByTab.get(tab);
        if (frame == null && (frame = (AbstractFrame)tab.getFrameFunction().apply(frameSection)) != null) {
            this.framesByTab.put(tab, frame);
        }
        return frame;
    }
}

