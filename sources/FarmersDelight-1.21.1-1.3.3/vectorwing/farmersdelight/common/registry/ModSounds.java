package vectorwing.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "farmersdelight");
   public static final Supplier<SoundEvent> BLOCK_STOVE_CRACKLE = SOUNDS.register(
      "block.stove.crackle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.stove.crackle"))
   );
   public static final Supplier<SoundEvent> BLOCK_COOKING_POT_BOIL = SOUNDS.register(
      "block.cooking_pot.boil", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cooking_pot.boil"))
   );
   public static final Supplier<SoundEvent> BLOCK_COOKING_POT_BOIL_SOUP = SOUNDS.register(
      "block.cooking_pot.boil_soup",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cooking_pot.boil_soup"))
   );
   public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_PLACE = SOUNDS.register(
      "block.cutting_board.place_item",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cutting_board.place_item"))
   );
   public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_REMOVE = SOUNDS.register(
      "block.cutting_board.remove_item",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cutting_board.remove_item"))
   );
   public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_CARVE = SOUNDS.register(
      "block.cutting_board.carve_tool",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cutting_board.carve_tool"))
   );
   public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_KNIFE = SOUNDS.register(
      "block.cutting_board.knife_cut",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cutting_board.knife_cut"))
   );
   public static final Supplier<SoundEvent> BLOCK_ROPE_FENCE_GATE_OPEN = SOUNDS.register(
      "block.rope_fence_gate.open",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.rope_fence_gate.open"))
   );
   public static final Supplier<SoundEvent> BLOCK_ROPE_FENCE_GATE_CLOSE = SOUNDS.register(
      "block.rope_fence_gate.close",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.rope_fence_gate.close"))
   );
   public static final Supplier<SoundEvent> BLOCK_CABINET_OPEN = SOUNDS.register(
      "block.cabinet.open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cabinet.open"))
   );
   public static final Supplier<SoundEvent> BLOCK_CABINET_CLOSE = SOUNDS.register(
      "block.cabinet.close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.cabinet.close"))
   );
   public static final Supplier<SoundEvent> BLOCK_SKILLET_SIZZLE = SOUNDS.register(
      "block.skillet.sizzle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.skillet.sizzle"))
   );
   public static final Supplier<SoundEvent> BLOCK_SKILLET_ADD_FOOD = SOUNDS.register(
      "block.skillet.add_food", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.skillet.add_food"))
   );
   public static final Supplier<SoundEvent> ITEM_SKILLET_ATTACK_STRONG = SOUNDS.register(
      "item.skillet.attack.strong",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "item.skillet.attack.strong"))
   );
   public static final Supplier<SoundEvent> ITEM_SKILLET_ATTACK_WEAK = SOUNDS.register(
      "item.skillet.attack.weak",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "item.skillet.attack.weak"))
   );
   public static final Supplier<SoundEvent> BLOCK_TOMATOES_PICK_TOMATOES = SOUNDS.register(
      "block.tomatoes.pick_tomatoes",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.tomatoes.pick_tomatoes"))
   );
   public static final Supplier<SoundEvent> BLOCK_FOOD_TAKE_PORTION = SOUNDS.register(
      "block.food.take_portion", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.food.take_portion"))
   );
   public static final Supplier<SoundEvent> BLOCK_FOOD_SLICE = SOUNDS.register(
      "block.food.slice", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "block.food.slice"))
   );
   public static final Supplier<SoundEvent> ENTITY_ROTTEN_TOMATO_THROW = SOUNDS.register(
      "entity.rotten_tomato.throw",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "entity.rotten_tomato.throw"))
   );
   public static final Supplier<SoundEvent> ENTITY_ROTTEN_TOMATO_HIT = SOUNDS.register(
      "entity.rotten_tomato.hit",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("farmersdelight", "entity.rotten_tomato.hit"))
   );
}
