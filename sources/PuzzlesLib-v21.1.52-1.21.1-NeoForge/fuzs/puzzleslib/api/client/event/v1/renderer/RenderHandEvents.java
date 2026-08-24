package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public final class RenderHandEvents {
   public static final EventInvoker<RenderHandEvents.MainHand> MAIN_HAND = EventInvoker.lookup(RenderHandEvents.MainHand.class);
   public static final EventInvoker<RenderHandEvents.OffHand> OFF_HAND = EventInvoker.lookup(RenderHandEvents.OffHand.class);
   public static final EventInvoker<RenderHandEvents.MainHand> BOTH = (phase, callback) -> {
      MAIN_HAND.register(phase, callback);
      OFF_HAND.register(phase, callback::onRenderMainHand);
   };

   private RenderHandEvents() {
   }

   @FunctionalInterface
   public interface MainHand {
      EventResult onRenderMainHand(
         ItemInHandRenderer var1,
         AbstractClientPlayer var2,
         HumanoidArm var3,
         ItemStack var4,
         PoseStack var5,
         MultiBufferSource var6,
         int var7,
         float var8,
         float var9,
         float var10,
         float var11
      );
   }

   @FunctionalInterface
   public interface OffHand {
      EventResult onRenderOffHand(
         ItemInHandRenderer var1,
         AbstractClientPlayer var2,
         HumanoidArm var3,
         ItemStack var4,
         PoseStack var5,
         MultiBufferSource var6,
         int var7,
         float var8,
         float var9,
         float var10,
         float var11
      );
   }
}
