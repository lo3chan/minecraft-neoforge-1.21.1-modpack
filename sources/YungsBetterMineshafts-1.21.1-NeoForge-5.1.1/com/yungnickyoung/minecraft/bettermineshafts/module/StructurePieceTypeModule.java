package com.yungnickyoung.minecraft.bettermineshafts.module;

import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.BigTunnel;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.LayeredIntersection4;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.LayeredIntersection5;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.OreDeposit;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SideRoom;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SideRoomDungeon;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnel;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnelStairs;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnelTurn;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.VerticalEntrance;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.ZombieVillagerRoom;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

@AutoRegister("bettermineshafts")
public class StructurePieceTypeModule {
   @AutoRegister("bmsverticalentrance")
   public static StructurePieceType VERTICAL_ENTRANCE = VerticalEntrance::new;
   @AutoRegister("bmsbigtunnel")
   public static StructurePieceType BIG_TUNNEL = BigTunnel::new;
   @AutoRegister("bmssmalltunnel")
   public static StructurePieceType SMALL_TUNNEL = SmallTunnel::new;
   @AutoRegister("bmssmalltunnelturn")
   public static StructurePieceType SMALL_TUNNEL_TURN = SmallTunnelTurn::new;
   @AutoRegister("bmssmalltunnelstairs")
   public static StructurePieceType SMALL_TUNNEL_STAIRS = SmallTunnelStairs::new;
   @AutoRegister("bmssideroom")
   public static StructurePieceType SIDE_ROOM = SideRoom::new;
   @AutoRegister("bmssideroomdungeon")
   public static StructurePieceType SIDE_ROOM_DUNGEON = SideRoomDungeon::new;
   @AutoRegister("bmsoredeposit")
   public static StructurePieceType ORE_DEPOSIT = OreDeposit::new;
   @AutoRegister("bmslayeredintersection4")
   public static StructurePieceType LAYERED_INTERSECTION_4 = LayeredIntersection4::new;
   @AutoRegister("bmslayeredintersection5")
   public static StructurePieceType LAYERED_INTERSECTION_5 = LayeredIntersection5::new;
   @AutoRegister("bmszombievillagerroom")
   public static StructurePieceType ZOMBIE_VILLAGER_ROOM = ZombieVillagerRoom::new;
}
