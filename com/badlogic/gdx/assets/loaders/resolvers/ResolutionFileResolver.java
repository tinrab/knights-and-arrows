package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ResolutionFileResolver implements FileHandleResolver {
   protected final FileHandleResolver baseResolver;
   protected final ResolutionFileResolver.Resolution[] descriptors;

   public ResolutionFileResolver(FileHandleResolver baseResolver, ResolutionFileResolver.Resolution... descriptors) {
      this.baseResolver = baseResolver;
      this.descriptors = descriptors;
   }

   public FileHandle resolve(String fileName) {
      ResolutionFileResolver.Resolution bestDesc = choose(this.descriptors);
      FileHandle originalHandle = new FileHandle(fileName);
      FileHandle handle = this.baseResolver.resolve(this.resolve(originalHandle, bestDesc.suffix));
      if (!handle.exists()) {
         handle = this.baseResolver.resolve(fileName);
      }

      return handle;
   }

   protected String resolve(FileHandle originalHandle, String suffix) {
      return originalHandle.parent() + "/" + suffix + "/" + originalHandle.name();
   }

   public static ResolutionFileResolver.Resolution choose(ResolutionFileResolver.Resolution... descriptors) {
      if (descriptors == null) {
         throw new IllegalArgumentException("descriptors cannot be null.");
      } else {
         int w = Gdx.graphics.getWidth();
         int h = Gdx.graphics.getHeight();
         ResolutionFileResolver.Resolution best = descriptors[0];
         int i;
         int n;
         ResolutionFileResolver.Resolution other;
         if (w < h) {
            i = 0;

            for(n = descriptors.length; i < n; ++i) {
               other = descriptors[i];
               if (w >= other.portraitWidth && other.portraitWidth >= best.portraitWidth && h >= other.portraitHeight && other.portraitHeight >= best.portraitHeight) {
                  best = descriptors[i];
               }
            }
         } else {
            i = 0;

            for(n = descriptors.length; i < n; ++i) {
               other = descriptors[i];
               if (w >= other.portraitHeight && other.portraitHeight >= best.portraitHeight && h >= other.portraitWidth && other.portraitWidth >= best.portraitWidth) {
                  best = descriptors[i];
               }
            }
         }

         return best;
      }
   }

   public static class Resolution {
      public final int portraitWidth;
      public final int portraitHeight;
      public final String suffix;

      public Resolution(int portraitWidth, int portraitHeight, String suffix) {
         this.portraitWidth = portraitWidth;
         this.portraitHeight = portraitHeight;
         this.suffix = suffix;
      }
   }
}
