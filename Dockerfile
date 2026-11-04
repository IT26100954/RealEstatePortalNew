FROM node:22-alpine AS frontend
WORKDIR /build/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9.11-eclipse-temurin-21 AS backend
WORKDIR /build
COPY backend/ ./backend/
COPY --from=frontend /build/frontend/dist ./frontend/dist
RUN mvn -B -f backend/pom.xml -Pwith-frontend clean package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --gid 10001 portal && useradd --uid 10001 --gid portal --no-create-home portal
COPY --from=backend /build/backend/target/real-estate-portal-0.0.1-SNAPSHOT.jar ./application.jar
COPY scripts/container-start.sh ./container-start.sh
RUN chmod 755 /app/container-start.sh
USER 10001:10001
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=55 -XX:+UseSerialGC -XX:ReservedCodeCacheSize=64m -Xss512k"
EXPOSE 8081
ENTRYPOINT ["/app/container-start.sh"]
