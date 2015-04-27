/*
* The first prototype is a simple Energy Management System that controls a heating system in a
* modelled house during a simulation in Energy Plus. The main goal of the control flow is to increase 
* the energy efficiency of the building based on inside temperatures of the house, photovoltaic (PV) 
* electricity production, day time and peak hours.		
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

public class FirstPrototype implements EMS{
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
			
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void controlSimulation() throws ClientProtocolException, IOException, InterruptedException{
		BufferedReader br;
		String line, daytime;
		StringEntity params;
		HttpPost request;
		HttpGet getRequest;
		int timestep = 1;
		
		while (timestep < 2882)
		{
			httpClient = HttpClientBuilder.create().build();
			getRequest = new HttpGet(Utilities.SERVER + Utilities.INSTANCE_ID + "/1/" + timestep + "/get_sensor");
			getRequest.addHeader("accept", "application/json");
			HttpResponse response = httpClient.execute(getRequest);
			br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			line = br.readLine();

			if (line.charAt(0) == '{') {
				timestep++;
				daytime = Utilities.getTime(timestep);
				
				if (daytime.equals("09:00") || daytime.equals("17:00")) {
					httpClient = HttpClientBuilder.create().build();
					request = new HttpPost(Utilities.SERVER	+ Utilities.INSTANCE_ID + "/setTemp/");
					params = new StringEntity("{\"TSetHea\":" + Utilities.MIN_TEMP + ",\"Status\":\"0\"}");
					params.setContentType("application/json");
					request.setEntity(params);
					httpClient.execute(request);
				} 
				else if (daytime.equals("15:00") || daytime.equals("19:00")) {
					httpClient = HttpClientBuilder.create().build();
					request = new HttpPost(Utilities.SERVER + Utilities.INSTANCE_ID + "/setTemp/");
					params = new StringEntity("{\"TSetHea\":" + Utilities.MAX_TEMP + ",\"Status\":\"1\"}");
					params.setContentType("application/json");
					request.setEntity(params);
					httpClient.execute(request);
				}
			}
			// waits and gives more time for Simulation to get the next results
			else{
				Thread.sleep(200);
			}
		}
	}
}
