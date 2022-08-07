package xyz.marsavic.gfxlab.bvh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.checkerframework.checker.signature.qual.Identifier;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Collider;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Collider.BruteForce;
import xyz.marsavic.utils.Numeric;

public class BVHNode implements Collider {

	private AABB box;
	private BVHNode left; 
	private BVHNode right; 
	private Collection<Body> bodies;
	Collider bfCollider;
	
	private BVHNode(AABB box, Collection<Body> bodies, BVHNode left, BVHNode right) { 
		this.left = left; 
		this.right = right;
		this.box = box; 
		this.bodies = bodies;
		if (isLeaf()) 
		{
			bfCollider = new Collider.BruteForce(bodies);
		}
	}
	
	public Collection<Body> getBodies() 
	{
		if (bodies == null) 
		{
			bodies = new LinkedList<>();
			for (Body body : left.getBodies()) {
				bodies.add(body);
			}
			for (Body body : right.getBodies()) {
				bodies.add(body);
			}
			return bodies;
		}
		else return bodies;
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
		if (bfCollider != null) {
			return bfCollider.collide(ray);
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
				Collision rightCollision = right.collide(ray);
				if (rightCollision == null) return leftCollision;
				else if (leftCollision == null) return rightCollision;
				else {
					if (rightCollision.hit().t() < leftCollision.hit().t())
						return rightCollision;
					else return leftCollision;
				}
			}
			return null;
		}
	}
	
	public void addBody(Body body) {
		if (bodies == null) // we are not in leaf 
		{
			if (left.getBox().intersection(body.solid().getAABB()).isEmpty()) 
			{
				// we add it to right and enlarge box 
				right.addBody(body);
				box = left.getBox().union(right.getBox());
			}
			else if (right.getBox().intersection(body.solid().getAABB()).isEmpty()) 
			{
				// we add it to left 
				left.addBody(body);
				box = left.getBox().union(right.getBox()); 
			}
			else {
				bodies = getBodies();
				bodies.add(body);
				box = AABB.unionAll(bodies);
				left = null; 
				right = null;
			}
		}
	}

	public void print() {
		System.out.println("__________");
		this.printChild(0);
		
	}
	private void printChild(int tabs) 
	{
		
		String identation = "";
		for (int i  = 0; i<tabs; i++) 
		{
			identation += "  ";
		}
		System.out.println(identation + "node" + this.getBox().toString());
		if (isLeaf()) 
		{
			for (Body body : bodies) 
			{
				
				System.out.println(identation + "|-" + body.show());
			}
		}
		else 
		{
			if (left != null) left.printChild(tabs+1);
			if (right != null) right.printChild(tabs+1);
		}
	}
	
	
	
	
}
