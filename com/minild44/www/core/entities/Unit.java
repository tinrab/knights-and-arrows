package com.minild44.www.core.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.minild44.www.core.Level;
import com.minild44.www.pathfinding.Mover;
import com.minild44.www.pathfinding.Path;
import com.minild44.www.pathfinding.Step;
import com.minild44.www.util.Animation;
import java.util.Iterator;

public abstract class Unit extends Entity implements Mover {
   public static final byte STATE_DEFAULT = 0;
   public static final byte STATE_RIGHT = 1;
   public static final byte STATE_LEFT = 2;
   public static final byte STATE_DOWN = 3;
   public static final byte STATE_UP = 4;
   public static final byte STATE_ATTACK_RIGHT = 5;
   public static final byte STATE_ATTACK_LEFT = 6;
   public static final byte STATE_ATTACK_UP = 7;
   public static final byte STATE_ATTACK_DOWN = 8;
   protected Animation animation = new Animation(2, 0.3F);
   protected Animation fireAnimation = new Animation(2, 1.0F);
   protected float speed = 1.0F;
   protected byte state = 0;
   protected boolean isLeader;
   protected boolean isMoving;
   protected float lastAttack;
   protected Stats stats = new Stats();
   protected Squad squad;
   private Path path;
   private int step;
   private Vector2 goal;
   private float delay;
   public boolean adjust;

   public Unit() {
      this.layer = 2;
   }

   public void join(Squad squad) {
      this.squad = squad;
   }

   public void updateDirection(float dx, float dy) {
      if (dx == 0.0F && dy == 0.0F) {
         this.state = 0;
      } else {
         float a = MathUtils.atan2(dx, dy) * 57.295776F + 180.0F;
         if (!(a >= 310.0F) && !(a <= 40.0F)) {
            if (a >= 40.0F && a <= 130.0F) {
               this.state = 2;
            } else if (a >= 130.0F && a <= 220.0F) {
               this.state = 4;
            } else {
               this.state = 1;
            }
         } else {
            this.state = 3;
         }
      }

   }

   private boolean move(float dx, float dy) {
      this.isMoving = true;
      if (dx != 0.0F && dy != 0.0F) {
         this.move(dx, 0.0F);
         this.move(0.0F, dy);
         return true;
      } else {
         dx *= this.speed;
         dy *= this.speed;
         if (!this.canMoveTo(dx, dy) && this.path == null) {
            return false;
         } else {
            this.bounds.translate(dx, dy);
            return true;
         }
      }
   }

   private boolean canMoveTo(float dx, float dy) {
      int x0 = (int)(this.bounds.x + dx);
      int y0 = (int)(this.bounds.y + dy);
      int x1 = x0 + (int)this.bounds.width;
      int y1 = y0 + (int)this.bounds.height;
      x0 >>= 5;
      x1 >>= 5;
      y0 >>= 5;
      y1 >>= 5;
      return !this.level.isBlocked(x0, y0) && !this.level.isBlocked(x1, y0) && !this.level.isBlocked(x1, y1) && !this.level.isBlocked(x0, y1);
   }

   public void attack(Vector2 target) {
      this.squad.lastAttack = 16.0F;
      this.lastAttack = this.stats.attackRate + MathUtils.random() * 0.2F - 0.1F;
      Vector2 c = this.bounds.getCenter();
      this.fire(c.x, c.y, target);
      float a = MathUtils.atan2(target.x - c.x, target.y - c.y) * 57.295776F + 180.0F;
      if (!(a >= 310.0F) && !(a <= 40.0F)) {
         if (a >= 40.0F && a <= 130.0F) {
            this.state = 6;
         } else if (a >= 130.0F && a <= 220.0F) {
            this.state = 7;
         } else {
            this.state = 5;
         }
      } else {
         this.state = 8;
      }

   }

   protected abstract void fire(float var1, float var2, Vector2 var3);

   public void update(float delta) {
      if (this.stats.hp <= 0.0F) {
         this.onDeadth();
      } else {
         this.fireAnimation.setFrameDuration(this.stats.attackRate / 2.0F);
         this.fireAnimation.update(delta);
         this.animation.update(delta);
         this.lastAttack -= delta;
         this.isMoving = false;
         Vector2 c;
         float dy;
         float dy;
         if (this.path != null) {
            this.delay -= delta;
            if (this.delay <= 0.0F) {
               c = this.bounds.getCenter();
               Step next = this.path.getStep(this.step);
               dy = next.x + 0.5F - c.x / 32.0F;
               dy = next.y + 0.5F - c.y / 32.0F;
               float len = (float)Math.sqrt((double)(dy * dy + dy * dy));
               if (len != 0.0F) {
                  dy /= len;
                  dy /= len;
               }

               if (this.move(dy * delta * 50.0F, dy * delta * 50.0F)) {
                  this.updateDirection(dy, dy);
               }

               if (len < 0.1F) {
                  ++this.step;
               }

               if (this.step >= this.path.getLength()) {
                  this.path = null;
                  this.state = 0;
               }
            } else {
               this.state = 0;
            }
         } else if (this.goal != null) {
            c = this.bounds.getCenter();
            float dx = this.goal.x + 0.5F - c.x / 32.0F;
            dy = this.goal.y + 0.5F - c.y / 32.0F;
            dy = (float)Math.sqrt((double)(dx * dx + dy * dy));
            if (dy > 0.1F) {
               if (dy != 0.0F) {
                  dx /= dy;
                  dy /= dy;
               }

               if (this.move(dx * delta * 50.0F, dy * delta * 50.0F)) {
                  this.updateDirection(dx, dy);
               }
            }
         }

         if ((this.squad.isInCombat() || this.squad.getBaseTarget() != null) && !this.isMoving) {
            if (this.lastAttack <= 0.0F) {
               this.state = 0;
               boolean attackedBased = false;
               if (this.squad.getBaseTarget() != null) {
                  Vector2 bc = this.squad.getBaseTarget().getBounds().getCenter();
                  if (this.level.canSee(new Vector2(this.bounds.x, this.bounds.y), bc, this.stats.attackRange)) {
                     attackedBased = true;
                     this.attack(bc);
                  }
               }

               if (!attackedBased) {
                  Array<Squad> targets = this.squad.getTargets();
                  Iterator var13 = targets.iterator();

                  while(var13.hasNext()) {
                     Squad s = (Squad)var13.next();
                     Iterator var7 = s.getMembers().iterator();

                     while(var7.hasNext()) {
                        Unit u = (Unit)var7.next();
                        if (this.level.canSee(this.bounds.getCenter(), u.getBounds().getCenter(), this.stats.attackRange)) {
                           this.attack(u.getBounds().getCenter());
                           return;
                        }
                     }
                  }
               }
            }
         } else if (!this.isMoving) {
            this.state = 0;
         }

      }
   }

   public void setPath(Path path, float delay) {
      this.adjust = !this.isLeader;
      this.path = path;
      this.delay = delay;
      this.step = 0;
   }

   public void setGoal(float x, float y) {
      this.goal = new Vector2(x, y);
   }

   public Vector2 getGoal() {
      return this.goal;
   }

   public boolean isLeader() {
      return this.isLeader;
   }

   public void setLeader(boolean b) {
      this.isLeader = b;
   }

   public void setSpeed(float speed) {
      this.speed = speed;
   }

   public float getSpeed() {
      return this.speed;
   }

   public Stats getStats() {
      return this.stats;
   }

   public void hurt(float hp) {
      Stats var10000 = this.stats;
      var10000.hp -= hp;
   }

   public void heal(float recover) {
      Stats var10000 = this.stats;
      var10000.hp += recover;
      if (this.stats.hp > this.stats.maxHp) {
         this.stats.hp = this.stats.maxHp;
      }

   }

   public Squad getSquad() {
      return this.squad;
   }

   private void onDeadth() {
      if (this.squad.isEnemy()) {
         ++this.level.killCount;
         Level var10000 = this.level;
         var10000.gold += MathUtils.random.nextInt(2) + 2;
      }

      this.remove();
   }
}
