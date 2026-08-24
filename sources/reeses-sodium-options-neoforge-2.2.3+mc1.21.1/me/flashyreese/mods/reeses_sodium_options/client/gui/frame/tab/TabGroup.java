package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab;

import java.util.List;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;

record TabGroup(String id, ModOptions modOptions, List<Tab<?>> tabs) {
}
