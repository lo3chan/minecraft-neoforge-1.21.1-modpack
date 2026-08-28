/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.tr7zw.notenoughanimations.mixins;

import dev.tr7zw.notenoughanimations.NEAnimationsLoader;
import dev.tr7zw.notenoughanimations.access.PlayerData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerModel.class})
public abstract class PlayerEntityModelMixin<T extends LivingEntity>
extends HumanoidModel<T> {
    @Unique
    private static final String SETUP_ANIM_METHOD = "setupAnim";

    public PlayerEntityModelMixin() {
        super(null);
    }

    @Inject(method={"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="HEAD")})
    public void setupAnimHEAD(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        PlayerModel model = (PlayerModel)this;
        if (!(livingEntity instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)livingEntity;
        NEAnimationsLoader.INSTANCE.playerTransformer.preUpdate(player, model, limbSwing, info);
    }

    @Inject(method={"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V", ordinal=0)})
    public void setupAnim(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        PlayerModel model = (PlayerModel)this;
        if (!(livingEntity instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)livingEntity;
        NEAnimationsLoader.INSTANCE.playerTransformer.updateModel(player, model, limbSwing, info);
    }

    @Inject(method={"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="RETURN")})
    public void setupAnimEnd(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        if (!(livingEntity instanceof PlayerData)) {
            return;
        }
        PlayerData data = (PlayerData)livingEntity;
        AbstractClientPlayer player = (AbstractClientPlayer)livingEntity;
        if (data.getPoseOverwrite() != null) {
            player.setPose(data.getPoseOverwrite());
            data.setPoseOverwrite(null);
        }
    }
}

