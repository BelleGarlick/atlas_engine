package atlas.userInput;

import org.joml.Vector2d;
import org.joml.Vector2f;

import atlas.engine.Window;
import atlas.utils.Loader;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class UserInput {

    private static Window window;
    
    private static final Vector2d previousPos = new Vector2d(-1, -1);
    private static final Vector2d currentPos = new Vector2d(0, 0);
    private static final Vector2f displVec = new Vector2f();

    private static boolean inWindow = false;

    public static boolean keyHelper = false;
    private static boolean leftButtonPressed = false;
    private static boolean rightButtonPressed = false;
    private static HashMap<Integer, Boolean> activeKeys = new HashMap<>();
    private static KeyToggled keyToggleFunction = null;    
    
    private static ArrayList<Controller> controllers = new ArrayList<>();
    private static ControllerToggled controllerAddedCallback = null;
    private static ControllerToggled controllerRemovedCallback = null;
    
    private static StringBuilder textInput = new StringBuilder();

    public static void init(Window window) {
    	UserInput.window = window;
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
        glfwSetCharCallback(window.getWindowHandle(), (handle, codepoint) -> {
        	textInput.append((char) codepoint);
    	});
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window.getWindowHandle(), (windowHandle, key, scancode, action, mods) -> {
            boolean keyChanged = false;
        	if (action == GLFW_RELEASE) {
            	if (activeKeys.containsKey(key)) {
                    activeKeys.remove(key); keyChanged = true;
                    if (keyToggleFunction != null) {keyToggleFunction.handle(key, false);}
            	}
            } else {
            	if (!activeKeys.containsKey(key)) {
            		activeKeys.put(key, Boolean.TRUE); keyChanged = true;
                    if (keyToggleFunction != null) {keyToggleFunction.handle(key, true);}
            	}
            }
        	if (keyChanged && keyHelper) {
            	System.out.println(key);
            }
        });
        
        glfwSetJoystickCallback((controller,e) -> {
            if (e == GLFW_CONNECTED) {
        		Controller c = new Controller(controller, glfwGetJoystickName(controller));
        		controllers.add(c);
        		if(controllerAddedCallback!=null){controllerAddedCallback.handle(c);}
            }
            else if (e == GLFW_DISCONNECTED) {
            	Controller removed = null;
            	for (Controller c : controllers) {
            		if (c.id() == controller) {
            			removed = c;
            		}
            	}
        		if(controllerRemovedCallback!=null){controllerRemovedCallback.handle(removed);}
            	controllers.remove(removed);
            }
        });
        
        
        //Controllers
        for (int gc = GLFW_JOYSTICK_1; gc < GLFW_JOYSTICK_LAST; gc++) { 
        	if (glfwJoystickPresent(gc)) {
        		Controller c = new Controller(gc, glfwGetJoystickName(gc));
        		controllers.add(c);
        	}
        }
        
    }
    
    public static void updateControllers() {
    	//check if any have been removed or added
    	for (Controller c : controllers) {
    		c.setAxis(glfwGetJoystickAxes(c.id()));
    		c.setButtons(glfwGetJoystickButtons(c.id()));
    	}
    }

    public static Vector2f getDisplVec() {return displVec;}
    
    public static void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        if (inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.x = (float) deltax;
            }
            if (rotateY) {
                displVec.y = (float) deltay;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }
    
    private static long cursor = -1;
    public static void createCustomCursor(String imageLocation, Vector2f hotSpot) throws Exception { 
    	PNGDecoder decoder = new PNGDecoder(Loader.getStream(Loader.class.getClassLoader(), imageLocation));

        int width = decoder.getWidth();
        int height = decoder.getHeight();

        // Load texture contents into a byte buffer
        ByteBuffer buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
        buf.flip();
    	
        GLFWImage cursorImg = GLFWImage.create();
        cursorImg.width(width);
        cursorImg.height(height);
        cursorImg.pixels(buf); 
        
        if (cursor != -1) { 
        	clearCustomCursor();
        }
    	cursor = glfwCreateCursor(cursorImg, Math.round(hotSpot.x), Math.round(hotSpot.y));
        glfwSetCursor(window.getWindowHandle(), cursor);
    }
    public static void clearCustomCursor() { 
    	glfwDestroyCursor(cursor);
    	cursor = -1;
    }
    
    public static boolean keyDown(int key) {return activeKeys.containsKey(key);}
    public static String getKeyName(int key) {return GLFW.glfwGetKeyName(key, 0);}

    public static boolean isLeftButtonPressed() {return leftButtonPressed;}
    public static boolean isRightButtonPressed() {return rightButtonPressed;}
    
    public static ArrayList<Controller> getControllers() {return controllers;}
    public static void setControllerAdded(ControllerToggled ct) {controllerAddedCallback = ct;}
    public static void setControllerRemoved(ControllerToggled ct) {controllerRemovedCallback = ct;}
    
    public static void clearTextBuffer() {textInput = new StringBuilder();}
    public static String getTextInput() {return textInput.toString();}
    
    public static void showCursor(){glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);}
    public static void hideCursor(){glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);}
    public static void disableCursor(){
    	glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}

    public static void setKeyToggle(KeyToggled kt) {keyToggleFunction = kt;}
    public static void clearKeyToggle() {keyToggleFunction = null;}

	public static interface KeyToggled {
		public void handle(int key, boolean active);
	}
	public static interface ControllerToggled {
		public void handle(Controller c);
	}
}
