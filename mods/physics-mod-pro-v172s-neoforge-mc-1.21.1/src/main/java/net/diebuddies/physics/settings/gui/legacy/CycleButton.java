/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package net.diebuddies.physics.settings.gui.legacy;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.UXTooltipAccessor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class CycleButton<T>
extends AbstractButton
implements UXTooltipAccessor {
    static final BooleanSupplier DEFAULT_ALT_LIST_SELECTOR = Screen::hasAltDown;
    private static final List<Boolean> BOOLEAN_OPTIONS = ImmutableList.of((Object)Boolean.TRUE, (Object)Boolean.FALSE);
    private final Component name;
    private int index;
    private T value;
    private final ValueListSupplier<T> values;
    private final Function<T, Component> valueStringifier;
    private final Function<CycleButton<T>, MutableComponent> narrationProvider;
    private final OnValueChange<T> onValueChange;
    private final TooltipSupplier<T> tooltipSupplier;
    private final boolean displayOnlyValue;

    CycleButton(int i, int j, int k, int l, Component component, Component component2, int m, T object, ValueListSupplier<T> valueListSupplier, Function<T, Component> function, Function<CycleButton<T>, MutableComponent> function2, OnValueChange<T> onValueChange, TooltipSupplier<T> tooltipSupplier, boolean bl) {
        super(i, j, k, l, component);
        this.name = component2;
        this.index = m;
        this.value = object;
        this.values = valueListSupplier;
        this.valueStringifier = function;
        this.narrationProvider = function2;
        this.onValueChange = onValueChange;
        this.tooltipSupplier = tooltipSupplier;
        this.displayOnlyValue = bl;
        ButtonSettings.addCustomButtonStyle(this);
    }

    public void onPress() {
        if (Screen.hasShiftDown()) {
            this.cycleValue(-1);
        } else {
            this.cycleValue(1);
        }
    }

    private void cycleValue(int i) {
        List<T> list = this.values.getSelectedList();
        this.index = Mth.positiveModulo((int)(this.index + i), (int)list.size());
        T object = list.get(this.index);
        this.updateValue(object);
        this.onValueChange.onValueChange(this, object);
    }

    private T getCycledValue(int i) {
        List<T> list = this.values.getSelectedList();
        return list.get(Mth.positiveModulo((int)(this.index + i), (int)list.size()));
    }

    public boolean mouseScrolled(double d, double e, double f, double g) {
        if (g > 0.0) {
            this.cycleValue(-1);
        } else if (g < 0.0) {
            this.cycleValue(1);
        }
        return true;
    }

    public void setValue(T object) {
        List<T> list = this.values.getSelectedList();
        int i = list.indexOf(object);
        if (i != -1) {
            this.index = i;
        }
        this.updateValue(object);
    }

    private void updateValue(T object) {
        Component component = this.createLabelForValue(object);
        this.setMessage(component);
        this.value = object;
    }

    private Component createLabelForValue(T object) {
        return this.displayOnlyValue ? this.valueStringifier.apply(object) : this.createFullName(object);
    }

    private MutableComponent createFullName(T object) {
        return CommonComponents.optionNameValue((Component)this.name, (Component)this.valueStringifier.apply(object));
    }

    public T getValue() {
        return this.value;
    }

    protected MutableComponent createNarrationMessage() {
        return this.narrationProvider.apply(this);
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
        if (this.active) {
            T object = this.getCycledValue(1);
            Component component = this.createLabelForValue(object);
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, (Component)Component.translatable((String)"narration.cycle_button.usage.focused", (Object[])new Object[]{component}));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, (Component)Component.translatable((String)"narration.cycle_button.usage.hovered", (Object[])new Object[]{component}));
            }
        }
    }

    public MutableComponent createDefaultNarrationMessage() {
        return CycleButton.wrapDefaultNarrationMessage((Component)(this.displayOnlyValue ? this.createFullName(this.value) : this.getMessage()));
    }

    @Override
    public Component getTooltipLegacy() {
        return (Component)this.tooltipSupplier.apply(this.value);
    }

    public static <T> Builder<T> builder(Function<T, Component> function) {
        return new Builder<T>(function);
    }

    public static Builder<Boolean> booleanBuilder(Component component, Component component2) {
        return new Builder<Boolean>(boolean_ -> boolean_ != false ? component : component2).withValues(BOOLEAN_OPTIONS);
    }

    public static Builder<Boolean> onOffBuilder() {
        return new Builder<Boolean>(boolean_ -> boolean_ != false ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF).withValues(BOOLEAN_OPTIONS);
    }

    public static Builder<Boolean> onOffBuilder(boolean bl) {
        return CycleButton.onOffBuilder().withInitialValue(bl);
    }

    public boolean isMouseOver(double d, double e) {
        return this.visible && d >= (double)this.getX() && e >= (double)this.getY() && d < (double)(this.getX() + this.width) && e < (double)(this.getY() + this.height);
    }

    static interface ValueListSupplier<T> {
        public List<T> getSelectedList();

        public List<T> getDefaultList();

        public static <T> ValueListSupplier<T> create(List<T> list) {
            final ImmutableList list2 = ImmutableList.copyOf(list);
            return new ValueListSupplier<T>(){

                @Override
                public List<T> getSelectedList() {
                    return list2;
                }

                @Override
                public List<T> getDefaultList() {
                    return list2;
                }
            };
        }

        public static <T> ValueListSupplier<T> create(final BooleanSupplier booleanSupplier, List<T> list, List<T> list2) {
            final ImmutableList list3 = ImmutableList.copyOf(list);
            final ImmutableList list4 = ImmutableList.copyOf(list2);
            return new ValueListSupplier<T>(){

                @Override
                public List<T> getSelectedList() {
                    return booleanSupplier.getAsBoolean() ? list4 : list3;
                }

                @Override
                public List<T> getDefaultList() {
                    return list3;
                }
            };
        }
    }

    public static interface OnValueChange<T> {
        public void onValueChange(CycleButton var1, T var2);
    }

    @FunctionalInterface
    public static interface TooltipSupplier<T>
    extends Function<T, Component> {
    }

    public static class Builder<T> {
        private int initialIndex;
        @Nullable
        private T initialValue;
        private final Function<T, Component> valueStringifier;
        private TooltipSupplier<T> tooltipSupplier = object -> null;
        private Function<CycleButton<T>, MutableComponent> narrationProvider = CycleButton::createDefaultNarrationMessage;
        private ValueListSupplier<T> values = ValueListSupplier.create(ImmutableList.of());
        private boolean displayOnlyValue;

        public Builder(Function<T, Component> function) {
            this.valueStringifier = function;
        }

        public Builder<T> withValues(List<T> list) {
            this.values = ValueListSupplier.create(list);
            return this;
        }

        @SafeVarargs
        public final Builder<T> withValues(T ... objects) {
            return this.withValues((List<T>)ImmutableList.copyOf((Object[])objects));
        }

        public Builder<T> withValues(List<T> list, List<T> list2) {
            this.values = ValueListSupplier.create(DEFAULT_ALT_LIST_SELECTOR, list, list2);
            return this;
        }

        public Builder<T> withValues(BooleanSupplier booleanSupplier, List<T> list, List<T> list2) {
            this.values = ValueListSupplier.create(booleanSupplier, list, list2);
            return this;
        }

        public Builder<T> withTooltip(TooltipSupplier<T> tooltipSupplier) {
            this.tooltipSupplier = tooltipSupplier;
            return this;
        }

        public Builder<T> withInitialValue(T object) {
            this.initialValue = object;
            int i = this.values.getDefaultList().indexOf(object);
            if (i != -1) {
                this.initialIndex = i;
            }
            return this;
        }

        public Builder<T> withCustomNarration(Function<CycleButton<T>, MutableComponent> function) {
            this.narrationProvider = function;
            return this;
        }

        public Builder<T> displayOnlyValue() {
            this.displayOnlyValue = true;
            return this;
        }

        public CycleButton<T> create(int i, int j, int k, int l, Component component) {
            return this.create(i, j, k, l, component, (cycleButton, object) -> {});
        }

        public CycleButton<T> create(int i, int j, int k, int l, Component component, OnValueChange<T> onValueChange) {
            List<T> list = this.values.getDefaultList();
            if (list.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            }
            T object = this.initialValue != null ? this.initialValue : list.get(this.initialIndex);
            Component component2 = this.valueStringifier.apply(object);
            Component component3 = this.displayOnlyValue ? component2 : CommonComponents.optionNameValue((Component)component, (Component)component2);
            return new CycleButton<T>(i, j, k, l, component3, component, this.initialIndex, object, this.values, this.valueStringifier, this.narrationProvider, onValueChange, this.tooltipSupplier, this.displayOnlyValue);
        }
    }
}

