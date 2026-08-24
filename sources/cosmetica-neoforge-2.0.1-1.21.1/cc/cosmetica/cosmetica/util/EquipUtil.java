package cc.cosmetica.cosmetica.util;

import cc.cosmetica.core.api.Accessory;
import cc.cosmetica.core.api.Accessory.Flag;
import gg.cloaks.javaclient.model.CreateOutfitAccessoryDto;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

public final class EquipUtil {
   private EquipUtil() {
   }

   public static CreateOutfitAccessoryDto dtoFromAccessory(Accessory accessory) {
      CreateOutfitAccessoryDto caod = new CreateOutfitAccessoryDto();
      caod.setId(accessory.getId());
      caod.setMirrored(accessory.isMirrored());
      caod.setOffset(
         Arrays.asList(BigDecimal.valueOf(accessory.getOffset().x), BigDecimal.valueOf(accessory.getOffset().y), BigDecimal.valueOf(accessory.getOffset().z))
      );
      caod.setFlags(accessory.getFlags() == accessory.getDefaultFlags() ? -1 : packFlags(accessory.getFlags()));
      return caod;
   }

   private static int packFlags(Collection<Flag> flags) {
      int response = 0;

      for (Flag flag : flags) {
         response |= maskOf(flag);
      }

      return response;
   }

   private static int maskOf(Flag flag) {
      switch (flag) {
         case HIDE_WITH_HELMET:
            return 1;
         case HIDE_WITH_CHESTPLATE:
            return 2;
         case HIDE_WITH_LEGGINGS:
            return 4;
         case HIDE_WITH_BOOTS:
            return 8;
         case HIDE_WITH_CLOAK:
            return 16;
         case HIDE_WITH_ELYTRA:
            return 32;
         case HIDE_WITH_PARROT:
            return 64;
         default:
            return 0;
      }
   }
}
