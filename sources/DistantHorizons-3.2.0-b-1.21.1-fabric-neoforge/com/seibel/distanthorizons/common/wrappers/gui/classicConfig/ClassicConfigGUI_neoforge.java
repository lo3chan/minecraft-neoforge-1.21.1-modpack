package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.Screen;

public class ClassicConfigGUI_neoforge {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final DhLogger RATE_LIMITED_LOGGER = new DhLoggerBuilder().maxCountPerSecond(1).build();
   public static final ClassicConfigGUI$ConfigCoreInterface_neoforge CONFIG_CORE_INTERFACE = new ClassicConfigGUI$ConfigCoreInterface_neoforge();
   public static final Pattern INTEGER_ONLY_REGEX = Pattern.compile("(-?[0-9]*)");
   public static final Pattern DECIMAL_ONLY_REGEX = Pattern.compile("-?([\\d]+\\.?[\\d]*|[\\d]*\\.?[\\d]+|\\.)");

   public static Screen getScreen(Screen parent, String category) {
      return new DhConfigScreen_neoforge(parent, category);
   }
}
