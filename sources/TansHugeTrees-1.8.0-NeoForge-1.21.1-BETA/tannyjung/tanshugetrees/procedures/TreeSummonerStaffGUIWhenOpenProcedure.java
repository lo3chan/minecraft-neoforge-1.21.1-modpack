package tannyjung.tanshugetrees.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.TreeSummonerStaff;

public class TreeSummonerStaffGUIWhenOpenProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         Player player = (Player)entity;
         TreeSummonerStaff.restore(player);
      }
   }
}
