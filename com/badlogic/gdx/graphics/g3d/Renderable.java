package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.math.Matrix4;

public class Renderable {
   public final Matrix4 worldTransform = new Matrix4();
   public Mesh mesh;
   public int meshPartOffset;
   public int meshPartSize;
   public int primitiveType;
   public Material material;
   public Matrix4[] bones;
   public Lights lights;
   public Shader shader;
   public Object userData;
}
