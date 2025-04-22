plugins {
    id("java")
}

group = "fhv.hotel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jboss.logging:jboss-logging:3.6.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.esotericsoftware.kryo:kryo5:5.6.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}