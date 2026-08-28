/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class NumberRangeFromStringArrayProperty<N extends Number>
extends RandomProperty {
    public final String originalInput;
    protected final ArrayList<RangeTester<N>> ARRAY = new ArrayList();
    protected final boolean doPrint;

    protected NumberRangeFromStringArrayProperty(String stringInput) throws RandomProperty.RandomPropertyException {
        this.originalInput = stringInput;
        if (stringInput == null || stringInput.isBlank()) {
            throw new RandomProperty.RandomPropertyException(this.getPropertyId() + " property was broken");
        }
        this.doPrint = stringInput.startsWith("print:");
        String testString = this.doPrint ? stringInput.substring(6) : stringInput;
        String onlyNumbersSpacesDashesAndPeriods = testString.replaceAll("[^0-9.\\s-]", "");
        String[] array = onlyNumbersSpacesDashesAndPeriods.trim().split("\\s+");
        if (array.length == 0) {
            throw new RandomProperty.RandomPropertyException(this.getPropertyId() + " property was broken");
        }
        for (String str : array) {
            RangeTester<N> tester = this.getRangeTesterFromString(str);
            if (tester == null) continue;
            this.ARRAY.add(tester);
        }
    }

    @Override
    public boolean testEntityInternal(ETFEntityRenderState entity) {
        N checkValue = this.getRangeValueFromEntity(entity);
        if (checkValue != null) {
            for (RangeTester<N> range : this.ARRAY) {
                if (range == null || !range.isValueWithinRangeOrEqual(checkValue)) continue;
                if (this.doPrint) {
                    ETFUtils2.logMessage(this.getPropertyId() + " property value print: [" + String.valueOf(checkValue) + "], returned: true.");
                }
                return true;
            }
        }
        if (this.doPrint) {
            ETFUtils2.logMessage(this.getPropertyId() + " property value print: [" + String.valueOf(checkValue) + "], returned: false.");
        }
        return false;
    }

    @Nullable
    protected abstract N getRangeValueFromEntity(ETFEntityRenderState var1);

    @Nullable
    protected abstract RangeTester<N> getRangeTesterFromString(String var1);

    @Override
    @NotNull
    public abstract String[] getPropertyIds();

    @Override
    protected String getPrintableRuleInfo() {
        return this.originalInput;
    }

    public static interface RangeTester<N> {
        public boolean isValueWithinRangeOrEqual(N var1);
    }
}

