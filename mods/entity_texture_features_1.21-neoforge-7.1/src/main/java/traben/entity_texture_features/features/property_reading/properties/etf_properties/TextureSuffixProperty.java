/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TextureSuffixProperty
extends SimpleIntegerArrayProperty {
    protected TextureSuffixProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(TextureSuffixProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "textureSuffix", "texture_suffix"));
    }

    public static TextureSuffixProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TextureSuffixProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"textureSuffix", "texture_suffix"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        int val = (Integer)ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.get(entity.uuid());
        return Math.max(val, 0);
    }
}

