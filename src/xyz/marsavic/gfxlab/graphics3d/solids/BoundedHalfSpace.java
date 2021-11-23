package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public class BoundedHalfSpace implements Solid {

	private Vec3 p1,p2,p3;
	public BoundedHalfSpace(Vec3 p1, Vec3 p2, Vec3 p3) {
		this.p1 = p1; 
		this.p2 = p2; 
		this.p3 = p3; 
	}
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		Hit result = HalfSpace.pn(
				p1, 
				p2.sub(p1).cross(p3.sub(p1))
			).firstHit(ray, afterTime);
		if (result == null) return null;
		Vec3 p = ray.at(result.t());
		Vec3 t = p2.sub(p1).cross(p3.sub(p1)).cross(p2.sub(p1));
		if (
				p.sub(p1).dot(p2.sub(p1)) < p3.sub(p1).dot(p2.sub(p1)) &&
				p.sub(p1).dot(p2.sub(p1)) >= 0 &&
				p.sub(p1).dot(t) >= 0 &&
				p.sub(p1).dot(t) < p3.sub(p1).dot(t)
				) {
			return result;
		}
		else return result;
		
	}
	
}
