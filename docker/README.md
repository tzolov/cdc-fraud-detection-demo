
To download the Spring Cloud Data Flow Server Docker Compose file, run the following command:

```
wget https://raw.githubusercontent.com/spring-cloud/spring-cloud-dataflow/v2.1.2.RELEASE/spring-cloud-dataflow-server/docker-compose.yml

```

In the directory where you downloaded `docker-compose.yml` and `docker-compose.override.yml` was already present, start the system, by running the following commands:
```
export DATAFLOW_VERSION=2.1.2.RELEASE
export SKIPPER_VERSION=2.0.3.RELEASE
docker-compose -f ./docker-compose.yml -f ./docker-compose.override.yml up
```