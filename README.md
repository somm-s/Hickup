# HiCKUP Analyser

![Project Logo](./logo.png)

## Table of Contents

- [HiCKUP Analyser](#hickup-analyser)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Installation](#installation)
  - [Usage](#usage)

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

5. Run integration tests with coverage information:
    ```
    mvn clean jacoco:prepare-agent test jacoco:report
    ```

Now, HiCKUP Analyser should be installed and ready to use. The pom file comes with jacoco for coverage analysis.


## Usage

Information is following.