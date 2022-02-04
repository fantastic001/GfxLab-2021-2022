package xyz.marsavic.gfxlab.bvh;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Collider;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.utils.Numeric;

public class BVHNode implements Collider {

	private AABB box;
	private BVHNode left; 
	private BVHNode right; 
	private Collection<Body> bodies;
	
	private BVHNode(AABB box, Collection<Body> bodies, BVHNode left, BVHNode right) { 
		this.left = left; 
		this.right = right;
		this.box = box; 
		this.bodies = bodies; 	
	}
	
	
	
	public AABB getBox() {
		return box;
	}

	public BVHNode getLeft() {
		return left;
	}

	public BVHNode getRight() {
		return right;
	}

	public static BVHNode leaf(Collection<Body> bodies) {
		return new BVHNode(AABB.unionAll(bodies), bodies, null, null);
	}
	
	public static BVHNode cover(BVHNode left, BVHNode right) {
		return new BVHNode(left.box.union(right.box), null, left, right);
	}
	
	public boolean isLeaf() {
		return this.right == null && this.left == null;
	}

	@Override
	public Collision collide(Ray ray) {
		if (isLeaf()) {
			return new Collider.BruteForce(bodies).collide(ray);
		}
		else {
			Vec3 tP = box.getP1().sub(ray.p()).div(ray.d());
			Vec3 tQ = box.getP2().sub(ray.p()).div(ray.d());
			
			Vec3 t0 = Vec3.min(tP, tQ);
			Vec3 t1 = Vec3.max(tP, tQ);
			
			int iMax0 = t0.maxIndex();
			int iMin1 = t1.minIndex();
			
			double max0 = t0.get(iMax0);
			double min1 = t1.get(iMin1);
			
			if (max0 < min1) {
				Collision leftCollision = left.collide(ray);
				if (leftCollision != null) return leftCollision;
				else return right.collide(ray);
			}
			return null;
		}
	}
	
	
	
	
}
