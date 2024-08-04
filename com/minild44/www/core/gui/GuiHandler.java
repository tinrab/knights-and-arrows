package com.minild44.www.core.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.minild44.www.Input;
import com.minild44.www.Main;
import com.minild44.www.Resources;
import com.minild44.www.core.Base;
import com.minild44.www.core.Level;
import com.minild44.www.core.entities.Campfire;
import com.minild44.www.core.entities.Entity;
import com.minild44.www.core.entities.Potato;
import com.minild44.www.core.entities.Squad;
import com.minild44.www.core.entities.Torch;
import com.minild44.www.core.entities.Unit;
import com.minild44.www.core.entities.units.Bowman;
import com.minild44.www.core.entities.units.Knight;
import com.minild44.www.core.entities.units.Magician;
import com.minild44.www.core.entities.units.Monk;
import com.minild44.www.util.Camera;
import java.util.Iterator;

public class GuiHandler {
   private Level level;
   private ShapeRenderer sr = new ShapeRenderer();
   private Vector2 lastClick = new Vector2();
   private boolean busy;
   private Object selectedObject;
   private boolean aMenu = false;
   private float aStage;
   private boolean cMenu = false;
   private boolean bMenu = false;
   private TextField name;
   private boolean sReady = true;
   private float ox;
   private float oy;
   private Rectangle selection = new Rectangle();
   private byte sMode = 0;
   private boolean selectAll;
   private Stage stage;
   private TextButton.TextButtonStyle style;
   private boolean first = true;

   public GuiHandler(Level level) {
      this.level = level;
   }

   public void updateAndRender(float delta, SpriteBatch batch, Camera camera, boolean paused) {
      this.sr.setProjectionMatrix(camera.combined);
      Vector2 mouse = Input.unprojectMouse(camera);
      float range;
      BitmapFont.TextBounds tb;
      BitmapFont.TextBounds tb;
      if (!this.busy && !paused) {
         Iterator var7;
         Array squads;
         if (Input.isButtonPressedAndReleased(0) && Input.getMouseX() < Gdx.graphics.getWidth() - 110) {
            this.lastClick.set(mouse.x, mouse.y);
            this.selectedObject = null;
            var7 = this.level.getObjects("actions").iterator();

            while(var7.hasNext()) {
               MapObject o = (MapObject)var7.next();
               if (o.getBounds().contains(mouse)) {
                  this.selectedObject = new String(o.getName());
                  break;
               }
            }

            squads = this.level.getSquads();

            for(int j = 0; j < squads.size; ++j) {
               Squad s = (Squad)squads.get(j);
               if (s.isEnemy()) {
                  Vector2 sp = camera.project(s.getLeader().getBounds().getCenter());
                  if (sp.dst(Input.getMousePosition(true)) < 68.0F) {
                     this.selectedObject = s;
                  }
               }
            }

            if (this.selectedObject == null && this.level.getRedBase().getBounds().contains(mouse)) {
               this.selectedObject = this.level.getRedBase();
            }

            if (this.selectedObject != null && !(this.selectedObject instanceof Squad)) {
               if (this.selectedObject instanceof String) {
                  String obj = (String)this.selectedObject;
                  if (obj.equals("baracks")) {
                     if (this.cMenu) {
                        this.cMenu = false;
                        this.busy = false;
                     } else {
                        this.busy = true;
                        this.cMenu = true;
                        this.setCMenu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        Resources.GUISOUNDS[1].play(1.0F);
                     }
                  } else if (obj.equals("base")) {
                     if (this.bMenu) {
                        this.bMenu = false;
                        this.busy = false;
                     } else {
                        this.busy = true;
                        this.bMenu = true;
                        this.setBMenu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        Resources.GUISOUNDS[1].play(1.0F);
                     }
                  }
               } else if (this.selectedObject instanceof Base) {
                  Base b = (Base)this.selectedObject;
                  if (b.isEnemy()) {
                     if (this.aMenu) {
                        this.aMenu = false;
                        this.busy = false;
                     } else {
                        this.busy = true;
                        this.aMenu = true;
                        this.aStage = 0.0F;
                     }
                  } else if (this.bMenu) {
                     this.bMenu = false;
                     this.busy = false;
                  } else {
                     this.busy = true;
                     this.bMenu = true;
                     this.setBMenu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                     Resources.GUISOUNDS[1].play(1.0F);
                  }
               }
            } else if (this.aMenu) {
               this.aMenu = false;
               this.busy = false;
            } else {
               this.busy = true;
               this.aMenu = true;
               this.aStage = 0.0F;
            }
         }

         Squad s;
         if (Input.isKeyPressed(129)) {
            var7 = this.level.getSquads().iterator();

            while(var7.hasNext()) {
               s = (Squad)var7.next();
               if (!s.isEnemy()) {
                  s.setSelected(!s.isSelected());
               }
            }
         }

         if (Input.isKeyPressed(29)) {
            var7 = this.level.getSquads().iterator();

            while(var7.hasNext()) {
               s = (Squad)var7.next();
               if (!s.isEnemy()) {
                  s.setSelected(!this.selectAll);
               }
            }

            this.selectAll = !this.selectAll;
         }

         if (Input.getMouseX() >= Gdx.graphics.getWidth() - 110) {
            this.sReady = true;
            this.ox = 0.0F;
            this.oy = 0.0F;
            this.selection.set(0.0F, 0.0F, 0.0F, 0.0F);
         } else if (Input.isButtonDown(0)) {
            if (Input.hasMouseMoved()) {
               if (this.sReady) {
                  this.sReady = false;
                  this.ox = mouse.x;
                  this.oy = mouse.y;
                  this.sMode = 0;
                  this.sMode = Input.isKeyDown(59) ? 1 : this.sMode;
               } else {
                  this.selection.set(this.ox, this.oy, mouse.x - this.ox, mouse.y - this.oy);
                  this.selection.fixNegativeSize();
               }
            }
         } else if (!this.sReady) {
            if (this.selection.width > 1.0F && this.selection.height > 1.0F) {
               squads = this.level.getSquads();
               Iterator var20 = squads.iterator();

               while(var20.hasNext()) {
                  Squad s = (Squad)var20.next();
                  if (!s.isEnemy()) {
                     if (this.sMode == 0) {
                        s.setSelected(false);
                     }

                     if (this.selection.contains(s.getLeader().getBounds().getCenter())) {
                        s.setSelected(true);
                     }
                  }
               }
            }

            this.sReady = true;
            this.ox = 0.0F;
            this.oy = 0.0F;
            this.selection.set(0.0F, 0.0F, 0.0F, 0.0F);
         }

         Gdx.gl.glEnable(3042);
         Gdx.gl.glBlendFunc(770, 771);
         String text = "";
         boolean repaint = false;
         Iterator var25 = this.level.getEntities().iterator();

         while(var25.hasNext()) {
            Entity e = (Entity)var25.next();
            if (e.getBounds().contains(mouse)) {
               if (e instanceof Unit) {
                  repaint = true;
                  Unit u = (Unit)e;
                  text = u.getClass().getSimpleName();
                  range = u.getStats().attackRange / 32.0F;
                  text = text + "\nRange: " + (float)((int)(range * 100.0F)) / 100.0F;
                  text = text + "\nRate: " + (float)((int)(1.0F / u.getStats().attackRate * 100.0F)) / 100.0F;
                  text = text + "\nHP: " + u.getStats().hp + "/" + u.getStats().maxHp;
                  tb = Resources.FONT.getMultiLineBounds(text);
                  this.sr.setColor(0.0F, 0.0F, 0.0F, 0.65F);
                  this.sr.begin(ShapeRenderer.ShapeType.Filled);
                  this.sr.rect(mouse.x - 5.0F, mouse.y - 5.0F, tb.width + 10.0F, tb.height + 10.0F);
                  this.sr.end();
                  batch.begin();
                  Resources.FONT.drawMultiLine(batch, text, (float)Input.getMouseX(), (float)Input.getMouseY() + tb.height);
                  batch.end();
               } else if (e instanceof Base) {
                  Base b = (Base)e;
                  if (b.isEnemy()) {
                     text = "Red Base";
                  } else {
                     repaint = true;
                     text = "Blue Base";
                     BitmapFont.TextBounds tb = Resources.FONT.getBounds(text);
                     this.sr.setColor(0.0F, 0.0F, 0.0F, 0.65F);
                     this.sr.begin(ShapeRenderer.ShapeType.Filled);
                     this.sr.rect(mouse.x - 5.0F, mouse.y - 5.0F, tb.width + 10.0F, tb.height + 10.0F);
                     this.sr.end();
                     batch.begin();
                     batch.draw(Resources.GUI, (float)Input.getMouseX() + tb.width + 10.0F, (float)(Input.getMouseY() - 10), 0.0F, 0.0F, 32.0F, 32.0F, 1.0F, 1.0F, 0.0F, 32, 112, 16, 16, false, false);
                     Resources.FONT.drawMultiLine(batch, text, (float)Input.getMouseX(), (float)Input.getMouseY() + tb.height);
                     batch.end();
                  }
               } else if (e instanceof Potato) {
                  text = "Potato";
               } else if (e instanceof Torch) {
                  text = "Torch";
               } else if (e instanceof Campfire) {
                  repaint = true;
                  text = "Campfire";
                  tb = Resources.FONT.getBounds(text);
                  this.sr.setColor(0.0F, 0.0F, 0.0F, 0.65F);
                  this.sr.begin(ShapeRenderer.ShapeType.Filled);
                  this.sr.rect(mouse.x - 5.0F, mouse.y - 5.0F, tb.width + 10.0F, tb.height + 10.0F);
                  this.sr.end();
                  batch.begin();
                  batch.draw(Resources.GUI, (float)Input.getMouseX() + tb.width + 10.0F, (float)(Input.getMouseY() - 10), 0.0F, 0.0F, 32.0F, 32.0F, 1.0F, 1.0F, 0.0F, 16, 112, 16, 16, false, false);
                  Resources.FONT.drawMultiLine(batch, text, (float)Input.getMouseX(), (float)Input.getMouseY() + tb.height);
                  batch.end();
               }
               break;
            }
         }

         if (!text.equals("") && !repaint) {
            BitmapFont.TextBounds tb = Resources.FONT.getBounds(text);
            this.sr.setColor(0.0F, 0.0F, 0.0F, 0.65F);
            this.sr.begin(ShapeRenderer.ShapeType.Filled);
            this.sr.rect(mouse.x - 5.0F, mouse.y - 5.0F, tb.width + 10.0F, tb.height + 10.0F);
            this.sr.end();
            batch.begin();
            Resources.FONT.drawMultiLine(batch, text, (float)Input.getMouseX(), (float)Input.getMouseY() + tb.height);
            batch.end();
         }
      }

      Gdx.gl.glEnable(3042);
      Gdx.gl.glBlendFunc(770, 771);
      if (!this.sReady) {
         this.sr.begin(ShapeRenderer.ShapeType.Line);
         this.sr.setColor(1.0F, 1.0F, 1.0F, 0.8F);
         this.sr.rect(this.selection.x, this.selection.y, this.selection.width, this.selection.height);
         this.sr.end();
         this.sr.begin(ShapeRenderer.ShapeType.Filled);
         this.sr.setColor(1.0F, 1.0F, 1.0F, 0.1F);
         this.sr.rect(this.selection.x, this.selection.y, this.selection.width, this.selection.height);
         this.sr.end();
      }

      int i;
      if (this.aMenu) {
         this.aStage = MathUtils.lerp(this.aStage, 1.0F, delta * 6.0F);
         Rectangle[] icons = new Rectangle[3];
         Vector2 v = camera.project(this.lastClick);
         icons[0] = new Rectangle(v.x + 32.0F, v.y - 32.0F, 64.0F, 64.0F);
         icons[1] = new Rectangle(v.x - 32.0F, v.y + 32.0F, 64.0F, 64.0F);
         icons[2] = new Rectangle(v.x - 96.0F, v.y - 32.0F, 64.0F, 64.0F);
         batch.begin();
         boolean pressed = Input.isButtonPressed(0);

         for(i = 0; i < 3; ++i) {
            boolean selected = icons[i].contains(Input.getMousePosition(true));
            if (pressed) {
               this.busy = false;
               if (selected) {
                  this.aMenu = false;
                  Array<Squad> squads = this.level.getSquads();
                  int j;
                  Squad s;
                  label287:
                  switch(i) {
                  case 0:
                     j = 0;

                     while(true) {
                        if (j >= squads.size) {
                           break label287;
                        }

                        s = (Squad)squads.get(j);
                        if (s.isSelected()) {
                           s.flee();
                        }

                        ++j;
                     }
                  case 1:
                     j = 0;

                     while(true) {
                        if (j >= squads.size) {
                           break label287;
                        }

                        s = (Squad)squads.get(j);
                        if (s.isSelected()) {
                           s.setTarget((int)(this.lastClick.x / 32.0F), (int)(this.lastClick.y / 32.0F));
                        }

                        ++j;
                     }
                  case 2:
                     int j;
                     Squad s;
                     if (this.selectedObject instanceof Squad) {
                        Squad target = (Squad)this.selectedObject;

                        for(j = 0; j < squads.size; ++j) {
                           s = (Squad)squads.get(j);
                           if (s.isSelected()) {
                              s.startAttack(target);
                           }
                        }
                     } else if (this.selectedObject instanceof Base) {
                        Base b = (Base)this.selectedObject;

                        for(j = 0; j < squads.size; ++j) {
                           s = (Squad)squads.get(j);
                           if (s.isSelected()) {
                              s.startBaseAttack(b);
                           }
                        }
                     }
                  }

                  Input.restUps();
               }
            }

            range = 320.0F + 40.0F * (this.aStage * 4.0F - (float)i);
            if (range > 360.0F) {
               range = 360.0F;
            }

            batch.draw(Resources.ACTIONS, icons[i].x, icons[i].y, icons[i].width / 2.0F, icons[i].height / 2.0F, icons[i].width, icons[i].height, this.aStage, this.aStage, range, i << 4, selected ? 0 : 16, 16, 16, false, false);
            if (selected) {
               String str = "";
               switch(i) {
               case 0:
                  str = "Abort";
                  break;
               case 1:
                  str = "Move";
                  break;
               case 2:
                  str = "Attack";
               }

               BitmapFont.TextBounds tb = Resources.FONT.getBounds(str);
               Resources.FONT.draw(batch, str, v.x - tb.width / 2.0F, v.y + Resources.FONT.getLineHeight() / 2.0F);
            }
         }

         batch.end();
      }

      if (!this.cMenu && !this.bMenu) {
         Gdx.input.setInputProcessor(Main.INPUT);
      } else {
         batch.begin();
         batch.draw(Resources.GUI, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight(), 127, 127, 1, 1, false, false);
         batch.end();
         Gdx.input.setInputProcessor(this.stage);
         this.stage.act(delta);
         this.stage.draw();
      }

      Gdx.gl.glEnable(3042);
      Gdx.gl.glBlendFunc(770, 771);
      this.sr.begin(ShapeRenderer.ShapeType.Filled);
      this.sr.setColor(0.0F, 0.0F, 0.0F, 0.1F);
      Vector2 p = camera.unproject(new Vector2((float)(Gdx.graphics.getWidth() - 110), (float)Gdx.graphics.getHeight()));
      this.sr.rect(p.x, p.y, 110.0F, (float)Gdx.graphics.getHeight());
      this.sr.end();
      int padding = 5;
      int dy = 0;

      String name;
      for(i = 0; i < this.level.getSquads().size; ++i) {
         Squad s = (Squad)this.level.getSquads().get(i);
         if (!s.isEnemy()) {
            Rectangle rect = new Rectangle((float)(Gdx.graphics.getWidth() - padding - 96), (float)(Gdx.graphics.getHeight() - (48 + padding) * (dy + 1) - 48), 96.0F, 48.0F);
            batch.begin();
            if (rect.contains(Input.getMousePosition(true))) {
               batch.setColor(Color.GRAY);
               if (Input.isButtonDown(0) && this.sReady) {
                  camera.setTarget(s.getPosition().x, s.getPosition().y);
               }
            }

            batch.draw(Resources.GUI, rect.x, rect.y, rect.width, rect.height, 0, s.isInCombat() ? 91 : 79, 24, 12, false, false);
            batch.setColor(Color.WHITE);
            tb = Resources.FONT.getBounds(s.getName());
            Resources.FONT.draw(batch, s.getName(), rect.x + 48.0F - tb.width / 2.0F, rect.y + 48.0F - tb.height);
            name = s.getMembers().size + "/" + 9;
            tb = Resources.FONT.getBounds(name);
            Resources.FONT.draw(batch, name, rect.x + 48.0F - tb.width / 2.0F, rect.y + 32.0F - tb.height);
            batch.end();
            ++dy;
         }
      }

      String gold = this.level.gold + "G";
      tb = Resources.FONT.getBounds(gold);
      batch.begin();
      Resources.FONT.setColor(Color.YELLOW);
      Resources.FONT.draw(batch, gold, (float)(Gdx.graphics.getWidth() - 55) - tb.width / 2.0F, (float)(Gdx.graphics.getHeight() - 24));
      Resources.FONT.setColor(Color.WHITE);
      batch.end();
      if (!this.bMenu && !this.cMenu) {
         Iterator var51 = this.level.getObjects("actions").iterator();

         while(var51.hasNext()) {
            MapObject o = (MapObject)var51.next();
            name = new String(o.getName());
            if (name.equals("baracks")) {
               Vector2 pos = camera.project(new Vector2((float)(Integer.parseInt(o.getProperties().get("x").toString()) << 2), (float)(Integer.parseInt(o.getProperties().get("y").toString()) << 2)));
               batch.begin();
               batch.draw(Resources.GUI, pos.x + 8.0F, pos.y + 96.0F, 16.0F, 16.0F, 80, 112, 16, 16, false, false);
               batch.end();
            }
         }
      }

   }

   public void resize(int width, int height) {
      if (this.first) {
         this.style = new TextButton.TextButtonStyle();
         this.style.up = Resources.BUTTONS.getDrawable("button_off");
         this.style.over = Resources.BUTTONS.getDrawable("button_on");
         this.style.down = Resources.BUTTONS.getDrawable("button_active");
         this.style.font = Resources.FONT;
      }

      if (this.cMenu) {
         this.setCMenu(width, height);
      }

      if (this.bMenu) {
         this.setBMenu(width, height);
      }

      this.first = false;
   }

   private void addUnit(int id) {
      boolean added = false;
      if (this.level.gold >= 10) {
         Iterator var4 = this.level.getSquads().iterator();

         while(var4.hasNext()) {
            Squad s = (Squad)var4.next();
            if (s.isSelected() && s.getMembers().size < 9) {
               switch(id) {
               case 0:
                  s.addToFreeSpot(new Magician());
                  break;
               case 1:
                  s.addToFreeSpot(new Knight());
                  break;
               case 2:
                  s.addToFreeSpot(new Bowman());
                  break;
               case 3:
                  s.addToFreeSpot(new Monk());
               }

               Level var10000 = this.level;
               var10000.gold -= 10;
               added = true;
               break;
            }
         }
      }

      Resources.GUISOUNDS[added ? 1 : 2].play();
   }

   private void createNewSquad(String name) {
      Level var10000 = this.level;
      var10000.gold -= 100;
      Squad squad = new Squad(this.level.getBlueBase().getBounds().x, this.level.getBlueBase().getBounds().y + 64.0F, this.level, name);
      squad.addRandomMembers(1);
      float tx = this.level.squadCreateArea.x + MathUtils.random() * this.level.squadCreateArea.width;
      float ty = this.level.squadCreateArea.y + MathUtils.random() * this.level.squadCreateArea.height;
      squad.setTarget((int)(tx / 32.0F), (int)(ty / 32.0F));
   }

   private void hideShop() {
      this.cMenu = false;
      this.busy = false;
   }

   private void hideBase() {
      this.bMenu = false;
      this.busy = false;
   }

   public boolean isBusy() {
      return this.busy;
   }

   private void setBMenu(int width, int height) {
      this.stage = new Stage((float)width, (float)height, true);
      this.stage.clear();
      TextureRegion bg = new TextureRegion(Resources.GUI);
      bg.setRegion(0, 48, 55, 31);
      Image image = new Image(new TextureRegionDrawable(bg));
      image.setSize(440.0F, 248.0F);
      float x = (float)(width / 2 - 220);
      float y = (float)(height / 2 - 124);
      image.setPosition(x, y);
      this.stage.addActor(image);
      TextButton create = new TextButton("Create (100G)", this.style);
      create.setSize(128.0F, 48.0F);
      create.setPosition(x + 160.0F, y + 12.0F);
      create.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               if (GuiHandler.this.level.gold >= 100 && GuiHandler.this.level.getSquadCount(false) < 10) {
                  String text = GuiHandler.this.name.getText();
                  text = text.substring(1, text.length());
                  if (!text.equals("") && !text.equals(" ")) {
                     GuiHandler.this.createNewSquad(text);
                     Resources.GUISOUNDS[1].play();
                     GuiHandler.this.hideBase();
                  } else {
                     Resources.GUISOUNDS[2].play();
                  }
               } else {
                  Resources.GUISOUNDS[2].play();
               }
            }

            super.touchUp(event, x, y, pointer, button);
         }
      });
      this.stage.addActor(create);
      TextButton close = new TextButton("Close", this.style);
      close.setSize(128.0F, 48.0F);
      close.setPosition(x + 300.0F, y + 12.0F);
      close.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               Resources.GUISOUNDS[1].play();
               GuiHandler.this.hideBase();
            }

            super.touchUp(event, x, y, pointer, button);
         }
      });
      this.stage.addActor(close);
      Label.LabelStyle lstyle = new Label.LabelStyle();
      lstyle.font = Resources.FONT;
      Label label = new Label("Create a new squad. (max 10)", lstyle);
      label.setPosition(this.stage.getWidth() / 2.0F - 220.0F + 32.0F, this.stage.getHeight() / 2.0F + 48.0F);
      this.stage.addActor(label);
      Label label2 = new Label("Name: ", lstyle);
      label2.setPosition(this.stage.getWidth() / 2.0F - 220.0F + 32.0F, this.stage.getHeight() / 2.0F);
      this.stage.addActor(label2);
      TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle();
      fieldStyle.background = Resources.BUTTONS.getDrawable("field_bg");
      fieldStyle.disabledBackground = Resources.BUTTONS.getDrawable("field_bg");
      fieldStyle.focusedBackground = Resources.BUTTONS.getDrawable("field_focus");
      fieldStyle.cursor = Resources.BUTTONS.getDrawable("cursor");
      fieldStyle.font = Resources.FONT;
      fieldStyle.fontColor = Color.WHITE;
      this.name = new TextField(" ", fieldStyle);
      this.name.setCursorPosition(1);
      this.name.setMaxLength(9);
      this.name.setBlinkTime(0.5F);
      this.name.setPosition(this.stage.getWidth() / 2.0F - 135.0F, this.stage.getHeight() / 2.0F - 7.0F);
      this.name.setSize(192.0F, 32.0F);
      this.name.addListener(new InputListener() {
         public boolean keyTyped(InputEvent event, char character) {
            TextField field = (TextField)event.getListenerActor();
            if (field.getText().length() == 0) {
               field.setText(" ");
               field.setCursorPosition(1);
            }

            return super.keyTyped(event, character);
         }

         public boolean keyUp(InputEvent event, int keycode) {
            if (keycode == 66) {
               if (GuiHandler.this.level.gold >= 100 && GuiHandler.this.level.getSquadCount(false) < 10) {
                  String text = GuiHandler.this.name.getText();
                  text = text.substring(1, text.length());
                  if (!text.equals("") && !text.equals(" ")) {
                     GuiHandler.this.createNewSquad(text);
                     Resources.GUISOUNDS[1].play();
                     GuiHandler.this.hideBase();
                  } else {
                     Resources.GUISOUNDS[2].play();
                  }
               } else {
                  Resources.GUISOUNDS[2].play();
               }
            }

            return super.keyUp(event, keycode);
         }
      });
      this.stage.addActor(this.name);
   }

   private void setCMenu(int width, int height) {
      this.stage = new Stage((float)width, (float)height, true);
      this.stage.clear();
      TextureRegion bg = new TextureRegion(Resources.GUI);
      bg.setRegion(0, 48, 55, 31);
      Image image = new Image(new TextureRegionDrawable(bg));
      image.setSize(440.0F, 248.0F);
      float x = (float)(width / 2 - 220);
      float y = (float)(height / 2 - 124);
      image.setPosition(x, y);
      this.stage.addActor(image);
      Rectangle[] icons = new Rectangle[4];

      for(int i = 0; i < 4; ++i) {
         icons[i] = new Rectangle(x + (float)(i * 96) + 32.0F, y + 248.0F - 96.0F, 64.0F, 64.0F);
      }

      ImageButton.ImageButtonStyle imgStyle = new ImageButton.ImageButtonStyle();
      imgStyle.up = Resources.BUTTONS.getDrawable("plus_off");
      imgStyle.down = Resources.BUTTONS.getDrawable("plus_on");
      imgStyle.over = Resources.BUTTONS.getDrawable("plus_on");

      final int sx;
      for(sx = 0; sx < 4; ++sx) {
         ImageButton plusButton = new ImageButton(imgStyle);
         plusButton.setSize(32.0F, 32.0F);
         plusButton.setPosition(icons[sx].x + 16.0F, icons[sx].y - 48.0F);
         plusButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               if (button == 0) {
                  GuiHandler.this.addUnit(sx);
               }

               super.touchUp(event, x, y, pointer, button);
            }
         });
         this.stage.addActor(plusButton);
      }

      for(sx = 0; sx < 2; ++sx) {
         for(int sy = 0; sy < 2; ++sy) {
            TextureRegion r = new TextureRegion(Resources.GUI);
            r.setRegion(64 + sx * 8, 112 + sy * 8, 8, 8);
            Image img = new Image(new TextureRegionDrawable(r));
            img.setSize(64.0F, 64.0F);
            Rectangle rect = icons[sx + sy * 2];
            img.setPosition(rect.x, rect.y);
            this.stage.addActor(img);
         }
      }

      TextButton okButton = new TextButton("Ok", this.style);
      okButton.setSize(128.0F, 48.0F);
      okButton.setPosition(x + 300.0F, y + 12.0F);
      okButton.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               Resources.GUISOUNDS[1].play();
               GuiHandler.this.hideShop();
            }

            super.touchUp(event, x, y, pointer, button);
         }
      });
      this.stage.addActor(okButton);
      Label.LabelStyle lstyle = new Label.LabelStyle();
      lstyle.font = Resources.FONT;
      Label addlabel = new Label("Add units to selected squad.", lstyle);
      addlabel.setPosition(this.stage.getWidth() / 2.0F - 220.0F + 32.0F, this.stage.getHeight() / 2.0F - 96.0F);
      this.stage.addActor(addlabel);

      for(int i = 0; i < 4; ++i) {
         Label cost = new Label("10G", lstyle);
         cost.setPosition(this.stage.getWidth() / 2.0F - 220.0F + (float)(i * 96) + 51.0F, this.stage.getHeight() / 2.0F - 40.0F);
         this.stage.addActor(cost);
      }

   }

   public void dispose() {
      if (this.stage != null) {
         this.stage.dispose();
      }

   }
}
