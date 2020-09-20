package edu.ucam.actions;

import edu.ucam.server.message.Message;

public class ActionsHelper {
	
	// LOGIN
	public static boolean isLoginAction(Message message) {
		
		for (LoginActions item : LoginActions.values()) {
			if(message.getAction().name().equals(item.name())) {
				return true;
			}
		}
		
		return false;
	}
	
	// FINCAS 
	public static boolean isFincaAction(Message message) {
		
		for (FincaActions item : FincaActions.values()) {
			if(message.getAction().name().equals(item.name())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isFincaAction(Actions action) {
		
		for (FincaActions item : FincaActions.values()) {
			if(item.name().equals(action.name())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static FincaActions obtainFincaAction(Actions action) {
		return FincaActions.valueOf(action.name());
	}
	
	// CULTIVOS
	public static boolean isCultivoAction(Message message) {
		
		for (CultivoActions item : CultivoActions.values()) {
			if(message.getAction().name().equals(item.name())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isCultivoAction(Actions action) {
		
		for (CultivoActions item : CultivoActions.values()) {
			if(item.name().equals(action.name())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static CultivoActions obtainCultivoAction(Actions action) {
		return CultivoActions.valueOf(action.name());
	}
	
	// ALL
	public static Actions obtainAction(String actionMessage) {
		for (Actions action : Actions.values()) {
			if(action.name().equals(actionMessage)) {
				return action;
			}
		}
		return null;
	}

}
