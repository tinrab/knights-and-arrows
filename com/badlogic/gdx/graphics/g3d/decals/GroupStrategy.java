package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

public interface GroupStrategy {
   ShaderProgram getGroupShader(int var1);

   int decideGroup(Decal var1);

   void beforeGroup(int var1, Array<Decal> var2);

   void afterGroup(int var1);

   void beforeGroups();

   void afterGroups();
}
