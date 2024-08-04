package com.minild44.www.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.minild44.www.EndScreen;
import com.minild44.www.Main;
import com.minild44.www.Resources;
import com.minild44.www.core.entities.BasicSpawner;
import com.minild44.www.core.entities.Campfire;
import com.minild44.www.core.entities.Entity;
import com.minild44.www.core.entities.Potato;
import com.minild44.www.core.entities.Spawner;
import com.minild44.www.core.entities.Squad;
import com.minild44.www.core.entities.Torch;
import com.minild44.www.core.entities.Unit;
import com.minild44.www.util.Camera;
import com.minild44.www.util.Light;
import com.minild44.www.util.LightHandler;
import com.minild44.www.util.ShadowCasterPass;
import com.minild44.www.util.TwoPassShader;
import java.util.HashMap;
import java.util.Iterator;

public class Level extends TileMap {
   public ShapeRenderer sr = new ShapeRenderer();
   private Camera camera;
   private TwoPassShader lighting;
   private LightHandler lights;
   private Array<Entity> entities = new Array();
   private Array<Squad> squads = new Array();
   private Array<Spawner> spawners = new Array();
   public Campfire campfire;
   public Rectangle squadCreateArea;
   private Base blueBase;
   private Base redBase;
   private float time;
   private float beforeEnd = 1.0F;
   private boolean finished;
   private int stage = 1;
   private float brightness = 1.0F;
   public int killCount;
   private boolean won;
   private boolean timerRunning = true;
   public int gold;
   private long fireID;
   private HashMap<String, ParticleEffectPool> pools = new HashMap();
   private Array<ParticleEffectPool.PooledEffect> effects = new Array();

   public Level(String tmxPath) {
      super(tmxPath);
      this.fireID = Resources.SOUNDS[10].play(0.0F);
      Resources.SOUNDS[10].setLooping(this.fireID, true);
      Resources.SOUNDS[10].setVolume(this.fireID, 0.5F);
      this.lights = new LightHandler(512);
      this.lights.setCasterPass(new ShadowCasterPass() {
         public void render(SpriteBatch batch, Light light) {
            Level.this.renderShadowCasters(batch, light, true);
         }
      });
      this.lighting = new TwoPassShader(Resources.internal("shaders/pass.vs"), Resources.internal("shaders/blend.fs"));
      String[] names = new String[]{"fire", "torchfire", "arrowhit", "magichit", "heal", "squaddefeat", "wind", "boom"};
      String[] var6 = names;
      int var5 = names.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         String name = var6[var4];
         ParticleEffect fireEffect = new ParticleEffect();
         fireEffect.load(Resources.internal("effects/" + name + ".p"), Resources.internal("effects"));
         this.pools.put(name, new ParticleEffectPool(fireEffect, 1, 64));
      }

      this.loadObjects();

      for(int i = 0; i < 2; ++i) {
         Squad first = new Squad(this.camera.position.x, this.camera.position.y, this);
         first.addRandomMembers(4);
         float tx = this.squadCreateArea.x + MathUtils.random() * this.squadCreateArea.width;
         float ty = this.squadCreateArea.y + MathUtils.random() * this.squadCreateArea.height;
         first.setTarget((int)(tx / 32.0F), (int)(ty / 32.0F));
      }

   }

   private void loadObjects() {
      MapLayer objectLayer = this.map.getLayers().get("objects");
      Iterator var3 = objectLayer.getObjects().iterator();

      int y;
      int w;
      int h;
      while(var3.hasNext()) {
         MapObject o = (MapObject)var3.next();
         String name = o.getName();
         int x = Integer.parseInt(o.getProperties().get("x").toString()) << 2;
         y = Integer.parseInt(o.getProperties().get("y").toString()) << 2;
         w = Integer.parseInt(o.getProperties().get("width").toString()) << 2;
         h = Integer.parseInt(o.getProperties().get("height").toString()) << 2;
         if (name.equals("torch")) {
            this.add((Entity)(new Torch((float)x, (float)y)));
         } else if (name.equals("campfire")) {
            this.campfire = new Campfire((float)x, (float)y);
            this.add((Entity)this.campfire);
         } else if (name.equals("blue_base")) {
            this.blueBase = new Base((float)(x + w / 2 - 16), (float)(y + h / 2 - 16), false);
            this.add((Entity)this.blueBase);
         } else if (name.equals("red_base")) {
            this.redBase = new Base((float)(x + w / 2 - 16), (float)(y + h / 2 - 16), true);
            this.add((Entity)this.redBase);
         } else if (name.equals("potato")) {
            this.add((Entity)(new Potato((float)(x + w / 2), (float)(y + h / 2))));
         }
      }

      MapLayer spawnLayer = this.map.getLayers().get("spawns");
      Iterator var16 = spawnLayer.getObjects().iterator();

      while(var16.hasNext()) {
         MapObject o = (MapObject)var16.next();
         String name = o.getName();
         y = Integer.parseInt(o.getProperties().get("x").toString()) << 2;
         w = Integer.parseInt(o.getProperties().get("y").toString()) << 2;
         h = Integer.parseInt(o.getProperties().get("width").toString()) << 2;
         int h = Integer.parseInt(o.getProperties().get("height").toString()) << 2;
         if (name.equals("basic")) {
            int delay = Integer.parseInt(o.getProperties().get("delay").toString());
            Entity target = o.getProperties().get("target").toString().equals("blue_base") ? this.blueBase : this.redBase;
            int max = Integer.parseInt(o.getProperties().get("max").toString());
            this.spawners.add(new BasicSpawner(y, w, h, h, delay, max, target));
         } else if (name.equals("create")) {
            this.squadCreateArea = new Rectangle((float)y, (float)w, (float)h, (float)h);
         }
      }

      MapLayer actions = this.map.getLayers().get("actions");
      Iterator var19 = actions.getObjects().iterator();

      while(var19.hasNext()) {
         MapObject o = (MapObject)var19.next();
         String name = o.getName();
         w = Integer.parseInt(o.getProperties().get("x").toString()) << 2;
         h = Integer.parseInt(o.getProperties().get("y").toString()) << 2;
         if (name.equals("camera")) {
            this.camera = new Camera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            this.camera.position.set((float)w, (float)h, 0.0F);
            this.camera.tmpPos.set((float)w, (float)h);
            this.camera.setTarget((float)w, (float)h);
            this.camera.update();
         }
      }

   }

   public void add(Entity e) {
      e.enter(this);
      this.entities.add(e);
      this.entities.sort();
   }

   public void add(Light l) {
      this.lights.add(l);
   }

   public void remove(Squad s) {
      this.squads.removeValue(s, false);
   }

   public void add(String effectName, float x, float y) {
      ParticleEffectPool.PooledEffect effect = ((ParticleEffectPool)this.pools.get(effectName)).obtain();
      effect.setPosition(x, y);
      this.effects.add(effect);
   }

   public void remove(Entity e) {
      this.entities.removeValue(e, false);
   }

   private void renderShadowCasters(SpriteBatch batch, Light light, boolean renderEntities) {
      if (light.castShadows) {
         Rectangle bounds = new Rectangle(this.mapRenderer.getViewBounds());
         float ls = this.lights.getLightSize() / 2.0F;
         this.mapRenderer.getViewBounds().set(bounds.x - ls, bounds.y - ls, bounds.width + ls * 2.0F, bounds.height + ls * 2.0F);
         this.renderLayers("ao", batch.getProjectionMatrix());
         this.mapRenderer.getViewBounds().set(bounds);
         if (renderEntities) {
            batch.begin();
            Iterator var7 = this.entities.iterator();

            while(var7.hasNext()) {
               Entity e = (Entity)var7.next();
               if (e.getID() != light.mask) {
                  e.render(batch);
               }
            }

            batch.end();
         }
      }

   }

   public void updateAndRender(float delta, Matrix4 screenView, SpriteBatch batch, boolean paused) {
      this.sr.setProjectionMatrix(screenView);
      Squad s;
      if (!paused) {
         if (this.timerRunning) {
            this.time += delta;
         } else {
            this.beforeEnd -= delta;
         }

         Iterator var7;
         if (this.beforeEnd <= 0.0F) {
            this.beforeEnd = 3.0F;
            if (this.finished) {
               Main.INSTANCE.setScreen(new EndScreen(this.won, this.killCount, this.time));
               Resources.SOUNDS[10].stop();
            } else {
               this.finished = true;
               Vector2 p = this.won ? this.redBase.getBounds().getCenter() : this.blueBase.getBounds().getCenter();
               var7 = this.squads.iterator();

               while(var7.hasNext()) {
                  s = (Squad)var7.next();
                  s.getTargets().clear();
                  s.stopBaseAttack();
               }

               Resources.play(7, p.x, p.y, this);
               this.add("boom", p.x, p.y);
               this.blueBase.remove();
               this.redBase.remove();
            }
         }

         float lowest = 0.35F;
         this.brightness = (MathUtils.sin(this.time * 2.0F) + 1.0F) * ((1.0F - lowest) / 2.0F) + lowest;
         this.brightness = lowest;
         this.stage = (int)(this.time / 70.0F) + 1;
         var7 = this.spawners.iterator();

         while(var7.hasNext()) {
            Spawner spawner = (Spawner)var7.next();
            spawner.update(delta, this);
         }

         this.lights.update(delta, batch, this.camera, this.brightness);
         float dst = (new Vector2(this.camera.position.x, this.camera.position.y)).dst(this.campfire.getBounds().x, this.campfire.getBounds().y);
         float d = (float)Math.sqrt((double)(Gdx.graphics.getWidth() * Gdx.graphics.getWidth() + Gdx.graphics.getHeight() * Gdx.graphics.getHeight())) / 2.0F;
         dst = (d - dst) / d;
         if (dst < 0.0F) {
            dst = 0.0F;
         }

         float dx = (this.campfire.getBounds().x - this.camera.position.x) / (float)Gdx.graphics.getWidth() / 2.0F;
         dst = MathUtils.clamp(dst, 0.0F, Main.VOLUME);
         Resources.SOUNDS[10].setPan(this.fireID, dx, dst / 4.0F);
      }

      this.lighting.beginFirst();
      this.setView(this.camera);
      this.renderLayers("render", this.camera.combined);
      this.lighting.endFirst();
      this.lighting.setSecond(this.lights.getLightTexture());
      batch.setProjectionMatrix(screenView);
      this.lighting.renderToScreen(batch, 1.0F - this.brightness);
      batch.setProjectionMatrix(this.camera.combined);
      int i;
      if (!paused) {
         for(i = this.squads.size - 1; i >= 0; --i) {
            s = (Squad)this.squads.get(i);
            s.update(delta);
         }
      }

      batch.begin();

      for(i = this.entities.size - 1; i >= 0; --i) {
         Entity e = (Entity)this.entities.get(i);
         if (e.wasRemoved()) {
            this.entities.removeIndex(i);
         } else {
            if (!paused) {
               e.update(delta);
            }

            if (this.camera.frustum.rectInFrustrum(e.getBounds())) {
               e.render(batch);
            }
         }
      }

      batch.end();
      batch.setProjectionMatrix(screenView);

      for(i = this.squads.size - 1; i >= 0; --i) {
         s = (Squad)this.squads.get(i);
         s.renderView(batch, this.camera);
      }

      this.blueBase.renderView(screenView, this.camera, batch);
      this.redBase.renderView(screenView, this.camera, batch);
      batch.setProjectionMatrix(this.camera.combined);
      batch.begin();

      for(i = this.effects.size - 1; i >= 0; --i) {
         ParticleEffectPool.PooledEffect effect = (ParticleEffectPool.PooledEffect)this.effects.get(i);
         effect.draw(batch, delta);
         if (effect.isComplete()) {
            effect.free();
            this.effects.removeIndex(i);
         }
      }

      batch.end();
   }

   public void resize(int width, int height) {
      this.lights.resize(width, height);
      Vector3 prev = this.camera.position.cpy();
      this.camera.setToOrtho(false, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
      this.camera.position.set(prev);
   }

   public LightHandler getLights() {
      return this.lights;
   }

   public float getBrightness() {
      return this.brightness;
   }

   public Array<Entity> getEntities() {
      return this.entities;
   }

   public Array<Squad> getSquads() {
      return this.squads;
   }

   public void dispose() {
      super.dispose();
      Iterator var2 = this.entities.iterator();

      while(var2.hasNext()) {
         Entity e = (Entity)var2.next();
         e.dispose();
      }

      var2 = this.effects.iterator();

      while(var2.hasNext()) {
         ParticleEffectPool.PooledEffect e = (ParticleEffectPool.PooledEffect)var2.next();
         e.free();
      }

      this.effects.clear();
      this.squads.clear();
   }

   public boolean isSpotTaken(int x, int y) {
      if (super.isBlocked(x, y)) {
         return true;
      } else {
         Iterator var4 = this.squads.iterator();

         while(var4.hasNext()) {
            Squad s = (Squad)var4.next();
            Iterator var6 = s.getMembers().iterator();

            while(var6.hasNext()) {
               Unit u = (Unit)var6.next();
               if (u.getGoal() != null && x == (int)u.getGoal().x && y == (int)u.getGoal().y) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public int getStage() {
      return this.stage;
   }

   public Camera getCamera() {
      return this.camera;
   }

   public Base getBlueBase() {
      return this.blueBase;
   }

   public Base getRedBase() {
      return this.redBase;
   }

   public void end(boolean won) {
      this.won = won;
      this.timerRunning = false;
      Vector2 p = won ? this.redBase.getBounds().getCenter() : this.blueBase.getBounds().getCenter();
      this.camera.setTarget(p.x, p.y);
   }

   public int getSquadCount(boolean enemy) {
      int count = 0;
      Iterator var4 = this.squads.iterator();

      while(var4.hasNext()) {
         Squad s = (Squad)var4.next();
         if (s.isEnemy() == enemy) {
            ++count;
         }
      }

      return count;
   }
}
