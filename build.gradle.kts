plugins {
    java
    application
    kotlin("jvm") version "1.4.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
task("execute", JavaExec::class) {
    group = "myCustomTasks"
    main = "com.cmd3.Application"
    classpath = sourceSets["main"].runtimeClasspath
}
task("customrun", JavaExec::class) {
    group = "myCustomTasks"
    main = "com.cmd3.forms.MainForm"
    classpath = sourceSets["main"].runtimeClasspath
}


application {
   mainClassName = "com.cmd3.Application"
}
dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.intellij","forms_rt","7.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    testCompile("junit", "junit", "4.12")
}
