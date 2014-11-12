#Concept

This app shows how to use the altbeacon library with a custom parser so we can scan for true ibeacon
specced beacons. By default it's configured to notify for beacons with the below uuid

D57092AC-DFAA-446C-8EF3-C81AA22815B5

This can be updated in the values/strings.xml file (or removed from code to react on all ibeacons)

Note that bluetooth low energy is only supported from Android API level 18 (4.3)

## Resources

* altbeacon project website: http://altbeacon.org
* altbeacon documentation: http://altbeacon.github.io/android-beacon-library/documentation.html
* ordered broadcast receiver: http://commonsware.com/blog/2010/08/11/activity-notification-ordered-broadcast.html


# TODO

* export framework to shared lib instead
* (TODO) test if bluetooth is enabled


# Version History

## 0.2.0 (upcoming)

* add a beacon simulator for testing on an emulator, will be added in debug mode
* convert menu adapter to baseadapter
* use action menu instead of button for form submit
* fixed error on non numeric input


## 0.1.0

* background scanning example
* foreground scanning example
* update interval from settings
* notification intercept using ordered broadcast