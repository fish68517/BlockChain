CollectorCoin Server Application.

This project contains the JBPM model, as well as models and controllers to interact with it. Stack: Java & Maven.

Currently configured to run on `http://localhost:8090`.

The project directory contains the following files:

1. business-application-model. This folder contains Java model definitions.
2. business-application-service. This is the entrypoint of the application. It contains the main logic of interacting with the JBPM model through JBPM API and client application through REST controllers. Refer to [this readme](business-application-service/readme.md) for more details.
3. business-central-kjar. This directory contains the .bpmn file with the JBPM model to be deployed with the application is run. The [module readme](business-central-kjar/readme.md) contains more details.
