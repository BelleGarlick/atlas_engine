package atlas.utils;

import atlas.objects.entityComponents.Mesh;

public class MeshDefaults {

	
	public static Mesh loadBox() throws Exception {
		return Loader.getMesh("defaultMeshes/box.obj");
	}
	
	public static Mesh loadPlane() throws Exception {
		return Loader.getMesh("defaultMeshes/plane.obj");
	}

	public static Mesh loadCylinder() throws Exception {
		return Loader.getMesh("defaultMeshes/cylinder.obj");
	}

	public static Mesh loadCone() throws Exception {
		return Loader.getMesh("defaultMeshes/cone.obj");
	}
	
	public static Mesh loadSphere() throws Exception {
		return Loader.getMesh("defaultMeshes/sphere.obj");
	}
	
	public static Mesh loadDragon() throws Exception {
		return Loader.getMesh("defaultMeshes/dragon.obj");
	}
	
	public static Mesh loadIsoSphere() throws Exception {
		return Loader.getMesh("defaultMeshes/isoSphere.obj");
	}
	
	public static Mesh loadMonkey() throws Exception {
		return Loader.getMesh("defaultMeshes/monkey.obj");
	}
	
	public static Mesh loadDonut() throws Exception {
		return Loader.getMesh("defaultMeshes/donut.obj");
	}
}