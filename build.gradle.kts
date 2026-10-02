// This Gradle script builds and releases Unfolding as a Processing library.
// It is based on https://github.com/processing/processing-library-template
// The section marked "USER BUILD CONFIGURATIONS" is intended for customization.


import java.util.Properties
import org.gradle.internal.os.OperatingSystem

plugins {
    id("java")
}

// Sets the Java version to use for compiling your library.
// Processing4 was compiled with Java version 17, so it's recommended to compile your library with version 17.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// read in user-defined properties in release.properties file
// most of these properties will be saved to the library.properties file, a required file in the release
// using task writeLibraryProperties
val libraryProperties = Properties().apply {
    load(rootProject.file("release.properties").inputStream())
}

// the following conditional allows for the version to be overwritten by a Github release
// via the release workflow, which defines a property named "githubReleaseTag"
version = if (project.hasProperty("githubReleaseTag")) {
    // remove leading "v" from tag (the leading "v" is required for the release workflow to trigger)
    project.property("githubReleaseTag").toString().drop(1)
} else {
    libraryProperties.getProperty("prettyVersion")
}

//==========================
// USER BUILD CONFIGURATIONS
//==========================

// the short name of your library. This string will name relevant files and folders.
// <libName>.jar will be the name of your build jar
// <libName>.zip will be the name of your release file
val libName = "Unfolding"

// The group ID of your library, which uniquely identifies your project.
group = "de.fhpotsdam"

// The location of your sketchbook folder. The sketchbook folder holds your installed
// libraries, tools, and modes. It is needed if you wish to copy the library to the
// Processing sketchbook (task deployToProcessingSketchbook), which installs the library locally.
// You can check the sketchbook location in your Processing application preferences.
var sketchbookLocation = ""
val userHome = System.getProperty("user.home")
val currentOS = OperatingSystem.current()
if(currentOS.isMacOsX) {
    sketchbookLocation = if (File("$userHome/Documents/Processing/sketchbook").isDirectory) {
        "$userHome/Documents/Processing/sketchbook"
    } else {
        "$userHome/Documents/Processing"
    }
} else if(currentOS.isWindows) {
    val docsFolder = if (File("$userHome/My Documents").isDirectory) {
        "$userHome/My Documents"
    } else {
        "$userHome/Documents"
    }
    sketchbookLocation = if (File(docsFolder,"Processing/sketchbook").isDirectory) {
        "$docsFolder/Processing/sketchbook"
    } else {
        "$docsFolder/Processing"
    }
} else {
    sketchbookLocation = "$userHome/sketchbook"
}
// If you need to set the sketchbook location manually, uncomment out the following
// line and set sketchbookLocation to the correct location
// sketchbookLocation = "$userHome/sketchbook"

// Unfolding keeps its original (pre-Gradle) folder layout:
// Java sources in src/, and the ui/ and shader/ resources bundled into the jar from data/.
sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
        }
        resources {
            setSrcDirs(listOf("data"))
            include("ui/**", "shader/**")
        }
    }
    // Java examples (not part of the release; the release ships examples-processing/ instead)
    create("examples") {
        java {
            setSrcDirs(listOf("examples", "examples-extern"))
        }
        resources {
            setSrcDirs(listOf("config"))
        }
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    // tests and test apps; some of them use classes from the Java examples
    test {
        java {
            setSrcDirs(listOf("test"))
        }
        resources {
            setSrcDirs(listOf("config"))
        }
        compileClasspath += getByName("examples").output
        runtimeClasspath += getByName("examples").output
    }
}

val examplesImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val examplesRuntimeOnly by configurations.getting

// JOGL native libraries, needed to run examples with the P2D/P3D renderers.
// They are extracted by the task extractJoglNatives, as JOGL cannot load them from Gradle's dependency cache.
val joglNatives by configurations.creating
val joglNativesClassifier = when {
    currentOS.isMacOsX -> "natives-macosx-universal"
    currentOS.isWindows -> "natives-windows-amd64"
    System.getProperty("os.arch") == "aarch64" -> "natives-linux-aarch64"
    else -> "natives-linux-amd64"
}

// Repositories where dependencies will be fetched from.
repositories {
    mavenCentral()
    maven { url = uri("https://jogamp.org/deployment/maven/") }
}

dependencies {
    // resolve Processing core
    compileOnly(group = "org.processing", name = "core", version = "4.3.1")

    // bundled with the library (copied to library/ in the release)
    implementation(group = "log4j", name = "log4j", version = "1.2.15") {
        // 1.2.15 declares optional dependencies (jms, jmx, mail) that are not needed and not on Maven Central
        isTransitive = false
    }
    implementation(files("lib/json4processing.jar"))

    // only needed to compile TuioCursorHandler; users of TUIO interactions provide it themselves
    compileOnly(files("lib/libTUIO.jar"))

    // Java examples
    examplesImplementation(group = "org.processing", name = "core", version = "4.3.1")
    examplesImplementation(files("lib/libTUIO.jar"))
    // third-party libraries used by examples-extern/
    examplesImplementation(fileTree("lib-extern") { include("*.jar") })
    joglNatives(group = "org.jogamp.gluegen", name = "gluegen-rt", version = "2.5.0", classifier = joglNativesClassifier)
    joglNatives(group = "org.jogamp.jogl", name = "jogl-all", version = "2.5.0", classifier = joglNativesClassifier)
    // for MBTilesMapProvider
    examplesRuntimeOnly(group = "org.xerial", name = "sqlite-jdbc", version = "3.53.4.0")

    // tests
    testImplementation(group = "org.processing", name = "core", version = "4.3.1")
    testImplementation(files("lib/libTUIO.jar"))
    testImplementation(group = "junit", name = "junit", version = "4.13.2")
    testRuntimeOnly(group = "org.xerial", name = "sqlite-jdbc", version = "3.53.4.0")
}

val joglNativesDirectory = layout.buildDirectory.dir("natives")

val extractJoglNatives by tasks.registering(Sync::class) {
    from(joglNatives.elements.map { jars -> jars.map { zipTree(it) } }) {
        include("natives/**")
        eachFile { path = path.substringAfterLast("/") }
        includeEmptyDirs = false
    }
    into(joglNativesDirectory)
}

// Runs a Java example, e.g. ./gradlew runExample -Pexample=de.fhpotsdam.unfolding.examples.SimpleMapApp
tasks.register<JavaExec>("runExample") {
    group = "application"
    description = "Runs the Java example given by -Pexample=<fully qualified class name>"
    dependsOn(extractJoglNatives)
    classpath = sourceSets["examples"].runtimeClasspath
    mainClass.set(providers.gradleProperty("example"))
    systemProperty("java.library.path", joglNativesDirectory.get().asFile.absolutePath)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
    // the existing javadoc comments do not pass doclint, which would fail the build
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
    (options as StandardJavadocDocletOptions).addBooleanOption("quiet", true)
}

//==============================
// END USER BUILD CONFIGURATIONS
//==============================


// =============================
// INTERNAL BUILD CONFIGURATIONS
// Do not edit the following sections unless you know what you're doing.
// =============================

// Settings for how the JAR file (your library) will be built.
// You want to name your jar with the library short name, aka libName.
tasks.jar {
    archiveBaseName.set(libName)
    archiveClassifier.set("")
    archiveVersion.set("")
}

// ===========================
// Tasks for releasing library
// ===========================

val releaseRoot = "$rootDir/release"
val releaseName = libName
val releaseDirectory = "$releaseRoot/$releaseName"

// Processing reads library.properties line by line without unescaping (processing.app.Util.readSettings),
// so it is written as plain "key=value" lines instead of using WriteProperties, which would escape ":" in URLs.
tasks.register("writeLibraryProperties") {
    group = "processing"
    val outputFile = file("library.properties")
    val keys = listOf(
        "name", "version", "prettyVersion", "authors", "url", "categories",
        "sentence", "paragraph", "minRevision", "maxRevision"
    )
    val values = keys.associateWith { key ->
        if (key == "prettyVersion") project.version.toString() else libraryProperties.getProperty(key, "")
    }
    inputs.properties(values)
    outputs.file(outputFile)

    doLast {
        outputFile.writeText(values.entries.joinToString("\n", postfix = "\n") { "${it.key}=${it.value}" })
    }
}

// define the order of running, to ensure clean is run first
tasks.build.get().mustRunAfter("clean")
tasks.javadoc.get().mustRunAfter("build")

tasks.register("buildReleaseArtifacts") {
    group = "processing"
    dependsOn("clean","build","javadoc", "writeLibraryProperties")
    finalizedBy("packageRelease", "duplicateZipToPdex")

    doFirst {
        println("Releasing library $libName")
        println(org.gradle.internal.jvm.Jvm.current())

        println("Cleaning release...")
        delete(releaseRoot)
    }

    doLast {
        println("Creating package...")

        println("Copy library...")
        copy {
            from(layout.buildDirectory.file("libs/${libName}.jar"))
            into("$releaseDirectory/library")
        }

        println("Copy dependencies...")
        copy {
            from(configurations.runtimeClasspath)
            into("$releaseDirectory/library")
        }

        println("Copy javadoc...")
        copy {
            from(layout.buildDirectory.dir("docs/javadoc"))
            into("$releaseDirectory/reference")
        }

        println("Copy examples...")
        copy {
            from("$rootDir/examples-processing")
            into("$releaseDirectory/examples")
            exclude("**/*.DS_Store")
        }

        println("Copy sources...")
        copy {
            from("$rootDir/src")
            include("**/*.java")
            into("$releaseDirectory/src")
        }

        println("Copy additional artifacts...")
        copy {
            from(rootDir)
            include("README.md", "LICENSE.txt", "library.properties")
            into(releaseDirectory)
        }

        println("Copy repository library.txt...")
        copy {
            from(rootDir)
            include("library.properties")
            into(releaseRoot)
            rename("library.properties", "$libName.txt")
        }
    }
}

tasks.register<Zip>("packageRelease") {
    dependsOn("buildReleaseArtifacts")
    doFirst {
        println("Create zip file...")
    }
    archiveFileName.set("${libName}.zip")
    from(releaseDirectory)
    into(releaseName)
    destinationDirectory.set(file(releaseRoot))
    exclude("**/*.DS_Store")
}

tasks.register<Copy>("duplicateZipToPdex") {
    doFirst {
        println("Duplicate zip file to pdex extension...")
    }
    from(releaseRoot) {
        include("$libName.zip")
        rename("$libName.zip", "$libName.pdex")
    }
    into(releaseRoot)
}
tasks["duplicateZipToPdex"].mustRunAfter("packageRelease")

tasks.register("deployToProcessingSketchbook") {
    group = "processing"
    dependsOn("buildReleaseArtifacts")

    doFirst {
        println("Copy to sketchbook  $sketchbookLocation ...")
    }

    doLast {
        val installDirectory = file("$sketchbookLocation/libraries/$libName")

        println("Removing old install from: $installDirectory")
        delete(installDirectory)

        println("Copying fresh build to sketchbook $sketchbookLocation ...")
        copy {
            from(releaseDirectory)
            include(
                "library.properties",
                "examples/**",
                "library/**",
                "reference/**",
                "src/**"
            )
            into(installDirectory)
        }
    }
}
