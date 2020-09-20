package edu.ucam.server.services;

import edu.ucam.actions.Actions;
import edu.ucam.pojos.Finca;
import edu.ucam.server.Server;

public class FincaService implements IActionService{

	private Finca finca;
	
	@Override
	public void process(Actions action, Object object, Finca f) {
		
		this.finca = (Finca) object;
		
		switch (action) {
		case ADDFINCA:
			Server.getFincas().put(finca.getId(), finca);
			break;
			
		case UPDATEFINCA:
			Server.getFincas().remove(finca.getId());
			Server.getFincas().put(finca.getId(), finca);
			break;

		default:
			break;
		}
	}

}
