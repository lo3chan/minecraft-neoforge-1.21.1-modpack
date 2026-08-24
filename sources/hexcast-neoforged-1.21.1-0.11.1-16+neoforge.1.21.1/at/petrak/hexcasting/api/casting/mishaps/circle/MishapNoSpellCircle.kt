package at.petrak.hexcasting.api.casting.mishaps.circle

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.Holder
import net.minecraft.core.NonNullList
import net.minecraft.core.Holder.Reference
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments

@SourceDebugExtension(["SMAP\nMishapNoSpellCircle.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MishapNoSpellCircle.kt\nat/petrak/hexcasting/api/casting/mishaps/circle/MishapNoSpellCircle\n+ 2 MishapNoSpellCircle.kt\nat/petrak/hexcasting/api/casting/mishaps/circle/MishapNoSpellCircle$dropAll$1\n*L\n1#1,46:1\n18#1,4:47\n22#1,5:52\n18#1,4:57\n22#1,5:62\n19#1,8:67\n18#2:51\n18#2:61\n*S KotlinDebug\n*F\n+ 1 MishapNoSpellCircle.kt\nat/petrak/hexcasting/api/casting/mishaps/circle/MishapNoSpellCircle\n*L\n32#1:47,4\n32#1:52,5\n33#1:57,4\n33#1:62,5\n37#1:67,8\n32#1:51\n33#1:61\n*E\n"])
public class MishapNoSpellCircle : Mishap {
   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.LIGHT_BLUE);
   }

   private inline fun dropAll(player: Player, stacks: MutableList<ItemStack>, filter: (ItemStack) -> Boolean = <unrepresentable>.INSTANCE as Function1) {
      var index: Int = 0;

      for (int var6 = stacks.size(); index < var6; index++) {
         val item: ItemStack = stacks.get(index) as ItemStack;
         if (!item.isEmpty() && filter.invoke(item) as java.lang.Boolean) {
            player.drop(item, true, false);
            val var10002: ItemStack = ItemStack.EMPTY;
            stacks.set(index, var10002);
         }
      }
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      val caster: ServerPlayer = env.getCaster();
      if (caster != null) {
         var var10000: NonNullList = caster.getInventory().items;
         var bindingCurse: java.util.List = var10000 as java.util.List;
         var `$i$f$dropAll`: Int = 0;

         for (int index$iv = stacks$iv.size(); index$iv < index$iv; index$iv++) {
            val `item$iv`: ItemStack = bindingCurse.get(`$i$f$dropAll`) as ItemStack;
            if (!`item$iv`.isEmpty() && true) {
               (caster as Player).drop(`item$iv`, true, false);
               val var10002: ItemStack = ItemStack.EMPTY;
               bindingCurse.set(`$i$f$dropAll`, var10002);
            }
         }

         var10000 = caster.getInventory().offhand;
         bindingCurse = var10000 as java.util.List;
         `$i$f$dropAll` = 0;

         for (int var20 = stacks$iv.size(); index$iv < var20; index$iv++) {
            val var22: ItemStack = bindingCurse.get(`$i$f$dropAll`) as ItemStack;
            if (!var22.isEmpty() && true) {
               (caster as Player).drop(var22, true, false);
               val var27: ItemStack = ItemStack.EMPTY;
               bindingCurse.set(`$i$f$dropAll`, var27);
            }
         }

         val var15: Reference = caster.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE);
         var10000 = caster.getInventory().armor;
         val `stacks$ivx`: java.util.List = var10000 as java.util.List;
         var `index$ivx`: Int = 0;

         for (int var23 = stacks$ivx.size(); index$ivx < var23; index$ivx++) {
            val `item$iv`: ItemStack = `stacks$ivx`.get(`index$ivx`) as ItemStack;
            if (!`item$iv`.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(var15 as Holder, `item$iv`) <= 0) {
               (caster as Player).drop(`item$iv`, true, false);
               val var28: ItemStack = ItemStack.EMPTY;
               `stacks$ivx`.set(`index$ivx`, var28);
            }
         }
      }
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return this.error("no_spell_circle", new Object[0]);
   }
}
