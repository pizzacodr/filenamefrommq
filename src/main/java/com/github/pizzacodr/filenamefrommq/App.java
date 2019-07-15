package com.github.pizzacodr.filenamefrommq;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

public class App {

	private static ConnectionFactory factory;
	private static GetResponse response;
	private static final ConfigFile CFG = ConfigFactory.create(ConfigFile.class, System.getProperties());
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

		setupConnectionFactory();

		try (Connection connection = factory.newConnection(); 
				Channel channel = connection.createChannel()) {

			LOGGER.info("Queue Name: " + CFG.queueName());

			channel.basicQos(1); // maximum number of files the server will deliver before an ack.

			response = channel.basicGet(CFG.queueName(), false);
			
			ifResponseNullWait(channel, 
					"The queue for watcher " + CFG.watcherName() +" is empty during startup, waiting for ");

			while (true) {
				byte[] body = response.getBody();
				String strBody = new String(body);
				LOGGER.info("Watcher " + CFG.watcherName() + " processed message : " + strBody);
				channel.basicAck(response.getEnvelope().getDeliveryTag(), false); // sends the ack
				response = channel.basicGet(CFG.queueName(), false);
				
				TimeUnit.SECONDS.sleep(1);
				
				Path original = Paths.get(CFG.watchDir() + File.separator + strBody);
				Path copy = Paths.get(CFG.processedDir()+ File.separator + strBody);
				
				Files.move(original, copy, StandardCopyOption.REPLACE_EXISTING);
				
				ifResponseNullWait(channel, 
						"Queue for watcher " + CFG.watcherName() + " is empty during processing, waiting for ");
			}
		}
	}

	private static void ifResponseNullWait(Channel channel, String loggerMsg) throws InterruptedException, IOException {
		while (response == null) {
			LOGGER.info(loggerMsg + CFG.waitTime() + " seconds");
			TimeUnit.SECONDS.sleep(CFG.waitTime());
			response = channel.basicGet(CFG.queueName(), false);
		}
	}

	private static void setupConnectionFactory() {
		factory = new ConnectionFactory();
		factory.setHost(CFG.hostname());
		LOGGER.info("Hostname: " + CFG.hostname());
	}
}