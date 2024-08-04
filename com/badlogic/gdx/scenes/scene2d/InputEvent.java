package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.math.Vector2;

public class InputEvent extends Event {
   private InputEvent.Type type;
   private float stageX;
   private float stageY;
   private int pointer;
   private int button;
   private int keyCode;
   private int scrollAmount;
   private char character;
   private Actor relatedActor;

   public void reset() {
      super.reset();
      this.relatedActor = null;
      this.button = -1;
   }

   public float getStageX() {
      return this.stageX;
   }

   public void setStageX(float stageX) {
      this.stageX = stageX;
   }

   public float getStageY() {
      return this.stageY;
   }

   public void setStageY(float stageY) {
      this.stageY = stageY;
   }

   public InputEvent.Type getType() {
      return this.type;
   }

   public void setType(InputEvent.Type type) {
      this.type = type;
   }

   public int getPointer() {
      return this.pointer;
   }

   public void setPointer(int pointer) {
      this.pointer = pointer;
   }

   public int getButton() {
      return this.button;
   }

   public void setButton(int button) {
      this.button = button;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public void setKeyCode(int keyCode) {
      this.keyCode = keyCode;
   }

   public char getCharacter() {
      return this.character;
   }

   public void setCharacter(char character) {
      this.character = character;
   }

   public int getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(int scrollAmount) {
      this.scrollAmount = scrollAmount;
   }

   public Actor getRelatedActor() {
      return this.relatedActor;
   }

   public void setRelatedActor(Actor relatedActor) {
      this.relatedActor = relatedActor;
   }

   public Vector2 toCoordinates(Actor actor, Vector2 actorCoords) {
      actorCoords.set(this.stageX, this.stageY);
      actor.stageToLocalCoordinates(actorCoords);
      return actorCoords;
   }

   public String toString() {
      return this.type.toString();
   }

   public static enum Type {
      touchDown,
      touchUp,
      touchDragged,
      mouseMoved,
      enter,
      exit,
      scrolled,
      keyDown,
      keyUp,
      keyTyped;
   }
}
