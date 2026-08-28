/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.animal.Panda
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.Panda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class PandaGeneProperty
extends StringArrayOrRegexProperty {
    protected PandaGeneProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(PandaGeneProperty.readPropertiesOrThrow(properties, propertyNum, "hiddenGene", "gene"));
    }

    public static PandaGeneProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new PandaGeneProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    @Nullable
    protected String getValueFromEntity(ETFEntityRenderState entityETF) {
        ETFEntity eTFEntity;
        if (entityETF != null && (eTFEntity = entityETF.entity()) instanceof Panda) {
            Panda panda = (Panda)eTFEntity;
            return panda.getHiddenGene().getSerializedName();
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"hiddenGene", "gene"};
    }
}

