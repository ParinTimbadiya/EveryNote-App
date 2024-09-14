EveryNotes: Android Note-Taking Application
    EveryNotes is an Android application designed to help users efficiently create, edit, organize, and store notes. It offers a seamless experience by allowing users to access and modify notes from multiple devices, with changes synced in real-time. The backend ensures user authentication, note storage, and synchronization for a consistent multi-device experience.
    
  Features:
        User Account Management:
            Sign up, log in, and manage user accounts with secure authentication.
        Note Management:
            Create, edit, update, and delete notes.
            View notes in an organized format.
            Sync notes across multiple devices.
        Multi-Device Synchronization:
            Real-time syncing of notes for users logged in on multiple devices.
    Backend Overview:
        Platform: Java Servlet API with JSON
        Database: MySQL for persistent storage and SQLite for local storage on Android devices.
        API: Java Servlets handle HTTP requests and responses, using JSON for data exchange between the client and server to manage user authentication, note CRUD operations, and syncing functionality.
        Authentication: Managed via Java Servlet sessions and JSON payloads for secure user login and session management.
    Database Structure:
        Users table to store user information.
        Notes table to store and manage note data, associated with each user.
    Technology Stack:
        Frontend: Android (Kotlin, Java)
        Backend: Java Servlet API with JSON for data exchange
        Database: MySQL for server-side storage, SQLite for local storage
        API Level: Android 6.0 (Marshmallow) and above
    Future Enhancements:
        Support for attaching images to notes.
        Integration of reminders for upcoming tasks or events.
        Enhanced security features for user data protection.
    
