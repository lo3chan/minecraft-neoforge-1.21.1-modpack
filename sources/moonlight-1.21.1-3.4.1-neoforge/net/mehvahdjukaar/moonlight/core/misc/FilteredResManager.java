package net.mehvahdjukaar.moonlight.core.misc;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.IEditablePackResources;
import net.mehvahdjukaar.moonlight.core.misc.platform.FilteredResManagerImpl;
import net.mehvahdjukaar.moonlight.core.pack.MergedDynamicClientResourcesProvider;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

public class FilteredResManager extends MultiPackResourceManager {
   public FilteredResManager(PackType packType, List<PackResources> list) {
      super(packType, list);
   }

   public static FilteredResManager including(ResourceManager original, PackType packType, String... packs) {
      return including(original, packType, p -> Set.of(packs).contains(p.packId()));
   }

   public static FilteredResManager including(ResourceManager original, PackType packType, Predicate<PackResources> predicate) {
      List<PackResources> list = original.listPacks().toList().stream().filter(predicate).toList();
      return new FilteredResManager(packType, list);
   }

   public static FilteredResManager excluding(ResourceManager original, PackType packType, String... packs) {
      return excluding(original, packType, p -> Set.of(packs).contains(p.packId()));
   }

   public static FilteredResManager excluding(ResourceManager original, PackType packType, Predicate<PackResources> predicate) {
      List<PackResources> list = original.listPacks().toList().stream().filter(p -> !predicate.test(p)).toList();
      return new FilteredResManager(packType, list);
   }

   public static FilteredResManager including(PackRepository original, PackType packType, String... packs) {
      Set<String> whitelist = Set.of(packs);
      List<PackResources> list = original.getAvailablePacks().stream().filter(p -> whitelist.contains(p.getId())).<PackResources>map(Pack::open).toList();
      return new FilteredResManager(packType, list);
   }

   public static FilteredResManager excluding(PackRepository original, PackType packType, String... packs) {
      Set<String> blacklist = Set.of(packs);
      List<PackResources> list = original.getAvailablePacks().stream().filter(p -> !blacklist.contains(p.getId())).<PackResources>map(Pack::open).toList();
      return new FilteredResManager(packType, list);
   }

   public static ResourceManager vanilla(ResourceManager manager, PackType type) {
      return including(manager, type, p -> {
         String id = p.packId();
         if (id.equals("vanilla")) {
            return true;
         } else if (isDynamicPackResource(p)) {
            return false;
         } else {
            return isModResourcePack(p) ? true : p.location().source() != PackSource.DEFAULT;
         }
      });
   }

   public static boolean isDynamicPackResource(PackResources p) {
      return p instanceof IEditablePackResources || PlatHelper.getPhysicalSide().isClient() && p instanceof MergedDynamicClientResourcesProvider;
   }

   public static boolean isModResourcePack(PackResources var0) {
      return FilteredResManagerImpl.isModResourcePack(var0);
   }
}
