package xyz.marsavic.gfxlab.playground;

import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.animation.*;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.RayTracerTest;
import xyz.marsavic.gfxlab.graphics3d.scenes.DiscoRoom;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoom;
import xyz.marsavic.gfxlab.graphics3d.scenes.OpenRoomRG;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Simplex;
import xyz.marsavic.gfxlab.graphics3d.solids.Terrain;
import xyz.marsavic.gfxlab.graphics3d.scenes.Mirrors;
import xyz.marsavic.gfxlab.tonemapping.ColorTransformForColorMatrix;
import xyz.marsavic.gfxlab.tonemapping.ToneMappingFunctionSimple;
import xyz.marsavic.objectinstruments.annotations.GadgetDouble;
import xyz.marsavic.objectinstruments.annotations.GadgetDoubleExponential;


public class GfxLab {
	
	Scene scene;
	Camera camera;
	RayTracer rayTracer;
	Animation<Matrix<Color>> animation;
	ToneMappingFunction toneMappingFunction;
	Animation<RawImage> toneMappedAnimation;
	
	
	@GadgetDouble(p = 0, q = 5)
	public double brightnessFactor = 1.0;
	
	public int nBalls = 7;
	
//	public int nLights = 8;
	
//	public int seed = 0;
	
	@GadgetDoubleExponential(p = 0x1p-16, q = 0x1p+16)
	public double preFactor = 0x1p-6;
	
	@GadgetDoubleExponential(p = 0x1p-4, q = 0x1p+4)
	public double power = 1.0;
	

	@GadgetDouble(p = 0, q = 0.5)
	public double fovAngle = 0.14;
	
	@GadgetDouble(p = -2, q = 3)
	public double triangleZ = -2;
	
//	@GadgetDoubleExponential(p = 1, q = 256)
//	public double shininess = 32;
	
	public boolean showDiffuse  = true;
	public boolean showSpecular = true;
	public boolean shadows = true;
	
	public double omicron = 0.0;
//	public double reflectivity = 0.5;
	
	public int maxDepth = 16;
	
	
	@GadgetDouble(p=-1, q=1)
	public double cameraX = 0;
	@GadgetDouble(p=-1, q=1)
	public double cameraY = 0;
	@GadgetDouble(p=-5, q=1)
	public double cameraZ = -3;
	@GadgetDouble(p=-0.2, q=0.2)
	public double alpha = 0;
	@GadgetDouble(p = -0.2, q = 0.2)
	public double phi = 0;
	
	synchronized void setup() {
		Solid mirrorSolid = new Terrain(
				1, // width
				1, // length
				0.5, //height
				2, // precision 
				Vec3.xyz(-1, 0, 0), // bottom-right position
				new Terrain.TerrainDefinition() {
					
					@Override
					public double get(double x, double y) {
						return 1-y;
					}
				}
			);
//		mirrorSolid = Ball.cr(Vec3.xyz(0.5, -0.5, -1), 0.5);
		Body mirror = Body.uniform(mirrorSolid, Material.MIRROR); 
		Body crown = Body.uniform(new Terrain(
				0.5, // width
				0.4, // length
				0.02, //height
				8, // precision 
				Vec3.xyz(-0.5, 0.2, -0.1), // position
				new Terrain.TerrainDefinition() {
					
					@Override
					public double get(double x, double y) {
						return 1 + 5*Math.sin(5*x);
					}
				}
			), Material.BLACK.diffuse(Color.rgb(0.9, 0.9, 0)).specular(Color.WHITE).shininess(64));
		scene = new OpenRoomRG()
				.add(mirror)
				.add(crown)
				.add(Body.uniform(Ball.cr(Vec3.xyz(-0.5, -0.5, -0.1), 0.5), Material.BLACK.diffuse(Color.code(0xff0000))))
			;
		
		camera = new TransformedCamera(
				Perspective.fov(fovAngle),
				Affine.IDENTITY
						.andThen(Affine.translation(Vec3.xyz(cameraX, cameraY, cameraZ)))
						.andThen(Affine.rotationAboutY(phi))
						.andThen(Affine.rotationAboutX(alpha))
		);
		
		rayTracer = new RayTracerTest(
				scene,
				Collider.BruteForce::new,
				camera,
				showDiffuse,
				showSpecular,
				shadows,
				maxDepth
		);
		
		animation =
				new RendererAggregateLastFrame(
						new AnimationColorSampling(
								Vec3.xyz(1, 640, 640),
								Transformation::toGeometric,
								rayTracer
						)
				)
		;
		
		toneMappingFunction = new ToneMappingFunctionSimple(
				new ColorTransformForColorMatrix.AutoSoft(preFactor, power)
		);
		
		toneMappedAnimation = new ToneMapperPerFrame(animation, toneMappingFunction, true);
	}
	
	
	// .......................................................................................................
	
	
	
	// This method is called when any of this class' public fields is changed.
	public synchronized void onChange() {
		if (animation instanceof Renderer renderer) {
			// Some cleanup because renderers can run their own threads.
			renderer.dispose();
		}
		
		setup();
	}
	
	// TODO Bug: not using the full cpu when the animation is AnimationColorSampling (for fast samplers).
	
	public synchronized void setAnimation(Animation<Matrix<Color>> animation) {
		toneMappedAnimation = new ToneMapperPerFrame(animation, toneMappingFunction, true);
	}
	
	
	public Animation<RawImage> toneMappedAnimation() {
		return toneMappedAnimation;
	}
	
	
	public void gc() {
		System.gc();
	}
	
}
