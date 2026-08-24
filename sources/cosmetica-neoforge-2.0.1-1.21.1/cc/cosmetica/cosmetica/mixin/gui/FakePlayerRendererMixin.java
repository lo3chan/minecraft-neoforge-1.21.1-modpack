package cc.cosmetica.cosmetica.mixin.gui;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.impl.NametagRenderer;
import cc.cosmetica.cosmetica.gui.widget.RotatableGUIPlayer;
import cc.cosmetica.cosmetica.util.NametagUtil;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.GUIPlayer.Nametag;
import cc.cosmetica.kupe.impl.fakeplayer.FakePlayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FakePlayerRenderer.class})
public class FakePlayerRendererMixin {
   @Shadow
   public List<Nametag> nametags;
   @Unique
   @Nullable
   private CachedImage cosmetica$icon0 = null;
   @Unique
   private boolean cosmetica$iconTransparent = false;
   @Unique
   @Nullable
   private CachedImage cosmetica$icon1 = null;

   @Inject(
      at = {@At("HEAD")},
      method = {"drawLivingEntity(Lcc/cosmetica/kupe/api/gui/GUIPlayer;Lcc/cosmetica/kupe/api/Context;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      remap = false
   )
   private void onDrawLiving(
      GUIPlayer player, Context context, float rotation, float delta, PoseStack stack, MultiBufferSource bufferSource, int light, CallbackInfo ci
   ) {
      if (player instanceof RotatableGUIPlayer) {
         this.cosmetica$icon0 = ((RotatableGUIPlayer)player).icon;
         this.cosmetica$iconTransparent = ((RotatableGUIPlayer)player).hasTransparentIcon();
         this.cosmetica$icon1 = ((RotatableGUIPlayer)player).loreIcon;
      }
   }

   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
         ordinal = 1,
         shift = Shift.AFTER
      )},
      method = {"drawLivingEntity(Lcc/cosmetica/kupe/api/gui/GUIPlayer;Lcc/cosmetica/kupe/api/Context;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}
   )
   private void onStartDrawNametag(
      GUIPlayer player, Context context, float rotation, float delta, PoseStack stack, MultiBufferSource bufferSource, int light, CallbackInfo ci
   ) {
      int nametags = this.nametags.size();
      if (nametags > 1) {
         Text lore = this.nametags.get(1).text;
         if (lore.isEmpty()) {
            nametags--;
         }
      }

      NametagUtil.shiftNametags(stack, player, nametags);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderNametag(Lcc/cosmetica/kupe/api/gui/GUIPlayer$Nametag;Lcc/cosmetica/kupe/api/Canvas;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      remap = false
   )
   private void onRenderNametag(Nametag nametag, Canvas canvas, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
      if (nametag == this.nametags.get(0)) {
         if (this.cosmetica$icon0 != null) {
            NametagRenderer.prepareIcon(this.cosmetica$icon0, 2, this.cosmetica$iconTransparent, true);
         }
      } else if (this.nametags.size() > 1 && nametag == this.nametags.get(1) && this.cosmetica$icon1 != null) {
         NametagRenderer.prepareIcon(this.cosmetica$icon1, 2, false, true);
      }
   }
}
