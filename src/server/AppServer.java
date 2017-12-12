package server;

import java.net.InetAddress;
import java.net.URI;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;


public class AppServer {

	public static void main(String[] args) throws Exception {

		int port = 8443;
		CommandLineParser parser = new DefaultParser();

		Options options = new Options();
		options.addOption("port", true, "server port");
		CommandLine line = parser.parse( options, args );

		if(line.hasOption("port")) {
			port= Integer.valueOf(line.getOptionValue("port"));
		}


		URI baseUri = UriBuilder.fromUri("https://0.0.0.0/").port(port).build();

		ResourceConfig config = new ResourceConfig();
		config.register( new ServerResources());
		config.register( JacksonFeature.class);


		JdkHttpServerFactory.createHttpServer(baseUri, config,SSLContext.getDefault());
		System.err.println("Server ready @ " + baseUri + " : local IP = " + InetAddress.getLocalHost().getHostAddress());





	}
}
