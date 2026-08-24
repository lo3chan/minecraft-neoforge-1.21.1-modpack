package net.joefoxe.hexerei.util.message;

import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipes;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public class WoodcutterRecipesPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, WoodcutterRecipesPacket> CODEC = StreamCodec.ofMember(
      WoodcutterRecipesPacket::encode, WoodcutterRecipesPacket::new
   );
   public static final Type<WoodcutterRecipesPacket> TYPE = new Type(HexereiUtil.getResource("woodcutter_recipes"));
   protected final List<WoodcutterRecipe> recipeList;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public WoodcutterRecipesPacket(List<WoodcutterRecipe> recipeList) {
      this.recipeList = recipeList;
   }

   public WoodcutterRecipesPacket(RegistryFriendlyByteBuf buf) {
      int size = buf.readInt();
      List<WoodcutterRecipe> recipes = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         recipes.add(i, (WoodcutterRecipe)WoodcutterRecipe.Serializer.STREAM_CODEC.decode(buf));
      }

      this.recipeList = recipes;
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.recipeList.size());

      for (WoodcutterRecipe recipe : this.recipeList) {
         WoodcutterRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe);
      }
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      WoodcutterRecipes.ALL = this.recipeList;
   }
}
