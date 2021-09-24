package com.virtualeria.eriaevents.event.behaviour.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomTestParticle extends AnimatedParticle {
  private final RenderLayer layer;

  protected CustomTestParticle(World world, double x, double y, double z, SpriteProvider sprites) {
    super((ClientWorld) world, x, y, z, sprites, 1);
    this.layer = RenderLayer.getEntityTranslucent(ElderGuardianEntityRenderer.TEXTURE);
    setSprite(sprites.getSprite(world.random));
  }

  @Override
  public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
    Vec3d vec3d = camera.getPos();
    float f = (float) (MathHelper.lerp((double) tickDelta, this.prevPosX, this.x) - vec3d.getX());
    float g = (float) (MathHelper.lerp((double) tickDelta, this.prevPosY, this.y) - vec3d.getY());
    float h = (float) (MathHelper.lerp((double) tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
    Quaternion quaternion2;
    if (this.angle == 0.0F) {
      quaternion2 = camera.getRotation();
    } else {
      quaternion2 = new Quaternion(camera.getRotation());
      float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
      quaternion2.hamiltonProduct(Vec3f.POSITIVE_Z.getRadialQuaternion(i));
    }

    Vec3f vec3f = new Vec3f(-1.0F, -1.0F, 0.0F);
    vec3f.rotate(quaternion2);
    Vec3f[] vec3fs = new Vec3f[] {new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F),
        new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
    float j = this.getSize(tickDelta);

    for (int k = 0; k < 4; ++k) {
      Vec3f vec3f2 = vec3fs[k];
      vec3f2.rotate(quaternion2);
      vec3f2.scale(j);
      vec3f2.add(f, g, h);
    }

    float l = this.getMinU();
    float m = this.getMaxU();
    float n = this.getMinV();
    float o = this.getMaxV();
    int p = this.getBrightness(tickDelta);
    vertexConsumer
        .vertex((double) vec3fs[0].getX(), (double) vec3fs[0].getY(), (double) vec3fs[0].getZ())
        .texture(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
        .light(p).next();
    vertexConsumer
        .vertex((double) vec3fs[1].getX(), (double) vec3fs[1].getY(), (double) vec3fs[1].getZ())
        .texture(m, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
        .light(p).next();
    vertexConsumer
        .vertex((double) vec3fs[2].getX(), (double) vec3fs[2].getY(), (double) vec3fs[2].getZ())
        .texture(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
        .light(p).next();
    vertexConsumer
        .vertex((double) vec3fs[3].getX(), (double) vec3fs[3].getY(), (double) vec3fs[3].getZ())
        .texture(l, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
        .light(p).next();
  }

  @Environment(EnvType.CLIENT)
  public static class Factory implements ParticleFactory<DefaultParticleType> {

    private final FabricSpriteProvider sprites;

    public Factory(FabricSpriteProvider sprites) {
      this.sprites = sprites;
    }

    @Nullable
    @Override
    public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x,
                                   double y, double z, double velocityX, double velocityY,
                                   double velocityZ) {
      return new CustomTestParticle(world, x, y, z, sprites);
    }
  }
}