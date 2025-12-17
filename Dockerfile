FROM maven:3.8.5-openjdk-17
# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app
# Копируем JAR-файл в контейнер
COPY target/VkVision-0.0.1-SNAPSHOT.jar app.jar
# Открываем порт
EXPOSE 8080
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/app.jar"]