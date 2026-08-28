/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.state;

import java.util.List;
import java.util.Set;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionLayoutState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionUiState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.SearchResultEntry;
import net.minecraft.resources.ResourceLocation;

public interface OptionStateStore {
    public OptionUiState optionUiState(ResourceLocation var1);

    public OptionLayoutState optionLayoutState(ResourceLocation var1);

    public boolean searchActive();

    public List<SearchResultEntry> searchResults();

    public Set<ResourceLocation> collapsedOptionGroups();
}

