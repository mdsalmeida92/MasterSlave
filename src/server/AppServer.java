package server;

import java.net.InetAddress;
import java.net.URI;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;


public class AppServer {

	public static void main(String[] args) throws Exception {
		int port = 8080;


		URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();
		
		ResourceConfig config = new ResourceConfig();
		config.register( new ServerResources());
		

		JdkHttpServerFactory.createHttpServer(baseUri, config);
		System.err.println("Server ready @ " + baseUri + " : local IP = " + InetAddress.getLocalHost().getHostAddress());

		
		
		

	}
}
