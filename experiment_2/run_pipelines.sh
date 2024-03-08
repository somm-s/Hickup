#!/bin/bash

# Input parameters
DATASET=$1
OUTPUT_FOLDER=$2

# Directory containing jar files
JAR_DIR="/home/sosi/Documents/repos/Hickup/experiment_2/jars"

# Check if jar directory exists
if [ ! -d "$JAR_DIR" ]; then
    echo "Jar directory does not exist: $JAR_DIR"
    exit 1
fi

# Loop over each jar file in the directory
for JAR_FILE in "$JAR_DIR"/*.jar
do
    # Get the base name of the jar file without the .jar extension
    BASE_NAME=$(basename "$JAR_FILE" .jar)

    # Create a distinct output folder for each jar file
    JAR_OUTPUT_FOLDER="$OUTPUT_FOLDER/$BASE_NAME"
    mkdir -p "$JAR_OUTPUT_FOLDER"

    # Run the jar file
    java -Xmx50g -jar $JAR_FILE $DATASET "" $JAR_OUTPUT_FOLDER

    # Compress the output folder
    zip -r "$JAR_OUTPUT_FOLDER.zip" "$JAR_OUTPUT_FOLDER"
    rm -rf "$JAR_OUTPUT_FOLDER"
done