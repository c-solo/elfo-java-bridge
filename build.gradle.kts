plugins {
    java
    `java-library`
    `maven-publish`
    id("com.diffplug.spotless") version "6.25.0"
}

allprojects {
    group = "io.github.csolo"
    version = "1.0.0-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters"))
    }
    
    tasks.test {
        useJUnitPlatform()
        // Enable assertions for tests
        jvmArgs("-ea")
    }
    
    // Enable assertions for all JavaExec tasks (like running examples)
    tasks.withType<JavaExec> {
        jvmArgs("-ea")
    }
}

// Publishing configuration
subprojects {
    apply(plugin = "maven-publish")
    
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                
                pom {
                    name.set(project.name)
                    description.set("Java bridge for elfo actor framework")
                    url.set("https://github.com/c-solo/elfo-java-bridge")
                    
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("c-solo")
                            name.set("Konstantin Solo")
                        }
                    }
                }
            }
        }
    }
}

// code formatting
subprojects {
    apply(plugin = "com.diffplug.spotless")
    
    spotless {
        java {
            googleJavaFormat("1.17.0")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
            toggleOffOn()
        }

    }
}
