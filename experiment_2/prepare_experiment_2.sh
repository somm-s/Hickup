#!/bin/bash

# Define the directories
CONFIG_DIR="/home/sosi/Documents/repos/Hickup/experiment_2/configs"
TARGET_DIR="/home/sosi/Documents/repos/Hickup/src/main/java/ch/cydcampus/hickup/pipeline"
JAR_DIR="/home/sosi/Documents/repos/Hickup/experiment_2/jars"

# Iterate over all files in the config directory
for CONFIG_FILE in $CONFIG_DIR/*
do
    # Copy the config file to the target directory
    cp $CONFIG_FILE $TARGET_DIR/PipelineConfig.java

    # Compile the project
    mvn clean package

    # Get the base name of the config file
    BASE_NAME=$(basename $CONFIG_FILE)

    # Replace the extension with .jar
    JAR_NAME="${BASE_NAME%.*}.jar"

    # Move the produced jar to the jars directory
    mv target/hickup*.jar $JAR_DIR/$JAR_NAME
done
