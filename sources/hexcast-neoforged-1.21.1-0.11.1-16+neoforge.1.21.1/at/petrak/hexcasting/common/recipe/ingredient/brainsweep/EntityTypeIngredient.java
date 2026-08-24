package at.petrak.hexcasting.common.recipe.ingredient.brainsweep;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityTypeIngredient extends BrainsweepeeIngredient {
   public final EntityType<?> entityType;

   public EntityTypeIngredient(EntityType<?> entityType) {
      this.entityType = entityType;
   }

   @Override
   public boolean test(Entity entity, ServerLevel level) {
      return entity.getType() == this.entityType;
   }

   @Override
   public Component getName() {
      return this.entityType.getDescription();
   }

   @Override
   public List<Component> getTooltip(boolean advanced) {
      return List.of(
         this.entityType.getDescription(), BrainsweepeeIngredient.getModNameComponent(BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType).getNamespace())
      );
   }

   @Override
   public Entity exampleEntity(Level level) {
      return this.entityType.create(level);
   }

   @Override
   public JsonObject serialize() {
      JsonObject obj = new JsonObject();
      obj.addProperty("type", BrainsweepeeIngredient.Type.ENTITY_TYPE.getSerializedName());
      obj.addProperty("entityType", BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType).toString());
      return obj;
   }

   @Override
   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(BuiltInRegistries.ENTITY_TYPE.getId(this.entityType));
   }

   public static EntityTypeIngredient deserialize(JsonObject obj) {
      ResourceLocation typeLoc = ResourceLocation.tryParse(GsonHelper.getAsString(obj, "entityType"));
      if (typeLoc != null && BuiltInRegistries.ENTITY_TYPE.containsKey(typeLoc)) {
         return new EntityTypeIngredient((EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(typeLoc));
      } else {
         throw new IllegalArgumentException("unknown entity type " + typeLoc);
      }
   }

   public static EntityTypeIngredient read(FriendlyByteBuf buf) {
      int tyId = buf.readVarInt();
      return new EntityTypeIngredient((EntityType<?>)BuiltInRegistries.ENTITY_TYPE.byId(tyId));
   }

   @Override
   public BrainsweepeeIngredient.Type ingrType() {
      return BrainsweepeeIngredient.Type.ENTITY_TYPE;
   }

   @Override
   public String getSomeKindOfReasonableIDForEmi() {
      ResourceLocation resloc = BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType);
      return resloc.getNamespace() + "//" + resloc.getPath();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EntityTypeIngredient that = (EntityTypeIngredient)o;
         return Objects.equals(this.entityType, that.entityType);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.entityType);
   }
}
