package at.petrak.hexcasting.api.casting;

import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexPattern;

public record ActionRegistryEntry(HexPattern prototype, Action action) {
}
