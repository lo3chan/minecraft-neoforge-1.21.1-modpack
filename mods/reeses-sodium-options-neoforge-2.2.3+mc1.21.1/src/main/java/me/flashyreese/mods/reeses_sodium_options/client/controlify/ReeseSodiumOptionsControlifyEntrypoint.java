/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.isxander.controlify.api.ControlifyApi
 *  dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint
 *  dev.isxander.controlify.api.entrypoint.InitContext
 *  dev.isxander.controlify.api.entrypoint.PreInitContext
 *  dev.isxander.controlify.screenop.ComponentProcessorProvider
 *  dev.isxander.controlify.screenop.ScreenProcessorProvider
 */
package me.flashyreese.mods.reeses_sodium_options.client.controlify;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.isxander.controlify.screenop.ComponentProcessorProvider;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import me.flashyreese.mods.reeses_sodium_options.client.controlify.RsoOptionsScreenProcessor;
import me.flashyreese.mods.reeses_sodium_options.client.controlify.RsoSearchTextFieldProcessor;
import me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen;
import me.flashyreese.mods.reeses_sodium_options.client.gui.search.SearchTextFieldWidget;

public final class ReeseSodiumOptionsControlifyEntrypoint
implements ControlifyEntrypoint {
    public void onControlifyPreInit(PreInitContext context) {
        ScreenProcessorProvider.registerProvider(SodiumVideoOptionsScreen.class, RsoOptionsScreenProcessor::new);
        ComponentProcessorProvider.REGISTRY.register(SearchTextFieldWidget.class, RsoSearchTextFieldProcessor::new);
    }

    public void onControlifyInit(InitContext context) {
    }

    public void onControllersDiscovered(ControlifyApi controlify) {
    }
}

