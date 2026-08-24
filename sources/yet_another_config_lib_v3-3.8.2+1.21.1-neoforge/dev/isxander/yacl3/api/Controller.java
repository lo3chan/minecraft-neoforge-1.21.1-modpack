package dev.isxander.yacl3.api;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;

public interface Controller<T> {
   Option<T> option();

   Component formatValue();

   AbstractWidget provideWidget(YACLScreen var1, Dimension<Integer> var2);
}
