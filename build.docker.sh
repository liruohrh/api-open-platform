docker run --rm \
-v ./:/app \
-v /var/deps/maven:/root/.m2 \
-w /app \
maven:3.8.6-openjdk-8-slim \
mvn -DskipTests=true clean package

docker run --rm \
-v ./frontend:/app \
-w /app \
node:20.19.2-alpine3.22 \
cd /app && npm ci --omit=dev --no-fund --no-audit && npm run build