# DESIGN.md

## Project: Zebra RFID RWDemo

### Overview
This project is an Android application designed for Zebra RFID devices, specifically targeting the EM45 hardware. It demonstrates RFID reading, device interaction, and provides a sample UI for RFID operations.

### Architecture
- **Language:** Java
- **Build System:** Gradle
- **Structure:**
  - `app/` contains the main Android application code
  - `libs/` contains third-party and Zebra-provided libraries
  - `build_deploy_launch.sh` automates build, deploy, and launch

### Key Components
- **RWDemoActivity:** Main activity for user interaction
- **RWDemoIntentParams:** Intent parameter definitions for RFID operations
- **FriendlyProfilesActivity:** Example of profile management

### Build & Deployment
- Use the provided shell script for automated build, deployment, and launch on EM45
- Gradle wrapper included for consistent builds

### Extensibility
- Modular structure for adding new features or device support
- Easy integration with additional Zebra SDKs
