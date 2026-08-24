package mezz.jei.common.config.file;

import java.util.List;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;

public interface IConfigCategoryBuilder {
   ConfigValue<Boolean> addBoolean(String var1, boolean var2);

   ConfigValue<Integer> addInteger(String var1, int var2, int var3, int var4);

   <T extends Enum<T>> ConfigValue<T> addEnum(String var1, T var2);

   <T> ConfigValue<List<T>> addList(String var1, List<T> var2, IJeiConfigValueSerializer<List<T>> var3);
}
