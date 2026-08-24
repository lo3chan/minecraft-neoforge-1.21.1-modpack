package net.cibernet.alchemancy.events.handler.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.TintedProperty;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber({Dist.CLIENT})
public class ClientEventHandler {
   @SubscribeEvent
   public static void onClientTick(Pre event) {
      if (Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused() && !Minecraft.getInstance().isLocalServer()) {
         AlchemancyServerData.tickGlobalTimer();
      }
   }

   @SubscribeEvent
   public static void onRenderHand(RenderHandEvent event) {
      Player player = Minecraft.getInstance().player;
      if (player != null
         && !player.getUseItem().canPerformAction(ItemAbilities.SHIELD_BLOCK)
         && InfusedPropertiesHelper.hasProperty(player.getUseItem(), AlchemancyProperties.SHIELDING)
         && player.getUsedItemHand() == event.getHand()) {
         PoseStack poseStack = event.getPoseStack();
         int sign = event.getHand() == (player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND) ? 1 : -1;
         poseStack.translate(-sign * 0.25F, 0.4F, -0.25F);
         poseStack.mulPose(Axis.YP.rotationDegrees(sign * 75.0F));
         poseStack.mulPose(Axis.XP.rotationDegrees(270.0F));
      }
   }

   @SubscribeEvent
   public static void onRenderArm(RenderArmEvent event) {
      AbstractClientPlayer player = event.getPlayer();
      if (player.hasData(AlchemancyDataAttachments.ENTITY_TINT.get())) {
         List<Integer> tint = (List<Integer>)player.getData(AlchemancyDataAttachments.ENTITY_TINT.get());
         if (!tint.isEmpty()) {
            Vector3f tintVec = Vec3.fromRGB24(ColorUtils.interpolateColorsOverTime(1.0F, tint.stream().mapToInt(Integer::intValue).toArray())).toVector3f();
            RenderSystem.setShaderColor(tintVec.x(), tintVec.y(), tintVec.z(), RenderSystem.getShaderColor()[3]);
         }
      }
   }

   @SubscribeEvent
   public static void modifyFogColor(ComputeFogColor event) {
      Vector3f tint = getScreenTintColor();
      if (tint != null) {
         event.setRed(event.getRed() * tint.x());
         event.setGreen(event.getGreen() * tint.y());
         event.setBlue(event.getBlue() * tint.z());
      }
   }

   public static boolean modifySkyColor(float red, float green, float blue, float alpha, Operation<Void> original) {
      Vector3f tint = getScreenTintColor();
      if (tint != null) {
         original.call(new Object[]{red * tint.x(), green * tint.y(), blue * tint.z(), alpha});
         return true;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onStageRender(RenderLevelStageEvent event) {
      Vector3f tint = getScreenTintColor();
      if (tint != null) {
         float[] shaderColor = RenderSystem.getShaderColor();
         RenderSystem.setShaderColor(tint.x(), tint.y(), tint.z(), shaderColor[3]);
      }
   }

   @SubscribeEvent
   public static void onStageRender(net.neoforged.neoforge.client.event.RenderFrameEvent.Pre event) {
      Vector3f tint = getScreenTintColor();
      if (tint != null) {
         float[] shaderColor = RenderSystem.getShaderColor();
      }
   }

   @Nullable
   private static Vector3f getScreenTintColor() {
      Player player = Minecraft.getInstance().player;
      if (player != null && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
         ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
         return !InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.TINTED_LENS)
            ? null
            : Vec3.fromRGB24(
                  InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.TINTED)
                     ? ((TintedProperty)AlchemancyProperties.TINTED.get()).getColor(stack)
                     : ((Property)AlchemancyProperties.TINTED_LENS.get()).getColor(stack)
               )
               .toVector3f();
      } else {
         return null;
      }
   }
}
