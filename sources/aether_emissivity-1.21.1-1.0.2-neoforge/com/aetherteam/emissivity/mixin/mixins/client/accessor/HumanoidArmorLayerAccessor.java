package com.aetherteam.emissivity.mixin.mixins.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({HumanoidArmorLayer.class})
public interface HumanoidArmorLayerAccessor<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
   @Invoker
   void callRenderArmorPiece(PoseStack var1, MultiBufferSource var2, T var3, EquipmentSlot var4, int var5, A var6);

   @Invoker
   A callGetArmorModel(EquipmentSlot var1);
}
