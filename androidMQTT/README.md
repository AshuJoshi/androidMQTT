Android MQTT 
============

This is a simple application that demonstrates running a MQTT client on an Android Device (Smartphone). 

What is MQTT?
-------------

*From the [MQTT Site:] (http://mqtt.org/).* 

MQTT stands for MQ Telemetry Transport. It is a publish/subscribe, extremely simple and lightweight messaging protocol, designed for constrained devices and low-bandwidth, high-latency or unreliable networks. The design principles are to minimise network bandwidth and device resource requirements whilst also attempting to ensure reliability and some degree of assurance of delivery. These principles also turn out to make the protocol ideal of the emerging “machine-to-machine” (M2M) or “Internet of Things” world of connected devices, and for mobile applications where bandwidth and battery power are at a premium.
In addition to the changes in the previous section, certain references are auto-linked:

Implementation Overview
-----------------------

The application has two major components - an Android Service that implements the MQTT client, and an Android Activity that allows to subscribe to topics and publish message to the same topic. The Service & Activity are part of the same APK/Application.

Notes & Credits
---------------

The Android service is in the file: **_MQTTService.java_**. This service is based on a fantastic tutorial by [Dale Lane] (http://dalelane.co.uk/) which can be found [here] (http://dalelane.co.uk/blog/?p=1599). I started out by simply copying the entire code from Dale's post above, and then making it work with my Android applications.  
Changes from Dale's codebase:
* Change the package names/paths to reflect my project.
* Dale's code used activity name from his project in the service. I had to rename the Activity in the code for the service (in _MQTTService.java_) if you end up using _a different activity_. Search for _MQTTActivity_ in the _MQTTService.java_, and replace it if you are using it with your own activity.
* Added a new method that would allow to _publish_ a message to the topic: `public  void publishMessageToTopic(String message)`.
* The code from Dale also uses an icon - I had to put in my own

### External Libraries:

The implementation uses a MQTT library from IBM - you would need to get the **_wmqtt.jar_** file directly from [IBM](http://www-01.ibm.com/support/docview.wss?rs=171&uid=swg24006006&loc=en_US&cs=utf-8&lang=en) or from the [MQTT IA92 Wiki](http://mqtt.org/wiki/doku.php/ia92). This file needs to be copied to the _libs_ directory in your project (this is not in the repo).

Trying it out
-------------

You will need a MQTT Broker such as [Mosquitto](http://mosquitto.org/). Download the broker and install it on your system. The Mosquitto broker comes with two utilities that can be used for testing `mosquitto_sub` (to simulate a subsriber client) and `mosquitto_pub` (for publishing messages).

* Run the Mosquitto broker: ` ./mosquitto -v`
* Load the application on an Android Smartphone (I have only tested with Nexus 4 and Samsung S4 (Google Play) running Android 4.4.2), go to the Preferences Menu, set the MQTT Topic, check the box for manual entry for MQTT broker and in the next field enter the IP Address of the broker
* Tap on the "Start Service" button
* The service should start and the MQTT Broker Console should show that the Android MQTT service has connected and there would be a notification on the Android device as well.
* For example if the Topic is "MQTT-Test" and the IP Address of the machine running the Mosquitto broker and the Mosquitto utilities is 192.168.0.128, then you would issue the command:

    mosquitto_pub -h 192.168.0.128 -t MQTT-Test -m "test message"


* You would see a notifcaiton on the Android device 
* To publish, run the subscriber:

    mosquitto_sub -h 192.168.0.128 -t MQTT-Test

* Now when you enter a message and tap the Publish button - you should be able to see the message being received by `mosquitto_sub`

To Do
-----

* Migrate to the [Eclipse Paho MQTT Java Client] (http://eclipse.org/paho/)
* Add support for discovery of the Broker using something like mDNS or UPnP
