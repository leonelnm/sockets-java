package edu.ucam.server.services;

import edu.ucam.actions.Actions;
import edu.ucam.pojos.Finca;

public interface IActionService {
	
	public void process(Actions action, Object object, Finca finca);

}
