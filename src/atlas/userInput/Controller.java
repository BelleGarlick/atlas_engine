package atlas.userInput;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Controller {

	private final int id;

	private float leftJoyStickHorz = 0;
	private float leftJoyStickVert = 0;
	private float rightJoyStickHorz = 0;
	private float rightJoyStickVert = 0;
	private float rightTriggerHorz = 0;
	private float rightTriggerVert = 0;

	private boolean leftButton = false;
	private boolean topButton = false;
	private boolean rightButton = false;
	private boolean bottomButton = false;
	private boolean leftDPad = false;
	private boolean topDPad = false;
	private boolean rightDPad = false;
	private boolean bottomDPad = false;
	private boolean leftTrigger = false;
	private boolean rightTrigger = false;
	private boolean optionsButton = false;
	private boolean startButton = false;
	private boolean leftJoyStickButton = false;
	private boolean rightJoyStickButton = false;
	
	public Controller(int id, String glfwGetJoystickName) {
		this.id = id;
	}

	public int id() {
		return id;
	}
	
	public void setAxis(FloatBuffer axis) {
		leftJoyStickHorz = axis.get(0);
		leftJoyStickVert = axis.get(1);
		rightJoyStickHorz = axis.get(2);
		rightJoyStickVert = axis.get(3);
		rightTriggerHorz = axis.get(4);
		rightTriggerVert = axis.get(5);
	}
	public void setButtons(ByteBuffer buttons) {
		bottomButton = buttons.get(0) == 1 ? true : false;
		rightButton = buttons.get(1) == 1 ? true : false;
		leftButton = buttons.get(2) == 1 ? true : false;
		topButton = buttons.get(3) == 1 ? true : false;
		leftTrigger = buttons.get(4) == 1 ? true : false;
		rightTrigger = buttons.get(5) == 1 ? true : false;
		optionsButton = buttons.get(6) == 1 ? true : false;
		startButton = buttons.get(7) == 1 ? true : false;
		leftJoyStickButton = buttons.get(8) == 1 ? true : false;
		rightJoyStickButton = buttons.get(9) == 1 ? true : false;
		topDPad = buttons.get(10) == 1 ? true : false;
		rightDPad = buttons.get(11) == 1 ? true : false;
		bottomDPad = buttons.get(12) == 1 ? true : false;
		leftDPad = buttons.get(13) == 1 ? true : false;
	}


	public float getLeftJoyStickHorz() {
		return leftJoyStickHorz;
	}

	public float getLeftJoyStickVert() {
		return leftJoyStickVert;
	}

	public float getRightJoyStickHorz() {
		return rightJoyStickHorz;
	}

	public float getRightJoyStickVert() {
		return rightJoyStickVert;
	}

	public float getRightTriggerHorz() {
		return rightTriggerHorz;
	}

	public float getRightTriggerVert() {
		return rightTriggerVert;
	}


	public boolean isLeftButtonPressed() {
		return leftButton;
	}

	public boolean isTopButtonPressed() {
		return topButton;
	}

	public boolean isRightButtonPressed() {
		return rightButton;
	}

	public boolean isBottomButtonPressed() {
		return bottomButton;
	}

	public boolean isLeftDPadPressed() {
		return leftDPad;
	}

	public boolean isTopDPadPressed() {
		return topDPad;
	}

	public boolean isRightDPadPressed() {
		return rightDPad;
	}

	public boolean isBottomDPadPressed() {
		return bottomDPad;
	}

	public boolean isLeftTriggerPressed() {
		return leftTrigger;
	}

	public boolean isRightTriggerPressed() {
		return rightTrigger;
	}

	public boolean isOptionsButtonPressed() {
		return optionsButton;
	}

	public boolean isStartButtonPressed() {
		return startButton;
	}

	public boolean isLeftJoyStickButtonPressed() {
		return leftJoyStickButton;
	}

	public boolean isRightJoyStickButtonPressed() {
		return rightJoyStickButton;
	}
	
}
