package com.github.pizzacodr.filenamefrommq;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "file:${user.dir}/configFilenameFromMQ.properties", 
    "file:${user.home}/configFilenameFromMQ.properties"})

public interface ConfigFile extends Config {
	
	@DefaultValue("${user.home}/watchDir")
	String watchDir();
	
	@DefaultValue("${user.home}/processedDir")
	String processedDir();
	
	@DefaultValue("localhost")
	String hostname();
	
	@DefaultValue("filename")
	String queueName();
	
	@DefaultValue("2")
	int waitTime();
	
	@DefaultValue("A")
	String watcherName();
}