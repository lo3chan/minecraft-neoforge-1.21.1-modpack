package at.petrak.hexcasting.api;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.google.common.base.Suppliers;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public interface HexAPI {
   String MOD_ID = "hexcasting";
   Logger LOGGER = LogManager.getLogger("hexcasting");
   Supplier<HexAPI> INSTANCE = Suppliers.memoize(() -> {
      try {
         return (HexAPI)Class.forName("at.petrak.hexcasting.common.impl.HexAPIImpl").getDeclaredConstructor().newInstance();
      } catch (ReflectiveOperationException var1) {
         LogManager.getLogger().warn("Unable to find HexAPIImpl, using a dummy");
         return new HexAPI() {};
      }
   });
   ArmorMaterial DUMMY_ARMOR_MATERIAL = new ArmorMaterial(
      Map.of(), 0, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, List.of(new Layer(modLoc("missingno"))), 0.0F, 0.0F
   );
   String RAVENMIND_USERDATA = modLoc("ravenmind").toString();
   String OP_COUNT_USERDATA = modLoc("op_count").toString();
   String MARKED_MOVED_USERDATA = modLoc("impulsed").toString();

   default String getActionI18nKey(ResourceKey<ActionRegistryEntry> action) {
      return "hexcasting.action.%s".formatted(action.location().toString());
   }

   default String getSpecialHandlerI18nKey(ResourceKey<SpecialHandler.Factory<?>> action) {
      return "hexcasting.special.%s".formatted(action.location().toString());
   }

   default String getRawHookI18nKey(ResourceLocation name) {
      return "hexcasting.rawhook.%s".formatted(name);
   }

   default Component getActionI18n(ResourceKey<ActionRegistryEntry> key, boolean isGreat) {
      return Component.translatable(this.getActionI18nKey(key)).withStyle(isGreat ? ChatFormatting.GOLD : ChatFormatting.LIGHT_PURPLE);
   }

   default Component getSpecialHandlerI18n(ResourceKey<SpecialHandler.Factory<?>> key) {
      return Component.translatable(this.getSpecialHandlerI18nKey(key)).withStyle(ChatFormatting.LIGHT_PURPLE);
   }

   default Component getRawHookI18n(ResourceLocation name) {
      return Component.translatable(this.getRawHookI18nKey(name)).withStyle(ChatFormatting.LIGHT_PURPLE);
   }

   default <T extends Entity> void registerSpecialVelocityGetter(EntityType<T> key, HexAPI.EntityVelocityGetter<T> getter) {
   }

   default Vec3 getEntityVelocitySpecial(Entity entity) {
      return entity.getDeltaMovement();
   }

   default <T extends Mob> void registerCustomBrainsweepingBehavior(EntityType<T> key, Consumer<T> hook) {
   }

   default Consumer<Mob> defaultBrainsweepingBehavior() {
      return mob -> {};
   }

   default <T extends Mob> Consumer<T> getBrainsweepBehavior(EntityType<T> mobType) {
      return mob -> {};
   }

   default void brainsweep(Mob mob) {
      EntityType<? extends Mob> type = mob.getType();
      Consumer<? extends Mob> behavior = this.getBrainsweepBehavior(type);
      behavior.accept(mob);
      IXplatAbstractions.INSTANCE.setBrainsweepAddlData(mob);
   }

   default boolean isBrainswept(Mob mob) {
      return IXplatAbstractions.INSTANCE.isBrainswept(mob);
   }

   @Nullable
   default Sentinel getSentinel(ServerPlayer player) {
      return null;
   }

   @Nullable
   default ADMediaHolder findMediaHolder(ItemStack stack) {
      return null;
   }

   default FrozenPigment getColorizer(Player player) {
      return FrozenPigment.DEFAULT.get();
   }

   default ArmorMaterial robesMaterial() {
      return DUMMY_ARMOR_MATERIAL;
   }

   static HexAPI instance() {
      return INSTANCE.get();
   }

   static ResourceLocation modLoc(String s) {
      return ResourceLocation.fromNamespaceAndPath("hexcasting", s);
   }

   @FunctionalInterface
   public interface EntityVelocityGetter<T extends Entity> {
      Vec3 getVelocity(T var1);
   }
}
