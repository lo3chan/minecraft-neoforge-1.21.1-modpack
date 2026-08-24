package net.blay09.mods.balm.api.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.blay09.mods.balm.api.Balm;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

public interface BalmCommands {
   static void registerPermission(ResourceLocation permission, int permissionLevel) {
      Balm.getPermissions().registerPermission(permission, context -> context.getCommandSource().map(it -> it.hasPermission(permissionLevel)).orElse(false));
   }

   static Predicate<CommandSourceStack> requirePermission(ResourceLocation permission) {
      return source -> Balm.getPermissions().hasPermission(source, permission);
   }

   static Predicate<CommandSourceStack> requireAnyPermission(ResourceLocation... permissions) {
      return source -> Arrays.stream(permissions).anyMatch(it -> Balm.getPermissions().hasPermission(source, it));
   }

   static Predicate<CommandSourceStack> requireAllPermissions(ResourceLocation... permissions) {
      return source -> Arrays.stream(permissions).allMatch(it -> Balm.getPermissions().hasPermission(source, it));
   }

   void register(Consumer<CommandDispatcher<CommandSourceStack>> var1);
}
