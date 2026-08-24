package com.blamejared.clumps;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent.PickupXp;

@Mod("clumps")
public class Clumps {
   public Clumps() {
      ClumpsCommon.pickupXPEvent = (player, experienceOrb) -> ((PickupXp)NeoForge.EVENT_BUS.post(new PickupXp(player, experienceOrb))).isCanceled();
   }
}
