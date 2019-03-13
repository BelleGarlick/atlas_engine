package atlas.objects.entityComponents.animation;

import java.util.List;

import org.joml.Matrix4f;

public class Animation {

    private int currentFrame;

    private List<AnimatedFrame> frames;
    
    private Matrix4f[] jointMatrix;

    private String name;

    double currentFrameTime = 0;
    private double frameDuration = 0;
    private double duration;

    public Animation(String name, List<AnimatedFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        currentFrame = 0;
        this.duration = duration;
    	frameDuration = this.duration/this.frames.size();
    }

    public Matrix4f[] getJointMatricies(){
    	return this.jointMatrix;
    }

    public double getDuration() {
        return this.duration;        
    }
    
    public List<AnimatedFrame> getFrames() {
        return frames;
    }

    public String getName() {
        return name;
    }

    private int nextFrameIndex() {
    	int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
        	nextFrame = 0;
        }
        return nextFrame;
    }
    
    public void update(float interval){
    	currentFrameTime += interval * 1;
    	if (currentFrameTime >= frameDuration){
        	currentFrameTime -= frameDuration;
        	this.currentFrame = nextFrameIndex();
    	}
    	updateJointsMatrix();
    }
    
    private void updateJointsMatrix(){
    	jointMatrix = calculateCurrentAnimationPose();
    }
    
    private Matrix4f[] calculateCurrentAnimationPose(){
    	AnimatedFrame[] frames = new AnimatedFrame[2];
    	frames[0] = this.frames.get(this.currentFrame);
    	frames[1] = this.frames.get(this.nextFrameIndex());
		return interpolatePoses(frames[0], frames[1], (float)(currentFrameTime/this.frameDuration));
    }
    
	private Matrix4f[] interpolatePoses(AnimatedFrame previousFrame, AnimatedFrame nextFrame, float progression) {
		Matrix4f[] currentPose = new Matrix4f[150];
		int count = 0;
		for (Matrix4f jointMatrix : previousFrame.getJointMatrices()) {
			Matrix4f previousTransform = jointMatrix;
			Matrix4f nextTransform = nextFrame.getJointMatrices()[count];
			Matrix4f interpolatedMatrix = interpolateMatrix(previousTransform,nextTransform,progression);
			currentPose[count] = interpolatedMatrix;
			count++;
		}
		return currentPose;
	}
	
	private Matrix4f interpolateMatrix(Matrix4f firstPose, Matrix4f secondPose, float progression){
		Matrix4f r0 = new Matrix4f(firstPose);
		Matrix4f r1 = new Matrix4f(secondPose);
		return r0.add(multiply(r1.sub(r0),progression));
	}
	
	private static Matrix4f multiply(Matrix4f a, float f){
		Matrix4f m = new Matrix4f(a);
		m.m00(m.m00()*f);
		m.m01(m.m01()*f);
		m.m02(m.m02()*f);
		m.m03(m.m03()*f);
		m.m10(m.m10()*f);
		m.m11(m.m11()*f);
		m.m12(m.m12()*f);
		m.m13(m.m13()*f);
		m.m20(m.m20()*f);
		m.m21(m.m21()*f);
		m.m22(m.m22()*f);
		m.m23(m.m23()*f);
		m.m30(m.m30()*f);
		m.m31(m.m31()*f);
		m.m32(m.m32()*f);
		m.m33(m.m33()*f);
		return m;
	}

}
