package server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class AppServer {

	public static void main(String[] args) throws Exception {

		int port = 8443;
		int p = 6379;
		int id = 0;
		CommandLineParser parser = new DefaultParser();

		Options options = new Options();
		options.addOption("port", true, "server port");
		options.addOption("p", true, "redis port");
		options.addOption("bft", false, "to use the bft-smart library");
		options.addOption("id", true, "bft id");
		try {
			CommandLine line = parser.parse( options, args );

			if(line.hasOption("port")) {
				port= Integer.valueOf(line.getOptionValue("port"));
			}

			if(line.hasOption("p")) {
				p= Integer.valueOf(line.getOptionValue("p"));
			}

			if(line.hasOption("id")) {
				id= Integer.valueOf(line.getOptionValue("id"));
			}

			URI baseUri = UriBuilder.fromUri("https://0.0.0.0/").port(port).build();

			ResourceConfig config = new ResourceConfig();

			if(line.hasOption("bft")) {
				config.register( new BFTServerResources(id));
			}
			else {
				config.register( new ServerResources(p));
			}
			config.register( JacksonFeature.class);

			InputStream keystoreInput = new FileInputStream("server.jks");
		    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		    keyStore.load(keystoreInput, "changeme".toCharArray());
		    keystoreInput.close();
			SslContextFactory factory = new SslContextFactory(true);
		    factory.setKeyStore(keyStore);
		    factory.setKeyManagerPassword("changeme");

			JettyHttpContainerFactory.createServer(baseUri,factory, config);
		//	JdkHttpServerFactory.createHttpServer(baseUri, config,SSLContext.getDefault());
			System.err.println("Server ready @ " + baseUri + " : local IP = " + InetAddress.getLocalHost().getHostAddress());

		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "AppServer", options );
		}






	}
}
