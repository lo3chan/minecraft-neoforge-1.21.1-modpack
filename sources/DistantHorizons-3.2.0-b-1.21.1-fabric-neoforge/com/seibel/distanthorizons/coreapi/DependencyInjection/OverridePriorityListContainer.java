package com.seibel.distanthorizons.coreapi.DependencyInjection;

import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.util.ArrayList;

public class OverridePriorityListContainer implements IBindable {
   private final ArrayList<OverridePriorityListContainer.OverridePriorityPair> overridePairList = new ArrayList<>();

   public void addOverride(IDhApiOverrideable override) {
      OverridePriorityListContainer.OverridePriorityPair priorityPair = new OverridePriorityListContainer.OverridePriorityPair(override, override.getPriority());
      this.overridePairList.add(priorityPair);
      this.sortList();
   }

   public boolean removeOverride(IDhApiOverrideable override) {
      return this.overridePairList.removeIf(pair -> pair.override.equals(override));
   }

   public IDhApiOverrideable getOverrideWithLowestPriority() {
      return this.overridePairList.size() == 0 ? null : this.overridePairList.get(this.overridePairList.size() - 1).override;
   }

   public IDhApiOverrideable getOverrideWithHighestPriority() {
      return this.overridePairList.size() != 0 ? this.overridePairList.get(0).override : null;
   }

   public IDhApiOverrideable getCoreOverride() {
      int lastIndex = this.overridePairList.size() - 1;
      return this.overridePairList.get(lastIndex) != null && this.overridePairList.get(lastIndex).priority == -1
         ? this.overridePairList.get(lastIndex).override
         : null;
   }

   public IDhApiOverrideable getOverrideWithPriority(int priority) {
      for (OverridePriorityListContainer.OverridePriorityPair pair : this.overridePairList) {
         if (pair.priority == priority) {
            return pair.override;
         }
      }

      return null;
   }

   private void sortList() {
      this.overridePairList.sort((x, y) -> Integer.compare(y.priority, x.priority));
   }

   private class OverridePriorityPair {
      public final IDhApiOverrideable override;
      public int priority;

      public OverridePriorityPair(IDhApiOverrideable newOverride, int newPriority) {
         this.override = newOverride;
         this.priority = newPriority;
      }
   }
}
