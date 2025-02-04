package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Light;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Scene;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.random.RNG;


public class DiscoRoom extends Scene.Base {
	
	public DiscoRoom(int nBalls, int nLights, long seed) {
		addBodiesFrom(new OpenRoom());
		
		RNG rngBalls  = new RNG(2*seed  );
		RNG rngLights = new RNG(2*seed+1);
		
		for (int i = 0; i < nBalls; i++) {
			bodies.add(Body.uniform(
					Ball.cr(Vec3.random(rngBalls).ZOtoMP(), 0.1),
					new Material(Color.hsb(rngBalls.nextDouble(), 0.8, 0.9)))
			);
		}

		for (int i = 0; i < nLights; i++) {
			lights.add(Light.pc(
					Vec3.random(rngLights).ZOtoMP(),
					Color.hsb(rngLights.nextDouble(), 0.8, 0.006))
			);
		}
	}
	
}
