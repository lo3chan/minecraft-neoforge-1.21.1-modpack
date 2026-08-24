package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.Border.BorderConfig;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class GuiUtils {
   public static final int NORMAL_COLOUR = 8750469;
   public static final int HIGHLIGHT_COLOUR = 10592673;
   public static final int SHADE_COLOUR = 5855577;
   public static final Optional<Border> POPOUT_BORDER = Border.create(BorderConfig.split(1, 10592673, 5855577));
   public static final Optional<Border> POP_IN_BORDER = Border.create(BorderConfig.split(1, 5855577, 10592673));
   public static final Optional<Border> SHADE_POPOUT_BORDER = Border.create(BorderConfig.split(1, 8750469, 3421236));

   private GuiUtils() {
   }

   public static void playClick() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }
}
