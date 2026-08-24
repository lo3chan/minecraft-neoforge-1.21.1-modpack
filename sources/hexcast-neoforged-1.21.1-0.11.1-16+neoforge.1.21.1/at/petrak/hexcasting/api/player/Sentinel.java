package at.petrak.hexcasting.api.player;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record Sentinel(boolean extendsRange, Vec3 position, ResourceKey<Level> dimension) {
}
