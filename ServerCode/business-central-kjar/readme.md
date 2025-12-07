Business Central Kjar
=======================
This folder contains the JBPM moodel to be deployed as part of the CollectorCoin backend application.

## Making changes to the business process:

1. Start the JBPM server by running `standalone.sh` script (should be located under bin).

2. Log on to the Business-Central which resides on http://localhost:8080/business-central.
Username: wbadmin
Password: wbadmin

3. Make your modifications in the Business Central interface. Creating a test process instance and following all the steps/tasks from start to finish is recommended.

4. Export the .bpmn using the download key.

5. Plug the .bpmn file into the business-central-kjar folder inside resources. Current file is located at `business-central-kjar/src/main/resources/NewListing.bpmn`.

Deployment is taken care of by the main launch script in business-application-server. If something goes wrong, you can run `mvn clean install` manually from this directory.

## Some useful resources:

1. KIE server and Business Central standalone applications https://www.jbpm.org/download/community.html
2. JBPM documentation https://docs.jbpm.org/7.73.0.Final/jbpm-docs/html_single/#_jbpmoverview
3. Some Javadoc links for JBPM API (watch for the correct version):
    - https://javadoc.io/doc/org.jbpm/jbpm-services-api/7.36.1.Final/org/jbpm/services/api/RuntimeDataService.html
    - https://javadoc.io/static/org.jbpm/jbpm-services-api/7.0.0.CR2/org/jbpm/services/api/ProcessService.html
    - https://docs.jboss.org/jbpm/v6.3/javadocs/org/kie/api/task/model/package-summary.html
    - https://javadoc.io/static/org.jbpm/jbpm-services-api/7.36.1.Final/org/jbpm/services/api/model/UserTaskInstanceDesc.html
4. [Video tutorial](https://www.youtube.com/watch?v=59id0mDKlbE&ab_channel=Intellipaat) our team found helpful.

