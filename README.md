[![Build and Tests](https://github.com/andre-celestino-santos/orders-apis/actions/workflows/ci.yml/badge.svg)](https://github.com/andre-celestino-santos/orders-apis/actions/workflows/ci.yml)
# orders-apis

```bash
docker run --name orders-apis-mysql-db -e MYSQL_USER=orders-apis-user -e MYSQL_PASSWORD=orders-apis-pass -e MYSQL_ROOT_PASSWORD=orders-apis-root-pass -e MYSQL_DATABASE=orders-apis-db -p 3306:3306 -d mysql:8
```

```bash
export SPRING_DATASOURCE_PASSWORD="orders-apis-pass" \
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/orders-apis-db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
export SPRING_DATASOURCE_USERNAME="orders-apis-user"
```

```text
SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/orders-apis-db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
SPRING_DATASOURCE_USERNAME="orders-apis-user";
SPRING_DATASOURCE_PASSWORD="orders-apis-pass"
```