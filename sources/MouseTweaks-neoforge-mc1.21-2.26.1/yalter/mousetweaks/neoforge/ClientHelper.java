package yalter.mousetweaks.neoforge;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import yalter.mousetweaks.ConfigScreen;

public class ClientHelper implements IConfigScreenFactory {
   public Screen createScreen(ModContainer container, Screen modListScreen) {
      return new ConfigScreen(modListScreen);
   }
}
