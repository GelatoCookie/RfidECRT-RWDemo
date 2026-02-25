# Zebra RFID RWDemo

This project is an Android demo application for Zebra RFID devices (EM45).

## Current Release
- Version: 1.0.6.1
- Fixes Android 13+ receiver registration SecurityException
- Uses debug package `com.zebra.rfid.rwdemo.debug` for deploy script to avoid persistent system app update conflicts

## Features
- RFID tag reading and management
- Sample UI for device interaction
- Automation script for build, deploy, and launch

## Quick Start
1. Clone the repository
2. Run `./build_deploy_launch.sh` to build, deploy, and launch on a connected EM45 device

## Project Structure
- `app/` - Main Android app source
- `libs/` - Third-party and Zebra libraries
- `build_deploy_launch.sh` - Automation script

## Requirements
- Zebra TC22R device
- Android SDK
- Java 8+
- Gradle (wrapper included)

## License
See LICENSE file for details.
