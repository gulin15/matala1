plugins {
    alias(libs.plugins.android.application)
    // משתמשים ב-id ומציינים במפורש apply false, או פשוט מסירים את הניסיון לבצע הגדרה מחדש.
    // הדרך הנכונה ביותר לפתור התנגשות כזו היא לכתוב את זה כך:
    id("org.jetbrains.kotlin.android") apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.matala1"
    // שינוי לגרסה יציבה וסטנדרטית של compileSdk
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.matala1"
        minSdk = 26
        targetSdk = 36 // מומלץ לסנכרן עם ה-compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    // מאפשר לתוסף להזריק את המשתנים מתוך local.properties ישירות לתוך קובץ ה-Manifest
    defaultPropertiesFileName = "local.properties"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.code.gson:gson:2.10.1")
}