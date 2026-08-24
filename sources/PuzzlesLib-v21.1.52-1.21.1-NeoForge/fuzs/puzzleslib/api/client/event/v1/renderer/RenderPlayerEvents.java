package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public final class RenderPlayerEvents {
   public static final EventInvoker<RenderPlayerEvents.Before> BEFORE = EventInvoker.lookup(RenderPlayerEvents.Before.class);
   public static final EventInvoker<RenderPlayerEvents.After> AFTER = EventInvoker.lookup(RenderPlayerEvents.After.class);

   private RenderPlayerEvents() {
   }

   @FunctionalInterface
   public interface After {
      void onAfterRenderPlayer(AbstractClientPlayer var1, PlayerRenderer var2, float var3, PoseStack var4, MultiBufferSource var5, int var6);
   }

   @FunctionalInterface
   public interface Before {
      EventResult onBeforeRenderPlayer(AbstractClientPlayer var1, PlayerRenderer var2, float var3, PoseStack var4, MultiBufferSource var5, int var6);
   }
}
