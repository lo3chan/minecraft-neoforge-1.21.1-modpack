/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.tconfig.gui.entries.TConfigEntryNullSafe;

public class TConfigEntryEnumButton<E extends Enum<E>>
extends TConfigEntryNullSafe<E> {
    private final EnumButtonWidget<E> widget;
    private boolean appendNullValue = false;

    public TConfigEntryEnumButton(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        super(text, tooltip, getter, setter, defaultValue);
        if (defaultValue == null) {
            this.appendNullValue = true;
        }
        this.widget = new EnumButtonWidget(this, this.getText(), (Enum)getter.get(), this.getTooltip(), enumClass);
    }

    public TConfigEntryEnumButton(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, tooltip, getter, setter, defaultValue, ((Enum)defaultValue).getDeclaringClass());
    }

    public TConfigEntryEnumButton(@Translatable String text, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        this(text, null, getter, setter, defaultValue, enumClass);
    }

    public TConfigEntryEnumButton(@Translatable String text, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, null, getter, setter, defaultValue, ((Enum)defaultValue).getDeclaringClass());
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

    public static class EnumButtonWidget<T extends Enum<?>>
    extends Button {
        private final T[] enumValues;
        private final String title;
        private int index;
        final /* synthetic */ TConfigEntryEnumButton this$0;

        public EnumButtonWidget(Component text, T initialValue, Tooltip tooltip, Class<T> enumClass) {
            this.this$0 = this$0;
            super(0, 0, 20, 20, text, button -> {}, Button.DEFAULT_NARRATION);
            this.enumValues = (Enum[])enumClass.getEnumConstants();
            this.title = text.getString() + ": ";
            this.setTooltip(tooltip);
            this.setValue(initialValue);
        }

        public int getIndex() {
            return this.index;
        }

        private int getChoiceCount() {
            return this.enumValues.length - (this.this$0.appendNullValue ? 0 : 1);
        }

        @Nullable
        private T getValue() {
            if (this.index >= this.enumValues.length) {
                return null;
            }
            return this.enumValues[this.index];
        }

        private void setValue(T value) {
            this.index = value == null ? this.enumValues.length : ((Enum)value).ordinal();
            this.updateMessage();
        }

        protected void updateMessage() {
            T value = this.getValue();
            this.setMessage(Component.nullToEmpty((String)(this.title + (value != this.this$0.getter.get() ? "\u00a7a" : "") + String.valueOf(value == null ? "---" : value))));
        }

        public void onPress() {
            ++this.index;
            if (this.index > this.getChoiceCount()) {
                this.index = 0;
            }
            this.updateMessage();
        }
    }
}

