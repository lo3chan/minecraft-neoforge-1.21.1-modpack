package at.petrak.hexcasting.common.misc;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record PatternTooltip(HexPattern pattern, ResourceLocation background) implements TooltipComponent {
}
