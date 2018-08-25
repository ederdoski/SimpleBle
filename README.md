# Android Simple Bluetooth Low Energy (BLE)

## Introduction

This project is a simple interface to facilitate the use of the native Android API, Bluetooth le (BLE), it is simply a class that develops, which allows basic operations with BLE, since after searching several libraries in different no sites were adapted to my requeriment, feel free to modify this class to your liking, or adapt it for what best suits your project.

## Usage

* **Required**

1) Declares a global variable of type BluetoothLEHelper

```
 BluetoothLEHelper ble;
```

2) Initialize the BluetoothLEHelper class in your onCreate activity

```
 ble = new BluetoothLEHelper(this);
```

3) On your OnDestroy, disconnect the device
```
 ble.disconnect();
```
4) Scan devices in the area
```
	Handler mHandler = new Handler();
	ble.scanLeDevice(true);

	mHandler.postDelayed(() -> {
		//--The scan is over, you should recover the found devices.
	}, ble.getScanPeriod());
```

* **Write in Ble devices**

1) You must enter a String with your service, its characteristic and additionally the value to send, (String, Byte [])

```
ble.write(INSERT_YOUR_SERVICE, INSERT_YOUR_CHARACTERISTIC, DATA_TO_SEND);
```

* **Read in Ble devices**

1) You must enter a String with the service and feature that you want to access

```
ble.read(INSERT_YOUR_SERVICE, INSERT_YOUR_CHARACTERISTIC);
```

## Aditionals Methods

* Returns the state of the connection to the device
```
ble.isConnected();
```
* Returns the current state of the scan
```
ble.isScanning();
```
* Set the scan time
```
ble.setScanPeriod();
```
* Get the scan time
```
ble.getScanPeriod();
```
* Filter the service scan to get only the desired ones
```
ble.setFilterService();
```

## References

* [Bluetooth LE](https://developer.android.com/guide/topics/connectivity/bluetooth-le) - Bluetooth low energy overview

## License

This code is open-sourced software licensed under the [MIT license.](https://opensource.org/licenses/MIT)

