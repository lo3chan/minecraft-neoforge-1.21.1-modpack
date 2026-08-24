package com.teamresourceful.resourcefulconfig.api.client;

import net.minecraft.client.gui.components.AbstractWidget;

@FunctionalInterface
public interface ModalWidgetConstructor {
   AbstractWidget construct(int var1, int var2, int var3, int var4);
}
