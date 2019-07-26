
To download the Spring Cloud Data Flow Server Docker Compose file, run the following command:

```
wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/v2.2.0.RC1/spring-cloud-dataflow-server/docker-compose.yml

```

In the directory where you downloaded `docker-compose.yml` and `docker-compose.fraud.yml` was already present, start the system, by running the following commands:
```
export DATAFLOW_VERSION=2.2.0.BUILD-SNAPSHOT
export SKIPPER_VERSION=2.1.0.BUILD-SNAPSHOT
docker-compose -f ./docker-compose.yml -f ./docker-compose.fraud up
```

```
stream3=cdc-debezium --cdc.config.schema.whitelist=cdc --cdc.name=my-sql-connector --cdc.connector=postgres --cdc.config.database.user=postgres --cdc.config.database.password=postgres --cdc.config.database.dbname=postgres --cdc.config.database.hostname=postgres-cdc --cdc.config.database.port=5432 --cdc.config.database.server.name=my-app-connector --cdc.flattering.enabled=true | log
```

```
stream3=cdc-debezium --cdc.config.schema.whitelist=cdc --cdc.name=my-sql-connector --cdc.connector=postgres --cdc.config.database.user=postgres --cdc.config.database.password=postgres --cdc.config.database.dbname=postgres --cdc.config.database.hostname=postgres-cdc --cdc.config.database.port=5432 --cdc.config.database.server.name=my-app-connector --cdc.flattering.enabled=true | log
```

```
stream2=cdc-debezium --cdc.config.schema.whitelist=cdc --cdc.name=my-sql-connector --cdc.connector=postgres --cdc.config.database.user=postgres --cdc.config.database.password=postgres --cdc.config.database.dbname=postgres --cdc.config.database.hostname=postgres-cdc --cdc.config.database.port=5432 --cdc.config.database.server.name=my-app-connector --cdc.flattering.enabled=true | fraud-detection --model-fetch=output --model='classpath:/fraud_detection_graph.pb' | log
```

```
stream4=cdc-debezium --cdc.config.schema.whitelist=cdc --cdc.name=my-sql-connector --cdc.connector=postgres --cdc.config.database.user=postgres --cdc.config.database.password=postgres --cdc.config.database.dbname=postgres --cdc.config.database.hostname=postgres-cdc --cdc.config.database.port=5432 --cdc.config.database.server.name=my-app-connector --cdc.flattering.enabled=true | fraud-detection --model-fetch=output --model='classpath:/fraud_detection_graph.pb' | counter --name=credit --counter.tag.expression.fraud=#jsonPath(payload,'$..detection')
```