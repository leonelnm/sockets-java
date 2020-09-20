package edu.ucam.server.message;

import edu.ucam.actions.ActionsHelper;

public class MessageValidator {
	
	public static boolean validate(String message) {
		boolean response = true;
		
		if(message != null && !"".equals(message.trim())){
			
			String parts[] = message.split(" ");
			
			if(parts.length < 2 || parts.length > 3 || ActionsHelper.obtainAction(parts[1]) == null) {
				response = false;
			}
		} else {
			response = false;
		}
		
		return response;
	}

}
