
## Notes

* w3c/sensors - security issues
    - Amazing stuff!
    - https://github.com/w3c/sensors/issues?q=is%3Aissue+sort%3Aupdated-desc+is%3Aclosed+label%3Asecurity

* 2017: GitHub discussion on increasing sampling rate in web applications
    - https://github.com/w3c/sensors/issues/98

* Similar discussion on Chrome:
    - https://bugs.chromium.org/p/chromium/issues/detail?id=421691
    - https://bugs.chromium.org/p/chromium/issues/detail?id=598674

* Firefox:
    - https://bugzilla.mozilla.org/show_bug.cgi?id=1197901
    - https://bugzilla.mozilla.org/show_bug.cgi?id=686401
    - https://bugzilla.mozilla.org/show_bug.cgi?id=1359076

* w3c has a recommendation to limit sensor rates to 60Hz or so:
    - http://w3c.github.io/deviceorientation/spec-source-orientation.html#security-and-privacy

* Android's hardware limititaion on sensor sampling rate:
    
    - SENSOR_DELAY_FASTEST basically gives you ~200 Hz

    - https://android.googlesource.com/platform/hardware/invensense/+/master/65xx/libsensors_iio/MPLSensor.cpp#69

## Research Questions

* How will the existing attacks change with varying sampling rate?
    - Is there a safe zone? - frequency which can be allowed

* From a battery usage perspective
    - Is polling a gyroscope at 60 Hz cheaper or more expensive than polling an accelerometer at 240Hz?

## Fixes to the problem

* Reduce sampling rate, frequency
* Limit data accuracy
* Fuzz frequency / timestamps ?
* Add permissions to sensors

## Why not fast sampling?

* Poor batter life

* Security concerns 
    - Pretty serious 
    - Hard to explain to users which isn't a good combo.
