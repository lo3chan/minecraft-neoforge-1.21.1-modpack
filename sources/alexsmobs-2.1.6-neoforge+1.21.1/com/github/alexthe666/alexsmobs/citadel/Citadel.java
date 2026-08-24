package com.github.alexthe666.alexsmobs.citadel;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Citadel {
   public static final Logger LOGGER = LogManager.getLogger("alexsmobs-citadel");
   public static final CitadelProxy PROXY = FMLEnvironment.dist.isClient() ? (CitadelClientProxy::new).get() : new CitadelProxy();

   public static <MSG> void sendMSGToServer(MSG message) {
      AlexsMobs.sendMSGToServer(message);
   }

   public static <MSG> void sendMSGToAll(MSG message) {
      AlexsMobs.sendMSGToAll(message);
   }

   public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
      AlexsMobs.sendNonLocal(msg, player);
   }
}
