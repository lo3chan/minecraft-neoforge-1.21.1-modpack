package dev.architectury.registry.client.particle.forge;

import com.mojang.logging.LogUtils;
import dev.architectury.platform.hooks.EventBusesHooks;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleEngine.MutableSpriteSet;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.slf4j.Logger;

public class ParticleProviderRegistryImpl {
   public static final Logger LOGGER = LogUtils.getLogger();
   private static List<Consumer<ParticleProviderRegistryImpl.ParticleProviderRegistrar>> deferred = new ArrayList<>();

   private static <T extends ParticleOptions> void doRegister(
      ParticleProviderRegistryImpl.ParticleProviderRegistrar registrar, ParticleType<T> type, ParticleProvider<T> provider
   ) {
      registrar.register(type, provider);
   }

   private static <T extends ParticleOptions> void doRegister(
      ParticleProviderRegistryImpl.ParticleProviderRegistrar registrar, ParticleType<T> type, ParticleProviderRegistry.DeferredParticleProvider<T> provider
   ) {
      registrar.register(
         type,
         (SpriteParticleRegistration<T>)(sprites -> provider.create(
            new ParticleProviderRegistryImpl.ExtendedSpriteSetImpl(Minecraft.getInstance().particleEngine, sprites)
         ))
      );
   }

   public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
      if (deferred == null) {
         LOGGER.warn("Something is attempting to register particle providers at a later point than intended! This might cause issues!", new Throwable());
         doRegister(ParticleProviderRegistryImpl.ParticleProviderRegistrar.ofFallback(), type, provider);
      } else {
         deferred.add(registrar -> doRegister(registrar, type, provider));
      }
   }

   public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProviderRegistry.DeferredParticleProvider<T> provider) {
      if (deferred == null) {
         LOGGER.warn("Something is attempting to register particle providers at a later point than intended! This might cause issues!", new Throwable());
         doRegister(ParticleProviderRegistryImpl.ParticleProviderRegistrar.ofFallback(), type, provider);
      } else {
         deferred.add(registrar -> doRegister(registrar, type, provider));
      }
   }

   public static void onParticleFactoryRegister(RegisterParticleProvidersEvent event) {
      if (deferred != null) {
         ParticleProviderRegistryImpl.ParticleProviderRegistrar registrar = ParticleProviderRegistryImpl.ParticleProviderRegistrar.ofForge(event);

         for (Consumer<ParticleProviderRegistryImpl.ParticleProviderRegistrar> consumer : deferred) {
            consumer.accept(registrar);
         }

         deferred = null;
      }
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.addListener(ParticleProviderRegistryImpl::onParticleFactoryRegister));
   }

   private static final class ExtendedSpriteSetImpl implements ParticleProviderRegistry.ExtendedSpriteSet {
      private final ParticleEngine engine;
      private final SpriteSet delegate;

      private ExtendedSpriteSetImpl(ParticleEngine engine, SpriteSet delegate) {
         this.engine = engine;
         this.delegate = delegate;
      }

      @Override
      public TextureAtlas getAtlas() {
         return this.engine.textureAtlas;
      }

      @Override
      public List<TextureAtlasSprite> getSprites() {
         return ((MutableSpriteSet)this.delegate).sprites;
      }

      public TextureAtlasSprite get(int i, int j) {
         return this.delegate.get(i, j);
      }

      public TextureAtlasSprite get(RandomSource random) {
         return this.delegate.get(random);
      }
   }

   private interface ParticleProviderRegistrar {
      <T extends ParticleOptions> void register(ParticleType<T> var1, ParticleProvider<T> var2);

      <T extends ParticleOptions> void register(ParticleType<T> var1, SpriteParticleRegistration<T> var2);

      static ParticleProviderRegistryImpl.ParticleProviderRegistrar ofForge(RegisterParticleProvidersEvent event) {
         return new ParticleProviderRegistryImpl.ParticleProviderRegistrar() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
               event.registerSpecial(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, SpriteParticleRegistration<T> registration) {
               event.registerSpriteSet(type, registration);
            }
         };
      }

      static ParticleProviderRegistryImpl.ParticleProviderRegistrar ofFallback() {
         return new ParticleProviderRegistryImpl.ParticleProviderRegistrar() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
               Minecraft.getInstance().particleEngine.register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, SpriteParticleRegistration<T> registration) {
               Minecraft.getInstance().particleEngine.register(type, registration);
            }
         };
      }
   }
}
