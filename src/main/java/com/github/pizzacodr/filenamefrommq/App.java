package com.github.pizzacodr.filenamefrommq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.GetResponse;

public class App {

	private static ConnectionFactory factory;

	public static void main(String[] args) throws IOException, TimeoutException {
		Logger logger = LoggerFactory.getLogger(App.class);

		String hostname = "localhost";
		String queueName = "filename";

		setupConnectionFactory(hostname, logger);

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

			//channel.queueDeclare(queueName, false, false, false, null);
			logger.info("Queue Name: " + queueName);

			//channel.basicQos(1); // maximum number of files the server will deliver before an ack.

			//DefaultConsumer consumer = new DefaultConsumer(channel);
			//channel.basicConsume(queueName, false); // no automatic ack
			GetResponse response = channel.basicGet(queueName, false);
			
			logger.info(Integer.toString(response.getMessageCount()));
			
			while (response != null) {
				logger.info(response.getBody().toString());
				channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
			}
			
			/*
			 * logger.info("consumers: " + channel.consumerCount(queueName) + " Tag: " +
			 * consumer.getConsumerTag());
			 */
			
			

			/*
			 * DeliverCallback deliverCallback = (consumerTag, delivery) -> { String message
			 * = new String(delivery.getBody(), "UTF-8");
			 * 
			 * logger.info(" [x] Received '" + message + "'");
			 * 
			 * logger.info(" [x] Done");
			 * channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); };
			 * channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {});
			 */
		}
	}

	private static void setupConnectionFactory(String hostname, Logger logger) {
		factory = new ConnectionFactory();
		factory.setHost(hostname);

		logger.info("Hostname: " + hostname);
	}
}