/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.monster.Phantom
 *  net.minecraft.world.entity.monster.Slime
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Slime;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class SizeProperty
extends SimpleIntegerArrayProperty {
    protected SizeProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(SizeProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "sizes", "size"));
    }

    public static SizeProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SizeProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"sizes", "size"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        ETFEntity eTFEntity;
        if (entity != null && (eTFEntity = entity.entity()) instanceof Slime) {
            Slime slime = (Slime)eTFEntity;
            return slime.getSize() - 1;
        }
        if (entity != null && (eTFEntity = entity.entity()) instanceof Phantom) {
            Phantom phantom = (Phantom)eTFEntity;
            return phantom.getPhantomSize();
        }
        return 0;
    }
}

