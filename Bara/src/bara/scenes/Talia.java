package bara.scenes;

import bara.Bara;

public class Talia implements Scene {

	@Override
	public String play(Bara bara) {
		
		bara.text("Hello boii!");
		bara.text("bajs");
		if(bara.didUserWrite("bajs")) {
			bara.text("wahh SUGOI!!!");
		}
		bara.text("ok bruhh");
		return "Start";
	}
}
