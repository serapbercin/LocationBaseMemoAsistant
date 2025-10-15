= GeoMemo – Location-Based Memo Assistant
:toc:
:toclevels: 3
:icons: font
:sectnums:
:sectanchors:
:source-highlighter: coderay

== Overview
GeoMemo is a modern Android application that allows users to create and manage memos that trigger contextual notifications when entering a specific geographic area.
When the user approaches a saved memo location (within 200 meters), the app displays a notification containing the memo’s title and the first 140 characters of its content even if the app is in the background or closed.

== Architecture

=== Modular Structure
The app follows a **Clean Architecture** pattern with clear separation of concerns:

[cols="1,3",options="header"]
|===
| Module | Responsibility
| `:app` | Application entry point, dependency graph initialization, navigation host
| `:core` | Common utilities (permissions, configuration constants, context helpers)
| `:data` | Room database, repositories, and data sources
| `:domain` | Business logic and use cases independent of Android framework
| `:featurememo` | UI and ViewModel layer for memo creation and editing
| `:notification` | Geofence management and system notification handling
|===

=== Layered Design - Module Dependencies

:app
├── :core
├── :data
├── :domain
├── :featurememo
└── :notification

:core
└── No dependencies (base module)
    ├── Utils
    ├── Constants
    └── Extensions

:data
├── :core
└── :domain
    ├── Repository Implementations
    ├── Room Database
    └── Data Sources

:domain
└── :core
    ├── Use Cases
    └── Repository Interfaces

:featurememo
├── :core
├── :domain
└── :data
    ├── UI Components
    ├── ViewModels
    └── States

:notification
├── :core
├── :domain
└── :data
    ├── GeofenceManager
    ├── NotificationManager
    └── Services


Each layer communicates with the one directly below it, ensuring modularity and testability.

== Tech Stack

=== Core Technologies
* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Architecture:** MVVM + Clean Architecture
* **Async:** Coroutines + Flow
* **Dependency Injection:** Dagger2
* **Navigation:** Jetpack Navigation Compose
* **Persistence:** Room Database
* **Background Work:** Geofencing API + Location Services

=== Permissions & Services
* ACCESS_FINE_LOCATION
* ACCESS_BACKGROUND_LOCATION (for Android Q+)
* POST_NOTIFICATIONS (Android 13+)


== Main UI Flow

=== User Interface

:imagesdir: screenshots

=== Main Screens
image::location_permission.png[width=250,alt="Location Permission Screen"]

On first launch, the app requests location permissions to enable geofencing features.

image::notification_permission.png[width=250,alt="Notification Permission Screen"]
For Android 13 and above, the app requests notification permissions to send memo alerts.

image::no_memo_yet.png[width=250,alt="No Memos Yet Screen"]

The initial screen when no memos are present prompts the user to add a new memo.

image::memo_list.png[width=250,alt="Memo List Screen"]

The main screen displays all memos with their titles and distances.

image::memo_create.png[width=250,alt="Create Memo Screen"]

The memo creation screen allows users to input title, content, and select a location on the map.

image::save_memo.png[width=250,alt="Save Memo Screen"]

After entering memo details, users can save the memo which sets up geofencing.

imaage::remove_memo.png[width=250,alt="Remove Memo Confirmation"]
Users can delete memos with a confirmation dialog.


=== Test Coverage Areas

* Business Logic (`domain`)
* Data Operations (`data`)
* UI Interactions (`featurememo`)



