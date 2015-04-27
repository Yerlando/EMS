package EMS;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

public class Simulation {
	public static void main(String[] args) throws ParseException, InterruptedException, ClientProtocolException, IOException {		
		RunSC();
		RunFP();
	}

	public static void RunSC() throws ParseException, InterruptedException, ClientProtocolException, IOException {
		SmartController sc = new SmartController();
		long st;
		st = System.currentTimeMillis();;

		RModel.startEngine();
		sc.beginSimulation();
		
		// wait 14 seconds to warm up the simulation environment
		Thread.sleep(14000);
		
		sc.controlSimulation();	
		System.out.println((System.currentTimeMillis() - st)/(1000.0*60.0)+" minutes took to run the simulation");
		RModel.stopEngine();		 
	}
	
	public static void RunFP() throws ParseException, InterruptedException, ClientProtocolException, IOException {
		FirstPrototype fp = new FirstPrototype();
		long st;
		st = System.currentTimeMillis();
		fp.beginSimulation();
		
		// wait 14 seconds to warm up the simulation environment
		Thread.sleep(14000);
		
		fp.controlSimulation();
		System.out.println((System.currentTimeMillis() - st)/(1000.0*60.0)+" minutes took to run the simulation");		
	}	
}
