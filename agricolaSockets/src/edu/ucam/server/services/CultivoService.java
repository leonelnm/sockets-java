package edu.ucam.server.services;

import edu.ucam.actions.Actions;
import edu.ucam.pojos.Cultivo;
import edu.ucam.pojos.Finca;
import edu.ucam.server.Server;

public class CultivoService implements IActionService {
	
	private Cultivo cultivo;

	@Override
	public void process(Actions action, Object object, Finca finca) {
		
		cultivo = (Cultivo) object;
		
		switch (action) {
		case ADDCULTIVO:
			Server.getFincas().get(finca.getId()).addCultivo(cultivo);
			break;
			
		default:
			break;
		}

	}

}
