package com.github.pizzacodr.filenamefrommq;

import java.io.IOException;
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
	private static ConfigFile cfg;
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

		cfg = ConfigFactory.create(ConfigFile.class, System.getProperties());

		setupConnectionFactory(cfg.hostname(), LOGGER);

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

			LOGGER.info("Queue Name: " + cfg.queueName());

			channel.basicQos(1); // maximum number of files the server will deliver before an ack.

			response = channel.basicGet(cfg.queueName(), false);
			
			ifResponseNullWait(channel, "Queue empty during startup, waiting for ");

			while (true) {
				byte[] body = response.getBody();
				LOGGER.info(new String(body));
				channel.basicAck(response.getEnvelope().getDeliveryTag(), false); // sends the ack
				response = channel.basicGet(cfg.queueName(), false);
				ifResponseNullWait(channel, "Queue empty during processing, waiting for ");
			}
		}
	}

	private static void ifResponseNullWait(Channel channel, String loggerMsg) throws InterruptedException, IOException {
		while (response == null) {
			LOGGER.info(loggerMsg + cfg.waitTime() + " seconds");
			TimeUnit.SECONDS.sleep(cfg.waitTime());
			response = channel.basicGet(cfg.queueName(), false);
		}
	}

	private static void setupConnectionFactory(String hostname, Logger logger) {
		factory = new ConnectionFactory();
		factory.setHost(hostname);
		logger.info("Hostname: " + hostname);
	}
}