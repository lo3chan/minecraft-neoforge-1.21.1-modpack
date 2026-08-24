package me.flashyreese.mods.reeses_sodium_options.client.gui.state;

import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public interface OptionStateStore {
   OptionUiState optionUiState(ResourceLocation var1);

   OptionLayoutState optionLayoutState(ResourceLocation var1);

   boolean searchActive();

   List<SearchResultEntry> searchResults();

   Set<ResourceLocation> collapsedOptionGroups();
}
