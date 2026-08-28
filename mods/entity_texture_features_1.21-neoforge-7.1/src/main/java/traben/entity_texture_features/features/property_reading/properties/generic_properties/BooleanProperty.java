/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import java.util.Properties;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class BooleanProperty
extends RandomProperty {
    private final boolean BOOLEAN;

    protected BooleanProperty(Boolean bool) throws RandomProperty.RandomPropertyException {
        if (bool == null) {
            throw new RandomProperty.RandomPropertyException(this.getPropertyId() + " property was broken");
        }
        this.BOOLEAN = bool;
    }

    @Nullable
    public static Boolean getGenericBooleanThatCanNull(Properties props, int num, String ... propertyNames) {
        if (propertyNames.length == 0) {
            throw new IllegalArgumentException("BooleanProperty, empty property names given");
        }
        for (String propertyName : propertyNames) {
            if (!props.containsKey(propertyName + "." + num)) continue;
            String input = props.getProperty(propertyName + "." + num).trim();
            if ("true".equals(input) || "false".equals(input)) {
                return "true".equals(input);
            }
            ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
        }
        return null;
    }

    @Override
    public boolean testEntityInternal(ETFEntityRenderState entity) {
        Boolean entityBoolean = this.getValueFromEntity(entity);
        if (entityBoolean != null) {
            return this.BOOLEAN == entityBoolean;
        }
        return false;
    }

    @Nullable
    protected abstract Boolean getValueFromEntity(ETFEntityRenderState var1);

    @Override
    protected String getPrintableRuleInfo() {
        return this.BOOLEAN ? "true" : "false";
    }
}

