/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.GuiProperties;
import mezz.jei.gui.overlay.IGuiPropertiesCache;
import mezz.jei.gui.overlay.IScreenPropertiesUpdater;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class GuiPropertiesCache<T>
implements IGuiPropertiesCache {
    private final GuiPropertiesGetter<T> guiPropertiesGetter;
    @Nullable
    private IGuiProperties previousGuiProperties;
    private boolean guiPropertiesAreValid = false;
    private Set<ImmutableRect2i> previousGuiExclusionAreas = Set.of();
    @Nullable
    private ImmutablePoint2i mouseExclusionArea;

    public GuiPropertiesCache(GuiPropertiesGetter<T> guiPropertiesGetter) {
        this.guiPropertiesGetter = guiPropertiesGetter;
    }

    @Override
    public IScreenPropertiesUpdater createUpdater(Runnable onChange) {
        return new Updater(this, onChange);
    }

    public boolean hasValidScreen() {
        return this.guiPropertiesAreValid;
    }

    @Override
    @Nullable
    public IGuiProperties getGuiProperties() {
        if (!this.guiPropertiesAreValid) {
            return null;
        }
        return this.previousGuiProperties;
    }

    @Override
    public Set<ImmutableRect2i> getGuiExclusionAreas() {
        return this.previousGuiExclusionAreas;
    }

    @Nullable
    public ImmutablePoint2i getMouseExclusionArea() {
        return this.mouseExclusionArea;
    }

    @FunctionalInterface
    public static interface GuiPropertiesGetter<T> {
        @Nullable
        public IGuiProperties getGuiProperties(T var1);
    }

    private static class Updater<T>
    implements IScreenPropertiesUpdater {
        private static final Logger LOGGER = LogManager.getLogger();
        private static final int MIN_GUI_DIMENSION = -1000000000;
        private static final int MAX_GUI_DIMENSION = 1000000000;
        private final GuiPropertiesCache<T> cache;
        private final Runnable onChange;
        private boolean changed = false;

        public Updater(GuiPropertiesCache<T> cache, Runnable onChange) {
            this.cache = cache;
            this.onChange = onChange;
        }

        @Override
        public Updater<T> updateScreen(@Nullable Screen guiScreen) {
            if (guiScreen == null) {
                return this.updateGuiProperties(null);
            }
            Screen typedScreen = guiScreen;
            return this.updateGuiProperties(this.cache.guiPropertiesGetter.getGuiProperties(typedScreen));
        }

        @Override
        public Updater<T> updateGuiProperties(@Nullable IGuiProperties currentGuiProperties) {
            if (!GuiProperties.areEqual(this.cache.previousGuiProperties, currentGuiProperties)) {
                boolean previouslyValid = this.cache.guiPropertiesAreValid;
                this.cache.guiPropertiesAreValid = Updater.validateGuiProperties(currentGuiProperties);
                this.cache.previousGuiProperties = currentGuiProperties;
                if (previouslyValid || this.cache.guiPropertiesAreValid) {
                    this.changed = true;
                }
            }
            return this;
        }

        @Override
        public Updater<T> updateExclusionAreas(Set<ImmutableRect2i> updatedGuiExclusionAreas) {
            if (!this.cache.previousGuiExclusionAreas.equals(updatedGuiExclusionAreas)) {
                this.cache.previousGuiExclusionAreas = updatedGuiExclusionAreas;
                this.changed = true;
            }
            return this;
        }

        @Override
        public Updater<T> updateMouseExclusionArea(@Nullable ImmutablePoint2i mouseExclusionArea) {
            if (!Objects.equals(this.cache.mouseExclusionArea, mouseExclusionArea)) {
                this.cache.mouseExclusionArea = mouseExclusionArea;
                this.changed = true;
            }
            return this;
        }

        @Override
        public void update() {
            if (this.changed) {
                this.notifyChange();
            }
        }

        @Override
        public void forceUpdate() {
            this.notifyChange();
        }

        private void notifyChange() {
            this.onChange.run();
        }

        private static void validate(List<String> errors, String property, int min, int max, int value) {
            if (value < min || value > max) {
                errors.add(String.format("%s must be greater than %s and less than %s: %s", property, min, max, value));
            }
        }

        private static boolean validateGuiProperties(@Nullable IGuiProperties guiProperties) {
            if (guiProperties == null) {
                return false;
            }
            ArrayList<String> errors = new ArrayList<String>();
            Updater.validate(errors, "guiXSize", 1, 1000000000, guiProperties.guiXSize());
            Updater.validate(errors, "guiYSize", 1, 1000000000, guiProperties.guiYSize());
            Updater.validate(errors, "screenWidth", 1, 1000000000, guiProperties.screenWidth());
            Updater.validate(errors, "screenHeight", 1, 1000000000, guiProperties.screenHeight());
            Updater.validate(errors, "guiLeft", -1000000000, 1000000000, guiProperties.guiLeft());
            Updater.validate(errors, "guiTop", -1000000000, 1000000000, guiProperties.guiTop());
            if (!errors.isEmpty()) {
                LOGGER.error("Received invalid gui properties for screen: {}\n{}", guiProperties.screenClass(), (Object)String.join((CharSequence)"\n", errors));
                return false;
            }
            return true;
        }
    }
}

