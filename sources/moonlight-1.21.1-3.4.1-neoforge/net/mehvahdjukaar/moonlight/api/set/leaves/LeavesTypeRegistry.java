package net.mehvahdjukaar.moonlight.api.set.leaves;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import org.jetbrains.annotations.Nullable;

public class LeavesTypeRegistry extends BlockTypeRegistry<LeavesType> {
   public static final LeavesTypeRegistry INSTANCE = new LeavesTypeRegistry();
   @Deprecated(
      forRemoval = true
   )
   public static LeavesType OAK_TYPE = VanillaLeavesTypes.OAK;
   private final Map<ResourceLocation, ResourceLocation> specialLeavesToWood = new HashMap<>();
   private final Map<LeavesType, WoodType> leavesToWood = new IdentityHashMap<>();

   @Deprecated(
      forRemoval = true
   )
   public static Collection<LeavesType> getTypes() {
      return INSTANCE.getValues();
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static LeavesType getValue(ResourceLocation leavesTypeId) {
      return INSTANCE.get(leavesTypeId);
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static LeavesType getValue(String leavesTypeId) {
      return INSTANCE.get(ResourceLocation.parse(leavesTypeId));
   }

   @Deprecated(
      forRemoval = true
   )
   public static LeavesType fromNBT(String name) {
      return INSTANCE.getFromNBT(name);
   }

   public LeavesTypeRegistry() {
      super(LeavesType.class, "leaves_type");
      LeavesType.CODEC = this.getCodec();
      LeavesType.STREAM_CODEC = this.getStreamCodecExplicit();
   }

   static void touch() {
   }

   protected LeavesType register(LeavesType vanillaType) {
      return super.register(vanillaType);
   }

   public LeavesType getDefaultType() {
      return VanillaLeavesTypes.OAK;
   }

   @Nullable
   public WoodType getEquivalentWoodType(LeavesType leavesType) {
      return this.leavesToWood.get(leavesType);
   }

   @Override
   public Optional<LeavesType> detectTypeFromBlock(Block baseBlock, ResourceLocation baseId) {
      String name = null;
      String path = baseId.getPath();
      if (path.endsWith("_leaves")) {
         name = path.substring(0, path.length() - "_leaves".length());
      } else if (path.startsWith("leaves_")) {
         name = path.substring("leaves_".length());
      }

      String namespace = baseId.getNamespace();
      if (name != null && !namespace.equals("securitycraft") && !path.contains("hanging") && baseBlock instanceof LeavesBlock) {
         ResourceLocation id = baseId.withPath(name);
         if (!this.valuesReg.containsKey(id)) {
            return Optional.of(new LeavesType(id, baseBlock));
         }
      }

      return Optional.empty();
   }

   private static boolean isBlacklisted(String namespace, String path) {
      return namespace.equals("securitycraft") || namespace.equals("dynamic_trees") || namespace.matches("dynamictrees|dt\\w+") || path.contains("hanging");
   }

   @Override
   public void finalizeAndFreeze() {
      super.finalizeAndFreeze();

      for (LeavesType l : this.getValues()) {
         ResourceLocation leavesId = l.id;
         ResourceLocation id = this.specialLeavesToWood.getOrDefault(leavesId, leavesId);
         WoodType o = WoodTypeRegistry.INSTANCE.get(id);
         String path = id.getPath();
         String namespace = id.getNamespace();
         if (o == null) {
            for (WoodType w : WoodTypeRegistry.INSTANCE.getValues()) {
               if (w.id.getPath().equals(path)) {
                  o = w;
                  break;
               }
            }
         }

         if (o == null) {
            for (WoodType wx : WoodTypeRegistry.INSTANCE.getValues()) {
               if ((wx.isVanilla() || wx.id.getNamespace().equals(namespace)) && path.endsWith(wx.id.getPath())) {
                  o = wx;
               }
            }
         }

         if (o != null) {
            this.leavesToWood.put(l, o);
         }
      }
   }

   public void addLeavesToWoodMapping(ResourceLocation leavesTypeId, ResourceLocation woodTypeId) {
      this.specialLeavesToWood.put(leavesTypeId, woodTypeId);
   }

   public void addLeavesToWoodMapping(String leavedId, String woodId) {
      this.addLeavesToWoodMapping(ResourceLocation.parse(leavedId), ResourceLocation.parse(woodId));
   }

   public void addLeavesToWoodMapping(String modId, String leavesTypeName, String woodTypeName) {
      this.addLeavesToWoodMapping(ResourceLocation.fromNamespaceAndPath(modId, leavesTypeName), ResourceLocation.fromNamespaceAndPath(modId, woodTypeName));
   }

   @Override
   public int priority() {
      return 99;
   }

   public LeavesType.Finder addSimpleFinder(ResourceLocation typeId) {
      LeavesType.Finder finder = new LeavesType.Finder(typeId);
      this.addFinder(finder);
      return finder;
   }

   public LeavesType.Finder addSimpleFinder(String typeId) {
      return this.addSimpleFinder(ResourceLocation.parse(typeId));
   }

   public LeavesType.Finder addSimpleFinder(String namespace, String name) {
      return this.addSimpleFinder(ResourceLocation.fromNamespaceAndPath(namespace, name));
   }
}
