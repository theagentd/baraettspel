package bara;

public class Util {
	
	public static float smoothmix(float a, float b, float t) {
		t = t * t * (3.0f - 2.0f * t);
		
		return mix(a, b, t);
	}

	public static float mix(float a, float b, float t) {
		return a + (b - a) * t;
	}
}