/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class HeightProperty
extends SimpleIntegerArrayProperty {
    protected HeightProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(HeightProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "heights", "height"));
    }

    public static HeightProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            if (!properties.containsKey("heights." + propertyNum) && !properties.containsKey("height." + propertyNum) && (properties.containsKey("minHeight." + propertyNum) || properties.containsKey("maxHeight." + propertyNum))) {
                String min = "-64";
                String max = "319";
                if (properties.containsKey("minHeight." + propertyNum)) {
                    min = properties.getProperty("minHeight." + propertyNum).strip();
                }
                if (properties.containsKey("maxHeight." + propertyNum)) {
                    max = properties.getProperty("maxHeight." + propertyNum).strip();
                }
                properties.put("heights." + propertyNum, min + "-" + max);
            }
            return new HeightProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"heights", "height"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        return entity.blockY();
    }
}

