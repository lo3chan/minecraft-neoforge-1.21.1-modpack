package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.Accessory;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.util.EquipUtil;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.Tooltip;
import gg.cloaks.javaclient.ApiException;
import gg.cloaks.javaclient.model.CreateOutfitAccessoryDto;
import gg.cloaks.javaclient.model.CreateOutfitDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import net.minecraft.client.Minecraft;

public final class ConfirmRemoveCosmeticScreen extends AbstractConfirmScreen {
   private final Cosmetics parentOutfit;
   private final String outfitId;
   private final String removedItemId;
   private final String removedItemName;
   private final boolean removedItemMirrored;

   public ConfirmRemoveCosmeticScreen(Cosmetics parentOutfit, String itemId, String itemName, boolean mirrored) {
      super(Text.translatable("screens.cosmetica.confirmDeletion", new String[0]));
      Objects.requireNonNull((String)parentOutfit.getOutfitId().orElse(null), "Cosmetics must represent an outfit to allow removals.");
      this.parentOutfit = parentOutfit;
      this.outfitId = (String)parentOutfit.getOutfitId().get();
      this.removedItemId = itemId;
      this.removedItemName = itemName;
      this.removedItemMirrored = mirrored;
   }

   @Override
   protected Label createConfirmLabel() {
      return new Label(Text.translatable("label.cosmetica.confirmRemove", new String[]{this.removedItemName, (String)this.parentOutfit.getOutfitName().get()}));
   }

   @Override
   protected Tooltip getUpdatingTooltip() {
      return new Tooltip(Text.translatable("tooltip.cosmetica.updatingOutfit", new String[0]));
   }

   @Override
   protected void onConfirm() {
      CreateOutfitDto dto = new CreateOutfitDto();
      boolean alreadyFoundItem = false;
      if (this.parentOutfit.getCloak().isPresent()) {
         String cloak = ((ImageCosmetic)this.parentOutfit.getCloak().get()).getId();
         if (cloak.equals(this.removedItemId)) {
            dto.setCloak(null);
            alreadyFoundItem = true;
         }
      }

      if (this.parentOutfit.getElytra().isPresent()) {
         String elytra = ((ImageCosmetic)this.parentOutfit.getElytra().get()).getId();
         if (elytra.equals(this.removedItemId)) {
            dto.setElytra(null);
            alreadyFoundItem = true;
         }
      }

      if (!alreadyFoundItem) {
         List<CreateOutfitAccessoryDto> accessories = new ArrayList<>();

         for (Accessory accessory : this.parentOutfit.getAccessories()) {
            if (!accessory.getId().equals(this.removedItemId) || accessory.isMirrored() != this.removedItemMirrored) {
               CreateOutfitAccessoryDto caod = EquipUtil.dtoFromAccessory(accessory);
               accessories.add(caod);
            }
         }

         dto.setAccessories(accessories);
      }

      this.setting.set(true);
      CosmeticaAPI.outfits()
         .requestAsync(api -> api.modify(this.outfitId, dto))
         .thenAcceptAsync(o -> {
            Cosmetica.updateOwnCosmetics(o);
            Screens.closeCurrentScreen();
         }, Minecraft.getInstance())
         .exceptionally(
            Cosmetica.mainThreadExcept(
               ex -> {
                  Logging.getInstance().error("Error updating outfit {}", ex, new Object[]{this.outfitId});
                  this.setting.set(false);
                  if (ex instanceof CompletionException) {
                     ex = ex.getCause();
                  }

                  if (ex instanceof ApiException) {
                     Cosmetica.showToast(
                        Text.translatable("toast.cosmetica.outfitUpdateError", new String[0]), Text.literal("Error code " + ((ApiException)ex).getCode())
                     );
                  } else {
                     Cosmetica.showToast(Text.translatable("toast.cosmetica.outfitUpdateError", new String[0]), Text.literal(ex.getClass().getSimpleName()));
                  }
               }
            )
         );
   }
}
