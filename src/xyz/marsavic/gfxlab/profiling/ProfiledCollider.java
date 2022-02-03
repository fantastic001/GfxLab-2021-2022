package xyz.marsavic.gfxlab.profiling;

import xyz.marsavic.gfxlab.graphics3d.Collider;
import xyz.marsavic.gfxlab.graphics3d.Ray;



public class ProfiledCollider implements Collider {
	private Collider collider;
	private StopWatch stopWatch;
	
	public ProfiledCollider(Collider collider, StopWatch stopWatch) {
		this.collider = collider;
		this.stopWatch = stopWatch;
	}

	@Override
	public Collision collide(Ray r) {
		this.stopWatch.start();
		Collision collision = this.collider.collide(r);
		this.stopWatch.take();
		return collision;
	}
	
	
}
