package dev.latvian.mods.kubejs.web;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import dev.latvian.apps.tinyserver.http.HTTPRequest;
import dev.latvian.apps.tinyserver.http.response.HTTPPayload;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.Nullable;

public class KJSHTTPRequest extends HTTPRequest {
   public final BlockableEventLoop<?> eventLoop;

   public KJSHTTPRequest(BlockableEventLoop<?> eventLoop) {
      this.eventLoop = eventLoop;
   }

   public RegistryAccessContainer registries() {
      return RegistryAccessContainer.current;
   }

   public void runInMainThread(Runnable task) {
      this.eventLoop.executeBlocking(task);
   }

   public <T> T supplyInMainThread(Supplier<T> task) {
      return CompletableFuture.supplyAsync(task, this.eventLoop).join();
   }

   public ResourceLocation id(String ns, String path) {
      return ResourceLocation.fromNamespaceAndPath(this.variable(ns).asString(), this.variable(path).asString());
   }

   @Nullable
   public ResourceLocation id() {
      return this.id("namespace", "path");
   }

   public DataComponentPatch components(DynamicOps<Tag> ops) throws CommandSyntaxException {
      String str = this.query("components").asString();
      return str.isEmpty() ? DataComponentPatch.EMPTY : DataComponentWrapper.readPatch(ops, new StringReader("[" + str + "]"));
   }

   public HTTPResponse handleResponse(HTTPPayload payload, HTTPResponse response, @Nullable Throwable error) {
      return response.cors();
   }
}
