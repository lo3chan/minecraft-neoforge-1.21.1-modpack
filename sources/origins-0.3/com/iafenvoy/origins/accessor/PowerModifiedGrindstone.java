package com.iafenvoy.origins.accessor;

import com.iafenvoy.origins.data.power.builtin.modify.ModifyGrindstonePower;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface PowerModifiedGrindstone {
   List<ModifyGrindstonePower> origins$getAppliedPowers();

   Player origins$getPlayer();

   @Nullable
   BlockPos origins$getPos();
}
