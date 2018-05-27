package bara;

import org.reflections.Reflections;

import bara.scenes.Scene;

public class Main {
	
	public static void main(String[] args) {
		
		Bara bara = new Bara();
		
		String sceneName = "Start";
		
		while(sceneName != null) {
			
			try {
				Scene scene = findScene(sceneName);
				sceneName = scene.play(bara);
			}catch(Exception ex) {
				System.err.println("Failed to find scene '" + sceneName + "'");
				sceneName = null;
			}
		}
	}

	private static Scene findScene(String sceneName) throws Exception{
		
		Reflections reflections = new Reflections("bara.scenes");
		for(Class<? extends Scene> c : reflections.getSubTypesOf(Scene.class)) {
			if(c.getSimpleName().equals(sceneName)) {
				return (Scene)c.getConstructors()[0].newInstance();
			}
		}
		
		return null;
	}
}