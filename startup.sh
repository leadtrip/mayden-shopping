#!/bin/bash
set -e

echo "🧹 compose coming down..."
docker compose down

echo "🔨 rebuilding app..."
./gradlew clean build -x test

echo "🚀 compose coming up..."
docker compose up --build
