package xyz.marsavic.gfxlab.bvh;

import java.lang.reflect.Array;
import java.util.ArrayList;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Affine;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public class AABB {
	private Vec3 p1;
	private Vec3 p2;
	private ArrayList<Solid> solids;
	
	private AABB(ArrayList<Solid> solids, Vec3 p1, Vec3 p2) {
		this.solids = solids; 
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public ArrayList<Solid> getSolids() {
		return solids;
	}

	public void setSolids(ArrayList<Solid> solids) {
		this.solids = solids;
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
		return new AABB(null, Vec3.ZERO, Vec3.ZERO);
	}
	
	public static AABB box(Solid solid, Vec3 p, Vec3 q) {
		ArrayList<Solid> solids = new ArrayList<Solid>();
		solids.add(solid);
		return new AABB(
				solids,
				Vec3.xyz(Math.min(p.x(), q.x()), Math.min(p.y(), q.y()), Math.min(p.z(), q.z())),
				Vec3.xyz(Math.max(p.x(), q.x()), Math.max(p.y(), q.y()), Math.max(p.z(), q.z()))
		);
	}
	
	public AABB withSolid(Solid solid) {
		if (this.solids == null) {
			ArrayList<Solid> list = new ArrayList<>();
			list.add(solid);
			return solid.getAABB(); 
		}
		else {
			return this.union(solid.getAABB());
		}
	}
	
	public AABB withSolids(Solid... solids) {
		AABB result = AABB.empty();
		for (Solid solid : solids) result = result.union(solid.getAABB());
		return result;
	}
	
	public AABB transform(Affine t) {
		return new AABB(solids, t.applyTo(p1), t.applyTo(p2));
	}
	
	public AABB union(AABB other) {
		if (this.solids == null) return new AABB(other.solids, other.p1, other.p2);
		ArrayList<Solid> all = new ArrayList<>();
		for (Solid solid : this.solids) all.add(solid);
		for (Solid solid : other.solids) all.add(solid);
		double minx = Math.min(Math.min(p1.x(), p2.x()), Math.min(other.p1.x(), other.p2.x()));
		double miny = Math.min(Math.min(p1.y(), p2.y()), Math.min(other.p1.y(), other.p2.y()));
		double minz = Math.min(Math.min(p1.z(), p2.z()), Math.min(other.p1.z(), other.p2.z()));

		
		double maxx = Math.max(Math.max(p1.x(), p2.x()), Math.max(other.p1.x(), other.p2.x()));
		double maxy = Math.max(Math.max(p1.y(), p2.y()), Math.max(other.p1.y(), other.p2.y()));
		double maxz = Math.max(Math.max(p1.z(), p2.z()), Math.max(other.p1.z(), other.p2.z()));

		return new AABB(all, Vec3.xyz(minx, miny, minz), Vec3.xyz(maxx, maxy, maxz));
	}
	
	public AABB intersection(AABB other) {
		ArrayList<Solid> all = new ArrayList<>();
		for (Solid solid : solids) all.add(solid);
		for (Solid solid : other.solids) all.add(solid);
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
		else return new AABB(all, Vec3.xyz(minx, miny, minz), Vec3.xyz(maxx, maxy, maxz));
		
	}
}
