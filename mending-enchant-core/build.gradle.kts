import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
//    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileTestJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.3-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")

    implementation("org.bstats:bstats-bukkit:3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.68.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    relocate("org.bstats", "me.crylonz.deadchest")
    archiveFileName.set("mending-enchant-SNAPSHOT.jar")
}

val spaceUsername: String by project
val spacePassword: String by project

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.crylonz"
            artifactId = "mending-enchant"
            version = "1.6.3-SNAPSHOT"
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.jetbrains.space/openbeam/p/minecraft-projects/plugins-artifacts")
            credentials {
                username = System.getenv("JB_SPACE_CLIENT_ID")
                password = System.getenv("JB_SPACE_CLIENT_SECRET")
            }
        }
    }
}