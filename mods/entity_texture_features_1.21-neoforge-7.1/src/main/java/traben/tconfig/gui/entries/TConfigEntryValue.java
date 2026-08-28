/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 */
package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import traben.tconfig.gui.entries.TConfigEntry;

public abstract class TConfigEntryValue<V>
extends TConfigEntry {
    protected final Supplier<V> getter;
    protected final Consumer<V> setter;
    protected final V defaultValue;

    protected TConfigEntryValue(@Translatable String translationKey, @Translatable String tooltip, Supplier<V> getter, Consumer<V> setter, V defaultValue) {
        super(translationKey, tooltip);
        this.getter = getter;
        this.setter = setter;
        this.defaultValue = defaultValue;
    }

    protected TConfigEntryValue(@Translatable String translationKey, Supplier<V> getter, Consumer<V> setter, V defaultValue) {
        super(translationKey, null);
        this.getter = getter;
        this.setter = setter;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean saveValuesToConfig() {
        if (this.hasChangedFromInitial()) {
            this.setter.accept(this.getValueFromWidget());
            return true;
        }
        return false;
    }

    protected abstract V getValueFromWidget();

    @Override
    void setValuesToDefault() {
        this.setWidgetToDefaultValue();
    }

    abstract void setWidgetToDefaultValue();

    @Override
    void resetValuesToInitial() {
        this.resetWidgetToInitialValue();
    }

    abstract void resetWidgetToInitialValue();

    @Override
    boolean hasChangedFromInitial() {
        return !this.getValueFromWidget().equals(this.getter.get());
    }
}

