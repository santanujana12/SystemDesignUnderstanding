#!/bin/bash

echo "==========================================="
echo "Spring Boot URL Shortener - Build Script"
echo "==========================================="
echo ""

# Check Java version
echo "1. Checking Java version..."
java -version 2>&1 | head -n 1
if [ $? -ne 0 ]; then
    echo "ERROR: Java not found. Please install Java 21."
    exit 1
fi

echo ""
echo "2. Cleaning previous builds..."
mvn clean

echo ""
echo "3. Compiling and building..."
mvn compile

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ BUILD SUCCESSFUL!"
    echo ""
    echo "4. Packaging application..."
    mvn package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ PACKAGE SUCCESSFUL!"
        echo ""
        echo "To run the application:"
        echo "  mvn spring-boot:run"
        echo ""
        echo "Or run the JAR:"
        echo "  java -jar target/url-shortener-1.0.0.jar"
    fi
else
    echo ""
    echo "❌ BUILD FAILED"
    echo "Please check the error messages above."
    exit 1
fi
