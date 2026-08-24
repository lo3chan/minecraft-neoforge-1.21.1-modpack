package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.minecraft.core.HolderLookup.Provider;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

public class Variable implements IVariable {
   private final JsonElement value;
   @Nullable
   private final Class<?> sourceClass;
   private final Provider registries;

   public Variable(JsonElement elem, Class<?> c, Provider provider) {
      this.value = Objects.requireNonNull(elem);
      this.sourceClass = c;
      this.registries = provider;
   }

   @Override
   public <T> T as(Class<T> clazz) {
      IVariableSerializer<T> serializer = VariableHelper.instance().serializerForClass(clazz);
      if (serializer == null) {
         throw new IllegalArgumentException(String.format("Can't deserialize object of class %s from IVariable", clazz));
      } else {
         return serializer.fromJson(this.value, this.registries);
      }
   }

   @Override
   public JsonElement unwrap() {
      return this.value;
   }

   @Override
   public String toString() {
      return this.value.toString();
   }
}
