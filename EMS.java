package EMS;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

public interface EMS {	
	void beginSimulation();
	void controlSimulation() throws ClientProtocolException, IOException, ParseException, InterruptedException;
}
