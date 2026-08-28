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

public class TextureRuleIndexProperty
extends SimpleIntegerArrayProperty {
    protected TextureRuleIndexProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(TextureRuleIndexProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "textureRule", "texture_rule"));
    }

    public static TextureRuleIndexProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TextureRuleIndexProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"textureRule", "texture_rule"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        int val = (Integer)ETFManager.getInstance().LAST_RULE_INDEX_OF_ENTITY.get(entity.uuid());
        return Math.max(val, 0);
    }
}

