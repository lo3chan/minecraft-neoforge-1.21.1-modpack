package net.blay09.mods.balm.api.permission;

import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface BalmPermissions {
   void registerPermission(ResourceLocation var1, Function<PermissionContext, Boolean> var2);

   boolean hasPermission(ServerPlayer var1, ResourceLocation var2);

   boolean hasPermission(CommandSourceStack var1, ResourceLocation var2);
}
