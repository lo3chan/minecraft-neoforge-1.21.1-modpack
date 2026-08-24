package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.AddPoints;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.DispatchAlternateLayout;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ForcePlacement;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ReplaceFully;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ReplacePartially;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.RegionManager;
import dev.worldgen.lithostitched.mixin.common.MultiNoiseBiomeSourceAccessor;
import dev.worldgen.lithostitched.mixin.common.mnbs.ParameterListAccessor;
import dev.worldgen.lithostitched.platform.LithostitchedPlatform;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.RTree;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;

public class InjectorBiomeSource extends BiomeSource implements Cloneable {
   public static final MapCodec<BiomeSource> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(BiomeSource.CODEC.fieldOf("delegate").forGetter(InjectorBiomeSource::getRootSource)).apply(i, Function.identity())
   );
   private final BiomeSource directDelegate;
   private final BiomeSource rootDelegate;
   public BiomeResolver baseResolver;
   private final Map<MapCodec<? extends BiomeInjector>, List<BiomeInjector>> injectorsByType = new HashMap<>();
   private List<Holder<Biome>> possibleBiomes = new ArrayList<>();
   private List<Holder<Biome>> replacedBiomes = new ArrayList<>();
   private RegionManager regionManager;

   public InjectorBiomeSource(BiomeSource directDelegate) {
      this.directDelegate = directDelegate;
      this.rootDelegate = getRootSource(directDelegate);
      this.baseResolver = directDelegate;
   }

   public BiomeSource rootDelegate() {
      return this.rootDelegate;
   }

   public BiomeSource directDelegate() {
      return this.directDelegate;
   }

   public void applyInjectors(
      Map<ResourceLocation, BiomeInjector> injectors,
      Optional<DensityFunction> regionFunction,
      Map<ResourceKey<Region>, Region> regions,
      DensityFunctionWrapper noiseHelper
   ) {
      this.possibleBiomes = new ArrayList<>();
      injectors.values().forEach(injector -> {
         injector.mapAll(noiseHelper);
         List<BiomeInjector> byType = this.injectorsByType.getOrDefault(injector.codec(), new ArrayList<>());
         byType.add(injector);
         this.injectorsByType.put(injector.codec(), byType);
         this.possibleBiomes.addAll(injector.possibleBiomes());
         if (injector instanceof ReplaceFully replaceFully) {
            this.replacedBiomes.addAll(replaceFully.targets().stream().toList());
         }
      });

      for (List<BiomeInjector> byType : this.injectorsByType.values()) {
         byType.sort(Comparator.comparingInt(BiomeInjector::priority));
      }

      if (this.rootDelegate instanceof MultiNoiseBiomeSource multiNoise) {
         MultiNoiseBiomeSourceAccessor accessor = (MultiNoiseBiomeSourceAccessor)multiNoise;
         Optional left = accessor.getParameters().left();
         Optional right = accessor.getParameters().right();
         ArrayList<Pair<ParameterPoint, Holder<Biome>>> modifiedParameters = new ArrayList<>(
            left.orElseGet(() -> ((MultiNoiseBiomeSourceParameterList)((Holder)right.get()).value()).parameters()).values()
         );
         AddPoints.apply(modifiedParameters, this.injectorsByType.getOrDefault(AddPoints.CODEC, new ArrayList<>()));
         if (left.isPresent()) {
            updateParameters((ParameterList<Holder<Biome>>)left.get(), modifiedParameters);
         } else {
            updateParameters(((MultiNoiseBiomeSourceParameterList)((Holder)right.get()).value()).parameters(), modifiedParameters);
         }

         this.regionManager = new RegionManager(regionFunction, regions, noiseHelper, this.directDelegate.possibleBiomes());
      } else {
         Lithostitched.debug(
            "Biome source is not a MultiNoiseBiomeSource instance, got {}. add_points injectors will not run.", this.rootDelegate.getClass().getSimpleName()
         );
         this.regionManager = new RegionManager(regionFunction, regions, noiseHelper, this.directDelegate.possibleBiomes());
      }
   }

   private static void updateParameters(ParameterList<Holder<Biome>> original, List<Pair<ParameterPoint, Holder<Biome>>> modifiedParameters) {
      ParameterListAccessor<Holder<Biome>> parameterListAccessor = (ParameterListAccessor<Holder<Biome>>)original;
      parameterListAccessor.lithostitched$setValues(modifiedParameters);
      RTree<Holder<Biome>> rtree = RTree.create(modifiedParameters);
      parameterListAccessor.lithostitched$index(rtree);
      if (LithostitchedPlatform.isModLoaded("terrablender")) {
         try {
            Field field1 = original.getClass().getDeclaredField("uniqueTrees");
            field1.setAccessible(true);
            Object[] uniqueTrees = (Object[])field1.get(original);
            Object originalTree = uniqueTrees[0];
            Class<?>[] entryComponents = Arrays.stream(originalTree.getClass().getRecordComponents()).map(RecordComponent::getType).toArray(Class[]::new);
            Constructor<?> entryConstructor = originalTree.getClass().getDeclaredConstructor(entryComponents);
            entryConstructor.setAccessible(true);
            Object[] entryArguments = Arrays.stream(originalTree.getClass().getRecordComponents()).map(rc -> {
               try {
                  Method accessor = rc.getAccessor();
                  return rc.getName().equals("tree") ? rtree : accessor.invoke(originalTree);
               } catch (Exception var4x) {
                  throw new RuntimeException(var4x);
               }
            }).toArray();
            uniqueTrees[0] = entryConstructor.newInstance(entryArguments);
         } catch (Exception var10) {
         }
      }
   }

   public MapCodec<? extends BiomeSource> codec() {
      return CODEC;
   }

   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      return Stream.concat(this.directDelegate.possibleBiomes().stream(), this.possibleBiomes.stream()).filter(biome -> !this.replacedBiomes.contains(biome));
   }

   public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Sampler sampler) {
      if (this.injectorsByType.isEmpty()) {
         return this.baseResolver.getNoiseBiome(quartX, quartY, quartZ, sampler);
      } else {
         int blockX = QuartPos.toBlock(quartX);
         int blockY = QuartPos.toBlock(quartY);
         int blockZ = QuartPos.toBlock(quartZ);
         SimpleContext context = SimpleContext.of(blockX, blockY, blockZ);
         TargetPoint point = sampler.sample(quartX, quartY, quartZ);
         HashMap<DensityFunction, Double> densities = new HashMap<>();
         Holder<Biome> biome = this.baseResolver.getNoiseBiome(quartX, quartY, quartZ, sampler);
         ResourceKey<Region> currentRegion = this.regionManager.getRegion(context, biome);
         if (this.injectorsByType.containsKey(ForcePlacement.CODEC)) {
            for (BiomeInjector injector : this.injectorsByType.getOrDefault(ForcePlacement.CODEC, List.of())) {
               ForcePlacement forcePlacement = (ForcePlacement)injector;
               if (forcePlacement.matches(context, point, densities, currentRegion)) {
                  return forcePlacement.biome();
               }
            }
         }

         if (this.injectorsByType.containsKey(DispatchAlternateLayout.CODEC)) {
            for (BiomeInjector injectorx : this.injectorsByType.getOrDefault(DispatchAlternateLayout.CODEC, List.of())) {
               DispatchAlternateLayout alternateLayout = (DispatchAlternateLayout)injectorx;
               if (alternateLayout.matches(context, point, densities, currentRegion)) {
                  biome = (Holder<Biome>)alternateLayout.points().findValue(point);
                  break;
               }
            }
         }

         for (BiomeInjector injectorxx : this.injectorsByType.getOrDefault(ReplacePartially.CODEC, List.of())) {
            ReplacePartially replacePartially = (ReplacePartially)injectorxx;
            if (replacePartially.matches(context, point, densities, biome, currentRegion)) {
               biome = replacePartially.replacement();
               break;
            }
         }

         if (this.replacedBiomes.contains(biome)) {
            for (BiomeInjector injectorxxx : this.injectorsByType.getOrDefault(ReplaceFully.CODEC, List.of())) {
               ReplaceFully replaceFully = (ReplaceFully)injectorxxx;
               if (replaceFully.targets().contains(biome)) {
                  return replaceFully.replacement();
               }
            }
         }

         return biome;
      }
   }

   public void addDebugInfo(List<String> lines, BlockPos pos, Sampler sampler) {
      this.directDelegate.addDebugInfo(lines, pos, sampler);
   }

   public String getRegionLine(Sampler sampler, BlockPos pos) {
      SimpleContext context = SimpleContext.of(pos);
      int quartX = QuartPos.fromBlock(pos.getX());
      int quartY = QuartPos.fromBlock(pos.getY());
      int quartZ = QuartPos.fromBlock(pos.getZ());
      Holder<Biome> biome = this.baseResolver.getNoiseBiome(quartX, quartY, quartZ, sampler);
      ResourceLocation region = this.regionManager.getRegion(context, biome).location();
      int rawValue = this.regionManager.getRegionValue(context, biome);
      return String.format("Region: %s (Raw value: %s)", region, rawValue);
   }

   private static BiomeSource getRootSource(BiomeSource currentSource) {
      if (currentSource instanceof InjectorBiomeSource injector) {
         return getRootSource(injector.directDelegate);
      } else if (instanceOfBlueprintSource(currentSource)) {
         try {
            Field field = currentSource.getClass().getDeclaredField("originalSource");
            field.setAccessible(true);
            BiomeSource delegate = (BiomeSource)field.get(currentSource);
            return getRootSource(delegate);
         } catch (IllegalAccessException | NoSuchFieldException var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return currentSource;
      }
   }

   private static boolean instanceOfBlueprintSource(BiomeSource source) {
      return source.getClass().getSimpleName().equals("ModdedBiomeSource");
   }

   public InjectorBiomeSource clone() {
      try {
         return (InjectorBiomeSource)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }
}
