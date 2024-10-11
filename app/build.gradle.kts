plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.Kussiya"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.Kussiya"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

      ///  testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:21.0.0")
    implementation("com.google.firebase:firebase-firestore:24.4.0")
    implementation("com.google.firebase:firebase-storage:20.2.0")

    // Android dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Navigation components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    // Play Services Auth
    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation(libs.appcompat) // Ensure libs catalog is properly set up.
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.google.android.gms:play-services-auth:20.2.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.google.firebase:firebase-storage:20.0.0")
    implementation ("com.google.firebase:firebase-auth:21.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // Image Slideshow library
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.activity:activity-ktx:1.6.0")
    implementation ("androidx.fragment:fragment-ktx:1.5.0")

    implementation ("org.mindrot:jbcrypt:0.4")
    implementation ("com.squareup.picasso:picasso:2.8")

}
