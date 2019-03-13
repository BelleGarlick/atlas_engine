package atlas.objects.entityComponents.animation;

import java.util.Map;
import java.util.Optional;

import atlas.objects.entityComponents.Mesh;

public class AnimatedModel {
	 private Map<String, Animation> animations;

	    private Animation currentAnimation;
	    
	    public Mesh[] meshes;

	    public AnimatedModel(Mesh[] meshes, Map<String, Animation> animations) {
	        this.meshes = meshes;
	        this.animations = animations;
	        Optional<Map.Entry<String, Animation>> entry = animations.entrySet().stream().findFirst();
	        currentAnimation = entry.isPresent() ? entry.get().getValue() : null;
	    }

	    public Animation getAnimation(String name) {
	        return animations.get(name);
	    }

	    public Animation getCurrentAnimation() {
	        return currentAnimation;
	    }
	    public void update(float i) {
	    	this.getCurrentAnimation().update(i);
	    }

	    public void setCurrentAnimation(Animation currentAnimation) {
	        this.currentAnimation = currentAnimation;
	    }
	    
	    public void cleanUp(){
	    	for (Mesh m : meshes){
	    		m.cleanUp();
	    	}
	    }

		public void restartAnimation() {
			this.getCurrentAnimation().currentFrameTime = 0;
		}
}