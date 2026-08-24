package net.mehvahdjukaar.moonlight.api.client;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PostShadersHelper {
   public static void toggleEffect(@Nullable ResourceLocation newPost, PostShadersHelper.Group group) {
      GameRenderer gr = Minecraft.getInstance().gameRenderer;

      try {
         RenderTarget target = gr.postEffect != null ? gr.postEffect.screenTarget : Minecraft.getInstance().getMainRenderTarget();
         gr.postEffect = refreshComposite(gr.postEffect, newPost, group, target);
         gr.effectActive = gr.postEffect != null;
      } catch (IOException var4) {
         Moonlight.LOGGER.warn("Failed to load shader: {}", newPost, var4);
         gr.effectActive = false;
      } catch (JsonSyntaxException var5) {
         Moonlight.LOGGER.warn("Failed to parse shader: {}", newPost, var5);
         gr.effectActive = false;
      }
   }

   @Nullable
   public static PostChain refreshComposite(
      @Nullable PostChain currentChain, @Nullable ResourceLocation newPost, PostShadersHelper.Group group, RenderTarget mainTarget
   ) throws IOException {
      PostChain newChain;
      if (currentChain == null) {
         if (newPost == null) {
            return null;
         }

         newChain = PostShadersHelper.ComposedPostChain.create(newPost, group, mainTarget);
      } else if (currentChain instanceof PostShadersHelper.ComposedPostChain cpc) {
         newChain = cpc.with(newPost, group);
      } else if (currentChain.passes.isEmpty()) {
         if (newPost == null) {
            return null;
         }

         newChain = PostShadersHelper.ComposedPostChain.create(newPost, group, mainTarget);
      } else {
         PostShadersHelper.ComposedPostChain wrapped = PostShadersHelper.ComposedPostChain.wrap(currentChain, PostShadersHelper.Group.DEFAULT);
         newChain = newPost == null ? wrapped : wrapped.with(newPost, group);
         if (newChain == null) {
            newChain = wrapped;
         }
      }

      return newChain;
   }

   private static final class ComposedPostChain extends PostChain {
      private final Map<PostShadersHelper.Group, PostChain> chainsPerGroup = new HashMap<>();

      private ComposedPostChain(TextureManager textureManager, ResourceProvider resourceProvider, RenderTarget screenTarget, ResourceLocation resourceLocation) throws IOException, JsonSyntaxException {
         super(textureManager, resourceProvider, screenTarget, resourceLocation);
      }

      public void close() {
         for (PostChain sub : this.chainsPerGroup.values()) {
            sub.close();
         }

         this.chainsPerGroup.clear();
         this.passes.clear();
         this.customRenderTargets.clear();
         this.fullSizedTargets.clear();
      }

      public void load(@NotNull TextureManager textureManager, @NotNull ResourceLocation resourceLocation) throws IOException, JsonSyntaxException {
      }

      private static PostShadersHelper.ComposedPostChain wrap(PostChain vanillaChain, PostShadersHelper.Group group) throws IOException {
         Minecraft mc = Minecraft.getInstance();
         TextureManager tm = mc.getTextureManager();
         ResourceManager rm = mc.getResourceManager();
         PostShadersHelper.ComposedPostChain cc = new PostShadersHelper.ComposedPostChain(
            tm, rm, vanillaChain.screenTarget, ResourceLocation.parse(vanillaChain.getName())
         );
         cc.addSubChain(vanillaChain, group);
         return cc;
      }

      private static PostShadersHelper.ComposedPostChain create(ResourceLocation postEffect, PostShadersHelper.Group group, RenderTarget mainTarget) throws IOException {
         Minecraft mc = Minecraft.getInstance();
         TextureManager tm = mc.getTextureManager();
         ResourceManager rm = mc.getResourceManager();
         PostChain vanillaChain = new PostChain(tm, rm, mainTarget, postEffect);
         vanillaChain.resize(mainTarget.width, mainTarget.height);
         return wrap(vanillaChain, group);
      }

      @Nullable
      private PostShadersHelper.ComposedPostChain with(@Nullable ResourceLocation newEffect, PostShadersHelper.Group group) throws IOException {
         Minecraft mc = Minecraft.getInstance();
         TextureManager tm = mc.getTextureManager();
         ResourceManager rm = mc.getResourceManager();
         if (newEffect != null) {
            PostChain existing = this.chainsPerGroup.get(group);
            if (existing != null && existing.getName().equals(newEffect.toString())) {
               return this;
            }
         }

         Map<PostShadersHelper.Group, PostChain> newGroups = new HashMap<>(this.chainsPerGroup);
         if (newEffect == null) {
            PostChain removed = newGroups.remove(group);
            if (removed != null) {
               removed.close();
            }

            if (newGroups.isEmpty()) {
               return null;
            }
         } else {
            PostChain newChain = new PostChain(tm, rm, this.screenTarget, newEffect);
            newChain.resize(this.screenTarget.width, this.screenTarget.height);
            PostChain old = newGroups.put(group, newChain);
            if (old != null) {
               old.close();
            }
         }

         List<Entry<PostShadersHelper.Group, PostChain>> ordered = newGroups.entrySet()
            .stream()
            .sorted((a, b) -> Float.compare(a.getKey().priority(), b.getKey().priority()))
            .toList();
         ResourceLocation newName = ordered.size() == 1
            ? ResourceLocation.parse(((PostChain)((Entry)ordered.getFirst()).getValue()).getName())
            : ResourceLocation.withDefaultNamespace(
               "composed/" + ordered.stream().map(e -> e.getValue().getName().replace(":", "_")).reduce((a, b) -> a + "_" + b).orElse("empty")
            );
         PostShadersHelper.ComposedPostChain result = new PostShadersHelper.ComposedPostChain(tm, rm, this.screenTarget, newName);

         for (Entry<PostShadersHelper.Group, PostChain> entry : ordered) {
            PostChain pc = entry.getValue();
            result.addSubChain(pc, entry.getKey());
         }

         result.resize(this.screenTarget.width, this.screenTarget.height);
         return result;
      }

      private void addSubChain(PostChain chain, PostShadersHelper.Group group) {
         this.chainsPerGroup.put(group, chain);
         this.passes.addAll(chain.passes);
         this.customRenderTargets.putAll(chain.customRenderTargets);
         this.fullSizedTargets.addAll(chain.fullSizedTargets);
         this.time = chain.time;
         this.lastStamp = chain.lastStamp;
      }
   }

   public record Group(ResourceLocation id, float priority) {
      public static final PostShadersHelper.Group DEFAULT = new PostShadersHelper.Group(ResourceLocation.withDefaultNamespace("default"), 0.0F);
      public static final PostShadersHelper.Group SPECTATOR_SHADERS = new PostShadersHelper.Group(
         ResourceLocation.withDefaultNamespace("spectator_shaders"), 1.0F
      );
   }
}
