package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Light;

public class OpenRoomWithObject extends OpenRoom {
	
	public OpenRoomWithObject(Body body, double lightPos) {
		super();
		bodies.add(body);
		lights.add(Light.pc(Vec3.xyz(0, lightPos, 0), Color.code(0xffff00)));
		
	}

}
