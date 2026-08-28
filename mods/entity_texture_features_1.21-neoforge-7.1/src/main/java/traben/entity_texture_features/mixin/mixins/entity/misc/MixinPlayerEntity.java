/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.player.PlayerModelPart
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerEntity;

@Mixin(value={Player.class})
public abstract class MixinPlayerEntity
extends Entity
implements ETFPlayerEntity {
    public MixinPlayerEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    public abstract Inventory getInventory();

    @Shadow
    @NotNull
    public abstract Component getName();

    @Shadow
    public abstract boolean isModelPartShown(PlayerModelPart var1);

    @Inject(method={"interactOn"}, at={@At(value="HEAD")})
    private void etf$injected(CallbackInfoReturnable<InteractionResult> cir, @Local(argsOnly=true) Entity entity) {
        if (this.level().isClientSide() && ETF.config().getConfig().debugLoggingMode != ETFConfig.DebugLogMode.None) {
            ETFManager.getInstance().markEntityForDebugPrint(entity.getUUID());
        }
    }

    @Override
    public Entity etf$getEntity() {
        return this;
    }

    @Override
    public boolean etf$isTeammate(Player player) {
        return this.isAlliedTo((Entity)player);
    }

    @Override
    public Inventory etf$getInventory() {
        return this.getInventory();
    }

    @Override
    @Deprecated
    public boolean etf$isPartVisible(PlayerModelPart part) {
        return this.isModelPartShown(part);
    }

    @Override
    public Component etf$getName() {
        return this.getName();
    }

    @Override
    public String etf$getUuidAsString() {
        return this.getStringUUID();
    }
}

