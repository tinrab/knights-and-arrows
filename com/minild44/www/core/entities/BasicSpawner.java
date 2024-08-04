package com.minild44.www.core.entities;

import com.badlogic.gdx.math.MathUtils;
import com.minild44.www.core.Base;
import com.minild44.www.core.Level;
import java.util.Iterator;

public class BasicSpawner extends Spawner {
   public BasicSpawner(int x, int y, int w, int h, int delay, int max, Entity target) {
      super(x, y, w, h, delay, max, target);
   }

   public void update(float delta, Level level) {
      this.last -= delta;
      if (this.last <= 0.0F) {
         int count = 0;
         Iterator var5 = level.getSquads().iterator();

         while(var5.hasNext()) {
            Squad s = (Squad)var5.next();
            if (s.spawnerID == this.id) {
               ++count;
            }
         }

         if (count <= this.max) {
            this.last = (float)this.delay;
            this.spawn(level);
         }
      }

   }

   private void spawn(Level level) {
      Squad squad = new Squad(this.bounds.x + MathUtils.random() * this.bounds.width, this.bounds.y + MathUtils.random() * this.bounds.height, level, true);
      squad.addRandomMembers(level.getStage() * 2);
      squad.setTarget((int)(this.target.getBounds().x / 32.0F), (int)(this.target.getBounds().y / 32.0F));
      if (!((Base)this.target).isEnemy()) {
         squad.startBaseAttack((Base)this.target);
         squad.setEnemyType(Squad.EnemyType.ZERG);
      } else {
         squad.setEnemyType(Squad.EnemyType.DEFEND);
      }

      squad.spawnerID = this.id;
   }
}
