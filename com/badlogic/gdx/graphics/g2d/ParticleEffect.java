package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ParticleEffect implements Disposable {
   private final Array<ParticleEmitter> emitters;

   public ParticleEffect() {
      this.emitters = new Array(8);
   }

   public ParticleEffect(ParticleEffect effect) {
      this.emitters = new Array(true, effect.emitters.size);
      int i = 0;

      for(int n = effect.emitters.size; i < n; ++i) {
         this.emitters.add(new ParticleEmitter((ParticleEmitter)effect.emitters.get(i)));
      }

   }

   public void start() {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).start();
      }

   }

   public void reset() {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).reset();
      }

   }

   public void update(float delta) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).update(delta);
      }

   }

   public void draw(SpriteBatch spriteBatch) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).draw(spriteBatch);
      }

   }

   public void draw(SpriteBatch spriteBatch, float delta) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).draw(spriteBatch, delta);
      }

   }

   public void allowCompletion() {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).allowCompletion();
      }

   }

   public boolean isComplete() {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
         if (!emitter.isComplete()) {
            return false;
         }
      }

      return true;
   }

   public void setDuration(int duration) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
         emitter.setContinuous(false);
         emitter.duration = (float)duration;
         emitter.durationTimer = 0.0F;
      }

   }

   public void setPosition(float x, float y) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).setPosition(x, y);
      }

   }

   public void setFlip(boolean flipX, boolean flipY) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).setFlip(flipX, flipY);
      }

   }

   public void flipY() {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ((ParticleEmitter)this.emitters.get(i)).flipY();
      }

   }

   public Array<ParticleEmitter> getEmitters() {
      return this.emitters;
   }

   public ParticleEmitter findEmitter(String name) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
         if (emitter.getName().equals(name)) {
            return emitter;
         }
      }

      return null;
   }

   public void save(File file) {
      FileWriter output = null;

      try {
         output = new FileWriter(file);
         int index = 0;
         int i = 0;

         for(int n = this.emitters.size; i < n; ++i) {
            ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
            if (index++ > 0) {
               output.write("\n\n");
            }

            emitter.save(output);
            output.write("- Image Path -\n");
            output.write(emitter.getImagePath() + "\n");
         }
      } catch (IOException var14) {
         throw new GdxRuntimeException("Error saving effect: " + file, var14);
      } finally {
         try {
            if (output != null) {
               output.close();
            }
         } catch (IOException var13) {
         }

      }

   }

   public void load(FileHandle effectFile, FileHandle imagesDir) {
      this.loadEmitters(effectFile);
      this.loadEmitterImages(imagesDir);
   }

   public void load(FileHandle effectFile, TextureAtlas atlas) {
      this.loadEmitters(effectFile);
      this.loadEmitterImages(atlas);
   }

   public void loadEmitters(FileHandle effectFile) {
      InputStream input = effectFile.read();
      this.emitters.clear();
      BufferedReader reader = null;

      try {
         reader = new BufferedReader(new InputStreamReader(input), 512);

         do {
            ParticleEmitter emitter = new ParticleEmitter(reader);
            reader.readLine();
            emitter.setImagePath(reader.readLine());
            this.emitters.add(emitter);
         } while(reader.readLine() != null && reader.readLine() != null);
      } catch (IOException var12) {
         throw new GdxRuntimeException("Error loading effect: " + effectFile, var12);
      } finally {
         try {
            if (reader != null) {
               reader.close();
            }
         } catch (IOException var11) {
         }

      }

   }

   public void loadEmitterImages(TextureAtlas atlas) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
         String imagePath = emitter.getImagePath();
         if (imagePath != null) {
            String imageName = (new File(imagePath.replace('\\', '/'))).getName();
            int lastDotIndex = imageName.lastIndexOf(46);
            if (lastDotIndex != -1) {
               imageName = imageName.substring(0, lastDotIndex);
            }

            Sprite sprite = atlas.createSprite(imageName);
            if (sprite == null) {
               throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
            }

            emitter.setSprite(sprite);
         }
      }

   }

   public void loadEmitterImages(FileHandle imagesDir) {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
         String imagePath = emitter.getImagePath();
         if (imagePath != null) {
            String imageName = (new File(imagePath.replace('\\', '/'))).getName();
            Sprite sprite = new Sprite(this.loadTexture(imagesDir.child(imageName)));
            sprite.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            emitter.setSprite(sprite);
         }
      }

   }

   protected Texture loadTexture(FileHandle file) {
      return new Texture(file, false);
   }

   public void dispose() {
      int i = 0;

      for(int n = this.emitters.size; i < n; ++i) {
         ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
         emitter.getSprite().getTexture().dispose();
      }

   }
}
