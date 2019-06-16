[![Build Status](https://travis-ci.com/agiles231/Tomcat-Http-Monitoring-Agent.svg?branch=master)](https://travis-ci.com/agiles231/Tomcat-Http-Monitoring-Agent)

# A java agent that monitors Tomcat's HTTP activities
This agent will report metrics relating to HTTP requests and responses.
Provided metrics are:
* Nothing so far

## Build
To build this agent:
`mvn package

## Deployment
To deploy this agent:
` java -javaagent SpringHttpMonitoringAgent.jar -jar <YourSpringApp.jar> `
