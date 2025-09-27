#!/bin/bash
set -e

echo "🧹 compose down..."
docker compose down

echo "🔨 rebuilding..."
./gradlew clean build -x test

echo "🚀 compose up..."
docker compose up --build
