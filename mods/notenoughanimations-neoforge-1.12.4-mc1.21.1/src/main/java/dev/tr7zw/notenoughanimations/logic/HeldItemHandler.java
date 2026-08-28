/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.tr7zw.transition.mc.GeneralUtil
 *  dev.tr7zw.transition.mc.ItemUtil
 *  dev.tr7zw.transition.mc.MathUtil
 *  net.minecraft.client.model.ArmedModel
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.HumanoidModel$ArmPose
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.CrossbowItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ProjectileWeaponItem
 *  net.minecraft.world.item.ShieldItem
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.tr7zw.notenoughanimations.logic;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import dev.tr7zw.notenoughanimations.util.MapRenderer;
import dev.tr7zw.notenoughanimations.util.NMSWrapper;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.DataHolder;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.transition.mc.ItemUtil;
import dev.tr7zw.transition.mc.MathUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class HeldItemHandler
implements DataHolder<HeldItemState> {
    private Item filledMap = ItemUtil.getItem((ResourceLocation)GeneralUtil.getResourceLocation((String)"minecraft", (String)"filled_map"));
    private Set<Item> hideItemsForTheseBows = new HashSet<Item>();
    private Set<Item> lanternItems = new HashSet<Item>();

    public void onLoad() {
        this.hideItemsForTheseBows.clear();
        this.hideItemsForTheseBows.addAll(AnimationUtil.parseItemList(NEABaseMod.config.hideItemsForTheseBows));
        this.lanternItems.clear();
        this.lanternItems.addAll(AnimationUtil.parseItemList(NEABaseMod.config.lanternItems));
    }

    public void onRenderItem(LivingEntity entity, EntityModel<?> model, ItemStack itemStack, HumanoidArm arm, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo info) {
        AbstractClientPlayer player;
        if (entity == null) {
            return;
        }
        if (entity.isSleeping()) {
            if (NEABaseMod.config.dontHoldItemsInBed) {
                info.cancel();
            }
            return;
        }
        if (NMSWrapper.hasCustomModel(itemStack)) {
            return;
        }
        if (model instanceof ArmedModel) {
            ArmedModel armedModel = (ArmedModel)model;
            if (model instanceof HumanoidModel) {
                HumanoidModel humanoid = (HumanoidModel)model;
                if ((arm == HumanoidArm.RIGHT && humanoid.rightArm.visible || arm == HumanoidArm.LEFT && humanoid.leftArm.visible) && NEABaseMod.config.enableInWorldMapRendering) {
                    if (arm == entity.getMainArm() && entity.getMainHandItem().getItem().equals(this.filledMap)) {
                        matrices.pushPose();
                        armedModel.translateToHand(arm, matrices);
                        matrices.mulPose(MathUtil.XP.rotationDegrees(-90.0f));
                        matrices.mulPose(MathUtil.YP.rotationDegrees(205.0f));
                        matrices.mulPose(MathUtil.ZP.rotationDegrees(10.0f));
                        boolean bl = arm == HumanoidArm.LEFT;
                        matrices.translate((double)((float)(bl ? -1 : 1) / 16.0f), 0.09 + (entity.getOffhandItem().isEmpty() ? 0.15 : 0.0), -0.625);
                        MapRenderer.renderFirstPersonMap(matrices, vertexConsumers, light, itemStack, !entity.getOffhandItem().isEmpty(), entity.getMainArm() == HumanoidArm.LEFT);
                        matrices.popPose();
                        info.cancel();
                        return;
                    }
                    if (arm != entity.getMainArm() && entity.getOffhandItem().getItem().equals(this.filledMap)) {
                        matrices.pushPose();
                        armedModel.translateToHand(arm, matrices);
                        matrices.mulPose(MathUtil.XP.rotationDegrees(-90.0f));
                        matrices.mulPose(MathUtil.YP.rotationDegrees(200.0f));
                        boolean bl = arm == HumanoidArm.LEFT;
                        matrices.translate((double)((float)(bl ? -1 : 1) / 16.0f), 0.125, -0.625);
                        MapRenderer.renderFirstPersonMap(matrices, vertexConsumers, light, itemStack, true, false);
                        matrices.popPose();
                        info.cancel();
                        return;
                    }
                }
            }
        }
        if (NEABaseMod.config.enableOffhandHiding && entity instanceof AbstractClientPlayer && !((player = (AbstractClientPlayer)entity).getMainHandItem().getItem() instanceof ShieldItem)) {
            boolean mainHandProjectileWeapon = player.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
            boolean offHandProjectileWeapon = player.getOffhandItem().getItem() instanceof ProjectileWeaponItem;
            if (!mainHandProjectileWeapon) {
                mainHandProjectileWeapon = this.hideItemsForTheseBows.contains(player.getMainHandItem().getItem());
            }
            if (!offHandProjectileWeapon) {
                offHandProjectileWeapon = this.hideItemsForTheseBows.contains(player.getOffhandItem().getItem());
            }
            boolean projectileWeaponEquipped = mainHandProjectileWeapon || offHandProjectileWeapon;
            boolean mainHandCharged = AnimationUtil.isChargedCrossbow(player.getMainHandItem());
            boolean offHandCharged = AnimationUtil.isChargedCrossbow(player.getOffhandItem());
            boolean isUsingItem = player.isUsingItem();
            if (!mainHandCharged && isUsingItem) {
                boolean bl = mainHandCharged = (float)(player.getMainHandItem().getUseDuration((LivingEntity)player) - player.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration((ItemStack)player.getMainHandItem(), (LivingEntity)player) >= 1.0f;
            }
            if (!offHandCharged && isUsingItem) {
                offHandCharged = (float)(player.getOffhandItem().getUseDuration((LivingEntity)player) - player.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration((ItemStack)player.getOffhandItem(), (LivingEntity)player) >= 1.0f;
            }
            HumanoidModel.ArmPose mainHandPose = AnimationUtil.getArmPose(player, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose offHandPose = AnimationUtil.getArmPose(player, InteractionHand.OFF_HAND);
            if (!(AnimationUtil.isUsingBothHands(mainHandPose) || AnimationUtil.isUsingBothHands(offHandPose) || projectileWeaponEquipped && (mainHandCharged || offHandCharged || isUsingItem))) {
                return;
            }
            if (mainHandPose.isTwoHanded()) {
                offHandPose = player.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }
            HumanoidArm mainArm = HumanoidArm.RIGHT;
            HumanoidArm offArm = HumanoidArm.LEFT;
            if (player.getMainArm() == HumanoidArm.LEFT) {
                mainArm = HumanoidArm.LEFT;
                offArm = HumanoidArm.RIGHT;
            }
            if (arm == mainArm && AnimationUtil.isUsingBothHands(offHandPose)) {
                info.cancel();
                return;
            }
            if (arm == offArm && AnimationUtil.isUsingBothHands(mainHandPose)) {
                info.cancel();
                return;
            }
            if (!(!projectileWeaponEquipped || !mainHandCharged && !offHandCharged && !isUsingItem || (mainHandCharged || offHandCharged) && isUsingItem || AnimationUtil.isUsingBothHands(mainHandPose) || AnimationUtil.isUsingBothHands(offHandPose))) {
                if (arm == mainArm && offHandProjectileWeapon && !mainHandProjectileWeapon) {
                    info.cancel();
                    return;
                }
                if (arm == offArm && mainHandProjectileWeapon) {
                    info.cancel();
                    return;
                }
            }
        }
    }

    public static class HeldItemState {
        public int lanternLastTick = 0;
        public Vec3 lanternPos;
        public Vec3 lanternVelocity = Vec3.ZERO;
        public Vec3 lastLanternVelocity = Vec3.ZERO;
        public Vec3 smoothedHandPos = null;

        public HeldItemState(LivingEntity entity) {
            this.lanternLastTick = entity.tickCount;
            this.lanternPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        }
    }
}

