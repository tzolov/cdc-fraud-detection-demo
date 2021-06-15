
To download the Spring Cloud Data Flow Server Docker Compose file, run the following command:

```
wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/main/src/docker-compose/docker-compose.yml
wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/main/src/docker-compose/docker-compose-dood.yml
wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/main/src/docker-compose/docker-compose-prometheus.yml
wget https://raw.githubusercontent.com/tzolov/cdc-fraud-detection-demo/master/docker/docker-compose.fraud.yml
```

From within the directory where you downloaded the above four docker-compose files, start the system, by running the following commands:

```
export COMPOSE_HTTP_TIMEOUT=300
export COMPOSE_PROJECT_NAME=scdf

docker-compose -f ./docker-compose.yml -f ./docker-compose-prometheus.yml -f ./docker-compose.fraud.yml -f ./docker-compose-dood.yml up
```

```
stream3=cdc-debezium --cdc.config.schema.whitelist=cdc --cdc.name=my-sql-connector --cdc.connector=postgres --cdc.config.database.user=postgres --cdc.config.database.password=postgres --cdc.config.database.dbname=postgres --cdc.config.database.hostname=postgres-cdc --cdc.config.database.port=5432 --cdc.config.database.server.name=my-app-connector --cdc.flattering.enabled=true | log
```

```
stream2=cdc-debezium --cdc.config.schema.whitelist=cdc --cdc.name=my-sql-connector --cdc.connector=postgres --cdc.config.database.user=postgres --cdc.config.database.password=postgres --cdc.config.database.dbname=postgres --cdc.config.database.hostname=postgres-cdc --cdc.config.database.port=5432 --cdc.config.database.server.name=my-app-connector --cdc.flattering.enabled=true | fraud-detection --fraud.detection.processor.model='classpath:/fraud_detection_graph.pb' | log
```

```
stream4=cdc-debezium --cdc.config.schema.whitelist=cdc --cdc.name=my-sql-connector --cdc.connector=postgres --cdc.config.database.user=postgres --cdc.config.database.password=postgres --cdc.config.database.dbname=postgres --cdc.config.database.hostname=postgres-cdc --cdc.config.database.port=5432 --cdc.config.database.server.name=my-app-connector --cdc.flattering.enabled=true | fraud-detection --fraud.detection.processor.model='classpath:/fraud_detection_graph.pb' | analytics --analytics.name=credit --analytics.tag.expression.fraud=#jsonPath(payload,'$..detection')
```
