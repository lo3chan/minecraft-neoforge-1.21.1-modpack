package at.petrak.hexcasting.common.loot;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import at.petrak.hexcasting.common.lib.HexLootFunctions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AddPerWorldPatternToScrollFunc extends LootItemConditionalFunction {
   public static final MapCodec<AddPerWorldPatternToScrollFunc> CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance).apply(instance, AddPerWorldPatternToScrollFunc::new)
   );

   public AddPerWorldPatternToScrollFunc(List<LootItemCondition> lootItemConditions) {
      super(lootItemConditions);
   }

   public static ItemStack doStatic(ItemStack stack, LootContext ctx) {
      RandomSource rand = ctx.getRandom();
      ArrayList<ResourceKey<ActionRegistryEntry>> perWorldKeys = new ArrayList<>();
      Registry<ActionRegistryEntry> regi = IXplatAbstractions.INSTANCE.getActionRegistry();

      for (ResourceKey<ActionRegistryEntry> key : regi.registryKeySet()) {
         if (HexUtils.isOfTag(regi, key, HexTags.Actions.PER_WORLD_PATTERN)) {
            perWorldKeys.add(key);
         }
      }

      ResourceKey<ActionRegistryEntry> keyx = perWorldKeys.get(rand.nextInt(perWorldKeys.size()));
      HexPattern pat = PatternRegistryManifest.getCanonicalStrokesPerWorld(keyx, ctx.getLevel().getServer().overworld());
      NBTHelper.putString(stack, "op_id", keyx.location().toString());
      NBTHelper.putCompound(stack, "pattern", pat.serializeToNBT());
      return stack;
   }

   protected ItemStack run(ItemStack stack, LootContext ctx) {
      return doStatic(stack, ctx);
   }

   public LootItemFunctionType<AddPerWorldPatternToScrollFunc> getType() {
      return HexLootFunctions.PATTERN_SCROLL;
   }
}
