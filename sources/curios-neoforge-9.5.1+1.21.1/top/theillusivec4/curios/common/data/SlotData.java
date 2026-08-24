package top.theillusivec4.curios.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.conditions.ICondition;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.data.ISlotData;

public class SlotData implements ISlotData {
   private Integer order;
   private Integer size;
   private String operation;
   private Boolean useNativeGui;
   private Boolean hasCosmetic;
   private ResourceLocation icon;
   private ICurio.DropRule dropRule;
   private Boolean renderToggle;
   private Boolean replace;
   private List<ICondition> conditions;
   private Set<ResourceLocation> validators;

   public SlotData replace(boolean replace) {
      this.replace = replace;
      return this;
   }

   public SlotData order(int order) {
      this.order = order;
      return this;
   }

   public SlotData size(int size) {
      this.size = size;
      return this;
   }

   public SlotData operation(String operation) {
      this.operation = operation;
      return this;
   }

   public SlotData operation(Operation operation) {
      this.operation = operation == Operation.ADD_VALUE ? "ADD" : "SET";
      return this;
   }

   public SlotData useNativeGui(boolean useNativeGui) {
      this.useNativeGui = useNativeGui;
      return this;
   }

   public SlotData addCosmetic(boolean addCosmetic) {
      this.hasCosmetic = addCosmetic;
      return this;
   }

   public SlotData renderToggle(boolean renderToggle) {
      this.renderToggle = renderToggle;
      return this;
   }

   public SlotData icon(ResourceLocation icon) {
      this.icon = icon;
      return this;
   }

   public SlotData dropRule(ICurio.DropRule dropRule) {
      this.dropRule = dropRule;
      return this;
   }

   public SlotData addCondition(ICondition condition) {
      if (this.conditions == null) {
         this.conditions = new ArrayList<>();
      }

      this.conditions.add(condition);
      return this;
   }

   @Override
   public ISlotData addValidator(ResourceLocation resourceLocation) {
      if (this.validators == null) {
         this.validators = new LinkedHashSet<>();
      }

      this.validators.add(resourceLocation);
      return this;
   }

   @Override
   public JsonObject serialize(Provider provider) {
      JsonObject jsonObject = new JsonObject();
      if (this.replace != null) {
         jsonObject.addProperty("replace", this.replace);
      }

      if (this.order != null) {
         jsonObject.addProperty("order", this.order);
      }

      if (this.size != null) {
         jsonObject.addProperty("size", this.size);
      }

      if (this.operation != null) {
         jsonObject.addProperty("operation", this.operation);
      }

      if (this.useNativeGui != null) {
         jsonObject.addProperty("use_native_gui", this.useNativeGui);
      }

      if (this.hasCosmetic != null) {
         jsonObject.addProperty("add_cosmetic", this.hasCosmetic);
      }

      if (this.icon != null) {
         jsonObject.addProperty("icon", this.icon.toString());
      }

      if (this.dropRule != null) {
         jsonObject.addProperty("drop_rule", this.dropRule.toString());
      }

      if (this.renderToggle != null) {
         jsonObject.addProperty("render_toggle", this.renderToggle);
      }

      if (this.conditions != null) {
         ICondition.writeConditions(provider, jsonObject, this.conditions);
      }

      if (this.validators != null) {
         JsonArray arr = new JsonArray();

         for (ResourceLocation slotResultPredicate : this.validators) {
            arr.add(slotResultPredicate.toString());
         }

         jsonObject.add("validators", arr);
      }

      return jsonObject;
   }
}
