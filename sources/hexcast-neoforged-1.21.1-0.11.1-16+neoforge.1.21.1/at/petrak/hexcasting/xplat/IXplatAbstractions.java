package at.petrak.hexcasting.xplat;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.addldata.ADVariantItem;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.AltioraAbility;
import at.petrak.hexcasting.api.player.FlightAbility;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.interop.pehkui.PehkuiInterop;
import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.ServiceLoader.Provider;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface IXplatAbstractions {
   GameProfile HEXCASTING = new GameProfile(UUID.fromString("8BE7E9DA-1667-11EE-BE56-0242AC120002"), "[HexCasting]");
   IXplatAbstractions INSTANCE = find();

   Platform platform();

   boolean isModPresent(String var1);

   boolean isPhysicalClient();

   void initPlatformSpecific();

   void sendPacketToPlayer(ServerPlayer var1, IMessage var2);

   void sendPacketNear(Vec3 var1, double var2, ServerLevel var4, IMessage var5);

   void sendPacketTracking(Entity var1, IMessage var2);

   Packet<ClientGamePacketListener> toVanillaClientboundPacket(IMessage var1);

   void setBrainsweepAddlData(Mob var1);

   boolean isBrainswept(Mob var1);

   @Nullable
   FrozenPigment setPigment(Player var1, @Nullable FrozenPigment var2);

   void setSentinel(Player var1, @Nullable Sentinel var2);

   void setFlight(ServerPlayer var1, @Nullable FlightAbility var2);

   void setAltiora(Player var1, @Nullable AltioraAbility var2);

   void setStaffcastImage(ServerPlayer var1, @Nullable CastingImage var2);

   void setPatterns(ServerPlayer var1, List<ResolvedPattern> var2);

   @Nullable
   FlightAbility getFlight(ServerPlayer var1);

   @Nullable
   AltioraAbility getAltiora(Player var1);

   FrozenPigment getPigment(Player var1);

   @Nullable
   Sentinel getSentinel(Player var1);

   CastingVM getStaffcastVM(ServerPlayer var1, InteractionHand var2);

   List<ResolvedPattern> getPatternsSavedInUi(ServerPlayer var1);

   void clearCastingData(ServerPlayer var1);

   @Nullable
   ADMediaHolder findMediaHolder(ItemStack var1);

   @Nullable
   ADMediaHolder findMediaHolder(ServerPlayer var1);

   @Nullable
   ADIotaHolder findDataHolder(ItemStack var1);

   @Nullable
   ADIotaHolder findDataHolder(Entity var1);

   @Nullable
   ADHexHolder findHexHolder(ItemStack var1);

   @Nullable
   ADVariantItem findVariantHolder(ItemStack var1);

   boolean isPigment(ItemStack var1);

   ColorProvider getColorProvider(FrozenPigment var1);

   <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> var1, Block... var2);

   boolean tryPlaceFluid(Level var1, InteractionHand var2, BlockPos var3, Fluid var4);

   boolean drainAllFluid(Level var1, BlockPos var2);

   boolean isCorrectTierForDrops(Tier var1, BlockState var2);

   Ingredient getUnsealedIngredient(ItemStack var1);

   IXplatTags tags();

   Builder isShearsCondition();

   String getModName(String var1);

   Registry<ActionRegistryEntry> getActionRegistry();

   Registry<SpecialHandler.Factory<?>> getSpecialHandlerRegistry();

   Registry<IotaType<?>> getIotaTypeRegistry();

   Registry<Arithmetic> getArithmeticRegistry();

   Registry<ContinuationFrame.Type<?>> getContinuationTypeRegistry();

   Registry<EvalSound> getEvalSoundRegistry();

   boolean isBreakingAllowed(ServerLevel var1, BlockPos var2, BlockState var3, @Nullable Player var4);

   boolean isPlacingAllowed(ServerLevel var1, BlockPos var2, ItemStack var3, @Nullable Player var4);

   PehkuiInterop.ApiAbstraction getPehkuiApi();

   private static IXplatAbstractions find() {
      List<Provider<IXplatAbstractions>> providers = ServiceLoader.load(IXplatAbstractions.class).stream().toList();
      if (providers.size() != 1) {
         String names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
         throw new IllegalStateException("There should be exactly one IXplatAbstractions implementation on the classpath. Found: " + names);
      } else {
         Provider<IXplatAbstractions> provider = providers.get(0);
         HexAPI.LOGGER.debug("Instantiating xplat impl: " + provider.type().getName());
         return provider.get();
      }
   }
}
