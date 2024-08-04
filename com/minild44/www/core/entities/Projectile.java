package com.minild44.www.core.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.minild44.www.core.Base;
import java.util.Iterator;

public abstract class Projectile extends Entity {
   protected float speed = 1.0F;
   protected float damage = 10.0F;
   protected float angle;
   protected float dx;
   protected float dy;
   protected Unit owner;

   public Projectile(float sx, float sy, Vector2 target, Unit owner) {
      this.layer = 2;
      this.bounds.set(sx, sy, 0.0F, 0.0F);
      this.owner = owner;
      float miss = 10.0F * MathUtils.random() - 5.0F;
      this.angle = MathUtils.atan2(target.x - sx, target.y - sy) * 57.295776F + miss;
      this.dx = MathUtils.sinDeg(this.angle);
      this.dy = MathUtils.cosDeg(this.angle);
   }

   public void update(float delta) {
      if (this.level.isBlocked((int)(this.bounds.x / 32.0F), (int)(this.bounds.y / 32.0F))) {
         this.onHitLevel();
         if (this.level.getCamera().frustum.pointInFrustum(new Vector3(this.bounds.x, this.bounds.y, 0.0F))) {
            this.level.getCamera().addShake(0.3F);
         }

         this.remove();
      } else {
         Iterator var3 = this.level.getEntities().iterator();

         while(var3.hasNext()) {
            Entity e = (Entity)var3.next();
            if (e.getBounds().contains(this.bounds.x, this.bounds.y)) {
               if (e instanceof Unit) {
                  Unit u = (Unit)e;
                  if (this.owner.getSquad().isEnemy() != u.getSquad().isEnemy()) {
                     this.onHitUnit(u);
                     if (this.level.getCamera().frustum.pointInFrustum(new Vector3(this.bounds.x, this.bounds.y, 0.0F))) {
                        this.level.getCamera().addShake(0.3F);
                     }

                     this.remove();
                     if (!u.getSquad().isInCombat() && this.owner.getSquad().hasMembers()) {
                        u.getSquad().startAttack(this.owner.getSquad());
                     }

                     return;
                  }
               } else if (e instanceof Base) {
                  Base base = (Base)e;
                  if (base.isEnemy() != this.owner.getSquad().isEnemy()) {
                     base.hurt(this.damage);
                     this.onHitLevel();
                     if (this.level.getCamera().frustum.pointInFrustum(new Vector3(this.bounds.x, this.bounds.y, 0.0F))) {
                        this.level.getCamera().addShake(0.3F);
                     }

                     this.remove();
                  }
               }
            }
         }

         this.bounds.translate(this.dx * this.speed * delta, this.dy * this.speed * delta);
      }
   }

   public abstract void onHitLevel();

   public abstract void onHitUnit(Unit var1);
}
