# HiCKUP

![Project Logo](./logo.png)

## Table of Contents

- [HiCKUP Analyser](#hickup-analyser)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Installation](#installation)
  - [Usage](#usage)
  - [Pipeline Configurations](#pipeline-configurations)

## Introduction

Natural Language Processing (NLP) has seen great advances in the recent years. This project provides a powerful framework to map network traffic data into the NLP domain.

## Installation

Follow these steps to install HiCKUP Analyser:

1. Ensure you have the following prerequisites installed:
    - openjdk 11.0.21 2023-10-17
    - Maven

2. Clone the repository:
    ```
    git clone https://github.com/somm-s/Hickup.git
    ```

3. Navigate into the project directory:
    ```
    cd Hickup
    ```

4. Build the project with Maven:
    ```
    mvn clean install
    ```
5. Build and produce executable JAR with all dependencies:
    ```
    mvn clean package
    ```
    Now, the executable JAR file should be ready for usage under ```target/hickup-1.0.jar```.


## Usage

The HiCKUP project provides two core utilities: (1) A converter that can convert a large scale and possibly non-sequential PCAP dataset into a linearized, condensed and compressed CSV format and (2) a pipeline that converts the stream of packet metadata from this format into a hierarchical token sequence representation that can be used by language models.


Run converter:
```
java -jar target/hickup-1.0.jar converter <inputDir> <outputDir>
```

Run pipeline:
```
java -jar target/hickup-1.0.jar pipeline <inputDir> <outputDir>
```

## Pipeline Configurations
All configurations can be set under ``` src/main/java/ch/cydcampus/hickup/pipeline/PipelineConfig.java```. The meaning of the different configurations are described in the config file. The file contains configuration for both, the abstraction module and the tokenization module.