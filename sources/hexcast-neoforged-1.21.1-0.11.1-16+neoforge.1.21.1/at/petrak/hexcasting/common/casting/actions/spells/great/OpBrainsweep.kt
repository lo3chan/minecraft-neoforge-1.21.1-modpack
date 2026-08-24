package at.petrak.hexcasting.common.casting.actions.spells.great

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapAlreadyBrainswept
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBrainsweep
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe
import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry
import at.petrak.hexcasting.ktxt.AccessorWrappers
import at.petrak.hexcasting.mixin.accessor.AccessorLivingEntity
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpBrainsweep.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpBrainsweep.kt\nat/petrak/hexcasting/common/casting/actions/spells/great/OpBrainsweep\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,89:1\n1#2:90\n*E\n"])
public object OpBrainsweep : SpellAction {
   public open val argc: Int = 2

   public override fun hasCastingSound(ctx: CastingEnvironment): Boolean {
      return false;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val sacrifice: Mob = OperatorUtils.getMob(args, 0, this.getArgc());
      val vecPos: Vec3 = OperatorUtils.getVec3(args, 1, this.getArgc());
      val pos: BlockPos = BlockPos.containing(vecPos as Position);
      env.assertVecInRange(vecPos);
      env.assertEntityInRange(sacrifice as Entity);
      if (!env.canEditBlockAt(pos)) {
         throw new MishapBadLocation(vecPos, "forbidden");
      } else if (sacrifice.getType().is(HexTags.Entities.NO_BRAINSWEEPING)) {
         throw new MishapBadBrainsweep(sacrifice, pos);
      } else if (IXplatAbstractions.INSTANCE.isBrainswept(sacrifice)) {
         throw new MishapAlreadyBrainswept(sacrifice);
      } else {
         val state: BlockState = env.getWorld().getBlockState(pos);
         val recipes: java.util.List = env.getWorld().getRecipeManager().getAllRecipesFor(HexRecipeStuffRegistry.BRAINSWEEP_TYPE);
         val var12: java.util.Iterator = recipes.iterator();

         var var10000: Any;
         while (true) {
            if (var12.hasNext()) {
               val var13: Any = var12.next();
               if (!((var13 as RecipeHolder).value() as BrainsweepRecipe).matches(state, sacrifice as Entity, env.getWorld())) {
                  continue;
               }

               var10000 = (RecipeHolder)var13;
               break;
            }

            var10000 = null;
            break;
         }

         var10000 = var10000;
         if (var10000 != null) {
            val var17: BrainsweepRecipe = var10000.value() as BrainsweepRecipe;
            if (var17 != null) {
               val var10002: RenderedSpell = new OpBrainsweep.Spell(pos, state, sacrifice, var17);
               val var10003: Long = var17.mediaCost();
               val var10: Array<ParticleSpray> = new ParticleSpray[2];
               var var10006: ParticleSpray.Companion = ParticleSpray.Companion;
               var var10007: Vec3 = sacrifice.position();
               var10[0] = ParticleSpray.Companion.cloud$default(var10006, var10007, 1.0, 0, 4, null);
               var10006 = ParticleSpray.Companion;
               var10007 = Vec3.atCenterOf(pos as Vec3i);
               var10[1] = var10006.burst(var10007, 0.3, 100);
               return new SpellAction.Result(var10002, var10003, CollectionsKt.listOf(var10), 0L, 8, null);
            }
         }

         throw new MishapBadBrainsweep(sacrifice, pos);
      }
   }

   override fun awardsCastingStat(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
   }

   override fun executeWithUserdata(args: MutableList<Iota>, env: CastingEnvironment, userData: CompoundTag): SpellAction.Result {
      return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return SpellAction.DefaultImpls.operate(this, env, image, continuation);
   }

   @SourceDebugExtension(["SMAP\nOpBrainsweep.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpBrainsweep.kt\nat/petrak/hexcasting/common/casting/actions/spells/great/OpBrainsweep$Spell\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,89:1\n1#2:90\n*E\n"])
   private data class Spell(pos: BlockPos, state: BlockState, sacrifice: Mob, recipe: BrainsweepRecipe) : RenderedSpell {
      public final val pos: BlockPos
      public final val state: BlockState
      public final val sacrifice: Mob
      public final val recipe: BrainsweepRecipe

      init {
         this.pos = pos;
         this.state = state;
         this.sacrifice = sacrifice;
         this.recipe = recipe;
      }

      public override fun cast(env: CastingEnvironment) {
         env.getWorld().setBlockAndUpdate(this.pos, BrainsweepRecipe.copyProperties(this.state, this.recipe.result()));
         IXplatAbstractions.INSTANCE.setBrainsweepAddlData(this.sacrifice);
         if (this.sacrifice is Villager && HexConfig.server().doVillagersTakeOffenseAtMindMurder()) {
            val var10000: ServerPlayer = env.getCaster();
            if (var10000 != null) {
               AccessorWrappers.tellWitnessesThatIWasMurdered(this.sacrifice as Villager, var10000 as Entity);
            }
         }

         val var5: Mob = this.sacrifice;
         val sound: SoundEvent = (var5 as AccessorLivingEntity).hex$getDeathSound();
         if (sound != null) {
            env.getWorld().playSound(null, this.sacrifice as Entity, sound, SoundSource.AMBIENT, 0.8F, 1.0F);
         }

         env.getWorld().playSound(null, this.sacrifice as Entity, SoundEvents.PLAYER_LEVELUP, SoundSource.AMBIENT, 0.5F, 0.8F);
      }

      public operator fun component1(): BlockPos {
         return this.pos;
      }

      public operator fun component2(): BlockState {
         return this.state;
      }

      public operator fun component3(): Mob {
         return this.sacrifice;
      }

      public operator fun component4(): BrainsweepRecipe {
         return this.recipe;
      }

      public fun copy(pos: BlockPos = this.pos, state: BlockState = this.state, sacrifice: Mob = this.sacrifice, recipe: BrainsweepRecipe = this.recipe): at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep.Spell {
         return new OpBrainsweep.Spell(pos, state, sacrifice, recipe);
      }

      public override fun toString(): String {
         return "Spell(pos=${this.pos}, state=${this.state}, sacrifice=${this.sacrifice}, recipe=${this.recipe})";
      }

      public override fun hashCode(): Int {
         return ((this.pos.hashCode() * 31 + this.state.hashCode()) * 31 + this.sacrifice.hashCode()) * 31 + this.recipe.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpBrainsweep.Spell) {
            return false;
         } else {
            val var2: OpBrainsweep.Spell = other as OpBrainsweep.Spell;
            if (!(this.pos == (other as OpBrainsweep.Spell).pos)) {
               return false;
            } else if (!(this.state == var2.state)) {
               return false;
            } else if (!(this.sacrifice == var2.sacrifice)) {
               return false;
            } else {
               return this.recipe == var2.recipe;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
