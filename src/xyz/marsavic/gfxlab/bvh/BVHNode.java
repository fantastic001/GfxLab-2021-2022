package xyz.marsavic.gfxlab.bvh;

public class BVHNode {

	private AABB box;
	private BVHNode left; 
	private BVHNode right; 
	
	private BVHNode(AABB box, BVHNode left, BVHNode right) {
		this.box = box; 
		this.left = left; 
		this.right = right; 
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

	public static BVHNode leaf(AABB box) {
		return new BVHNode(box, null, null);
	}
	public boolean isLeaf() {
		return this.right == null && this.left == null;
	}
	
	
	
	
}
