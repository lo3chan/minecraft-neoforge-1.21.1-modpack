/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.VariantHolder
 *  net.minecraft.world.entity.animal.Cat
 *  net.minecraft.world.entity.animal.Sheep
 *  net.minecraft.world.entity.animal.TropicalFish
 *  net.minecraft.world.entity.animal.Wolf
 *  net.minecraft.world.entity.animal.horse.Llama
 *  net.minecraft.world.entity.monster.Shulker
 *  net.minecraft.world.item.DyeColor
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Optional;
import java.util.Properties;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class ColorProperty
extends StringArrayOrRegexProperty {
    protected ColorProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "colors", "collarColors"));
    }

    public static ColorProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ColorProperty(properties, propertyNum);
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
    protected String getValueFromEntity(ETFEntityRenderState state) {
        if (state != null) {
            Shulker shulker;
            Llama llama;
            DyeColor str;
            ETFEntity entity = state.entity();
            if (entity instanceof Llama && (str = (llama = (Llama)entity).getSwag()) != null) {
                return str.getName();
            }
            if (entity instanceof Cat) {
                Cat cat = (Cat)entity;
                return cat.getCollarColor().getName();
            }
            if (entity instanceof Shulker && (str = (shulker = (Shulker)entity).getColor()) != null) {
                return str.getName();
            }
            if (entity instanceof Wolf) {
                Wolf wolf = (Wolf)entity;
                return wolf.getCollarColor().getName();
            }
            if (entity instanceof Sheep) {
                Sheep sheep = (Sheep)entity;
                return sheep.getColor().getName();
            }
            if (entity instanceof TropicalFish) {
                TropicalFish fishy = (TropicalFish)entity;
                str = TropicalFish.getBaseColor((int)fishy.getVariant().getPackedId());
                return str.getName();
            }
            if (entity instanceof VariantHolder) {
                VariantHolder variantHolder = (VariantHolder)entity;
                try {
                    Optional optional;
                    Object object = variantHolder.getVariant();
                    if (object instanceof DyeColor) {
                        DyeColor dye = (DyeColor)object;
                        return dye.getName();
                    }
                    object = variantHolder.getVariant();
                    if (object instanceof Optional && (optional = (Optional)object).isPresent() && (object = optional.get()) instanceof DyeColor) {
                        DyeColor dye = (DyeColor)object;
                        return dye.getName();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"colors", "collarColors"};
    }
}

