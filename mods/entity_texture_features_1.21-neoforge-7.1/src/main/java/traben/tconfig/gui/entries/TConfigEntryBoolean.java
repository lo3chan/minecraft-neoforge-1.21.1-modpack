/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 */
package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import traben.tconfig.gui.entries.TConfigEntryValue;

public class TConfigEntryBoolean
extends TConfigEntryValue<Boolean> {
    private final BooleanButtonWidget widget;

    public TConfigEntryBoolean(@Translatable String translationKey, @Translatable @Nullable String tooltip, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean defaultValue) {
        super(translationKey, tooltip, getter, setter, defaultValue);
        this.widget = new BooleanButtonWidget(0, 0, 20, 20, this.getText().getString(), getter.get(), this.getTooltip());
    }

    public TConfigEntryBoolean(@Translatable String translationKey, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean defaultValue) {
        this(translationKey, (String)null, getter, setter, defaultValue);
    }

    public TConfigEntryBoolean setType(Type type) {
        this.widget.type = type;
        return this;
    }

    @Override
    protected Boolean getValueFromWidget() {
        return this.widget.value;
    }

    @Override
    public AbstractWidget getWidget(int x, int y, int width, int height) {
        this.widget.setRectangle(width, height, x, y);
        return this.widget;
    }

    @Override
    void setWidgetToDefaultValue() {
        this.widget.value = (Boolean)this.defaultValue;
        this.widget.updateMessage();
    }

    @Override
    void resetWidgetToInitialValue() {
        this.widget.value = (Boolean)this.getter.get();
        this.widget.updateMessage();
    }

    private class BooleanButtonWidget
    extends Button {
        private final String title;
        private boolean value;
        private Type type;

        public BooleanButtonWidget(int x, int y, int width, int height, String text, boolean initialValue, Tooltip tooltip) {
            super(x, y, width, height, Component.nullToEmpty((String)""), null, DEFAULT_NARRATION);
            this.type = Type.ON_OFF;
            this.value = initialValue;
            this.title = text + ": ";
            this.updateMessage();
            this.setTooltip(tooltip);
        }

        private void updateMessage() {
            this.setMessage(Component.nullToEmpty((String)(this.title + (this.value != (Boolean)TConfigEntryBoolean.this.getter.get() ? "\u00a7a" : "") + this.type.get(this.value))));
        }

        public void onPress() {
            this.value = !this.value;
            this.updateMessage();
        }
    }

    public static enum Type {
        ON_OFF(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF),
        YES_NO(CommonComponents.GUI_YES, CommonComponents.GUI_NO);

        private final String t;
        private final String f;

        private Type(Component t, Component f) {
            this.t = t.getString();
            this.f = f.getString();
        }

        public String get(boolean value) {
            return value ? this.t : this.f;
        }
    }
}

