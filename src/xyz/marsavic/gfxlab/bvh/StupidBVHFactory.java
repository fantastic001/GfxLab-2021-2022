package xyz.marsavic.gfxlab.bvh;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import xyz.marsavic.functions.interfaces.Function1;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Collider;

public class StupidBVHFactory implements Function1<Collider, Collection<Body>>{

	BVHNode root;
	public void constructNewTree(Collection<Body> bodies) {
		root = BVHNode.leaf(bodies);
	}
	
	@Override
	public Collider at(Collection<Body> bodies) {
		return root;
	}
}
