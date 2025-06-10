FROM openjdk:8-jre-alpine3.9

ARG APP_NAME="api-platform"

#-XX:TieredStopAtLevel=1: 优化JIT 编译器停留在第 1 级（C1 编译器），跳过更耗时的高级优化（如 C2 编译器）,启动速度更快,内存占用更低(不适合高并发或内存密集型)
# -noverify: 禁用类验证过程,提升启动速度
# -Xmx256m -Xms256m: 初始堆内存与最大堆内存一致
# -XX:+UseContainerSupport: 让 JVM 感知容器资源限制
# -XX:MaxRAMPercentage=80.0: 限制 JVM 使用 80% 的容器内存
# -Djava.security.egd=file:/dev/./urandom: 加速随机数生成
ENV JVM_ARGS "-XX:TieredStopAtLevel=1 -noverify -Xmx256m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0 -Djava.security.egd=file:/dev/./urandom"
ENV JAVA_ARGS ""

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app
COPY /target/$APP_NAME*.jar /app/app.jar

EXPOSE 8080

# 这样才能不用只需sh -c 命令来读取环境变量
ENTRYPOINT exec java $JVM_ARGS -jar app.jar $JAVA_ARGS