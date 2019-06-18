[![Build Status](https://travis-ci.com/agiles231/Tomcat-Http-Monitoring-Agent.svg?branch=master)](https://travis-ci.com/agiles231/Tomcat-Http-Monitoring-Agent)

# A java agent that monitors Tomcat's HTTP activities
This agent will report metrics relating to HTTP requests and responses.
Provided metrics are:
* Request processing time
* Response size

Currently, this is logged in the console (Not very useful). Later, this will be available through a controller.

## Purpose
Have you ever thought it would be cool if you had timing added to all of your controllers' methods in your Spring Boot application? This agent does that for you. In addition to that, you get the added benefit of uniquely identifiable responses, and the sizes of the responses as well.

## Testing
To run the test suite:
`mvn test`
You may notice that the tests are convoluted. This is because it is tricky to perform modifications to bytecode without instrumentation available (only available in premain). Because of this, all modified classes have to be reflected, and any class that references the modified class as well. In the future, a classloader that solves this problem may be preferrable if possible. Also, there is some code coverage lacking. Basically, all of the ClassVisitors are tested, but none of the transformers or MethodVisitors. It would be difficult to test the MethodVisitors because they rely on certain assumptions about the method they are transforming, i.e. they are basically coupled to the class they are transforming (and by extension, the ClassVisitor that uses them).

The resources in directory ./test-resources are either:
1) Apache class file that I ripped from apache jar
2) A class from the `mock` package. These classes meet certain criteria required for tests. They are not in the test directory because I want them to compile so that I can move their .class file to this directory for tests.
## Build
To build this agent:
`mvn package

## Deployment
To deploy this agent:
` java -javaagent:"tomcat-http-monitoring-agent-jar-with-dependencies.jar" -jar <YourSpringApp.jar> `
