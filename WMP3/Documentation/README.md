# Where’s My Phone!
https://github.cs.adelaide.edu.au/a1790336/WMP3


### 1 Brief Description
### 2 Running/Installation

2.1 Android App

2.2 Server App

### 3 Android App
3.1 Accelerometer

3.2 Wifi Dumping

3.3 JSON Parsing

3.4 ‘Send Result’ Function

3.5 ‘Check Permission’ Function

3.6 UI Layout

### 4 Webapp Server 

4.1 Functionality

4.2 URL operations

4.3 System Requirements

4.4 Operation Instructions

4.5 The Dummy Device Simulator


## 1. Brief Description:

“Where’s My Phone!” is an android application that leverages several API’s in order to scan nearby networks and compare them to its catalog of pre recorded wifi-points. The app will then forward this data to a web server where the location on a map will be calculated and displayed for the user

## 2. Running/Installing the project:

### 2.1 Android App:

Download the latest apk-release stored at WMP3\Android App\WheresMyPhone - Wifi Dump\app\release
Ensure that install apps from unknown sources is either allowed on your phone or accept the prompt when installing the apk.
Run the app and accept all permission requests.
If your Device is Android 10+ for the best accuracy you will want to go into developer options and turn off wifi scan throttling.
In the ligation tab slide “Enable Location” to on.

### 2.2 Web Server:

Already hosted at https://wheresmyphone.ts.r.appspot.com/ as the app is pointed to this address
If you would like to host it yourself follow these instructions:
Download the webserver from the GitHub
Use google to find your hosting sites directions for hosting a nodeJS site, please note you must use a webhost that supports nodeJS such as google Cloud
Further documentation on the operation and features of the Web Server is later in the document.


## 3. Android App:

### 3.1 Accelerometer:

Using a sensor manager get permission to use the phones sensors
Then store the type of sensor you want to utilise into a sensor class, i.e. Sensor accelerometer (Sensor Class) Sensor.TYPE_LINEAR_ACCELERATION (Sensor Type)
Then register a listener to capture the activity of the sensors
The function onSensorChanged(SensorEvent event) updates the x,y,z values of the of the sensor, and updates it on the screen using TextView

### 3.2 WiFi dump:

The Handler API is used to schedule the wifi scan to run once every 2 seconds this can be changed through mInterval.
Registers a broadcast receiver that will fetch the wifi scan results and pass them through to the wifiManager API
Retrieves the BSSID’s and the signal strength in dBm
The Broadcast receiver passes the wifi dump into either dump compare or con_to_json depending on the purpose it is called for.
When calling dump_compare to retrieve a location the app will start the process in a new thread to ensure that the broadcast receiver returns within reasonable time and also that the UI thread is not locked up while the dump is processed.
The broadcast is aborted when the location is turned off as the broadcast is tethered to every wifi scan it would run the code on just a standard scan.

### 3.3 JSON Database:

Every new line is a new json object starting with the coordinates as a parent and then the SSIDs and strengths as children.
Is Stored under /sdcard/WifiScan/ in the android file system although in future this will likely be ported to firebase which will sync in real time between multiple devices.

### 3.4 SendResult

Initiates a new thread as network operations should not be done on the main thread.
Uses the HTTPConnection API to initiate a POST request to our web server.
Prints a log of the response code for debugging

### 3.5 checkPermission:

Used to check whether the user has granted the necessary permissions for the app to function. It is called when the main scene is created, as well as when “ScanButton” is clicked.
Permissions required:

```
WRITE_EXTERNAL_STORAGE
ACCESS_COARSE_LOCATION
ACCESS_FINE_LOCATION
ACCESS_WIFI_STATE
CHANGE_WIFI_STATE
ACCESS_NETWORK_STATE
INTERNET
```

If the function is called and one of these permissions is not permitted, the user will be prompted with a dialogue box to grant these permissions. Once they have been granted, the app will proceed to the next operation.

### 3.5.1 onRequestPermissionResult:

Used to determine if all permissions have been granted. Permissions are stored in a string array.

### 3.6 General Layout of the Application

Main Activity Houses 3 fragments and a bottom navigation toolbar
Location fragment includes LocationFragment.java and fragment_location.XML this fragment handles switching the scheduler on and beginning a new scan.
Setting fragment includes SettingsFragment.java and fragment_settings.XML this fragment handles adding a new location point and keeps this and potential other settings out of the way of the user.
Webview fragment includes WebViewFragment.java and fragment_webview.XML this fragment handles the webView and connects to our server so it can display the user with their current location
Scanner is a separate android class purely for compatibility and code standard point of view that handles all tasks in relation to a wifi scan and can be called from any of the fragments

## 4. Webapp server:

The server is powered by NodeJS 6.14.8, it does not require any external dependencies, and will very likely work without fault on newer and upgraded versions of NodeJS in the future.

### 4.1 The Web server provides 2 primary functions:

Listen to POST inputs from the input phone and save it
Host a HTML/Js webpage that provides a live updated map of the device


### 4.2 The web server provides the following URLs:
```
[IP Address]:	GET	/			Root homepage for the website
		GET	/GetCoord		Returns the X,Y,Z coordinates
		GET	/Ping			Ping check for Javascript
		POST	/Send?X=?Y=?Z=?		Input X,Y,Z from the phone app
```

### 4.3 Server OS Requirements:
Linux
SmartOS
macOS 10.7 and higher
Windows Server 2008 and higher

### 4.4 Operation Instructions:
Open a terminal console within the Server directory, this directory is located in /Server of the Git repository. This directory contains “app.js”, “index.js” files. Use the command to start the server:

```
npm start
```

### 4.5 Dummy Device Simulator

The webapp can simulate a phone to test the functionality of the program, this can be enabled by opening /routes/index.js and setting the the flag on line 13 to true:

```
var useDummy = false; 
```

The server is very lightweight and performance friendly, this was designed in mind to have the most CPU intensive operations performed on individual devices when scaling to multiple phones. The Server has partial multi-device support, however has not been enabled yet as it was out of the scope of the project, however it was built in mind, and the code is commented and will be non trivial for future work to be added.
