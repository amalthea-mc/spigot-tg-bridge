import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

tasks.wrapper {
    gradleVersion = "7.6.3"
    // You can either download the binary-only version of Gradle (BIN) or
    // the full version (with sources and documentation) of Gradle (ALL)
    distributionType = Wrapper.DistributionType.ALL
}

buildscript {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.yaml:snakeyaml:1.26")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:13.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

group = "org.kraftwerk28"
val cfg: Map<String, String> = Yaml()
    .load(FileInputStream("$projectDir/src/main/resources/plugin.yml"))
val pluginVersion = cfg.get("version")
val spigotApiVersion = cfg.get("api-version")
val retrofitVersion = "3.0.0"
version = pluginVersion as Any

repositories {
    mavenCentral()
    maven(
        url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
    )
    maven(url = "https://jitpack.io")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.spigotmc:spigot-api:$spigotApiVersion-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("com.vdurmont:emoji-java:5.1.1")
    compileOnly("fr.xephi:authme:5.6.0")
}

defaultTasks("shadowJar")

kotlin {
    jvmToolchain(17)
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set(
            "spigot-tg-bridge-$pluginVersion.jar"
        )
    }
    register("pack") {
        description = "[For development only!] Build project and copy .jar into servers directory"
        dependsOn("shadowJar")
        finalizedBy("copyArtifacts")
    }
}
