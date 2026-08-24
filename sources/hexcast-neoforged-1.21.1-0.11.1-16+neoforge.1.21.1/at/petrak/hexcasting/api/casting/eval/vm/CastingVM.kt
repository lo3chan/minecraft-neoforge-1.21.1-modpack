package at.petrak.hexcasting.api.casting.eval.vm

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.SpecialPatterns
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapInternalException
import at.petrak.hexcasting.api.casting.mishaps.MishapTooManyCloseParens
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel

@SourceDebugExtension(["SMAP\nCastingVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CastingVM.kt\nat/petrak/hexcasting/api/casting/eval/vm/CastingVM\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,291:1\n1#2:292\n1563#3:293\n1634#3,3:294\n1563#3:297\n1634#3,3:298\n*S KotlinDebug\n*F\n+ 1 CastingVM.kt\nat/petrak/hexcasting/api/casting/eval/vm/CastingVM\n*L\n146#1:293\n146#1:294,3\n206#1:297\n206#1:298,3\n*E\n"])
public class CastingVM(image: CastingImage, env: CastingEnvironment) {
   public final var image: CastingImage
      internal set

   public final val env: CastingEnvironment

   init {
      this.image = image;
      this.env = env;
      this.env.triggerCreateEvent();
   }

   public fun queueExecuteAndWrapIota(iota: Iota, world: ServerLevel): ExecutionClientView {
      return this.queueExecuteAndWrapIotas(CollectionsKt.listOf(iota), world);
   }

   public fun queueExecuteAndWrapIotas(iotas: List<Iota>, world: ServerLevel): ExecutionClientView {
      var continuation: SpellContinuation = SpellContinuation.Done.INSTANCE.pushFrame(new FrameEvaluate(new SpellList.LList(0, iotas), false));
      val info: CastingVM.TempControllerInfo = new CastingVM.TempControllerInfo(false);
      var lastResolutionType: ResolvedPatternType = ResolvedPatternType.UNRESOLVED;

      while (continuation instanceof SpellContinuation.NotDone && !info.getEarlyExit()) {
         val stackDescs: CastResult = (continuation as SpellContinuation.NotDone)
            .getFrame()
            .evaluate((continuation as SpellContinuation.NotDone).getNext(), world, this);
         if (stackDescs.getNewData() != null) {
            this.image = stackDescs.getNewData();
         }

         this.env.postExecution(stackDescs);
         continuation = stackDescs.getContinuation();
         lastResolutionType = stackDescs.getResolutionType();
         this.performSideEffects(info, stackDescs.getSideEffects());
         info.setEarlyExit(info.getEarlyExit() || !lastResolutionType.getSuccess());
      }

      if (continuation is SpellContinuation.NotDone) {
         lastResolutionType = if (lastResolutionType.getSuccess()) ResolvedPatternType.EVALUATED else ResolvedPatternType.ERRORED;
      }

      val var10: Pair = this.generateDescs();
      val var11: java.util.List = var10.component1() as java.util.List;
      val ravenmind: CompoundTag = var10.component2() as CompoundTag;
      return new ExecutionClientView(
         this.image.getStack().isEmpty() && this.image.getParenCount() == 0 && !this.image.getEscapeNext() && ravenmind == null,
         lastResolutionType,
         var11,
         ravenmind
      );
   }

   public fun executeInner(iota: Iota, world: ServerLevel, continuation: SpellContinuation): CastResult {
      try {
         try {
            val var4: Pair = this.handleParentheses(iota);
            if (var4 != null) {
               val data: CastingImage = var4.component1() as CastingImage;
               val resolutionType: ResolvedPatternType = var4.component2() as ResolvedPatternType;
               val var17: java.util.List = CollectionsKt.emptyList();
               val var22: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
               return new CastResult(iota, continuation, data, var17, resolutionType, var22);
            }
         } catch (var10: MishapTooManyCloseParens) {
            var var12: CastResult;
            var var15: OperatorSideEffect.DoMishap;
            var var20: Mishap;
            var var23: Mishap.Context;
            var var26: HexPattern;
            label38: {
               var12 = new CastResult;
               var15 = new OperatorSideEffect.DoMishap;
               var20 = var10;
               var23 = new Mishap.Context;
               val var25: PatternIota = iota as? PatternIota;
               if ((iota as? PatternIota) != null) {
                  var26 = var25.getPattern();
                  if (var26 != null) {
                     break label38;
                  }
               }

               var26 = new HexPattern(HexDir.WEST, null, 2, null);
            }

            var23./* $VF: Unable to resugar constructor */<init>(var26, HexAPI.instance().getRawHookI18n(HexAPI.modLoc("close_paren")));
            var15./* $VF: Unable to resugar constructor */<init>(var20, var23);
            val var16: java.util.List = CollectionsKt.listOf(var15);
            val var18: ResolvedPatternType = ResolvedPatternType.ERRORED;
            val var21: EvalSound = HexEvalSounds.MISHAP;
            var12./* $VF: Unable to resugar constructor */<init>(iota, continuation, null, var16, var18, var21);
            return var12;
         }

         val var13: CastResult = iota.execute(this, world, continuation);
         return var13;
      } catch (var11: Exception) {
         var var24: HexPattern;
         var var10000: CastResult;
         var var10005: OperatorSideEffect.DoMishap;
         var var10007: Mishap;
         var var10008: Mishap.Context;
         label29: {
            HexAPI.LOGGER.error("Unexpected exception in spell execution", var11);
            var10000 = new CastResult;
            var10005 = new OperatorSideEffect.DoMishap;
            var10007 = new MishapInternalException(var11);
            var10008 = new Mishap.Context;
            val var10010: PatternIota = iota as? PatternIota;
            if ((iota as? PatternIota) != null) {
               var24 = var10010.getPattern();
               if (var24 != null) {
                  break label29;
               }
            }

            var24 = new HexPattern(HexDir.WEST, null, 2, null);
         }

         var10008./* $VF: Unable to resugar constructor */<init>(var24, null);
         var10005./* $VF: Unable to resugar constructor */<init>(var10007, var10008);
         val var14: java.util.List = CollectionsKt.listOf(var10005);
         val var10006: ResolvedPatternType = ResolvedPatternType.ERRORED;
         val var19: EvalSound = HexEvalSounds.MISHAP;
         var10000./* $VF: Unable to resugar constructor */<init>(iota, continuation, null, var14, var10006, var19);
         return var10000;
      }
   }

   public fun performSideEffects(info: at.petrak.hexcasting.api.casting.eval.vm.CastingVM.TempControllerInfo, sideEffects: List<OperatorSideEffect>) {
      for (OperatorSideEffect haskellProgrammersShakingandCryingRN : sideEffects) {
         if (haskellProgrammersShakingandCryingRN.performEffect(this)) {
            info.setEarlyExit(true);
            break;
         }
      }
   }

   public fun generateDescs(): Pair<List<CompoundTag>, CompoundTag?> {
      val ravenmind: java.lang.Iterable = this.image.getStack();
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(ravenmind, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(IotaType.serialize(`item$iv$iv` as Iota));
      }

      return new Pair(
         `destination$iv$iv` as java.util.List,
         if (this.image.getUserData().contains(HexAPI.RAVENMIND_USERDATA)) this.image.getUserData().getCompound(HexAPI.RAVENMIND_USERDATA) else null
      );
   }

   @Throws(at/petrak/hexcasting/api/casting/mishaps/MishapTooManyCloseParens::class)
   private fun handleParentheses(iota: Iota): Pair<CastingImage, ResolvedPatternType>? {
      var var32: java.lang.String;
      label92: {
         val var10000: PatternIota = iota as? PatternIota;
         if ((iota as? PatternIota) != null) {
            val var31: HexPattern = var10000.getPattern();
            if (var31 != null) {
               var32 = var31.anglesSignature();
               break label92;
            }
         }

         var32 = null;
      }

      var displayDepth: Int = this.image.getParenCount();
      val var33: Pair;
      if (displayDepth > 0) {
         if (this.image.getEscapeNext()) {
            val newStack: java.util.List = CollectionsKt.toMutableList(this.image.getParenthesized());
            newStack.add(new CastingImage.ParenthesizedIota(iota, true));
            var33 = TuplesKt.to(CastingImage.copy$default(this.image, null, 0, newStack, false, 0L, null, 51, null), ResolvedPatternType.ESCAPED);
         } else if (var32 == SpecialPatterns.CONSIDERATION.anglesSignature()) {
            var33 = TuplesKt.to(CastingImage.copy$default(this.image, null, 0, null, true, 0L, null, 55, null), ResolvedPatternType.EVALUATED);
         } else if (var32 == SpecialPatterns.EVANITION.anglesSignature()) {
            val newParens: java.util.List = CollectionsKt.toMutableList(this.image.getParenthesized());
            val newParensx: CastingImage.ParenthesizedIota = CollectionsKt.removeLastOrNull(newParens) as CastingImage.ParenthesizedIota;
            val var34: Int = this.image.getParenCount();
            val var10001: Int;
            if (newParensx != null && !newParensx.getEscaped() && newParensx.getIota() is PatternIota) {
               val `$i$f$map`: HexPattern = (newParensx.getIota() as PatternIota).getPattern();
               var10001 = if (`$i$f$map` == SpecialPatterns.INTROSPECTION) -1 else (if (`$i$f$map` == SpecialPatterns.RETROSPECTION) 1 else -1);
            } else {
               var10001 = 0;
            }

            var33 = TuplesKt.to(
               CastingImage.copy$default(this.image, null, var34 + var10001, newParens, false, 0L, null, 57, null),
               if (newParensx == null) ResolvedPatternType.ERRORED else ResolvedPatternType.UNDONE
            );
         } else if (var32 == SpecialPatterns.INTROSPECTION.anglesSignature()) {
            val var22: java.util.List = CollectionsKt.toMutableList(this.image.getParenthesized());
            var22.add(new CastingImage.ParenthesizedIota(iota, false));
            var33 = TuplesKt.to(
               CastingImage.copy$default(this.image, null, this.image.getParenCount() + 1, var22, false, 0L, null, 57, null),
               if (this.image.getParenCount() == 0) ResolvedPatternType.EVALUATED else ResolvedPatternType.ESCAPED
            );
         } else if (var32 == SpecialPatterns.RETROSPECTION.anglesSignature()) {
            val var23: Int = this.image.getParenCount() - 1;
            displayDepth--;
            if (var23 == 0) {
               val var26: java.util.List = CollectionsKt.toMutableList(this.image.getStack());
               val var29: java.lang.Iterable = CollectionsKt.toList(this.image.getParenthesized());
               val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var29, 10));

               for (Object item$iv$iv : var29) {
                  `destination$iv$iv`.add((`item$iv$iv` as CastingImage.ParenthesizedIota).getIota());
               }

               var26.add(new ListIota(`destination$iv$iv` as MutableList<Iota>));
               var33 = TuplesKt.to(
                  CastingImage.copy$default(this.image, var26, var23, CollectionsKt.emptyList(), false, 0L, null, 56, null), ResolvedPatternType.EVALUATED
               );
            } else {
               if (var23 < 0) {
                  throw new MishapTooManyCloseParens();
               }

               val var27: java.util.List = CollectionsKt.toMutableList(this.image.getParenthesized());
               var27.add(new CastingImage.ParenthesizedIota(iota, false));
               var33 = TuplesKt.to(CastingImage.copy$default(this.image, null, var23, var27, false, 0L, null, 57, null), ResolvedPatternType.ESCAPED);
            }
         } else {
            val var24: java.util.List = CollectionsKt.toMutableList(this.image.getParenthesized());
            var24.add(new CastingImage.ParenthesizedIota(iota, false));
            var33 = TuplesKt.to(CastingImage.copy$default(this.image, null, 0, var24, false, 0L, null, 59, null), ResolvedPatternType.ESCAPED);
         }
      } else if (this.image.getEscapeNext()) {
         val var21: java.util.List = CollectionsKt.toMutableList(this.image.getStack());
         var21.add(iota);
         var33 = TuplesKt.to(CastingImage.copy$default(this.image, var21, 0, null, false, 0L, null, 54, null), ResolvedPatternType.ESCAPED);
      } else if (var32 == SpecialPatterns.CONSIDERATION.anglesSignature()) {
         var33 = TuplesKt.to(CastingImage.copy$default(this.image, null, 0, null, true, 0L, null, 55, null), ResolvedPatternType.EVALUATED);
      } else if (var32 == SpecialPatterns.INTROSPECTION.anglesSignature()) {
         var33 = TuplesKt.to(
            CastingImage.copy$default(this.image, null, this.image.getParenCount() + 1, null, false, 0L, null, 61, null), ResolvedPatternType.EVALUATED
         );
      } else {
         if (var32 == SpecialPatterns.RETROSPECTION.anglesSignature()) {
            throw new MishapTooManyCloseParens();
         }

         var33 = null;
      }

      return var33;
   }

   public companion object {
      public fun empty(env: CastingEnvironment): CastingVM {
         return new CastingVM(new CastingImage(), env);
      }
   }

   public data class TempControllerInfo(earlyExit: Boolean) {
      public final var earlyExit: Boolean
         internal set

      init {
         this.earlyExit = earlyExit;
      }

      public operator fun component1(): Boolean {
         return this.earlyExit;
      }

      public fun copy(earlyExit: Boolean = this.earlyExit): at.petrak.hexcasting.api.casting.eval.vm.CastingVM.TempControllerInfo {
         return new CastingVM.TempControllerInfo(earlyExit);
      }

      public override fun toString(): String {
         return "TempControllerInfo(earlyExit=${this.earlyExit})";
      }

      public override fun hashCode(): Int {
         return java.lang.Boolean.hashCode(this.earlyExit);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is CastingVM.TempControllerInfo) {
            return false;
         } else {
            return this.earlyExit == (other as CastingVM.TempControllerInfo).earlyExit;
         }
      }
   }
}
