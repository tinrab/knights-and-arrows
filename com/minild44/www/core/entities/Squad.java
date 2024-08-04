package com.minild44.www.core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.minild44.www.Input;
import com.minild44.www.Resources;
import com.minild44.www.core.Base;
import com.minild44.www.core.Level;
import com.minild44.www.core.entities.units.Archer;
import com.minild44.www.core.entities.units.Bowman;
import com.minild44.www.core.entities.units.Knight;
import com.minild44.www.core.entities.units.Lich;
import com.minild44.www.core.entities.units.Magician;
import com.minild44.www.core.entities.units.Monk;
import com.minild44.www.core.entities.units.Necro;
import com.minild44.www.core.entities.units.Warrior;
import com.minild44.www.pathfinding.Path;
import com.minild44.www.util.Animation;
import com.minild44.www.util.Camera;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;

public class Squad {
   public static final byte MAX_SIZE = 9;
   public static final byte MAX_COUNT = 10;
   private static int next;
   private int id;
   private String name;
   private Animation flag;
   private boolean enemy;
   private Squad.EnemyType type;
   public float lastAttack;
   public byte spawnerID;
   private boolean selected;
   private Level level;
   private Vector2 position;
   private Path path;
   private boolean inCombat;
   private Array<Unit> members;
   private Array<Squad> targets;
   private Base baseTarget;

   public Squad(float x, float y, Level level, String name) {
      this(x, y, level, false, name);
   }

   public Squad(float x, float y, Level level, boolean enemy) {
      this(x, y, level, enemy, (String)Resources.NAMES.get((int)(Math.random() * (double)Resources.NAMES.size())));
   }

   public Squad(float x, float y, Level level) {
      this(x, y, level, false, (String)Resources.NAMES.get((int)(Math.random() * (double)Resources.NAMES.size())));
   }

   public Squad(float x, float y, Level level, Squad.EnemyType type) {
      this(x, y, level, false, (String)Resources.NAMES.get((int)(Math.random() * (double)Resources.NAMES.size())));
      this.type = type;
   }

   public Squad(float x, float y, Level level, boolean enemy, String name) {
      this.members = new Array();
      this.targets = new Array();
      this.id = ++next;
      this.enemy = enemy;
      this.name = name;
      this.level = level;
      level.getSquads().add(this);
      this.position = new Vector2(x, y);
      this.flag = new Animation(2, 0.3F);
   }

   public int getID() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Squad.EnemyType getEnemyType() {
      return this.type;
   }

   public void setEnemyType(Squad.EnemyType type) {
      this.type = type;
   }

   public void add(Unit u) {
      if (this.members.size < 9) {
         this.level.add((Entity)u);
         u.enter(this.level);
         u.join(this);
         u.setLeader(this.members.size == 0);
         u.setPosition(this.position.x, this.position.y);
         this.members.add(u);
      }

   }

   public void addToFreeSpot(Unit u) {
      if (this.members.size < 9) {
         this.level.add((Entity)u);
         u.enter(this.level);
         u.join(this);
         this.members.add(u);
         Vector2 c = new Vector2(this.position.x / 32.0F, this.position.y / 32.0F);

         for(int y = -1; y <= 1; ++y) {
            for(int x = -1; x <= 1; ++x) {
               int tx = (int)c.x + x;
               int ty = (int)c.y + y;
               if ((x != 0 || y != 0) && !this.level.isSpotTaken(tx, ty)) {
                  u.setPosition((float)(tx << 5), (float)(ty << 5));
                  u.setGoal((float)tx, (float)ty);
               }
            }
         }
      }

   }

   public void update(float delta) {
      this.flag.update(delta);
      this.lastAttack -= delta;
      if (this.type == Squad.EnemyType.DEFEND) {
         if (!this.inCombat) {
            Iterator var3 = this.level.getSquads().iterator();

            while(var3.hasNext()) {
               Squad s = (Squad)var3.next();
               if (!s.isEnemy() && this.level.canSee(this.getLeader().getBounds().getCenter(), s.getLeader().getBounds().getCenter(), 512.0F)) {
                  this.startAttack(s);
                  break;
               }
            }
         } else if (this.lastAttack <= 0.0F) {
            this.flee();
         }
      }

      for(int i = this.members.size - 1; i >= 0; --i) {
         Unit u = (Unit)this.members.get(i);
         Vector2 c;
         if (u.wasRemoved()) {
            this.level.getEntities().removeValue(u, false);
            c = ((Unit)this.members.get(0)).getBounds().getCenter();
            this.members.removeIndex(i);
            if (this.members.size == 0) {
               Resources.play(6, this.position.x, this.position.y, this.level);
               this.level.add("squaddefeat", c.x, c.y);
               this.level.remove(this);
               Iterator var12 = this.level.getSquads().iterator();

               while(var12.hasNext()) {
                  Squad s = (Squad)var12.next();
                  s.stopAttack(this);
               }

               if (!this.enemy && this.level.getSquadCount(false) == 0) {
                  this.level.end(false);
               }

               return;
            }

            if (u.isLeader()) {
               ((Unit)this.members.get(0)).setLeader(true);
            }
         } else if (u.adjust) {
            u.adjust = false;
            c = new Vector2(this.position.x / 32.0F, this.position.y / 32.0F);

            for(int y = -1; y <= 1; ++y) {
               for(int x = -1; x <= 1; ++x) {
                  int tx = (int)c.x + x;
                  int ty = (int)c.y + y;
                  if ((x != 0 || y != 0) && !this.level.isSpotTaken(tx, ty)) {
                     u.setGoal((float)tx, (float)ty);
                  }
               }
            }
         }
      }

   }

   public void renderView(SpriteBatch batch, Camera camera) {
      Vector2 sp = camera.project(new Vector2(this.position.x + 16.0F, this.position.y + 16.0F));
      Vector2 leaderPos = camera.project(((Unit)this.members.get(0)).getBounds().getCenter());
      GL11.glLineWidth(2.0F / camera.zoom);
      Gdx.gl.glEnable(3042);
      Gdx.gl.glBlendFunc(770, 771);
      Squad target;
      Iterator var6;
      Vector2 pos;
      Vector2 dir;
      float f;
      float d;
      float d2;
      if (this.inCombat && !this.enemy) {
         var6 = this.targets.iterator();

         while(var6.hasNext()) {
            target = (Squad)var6.next();
            pos = camera.project(target.getLeader().getBounds().getCenter());
            dir = pos.cpy().sub(sp);
            f = dir.len() - 140.0F;
            dir.nor();
            Gdx.gl.glEnable(3042);
            Gdx.gl.glBlendFunc(770, 771);
            this.level.sr.begin(ShapeRenderer.ShapeType.Line);
            this.level.sr.setColor(1.0F, 0.0F, 0.0F, 0.5F);

            for(d = 0.0F; d < f - 16.0F; d += 16.0F) {
               d2 = 70.0F + d;
               float d2 = 70.0F + d + 8.0F;
               this.level.sr.line(sp.x + dir.x * d2, sp.y + dir.y * d2, sp.x + dir.x * d2, sp.y + dir.y * d2);
            }

            this.level.sr.end();
            Rectangle icon = new Rectangle((pos.x + sp.x) / 2.0F - 16.0F, (pos.y + sp.y) / 2.0F - 16.0F, 32.0F, 32.0F);
            boolean in = icon.contains(Input.getMousePosition(true));
            batch.begin();
            batch.enableBlending();
            batch.setColor(1.0F, 1.0F, 1.0F, in ? 1.0F : 0.3F);
            batch.draw(Resources.GUI, icon.x, icon.y, icon.width, icon.height, 0, 112, 16, 16, false, false);
            batch.end();
            batch.setColor(Color.WHITE);
            if (in && Input.isButtonDown(2)) {
               this.stopAttack(target);
            }
         }
      }

      if (!this.enemy) {
         target = null;
         float min = 512.0F;
         Iterator var18 = this.level.getSquads().iterator();

         while(var18.hasNext()) {
            Squad s = (Squad)var18.next();
            if (s.isEnemy() && !this.targets.contains(s, false)) {
               Vector2 tp = camera.project(s.getLeader().getBounds().getCenter());
               Vector2 sub = tp.cpy().sub(sp);
               d2 = sub.len() - 140.0F;
               if (d2 < min) {
                  min = d2;
                  target = s;
               }
            }
         }

         if (target != null) {
            pos = camera.project(new Vector2(target.getLeader().getBounds().getCenter())).sub(sp);
            dir = pos.cpy().nor();
            Gdx.gl.glEnable(3042);
            Gdx.gl.glBlendFunc(770, 771);
            this.level.sr.begin(ShapeRenderer.ShapeType.Line);
            this.level.sr.setColor(0.5F, 0.5F, 0.5F, 0.5F);

            for(f = 0.0F; f < min - 16.0F; f += 16.0F) {
               d = 70.0F + f;
               d2 = 70.0F + f + 8.0F;
               this.level.sr.line(sp.x + dir.x * d, sp.y + dir.y * d, sp.x + dir.x * d2, sp.y + dir.y * d2);
            }

            this.level.sr.end();
         }
      }

      Gdx.gl.glEnable(3042);
      Gdx.gl.glBlendFunc(770, 771);
      if (!this.enemy || leaderPos.dst(Input.getMousePosition(true)) < 70.0F || this.inCombat) {
         this.level.sr.begin(ShapeRenderer.ShapeType.Filled);
         var6 = this.members.iterator();

         while(var6.hasNext()) {
            Unit u = (Unit)var6.next();
            pos = camera.project(u.getBounds().getCenter());
            this.level.sr.setColor(Color.GREEN);
            float w = 16.0F;
            f = w * u.getStats().hp / u.getStats().maxHp;
            d = -20.0F;
            this.level.sr.rect(pos.x - w / 2.0F, pos.y + d, f, 1.0F);
            this.level.sr.setColor(Color.RED);
            this.level.sr.rect(pos.x - w / 2.0F + f, pos.y + d, w - f, 1.0F);
         }

         this.level.sr.end();
      }

      String text = this.name + " (" + this.members.size + "/" + 9 + ")";
      BitmapFont.TextBounds tb = Resources.FONT.getBounds(text);
      this.level.sr.begin(ShapeRenderer.ShapeType.Filled);
      this.level.sr.setColor(0.0F, 0.0F, 0.0F, 0.3F);
      this.level.sr.rect(leaderPos.x - tb.width / 2.0F - 4.0F, leaderPos.y + 70.0F, tb.width + 8.0F, tb.height + 8.0F);
      this.level.sr.end();
      batch.begin();
      if (this.enemy) {
         batch.draw(Resources.SPRITES[12 + this.flag.getCurrentFrame()][4], leaderPos.x - 16.0F, leaderPos.y + 90.0F, 16.0F, 16.0F);
      } else {
         float dy = 0.0F;
         if (this.position.dst(((Unit)this.members.get(0)).getBounds().getCenter()) < 32.0F) {
            dy = 90.0F;
         } else {
            batch.setColor(Color.GRAY);
         }

         batch.draw(Resources.SPRITES[12 + this.flag.getCurrentFrame()][5], sp.x - 16.0F, sp.y + dy, 16.0F, 16.0F);
         batch.setColor(Color.WHITE);
      }

      Resources.FONT.draw(batch, text, leaderPos.x - tb.width / 2.0F, leaderPos.y + tb.height + 74.0F);
      batch.end();
      Gdx.gl.glEnable(3042);
      Gdx.gl.glBlendFunc(770, 771);
      if (this.enemy) {
         if (leaderPos.dst(Input.getMousePosition(true)) < 70.0F || this.inCombat) {
            this.level.sr.setColor(1.0F, 0.0F, 0.0F, 0.5F);
            this.level.sr.setProjectionMatrix(batch.getProjectionMatrix());
            this.level.sr.begin(ShapeRenderer.ShapeType.Line);
            this.level.sr.circle(leaderPos.x, leaderPos.y, 70.0F);
            this.level.sr.end();
         }
      } else if (this.selected) {
         this.level.sr.setColor(1.0F, 1.0F, 1.0F, 0.5F);
         this.level.sr.setProjectionMatrix(batch.getProjectionMatrix());
         this.level.sr.begin(ShapeRenderer.ShapeType.Line);
         this.level.sr.circle(sp.x, sp.y, 70.0F);
         this.level.sr.end();
      }

      Gdx.gl.glDisable(3042);
   }

   public void setTarget(int tx, int ty) {
      if (!this.level.isBlocked(tx, ty)) {
         boolean taken = this.spotTaken(tx, ty);

         for(int j = 0; taken; ++j) {
            int n = (3 + j * 2) * 2;
            n += n - 4;

            for(float a = 0.0F; a < 6.2831855F; a += 6.2831855F / (float)n) {
               float dx = MathUtils.sin(a) * (float)(j + 1);
               float dy = MathUtils.cos(a) * (float)(j + 1);
               if (!this.spotTaken((int)((float)tx + dx), (int)((float)ty + dy))) {
                  taken = false;
                  tx = (int)((float)tx + dx);
                  ty = (int)((float)ty + dy);
                  break;
               }
            }
         }

         Vector2 s = ((Unit)this.members.get(0)).getBounds().getCenter();
         this.path = this.level.getPath((int)(s.x / 32.0F), (int)(s.y / 32.0F), tx, ty, 256, (Unit)null);
         if (this.path != null) {
            this.position.set((float)(tx << 5), (float)(ty << 5));

            for(int i = 0; i < this.members.size; ++i) {
               ((Unit)this.members.get(i)).setPath(this.path, (float)i * 0.5F);
            }
         }

      }
   }

   public Path getPath() {
      return this.path;
   }

   private boolean spotTaken(int x, int y) {
      x <<= 5;
      y <<= 5;
      Iterator var4 = this.level.getSquads().iterator();

      while(var4.hasNext()) {
         Squad s = (Squad)var4.next();
         if (this.enemy == s.isEnemy() && this.id != s.id) {
            float dx = s.position.x - (float)x;
            float dy = s.position.y - (float)y;
            if (dx * dx + dy * dy < 15000.0F) {
               return true;
            }
         }
      }

      return false;
   }

   public Vector2 getPosition() {
      return this.position;
   }

   public Array<Unit> getMembers() {
      return this.members;
   }

   public boolean hasMembers() {
      return this.members.size > 0;
   }

   public Unit getLeader() {
      return (Unit)this.members.get(0);
   }

   public void setSelected(boolean b) {
      this.selected = b;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         return this.id == ((Squad)obj).id;
      }
   }

   public boolean isEnemy() {
      return this.enemy;
   }

   public boolean isInCombat() {
      return this.inCombat;
   }

   public void startAttack(Squad s) {
      if (!this.targets.contains(s, false)) {
         this.targets.add(s);
         this.inCombat = true;
      }

   }

   public void startBaseAttack(Base base) {
      this.baseTarget = base;
   }

   public void stopAttack(Squad s) {
      this.targets.removeValue(s, false);
      this.inCombat = this.targets.size > 0;
   }

   public void stopBaseAttack() {
      this.baseTarget = null;
   }

   public Base getBaseTarget() {
      return this.baseTarget;
   }

   public Array<Squad> getTargets() {
      return this.targets;
   }

   public void addRandomMembers(int n) {
      if (n > 9) {
         n = 9;
      }

      for(int i = 0; i < n; ++i) {
         int random = MathUtils.random(3);
         switch(random) {
         case 0:
            this.add((Unit)(this.enemy ? new Archer() : new Bowman()));
            break;
         case 1:
            this.add((Unit)(this.enemy ? new Warrior() : new Knight()));
            break;
         case 2:
            this.add((Unit)(this.enemy ? new Necro() : new Magician()));
            break;
         case 3:
            this.add((Unit)(this.enemy ? new Lich() : new Monk()));
         }
      }

   }

   public void flee() {
      this.baseTarget = null;
      this.targets.clear();
      this.inCombat = false;
      Iterator var2 = this.level.getSquads().iterator();

      while(var2.hasNext()) {
         Squad s = (Squad)var2.next();
         if (s.isEnemy() != this.enemy) {
            s.targets.removeValue(this, false);
            s.inCombat = s.targets.size > 0;
         }
      }

   }

   public static enum EnemyType {
      ZERG,
      DEFEND;
   }
}
