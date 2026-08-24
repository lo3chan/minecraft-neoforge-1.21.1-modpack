package vazkii.psi.api;

import java.util.Collection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.RegistryBuilder;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADData;
import vazkii.psi.api.cad.IPsiBarDisplay;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.internal.DummyMethodHandler;
import vazkii.psi.api.internal.IInternalMethodHandler;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.ISpellImmune;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.detonator.IDetonationHandler;
import vazkii.psi.common.item.base.ModItems;

public final class PsiAPI {
   public static final String MOD_ID = "psi";
   public static final EntityCapability<ISpellImmune, Void> SPELL_IMMUNE_CAPABILITY = EntityCapability.createVoid(location("spell_immune"), ISpellImmune.class);
   public static final EntityCapability<IDetonationHandler, Void> DETONATION_HANDLER_CAPABILITY = EntityCapability.createVoid(
      location("detonation_handler"), IDetonationHandler.class
   );
   public static final ItemCapability<IPsiBarDisplay, Void> PSI_BAR_DISPLAY_CAPABILITY = ItemCapability.createVoid(
      location("psi_bar_display"), IPsiBarDisplay.class
   );
   public static final ItemCapability<ISpellAcceptor, Void> SPELL_ACCEPTOR_CAPABILITY = ItemCapability.createVoid(
      location("spell_acceptor"), ISpellAcceptor.class
   );
   public static final ItemCapability<ICADData, Void> CAD_DATA_CAPABILITY = ItemCapability.createVoid(location("cad_data"), ICADData.class);
   public static final ItemCapability<ISocketable, Void> SOCKETABLE_CAPABILITY = ItemCapability.createVoid(location("socketable"), ISocketable.class);
   public static final ResourceKey<Registry<Class<? extends SpellPiece>>> SPELL_PIECE_REGISTRY_TYPE_KEY = ResourceKey.createRegistryKey(
      location("spell_piece_registry_type_key")
   );
   public static final Registry<Class<? extends SpellPiece>> SPELL_PIECE_REGISTRY = new RegistryBuilder(SPELL_PIECE_REGISTRY_TYPE_KEY).create();
   public static final ResourceKey<Registry<Collection<Class<? extends SpellPiece>>>> ADVANCEMENT_GROUP_REGISTRY_KEY = ResourceKey.createRegistryKey(
      location("advancement_group_registry_key")
   );
   public static final Registry<Collection<Class<? extends SpellPiece>>> ADVANCEMENT_GROUP_REGISTRY = new RegistryBuilder(ADVANCEMENT_GROUP_REGISTRY_KEY)
      .create();
   public static final Tier PSIMETAL_TOOL_MATERIAL = new SimpleTier(
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 900, 7.8F, 2.0F, 12, () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.psimetal.get()})
   );
   public static IInternalMethodHandler internalHandler = new DummyMethodHandler();

   public static ItemStack getPlayerCAD(Player player) {
      if (player == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack cad = ItemStack.EMPTY;

         for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stackAt = player.getInventory().getItem(i);
            if (!stackAt.isEmpty() && stackAt.getItem() instanceof ICAD) {
               if (!cad.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               cad = stackAt;
            }
         }

         return cad;
      }
   }

   public static int getPlayerCADSlot(Player player) {
      if (player == null) {
         return -1;
      } else {
         int slot = -1;

         for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stackAt = player.getInventory().getItem(i);
            if (!stackAt.isEmpty() && stackAt.getItem() instanceof ICAD) {
               if (slot != -1) {
                  return -1;
               }

               slot = i;
            }
         }

         return slot;
      }
   }

   public static boolean canCADBeUpdated(Player player) {
      if (player == null) {
         return false;
      } else if (player.containerMenu == null) {
         return true;
      } else {
         int cadSlot = getPlayerCADSlot(player);
         return cadSlot < 9 || cadSlot == 40;
      }
   }

   public static ResourceLocation location(String path) {
      return ResourceLocation.fromNamespaceAndPath("psi", path);
   }
}
