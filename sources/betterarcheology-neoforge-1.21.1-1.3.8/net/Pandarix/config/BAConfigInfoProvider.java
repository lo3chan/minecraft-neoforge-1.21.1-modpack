package net.Pandarix.config;

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColor;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigColorValue;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigInfo;
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink;
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue;

public class BAConfigInfoProvider implements ResourcefulConfigInfo {
   private final String id;

   public BAConfigInfoProvider(String id) {
      this.id = id;
   }

   public TranslatableValue title() {
      return new TranslatableValue("BetterArcheology Config");
   }

   public TranslatableValue description() {
      return new TranslatableValue("This is the common config of betterarcheology.");
   }

   public String icon() {
      return "circle";
   }

   public ResourcefulConfigColor color() {
      return (ResourcefulConfigColorValue)() -> "#FF0000";
   }

   public ResourcefulConfigLink[] links() {
      return new ResourcefulConfigLink[0];
   }

   public boolean isHidden() {
      return false;
   }
}
