package com.aetherteam.cumulus.mixin.mixins.common.accessor;

import java.util.UUID;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.server.LanServerPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({IntegratedServer.class})
public interface IntegratedServerAccessor {
   @Accessor("publishedPort")
   void cumulus$setPublishedPort(int var1);

   @Accessor("lanPinger")
   LanServerPinger cumulus$getLanPinger();

   @Accessor("lanPinger")
   void cumulus$setLanPinger(LanServerPinger var1);

   @Accessor("uuid")
   UUID cumulus$getUUID();
}
