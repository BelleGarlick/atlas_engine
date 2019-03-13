package atlas.engine;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL13;

public class Window {

    private static final float Z_NEAR = 0.2f;

    private static final float Z_FAR = 2000.f;

    private final String title;

    private boolean fullScreen = false;
    private int width;
    private int height;

    private long windowHandle;

    private boolean resized;

    private boolean vSync;

    private float yscroll;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync; 
        this.resized = false;
    }

    public Window(String title, boolean vSync) {
        this.title = title;
        this.vSync = vSync; 
        this.resized = false;
        this.fullScreen = true;
    }

    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
//        if (opts.compatibleProfile) {
//            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
//        } else {
//        }


        // Create the window
        if (this.fullScreen){
        	GLFWVidMode  vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            windowHandle = glfwCreateWindow(vidmode.width(), vidmode.height(), title, glfwGetPrimaryMonitor(), NULL);
        }else{
            windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        }
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        //Set height / width for calculating buffer window res
        IntBuffer wBuf = BufferUtils.createIntBuffer(1), hBuf = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(windowHandle, wBuf, hBuf);
        this.width = wBuf.get(0);this.height = hBuf.get(0);
        
        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });


        // Setup a scroll callback
//        glfwSetScrollCallback(windowHandle, (window, xOffset, yOffset) -> {
//            this.yscroll = (float) (yOffset * Options.GUI_SCROLL_SPEED);
//        });


        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);

        // Support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        
        glEnable(GL13.GL_MULTISAMPLE);


        
        
        
        
//        PNGDecoder dec = null;
//        try{
//        	dec = new PNGDecoder(DataLoader.load("icons/icon32.png"));
//            int width = dec.getWidth();
//            int height = dec.getHeight();
//            ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
//            dec.decode(buf, width * 4, PNGDecoder.Format.RGBA);
//            buf.flip();
//            
//            GLFWImage image = GLFWImage.malloc();
//            GLFWImage.Buffer images = GLFWImage.malloc(1);
//            image.set(width, height, buf);
//            images.put(0, image);
//
//            glfwSetWindowIcon(windowHandle, images);
//
//            images.free();
//            image.free();
//        }catch(Exception err){
//        	err.printStackTrace();
//        }
    }

    public void restoreState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public String getWindowTitle() {
        return title;
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(windowHandle, title);
    }

//    public Matrix4f updateProjectionMatrix() {
//        float aspectRatio = (float) width / (float) height;
//        return projectionMatrix.setPerspective(Options.FOV, aspectRatio, Z_NEAR, Z_FAR);
//    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public static float getFar(){
    	return Z_FAR;
    }
    
    public static float getNear(){
    	return Z_NEAR;

    }
    
    public float getYScroll(){
    	float yScroll = this.yscroll;
    	this.yscroll = 0;
    	return yScroll;
    }
}
