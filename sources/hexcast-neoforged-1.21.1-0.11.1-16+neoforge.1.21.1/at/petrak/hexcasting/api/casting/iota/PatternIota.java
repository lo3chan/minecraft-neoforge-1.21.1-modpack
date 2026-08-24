package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapEvalTooMuch;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidPattern;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public class PatternIota extends Iota {
   public static IotaType<PatternIota> TYPE = new IotaType<PatternIota>() {
      public PatternIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
         return PatternIota.deserialize(tag);
      }

      @Override
      public Component display(Tag tag) {
         return PatternIota.display(PatternIota.deserialize(tag).getPattern());
      }

      @Override
      public int color() {
         return -22016;
      }
   };

   public PatternIota(@NotNull HexPattern pattern) {
      super(HexIotaTypes.PATTERN, pattern);
   }

   public HexPattern getPattern() {
      return (HexPattern)this.payload;
   }

   protected PatternIota(@NotNull IotaType<?> type, @NotNull Object payload) {
      super(type, payload);
   }

   @Override
   public boolean isTruthy() {
      return true;
   }

   @Override
   public boolean toleratesOther(Iota that) {
      return typesMatch(this, that) && that instanceof PatternIota piota && this.getPattern().anglesSignature().equals(piota.getPattern().anglesSignature());
   }

   @NotNull
   @Override
   public Tag serialize() {
      return this.getPattern().serializeToNBT();
   }

   @NotNull
   @Override
   public CastResult execute(CastingVM vm, ServerLevel world, SpellContinuation continuation) {
      Component castedName = null;

      try {
         PatternShapeMatch lookup = PatternRegistryManifest.matchPattern(this.getPattern(), world, false);
         vm.getEnv().precheckAction(lookup);
         Action action;
         if (!(lookup instanceof PatternShapeMatch.Normal) && !(lookup instanceof PatternShapeMatch.PerWorld)) {
            if (!(lookup instanceof PatternShapeMatch.Special special)) {
               if (lookup instanceof PatternShapeMatch.Nothing) {
                  throw new MishapInvalidPattern();
               }

               throw new IllegalStateException();
            }

            castedName = special.handler.getName();
            action = special.handler.act();
         } else {
            ResourceKey<ActionRegistryEntry> key;
            if (lookup instanceof PatternShapeMatch.Normal normal) {
               key = normal.key;
            } else {
               PatternShapeMatch.PerWorld perWorld = (PatternShapeMatch.PerWorld)lookup;
               key = perWorld.key;
            }

            boolean reqsEnlightenment = HexUtils.isOfTag(IXplatAbstractions.INSTANCE.getActionRegistry(), key, HexTags.Actions.REQUIRES_ENLIGHTENMENT);
            castedName = HexAPI.instance().getActionI18n(key, reqsEnlightenment);
            action = ((ActionRegistryEntry)Objects.requireNonNull((ActionRegistryEntry)IXplatAbstractions.INSTANCE.getActionRegistry().get(key))).action();
            if (reqsEnlightenment && !vm.getEnv().isEnlightened()) {
               throw new MishapUnenlightened();
            }
         }

         OperationResult result = action.operate(vm.getEnv(), vm.getImage(), continuation);
         if (result.getNewImage().getOpsConsumed() > HexConfig.server().maxOpCount()) {
            throw new MishapEvalTooMuch();
         } else {
            SpellContinuation cont2 = result.getNewContinuation();
            List<OperatorSideEffect> sideEffects = result.getSideEffects();
            return new CastResult(this, cont2, result.getNewImage(), sideEffects, ResolvedPatternType.EVALUATED, result.getSound());
         }
      } catch (Mishap var11) {
         return new CastResult(
            this,
            continuation,
            null,
            List.of(new OperatorSideEffect.DoMishap(var11, new Mishap.Context(this.getPattern(), castedName))),
            var11.resolutionType(vm.getEnv()),
            HexEvalSounds.MISHAP
         );
      }
   }

   @Override
   public boolean executable() {
      return true;
   }

   public static PatternIota deserialize(Tag tag) throws IllegalArgumentException {
      CompoundTag patTag = HexUtils.downcast(tag, CompoundTag.TYPE);
      HexPattern pat = HexPattern.fromNBT(patTag);
      return new PatternIota(pat);
   }

   public static Component display(HexPattern pat) {
      StringBuilder bob = new StringBuilder();
      bob.append(pat.getStartDir());
      String sig = pat.anglesSignature();
      if (!sig.isEmpty()) {
         bob.append(" ");
         bob.append(sig);
      }

      return Component.translatable("hexcasting.tooltip.pattern_iota", new Object[]{Component.literal(bob.toString()).withStyle(ChatFormatting.WHITE)})
         .withStyle(ChatFormatting.GOLD);
   }
}
