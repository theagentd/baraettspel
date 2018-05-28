package bara.actions;

import bara.SceneManager;
import bara.character.PersonImpl;

public class DestroyPerson implements Action{
	
	private PersonImpl person;
	
	public DestroyPerson(PersonImpl person) {
		this.person = person;
	}

	@Override
	public boolean start(SceneManager sceneManager) {
		sceneManager.removePerson(person);
		person.destroy();
		return false;
	}

	@Override
	public boolean update(SceneManager sceneManager, float delta) {
		return false;
	}

	@Override
	public void skip(SceneManager sceneManager) {}
}