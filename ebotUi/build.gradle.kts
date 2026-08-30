plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.ebot.ui"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    mavenPublishing {
        coordinates(
            groupId = "io.github.emilioboti",
            artifactId = "ebot-ui",
            version = "1.0.2"
        )

        pom {
            name.set("ebotUI")
            description.set("Library to provide UI compose component for Android native app")
            inceptionYear.set("2026")
            url.set("https://github.com/EmilioBoti/ebotLib")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/license/mit")
                }
            }

            developers {
                developer {
                    id.set("ebot")
                    name.set("Emilio Botier")
                    email.set("emiliobotier@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/EmilioBoti/ebotLib.git")
                developerConnection.set("scm:git:ssh://github.com/EmilioBoti/ebotLib.git")
                url.set("https://github.com/EmilioBoti/ebotLib")
            }
        }

        publishToMavenCentral(automaticRelease = true)

        signAllPublications()
    }

}

dependencies {
    // TESTING COMPOSE
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.androidx.espresso.core)

    // COMPOSE
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.material3)
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication>().configureEach {
            versionMapping {
                usage("java-api") {
                    fromResolutionResult()
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}