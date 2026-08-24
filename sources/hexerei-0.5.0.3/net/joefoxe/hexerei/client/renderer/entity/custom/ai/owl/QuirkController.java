package net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl;

import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class QuirkController {
   private List<Quirk> activeQuirks = new ArrayList<>();

   public void addQuirk(Quirk quirk) {
      this.activeQuirks.add(quirk);
   }

   public void tick(OwlEntity owl) {
      for (Quirk quirk : this.activeQuirks) {
         quirk.tick(owl);
      }
   }

   public List<Quirk> getActiveQuirks() {
      return this.activeQuirks;
   }

   public void write(CompoundTag compound) {
      ListTag quirksList = new ListTag();

      for (Quirk quirk : this.activeQuirks) {
         CompoundTag quirkCompound = new CompoundTag();
         quirkCompound.putString("name", quirk.getName());
         quirk.write(quirkCompound);
         quirksList.add(quirkCompound);
      }

      compound.put("activeQuirks", quirksList);
   }

   public void read(CompoundTag compound) {
      this.activeQuirks = new ArrayList<>();
      if (compound.contains("activeQuirks")) {
         ListTag quirksList = compound.getList("activeQuirks", 10);

         for (int i = 0; i < quirksList.size(); i++) {
            CompoundTag quirkCompound = quirksList.getCompound(i);
            String quirkName = quirkCompound.getString("name");
            Quirk quirk = QuirkRegistry.getQuirkByName(quirkName);
            if (quirk != null) {
               quirk.read(quirkCompound);
               this.addQuirk(quirk);
            }
         }
      }
   }
}
