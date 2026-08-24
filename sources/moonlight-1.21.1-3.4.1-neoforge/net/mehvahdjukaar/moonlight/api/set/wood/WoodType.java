package net.mehvahdjukaar.moonlight.api.set.wood;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.CompatHandler;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat.Type;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class WoodType extends BlockType {
   public static Codec<WoodType> CODEC;
   public static StreamCodec<ByteBuf, WoodType> STREAM_CODEC;
   public static Supplier<EntityDataSerializer<WoodType>> ENTITY_SERIALIZER;
   public final Block planks;
   public final Block log;
   private final boolean bambooLike;
   private final Supplier<net.minecraft.world.level.block.state.properties.WoodType> vanillaType = Suppliers.memoize(this::detectVanillaWood);
   private final Supplier<Type> boatType = Suppliers.memoize(this::detectVanillaBoat);

   @Nullable
   private Type detectVanillaBoat() {
      if (this == VanillaWoodTypes.OAK) {
         return Type.OAK;
      } else {
         ResourceLocation id = this.getId();

         for (String s : Set.of(
            id.getPath(), id.getNamespace() + id.getPath(), id.getNamespace() + "_" + id.getPath(), id.getNamespace() + "/" + id.getPath(), id.toString()
         )) {
            Type o = Type.byName(s);
            if (o != Type.OAK) {
               return o;
            }
         }

         return null;
      }
   }

   @Nullable
   private net.minecraft.world.level.block.state.properties.WoodType detectVanillaWood() {
      if (this.getChild("hanging_sign") instanceof CeilingHangingSignBlock c) {
         return c.type();
      } else if (this.getChild("sign") instanceof SignBlock f) {
         return f.type();
      } else {
         String i = this.id.getNamespace().equals("minecraft") ? this.id.getPath() : this.id.toString();
         Stream<net.minecraft.world.level.block.state.properties.WoodType> values = net.minecraft.world.level.block.state.properties.WoodType.values();
         Optional<net.minecraft.world.level.block.state.properties.WoodType> o = values.filter(v -> v.name().equals(i)).findAny();
         return o.orElse(null);
      }
   }

   public WoodType(ResourceLocation id, Block baseBlock, Block logBlock) {
      this(id, baseBlock, logBlock, defaultIsBambooLike(id));
   }

   public WoodType(ResourceLocation id, Block baseBlock, Block logBlock, boolean bambooLike) {
      super(id);
      this.planks = baseBlock;
      this.log = logBlock;
      this.bambooLike = bambooLike;
   }

   public static boolean defaultIsBambooLike(ResourceLocation id) {
      String name = id.getPath();
      int slash = name.lastIndexOf(47);
      if (slash >= 0) {
         name = name.substring(slash + 1);
      }

      return name.contains("bamboo");
   }

   public boolean isBambooLike() {
      return this.bambooLike;
   }

   @Override
   public ItemLike mainChild() {
      return this.planks;
   }

   @Nullable
   public net.minecraft.world.level.block.state.properties.WoodType toVanilla() {
      return this.vanillaType.get();
   }

   @Nullable
   public Type toVanillaBoat() {
      return this.boatType.get();
   }

   @NotNull
   public net.minecraft.world.level.block.state.properties.WoodType toVanillaOrOak() {
      net.minecraft.world.level.block.state.properties.WoodType v = this.toVanilla();
      return v != null ? v : net.minecraft.world.level.block.state.properties.WoodType.OAK;
   }

   @NotNull
   public Type toVanillaBoatOrOak() {
      Type v = this.toVanillaBoat();
      if (v != null) {
         return v;
      } else {
         return this.isBambooLike() ? Type.BAMBOO : Type.OAK;
      }
   }

   public String getTexturePath() {
      String namespace = this.getNamespace();
      return namespace.equals("minecraft") ? this.getTypeName() : this.getNamespace() + "/" + this.getTypeName();
   }

   public boolean canBurn() {
      return this.planks.defaultBlockState().ignitedByLava();
   }

   public MapColor getColor() {
      return this.planks.defaultMapColor();
   }

   @Override
   public String getTranslationKey() {
      return "wood_type." + this.getNamespace() + "." + this.getTypeName();
   }

   @Override
   public void initializeChildrenBlocks() {
      this.addChild("planks", this.planks);
      this.addChild("log", this.log);
      this.addChild("leaves", this.findRelatedEntry("leaves", BuiltInRegistries.BLOCK));
      this.addChild("wood", this.findLogRelatedBlock("", "wood", "hyphae", "bark"));
      this.addChild("stripped_log", this.findStrippedLog("log", "stem", "stalk"));
      this.addChild("stripped_wood", this.findStrippedLog("wood", "hyphae", "bark"));
      this.addChild("slab", this.findRelatedEntry("slab", BuiltInRegistries.BLOCK));
      this.addChild("stairs", this.findRelatedEntry("stairs", BuiltInRegistries.BLOCK));
      Block fence = this.findRelatedEntry("fence", BuiltInRegistries.BLOCK);
      this.addChild("fence", fence);
      this.addChild("fence_gate", this.findRelatedEntry("fence_gate", BuiltInRegistries.BLOCK));
      this.addChild("door", this.findRelatedEntry("door", BuiltInRegistries.BLOCK));
      this.addChild("trapdoor", this.findRelatedEntry("trapdoor", BuiltInRegistries.BLOCK));
      this.addChild("button", this.findRelatedEntry("button", BuiltInRegistries.BLOCK));
      this.addChild("pressure_plate", this.findRelatedEntry("pressure_plate", BuiltInRegistries.BLOCK));
      this.addChild("hanging_sign", this.findRelatedEntry("hanging_sign", BuiltInRegistries.BLOCK));
      this.addChild("wall_hanging_sign", this.findRelatedEntry("wall_hanging_sign", BuiltInRegistries.BLOCK));
      this.addChild("sign", this.findRelatedEntry("sign", BuiltInRegistries.BLOCK));
      this.addChild("wall_sign", this.findRelatedEntry("wall_sign", BuiltInRegistries.BLOCK));
      if (this.id.getNamespace().matches("tfc|afc")) {
         this.addChild("sign", this.findRelatedEntry("sign", "", BuiltInRegistries.BLOCK));
         this.addChild("hanging_sign", this.findRelatedEntry("hanging_sign/wrought_sign", "", BuiltInRegistries.BLOCK));
      }

      if (fence != null && CompatHandler.DIAGONALFENCES) {
         Optional<Block> diagonalFence = BuiltInRegistries.BLOCK
            .getOptional(ResourceLocation.fromNamespaceAndPath("diagonalfences", Utils.getID(fence).toString().replace(":", "/")));
         diagonalFence.ifPresent(block -> this.addChild("diagonalfences:fence", block));
      }
   }

   @Override
   public void initializeChildrenItems() {
      if (this.isBambooLike()) {
         this.addChild("boat", this.findRelatedItem("raft", "boat"));
         this.addChild("chest_boat", this.findRelatedItem("chest_raft", "chest_boat"));
      } else {
         this.addChild("boat", this.findRelatedItem("boat", "raft"));
         this.addChild("chest_boat", this.findRelatedItem("chest_boat", "chest_raft"));
      }

      this.addChild("sapling", this.findRelatedEntry("sapling", BuiltInRegistries.ITEM));
      if (this.id.getNamespace().matches("tfc|afc")) {
         this.addChild("stick", this.findRelatedEntry("twig", BuiltInRegistries.BLOCK));
         this.addChild("boat", this.findRelatedEntry("boat", "", BuiltInRegistries.BLOCK));
      }
   }

   @Nullable
   @Override
   protected <V> V findRelatedEntry(String prefixOrInfix, String suffix, Registry<V> reg) {
      if (!suffix.isEmpty()) {
         suffix = "_" + suffix;
      }

      ResourceLocation[] targets = new ResourceLocation[]{
         ResourceLocation.fromNamespaceAndPath(this.id.getNamespace(), this.id.getPath() + "_" + prefixOrInfix + suffix),
         ResourceLocation.fromNamespaceAndPath(this.id.getNamespace(), prefixOrInfix + "_" + this.id.getPath() + suffix),
         this.id.withPath(this.id.getPath() + "_planks_" + prefixOrInfix + suffix),
         this.id.withPath("wood/planks/" + this.id.getPath() + "_" + prefixOrInfix),
         this.id.withPath("wood/" + prefixOrInfix + suffix + "/" + this.id.getPath())
      };
      return Utils.findFirstInRegistry(reg, targets);
   }

   @Nullable
   protected Block findStrippedLog(String... possibleNames) {
      for (String v : possibleNames) {
         Block b = this.getBlockOfThis(v);
         if (v != null) {
            Block stripped = (Block)AxeItem.STRIPPABLES.get(b);
            if (stripped != null && stripped != b) {
               return stripped;
            }
         }
      }

      return this.findLogRelatedBlock("stripped", possibleNames);
   }

   @Nullable
   protected Item findRelatedItem(String... names) {
      for (String n : names) {
         Item b = this.findRelatedEntry(n, BuiltInRegistries.ITEM);
         if (b != null) {
            return b;
         }
      }

      return null;
   }

   @Nullable
   protected Block findLogRelatedBlock(String prefix, String... possibleSuffix) {
      for (String n : possibleSuffix) {
         Block b = this.findLogWithAffix(prefix, n);
         if (b != null) {
            return b;
         }
      }

      return null;
   }

   @Nullable
   protected Block findLogWithAffix(String prefix, String suffix) {
      if (this.id.getNamespace().matches("tfc|afc")) {
         String prefix_ = prefix.isEmpty() ? "" : prefix + "_";
         Optional<Block> o = BuiltInRegistries.BLOCK
            .getOptional(ResourceLocation.fromNamespaceAndPath(this.getNamespace(), "wood/" + prefix_ + suffix + "/" + this.id.getPath()));
         if (o.isPresent()) {
            return o.get();
         }
      }

      List<ResourceLocation> targets = makeKnownIDConventionsAffix(this.id.getNamespace(), this.id.getPath(), prefix, suffix, Utils.getID(this.log).getPath());
      return Utils.findFirstInRegistry(BuiltInRegistries.BLOCK, targets);
   }

   @NotNull
   private static List<ResourceLocation> makeKnownIDConventionsAffix(
      String myNamespace, String myPath, String prefixOrInfix, String suffix, @Nullable String alternateNamespace
   ) {
      boolean noneEmpty = !prefixOrInfix.isEmpty() && !suffix.isEmpty();
      String prefix_ = prefixOrInfix.isEmpty() ? "" : prefixOrInfix + "_";
      String _infix = prefixOrInfix.isEmpty() ? "" : "_" + prefixOrInfix;
      String _suffix = suffix.isEmpty() ? "" : "_" + suffix;
      List<ResourceLocation> targets = new ArrayList<>();
      targets.add(ResourceLocation.fromNamespaceAndPath(myNamespace, myPath + _infix + _suffix));
      targets.add(ResourceLocation.fromNamespaceAndPath(myNamespace, myPath + _suffix + _infix));
      targets.add(ResourceLocation.fromNamespaceAndPath(myNamespace, prefix_ + myPath + _suffix));
      if (alternateNamespace != null) {
         targets.add(ResourceLocation.fromNamespaceAndPath(myNamespace, alternateNamespace + _infix + _suffix));
      }

      if (noneEmpty && alternateNamespace != null) {
         targets.add(ResourceLocation.fromNamespaceAndPath(myNamespace, prefix_ + alternateNamespace + _suffix));
      }

      if (myPath.endsWith(suffix)) {
         targets.add(ResourceLocation.fromNamespaceAndPath(myNamespace, prefix_ + myPath));
      }

      return targets;
   }

   static List<ResourceLocation> makeKnownIDConventions(ResourceLocation id, String... affixKeyword) {
      String myPath = id.getPath();
      String myNamespace = id.getNamespace();
      List<ResourceLocation> possibleTargets = new ArrayList<>();

      for (String affix : affixKeyword) {
         possibleTargets.addAll(makeKnownIDConventionsAffix(myNamespace, myPath, "", affix, null));
         possibleTargets.addAll(makeKnownIDConventionsAffix(myNamespace, myPath, affix, "", null));
      }

      return possibleTargets;
   }

   @Nullable
   static Block findLog(ResourceLocation id) {
      List<ResourceLocation> tests = makeKnownIDConventions(id, "log", "stem", "stalk", "hyphae");
      return Utils.findFirstInRegistry(BuiltInRegistries.BLOCK, tests);
   }

   @Nullable
   static Block findPlanks(ResourceLocation id) {
      List<ResourceLocation> tests = makeKnownIDConventions(id, "planks", "plank");
      return Utils.findFirstInRegistry(BuiltInRegistries.BLOCK, tests);
   }

   public Properties copyProperties() {
      Properties p = Properties.of();
      p.mapColor(this.getColor());
      if (this.canBurn()) {
         p.ignitedByLava();
      }

      p.sound(this.getSound());
      return p;
   }

   static {
      WoodTypeRegistry.touch();
   }

   public static class Finder extends BlockType.SetFinderBuilder<WoodType> {
      private Supplier<Block> planksFinder;
      private Supplier<Block> logFinder;
      @Nullable
      private Boolean bambooLike;

      public Finder(ResourceLocation id) {
         super(id, WoodTypeRegistry.INSTANCE);
         this.log((Supplier<Block>)(() -> WoodType.findLog(id)));
         this.planks((Supplier<Block>)(() -> WoodType.findPlanks(id)));
      }

      public WoodType.Finder planks(Supplier<Block> planksFinder) {
         this.planksFinder = planksFinder;
         return this;
      }

      public WoodType.Finder planks(ResourceLocation id) {
         return this.planks(
            (Supplier<Block>)(() -> (Block)BuiltInRegistries.BLOCK
               .getOptional(id)
               .orElseThrow(() -> new IllegalStateException("Failed to find planks block: " + id)))
         );
      }

      public WoodType.Finder planks(String planksName) {
         return this.planks(Utils.idWithOptionalNamespace(planksName, this.id.getNamespace()));
      }

      public WoodType.Finder planksAffix(String prefix, String suffix) {
         return this.planks(prefix + this.id.getPath() + suffix);
      }

      public WoodType.Finder planksSuffix(String suffix) {
         return this.planks(this.id.getPath() + suffix);
      }

      public WoodType.Finder log(Supplier<Block> logFinder) {
         this.logFinder = logFinder;
         return this;
      }

      public WoodType.Finder log(ResourceLocation id) {
         return this.log(
            (Supplier<Block>)(() -> (Block)BuiltInRegistries.BLOCK
               .getOptional(id)
               .orElseThrow(() -> new IllegalStateException("Failed to find log block: " + id)))
         );
      }

      public WoodType.Finder log(String nameLog) {
         return this.log(Utils.idWithOptionalNamespace(nameLog, this.id.getNamespace()));
      }

      public WoodType.Finder logAffix(String prefix, String suffix) {
         return this.log(prefix + this.id.getPath() + suffix);
      }

      public WoodType.Finder logSuffix(String suffix) {
         return this.log(this.id.getPath() + suffix);
      }

      public WoodType.Finder bambooLike(boolean bambooLike) {
         this.bambooLike = bambooLike;
         return this;
      }

      @Internal
      @Override
      public Optional<WoodType> get() {
         if (PlatHelper.isModLoaded(this.id.getNamespace())) {
            try {
               Block plank = (Block)Preconditions.checkNotNull(this.planksFinder.get(), "Manual Finder - failed to find a plank block for {}", this.id);
               Block log = (Block)Preconditions.checkNotNull(this.logFinder.get(), "Manual Finder - failed to find a log block for {}", this.id);
               boolean isBambooLike = this.bambooLike != null ? this.bambooLike : WoodType.defaultIsBambooLike(this.id);
               WoodType woodType = new WoodType(this.id, plank, log, isBambooLike);
               this.childNames.forEach((key, value) -> {
                  try {
                     ItemLike obj = (ItemLike)Preconditions.checkNotNull(value.get());
                     woodType.addChild(key, obj);
                  } catch (Exception var5x) {
                     Moonlight.LOGGER.warn("Failed to find child for WoodType: {} - {}. Ignored! ERROR: {}", this.id, key, var5x.getMessage());
                  }
               });
               return Optional.of(woodType);
            } catch (Exception var5) {
               Moonlight.LOGGER.warn("Failed to find custom WoodType:  {} - ", this.id, var5);
            }
         }

         return Optional.empty();
      }

      @Deprecated(
         forRemoval = true
      )
      public Finder(ResourceLocation id, Supplier<Block> planks, Supplier<Block> log) {
         super(id, WoodTypeRegistry.INSTANCE);
         this.planksFinder = planks;
         this.logFinder = log;
      }

      @Deprecated(
         forRemoval = true
      )
      public static WoodType.Finder simple(String modId, String woodTypeName, String planksName, String logName) {
         return simple(
            ResourceLocation.fromNamespaceAndPath(modId, woodTypeName),
            ResourceLocation.fromNamespaceAndPath(modId, planksName),
            ResourceLocation.fromNamespaceAndPath(modId, logName)
         );
      }

      @Deprecated(
         forRemoval = true
      )
      public static WoodType.Finder simple(ResourceLocation woodTypeName, ResourceLocation planksName, ResourceLocation logName) {
         return new WoodType.Finder(woodTypeName, () -> (Block)BuiltInRegistries.BLOCK.get(planksName), () -> (Block)BuiltInRegistries.BLOCK.get(logName));
      }

      @Deprecated(
         forRemoval = true
      )
      public void addChild(String childType, String childName) {
         this.childBlock(childType, childName);
      }

      @Deprecated(
         forRemoval = true
      )
      public void addChild(String childType, ResourceLocation childName) {
         this.childBlock(childType, childName);
      }
   }
}
