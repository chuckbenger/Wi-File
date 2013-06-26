#Wi-File
##Introduction
Available at: https://play.google.com/store/apps/details?id=com.tkblackbelt.sync
Android application that let phones or web browsers have read only access to your devices file system.

##Features
- Beam support
- Authentication
- Access directly in the application or through a web browser

##Technical overview
### Connecting
- Two devices that are on the same network will UDP broadcast their respective ip address to other listening devices.
- Devices can also be connected by using NFC and beaming over your connection info
- The ip address of the iphone is displayed in the system tray so anyone with a web browser can connect

### Authentication
- The requester is required to enter a 4 digit pin displayed on the other phone in order to connect

### File transfer
- File transfers are done using a very small HTTP server library.
- The http server is run in a background thread so transfers can continue if the user exists the application

##Dependancies
NANO Http: https://github.com/NanoHttpd/nanohttpd
Android 3.2 and up