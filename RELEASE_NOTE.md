# Release Note: 1.0.6.1

## Version: 1.0.6.1

- Fixed SecurityException on Android 13+ by adding RECEIVER_EXPORTED flag when registering datawedgeBroadcastReceiver.
- Updated app versioning to 1.0.6.1.
- Updated build/deploy launch flow to use debug package (`com.zebra.rfid.rwdemo.debug`) to avoid persistent system app update conflicts.
- Removed deprecated manifest package usage and cleaned Gradle property deprecations for cleaner build output.

---

# Release Note: init

## Version: init

- Initial project import and cleanup
- Added automation script for build, deploy, and launch
- Project structure for Zebra RFID RWDemo
- Ready for development and deployment on EM45
