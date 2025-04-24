allprojects {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    group = "fhv.hotel"
    version = "1.0-SNAPSHOT"
}

// task paths
val eventDev   = ":event:quarkusDev"
val commandDev = ":command:quarkusDev"
val queryDev   = ":query:quarkusDev"

// configure order
tasks.matching { it.path == commandDev || it.path == queryDev }.configureEach {
    mustRunAfter(eventDev)                      // ← set the rule early
}

// !!! use --parallel when using this task
tasks.register("devOrdered") {
    group = "dev"
    description = "Start event → command & query in Quarkus dev‑mode"
    dependsOn(eventDev, commandDev, queryDev)

    // Check if --parallel was used
    doFirst {
        val isParallel = project.gradle.startParameter.isParallelProjectExecutionEnabled
        if (!isParallel) {
            logger.warn(
                "⚠ devOrdered is running **without** --parallel. " +
                        "Only the first task will run sequentially. " +
                        "Run `./gradlew devOrdered --parallel` to start all tasks concurrently."
            )
        }
    }
}