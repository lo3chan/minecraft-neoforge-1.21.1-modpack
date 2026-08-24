package de.cristelknight.cristellib.config.client.structure;

import de.cristelknight.cristellib.StructureConfig;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public record ClientStructureConfig(
   StructureConfig structureConfig, Map<ResourceLocation, ClientPlacementConfig> clientPlacementConfigs, Map<ResourceLocation, ClientEDConfig> clientEDConfigs
) {
}
