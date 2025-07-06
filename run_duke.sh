#!/bin/bash

# Duke Chat Application Launcher - Premium WhatsApp Style UI
echo "🚀 Starting Duke - WhatsApp Style Chat Interface"
echo "✨ Features: Polished UI, Real Images, Modern Scrollbar, Enhanced Typography"

# Build the application first
echo "📦 Building application..."
./gradlew compileJava processResources -q

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "🎯 Launching Duke with polished UI..."
    java -cp build/classes/java/main:build/resources/main ChatLauncher
else
    echo "❌ Build failed! Please check for errors."
    exit 1
fi 