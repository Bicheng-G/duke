#!/bin/bash

echo "🚀 Starting Duke Premium Task Assistant..."
echo "==========================================="

# Compile if needed
echo "📦 Compiling premium UI..."
./gradlew compileJava

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "🎨 Launching premium WhatsApp-style interface..."
    echo ""
    
    # Run the premium UI
    java -cp build/classes/java/main:build/resources/main ui.WhatsAppStyleChatUI
else
    echo "❌ Compilation failed. Please check the errors above."
    exit 1
fi 