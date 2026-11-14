# My Tasks — Android app with a PIN lock

A to-do app with three tabs and a PIN screen in front of them.

- **Tasks** — the box for adding tasks, plus everything still to do
- **Done** — everything ticked off, with restore and clear buttons
- **Settings** — change the PIN, lock on demand, delete all tasks

The PIN is asked for every time the app opens. Tasks save themselves, so they
survive closing the app.

This installs alongside the first to-do app instead of replacing it — the two
have different package names.

---

## Getting the APK

Two routes. The first is faster if Android Studio already works on your machine.

### Route 1 — your laptop

1. Unzip, then **File → Open** in Android Studio → pick the `MyTasks` folder
2. Wait for the first Gradle sync to finish
3. **Build → Build Bundle(s) / APK(s) → Build APK(s)**

The file lands at `app/build/outputs/apk/debug/app-debug.apk`.

### Route 2 — GitHub builds it for you, free

Useful if the laptop build fails, or you just want the APK without running anything.
GitHub's servers have the Android SDK already, and this project ships with the
instructions for them in `.github/workflows/build-apk.yml`.

1. Make a free account at **github.com**
2. **New repository** → name it `MyTasks` → Private is fine → **Create**
3. **Add file → Upload files** → drag the whole unzipped `MyTasks` folder in → **Commit**
4. Open the **Actions** tab. A run called *Build APK* starts by itself
5. Wait about 5 minutes for the green check mark
6. Click the run → scroll to **Artifacts** at the bottom → download **MyTasks-debug-apk**
7. Unzip it → that's `app-debug.apk` → send it to your phone and install

If the Actions tab is empty after step 3, the browser skipped the hidden `.github`
folder during upload. Fix it with **Add file → Create new file**, type
`.github/workflows/build-apk.yml` as the filename, paste the contents of that file
from the zip, and commit.

### Installing on the phone

Copy the APK over (Drive, email, USB), tap it in the Files app, and allow
"install unknown apps" for whichever app you opened it from. If Play Protect warns
you, **More details → Install anyway** — normal for an app that isn't from the store.

---

## How the lock works

First launch asks you to invent a 4-digit PIN and type it twice. Every launch after
that asks for it once.

The PIN itself is never saved. It goes through SHA-256, a one-way function: the same
input always produces the same 64-character fingerprint, and there is no way to work
backwards from the fingerprint to the PIN. Checking a PIN means hashing what was typed
and comparing fingerprints.

**Be honest about what this protects.** It stops someone picking up your unlocked phone
and reading your list. It is not encryption — your tasks sit in the app's storage as
plain text, and anyone with developer tools and physical access could read them. A
4-digit PIN is 10,000 guesses, and nothing here slows an attacker down. It's a door
lock, not a safe.

**Forgot the PIN?** There's no recovery. Settings → Apps → My Tasks → Storage →
Clear data wipes the PIN and the tasks together.

---

## What each file does

```
MyTasks/
├── .github/workflows/build-apk.yml   tells GitHub how to build the APK
├── settings.gradle.kts               where Gradle downloads libraries from
├── build.gradle.kts                  plugins and their versions
├── gradlew / gradlew.bat             run the build without installing Gradle
│
└── app/
    ├── build.gradle.kts              SDK versions, app ID, dependency list
    └── src/main/
        ├── AndroidManifest.xml       app name, icon, which screen launches
        │
        ├── java/com/example/mytasks/
        │   ├── MainActivity.kt       the lock gate + the three tabs
        │   ├── Task.kt               what one task is
        │   ├── TaskStore.kt          saving and loading tasks
        │   ├── PinStore.kt           hashing and checking the PIN
        │   ├── screens/
        │   │   ├── LockScreen.kt     PIN keypad, setup and unlock
        │   │   ├── TasksScreen.kt    tab 1
        │   │   ├── DoneScreen.kt     tab 2
        │   │   ├── SettingsScreen.kt tab 3
        │   │   └── TaskRow.kt        one row, shared by tabs 1 and 2
        │   └── ui/theme/             colours and light/dark setup
        │
        └── res/                      strings, icons, base theme
```

---

## Two ideas worth understanding

**State hoisting.** The task list lives in `MainScreen`, one level above all three
tabs. Each tab gets the list as a parameter and reports changes back through lambdas
(`onAdd`, `onToggle`, `onDelete`). No tab owns the data. That's why ticking a task on
the Tasks tab makes it appear on the Done tab with no syncing code — both tabs are
reading the same list and filtering it differently.

**Navigation without a library.** `selectedTab` is a number, 0 to 2, and a `when`
block picks which screen to draw. Plenty for three tabs with no back stack between
them. Bigger apps add the Navigation Compose library for routes and arguments; this
one doesn't need it.

---

## Things to try next

1. Change the PIN length — `PIN_LENGTH` at the top of `LockScreen.kt`, one number
2. Change the colours in `ui/theme/Color.kt`
3. Add a fourth tab — a new `NavigationBarItem` plus a branch in the `when`
4. Sort tasks newest first — sort the list before `items(...)` in `TasksScreen.kt`
5. Lock the app when it goes to the background, not just when it closes
6. Add due dates — a new field on `Task`, saved in `TaskStore` with the rest

---

## If the build fails

**"SDK location not found"** — open **Tools → SDK Manager**, install Android 15 (API 35),
let Studio write `local.properties` itself. Don't create that file by hand.

**"Unsupported Gradle/AGP version"** — your Studio is newer than this project. Accept the
upgrade prompt, or **Tools → AGP Upgrade Assistant**.

**Red underlines right after opening** — the sync hasn't finished. Wait for the bar at
the bottom, then **File → Invalidate Caches → Invalidate and Restart** if it sticks.
