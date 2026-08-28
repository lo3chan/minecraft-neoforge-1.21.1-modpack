/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.NeutralMob
 *  net.minecraft.world.entity.monster.Blaze
 *  net.minecraft.world.entity.monster.EnderMan
 *  net.minecraft.world.entity.monster.Guardian
 *  net.minecraft.world.entity.monster.SpellcasterIllager
 *  net.minecraft.world.entity.monster.Vindicator
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vindicator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class AngryProperty
extends BooleanProperty {
    protected AngryProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(AngryProperty.getGenericBooleanThatCanNull(properties, propertyNum, "angry", "isAngry", "is_angry", "aggressive", "is_aggressive"));
    }

    public static AngryProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new AngryProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState state) {
        if (state != null) {
            ETFEntity etfEntity = state.entity();
            if (etfEntity instanceof EnderMan) {
                EnderMan enderman = (EnderMan)etfEntity;
                return enderman.isCreepy();
            }
            if (etfEntity instanceof Blaze) {
                Blaze blaze = (Blaze)etfEntity;
                return blaze.isOnFire();
            }
            if (etfEntity instanceof Guardian) {
                Guardian guardian = (Guardian)etfEntity;
                return guardian.getActiveAttackTarget() != null;
            }
            if (etfEntity instanceof Vindicator) {
                Vindicator vindicator = (Vindicator)etfEntity;
                return vindicator.isAggressive();
            }
            if (etfEntity instanceof SpellcasterIllager) {
                SpellcasterIllager caster = (SpellcasterIllager)etfEntity;
                return caster.isCastingSpell();
            }
            if (etfEntity instanceof NeutralMob) {
                NeutralMob angry = (NeutralMob)etfEntity;
                return angry.isAngry();
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"angry", "isAngry", "is_angry", "aggressive", "is_aggressive"};
    }
}

