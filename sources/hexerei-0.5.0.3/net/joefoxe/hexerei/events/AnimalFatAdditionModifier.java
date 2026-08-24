package net.joefoxe.hexerei.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class AnimalFatAdditionModifier extends LootModifier {
   private final Item addition;
   private final float chance;
   private final int base_count;
   public static final MapCodec<AnimalFatAdditionModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> codecStart(instance)
         .and(
            instance.group(
               Codec.STRING.optionalFieldOf("addition", "").forGetter(d -> BuiltInRegistries.ITEM.getKey(d.addition).toString()),
               Codec.FLOAT.optionalFieldOf("chance", 0.45F).forGetter(d -> d.chance),
               Codec.INT.optionalFieldOf("base_count", 1).forGetter(d -> d.base_count)
            )
         )
         .apply(instance, AnimalFatAdditionModifier::new)
   );
   private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "hexerei"
   );
   private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AnimalFatAdditionModifier>> ANIMAL_FAT_DROPS = REGISTER.register(
      "animal_fat_drops", () -> CODEC
   );

   public AnimalFatAdditionModifier(LootItemCondition[] conditionsIn, String addition, float chance, int base_count) {
      super(conditionsIn);
      this.addition = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(addition)).orElse(Items.AIR);
      this.chance = chance;
      this.base_count = base_count;
   }

   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      if (context.hasParam(LootContextParams.ENCHANTMENT_ACTIVE)) {
         if (context.getRandom().nextDouble() / Math.min((Integer)context.getParam(LootContextParams.ENCHANTMENT_LEVEL) + 1, 4) < this.chance) {
            generatedLoot.add(
               new ItemStack(this.addition, context.getRandom().nextInt(Math.min((Integer)context.getParam(LootContextParams.ENCHANTMENT_LEVEL) + 1, 4)) + 1)
            );
         }
      } else if (context.getRandom().nextDouble() < 0.45) {
         generatedLoot.add(new ItemStack(this.addition, 1));
      }

      return generatedLoot;
   }

   public static void init(IEventBus eventBus) {
      REGISTER.register(eventBus);
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return (MapCodec<? extends IGlobalLootModifier>)ANIMAL_FAT_DROPS.get();
   }
}
