package xyz.marsavic.gfxlab.bvh;

import java.util.ArrayList;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public class SAHBin {
	private Vec3 start; 
	private Vec3 end;
	private ArrayList<Body> solids;
	public SAHBin(Vec3 start, Vec3 end) {
		this.start = start; 
		this.end = end;
		this.solids = new ArrayList<>();
	}
	
	public boolean isPartOf(AABB box) {
		double x = end.sub(start).dot(box.getCenter().sub(start)); 
		double y = end.sub(start).dot(box.getCenter().sub(start));
		return x >= 0 && y <= end.sub(start).dot(end.sub(start));
		
	}
	public void addBox(Body solid) {
		if (this.isPartOf(solid.solid().getAABB())) {
			this.solids.add(solid);
		}
	}
	
	public double getArea() {
		return end.sub(start).lengthSquared();
	}
	
	public int getCount() {
		return solids.size();
	}
	
	public static SAHBin generateFromSolids(Vec3 start, Vec3 end, ArrayList<Body> solids) {
		SAHBin result = new SAHBin(start, end);
		for (Body solid : solids) {
			result.addBox(solid);
		}
		return result;
	}
	
	public ArrayList<Body> getsolids() {
		return this.solids;
	}
	
	

}
