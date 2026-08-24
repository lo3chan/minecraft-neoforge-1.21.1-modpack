package com.yungnickyoung.minecraft.yungsapi.world.structure.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public interface StructureActionType<C extends StructureAction> {
   Map<ResourceLocation, StructureActionType<?>> ACTION_TYPES_BY_NAME = new HashMap<>();
   Map<StructureActionType<?>, ResourceLocation> NAME_BY_ACTION_TYPES = new HashMap<>();
   Codec<StructureActionType<?>> ACTION_TYPE_CODEC = ResourceLocation.CODEC
      .flatXmap(
         resourceLocation -> Optional.ofNullable(ACTION_TYPES_BY_NAME.get(resourceLocation))
            .<DataResult>map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "Unknown structure action type: " + resourceLocation)),
         actionType -> Optional.of(NAME_BY_ACTION_TYPES.get(actionType))
            .<DataResult>map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "No ID found for structure action type " + actionType + ". Is it registered?"))
      );
   Codec<StructureAction> ACTION_CODEC = ACTION_TYPE_CODEC.dispatch("type", StructureAction::type, StructureActionType::codec);
   StructureActionType<TransformAction> TRANSFORM = register("transform", TransformAction.CODEC);
   StructureActionType<DelayGenerationAction> DELAY_GENERATION = register("delay_generation", DelayGenerationAction.CODEC);

   static <C extends StructureAction> StructureActionType<C> register(ResourceLocation resourceLocation, MapCodec<C> codec) {
      StructureActionType<C> actionType = () -> codec;
      ACTION_TYPES_BY_NAME.put(resourceLocation, actionType);
      NAME_BY_ACTION_TYPES.put(actionType, resourceLocation);
      return actionType;
   }

   private static <C extends StructureAction> StructureActionType<C> register(String id, MapCodec<C> codec) {
      return register(ResourceLocation.fromNamespaceAndPath("yungsapi", id), codec);
   }

   MapCodec<C> codec();
}
