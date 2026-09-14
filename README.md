# AI Expense Manager - Complete Android Studio Project

Welcome to **AI Expense Manager**! This is a complete, lightweight, battery-friendly Android application written in **Kotlin** and **Jetpack Compose**.

## Features

1. **Natural Language Expense Parser**:
   - Type naturally: *"Tea 20"*, *"Petrol 500"*, *"Had biriyani for 180"*, *"Paid electricity bill 1200"*, *"Got salary 30000"*, *"1.5k"*.
   - Automatically extracts Amount, Category, Description, Type (Expense/Income), and Date.
2. **Offline-First**:
   - 100% offline. Zero internet required. Zero cloud database.
   - Zero ML weight inside APK: fast startup and low RAM usage.
3. **Personal Learning System**:
   - When you correct a detected category (e.g. changing *"Chaya 20"* from Other to Food), the app stores *"Chaya" -> Food* locally in SQLite Room.
   - Next time you type *"Chaya 30"*, it automatically assigns **Food**!
4. **Expense Preview**:
   - Preview sheet showing Amount, Category, Description, Type with **[ Edit ]** and **[ Save ]** buttons.
5. **Monthly Budgeting & History**:
   - Optional monthly budget with warning bar when exceeded.
   - History screen with category and transaction type filtering.
6. **Future-Proof AI Architecture**:
   - Uses `ExpenseParser` interface with `LocalExpenseParser` implementation.
   - Future cloud or on-device AI models (like Gemini) can be added cleanly without changing UI or database code.

---

## How to Build the APK File

### Method 1: Build in Android Studio (Recommended)
1. Download or extract the project files into a folder (e.g., `AIExpenseManager/`).
2. Open **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer).
3. Click **File -> Open...** and select the `AIExpenseManager` folder.
4. Wait for Gradle Sync to complete.
5. Click **Build -> Build Bundle(s) / APK(s) -> Build APK(s)** in the top menu.
6. A notification will appear in the bottom-right corner: *"APK(s) generated successfully: locate"*.
7. Click **locate** to open the folder containing `app-debug.apk`.
8. Copy `app-debug.apk` to your phone and install!

### Method 2: Command Line (Terminal)
In the project directory, run:
```bash
./gradlew assembleDebug
```
The APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Method 3: Cloud GitHub Actions (Free, No Android Studio needed)
1. Push this project folder to a GitHub repository.
2. Go to the **Actions** tab on your GitHub repository.
3. The workflow will run and produce a downloadable `AI-Expense-Manager-Debug-APK` file!
