# -----------------------------------------------------------------------------
# ЭТАП 1: СБОРКА (BUILDER)
# -----------------------------------------------------------------------------
# Используем полный образ с Gradle и JDK 21

FROM gradle:8-jdk21-alpine AS builder

ARG APP_VERSION
ARG BUILD_DATE

LABEL version=${APP_VERSION}
LABEL build_date=${BUILD_DATE}
LABEL maintainer="m4-macbook-air"

WORKDIR /app

# Копируем gradle-файл и настройки gradle
# Важно: сначала копируем только pom.xml, потом исходники
# Это позволяет закэшировать слой с зависимостями
COPY /inventory-service/gradlew ./inventory-service/gradlew
COPY /inventory-service/gradle ./inventory-service/gradle
COPY /inventory-service/build.gradle.kts ./inventory-service/build.gradle.kts
COPY /inventory-service/settings.gradle.kts ./inventory-service/settings.gradle.kts

# 2. Важно: предоставим скрипту gradlew права на выполнение в Linux
RUN chmod +x ./inventory-service/gradlew

# Переходим в папку модуля
WORKDIR /app/inventory-service

# Загружаем все зависимости без сборки
# --mount=type=cache сохраняет кэш Maven между сборками (ускоряет локальные сборки)
# --mount=type=cache сохраняет кэш Gradle между сборками (.gradle/caches)
RUN --mount=type=cache,target=/root/gradle/.gradle/caches \
    ./gradlew dependencies --no-daemon || true

COPY /inventory-service/src ./src

# Собираем приложение
# clean — удаляем предыдущие артефакты
# package — создаём jar
# -DskipTests — не запускаем тесты (тесты должны быть запущены до сборки образа)
RUN --mount=type=cache,target=/root/gradle/.gradle/caches \
    ./gradlew bootJar --no-daemon -x test


# -----------------------------------------------------------------------------
# ЭТАП 2: ФИНАЛЬНЫЙ ОБРАЗ (inventoryION)
# -----------------------------------------------------------------------------
# Используем минимальный образ с JRE (без JDK, без компилятора)
# alpine — максимально лёгкий Linux (≈5MB)
FROM eclipse-temurin:21-jre-alpine

# Повторяем аргументы для LABEL (они не передаются между этапами автоматически)
ARG APP_VERSION
ARG BUILD_DATE

# Метаданные финального образа
LABEL version=${APP_VERSION}
LABEL build-date=${BUILD_DATE}
LABEL description="E-Commerce inventoryion Application"

WORKDIR /app

# Устанавливаем curl для healthcheck
# apk add — пакетный менеджер Alpine
# --no-cache — не сохранять индекс пакетов (меньше размер)
RUN apk add --no-cache curl

# Создаём непривилегированного пользователя для безопасности
# spring:spring — пользователь и группа
# -S — системный пользователь (без домашней директории)
RUN addgroup -S spring && adduser -S spring -G spring

# Копируем собранный jar из предыдущего этапа
# --from=builder — берём файл из этапа "builder"
COPY --from=builder /app/inventory-service/build/libs/*.jar app.jar

# Документируем, какой порт использует приложение
# Не открывает порт автоматически, только информация
EXPOSE 8085

# Переключаемся на непривилегированного пользователя
# ВСЕГДА делайте это в inventoryion! Иначе приложение работает от root
USER spring:spring

# Healthcheck для оркестраторов (Kubernetes, Docker Swarm)
# Проверяет /actuator/health каждые 30 секунд
# Если 3 раза подряд неудача — контейнер считается нездоровым
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1


# Точка входа — команда по умолчанию
# Переменные окружения (SPRING_DATASOURCE_URL и др.) подставляются из docker-compose
# Массивная форма ["java", "-jar"] предпочтительнее shell-формы
ENTRYPOINT ["java", "-jar", "app.jar"]