package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface CustomTabProvider {
   Tab createTab(YACLScreen var1, ScreenRectangle var2);
}
