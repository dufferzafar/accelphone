
# Accelphone

Abstract:

> The popularity of smartphones continues to grow because of the wide variety
of functionality they offer - from just being able to make calls and access
the internet to recording videos and playing games. To provide a lot of this
functionality, a modern smartphone comes equipped with sensors such as
camera, microphone, GPS etc. Data from these sensors enables the creation
of applications that offer rich and personalised user experience.
Use of these sensors also opens up the possibilities of new attacks by leaking
information via side channels. In this report, we explore how data from a
particular mobile sensor - the accelerometer - can be used to eavesdrop acous-
tic signals in the vicinity of the phone, thereby converting the accelerometer
into a microphone, and since accessing the sensor doesn’t require any special
permission, this allows an adversary unregulated access to audio surrounding
the device’s environment.

## Contents

`apk/` contains a simple android application to gather sensor data.

`report/` contains the LaTeX report.

`Sensor Data.ipynb` has the python signal analysis stuff.
