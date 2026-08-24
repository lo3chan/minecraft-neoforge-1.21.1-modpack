package cc.cosmetica.cosmetica.gui.player;

import cc.cosmetica.core.api.Accessory;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.Accessory.Flag;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.core.mixin.PlayerModelAccessor;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.GUIPlayer.Attachment;
import cc.cosmetica.kupe.api.gui.GUIPlayer.CapeProperties;
import cc.cosmetica.kupe.api.gui.GUIPlayer.Posture;
import cc.cosmetica.kupe.impl.KupeScreen;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class AccessoriesAttachment implements Attachment<Collection<Accessory>> {
   public static final AccessoriesAttachment INSTANCE = new AccessoriesAttachment();

   private AccessoriesAttachment() {
   }

   public void render(
      GUIPlayer component,
      PlayerModel playerModel,
      Posture posture,
      Canvas canvas,
      Collection<Accessory> configuration,
      Quaternionf cameraOrientation,
      MultiBufferSource bufferSource,
      int packedLight
   ) {
      boolean elytra = false;
      boolean cloak = false;
      Iterator<Attachment<?>> attachments = component.getRenderingAttachments();

      while (attachments.hasNext()) {
         Attachment<?> attachment = attachments.next();
         if (attachment == GUIPlayer.ELYTRA) {
            elytra = true;
         }

         if (attachment == GUIPlayer.CAPE) {
            cloak = true;
         }
      }

      CapeProperties cape = (CapeProperties)component.getConfiguration(GUIPlayer.CAPE);

      for (Accessory accessory : configuration) {
         if (!(Minecraft.getInstance().screen instanceof KupeScreen)
            || (!cloak || cape == null || !cape.getTexture().isPresent() || !accessory.getFlags().contains(Flag.HIDE_WITH_CLOAK))
               && (!elytra || !accessory.getFlags().contains(Flag.HIDE_WITH_ELYTRA))) {
            ModelPart part = null;
            float additionalXOffset = 0.0F;
            switch (accessory.getAttachment()) {
               case HEAD:
                  part = playerModel.head;
                  break;
               case BODY:
                  part = playerModel.body;
                  break;
               case LEFT_ARM:
                  part = accessory.isMirrored() ? playerModel.rightArm : playerModel.leftArm;
                  if (((PlayerModelAccessor)playerModel).isSlim()) {
                     additionalXOffset += 0.03125F;
                  }
                  break;
               case RIGHT_ARM:
                  part = accessory.isMirrored() ? playerModel.leftArm : playerModel.rightArm;
                  if (((PlayerModelAccessor)playerModel).isSlim()) {
                     additionalXOffset -= 0.03125F;
                  }
                  break;
               case LEFT_LEG:
                  part = accessory.isMirrored() ? playerModel.rightLeg : playerModel.leftLeg;
                  break;
               case RIGHT_LEG:
                  part = accessory.isMirrored() ? playerModel.leftLeg : playerModel.rightLeg;
                  break;
               case UNKNOWN_DEFAULT_OPEN_API:
                  Logging.getInstance()
                     .warnOnce("attachment_unknown_accessory_gui", "Unknown attachment for accessory (GUI player): {}", new Object[]{accessory.getName()});
                  continue;
            }

            Vec3 offset = accessory.getOffset();
            if (part.visible) {
               accessory.getModel()
                  .renderOnPart(
                     part,
                     canvas.getStack().getMinecraftStack(),
                     bufferSource,
                     packedLight,
                     (float)offset.x + additionalXOffset,
                     (float)offset.y,
                     (float)offset.z,
                     accessory.isMirrored()
                  );
            }
         }
      }
   }

   public Collection<Accessory> getDynamicConfiguration(UUID uuid) {
      Optional<Cosmetics> cosmetics = CosmeticaCapeProvider.getCosmetics(uuid);
      return cosmetics.<Collection<Accessory>>map(Cosmetics::getAccessories).orElse(null);
   }
}
