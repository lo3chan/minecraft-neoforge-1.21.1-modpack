package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface AddInformationKubeEvent extends KubeEvent {
   void add(Context cx, Object filter, List<Component> info);
}
