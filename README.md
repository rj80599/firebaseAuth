# Firebase Authentication with Jetpack Compose

This project demonstrates a simple Android application built using **Jetpack Compose** and **Firebase Authentication**. It features a login page, signup page, and Google sign-in integration, with real-time authentication status handling.

## Features
- **Email and Password Authentication**: Users can log in or sign up using their email and password.
- **Google Sign-In**: Users can authenticate using their Google account.
- **Loading Indicator**: A progress bar is shown during authentication.
- **Navigation**: Simple navigation between login, signup, and home pages using Jetpack Compose's `NavHost`.

## Tech Stack
- **Kotlin**: Primary programming language.
- **Jetpack Compose**: UI framework for building the UI declaratively.
- **Firebase Authentication**: Backend authentication service for handling user login, signup, and Google Sign-In.
- **CredentialManager API**: For handling Google authentication using the Credential Manager API.
  
## Getting Started

### Prerequisites

1. **Android Studio** with **Jetpack Compose** support.
2. **Firebase Project**:
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
   - Set up Firebase Authentication for Email/Password and Google Sign-In in the Firebase console.
   - Download the `google-services.json` file and add it to the `app/` folder of the project.
   - Add your Firebase Web Client ID in `strings.xml`.

### Steps to Run the Project

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/yourusername/firebaseAuth.git
   cd firebaseAuth
