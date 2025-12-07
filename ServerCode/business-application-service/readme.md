Business Application Service
=============================

This is the entrypoint of the CollectorCoin backend server.

## Prerequisites

1. Java **8 (version is important)**. Make sure `$JAVA_HOME` is set to the appropriate path.
2. Maven. Make sure `$M2_HOME` is set to the appropriate path.
3. MySQL Server. Currently the app expects it to run on port 3306. You have to start a MySQL server, create a schema `jbpm_db`, create a user with the username/password specified in `application.properties` with access to `jbpm_db`. The tables are created and populated by JBPM automatically.
```
CREATE DATABASE cc_jbpm_db;

CREATE USER 'ccjbpmusr'@'localhost' IDENTIFIED BY 'ccjbpmpass';

GRANT ALL PRIVILEGES ON cc_jbpm_db.* TO ccjbpmusr@localhost;

FLUSH PRIVILEGES;
```
mvn clean install

npm install


## Running the application
1. In one terminal, run
```npm run watch```
for reach page

2. In another terminal, run
```./launch.sh clean install```
to start the server.

## Main Files
1. TaskController.java contains endpoints to complete process instance tasks.
2. ProcessController.java contains auxiliary endpoints that retrieves process instances that are at requested steps in the process.
## Troubleshooting

1. You might have to run `mvn package` and `mvn clean install` if any errors appear at first.
2. If you're getting a permission error run `chmod -x script-name`.
3. If you get `MysqlXAException -  check your data for consistency`, run the SQL commands below. Replace `username` and `host` with applicable values.
```
GRANT XA_RECOVER_ADMIN ON *.* TO 'username'@'host';
FLUSH PRIVILEGES;
```
