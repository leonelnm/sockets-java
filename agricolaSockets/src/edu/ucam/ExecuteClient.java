package edu.ucam;

import edu.ucam.client.Client;

public class ExecuteClient {
	
	public static void main(String[] args) {
		Client client = new Client();
	    client.startConnection("127.0.0.1", 2020);
	}

}
