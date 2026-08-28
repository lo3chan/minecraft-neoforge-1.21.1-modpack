/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.dimension.BuiltinDimensionTypes
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Optional;
import java.util.Properties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public class DimensionProperty
extends StringArrayOrRegexProperty {
    private final boolean doPrint;

    protected DimensionProperty(String string) throws RandomProperty.RandomPropertyException {
        super(string.replace("print:", ""));
        this.doPrint = string.startsWith("print:");
    }

    public static DimensionProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new DimensionProperty(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "dimension"));
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String getValueFromEntity(ETFEntityRenderState etfEntity) {
        if (etfEntity == null) {
            return null;
        }
        Level world = etfEntity.world();
        if (world == null) {
            return null;
        }
        Optional dimKey = etfEntity.world().dimensionTypeRegistration().unwrapKey();
        if (dimKey.isEmpty()) {
            return null;
        }
        ResourceLocation key = ((ResourceKey)dimKey.get()).location();
        if (key == null) {
            return null;
        }
        String output = key.equals((Object)BuiltinDimensionTypes.OVERWORLD_EFFECTS) || key.getPath().equals("overworld_caves") ? "overworld" : (key.equals((Object)BuiltinDimensionTypes.NETHER_EFFECTS) ? "the_nether" : (key.equals((Object)BuiltinDimensionTypes.END_EFFECTS) ? "the_end" : key.toString()));
        if (this.doPrint) {
            ETFUtils2.logMessage("[Dimension property print]: " + output);
        }
        return output;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"dimension"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }
}

