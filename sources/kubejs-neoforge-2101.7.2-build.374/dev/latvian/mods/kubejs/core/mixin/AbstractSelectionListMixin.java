package dev.latvian.mods.kubejs.core.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractSelectionList.Entry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({AbstractSelectionList.class})
public abstract class AbstractSelectionListMixin<E extends Entry<E>> {
}
