# elfo-java-bridge

Java bridge for [elfo](https://github.com/elfo-rs/elfo) actor framework. 
This project provides a way for Java applications to interact with elfo-network as if they were native elfo nodes.

!!! IMPLEMENTATION IS NOT READY YET !!!

## Architecture

The project is split into two main modules:

- **elfo-java-network**: Low-level network protocol implementation
- **elfo-java-node**: High-level node abstraction for Java applications

## Quick Start

### 1. Add dependency

```xml
<dependency>
    <groupId>io.github.csolo</groupId>
    <artifactId>elfo-java-node</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Requirements

- Java 21+
- Gradle 8+

## Building

```bash
./gradlew build
```

## Running Examples

```bash
./gradlew :examples:run
```

## License

MIT License
