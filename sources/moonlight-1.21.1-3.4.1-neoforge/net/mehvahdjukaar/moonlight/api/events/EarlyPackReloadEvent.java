package net.mehvahdjukaar.moonlight.api.events;

import java.util.Collection;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public record EarlyPackReloadEvent(Collection<PackResources> selectedPacks, ResourceManager manager, PackType type, IProgressTracker progress)
   implements SimpleEvent {
}
