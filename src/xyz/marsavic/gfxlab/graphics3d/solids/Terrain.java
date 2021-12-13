package xyz.marsavic.gfxlab.graphics3d.solids;

import java.util.ArrayList;

import org.checkerframework.checker.units.qual.min;
import org.checkerframework.common.value.qual.ArrayLen;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Body;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

public class Terrain implements Solid {

	private double width, length, height;
	private int precision;
	private TerrainDefinition f;
	private ArrayList<Simplex> triangles; 
	private Vec3 pos;
	public Terrain(double width, double length, double height, int precision, Vec3 pos, TerrainDefinition def) {
		this.width = width;
		this.height = height;
		this.precision = precision;
		f = def;
		this.pos = pos.sub(Vec3.xyz(width/2, length/2, 0));
		triangles = new ArrayList<>();
		double dx = width / precision; 
		double dy = length / precision; 
		double di = 2.0 / precision; 
		for (int i = 0; i<precision; i++) {
			for (int j = 0; j<precision; j++) {
				double x = pos.x() + i*dx; 
				double y = pos.z() +  j*dy;
				double xi = 2*(x - pos.x()) / width - 1;
				double yi = 2*(y - pos.y()) / length - 1;
				triangles.add(
							new Simplex(
									Vec3.xyz(x, pos.y() + height * f.get(xi,yi), y), 
									Vec3.xyz(x,pos.y() + height * f.get(xi, yi+di), y+dy), 
									Vec3.xyz(x+dx, pos.y() + height * f.get(xi+di, yi+di), y+dy))
						);
				triangles.add(
						new Simplex(
								Vec3.xyz(x+dx, pos.y() + height * f.get(xi+di,yi+di), y+dy), 
								Vec3.xyz(x+dx,pos.y() + height * f.get(xi+di, yi), y), 
								Vec3.xyz(x, pos.y() + height * f.get(xi, yi), y))
					);
			}
		}
	}
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		Hit minHit = null;
		for (Simplex trig : triangles) {
			Hit hit = trig.firstHit(ray, afterTime);
			if (hit != null) {
				if (minHit == null) minHit = hit; 
				else {
					if (minHit.t() > hit.t()) minHit = hit;
				}
			} 
		}
		return minHit;
	}

	public interface TerrainDefinition {
		double get(double x, double y);
	}

}
