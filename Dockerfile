# Sử dụng image base với JDK 15
FROM openjdk:15-jdk-alpine

# Sao chép file JAR vào trong container
COPY ./target/ShopNow-0.0.1-SNAPSHOT.jar .

# Lệnh chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "ShopNow-0.0.1-SNAPSHOT.jar"]

