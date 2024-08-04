package com.minild44.www.pathfinding;

import java.io.Serializable;
import java.util.ArrayList;

public class Path implements Serializable {
   private static final long serialVersionUID = 1L;
   private ArrayList<Step> steps = new ArrayList();

   public int getLength() {
      return this.steps.size();
   }

   public Step getStep(int index) {
      return (Step)this.steps.get(index);
   }

   public float getX(int index) {
      return this.getStep(index).x;
   }

   public float getY(int index) {
      return this.getStep(index).y;
   }

   public void appendStep(float x, float y) {
      this.steps.add(new Step(x, y));
   }

   public void prependStep(float x, float y) {
      this.steps.add(0, new Step(x, y));
   }

   public boolean contains(float x, float y) {
      return this.steps.contains(new Step(x, y));
   }

   public void setSteps(ArrayList<Step> steps) {
      this.steps = steps;
   }

   public ArrayList<Step> getSteps() {
      return this.steps;
   }
}
