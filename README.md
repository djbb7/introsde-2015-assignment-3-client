# introsde-2015-assignment-3-client

This project defines a client for connecting to and using a SOAP web service defined here ([introsde-assignment-3-server](https://github.com/djbb7/introsde-2015-assignment-3-server)).

The client tests all functionality of the Web Service in a specific order to make sure they are doing what is expected.

The class

##Package Structure

The project is divided into 2 packages. Each package contains:

`introsde.assignment.client`: The client program which connects to the Web Service.

`introsde.assignment.soap`: The classes generated using `wsimport`, which allow to pass and receive the right messages from the Web Service.

##Files included

The project contains some additional files.

`client.log`: Output log of running test's against the Web Server

##Execution

This project contains a `build.xml` file which can be run by `ant`. It will download all the required dependencies using ivy. It will also download ivy if it is not installed.

To execute the client run:
```
ant execute.client
```
This will install the missing dependencies, compile, and execute the program. The output will be save to `client.log`.

The path of the server's WSDL file is defined in the `build.xml` file as `output.log`.