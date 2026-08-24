package dev.architectury.registry.forge;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.Nullable;

public class ReloadListenerRegistryImpl {
   private static List<PreparableReloadListener> serverDataReloadListeners = Lists.newArrayList();

   public static void register(
      PackType type, PreparableReloadListener listener, @Nullable ResourceLocation listenerId, Collection<ResourceLocation> dependencies
   ) {
      if (type == PackType.SERVER_DATA) {
         serverDataReloadListeners.add(listener);
      } else if (type == PackType.CLIENT_RESOURCES) {
         registerClient(listener);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void registerClient(PreparableReloadListener listener) {
      ((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
   }

   public static void addReloadListeners(AddReloadListenerEvent event) {
      for (PreparableReloadListener listener : serverDataReloadListeners) {
         event.addListener(listener);
      }
   }

   static {
      NeoForge.EVENT_BUS.addListener(ReloadListenerRegistryImpl::addReloadListeners);
   }
}
