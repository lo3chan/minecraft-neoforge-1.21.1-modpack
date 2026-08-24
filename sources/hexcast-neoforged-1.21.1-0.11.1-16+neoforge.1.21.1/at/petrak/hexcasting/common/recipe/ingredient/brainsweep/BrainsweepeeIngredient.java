package at.petrak.hexcasting.common.recipe.ingredient.brainsweep;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class BrainsweepeeIngredient {
   public abstract boolean test(Entity var1, ServerLevel var2);

   public abstract Component getName();

   public abstract List<Component> getTooltip(boolean var1);

   public abstract JsonObject serialize();

   public void wrapWrite(FriendlyByteBuf buf) {
      buf.writeEnum(this.ingrType());
      this.write(buf);
   }

   public abstract void write(FriendlyByteBuf var1);

   @Nullable
   public abstract Entity exampleEntity(Level var1);

   public abstract BrainsweepeeIngredient.Type ingrType();

   public abstract String getSomeKindOfReasonableIDForEmi();

   public static BrainsweepeeIngredient read(FriendlyByteBuf buf) {
      BrainsweepeeIngredient.Type type = (BrainsweepeeIngredient.Type)buf.readEnum(BrainsweepeeIngredient.Type.class);

      return (BrainsweepeeIngredient)(switch (type) {
         case VILLAGER -> VillagerIngredient.read(buf);
         case ENTITY_TYPE -> EntityTypeIngredient.read(buf);
         case ENTITY_TAG -> EntityTagIngredient.read(buf);
      });
   }

   public static BrainsweepeeIngredient deserialize(JsonObject json) {
      String typestr = GsonHelper.getAsString(json, "type");
      BrainsweepeeIngredient.Type type = BrainsweepeeIngredient.Type.valueOf(typestr.toUpperCase(Locale.ROOT));

      return (BrainsweepeeIngredient)(switch (type) {
         case VILLAGER -> VillagerIngredient.deserialize(json);
         case ENTITY_TYPE -> EntityTypeIngredient.deserialize(json);
         case ENTITY_TAG -> EntityTagIngredient.deserialize(json);
      });
   }

   public static Component getModNameComponent(String namespace) {
      String mod = IXplatAbstractions.INSTANCE.getModName(namespace);
      return Component.literal(mod).withStyle(new ChatFormatting[]{ChatFormatting.BLUE, ChatFormatting.ITALIC});
   }

   public static enum Type implements StringRepresentable {
      VILLAGER,
      ENTITY_TYPE,
      ENTITY_TAG;

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
