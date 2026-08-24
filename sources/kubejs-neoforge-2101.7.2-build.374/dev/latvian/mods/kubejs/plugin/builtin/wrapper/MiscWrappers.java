package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import java.io.File;
import java.lang.runtime.SwitchBootstraps;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface MiscWrappers {
   static IntProvider wrapIntProvider(Context cx, Object o) {
      return (IntProvider)tryWrapIntProvider(cx, o)
         .getOrThrow(error -> new KubeRuntimeException("Failed to read IntProvider from %s: %s".formatted(o, error)).source(SourceLine.of(cx)));
   }

   static FloatProvider wrapFloatProvider(Context cx, Object o) {
      return (FloatProvider)tryWrapFloatProvider(cx, o)
         .getOrThrow(error -> new KubeRuntimeException("Failed to read FloatProvider from %s: %s".formatted(o, error)).source(SourceLine.of(cx)));
   }

   static NumberProvider wrapNumberProvider(Context cx, Object o) {
      return (NumberProvider)tryWrapNumberProvider(cx, o)
         .getOrThrow(error -> new KubeRuntimeException("Failed to read NumberProvider from %s: %s".formatted(o, error)).source(SourceLine.of(cx)));
   }

   static Vec3 wrapVec3(Context cx, @Nullable Object o) {
      Object var2 = o;
      byte var3 = 0;

      while (true) {
         Vec3 var10000;
         switch (SwitchBootstraps.typeSwitch<"typeSwitch",Vec3,Position,List,List,BlockPos,Entity,LevelBlock>(var2, var3)) {
            case -1:
               throw new KubeRuntimeException("Vec3 cannot be null!").source(SourceLine.of(cx));
            case 0: {
               Vec3 vec = (Vec3)var2;
               var10000 = vec;
               break;
            }
            case 1: {
               Position vec = (Position)var2;
               var10000 = new Vec3(vec.x(), vec.y(), vec.z());
               break;
            }
            case 2: {
               List<?> list = (List<?>)var2;
               if (list.size() != 3) {
                  var3 = 3;
                  continue;
               }

               var10000 = new Vec3(
                  StringUtilsWrapper.parseDouble(list.get(0), 0.0),
                  StringUtilsWrapper.parseDouble(list.get(1), 0.0),
                  StringUtilsWrapper.parseDouble(list.get(2), 0.0)
               );
               break;
            }
            case 3: {
               List<?> list = (List<?>)var2;
               throw new KubeRuntimeException("Vec3 list requires 3 entries, got %s".formatted(list)).source(SourceLine.of(cx));
            }
            case 4:
               BlockPos pos = (BlockPos)var2;
               var10000 = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
               break;
            case 5:
               Entity entity = (Entity)var2;
               var10000 = entity.position();
               break;
            case 6:
               LevelBlock block = (LevelBlock)var2;
               var10000 = new Vec3(block.getCenterX(), block.getCenterY(), block.getCenterZ());
               break;
            default:
               throw new KubeRuntimeException("Invalid Vec3 input! Expected list, position, block position, entity or block, got %s.".formatted(o))
                  .source(SourceLine.of(cx));
         }

         return var10000;
      }
   }

   static BlockPos wrapBlockPos(Context cx, @Nullable Object o) {
      Object var2 = o;
      byte var3 = 0;

      while (true) {
         BlockPos var10000;
         switch (SwitchBootstraps.typeSwitch<"typeSwitch",BlockPos,List,List,LevelBlock,Position>(var2, var3)) {
            case -1:
               throw new KubeRuntimeException("BlockPos cannot be null!").source(SourceLine.of(cx));
            case 0:
               BlockPos pos = (BlockPos)var2;
               var10000 = pos;
               break;
            case 1: {
               List<?> list = (List<?>)var2;
               if (list.size() != 3) {
                  var3 = 2;
                  continue;
               }

               var10000 = new BlockPos(
                  StringUtilsWrapper.parseInt(list.get(0), 0), StringUtilsWrapper.parseInt(list.get(1), 0), StringUtilsWrapper.parseInt(list.get(2), 0)
               );
               break;
            }
            case 2: {
               List<?> list = (List<?>)var2;
               throw new KubeRuntimeException("BlockPos list requires 3 entries, got %s".formatted(list)).source(SourceLine.of(cx));
            }
            case 3:
               LevelBlock block = (LevelBlock)var2;
               var10000 = block.getPos();
               break;
            case 4:
               Position vec = (Position)var2;
               var10000 = BlockPos.containing(vec.x(), vec.y(), vec.z());
               break;
            default:
               throw new KubeRuntimeException("Invalid BlockPos input! Expected list, position or block, got %s.".formatted(o)).source(SourceLine.of(cx));
         }

         return var10000;
      }
   }

   private static DataResult<IntProvider> tryWrapIntProvider(Context cx, Object o) {
      return switch (o) {
         case Number n -> DataResult.success(ConstantInt.of(n.intValue()));
         case List<?> list -> {
            switch (list.size()) {
               case 0:
                  yield DataResult.error(() -> "list cannot be empty");
               case 1:
                  yield StringUtilsWrapper.tryParseInt(list.get(0)).map(ConstantInt::of);
               case 2:
                  yield StringUtilsWrapper.tryParseInt(list.get(0)).apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseInt(list.get(1)));
               default:
                  yield DataResult.error(() -> "list can contain at most 2 numbers");
            }
         }
         case Map<?, ?> m -> {
            Map<String, Object> map = Cast.to(m);
            yield map.containsKey("clamped")
               ? tryWrapIntProvider(cx, map.get("clamped")).apply2(MiscWrappers::toClamped, parseIntBounds(map))
               : (
                  map.containsKey("clamped_normal")
                     ? StringUtilsWrapper.tryParseInt(map.get("mean"))
                        .apply3(MiscWrappers::toClampedNormal, StringUtilsWrapper.tryParseInt(map.get("deviation")), parseIntBounds(map))
                     : (
                        hasBounds(map)
                           ? parseIntBounds(map).map(v -> v)
                           : IntProvider.CODEC
                              .parse(RegistryAccessContainer.of(cx).nbt(), NBTWrapper.wrapCompound(cx, map))
                              .mapError(error -> "Failed to decode IntProvider from %s: %s".formatted(map, error))
                     )
               );
         }
         case null, default -> DataResult.error(() -> "Expected a number, a numeric list, or a supported map format");
      };
   }

   private static DataResult<FloatProvider> tryWrapFloatProvider(Context cx, Object o) {
      return switch (o) {
         case Number n -> DataResult.success(ConstantFloat.of(n.floatValue()));
         case List<?> list -> {
            switch (list.size()) {
               case 0:
                  yield DataResult.error(() -> "list cannot be empty");
               case 1:
                  yield StringUtilsWrapper.tryParseFloat(list.get(0)).map(ConstantFloat::of);
               case 2:
                  yield StringUtilsWrapper.tryParseFloat(list.get(0)).apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseFloat(list.get(1)));
               default:
                  yield DataResult.error(() -> "list can contain at most 2 numbers");
            }
         }
         case Map<?, ?> map -> floatProviderFromMap(cx, Cast.to(map));
         case null, default -> DataResult.error(() -> "Expected a number, a numeric list, or a supported map format");
      };
   }

   private static DataResult<NumberProvider> tryWrapNumberProvider(Context cx, Object o) {
      return switch (o) {
         case Number n -> {
            float f = n.floatValue();
            yield DataResult.success(UniformGenerator.between(f, f));
         }
         case List<?> list -> {
            switch (list.size()) {
               case 0:
                  yield DataResult.error(() -> "list cannot be empty");
               case 1:
                  yield StringUtilsWrapper.tryParseFloat(list.get(0)).map(v -> UniformGenerator.between(v, v));
               case 2:
                  yield StringUtilsWrapper.tryParseFloat(list.get(0)).apply2(UniformGenerator::between, StringUtilsWrapper.tryParseFloat(list.get(1)));
               default:
                  yield DataResult.error(() -> "list can contain at most 2 numbers");
            }
         }
         case Map<?, ?> map -> numberProviderFromMap(cx, Cast.to(map));
         case null, default -> DataResult.error(() -> "Expected a number, list of numbers, or a supported map format");
      };
   }

   private static DataResult<UniformInt> parseIntBounds(Map<String, Object> m) {
      if (m.get("bounds") instanceof List<?> bounds) {
         return bounds.size() < 2
            ? DataResult.error(() -> "int bounds must contain at least 2 numbers, got %s".formatted(bounds))
            : StringUtilsWrapper.tryParseInt(bounds.get(0)).apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseInt(bounds.get(1)));
      } else if (m.containsKey("min") && m.containsKey("max")) {
         return StringUtilsWrapper.tryParseInt(m.get("min")).apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseInt(m.get("max")));
      } else if (m.containsKey("min_inclusive") && m.containsKey("max_inclusive")) {
         return StringUtilsWrapper.tryParseInt(m.get("min_inclusive")).apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseInt(m.get("max_inclusive")));
      } else {
         return m.containsKey("value")
            ? StringUtilsWrapper.tryParseInt(m.get("value")).map(f -> UniformInt.of(f, f))
            : DataResult.error(() -> "Failed to parse int bounds!");
      }
   }

   private static DataResult<UniformFloat> parseFloatBounds(Map<String, Object> m) {
      if (m.get("bounds") instanceof List<?> bounds) {
         return bounds.size() < 2
            ? DataResult.error(() -> "float bounds must contain at least 2 numbers, got %s".formatted(bounds))
            : StringUtilsWrapper.tryParseFloat(bounds.get(0)).apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseFloat(bounds.get(1)));
      } else if (m.containsKey("min") && m.containsKey("max")) {
         return StringUtilsWrapper.tryParseFloat(m.get("min")).apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseFloat(m.get("max")));
      } else if (m.containsKey("min_inclusive") && m.containsKey("max_inclusive")) {
         return StringUtilsWrapper.tryParseFloat(m.get("min_inclusive"))
            .apply2(MiscWrappers::toUniform, StringUtilsWrapper.tryParseFloat(m.get("max_inclusive")));
      } else {
         return m.containsKey("value")
            ? StringUtilsWrapper.tryParseFloat(m.get("value")).map(f -> UniformFloat.of(f, f))
            : DataResult.error(() -> "Failed to parse float bounds!");
      }
   }

   private static DataResult<IntProvider> intProviderFromMap(Context cx, Map<String, Object> m) {
      if (m.containsKey("clamped")) {
         return tryWrapIntProvider(cx, m.get("clamped")).apply2(MiscWrappers::toClamped, parseIntBounds(m));
      } else if (m.containsKey("clamped_normal")) {
         return StringUtilsWrapper.tryParseInt(m.get("mean"))
            .apply3(MiscWrappers::toClampedNormal, StringUtilsWrapper.tryParseInt(m.get("deviation")), parseIntBounds(m));
      } else {
         return hasBounds(m)
            ? parseIntBounds(m).map(v -> v)
            : IntProvider.CODEC
               .parse(RegistryAccessContainer.of(cx).nbt(), NBTWrapper.wrapCompound(cx, m))
               .map(v -> v)
               .mapError(error -> "Failed to decode IntProvider from %s: %s".formatted(m, error));
      }
   }

   private static DataResult<NumberProvider> numberProviderFromMap(Context cx, Map<String, Object> m) {
      if (m.containsKey("min") && m.containsKey("max")) {
         return StringUtilsWrapper.tryParseInt(m.get("min")).apply2(UniformGenerator::between, StringUtilsWrapper.tryParseFloat(m.get("max")));
      } else if (m.containsKey("n") && m.containsKey("p")) {
         return StringUtilsWrapper.tryParseInt(m.get("n")).apply2(BinomialDistributionGenerator::binomial, StringUtilsWrapper.tryParseFloat(m.get("p")));
      } else {
         return m.containsKey("value")
            ? StringUtilsWrapper.tryParseFloat(m.get("value")).map(f -> UniformGenerator.between(f, f))
            : DataResult.error(() -> "Invalid NumberProvider map %s. Expected {min,max}, {n,p}, or {value}.".formatted(m));
      }
   }

   private static DataResult<FloatProvider> floatProviderFromMap(Context cx, Map<String, Object> m) {
      if (m.containsKey("clamped_normal")) {
         return StringUtilsWrapper.tryParseInt(m.get("mean"))
            .apply3(MiscWrappers::toClampedNormal, StringUtilsWrapper.tryParseFloat(m.get("deviation")), parseFloatBounds(m));
      } else {
         return hasBounds(m)
            ? parseFloatBounds(m).map(v -> v)
            : FloatProvider.CODEC
               .parse(RegistryAccessContainer.of(cx).nbt(), NBTWrapper.wrapCompound(cx, m))
               .mapError(error -> "Failed to decode FloatProvider from %s: %s".formatted(m, error));
      }
   }

   private static boolean hasBounds(Map<String, Object> m) {
      return m.get("bounds") instanceof List
         || m.containsKey("min") && m.containsKey("max")
         || m.containsKey("min_inclusive") && m.containsKey("max_inclusive")
         || m.containsKey("value");
   }

   private static UniformInt toUniform(int x, int y) {
      int min = Math.min(x, y);
      int max = Math.max(x, y);
      return UniformInt.of(min, max);
   }

   private static UniformFloat toUniform(float x, float y) {
      float min = Math.min(x, y);
      float max = Math.max(x, y);
      return UniformFloat.of(min, max);
   }

   private static IntProvider toClamped(IntProvider source, UniformInt clampTo) {
      return ClampedInt.of(source, clampTo.getMinValue(), clampTo.getMaxValue());
   }

   private static IntProvider toClampedNormal(int mean, int deviation, UniformInt clampTo) {
      return ClampedNormalInt.of(mean, deviation, clampTo.getMinValue(), clampTo.getMaxValue());
   }

   private static FloatProvider toClampedNormal(float mean, float deviation, UniformFloat clampTo) {
      return ClampedNormalFloat.of(mean, deviation, clampTo.getMinValue(), clampTo.getMaxValue());
   }

   static Path wrapPath(Context cx, Object o) {
      try {
         if (o instanceof Path p) {
            return KubeJSPaths.verifyFilePath(p);
         } else {
            return o != null && !o.toString().isEmpty() ? KubeJSPaths.verifyFilePath(KubeJSPaths.GAMEDIR.resolve(o.toString())) : null;
         }
      } catch (Exception var3) {
         throw new KubeRuntimeException("Invalid path '%s'".formatted(o), var3).source(SourceLine.of(cx));
      }
   }

   static File wrapFile(Context cx, Object o) {
      try {
         if (o instanceof File f) {
            return KubeJSPaths.verifyFilePath(f.toPath()).toFile();
         } else {
            return o != null && !o.toString().isEmpty() ? KubeJSPaths.verifyFilePath(KubeJSPaths.GAMEDIR.resolve(o.toString())).toFile() : null;
         }
      } catch (Exception var3) {
         throw new KubeRuntimeException("Invalid file path '%s'".formatted(o), var3).source(SourceLine.of(cx));
      }
   }
}
