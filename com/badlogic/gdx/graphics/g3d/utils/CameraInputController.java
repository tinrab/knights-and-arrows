package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class CameraInputController extends InputAdapter {
   public int rotateButton = 0;
   public float rotateAngle = 360.0F;
   public int translateButton = 1;
   public float translateUnits = 10.0F;
   public int forwardButton = 2;
   public int activateKey = 0;
   protected boolean activatePressed;
   public boolean alwaysScroll = true;
   public float scrollFactor = -0.1F;
   public boolean autoUpdate = true;
   public Vector3 target = new Vector3();
   public boolean translateTarget = true;
   public boolean forwardTarget = true;
   public boolean scrollTarget = false;
   public int forwardKey = 19;
   protected boolean forwardPressed;
   public int backwardKey = 20;
   protected boolean backwardPressed;
   public int rotateRightKey = 22;
   protected boolean rotateRightPressed;
   public int rotateLeftKey = 21;
   protected boolean rotateLeftPressed;
   public Camera camera;
   protected int button = -1;
   private float startX;
   private float startY;
   private final Vector3 tmpV1 = new Vector3();
   private final Vector3 tmpV2 = new Vector3();

   public CameraInputController(Camera camera) {
      this.camera = camera;
   }

   public void update() {
      if (this.rotateRightPressed || this.rotateLeftPressed || this.forwardPressed || this.backwardPressed) {
         float delta = Gdx.graphics.getDeltaTime();
         if (this.rotateRightPressed) {
            this.camera.rotate(this.camera.up, -delta * this.rotateAngle);
         }

         if (this.rotateLeftPressed) {
            this.camera.rotate(this.camera.up, delta * this.rotateAngle);
         }

         if (this.forwardPressed) {
            this.camera.translate(this.tmpV1.set(this.camera.direction).scl(delta * this.translateUnits));
            if (this.forwardTarget) {
               this.target.add(this.tmpV1);
            }
         }

         if (this.backwardPressed) {
            this.camera.translate(this.tmpV1.set(this.camera.direction).scl(-delta * this.translateUnits));
            if (this.forwardTarget) {
               this.target.add(this.tmpV1);
            }
         }

         if (this.autoUpdate) {
            this.camera.update();
         }
      }

   }

   public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      if (this.button < 0 && (this.activateKey == 0 || this.activatePressed)) {
         this.startX = (float)screenX;
         this.startY = (float)screenY;
         this.button = button;
      }

      return this.activatePressed;
   }

   public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      if (button == this.button) {
         this.button = -1;
      }

      return this.activatePressed;
   }

   protected boolean process(float deltaX, float deltaY, int button) {
      if (button == this.rotateButton) {
         this.tmpV1.set(this.camera.direction).crs(this.camera.up).y = 0.0F;
         this.camera.rotateAround(this.target, this.tmpV1.nor(), deltaY * this.rotateAngle);
         this.camera.rotateAround(this.target, Vector3.Y, deltaX * -this.rotateAngle);
      } else if (button == this.translateButton) {
         this.camera.translate(this.tmpV1.set(this.camera.direction).crs(this.camera.up).nor().scl(-deltaX * this.translateUnits));
         this.camera.translate(this.tmpV2.set(this.camera.up).scl(-deltaY * this.translateUnits));
         if (this.translateTarget) {
            this.target.add(this.tmpV1).add(this.tmpV2);
         }
      } else if (button == this.forwardButton) {
         this.camera.translate(this.tmpV1.set(this.camera.direction).scl(deltaY * this.translateUnits));
         if (this.forwardTarget) {
            this.target.add(this.tmpV1);
         }
      }

      if (this.autoUpdate) {
         this.camera.update();
      }

      return true;
   }

   public boolean touchDragged(int screenX, int screenY, int pointer) {
      if (this.button < 0) {
         return false;
      } else {
         float deltaX = ((float)screenX - this.startX) / (float)Gdx.graphics.getWidth();
         float deltaY = (this.startY - (float)screenY) / (float)Gdx.graphics.getHeight();
         this.startX = (float)screenX;
         this.startY = (float)screenY;
         return this.process(deltaX, deltaY, this.button);
      }
   }

   public boolean scrolled(int amount) {
      if (!this.alwaysScroll && this.activateKey != 0 && !this.activatePressed) {
         return false;
      } else {
         this.camera.translate(this.tmpV1.set(this.camera.direction).scl((float)amount * this.scrollFactor * this.translateUnits));
         if (this.scrollTarget) {
            this.target.add(this.tmpV1);
         }

         if (this.autoUpdate) {
            this.camera.update();
         }

         return true;
      }
   }

   public boolean keyDown(int keycode) {
      if (keycode == this.activateKey) {
         this.activatePressed = true;
      }

      if (keycode == this.forwardKey) {
         this.forwardPressed = true;
      } else if (keycode == this.backwardKey) {
         this.backwardPressed = true;
      } else if (keycode == this.rotateRightKey) {
         this.rotateRightPressed = true;
      } else if (keycode == this.rotateLeftKey) {
         this.rotateLeftPressed = true;
      }

      return false;
   }

   public boolean keyUp(int keycode) {
      if (keycode == this.activateKey) {
         this.activatePressed = false;
         this.button = -1;
      }

      if (keycode == this.forwardKey) {
         this.forwardPressed = false;
      } else if (keycode == this.backwardKey) {
         this.backwardPressed = false;
      } else if (keycode == this.rotateRightKey) {
         this.rotateRightPressed = false;
      } else if (keycode == this.rotateLeftKey) {
         this.rotateLeftPressed = false;
      }

      return false;
   }
}
