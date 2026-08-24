package com.blamejared.clumps;

import java.util.function.BiPredicate;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClumpsCommon {
   public static final Logger LOG = LogManager.getLogger("clumps");
   public static BiPredicate<Player, ExperienceOrb> pickupXPEvent = (player, experienceOrb) -> false;
}
