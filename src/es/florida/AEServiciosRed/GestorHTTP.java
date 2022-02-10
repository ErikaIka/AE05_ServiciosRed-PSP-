package es.florida.AEServiciosRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class GestorHTTP implements HttpHandler {

	private int temperaturaActual;
	private int temperaturaTermostato;
	
	public GestorHTTP (){
		setTemperaturaActual(15);
		setTemperaturaTermostato(15);
	}
	
	public int getTemperaturaActual() {
		return temperaturaActual;
	}

	public void setTemperaturaActual(int temperaturaActual) {
		this.temperaturaActual = temperaturaActual;
	}

	public int getTemperaturaTermostato() {
		return temperaturaTermostato;
	}

	public void setTemperaturaTermostato(int temperaturaTermostato) {
		this.temperaturaTermostato = temperaturaTermostato;
	}
	

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String requestParamValue=null;
		if("GET".equals(httpExchange.getRequestMethod())) {
			requestParamValue = handleGetRequest(httpExchange);
			handleGETResponse(httpExchange,requestParamValue);
		} else if ("POST".equals(httpExchange.getRequestMethod())) {
			requestParamValue = handlePostRequest(httpExchange);
			handlePOSTResponse(httpExchange,requestParamValue);
		}
	}
	
	private String handleGetRequest(HttpExchange httpExchange) {
		System.out.println("Recibida URI tipo GET: " + httpExchange.getRequestURI().toString());
		return httpExchange.getRequestURI().toString().split("\\?")[1];
	}
	
	private void handleGETResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
		OutputStream outputStream = httpExchange.getResponseBody();
		String htmlResponse = "<html><body style='background-color:#C7FDEF'><br><h2 style='text-align:center'>Temperatura actual "+this.temperaturaActual +"</h2>"+"<h2 style='text-align:center'>Temperatura termostato "+this.temperaturaTermostato +"</h2><div align='center'><img src='https://cdn.urbantecno.com/urbantecno/2017/08/Rey-Noche-Juego-Tronos.jpg' width='900' height='690'></div></body></html>";
		httpExchange.sendResponseHeaders(200, htmlResponse.length());
		outputStream.write(htmlResponse.getBytes());
		outputStream.flush();
		outputStream.close();
		System.out.println("Devuelve respuesta HTML: " + htmlResponse);
	}
	
	private String handlePostRequest(HttpExchange httpExchange) {
		System.out.println("Recibida temperatura por parte del POST: " + httpExchange.getRequestBody().toString());
		InputStream inputStream = httpExchange.getRequestBody();
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		temperaturaTermostato = Integer.parseInt(sb.toString().split("=")[1]);
		
		try {
			regularTemperatura();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
		
	}
	
	private void handlePOSTResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
		OutputStream outputStream = httpExchange.getResponseBody();
		String htmlResponse = "Parametro POST: " + requestParamValue + " -> Se procesara por parte del servidor";
		httpExchange.sendResponseHeaders(200, htmlResponse.length());
		outputStream.write(htmlResponse.getBytes());
		outputStream.flush();
		outputStream.close();
		System.out.println("Devuelve respuesta HTML: " + htmlResponse);
	}
	
	private void regularTemperatura() throws InterruptedException {
		while(temperaturaActual != temperaturaTermostato) {
			
			if (temperaturaActual < temperaturaTermostato) {
				temperaturaActual++;
				System.out.println("Temperatura actual aumentando: " + temperaturaActual);
			}else if (temperaturaActual > temperaturaTermostato) {
				temperaturaActual--;
				System.out.println("Temperatura actual disminuyendo: " + temperaturaActual);
			} 
			System.out.println("La temperatura del termostato es: " + temperaturaTermostato);
			Thread.sleep(5000);
		}

		System.out.println("La temperatura está como mi Lord desea :)");
	}
}
