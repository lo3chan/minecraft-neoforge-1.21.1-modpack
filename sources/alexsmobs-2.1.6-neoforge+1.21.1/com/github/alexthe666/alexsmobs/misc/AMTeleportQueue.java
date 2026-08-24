package com.github.alexthe666.alexsmobs.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.tuple.Triple;

public class AMTeleportQueue {
   public static final ObjectList<Triple<ServerPlayer, ServerLevel, BlockPos>> PLAYERS = new ObjectArrayList();

   private AMTeleportQueue() {
   }
}
