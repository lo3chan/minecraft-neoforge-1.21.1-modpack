package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.Tooltip;
import gg.cloaks.javaclient.ApiException;
import java.util.List;
import java.util.concurrent.CompletionException;
import net.minecraft.client.Minecraft;

public final class ConfirmRemoveOutfitScreen extends AbstractConfirmScreen {
   private final String outfitId;
   private final String outfitName;

   public ConfirmRemoveOutfitScreen(String outfitId, String outfitName) {
      super(Text.translatable("screens.cosmetica.confirmDeletion", new String[0]));
      this.outfitId = outfitId;
      this.outfitName = outfitName;
   }

   @Override
   protected Label createConfirmLabel() {
      return new Label(Text.translatable("label.cosmetica.confirmDelete", new String[]{this.outfitName}));
   }

   @Override
   protected Tooltip getUpdatingTooltip() {
      return new Tooltip(Text.translatable("tooltip.cosmetica.updatingOutfit", new String[0]));
   }

   @Override
   protected void onConfirm() {
      this.setting.set(true);
      CosmeticaAPI.outfits().requestAsync(api -> {
         api.delete(this.outfitId);
         return (Void)null;
      }).thenAcceptAsync(none -> {
         ((List)Cosmetica.OWN_OUTFITS.peek()).removeIf(o -> this.outfitId.equals(o.id));
         Cosmetica.OWN_OUTFITS.set((List)Cosmetica.OWN_OUTFITS.peek());
         Screens.closeCurrentScreen();
      }, Minecraft.getInstance()).exceptionally(err -> {
         if (err instanceof CompletionException) {
            err = err.getCause();
         }

         if (err instanceof ApiException) {
            int code = ((ApiException)err).getCode();
            if (code == 404) {
               Cosmetica.showToast(Text.translatable("toast.cosmetica.outfit404", new String[0]), null);
               Minecraft.getInstance().execute(Screens::closeCurrentScreen);
            } else {
               Logging.getInstance().error("Error deleting outfit", new Object[]{code});
               Minecraft.getInstance().execute(() -> this.setting.set(false));
               Cosmetica.showToast(Text.translatable("toast.cosmetica.outfitDeleteError", new String[0]), Text.literal("Error code " + code));
            }
         } else {
            Logging.getInstance().error("Failed to remove outfit", err);
            Cosmetica.showToast(Text.translatable("toast.cosmetica.outfitDeleteError", new String[0]), Text.literal(err.getClass().getSimpleName()));
         }

         return null;
      });
   }
}
