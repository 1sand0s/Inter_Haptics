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

Components Required
==========================

1.  AT89C51              - 1
2.  24 Mhz Crystal       - 1
3.  30pF Capacitor       - 2
4.  IC-74573             - 2
5.  10k Resistor         - 16
6.  20k Resistor         - 16
7.  2STL1360             - 64 //Depends on the grid size
8.  MA40S4S              - 64 //Depends on the grid size
9.  Arduino              - 1
10. Adafruit PWM driver  - 2  //Depends on the grid size
11. IC-AD633JN           - 1


Circuit
==========================

1. The link to the PCB layout and schematic on my PCBweb account of the 8051 Signal generator is 
   http://www.pcbweb.com/projects/tzcshXaWRVyupbADNmpTBtbRMHqndq

Queries
=========================
For any questions,bugs and any other doubts mail me at 1sand0sardpi@gmail.com



