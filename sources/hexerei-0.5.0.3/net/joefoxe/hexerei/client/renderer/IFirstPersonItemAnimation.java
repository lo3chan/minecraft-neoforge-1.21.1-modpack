package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IFirstPersonItemAnimation {
   void animateItemFirstPerson(LivingEntity var1, ItemStack var2, InteractionHand var3, PoseStack var4, float var5, float var6, float var7, float var8);
}
