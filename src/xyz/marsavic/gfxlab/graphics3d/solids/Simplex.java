package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.tuples.Tuple2;

public class Simplex implements Solid {

	private Vec3 p1,p2,p3;
	private Vec3 n;
	private Vec3 edge1 ,edge2, edge3;
	public Simplex(Vec3 p1, Vec3 p2, Vec3 p3) {
		this.p1 = p1; 
		this.p2 = p2; 
		this.p3 = p3;
		n = p2.sub(p1).cross(p3.sub(p2));
		edge1 = p2.sub(p1);
		edge2 = p3.sub(p2);
		edge3 = p1.sub(p3);
	}
	
//	private Tuple2<Double, Double> projectToPlane(Vec3 p) {
//		return new Tuple2<Double, Double>(i.dot(p), j.dot(p));
//	}
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		Hit result = HalfSpace.pn(p1, n).firstHit(ray, afterTime);
		if (result == null) return null;
		Vec3 p = ray.at(result.t());
//		Tuple2<Double, Double> q = projectToPlane(p);
//		double x = q.p0();
//		double y = q.p1();
//		Tuple2<Double, Double> q1, q2, q3;
//		q1 = projectToPlane(p1);
//		q2 = projectToPlane(p2);
//		q3 = projectToPlane(p3);
//		double x1, x2 , x3, y1, y2, y3; 
//		x1 = q1.p0();
//		x2 = q2.p0();
//		x3 = q3.p0();
//		y1 = q1.p1();
//		y2 = q2.p1();
//		y3 = q3.p1();
//		double l1, l2, l3;
//		l1 = ((y2-y3)*(x - x3) + (x3 - x2)*(y-y3)) / ((y2-y3)*(x1-x3) + (x3-x2)*(y1-y3));
//		l2 = ((y3-y1)*(x-x3) + (x1-x3)*(y-y3))/((y2-y3)*(x1 - x3) + (x3-x2)*(y1-y3));
//		l3 = 1 - l1 - l2;
//		if (0.1 < l1 && l1 < 0.9 && 0.1 < l2 && l2 < 0.9 && 0.1 < l3 && l3 < 0.9) return result; 
//		else return null;
		
		Vec3 c1 = p.sub(p1);
		Vec3 c2 = p.sub(p2);
		Vec3 c3 = p.sub(p3);
		double k1, k2, k3;
		k1 = n.dot(edge1.cross(c1));
		k2 = n.dot(edge2.cross(c2));
		k3 = n.dot(edge3.cross(c3));
		if (
				k1 > 0 && 
				k2 > 0 && 
				k3 > 0
				) return result; 
				else return null;
	}
	
}
