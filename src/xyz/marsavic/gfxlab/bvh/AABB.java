package xyz.marsavic.gfxlab.bvh;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Affine;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public class AABB {
	private Vec3 p1;
	private Vec3 p2;
	
	private AABB(Vec3 p1, Vec3 p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	

	public Vec3 getP1() {
		return p1;
	}

	public Vec3 getP2() {
		return p2;
	}

	public double height() {
		return p2.x() - p1.x();
	}
	
	public double width() {
		return p2.y() - p1.y();
	}
	
	public double length() {
		return p2.z() - p2.z();
	}
	
	public static AABB empty() {
		return new AABB(Vec3.ZERO, Vec3.ZERO);
	}
	
	public static AABB box(Vec3 p, Vec3 q) {
		return new AABB(
				Vec3.xyz(Math.min(p.x(), q.x()), Math.min(p.y(), q.y()), Math.min(p.z(), q.z())),
				Vec3.xyz(Math.max(p.x(), q.x()), Math.max(p.y(), q.y()), Math.max(p.z(), q.z()))
		);
	}
	
	
	public AABB transform(Affine t) {
		return new AABB(t.applyTo(p1), t.applyTo(p2));
	}
	
	public boolean isEmpty() {
		return p2.sub(p1).equals(Vec3.ZERO);
	}
	
	public AABB union(AABB other) {
		if (this.isEmpty()) return new AABB(other.p1, other.p2);
		double minx = Math.min(Math.min(p1.x(), p2.x()), Math.min(other.p1.x(), other.p2.x()));
		double miny = Math.min(Math.min(p1.y(), p2.y()), Math.min(other.p1.y(), other.p2.y()));
		double minz = Math.min(Math.min(p1.z(), p2.z()), Math.min(other.p1.z(), other.p2.z()));
		
		double maxx = Math.max(Math.max(p1.x(), p2.x()), Math.max(other.p1.x(), other.p2.x()));
		double maxy = Math.max(Math.max(p1.y(), p2.y()), Math.max(other.p1.y(), other.p2.y()));
		double maxz = Math.max(Math.max(p1.z(), p2.z()), Math.max(other.p1.z(), other.p2.z()));

		return new AABB(Vec3.xyz(minx, miny, minz), Vec3.xyz(maxx, maxy, maxz));
	}
	
	public AABB intersection(AABB other) {
		if (this.isEmpty()) return AABB.empty();
		double min1x = p1.x() < p2.x() ?  p1.x() : p2.x();
		double min1y = p1.y() < p2.y() ?  p1.y() : p2.y();
		double min1z = p1.z() < p2.z() ?  p1.z() : p2.z();
		
		double min2x = other.p1.x() < other.p2.x() ?  other.p1.x() : other.p2.x();
		double min2y = other.p1.y() < other.p2.y() ?  other.p1.y() : other.p2.y();
		double min2z = other.p1.z() < other.p2.z() ?  other.p1.z() : other.p2.z();

		double max1x = p1.x() > p2.x() ?  p1.x() : p2.x();
		double max1y = p1.y() > p2.y() ?  p1.y() : p2.y();
		double max1z = p1.z() > p2.z() ?  p1.z() : p2.z();
		
		double max2x = other.p1.x() > other.p2.x() ?  other.p1.x() : other.p2.x();
		double max2y = other.p1.y() > other.p2.y() ?  other.p1.y() : other.p2.y();
		double max2z = other.p1.z() > other.p2.z() ?  other.p1.z() : other.p2.z();
		
		double minx = Math.max(min1x, min2x);
		double miny = Math.max(min1y, min2y);
		double minz = Math.max(min1z, min2z);
		
		double maxx = Math.min(max1x, max2x);
		double maxy = Math.min(max1y, max2y);
		double maxz = Math.min(max1z, max2z);
		
		if (minx > maxx || miny > maxy || minz > maxz) return AABB.empty();
		else return new AABB(Vec3.xyz(minx, miny, minz), Vec3.xyz(maxx, maxy, maxz));
	}
	
	public static AABB unionAll(Collection<Body> bodies) {
		AABB result = AABB.empty();
		for (Body body : bodies) {
			result = result.union(body.solid().getAABB());
		}
		return result;
	}
	
	public Vec3 getCenter() {
		return p2.sub(p1).div(2).add(p1);
	}
	
	@Override
	public String toString() 
	{
		return "Box(" + p1.toString() + ", " + p2.toString() + ")";
	}
}
