package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;

public interface RenderableSorter {
   void sort(Camera var1, Array<Renderable> var2);
}
