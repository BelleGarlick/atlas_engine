package atlas.utils;

import atlas.objects.entityComponents.Mesh;

public class MeshDefaults {

	
	public static Mesh loadBox() throws Exception {
		return Loader.getMesh(Mesh.class.getClassLoader(), "defaultMeshes/box.obj");
	}
	
	public static Mesh loadPlane() throws Exception {
		return Loader.getMesh(Mesh.class.getClassLoader(), "defaultMeshes/plane.obj");
	}
	
	public static Mesh loadCylinder() throws Exception {
		return Loader.getMesh(Mesh.class.getClassLoader(), "defaultMeshes/cylinder.obj");
	}
	
	public static Mesh loadSphere() throws Exception {
		return Loader.getMesh(Mesh.class.getClassLoader(), "defaultMeshes/sphere.obj");
	}
	
	public static Mesh loadIsoSphere() throws Exception {
		return Loader.getMesh(Mesh.class.getClassLoader(), "defaultMeshes/isoSphere.obj");
	}
}