package net.blay09.mods.balm.api.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import java.util.function.Consumer;
import net.minecraft.commands.SharedSuggestionProvider;

public interface BalmClientCommands {
   void register(Consumer<CommandDispatcher<SharedSuggestionProvider>> var1);
}
