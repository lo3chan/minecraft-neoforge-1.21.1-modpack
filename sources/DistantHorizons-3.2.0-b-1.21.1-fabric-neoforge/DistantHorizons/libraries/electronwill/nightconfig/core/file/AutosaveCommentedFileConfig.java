package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ConcurrentCommentedConfigWrapper;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ObservedMap;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class AutosaveCommentedFileConfig extends ConcurrentCommentedConfigWrapper<CommentedFileConfig> implements CommentedFileConfig {
   private final Runnable autoSaveListener;

   AutosaveCommentedFileConfig(CommentedFileConfig fileConfig, Runnable autosaveListener) {
      super(fileConfig);
      this.autoSaveListener = autosaveListener;
   }

   private void autoSave() {
      this.save();
      this.autoSaveListener.run();
   }

   @Override
   public void save() {
      this.config.save();
   }

   @Override
   public <T> T set(List<String> path, Object value) {
      T result = super.set(path, value);
      this.autoSave();
      return result;
   }

   @Override
   public boolean add(List<String> path, Object value) {
      boolean result = super.add(path, value);
      this.autoSave();
      return result;
   }

   @Override
   public <T> T remove(List<String> path) {
      T result = super.remove(path);
      this.autoSave();
      return result;
   }

   @Override
   public String setComment(List<String> path, String comment) {
      String result = super.setComment(path, comment);
      this.autoSave();
      return result;
   }

   @Override
   public String removeComment(List<String> path) {
      String result = super.removeComment(path);
      this.autoSave();
      return result;
   }

   @Override
   public void removeAll(UnmodifiableConfig config) {
      super.removeAll(config);
      this.autoSave();
   }

   @Override
   public void putAll(UnmodifiableConfig config) {
      super.putAll(config);
      this.autoSave();
   }

   @Override
   public void clear() {
      super.clear();
      this.autoSave();
   }

   @Override
   public void clearComments() {
      super.clearComments();
      this.autoSave();
   }

   @Override
   public void putAllComments(UnmodifiableCommentedConfig commentedConfig) {
      super.putAllComments(commentedConfig);
      this.autoSave();
   }

   @Override
   public void putAllComments(Map<String, UnmodifiableCommentedConfig.CommentNode> comments) {
      super.putAllComments(comments);
      this.autoSave();
   }

   @Override
   public Map<String, Object> valueMap() {
      return new ObservedMap<>(super.valueMap(), this::autoSave);
   }

   @Override
   public Map<String, String> commentMap() {
      return new ObservedMap<>(super.commentMap(), this::autoSave);
   }

   @Override
   public File getFile() {
      return this.config.getFile();
   }

   @Override
   public Path getNioPath() {
      return this.config.getNioPath();
   }

   @Override
   public void load() {
      this.config.load();
   }

   @Override
   public void close() {
      this.config.close();
   }

   @Override
   public <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> action) {
      R result = this.config.bulkCommentedUpdate(action);
      this.autoSave();
      return result;
   }

   @Override
   public <R> R bulkUpdate(Function<? super Config, R> action) {
      R result = CommentedFileConfig.super.bulkUpdate(action);
      this.autoSave();
      return result;
   }
}
