package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SpinyShellArmorDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.spiny_shell_armor_helmet.description_0").getString()
            + "\n\n"
            + Component.translatable("item.born_in_chaos_v1.dark_metal_armor_helmet.description_0").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.spiny_shell_armor_helmet.description_1").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
