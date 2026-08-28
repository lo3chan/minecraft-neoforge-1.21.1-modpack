/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.monster.Blaze
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package traben.entity_texture_features.mixin.mixins.entity.misc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;

@Mixin(value={Blaze.class})
public abstract class MixinBlazeEntity
extends Monster {
    protected MixinBlazeEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyArg(method={"aiStep"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), index=2)
    private double mixin(double x) {
        if (ETF.config().getConfig().canDoCustomTextures() && ETFManager.getInstance().ENTITY_TYPE_IGNORE_PARTICLES.contains(this.getType())) {
            return -500.0;
        }
        return x;
    }
}

