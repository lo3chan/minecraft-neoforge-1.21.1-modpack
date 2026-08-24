package net.blay09.mods.balm.api.command;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo.Template;
import net.minecraft.resources.ResourceLocation;

public interface BalmArgumentTypeRegistrar {
   <A extends ArgumentType<?>, T extends Template<A>> void register(ResourceLocation var1, Class<A> var2, ArgumentTypeInfo<A, T> var3);

   <A extends ArgumentType<?>, T extends Template<A>> void register(String var1, Class<A> var2, ArgumentTypeInfo<A, T> var3);
}
