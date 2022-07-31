package xyz.marsavic.gfxlab.bvh;

import java.util.ArrayList;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.tuples.Pair;

public class SAH {

	private ArrayList<Body> solids; 
	
	
	
	public SAH(ArrayList<Body> solids) {
		this.solids = solids;
	}
	
	public BVHNode getNode(int numberOfBins, double kt, double ki) {
		if (solids.size() <= 2) {
			return BVHNode.leaf(this.solids);
		}
		AABB all = AABB.empty();
		for (Body s : solids) {
			all = all.union(s.solid().getAABB());
		}
		Vec3 line = all.height() >= all.width() && all.height() >= all.length()
			? Vec3.xyz(1, 0, 0) 
			: all.width() >= all.height() && all.width() >= all.length() ? 
					Vec3.xyz(0, 1, 0)
					: Vec3.xyz(0, 0, 1);
		Vec3 start = solids.get(0).solid().getAABB().getCenter();
		Vec3 end = solids.get(0).solid().getAABB().getCenter();
		for (Body s : solids) {
			if (s.solid().getAABB().getCenter().dot(line) < start.dot(line)) {
				start = s.solid().getAABB().getCenter();
			}
			if (s.solid().getAABB().getCenter().dot(line) > end.dot(line)) {
				end = s.solid().getAABB().getCenter();
			}
		}
		Vec3 linePart = end.sub(start).div(numberOfBins);
		SAHBin[] bins = new SAHBin[numberOfBins];
		for (int i = 0; i<numberOfBins; i++) {
			bins[i] = SAHBin.generateFromSolids(start.add(linePart.mul(i)), start.add(linePart.mul(i+1)), solids);
		}
		double area = new SAHBin(start, end).getArea();
		int left = 1;
		int leftCount = bins[0].getCount();
		double leftArea = bins[0].getArea();
		int rightCount = 0;
		double rightArea = 0;
		for (int i = 1; i<numberOfBins; i++) {
			rightCount += bins[i].getCount();
			rightArea += bins[i].getArea();
		}
		double minCost = kt + ki*(leftCount * leftArea + rightCount * rightArea) / area;
		for (int i = 1; i<numberOfBins; i++) {
			leftArea += bins[i].getArea();
			rightArea -= bins[i].getArea();
			leftCount += bins[i].getCount();
			rightCount -= bins[i].getCount();
			double cost = kt + ki*(leftCount * leftArea + rightCount * rightArea) / area;
			if (cost < minCost) {
				left = i+1;
				minCost = cost;
			}
		}
		System.out.println("Bins on left: " + left);
		ArrayList<Body> solidsLeft = new ArrayList<>();
		ArrayList<Body> solidsRight = new ArrayList<>();
		for (int i = 0; i<numberOfBins; i++) {
			for (Body s : bins[i].getsolids()) {
				if (i < left) {
					solidsLeft.add(s);
				}
				else {
					solidsRight.add(s);
				}
			}
		}
		SAH leftSAH = new SAH(solidsLeft);
		SAH rightSAH = new SAH(solidsRight);
		return BVHNode.cover(leftSAH.getNode(numberOfBins, kt, ki), rightSAH.getNode(numberOfBins, kt, ki));
	}
}
