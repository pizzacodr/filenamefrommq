# Purpose

The purpose of this project is to showcase the easy of setup of [RabbitMQ](https://www.rabbitmq.com/) with Java for a presentation at the [Linux user group in Richmond, VA](https://www.meetup.com/RVALUG/). 

 

# What it does

A java process watches a RabbitMQ queue for messages.  When a message arrives, it process it, waits 1 second and moves
the file that message refer to a processed directory.



# IDE Setup

This project was developed using [Eclipse](https://www.eclipse.org/), but all the build information is available on the [Apache Maven](https://maven.apache.org/) build file, so it should be easily setup on any IDE that supports that.  Any other build information can be gleaned from the Maven [pom.xml](./pom.xml) build file.  On my run command on Eclipse, I added `-Djava.util.logging.SimpleFormatter.format="[%1$tF %1$tT] [%4$-7s] %5$s %n"` to the java options on the run command to see the log messages in one line.

 
# Maven Command to Build the Binary

To build a binary, run the maven command `clean compile assembly:single`

An executable will be created on the target directory.


# Running the Binary

The java binary can be setup with a properties file, or the defaults will be used.  A sample properties file is available on the [src/test/resources/configFilenameFroMQ.properties](./src/test/resources/configFilenameFroMQ.properties) . The defaults can be seen on the class [ConfigFile.java](./src/main/java/com/github/pizzacodr/filenamefrommq/ConfigFile.java) with the DefaultValue notation.

A sample logging file was provided on the [src/main/resources/logging.properties](./src/main/resources/logging.properties) .  To use your logging file, add a ` -Djava.util.logging.config.file=logging.properties` to the java command below.

The command to run the binary with Java on Linux is `java -jar filenamefrommq-0.0.1-SNAPSHOT-jar-with-dependencies.jar configFilenameToMQ.properties` or just `java -jar filenamefrommq-0.0.1-SNAPSHOT-jar-with-dependencies.jar` if you are going to use the defaults.

# Sample Shell Script for Running the Binary

To easily run several versions of this code, the [runningProject.sh](./src/test/resources/runningProject.sh) batch script was created with the most common options.
