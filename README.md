# Inter_Haptics

This project basically tries to implement a system wherein things that appear in Virtual reality devices like 
Hololens,Occulus etc can be felt . It revolves around adding tactile perception to virtual reality by using 
Ultrasonic transducers.By manipulating their phase and amplitude it is possible to create a pressure point in
space which can be felt by human hands and hence the space can be scanned pretty much like a CRT scanning the TV
screen to simulate texture ,shape etc. Please visit 1sand0s.github.io for detailed documentation of the project.

Usage
==========================

1.Clone the repository

2.Make sure the system variable "JAVA_HOME" has been set up and pointing to the jdk installation dirctory

3.Navigate to the Inter_Haptics/IS_UltraSonic directory and run
<pre><code>
./gradlew cleanEclipse eclipse
</code></pre>
The above code will clean the project

4.To build the project ,run
<pre><code>
  ./gradlew build
</code></pre>

5.To run the project , run
<pre><code>
./gradlew run
</code></pre>

6.For more details, see my video https://youtu.be/iWZdgHCAuVU

NOTE
==========================

Due to the inability of the arduino to switch the signal control transistors at a speed higher than a few milliseconds 
I resorted to use the Raspberry Pi instead. This adds an extra PNP transistor since GPIO's are 3.3 V operational and not 5V
therefore added the PNP as a buffer incase anything shoud go wrong which is very unlikely since the source of the signals are
digital as well.
         This requires wiringPi by Gordon Henderson to be installed on the Raspberry Pi, a brief reveiw will reveal that 
GPIO's can switch at speeds between 4.1MHz-4.6MHz when operated with wiringPi C library which clocks in the nanosecond range
which is perfect for switching the transistors.

Link to wiringPi download and install : http://wiringpi.com/download-and-install/

Link to benchmarking RPi GPIO speed : http://codeandlife.com/2012/07/03/benchmarking-raspberry-pi-gpio-speed/

The pitest file uses 9 Transducers, right now it works only for 9 but with inclusion of Adafruit's PWM i2c driver this can 
be increased significantly to include as many as 992 transducers.

The pin configs are as follows:
                       
                                      (38)(07)(22)                   (28)(07)(06)
                                      (18)(16)(15)                   (05)(04)(03)
                                      (13)(12)(11)                   (02)(01)(00)
                                          SET-A                         SET-B
                                    
Where the numbers of SET-A correspond to the physical pins of RPi's GPIOs and numbers of SET-B correspond to the wiringpi
definition or numbering for those physical pins , either one can be used while coding the C file, but strictly adhere to the
physical pin mapping (SET-A) when connecting the transducers to the RPI.



Components Required
==========================

1.  AT89C51/AT89S51      - 1  http://www.keil.com/dd/docs/datashts/atmel/at89c51_ds.pdf
2.  24 Mhz Crystal       - 1
3.  30pF Capacitor       - 2
4.  IC-74573             - 2  http://www.nxp.com/documents/data_sheet/74HC_HCT573.pdf
5.  10k Resistor         - 16
6.  20k Resistor         - 16
7.  2STL1360             - 64 http://www.st.com/web/en/resource/technical/document/datasheet/CD00072383.pdf 
      
      OR

    PN2222A              - 64 https://www.fairchildsemi.com/datasheets/PN/PN2222A.pdf
8.  MA40S4S              - 64 http://www.farnell.com/datasheets/484560.pdf
9.  Atmega328            - 1  http://www.farnell.com/datasheets/1693866.pdf
10. PCA9685              - 2  http://www.adafruit.com/datasheets/PCA9685.pdf
11. IC-AD633JN           - 1  http://www.analog.com/media/en/technical-documentation/data-sheets/AD633.pdf
12. IC 7490              - 2  http://www.ti.com/lit/ds/symlink/sn74ls90.pdf
13. IC NE565             - 2  http://www.bucek.name/pdf/ne565.pdf
14. CP2102               - 1  https://www.silabs.com/Support%20Documents/TechnicalDocs/CP2102-9.pdf
15. 1k Resistor          - 10 

Circuit
==========================

1. The link to the PCB layout and schematic on my PCBweb account of the 8051 Signal generator is 
   http://www.pcbweb.com/projects/tzcshXaWRVyupbADNmpTBtbRMHqndq

Queries
=========================
For any questions,bugs and any other doubts mail me at 1sand0sardpi@gmail.com



