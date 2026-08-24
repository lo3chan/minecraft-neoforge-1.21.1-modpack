package at.petrak.hexcasting.interop;

import at.petrak.hexcasting.interop.pehkui.PehkuiInterop;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import at.petrak.hexcasting.xplat.Platform;
import java.util.List;
import vazkii.patchouli.api.PatchouliAPI;

public class HexInterop {
   public static final String PATCHOULI_ANY_INTEROP_FLAG = "hexcasting:any_interop";
   public static final String PEHKUI_ID = "pehkui";

   public static void init() {
      initPatchouli();
      IXplatAbstractions xplat = IXplatAbstractions.INSTANCE;
      if (xplat.isModPresent("pehkui")) {
         PehkuiInterop.init();
      }

      xplat.initPlatformSpecific();
   }

   public static void clientInit() {
      IClientXplatAbstractions.INSTANCE.initPlatformSpecific();
   }

   private static void initPatchouli() {
      List<String> integrations = List.of("pehkui");
      boolean anyInterop = false;

      for (String id : integrations) {
         if (IXplatAbstractions.INSTANCE.isModPresent(id)) {
            anyInterop = true;
            break;
         }
      }

      if (!anyInterop) {
         Platform platform = IXplatAbstractions.INSTANCE.platform();
         if (platform != Platform.FORGE) {
            throw new UnsupportedOperationException();
         }

         for (String idx : List.of()) {
            if (IXplatAbstractions.INSTANCE.isModPresent(idx)) {
               anyInterop = true;
               break;
            }
         }
      }

      if (anyInterop) {
         PatchouliAPI.get().setConfigFlag("hexcasting:any_interop", true);
      }
   }

   public static final class Forge {
      public static final String CURIOS_API_ID = "curios";
   }
}
