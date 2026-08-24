package com.github.alexthe666.alexsmobs.item;

import java.util.function.Consumer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public interface IClientExtensionItem {
   void initializeClient(Consumer<IClientItemExtensions> var1);
}
