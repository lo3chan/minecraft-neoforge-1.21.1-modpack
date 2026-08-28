/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class LanguageProperty
extends StringArrayOrRegexProperty {
    protected LanguageProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "language"));
    }

    public static LanguageProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new LanguageProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String getValueFromEntity(ETFEntityRenderState etfEntity) {
        return Minecraft.getInstance().options.languageCode;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"language"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }
}

