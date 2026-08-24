package net.blay09.mods.balm.world.entity;

import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public interface BalmEntityTypeRegistration<T extends Entity> extends BalmHolderRegistration<EntityType<T>> {
   BalmEntityTypeRegistration<T> withDefaultAttributes(Supplier<Builder> var1);

   BalmEntityTypeRegistration<T> withDefaultAttributes(Function<Builder, Builder> var1);

   BalmEntityTypeRegistration<T> withSpawnPlacement(SpawnPlacementType var1, Types var2, Supplier<SpawnPredicate<T>> var3);
}
