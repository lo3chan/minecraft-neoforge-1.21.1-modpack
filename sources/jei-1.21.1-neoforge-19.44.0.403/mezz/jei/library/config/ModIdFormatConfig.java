package mezz.jei.library.config;

import java.util.List;
import java.util.function.Supplier;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchemaBuilder;
import mezz.jei.common.util.function.CachedSupplierTransformer;
import mezz.jei.library.config.serializers.ChatFormattingSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ModIdFormatConfig implements IModIdFormatConfig {
   protected static final List<ChatFormatting> defaultModNameFormat = List.of(ChatFormatting.BLUE, ChatFormatting.ITALIC);
   public static final String MOD_NAME_FORMAT_CODE = "%MODNAME%";
   private final Supplier<Component> modNameFormat;
   @Nullable
   private Component cachedOverride;

   public ModIdFormatConfig(IConfigSchemaBuilder builder) {
      IConfigCategoryBuilder modName = builder.addCategory("modName");
      Supplier<List<ChatFormatting>> configValue = modName.addList("modNameFormat", defaultModNameFormat, ChatFormattingSerializer.INSTANCE);
      this.modNameFormat = new CachedSupplierTransformer<>(configValue, ModIdFormatConfig::toFormatString);
   }

   private static Component toFormatString(List<ChatFormatting> values) {
      return values.isEmpty() ? Component.empty() : Component.literal("%MODNAME%").withStyle(values.toArray(ChatFormatting[]::new));
   }

   private Component getOverride() {
      if (this.cachedOverride == null) {
         this.cachedOverride = ModIdFormatDetectionHelper.detectModNameTooltipFormatting();
      }

      return this.cachedOverride;
   }

   @Override
   public final Component getModNameFormat() {
      Component override = this.getOverride();
      return !override.getString().isEmpty() ? override : this.modNameFormat.get();
   }

   @Override
   public final boolean isModNameFormatOverrideActive() {
      return !this.getOverride().getString().isEmpty();
   }

   public static Component detectModNameTooltipFormatting(List<Component> tooltip) {
      if (tooltip.size() <= 1) {
         return Component.empty();
      } else {
         for (int lineNum = 1; lineNum < tooltip.size(); lineNum++) {
            Component line = tooltip.get(lineNum);
            Component result = detectModNameTooltipFormatting(line);
            if (!result.getString().isEmpty()) {
               return result;
            }
         }

         return Component.empty();
      }
   }

   private static Component detectModNameTooltipFormatting(Component line) {
      return StyledTextHelper.replaceFirst(line, "Minecraft", Component.literal("%MODNAME%")).orElseGet(Component::empty);
   }

   public static Component replaceModNameFormatCode(Component format, String modName) {
      return StyledTextHelper.replaceFirst(format, "%MODNAME%", Component.literal(modName)).orElseGet(() -> format.copy().append(Component.literal(modName)));
   }
}
