package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class ParticleEmitter {
   private static final int UPDATE_SCALE = 1;
   private static final int UPDATE_ANGLE = 2;
   private static final int UPDATE_ROTATION = 4;
   private static final int UPDATE_VELOCITY = 8;
   private static final int UPDATE_WIND = 16;
   private static final int UPDATE_GRAVITY = 32;
   private static final int UPDATE_TINT = 64;
   private ParticleEmitter.RangedNumericValue delayValue = new ParticleEmitter.RangedNumericValue();
   private ParticleEmitter.ScaledNumericValue lifeOffsetValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.RangedNumericValue durationValue = new ParticleEmitter.RangedNumericValue();
   private ParticleEmitter.ScaledNumericValue lifeValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue emissionValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue scaleValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue rotationValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue velocityValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue angleValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue windValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue gravityValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue transparencyValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.GradientColorValue tintValue = new ParticleEmitter.GradientColorValue();
   private ParticleEmitter.RangedNumericValue xOffsetValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.RangedNumericValue yOffsetValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue spawnWidthValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.ScaledNumericValue spawnHeightValue = new ParticleEmitter.ScaledNumericValue();
   private ParticleEmitter.SpawnShapeValue spawnShapeValue = new ParticleEmitter.SpawnShapeValue();
   private float accumulator;
   private Sprite sprite;
   private ParticleEmitter.Particle[] particles;
   private int minParticleCount;
   private int maxParticleCount = 4;
   private float x;
   private float y;
   private String name;
   private String imagePath;
   private int activeCount;
   private boolean[] active;
   private boolean firstUpdate;
   private boolean flipX;
   private boolean flipY;
   private int updateFlags;
   private boolean allowCompletion;
   private int emission;
   private int emissionDiff;
   private int emissionDelta;
   private int lifeOffset;
   private int lifeOffsetDiff;
   private int life;
   private int lifeDiff;
   private float spawnWidth;
   private float spawnWidthDiff;
   private float spawnHeight;
   private float spawnHeightDiff;
   public float duration = 1.0F;
   public float durationTimer;
   private float delay;
   private float delayTimer;
   private boolean attached;
   private boolean continuous;
   private boolean aligned;
   private boolean behind;
   private boolean additive = true;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape;

   public ParticleEmitter() {
      this.initialize();
   }

   public ParticleEmitter(BufferedReader reader) throws IOException {
      this.initialize();
      this.load(reader);
   }

   public ParticleEmitter(ParticleEmitter emitter) {
      this.sprite = emitter.sprite;
      this.name = emitter.name;
      this.setMaxParticleCount(emitter.maxParticleCount);
      this.minParticleCount = emitter.minParticleCount;
      this.delayValue.load(emitter.delayValue);
      this.durationValue.load(emitter.durationValue);
      this.emissionValue.load(emitter.emissionValue);
      this.lifeValue.load(emitter.lifeValue);
      this.lifeOffsetValue.load(emitter.lifeOffsetValue);
      this.scaleValue.load(emitter.scaleValue);
      this.rotationValue.load(emitter.rotationValue);
      this.velocityValue.load(emitter.velocityValue);
      this.angleValue.load(emitter.angleValue);
      this.windValue.load(emitter.windValue);
      this.gravityValue.load(emitter.gravityValue);
      this.transparencyValue.load(emitter.transparencyValue);
      this.tintValue.load(emitter.tintValue);
      this.xOffsetValue.load(emitter.xOffsetValue);
      this.yOffsetValue.load(emitter.yOffsetValue);
      this.spawnWidthValue.load(emitter.spawnWidthValue);
      this.spawnHeightValue.load(emitter.spawnHeightValue);
      this.spawnShapeValue.load(emitter.spawnShapeValue);
      this.attached = emitter.attached;
      this.continuous = emitter.continuous;
      this.aligned = emitter.aligned;
      this.behind = emitter.behind;
      this.additive = emitter.additive;
   }

   private void initialize() {
      this.durationValue.setAlwaysActive(true);
      this.emissionValue.setAlwaysActive(true);
      this.lifeValue.setAlwaysActive(true);
      this.scaleValue.setAlwaysActive(true);
      this.transparencyValue.setAlwaysActive(true);
      this.spawnShapeValue.setAlwaysActive(true);
      this.spawnWidthValue.setAlwaysActive(true);
      this.spawnHeightValue.setAlwaysActive(true);
   }

   public void setMaxParticleCount(int maxParticleCount) {
      this.maxParticleCount = maxParticleCount;
      this.active = new boolean[maxParticleCount];
      this.activeCount = 0;
      this.particles = new ParticleEmitter.Particle[maxParticleCount];
   }

   public void addParticle() {
      int activeCount = this.activeCount;
      if (activeCount != this.maxParticleCount) {
         boolean[] active = this.active;
         int i = 0;

         for(int n = active.length; i < n; ++i) {
            if (!active[i]) {
               this.activateParticle(i);
               active[i] = true;
               this.activeCount = activeCount + 1;
               break;
            }
         }

      }
   }

   public void addParticles(int count) {
      count = Math.min(count, this.maxParticleCount - this.activeCount);
      if (count != 0) {
         boolean[] active = this.active;
         int index = 0;
         int n = active.length;
         int i = 0;

         label25:
         while(i < count) {
            while(true) {
               if (index >= n) {
                  break label25;
               }

               if (!active[index]) {
                  this.activateParticle(index);
                  active[index++] = true;
                  ++i;
                  break;
               }

               ++index;
            }
         }

         this.activeCount += count;
      }
   }

   public void update(float delta) {
      this.accumulator += Math.min(delta * 1000.0F, 250.0F);
      if (!(this.accumulator < 1.0F)) {
         int deltaMillis = (int)this.accumulator;
         this.accumulator -= (float)deltaMillis;
         boolean[] active = this.active;
         int activeCount = this.activeCount;
         int i = 0;

         int emitCount;
         for(emitCount = active.length; i < emitCount; ++i) {
            if (active[i] && !this.updateParticle(this.particles[i], delta, deltaMillis)) {
               active[i] = false;
               --activeCount;
            }
         }

         this.activeCount = activeCount;
         if (this.delayTimer < this.delay) {
            this.delayTimer += (float)deltaMillis;
         } else {
            if (this.firstUpdate) {
               this.firstUpdate = false;
               this.addParticle();
            }

            if (this.durationTimer < this.duration) {
               this.durationTimer += (float)deltaMillis;
            } else {
               if (!this.continuous || this.allowCompletion) {
                  return;
               }

               this.restart();
            }

            this.emissionDelta += deltaMillis;
            float emissionTime = (float)this.emission + (float)this.emissionDiff * this.emissionValue.getScale(this.durationTimer / this.duration);
            if (emissionTime > 0.0F) {
               emissionTime = 1000.0F / emissionTime;
               if ((float)this.emissionDelta >= emissionTime) {
                  emitCount = (int)((float)this.emissionDelta / emissionTime);
                  emitCount = Math.min(emitCount, this.maxParticleCount - activeCount);
                  this.emissionDelta = (int)((float)this.emissionDelta - (float)emitCount * emissionTime);
                  this.emissionDelta = (int)((float)this.emissionDelta % emissionTime);
                  this.addParticles(emitCount);
               }
            }

            if (activeCount < this.minParticleCount) {
               this.addParticles(this.minParticleCount - activeCount);
            }

         }
      }
   }

   public void draw(SpriteBatch spriteBatch) {
      if (this.additive) {
         spriteBatch.setBlendFunction(770, 1);
      }

      ParticleEmitter.Particle[] particles = this.particles;
      boolean[] active = this.active;
      int activeCount = this.activeCount;
      int i = 0;

      for(int n = active.length; i < n; ++i) {
         if (active[i]) {
            particles[i].draw(spriteBatch);
         }
      }

      this.activeCount = activeCount;
      if (this.additive) {
         spriteBatch.setBlendFunction(770, 771);
      }

   }

   public void draw(SpriteBatch spriteBatch, float delta) {
      this.accumulator += Math.min(delta * 1000.0F, 250.0F);
      if (this.accumulator < 1.0F) {
         this.draw(spriteBatch);
      } else {
         int deltaMillis = (int)this.accumulator;
         this.accumulator -= (float)deltaMillis;
         if (this.additive) {
            spriteBatch.setBlendFunction(770, 1);
         }

         ParticleEmitter.Particle[] particles = this.particles;
         boolean[] active = this.active;
         int activeCount = this.activeCount;
         int i = 0;

         int emitCount;
         for(emitCount = active.length; i < emitCount; ++i) {
            if (active[i]) {
               ParticleEmitter.Particle particle = particles[i];
               if (this.updateParticle(particle, delta, deltaMillis)) {
                  particle.draw(spriteBatch);
               } else {
                  active[i] = false;
                  --activeCount;
               }
            }
         }

         this.activeCount = activeCount;
         if (this.additive) {
            spriteBatch.setBlendFunction(770, 771);
         }

         if (this.delayTimer < this.delay) {
            this.delayTimer += (float)deltaMillis;
         } else {
            if (this.firstUpdate) {
               this.firstUpdate = false;
               this.addParticle();
            }

            if (this.durationTimer < this.duration) {
               this.durationTimer += (float)deltaMillis;
            } else {
               if (!this.continuous || this.allowCompletion) {
                  return;
               }

               this.restart();
            }

            this.emissionDelta += deltaMillis;
            float emissionTime = (float)this.emission + (float)this.emissionDiff * this.emissionValue.getScale(this.durationTimer / this.duration);
            if (emissionTime > 0.0F) {
               emissionTime = 1000.0F / emissionTime;
               if ((float)this.emissionDelta >= emissionTime) {
                  emitCount = (int)((float)this.emissionDelta / emissionTime);
                  emitCount = Math.min(emitCount, this.maxParticleCount - activeCount);
                  this.emissionDelta = (int)((float)this.emissionDelta - (float)emitCount * emissionTime);
                  this.emissionDelta = (int)((float)this.emissionDelta % emissionTime);
                  this.addParticles(emitCount);
               }
            }

            if (activeCount < this.minParticleCount) {
               this.addParticles(this.minParticleCount - activeCount);
            }

         }
      }
   }

   public void start() {
      this.firstUpdate = true;
      this.allowCompletion = false;
      this.restart();
   }

   public void reset() {
      this.emissionDelta = 0;
      this.durationTimer = this.duration;
      this.start();
   }

   private void restart() {
      this.delay = this.delayValue.active ? this.delayValue.newLowValue() : 0.0F;
      this.delayTimer = 0.0F;
      this.durationTimer -= this.duration;
      this.duration = this.durationValue.newLowValue();
      this.emission = (int)this.emissionValue.newLowValue();
      this.emissionDiff = (int)this.emissionValue.newHighValue();
      if (!this.emissionValue.isRelative()) {
         this.emissionDiff -= this.emission;
      }

      this.life = (int)this.lifeValue.newLowValue();
      this.lifeDiff = (int)this.lifeValue.newHighValue();
      if (!this.lifeValue.isRelative()) {
         this.lifeDiff -= this.life;
      }

      this.lifeOffset = this.lifeOffsetValue.active ? (int)this.lifeOffsetValue.newLowValue() : 0;
      this.lifeOffsetDiff = (int)this.lifeOffsetValue.newHighValue();
      if (!this.lifeOffsetValue.isRelative()) {
         this.lifeOffsetDiff -= this.lifeOffset;
      }

      this.spawnWidth = this.spawnWidthValue.newLowValue();
      this.spawnWidthDiff = this.spawnWidthValue.newHighValue();
      if (!this.spawnWidthValue.isRelative()) {
         this.spawnWidthDiff -= this.spawnWidth;
      }

      this.spawnHeight = this.spawnHeightValue.newLowValue();
      this.spawnHeightDiff = this.spawnHeightValue.newHighValue();
      if (!this.spawnHeightValue.isRelative()) {
         this.spawnHeightDiff -= this.spawnHeight;
      }

      this.updateFlags = 0;
      if (this.angleValue.active && this.angleValue.timeline.length > 1) {
         this.updateFlags |= 2;
      }

      if (this.velocityValue.active && this.velocityValue.active) {
         this.updateFlags |= 8;
      }

      if (this.scaleValue.timeline.length > 1) {
         this.updateFlags |= 1;
      }

      if (this.rotationValue.active && this.rotationValue.timeline.length > 1) {
         this.updateFlags |= 4;
      }

      if (this.windValue.active) {
         this.updateFlags |= 16;
      }

      if (this.gravityValue.active) {
         this.updateFlags |= 32;
      }

      if (this.tintValue.timeline.length > 1) {
         this.updateFlags |= 64;
      }

   }

   protected ParticleEmitter.Particle newParticle(Sprite sprite) {
      return new ParticleEmitter.Particle(sprite);
   }

   private void activateParticle(int index) {
      ParticleEmitter.Particle particle = this.particles[index];
      if (particle == null) {
         this.particles[index] = particle = this.newParticle(this.sprite);
         particle.flip(this.flipX, this.flipY);
      }

      float percent = this.durationTimer / this.duration;
      int updateFlags = this.updateFlags;
      particle.currentLife = particle.life = this.life + (int)((float)this.lifeDiff * this.lifeValue.getScale(percent));
      if (this.velocityValue.active) {
         particle.velocity = this.velocityValue.newLowValue();
         particle.velocityDiff = this.velocityValue.newHighValue();
         if (!this.velocityValue.isRelative()) {
            particle.velocityDiff -= particle.velocity;
         }
      }

      particle.angle = this.angleValue.newLowValue();
      particle.angleDiff = this.angleValue.newHighValue();
      if (!this.angleValue.isRelative()) {
         particle.angleDiff -= particle.angle;
      }

      float angle = 0.0F;
      if ((updateFlags & 2) == 0) {
         angle = particle.angle + particle.angleDiff * this.angleValue.getScale(0.0F);
         particle.angle = angle;
         particle.angleCos = MathUtils.cosDeg(angle);
         particle.angleSin = MathUtils.sinDeg(angle);
      }

      float spriteWidth = this.sprite.getWidth();
      particle.scale = this.scaleValue.newLowValue() / spriteWidth;
      particle.scaleDiff = this.scaleValue.newHighValue() / spriteWidth;
      if (!this.scaleValue.isRelative()) {
         particle.scaleDiff -= particle.scale;
      }

      particle.setScale(particle.scale + particle.scaleDiff * this.scaleValue.getScale(0.0F));
      if (this.rotationValue.active) {
         particle.rotation = this.rotationValue.newLowValue();
         particle.rotationDiff = this.rotationValue.newHighValue();
         if (!this.rotationValue.isRelative()) {
            particle.rotationDiff -= particle.rotation;
         }

         float rotation = particle.rotation + particle.rotationDiff * this.rotationValue.getScale(0.0F);
         if (this.aligned) {
            rotation += angle;
         }

         particle.setRotation(rotation);
      }

      if (this.windValue.active) {
         particle.wind = this.windValue.newLowValue();
         particle.windDiff = this.windValue.newHighValue();
         if (!this.windValue.isRelative()) {
            particle.windDiff -= particle.wind;
         }
      }

      if (this.gravityValue.active) {
         particle.gravity = this.gravityValue.newLowValue();
         particle.gravityDiff = this.gravityValue.newHighValue();
         if (!this.gravityValue.isRelative()) {
            particle.gravityDiff -= particle.gravity;
         }
      }

      float[] color = particle.tint;
      if (color == null) {
         particle.tint = color = new float[3];
      }

      float[] temp = this.tintValue.getColor(0.0F);
      color[0] = temp[0];
      color[1] = temp[1];
      color[2] = temp[2];
      particle.transparency = this.transparencyValue.newLowValue();
      particle.transparencyDiff = this.transparencyValue.newHighValue() - particle.transparency;
      float x = this.x;
      if (this.xOffsetValue.active) {
         x += this.xOffsetValue.newLowValue();
      }

      float y = this.y;
      if (this.yOffsetValue.active) {
         y += this.yOffsetValue.newLowValue();
      }

      float width;
      float height;
      float radiusX;
      switch($SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape()[this.spawnShapeValue.shape.ordinal()]) {
      case 2:
         width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
         height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
         if (width != 0.0F) {
            radiusX = width * MathUtils.random();
            x += radiusX;
            y += radiusX * (height / width);
         } else {
            y += height * MathUtils.random();
         }
         break;
      case 3:
         width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
         height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
         x += MathUtils.random(width) - width / 2.0F;
         y += MathUtils.random(height) - height / 2.0F;
         break;
      case 4:
         width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
         height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
         radiusX = width / 2.0F;
         float radiusY = height / 2.0F;
         if (radiusX != 0.0F && radiusY != 0.0F) {
            float scaleY = radiusX / radiusY;
            float spawnAngle;
            float cosDeg;
            float sinDeg;
            if (this.spawnShapeValue.edges) {
               switch($SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide()[this.spawnShapeValue.side.ordinal()]) {
               case 2:
                  spawnAngle = -MathUtils.random(179.0F);
                  break;
               case 3:
                  spawnAngle = MathUtils.random(179.0F);
                  break;
               default:
                  spawnAngle = MathUtils.random(360.0F);
               }

               cosDeg = MathUtils.cosDeg(spawnAngle);
               sinDeg = MathUtils.sinDeg(spawnAngle);
               x += cosDeg * radiusX;
               y += sinDeg * radiusX / scaleY;
               if ((updateFlags & 2) == 0) {
                  particle.angle = spawnAngle;
                  particle.angleCos = cosDeg;
                  particle.angleSin = sinDeg;
               }
            } else {
               spawnAngle = radiusX * radiusX;

               do {
                  cosDeg = MathUtils.random(width) - radiusX;
                  sinDeg = MathUtils.random(width) - radiusX;
               } while(!(cosDeg * cosDeg + sinDeg * sinDeg <= spawnAngle));

               x += cosDeg;
               y += sinDeg / scaleY;
            }
         }
      }

      width = this.sprite.getHeight();
      particle.setBounds(x - spriteWidth / 2.0F, y - width / 2.0F, spriteWidth, width);
      int offsetTime = (int)((float)this.lifeOffset + (float)this.lifeOffsetDiff * this.lifeOffsetValue.getScale(percent));
      if (offsetTime > 0) {
         if (offsetTime >= particle.currentLife) {
            offsetTime = particle.currentLife - 1;
         }

         this.updateParticle(particle, (float)offsetTime / 1000.0F, offsetTime);
      }

   }

   private boolean updateParticle(ParticleEmitter.Particle particle, float delta, int deltaMillis) {
      int life = particle.currentLife - deltaMillis;
      if (life <= 0) {
         return false;
      } else {
         particle.currentLife = life;
         float percent = 1.0F - (float)particle.currentLife / (float)particle.life;
         int updateFlags = this.updateFlags;
         if ((updateFlags & 1) != 0) {
            particle.setScale(particle.scale + particle.scaleDiff * this.scaleValue.getScale(percent));
         }

         if ((updateFlags & 8) != 0) {
            float velocity = (particle.velocity + particle.velocityDiff * this.velocityValue.getScale(percent)) * delta;
            float velocityX;
            float velocityY;
            float rotation;
            if ((updateFlags & 2) != 0) {
               rotation = particle.angle + particle.angleDiff * this.angleValue.getScale(percent);
               velocityX = velocity * MathUtils.cosDeg(rotation);
               velocityY = velocity * MathUtils.sinDeg(rotation);
               if ((updateFlags & 4) != 0) {
                  float rotation = particle.rotation + particle.rotationDiff * this.rotationValue.getScale(percent);
                  if (this.aligned) {
                     rotation += rotation;
                  }

                  particle.setRotation(rotation);
               }
            } else {
               velocityX = velocity * particle.angleCos;
               velocityY = velocity * particle.angleSin;
               if (this.aligned || (updateFlags & 4) != 0) {
                  rotation = particle.rotation + particle.rotationDiff * this.rotationValue.getScale(percent);
                  if (this.aligned) {
                     rotation += particle.angle;
                  }

                  particle.setRotation(rotation);
               }
            }

            if ((updateFlags & 16) != 0) {
               velocityX += (particle.wind + particle.windDiff * this.windValue.getScale(percent)) * delta;
            }

            if ((updateFlags & 32) != 0) {
               velocityY += (particle.gravity + particle.gravityDiff * this.gravityValue.getScale(percent)) * delta;
            }

            particle.translate(velocityX, velocityY);
         } else if ((updateFlags & 4) != 0) {
            particle.setRotation(particle.rotation + particle.rotationDiff * this.rotationValue.getScale(percent));
         }

         float[] color;
         if ((updateFlags & 64) != 0) {
            color = this.tintValue.getColor(percent);
         } else {
            color = particle.tint;
         }

         particle.setColor(color[0], color[1], color[2], particle.transparency + particle.transparencyDiff * this.transparencyValue.getScale(percent));
         return true;
      }
   }

   public void setPosition(float x, float y) {
      if (this.attached) {
         float xAmount = x - this.x;
         float yAmount = y - this.y;
         boolean[] active = this.active;
         int i = 0;

         for(int n = active.length; i < n; ++i) {
            if (active[i]) {
               this.particles[i].translate(xAmount, yAmount);
            }
         }
      }

      this.x = x;
      this.y = y;
   }

   public void setSprite(Sprite sprite) {
      this.sprite = sprite;
      if (sprite != null) {
         float originX = sprite.getOriginX();
         float originY = sprite.getOriginY();
         Texture texture = sprite.getTexture();
         int i = 0;

         for(int n = this.particles.length; i < n; ++i) {
            ParticleEmitter.Particle particle = this.particles[i];
            if (particle == null) {
               break;
            }

            particle.setTexture(texture);
            particle.setOrigin(originX, originY);
         }

      }
   }

   public void allowCompletion() {
      this.allowCompletion = true;
      this.durationTimer = this.duration;
   }

   public Sprite getSprite() {
      return this.sprite;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public ParticleEmitter.ScaledNumericValue getLife() {
      return this.lifeValue;
   }

   public ParticleEmitter.ScaledNumericValue getScale() {
      return this.scaleValue;
   }

   public ParticleEmitter.ScaledNumericValue getRotation() {
      return this.rotationValue;
   }

   public ParticleEmitter.GradientColorValue getTint() {
      return this.tintValue;
   }

   public ParticleEmitter.ScaledNumericValue getVelocity() {
      return this.velocityValue;
   }

   public ParticleEmitter.ScaledNumericValue getWind() {
      return this.windValue;
   }

   public ParticleEmitter.ScaledNumericValue getGravity() {
      return this.gravityValue;
   }

   public ParticleEmitter.ScaledNumericValue getAngle() {
      return this.angleValue;
   }

   public ParticleEmitter.ScaledNumericValue getEmission() {
      return this.emissionValue;
   }

   public ParticleEmitter.ScaledNumericValue getTransparency() {
      return this.transparencyValue;
   }

   public ParticleEmitter.RangedNumericValue getDuration() {
      return this.durationValue;
   }

   public ParticleEmitter.RangedNumericValue getDelay() {
      return this.delayValue;
   }

   public ParticleEmitter.ScaledNumericValue getLifeOffset() {
      return this.lifeOffsetValue;
   }

   public ParticleEmitter.RangedNumericValue getXOffsetValue() {
      return this.xOffsetValue;
   }

   public ParticleEmitter.RangedNumericValue getYOffsetValue() {
      return this.yOffsetValue;
   }

   public ParticleEmitter.ScaledNumericValue getSpawnWidth() {
      return this.spawnWidthValue;
   }

   public ParticleEmitter.ScaledNumericValue getSpawnHeight() {
      return this.spawnHeightValue;
   }

   public ParticleEmitter.SpawnShapeValue getSpawnShape() {
      return this.spawnShapeValue;
   }

   public boolean isAttached() {
      return this.attached;
   }

   public void setAttached(boolean attached) {
      this.attached = attached;
   }

   public boolean isContinuous() {
      return this.continuous;
   }

   public void setContinuous(boolean continuous) {
      this.continuous = continuous;
   }

   public boolean isAligned() {
      return this.aligned;
   }

   public void setAligned(boolean aligned) {
      this.aligned = aligned;
   }

   public boolean isAdditive() {
      return this.additive;
   }

   public void setAdditive(boolean additive) {
      this.additive = additive;
   }

   public boolean isBehind() {
      return this.behind;
   }

   public void setBehind(boolean behind) {
      this.behind = behind;
   }

   public int getMinParticleCount() {
      return this.minParticleCount;
   }

   public void setMinParticleCount(int minParticleCount) {
      this.minParticleCount = minParticleCount;
   }

   public int getMaxParticleCount() {
      return this.maxParticleCount;
   }

   public boolean isComplete() {
      if (this.delayTimer < this.delay) {
         return false;
      } else {
         return this.durationTimer >= this.duration && this.activeCount == 0;
      }
   }

   public float getPercentComplete() {
      return this.delayTimer < this.delay ? 0.0F : Math.min(1.0F, this.durationTimer / this.duration);
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public int getActiveCount() {
      return this.activeCount;
   }

   public String getImagePath() {
      return this.imagePath;
   }

   public void setImagePath(String imagePath) {
      this.imagePath = imagePath;
   }

   public void setFlip(boolean flipX, boolean flipY) {
      this.flipX = flipX;
      this.flipY = flipY;
      if (this.particles != null) {
         int i = 0;

         for(int n = this.particles.length; i < n; ++i) {
            ParticleEmitter.Particle particle = this.particles[i];
            if (particle != null) {
               particle.flip(flipX, flipY);
            }
         }

      }
   }

   public void flipY() {
      this.angleValue.setHigh(-this.angleValue.getHighMin(), -this.angleValue.getHighMax());
      this.angleValue.setLow(-this.angleValue.getLowMin(), -this.angleValue.getLowMax());
      this.gravityValue.setHigh(-this.gravityValue.getHighMin(), -this.gravityValue.getHighMax());
      this.gravityValue.setLow(-this.gravityValue.getLowMin(), -this.gravityValue.getLowMax());
      this.windValue.setHigh(-this.windValue.getHighMin(), -this.windValue.getHighMax());
      this.windValue.setLow(-this.windValue.getLowMin(), -this.windValue.getLowMax());
      this.rotationValue.setHigh(-this.rotationValue.getHighMin(), -this.rotationValue.getHighMax());
      this.rotationValue.setLow(-this.rotationValue.getLowMin(), -this.rotationValue.getLowMax());
      this.yOffsetValue.setLow(-this.yOffsetValue.getLowMin(), -this.yOffsetValue.getLowMax());
   }

   public void save(Writer output) throws IOException {
      output.write(this.name + "\n");
      output.write("- Delay -\n");
      this.delayValue.save(output);
      output.write("- Duration - \n");
      this.durationValue.save(output);
      output.write("- Count - \n");
      output.write("min: " + this.minParticleCount + "\n");
      output.write("max: " + this.maxParticleCount + "\n");
      output.write("- Emission - \n");
      this.emissionValue.save(output);
      output.write("- Life - \n");
      this.lifeValue.save(output);
      output.write("- Life Offset - \n");
      this.lifeOffsetValue.save(output);
      output.write("- X Offset - \n");
      this.xOffsetValue.save(output);
      output.write("- Y Offset - \n");
      this.yOffsetValue.save(output);
      output.write("- Spawn Shape - \n");
      this.spawnShapeValue.save(output);
      output.write("- Spawn Width - \n");
      this.spawnWidthValue.save(output);
      output.write("- Spawn Height - \n");
      this.spawnHeightValue.save(output);
      output.write("- Scale - \n");
      this.scaleValue.save(output);
      output.write("- Velocity - \n");
      this.velocityValue.save(output);
      output.write("- Angle - \n");
      this.angleValue.save(output);
      output.write("- Rotation - \n");
      this.rotationValue.save(output);
      output.write("- Wind - \n");
      this.windValue.save(output);
      output.write("- Gravity - \n");
      this.gravityValue.save(output);
      output.write("- Tint - \n");
      this.tintValue.save(output);
      output.write("- Transparency - \n");
      this.transparencyValue.save(output);
      output.write("- Options - \n");
      output.write("attached: " + this.attached + "\n");
      output.write("continuous: " + this.continuous + "\n");
      output.write("aligned: " + this.aligned + "\n");
      output.write("additive: " + this.additive + "\n");
      output.write("behind: " + this.behind + "\n");
   }

   public void load(BufferedReader reader) throws IOException {
      try {
         this.name = readString(reader, "name");
         reader.readLine();
         this.delayValue.load(reader);
         reader.readLine();
         this.durationValue.load(reader);
         reader.readLine();
         this.setMinParticleCount(readInt(reader, "minParticleCount"));
         this.setMaxParticleCount(readInt(reader, "maxParticleCount"));
         reader.readLine();
         this.emissionValue.load(reader);
         reader.readLine();
         this.lifeValue.load(reader);
         reader.readLine();
         this.lifeOffsetValue.load(reader);
         reader.readLine();
         this.xOffsetValue.load(reader);
         reader.readLine();
         this.yOffsetValue.load(reader);
         reader.readLine();
         this.spawnShapeValue.load(reader);
         reader.readLine();
         this.spawnWidthValue.load(reader);
         reader.readLine();
         this.spawnHeightValue.load(reader);
         reader.readLine();
         this.scaleValue.load(reader);
         reader.readLine();
         this.velocityValue.load(reader);
         reader.readLine();
         this.angleValue.load(reader);
         reader.readLine();
         this.rotationValue.load(reader);
         reader.readLine();
         this.windValue.load(reader);
         reader.readLine();
         this.gravityValue.load(reader);
         reader.readLine();
         this.tintValue.load(reader);
         reader.readLine();
         this.transparencyValue.load(reader);
         reader.readLine();
         this.attached = readBoolean(reader, "attached");
         this.continuous = readBoolean(reader, "continuous");
         this.aligned = readBoolean(reader, "aligned");
         this.additive = readBoolean(reader, "additive");
         this.behind = readBoolean(reader, "behind");
      } catch (RuntimeException var3) {
         if (this.name == null) {
            throw var3;
         } else {
            throw new RuntimeException("Error parsing emitter: " + this.name, var3);
         }
      }
   }

   static String readString(BufferedReader reader, String name) throws IOException {
      String line = reader.readLine();
      if (line == null) {
         throw new IOException("Missing value: " + name);
      } else {
         return line.substring(line.indexOf(":") + 1).trim();
      }
   }

   static boolean readBoolean(BufferedReader reader, String name) throws IOException {
      return Boolean.parseBoolean(readString(reader, name));
   }

   static int readInt(BufferedReader reader, String name) throws IOException {
      return Integer.parseInt(readString(reader, name));
   }

   static float readFloat(BufferedReader reader, String name) throws IOException {
      return Float.parseFloat(readString(reader, name));
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide() {
      int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[ParticleEmitter.SpawnEllipseSide.values().length];

         try {
            var0[ParticleEmitter.SpawnEllipseSide.both.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[ParticleEmitter.SpawnEllipseSide.bottom.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[ParticleEmitter.SpawnEllipseSide.top.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape() {
      int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[ParticleEmitter.SpawnShape.values().length];

         try {
            var0[ParticleEmitter.SpawnShape.ellipse.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[ParticleEmitter.SpawnShape.line.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[ParticleEmitter.SpawnShape.point.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[ParticleEmitter.SpawnShape.square.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape = var0;
         return var0;
      }
   }

   public static class GradientColorValue extends ParticleEmitter.ParticleValue {
      private static float[] temp = new float[4];
      private float[] colors = new float[]{1.0F, 1.0F, 1.0F};
      float[] timeline = new float[]{0.0F};

      public GradientColorValue() {
         this.alwaysActive = true;
      }

      public float[] getTimeline() {
         return this.timeline;
      }

      public void setTimeline(float[] timeline) {
         this.timeline = timeline;
      }

      public float[] getColors() {
         return this.colors;
      }

      public void setColors(float[] colors) {
         this.colors = colors;
      }

      public float[] getColor(float percent) {
         int startIndex = 0;
         int endIndex = -1;
         float[] timeline = this.timeline;
         int n = timeline.length;

         float r1;
         for(int i = 1; i < n; startIndex = i++) {
            r1 = timeline[i];
            if (r1 > percent) {
               endIndex = i;
               break;
            }
         }

         float startTime = timeline[startIndex];
         startIndex *= 3;
         r1 = this.colors[startIndex];
         float g1 = this.colors[startIndex + 1];
         float b1 = this.colors[startIndex + 2];
         if (endIndex == -1) {
            temp[0] = r1;
            temp[1] = g1;
            temp[2] = b1;
            return temp;
         } else {
            float factor = (percent - startTime) / (timeline[endIndex] - startTime);
            endIndex *= 3;
            temp[0] = r1 + (this.colors[endIndex] - r1) * factor;
            temp[1] = g1 + (this.colors[endIndex + 1] - g1) * factor;
            temp[2] = b1 + (this.colors[endIndex + 2] - b1) * factor;
            return temp;
         }
      }

      public void save(Writer output) throws IOException {
         super.save(output);
         if (this.active) {
            output.write("colorsCount: " + this.colors.length + "\n");

            int i;
            for(i = 0; i < this.colors.length; ++i) {
               output.write("colors" + i + ": " + this.colors[i] + "\n");
            }

            output.write("timelineCount: " + this.timeline.length + "\n");

            for(i = 0; i < this.timeline.length; ++i) {
               output.write("timeline" + i + ": " + this.timeline[i] + "\n");
            }

         }
      }

      public void load(BufferedReader reader) throws IOException {
         super.load(reader);
         if (this.active) {
            this.colors = new float[ParticleEmitter.readInt(reader, "colorsCount")];

            int i;
            for(i = 0; i < this.colors.length; ++i) {
               this.colors[i] = ParticleEmitter.readFloat(reader, "colors" + i);
            }

            this.timeline = new float[ParticleEmitter.readInt(reader, "timelineCount")];

            for(i = 0; i < this.timeline.length; ++i) {
               this.timeline[i] = ParticleEmitter.readFloat(reader, "timeline" + i);
            }

         }
      }

      public void load(ParticleEmitter.GradientColorValue value) {
         super.load((ParticleEmitter.ParticleValue)value);
         this.colors = new float[value.colors.length];
         System.arraycopy(value.colors, 0, this.colors, 0, this.colors.length);
         this.timeline = new float[value.timeline.length];
         System.arraycopy(value.timeline, 0, this.timeline, 0, this.timeline.length);
      }
   }

   public static class NumericValue extends ParticleEmitter.ParticleValue {
      private float value;

      public float getValue() {
         return this.value;
      }

      public void setValue(float value) {
         this.value = value;
      }

      public void save(Writer output) throws IOException {
         super.save(output);
         if (this.active) {
            output.write("value: " + this.value + "\n");
         }
      }

      public void load(BufferedReader reader) throws IOException {
         super.load(reader);
         if (this.active) {
            this.value = ParticleEmitter.readFloat(reader, "value");
         }
      }

      public void load(ParticleEmitter.NumericValue value) {
         super.load((ParticleEmitter.ParticleValue)value);
         this.value = value.value;
      }
   }

   public static class Particle extends Sprite {
      protected int life;
      protected int currentLife;
      protected float scale;
      protected float scaleDiff;
      protected float rotation;
      protected float rotationDiff;
      protected float velocity;
      protected float velocityDiff;
      protected float angle;
      protected float angleDiff;
      protected float angleCos;
      protected float angleSin;
      protected float transparency;
      protected float transparencyDiff;
      protected float wind;
      protected float windDiff;
      protected float gravity;
      protected float gravityDiff;
      protected float[] tint;

      public Particle(Sprite sprite) {
         super(sprite);
      }
   }

   public static class ParticleValue {
      boolean active;
      boolean alwaysActive;

      public void setAlwaysActive(boolean alwaysActive) {
         this.alwaysActive = alwaysActive;
      }

      public boolean isAlwaysActive() {
         return this.alwaysActive;
      }

      public boolean isActive() {
         return this.alwaysActive || this.active;
      }

      public void setActive(boolean active) {
         this.active = active;
      }

      public void save(Writer output) throws IOException {
         if (!this.alwaysActive) {
            output.write("active: " + this.active + "\n");
         } else {
            this.active = true;
         }

      }

      public void load(BufferedReader reader) throws IOException {
         if (!this.alwaysActive) {
            this.active = ParticleEmitter.readBoolean(reader, "active");
         } else {
            this.active = true;
         }

      }

      public void load(ParticleEmitter.ParticleValue value) {
         this.active = value.active;
         this.alwaysActive = value.alwaysActive;
      }
   }

   public static class RangedNumericValue extends ParticleEmitter.ParticleValue {
      private float lowMin;
      private float lowMax;

      public float newLowValue() {
         return this.lowMin + (this.lowMax - this.lowMin) * MathUtils.random();
      }

      public void setLow(float value) {
         this.lowMin = value;
         this.lowMax = value;
      }

      public void setLow(float min, float max) {
         this.lowMin = min;
         this.lowMax = max;
      }

      public float getLowMin() {
         return this.lowMin;
      }

      public void setLowMin(float lowMin) {
         this.lowMin = lowMin;
      }

      public float getLowMax() {
         return this.lowMax;
      }

      public void setLowMax(float lowMax) {
         this.lowMax = lowMax;
      }

      public void save(Writer output) throws IOException {
         super.save(output);
         if (this.active) {
            output.write("lowMin: " + this.lowMin + "\n");
            output.write("lowMax: " + this.lowMax + "\n");
         }
      }

      public void load(BufferedReader reader) throws IOException {
         super.load(reader);
         if (this.active) {
            this.lowMin = ParticleEmitter.readFloat(reader, "lowMin");
            this.lowMax = ParticleEmitter.readFloat(reader, "lowMax");
         }
      }

      public void load(ParticleEmitter.RangedNumericValue value) {
         super.load((ParticleEmitter.ParticleValue)value);
         this.lowMax = value.lowMax;
         this.lowMin = value.lowMin;
      }
   }

   public static class ScaledNumericValue extends ParticleEmitter.RangedNumericValue {
      private float[] scaling = new float[]{1.0F};
      float[] timeline = new float[]{0.0F};
      private float highMin;
      private float highMax;
      private boolean relative;

      public float newHighValue() {
         return this.highMin + (this.highMax - this.highMin) * MathUtils.random();
      }

      public void setHigh(float value) {
         this.highMin = value;
         this.highMax = value;
      }

      public void setHigh(float min, float max) {
         this.highMin = min;
         this.highMax = max;
      }

      public float getHighMin() {
         return this.highMin;
      }

      public void setHighMin(float highMin) {
         this.highMin = highMin;
      }

      public float getHighMax() {
         return this.highMax;
      }

      public void setHighMax(float highMax) {
         this.highMax = highMax;
      }

      public float[] getScaling() {
         return this.scaling;
      }

      public void setScaling(float[] values) {
         this.scaling = values;
      }

      public float[] getTimeline() {
         return this.timeline;
      }

      public void setTimeline(float[] timeline) {
         this.timeline = timeline;
      }

      public boolean isRelative() {
         return this.relative;
      }

      public void setRelative(boolean relative) {
         this.relative = relative;
      }

      public float getScale(float percent) {
         int endIndex = -1;
         float[] timeline = this.timeline;
         int n = timeline.length;

         for(int i = 1; i < n; ++i) {
            float t = timeline[i];
            if (t > percent) {
               endIndex = i;
               break;
            }
         }

         if (endIndex == -1) {
            return this.scaling[n - 1];
         } else {
            float[] scaling = this.scaling;
            int startIndex = endIndex - 1;
            float startValue = scaling[startIndex];
            float startTime = timeline[startIndex];
            return startValue + (scaling[endIndex] - startValue) * ((percent - startTime) / (timeline[endIndex] - startTime));
         }
      }

      public void save(Writer output) throws IOException {
         super.save(output);
         if (this.active) {
            output.write("highMin: " + this.highMin + "\n");
            output.write("highMax: " + this.highMax + "\n");
            output.write("relative: " + this.relative + "\n");
            output.write("scalingCount: " + this.scaling.length + "\n");

            int i;
            for(i = 0; i < this.scaling.length; ++i) {
               output.write("scaling" + i + ": " + this.scaling[i] + "\n");
            }

            output.write("timelineCount: " + this.timeline.length + "\n");

            for(i = 0; i < this.timeline.length; ++i) {
               output.write("timeline" + i + ": " + this.timeline[i] + "\n");
            }

         }
      }

      public void load(BufferedReader reader) throws IOException {
         super.load(reader);
         if (this.active) {
            this.highMin = ParticleEmitter.readFloat(reader, "highMin");
            this.highMax = ParticleEmitter.readFloat(reader, "highMax");
            this.relative = ParticleEmitter.readBoolean(reader, "relative");
            this.scaling = new float[ParticleEmitter.readInt(reader, "scalingCount")];

            int i;
            for(i = 0; i < this.scaling.length; ++i) {
               this.scaling[i] = ParticleEmitter.readFloat(reader, "scaling" + i);
            }

            this.timeline = new float[ParticleEmitter.readInt(reader, "timelineCount")];

            for(i = 0; i < this.timeline.length; ++i) {
               this.timeline[i] = ParticleEmitter.readFloat(reader, "timeline" + i);
            }

         }
      }

      public void load(ParticleEmitter.ScaledNumericValue value) {
         super.load((ParticleEmitter.RangedNumericValue)value);
         this.highMax = value.highMax;
         this.highMin = value.highMin;
         this.scaling = new float[value.scaling.length];
         System.arraycopy(value.scaling, 0, this.scaling, 0, this.scaling.length);
         this.timeline = new float[value.timeline.length];
         System.arraycopy(value.timeline, 0, this.timeline, 0, this.timeline.length);
         this.relative = value.relative;
      }
   }

   public static enum SpawnEllipseSide {
      both,
      top,
      bottom;
   }

   public static enum SpawnShape {
      point,
      line,
      square,
      ellipse;
   }

   public static class SpawnShapeValue extends ParticleEmitter.ParticleValue {
      ParticleEmitter.SpawnShape shape;
      boolean edges;
      ParticleEmitter.SpawnEllipseSide side;

      public SpawnShapeValue() {
         this.shape = ParticleEmitter.SpawnShape.point;
         this.side = ParticleEmitter.SpawnEllipseSide.both;
      }

      public ParticleEmitter.SpawnShape getShape() {
         return this.shape;
      }

      public void setShape(ParticleEmitter.SpawnShape shape) {
         this.shape = shape;
      }

      public boolean isEdges() {
         return this.edges;
      }

      public void setEdges(boolean edges) {
         this.edges = edges;
      }

      public ParticleEmitter.SpawnEllipseSide getSide() {
         return this.side;
      }

      public void setSide(ParticleEmitter.SpawnEllipseSide side) {
         this.side = side;
      }

      public void save(Writer output) throws IOException {
         super.save(output);
         if (this.active) {
            output.write("shape: " + this.shape + "\n");
            if (this.shape == ParticleEmitter.SpawnShape.ellipse) {
               output.write("edges: " + this.edges + "\n");
               output.write("side: " + this.side + "\n");
            }

         }
      }

      public void load(BufferedReader reader) throws IOException {
         super.load(reader);
         if (this.active) {
            this.shape = ParticleEmitter.SpawnShape.valueOf(ParticleEmitter.readString(reader, "shape"));
            if (this.shape == ParticleEmitter.SpawnShape.ellipse) {
               this.edges = ParticleEmitter.readBoolean(reader, "edges");
               this.side = ParticleEmitter.SpawnEllipseSide.valueOf(ParticleEmitter.readString(reader, "side"));
            }

         }
      }

      public void load(ParticleEmitter.SpawnShapeValue value) {
         super.load((ParticleEmitter.ParticleValue)value);
         this.shape = value.shape;
         this.edges = value.edges;
         this.side = value.side;
      }
   }
}
