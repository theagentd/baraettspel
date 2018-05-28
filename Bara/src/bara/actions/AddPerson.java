package bara.actions;

import bara.SceneManager;
import bara.character.PersonImpl;

public class AddPerson extends InstantAction{
	
	private PersonImpl person;
	
	public AddPerson(PersonImpl person) {
		this.person = person;
	}

	@Override
	public void perform(SceneManager sceneManager) {
		sceneManager.addPerson(person);
	}
}