package net.joefoxe.hexerei.block.connected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class StitchedSprite {
   public static final Map<ResourceLocation, List<StitchedSprite>> ALL = new HashMap<>();
   protected final ResourceLocation atlasLocation;
   protected final ResourceLocation location;
   protected TextureAtlasSprite sprite;

   public StitchedSprite(ResourceLocation atlas, ResourceLocation location) {
      this.atlasLocation = atlas;
      this.location = location;
      ALL.computeIfAbsent(this.atlasLocation, $ -> new ArrayList<>()).add(this);
   }

   public StitchedSprite(ResourceLocation location) {
      this(InventoryMenu.BLOCK_ATLAS, location);
   }

   public void loadSprite(TextureAtlas atlas) {
      this.sprite = atlas.getSprite(this.location);
   }

   public void setSprite(TextureAtlasSprite sprite) {
      this.sprite = sprite;
   }

   public ResourceLocation getAtlasLocation() {
      return this.atlasLocation;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public TextureAtlasSprite get() {
      return this.sprite;
   }
}
