/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.animal.Parrot
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer.feature;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(value={ParrotOnShoulderLayer.class})
public abstract class MixinShoulderParrotFeatureRenderer<T extends Player>
extends RenderLayer<T, PlayerModel<T>> {
    @Unique
    private ETFEntityRenderState etf$heldEntity = null;

    public MixinShoulderParrotFeatureRenderer(RenderLayerParent<T, PlayerModel<T>> context) {
        super(context);
    }

    @ModifyArg(method={"Lnet/minecraft/client/renderer/entity/layers/ParrotOnShoulderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V"}, at=@At(value="INVOKE", target="Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private Consumer<EntityType<?>> etf$alterEntity(Consumer<EntityType<?>> action, @Local(argsOnly=true) T t, @Local CompoundTag nbtCompound) {
        return v -> {
            this.etf$HEADalterEntity(t, nbtCompound);
            action.accept((EntityType<?>)v);
            this.etf$TAILresetEntity();
        };
    }

    @Unique
    private void etf$HEADalterEntity(T playerEntity, CompoundTag nbtCompound) {
        if (nbtCompound != null) {
            Object t;
            this.etf$heldEntity = ETFRenderContext.getCurrentEntityState();
            Optional optionalEntity = EntityType.create((CompoundTag)nbtCompound, (Level)playerEntity.level());
            if (optionalEntity.isPresent() && (t = optionalEntity.get()) instanceof Parrot) {
                Parrot parrot = (Parrot)t;
                ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity)parrot));
            }
        }
    }

    @Unique
    private void etf$TAILresetEntity() {
        if (this.etf$heldEntity != null) {
            ETFRenderContext.setCurrentEntity(this.etf$heldEntity);
        }
        this.etf$heldEntity = null;
    }
}

