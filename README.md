# Inter_Haptics

This project basically tries to implement a system wherein things that appear in Virtual reality devices like 
Hololens,Occulus etc can be felt . It revolves around adding tactile perception to virtual reality by using 
Ultrasonic transducers.By manipulating their phase and amplitude it is possible to create a pressure point in
space which can be felt by human hands and hence the space can be scanned pretty much like a CRT scanning the TV
screen to simulate texture ,shape etc.

Usage
==========================

1. Download the zip file.
2. Import the folder Inter_Haptics-master into Eclipse
3. A project called 'IS_UltraSonic' should appear in the workspace of Eclipse
4. Go to build path of this project and under 'external jars' include everything 
   thats inside a folder called 'jars' which is also included in the folder 
   'Inter_Haptics-master/IS_UltraSonic'
5. Run the java file Ultra_main.java
6. For more details, see my video https://youtu.be/iWZdgHCAuVU

                                    From Commandline
                        
1. Clone the repository.
2. Compile the Ultra_main.java present in the folder /root/Inter_Haptics/IS_UltraSonic/src/IS_UltraSonic
   Using javac -Xlint:unchecked Ultra_main.java
3. Now step down one directory by cd.., that is your present directory should be /root/Inter_Haptics/IS_UltraSonic/src
4. run "java -cp :/root/Inter_Haptics/IS_UltraSonic/jars/core.jar:/root/Inter_Haptics/IS_UltraSonic/jars/serial.jar:/root/Inter_Haptics/IS_UltraSonic/jars/jssc.jar IS_UltraSonic.Ultra_main

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



