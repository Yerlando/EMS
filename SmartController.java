/*
 * Smart Controller uses M5P tree based model through R interface. To implement smart system, some components
 * such as comfort temperature inside the house, the energy price during the day and day time were taken into account.
 * Smart Controller uses an existing simulation instance from SimAPI database to run simulation. Moreover, this system
 * is able to give an easy-to access information on heating system electricity consumption, control the heating system and
 * optimize the power usage at home with a prediction of the inside temperature.
 */
package EMS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SmartController implements EMS {
	private HttpClient httpClient = HttpClientBuilder.create().build();
	
	public void beginSimulation(){
		// To start the simulation call API service Begin
		try { 
			 HttpPost request = new HttpPost(Utilities.SERVER + Utilities.INSTANCE_ID + "/begin"); 
			 HttpResponse response4 = httpClient.execute(request);
			 BufferedReader rd = new BufferedReader(new InputStreamReader(response4.getEntity().getContent())); 
			 System.out.println("Simulation starts:");
			 System.out.println(rd.readLine());
			 System.out.println("-------------------------");		 
		 }
		 catch (IOException e) { 
			 System.err.println("Fatal transport error: " + e.getMessage()); 
			 e.printStackTrace();
		 }  
	}
	
	public void controlSimulation() throws ClientProtocolException, IOException, ParseException, InterruptedException{
		BufferedReader br;
		 String line;
		 StringEntity params;
		 HttpPost request;
		 HttpGet getRequest;
		 JSONObject jobject;
		 double inside_temperature, tank_temperature, pump_energy;
		 JSONParser jparser = new JSONParser();
		 int newPrediction, oldPrediction=1;
		 int timestep = 1;	
		 
		 while(timestep < Utilities.MAXSIZE+1) 
		 { 		
			httpClient = HttpClientBuilder.create().build();
			getRequest = new HttpGet(Utilities.SERVER + Utilities.INSTANCE_ID + "/1/"+timestep+"/get_sensor"); 
			getRequest.addHeader("accept", "application/json");
			HttpResponse response = httpClient.execute(getRequest); 
			br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			line = br.readLine();
			
			if(line.charAt(0) == '{'){
				System.out.println(line);
				jobject = (JSONObject)jparser.parse(line);
				inside_temperature = Double.parseDouble(jobject.get("Inside_Temperature").toString());
				tank_temperature = Double.parseDouble(jobject.get("Tank_Temperature").toString());
				pump_energy = Double.parseDouble(jobject.get("Pump_Energy").toString());
				
				State state = new State(timestep, inside_temperature,tank_temperature, pump_energy, -1);
				newPrediction = new Predictor().runPrediction(state, timestep);
				
				// update temperature only if it is different from the last one
				if(newPrediction != oldPrediction){
					oldPrediction = newPrediction;
					// case for setting the minimum temperature with status 0
					if(newPrediction == 0)
					{	
						httpClient= HttpClientBuilder.create().build();
						request = new HttpPost(Utilities.SERVER + Utilities.INSTANCE_ID + "/setTemp/");
						params = new StringEntity("{\"TSetHea\":"+Utilities.MIN_TEMP +",\"Status\":\"0\"}");
						params.setContentType("application/json");
						request.setEntity(params);	        
						httpClient.execute(request);
					}
					// case for setting the maximum temperature with status 1
					else
					{
						httpClient =  HttpClientBuilder.create().build();
						request = new HttpPost(Utilities.SERVER + Utilities.INSTANCE_ID + "/setTemp/");
						params = new StringEntity("{\"TSetHea\":"+Utilities.MAX_TEMP +",\"Status\":\"1\"}");
						params.setContentType("application/json");
						request.setEntity(params);	        
						httpClient.execute(request);
					}
				}
				timestep ++;
			}
			// waits and gives more time for Simulation to get the next results
			else{
				Thread.sleep(200);
			}
		 }			 	
	}
}
