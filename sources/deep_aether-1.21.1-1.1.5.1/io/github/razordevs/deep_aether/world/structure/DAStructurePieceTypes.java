package io.github.razordevs.deep_aether.world.structure;

import io.github.razordevs.deep_aether.world.structure.brass.BrassRoom;
import java.util.Locale;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DAStructurePieceTypes {
   public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, "deep_aether");
   public static final DeferredHolder<StructurePieceType, StructurePieceType> BRASS_BOSS_ROOM = register("BBossRoom", BrassRoom.BossRoom::new);
   public static final DeferredHolder<StructurePieceType, StructurePieceType> BRASS_ROOM = register("BRoom", BrassRoom::new);

   private static DeferredHolder<StructurePieceType, StructurePieceType> register(String name, StructurePieceType structurePieceType) {
      return STRUCTURE_PIECE_TYPES.register(name.toLowerCase(Locale.ROOT), () -> structurePieceType);
   }
}
