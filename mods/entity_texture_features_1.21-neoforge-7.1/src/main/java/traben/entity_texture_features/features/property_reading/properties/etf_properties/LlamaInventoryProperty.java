/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.animal.horse.Llama
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.horse.Llama;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class LlamaInventoryProperty
extends SimpleIntegerArrayProperty {
    protected LlamaInventoryProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(LlamaInventoryProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "llamaInventory"));
    }

    public static LlamaInventoryProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new LlamaInventoryProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"llamaInventory"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        ETFEntity eTFEntity;
        if (entity != null && (eTFEntity = entity.entity()) instanceof Llama) {
            Llama llama = (Llama)eTFEntity;
            return llama.getInventoryColumns();
        }
        return Integer.MIN_VALUE;
    }
}

