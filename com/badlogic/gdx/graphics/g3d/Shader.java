package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.Disposable;

public interface Shader extends Disposable {
   void init();

   int compareTo(Shader var1);

   boolean canRender(Renderable var1);

   void begin(Camera var1, RenderContext var2);

   void render(Renderable var1);

   void end();
}
