package com.badlogic.gdx.assets;

public class AssetDescriptor<T> {
   public final String fileName;
   public final Class<T> type;
   public final AssetLoaderParameters params;

   public AssetDescriptor(String fileName, Class<T> assetType) {
      this(fileName, assetType, (AssetLoaderParameters)null);
   }

   public AssetDescriptor(String fileName, Class<T> assetType, AssetLoaderParameters<T> params) {
      this.fileName = fileName.replaceAll("\\\\", "/");
      this.type = assetType;
      this.params = params;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(this.fileName);
      buffer.append(", ");
      buffer.append(this.type.getName());
      return buffer.toString();
   }
}
