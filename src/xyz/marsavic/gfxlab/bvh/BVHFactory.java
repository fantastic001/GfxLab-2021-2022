package xyz.marsavic.gfxlab.bvh;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import xyz.marsavic.functions.interfaces.Function1;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Collider;
import xyz.marsavic.gfxlab.graphics3d.Collider.BruteForce;
import xyz.marsavic.gfxlab.graphics3d.Collider.Collision;
import xyz.marsavic.gfxlab.graphics3d.Ray;

public class BVHFactory implements Function1<Collider, Collection<Body>>{

	BVHNode root;
	double[] vector;
	double thresholdForReconstruction = 0.01;
	public void constructNewTree(Collection<Body> bodies) {
		ArrayList<Body> mybodies = new ArrayList();
		for (Body body : bodies) {
			mybodies.add(body);
		}
		SAH sah = new SAH(mybodies);
		root = sah.getNode(32, 2.0, 10);
		System.out.println("Printing resulting node");
		root.print();

	}
	
	@Override
	public Collider at(Collection<Body> bodies) {
		if (shouldReconstruct(bodies)) 
		{
			constructNewTree(bodies);
		}
		else 
		{
			addBodiesNotPresent(bodies);
		}
		return root;
	}
	
	private void addBodiesNotPresent(Collection<Body> bodies) {
		for (Body body : bodies) 
		{
			if (!root.getBodies().contains(body)) 
			{
				root.addBody(body);
			}
		}
	}

	public boolean shouldReconstruct(Collection<Body> bodies) {
		if (this.vector == null) 
		{
			this.vector = this.computeVector(bodies);
			return true; 
		}
		else 
		{
			double[] result = computeVector(bodies);
			if (computeDistance(vector, result) < thresholdForReconstruction) 
			{
				this.vector = result;
				return false;
			}
			else 
			{
				this.vector = result;
				return true;
			}
		}
	}
	
	private double[] computeVector(Collection<Body> bodies) 
	{
		int k = 1024;
		double R = 1000;
		double[] result = new double[k];
		Collider col = new Collider.BruteForce(bodies);
		for (int i = 0; i<k; i++)  
		{
			Collision c = col.collide(Ray.pq(Vec3.xyz(R*Math.cos(i*2*Math.PI/k), 1000, R*Math.sin(i*2*Math.PI/k)), Vec3.ZERO));
			if (c == null) result[i] = -100000;
			else if (c.hit() == null) result[i] = -100000;
			else result[i] = c.hit().t();
		}
		return result;
	}
	
	private double computeDistance(double[] a, double[] b) 
	{
		int L = Math.max(a.length, b.length);
		double dist = 0;
		for (int i = 0; i<L; i++) 
		{
			if (i < a.length && i < b.length) dist += (a[i] - b[i])*(a[i] - b[i]);
			else if (i < a.length) dist += a[i]*a[i];
			else if (i < b.length) dist += b[i]*b[i];
		}
		return dist;
	}
}
