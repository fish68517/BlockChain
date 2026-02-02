CollectorCoin Client Application
====

This is the current working directory for the client application.

1. `src/main/js` folder contains frontend React source code.
2. `src/main/java` folder contains Spring Boot source code.
3. `src/main/resource` folder contains static resource needed at application runtime.

## Prerequisites
1. Java 8. Make sure `$JAVA_HOME` variable is set correctly.
2. Maven
3. Node
4. NPM
5. MySQL Server. Currently the app expects it to run on port 3306. You have to start a MySQL server, create a database collectorcoin_db, create a user username collectorcoinusr and password collectorcoinpwd. The tables should be created automatically when you run the server, but they need to be populated by hand. Here is all the roles that will be needed:
```
## Before Starting Client
CREATE DATABASE collectorcoin_db;
CREATE USER 'collectorcoinusr'@'localhost' IDENTIFIED BY 'collectorcoinpwd';
GRANT ALL PRIVILEGES ON collectorcoin_db.* TO collectorcoinusr@localhost;
FLUSH PRIVILEGES;

## This will create all the allowed user role type that can be registered 
USE collectorcoin_db;
INSERT INTO roles (name) VALUES ('ROLE_OWNER'); 
INSERT INTO roles (name) VALUES ('ROLE_INVESTOR'); 
INSERT INTO roles (name) VALUES ('ROLE_RESTORER'); 
INSERT INTO roles (name) VALUES ('ROLE_BUYER'); 
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

6. Create a `src/main/resources/application.properties` file and copy the contents from `src/main/resources/application.properties.example`. Replace `collectorcoin.app.absoluteUploadPath` to a path relative to your file system. Files uploaded as part of project listing creation will be saved under this directory.

## Running the Client application

```
export JAVA_HOME={PATH_TO_YOUR_JAVA}
echo $JAVA_HOME
export M2_HOME={PATH_TO_YOUR_MAVEN}
PATH="${JAVA_HOME}/BIN:${M2_HOME}/bin:${PATH}"
export PATH
mvn wrapper:wrapper
./mvnw spring-boot:run
```
## Troubleshooting

1. You might have to run `mvn clean install` before starting the application.
2. If there is a permission error, you can run `chmod -x script-name`.
3. You need to register users using the `Register` button on frontend before logging in. 
## Some details about the Client application
1. Frontend Components are located under `./src/main/js`. Routing information is in `./src/main/js/Navigation.js` and the main frontend pages are in `./src/main/js/pages`.
2. Backend Components are located under `./src/main/java`. Api endpoint controllers are location under `./src/main/java/com/dlmgroup/collectorcoin/controllers`.
3. JBPM Client Api which is used to connect to the server is under `./src/main/java/com/dlmgroup/collectorcoin/jbpm`.
4. Data models that are used to create MYSQL tables are located under `./src/main/java/com/dlmgroup/collectorcoin/models`.

