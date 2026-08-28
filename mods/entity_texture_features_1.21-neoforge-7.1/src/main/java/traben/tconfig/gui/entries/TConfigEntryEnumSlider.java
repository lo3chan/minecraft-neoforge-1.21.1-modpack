/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.gui.components.AbstractSliderButton
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.tconfig.gui.entries.TConfigEntryNullSafe;

public class TConfigEntryEnumSlider<E extends Enum<E>>
extends TConfigEntryNullSafe<E> {
    private final EnumSliderWidget<E> widget;
    private boolean appendNullValue = false;

    public TConfigEntryEnumSlider(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        super(text, tooltip, getter, setter, defaultValue);
        if (defaultValue == null) {
            this.appendNullValue = true;
        }
        this.widget = new EnumSliderWidget(this, this.getText(), (Enum)getter.get(), this.getTooltip(), enumClass);
    }

    public TConfigEntryEnumSlider(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, tooltip, getter, setter, defaultValue, ((Enum)defaultValue).getDeclaringClass());
    }

    public TConfigEntryEnumSlider(@Translatable String text, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, null, getter, setter, defaultValue, ((Enum)defaultValue).getDeclaringClass());
    }

    public TConfigEntryEnumSlider(@Translatable String text, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        this(text, null, getter, setter, defaultValue, enumClass);
    }

    @Override
    public TConfigEntryNullSafe<E> allowNullValue() {
        this.appendNullValue = true;
        return this;
    }

    @Override
    protected E getValueFromWidget() {
        return this.widget.getValue();
    }

    @Override
    public AbstractWidget getWidget(int x, int y, int width, int height) {
        this.widget.setRectangle(width, height, x, y);
        return this.widget;
    }

    @Override
    void setWidgetToDefaultValue() {
        this.widget.setValue((Enum)this.defaultValue);
    }

    @Override
    void resetWidgetToInitialValue() {
        this.widget.setValue((Enum)this.getter.get());
    }

    public static class EnumSliderWidget<T extends Enum<?>>
    extends AbstractSliderButton {
        private final T[] enumValues;
        private final String title;
        final /* synthetic */ TConfigEntryEnumSlider this$0;

        public EnumSliderWidget(Component text, T initialValue, Tooltip tooltip, Class<T> enumClass) {
            this.this$0 = this$0;
            super(0, 0, 20, 20, text, 1.0);
            this.enumValues = (Enum[])enumClass.getEnumConstants();
            this.title = text.getString() + ": ";
            this.setTooltip(tooltip);
            this.setValue(initialValue);
        }

        @Nullable
        private T getValue() {
            if (this.getIndex() >= this.enumValues.length) {
                return null;
            }
            return this.enumValues[this.getIndex()];
        }

        private void setValue(T value) {
            this.value = value == null ? 1.0 : (double)((Enum)value).ordinal() / (double)this.getChoiceCount();
            this.updateMessage();
        }

        protected void updateMessage() {
            this.value = (double)this.getIndex() / (double)this.getChoiceCount();
            T value2 = this.getValue();
            this.setMessage(Component.nullToEmpty((String)(this.title + (value2 != this.this$0.getter.get() ? "\u00a7a" : "") + String.valueOf(value2 == null ? "---" : value2))));
        }

        protected void applyValue() {
        }

        private int getChoiceCount() {
            return this.enumValues.length - (this.this$0.appendNullValue ? 0 : 1);
        }

        private int getIndex() {
            return (int)Math.round(this.value * (double)this.getChoiceCount());
        }
    }
}

