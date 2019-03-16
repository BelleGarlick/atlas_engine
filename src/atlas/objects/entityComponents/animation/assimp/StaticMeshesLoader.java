package atlas.objects.entityComponents.animation.assimp;

//import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
//import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
//import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
//import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiImportFileFromMemory;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
//import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
//import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
//import org.lwjgl.PointerBuffer;
//import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
//import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
//import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
//import org.lwjgl.assimp.Assimp;

//import atlas.objects.entityComponents.Material;
import atlas.objects.entityComponents.Mesh;
import atlas.utils.Loader;
import atlas.utils.Utils;

public class StaticMeshesLoader {

    public static Mesh load(String resourcePath) throws Exception {
        return load(resourcePath, 
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
                        | aiProcess_FixInfacingNormals);
    }
    
    public static Mesh load(String resourcePath, int flags) throws Exception {
    	ByteBuffer resourceByteBuffer = Loader.ioResourceToByteBuffer(Loader.class.getClassLoader(), resourcePath,1024);
    	ByteBuffer argsBuffer = BufferUtils.createByteBuffer(8);
        AIScene aiScene = aiImportFileFromMemory(resourceByteBuffer, flags, argsBuffer);
        if (aiScene == null) {
            throw new Exception("Error loading model");
        }

//        int numMaterials = aiScene.mNumMaterials();
//        PointerBuffer aiMaterials = aiScene.mMaterials();
//        List<Material> materials = new ArrayList<>();
//        for (int i = 0; i < numMaterials; i++) {
//            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
////            processMaterial(aiMaterial, materials, texturesDir);
//        }

//        int numMeshes = aiScene.mNumMeshes();
////        PointerBuffer aiMeshes = aiScene.mMeshes();
//        Mesh[] meshes = new Mesh[numMeshes];
//        for (int i = 0; i < numMeshes; i++) {
////            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
//
//        	System.out.println("Static Meshes Loader: Uncomment");
//            Mesh mesh = processMesh(aiMesh, materials);
////            meshes[i] = mesh;
//        }
        PointerBuffer aiMeshes = aiScene.mMeshes();
        AIMesh aiMesh = AIMesh.create(aiMeshes.get(0));
        Mesh mesh = processMesh(aiMesh);

        return mesh;
    }

    protected static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }

    private static Mesh processMesh(AIMesh aiMesh) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);

        Mesh mesh = new Mesh(Utils.listToArray(vertices), Utils.listToArray(textures),
                Utils.listToArray(normals), Utils.listIntToArray(indices));

        return mesh;
    }

    protected static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    protected static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    protected static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }
}
