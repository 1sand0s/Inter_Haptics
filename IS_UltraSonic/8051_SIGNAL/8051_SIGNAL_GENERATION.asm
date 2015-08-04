
 ;* Copyright (C) 2015 ADITYA T 
 ;* ORG: Interactive Spaces
 ;* This library is free software; you can redistribute it and/or
 ;* modify it under the terms of the GNU Library General Public
 ;* License as published by the Free Software Foundation; either
 ;* version 2 of the License, or (at your option) any later version.
 ;*
 ;* This library is distributed in the hope that it will be useful,
 ;* but WITHOUT ANY WARRANTY; without even the implied warranty of
 ;* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 ;* Library General Public License for more details.
ORG 0000H

LJMP SETTINGS

ORG 0003H ; Vector Address for Harware Interrupt INT0
;This block ensures to enable serial interrupt so that any MC can communicate with the 8051 serially through UART
;Since the algorithm uses TH1 for generating the waves and since it is also required for setting the baud rate 
;for serial communication ,this block stops the wave generation and sets baud rate and hence enables serial communication
	
		 CLR TR1		;Clear Timer 1 run bit
		 CLR TR0		;Clear Timer 0 run bit
		 MOV TH1,#0FDH		;Set TH1 to FDH to configure baud rate 9600 for serial communication
		 SETB IE.4		;Enable Serial Interrupt to converse through UART
		 RETI			;Return
		 
ORG 000BH ; Vector Address for Timer 0 Interrupt
;This section of the signal generation is probed through interrupt mode and is used for generating the high frequency signal
;resonant with the crystal frequency of the ultrasonic transducer

		 CLR A 			;Clear accumulator
		 MOVC A,@A+DPTR		;Move the pointer to the address "SINE" containing the values of the signal to the accumulator
		 INC DPTR		;Increment the pointer to point to the address of the next value
 		 MOV P2,A		;Move the value to the port
 		 ;------------------------------
		 SETB P3.6		;This block handles the enabling and disabling of the latch to prevent the output of
		 CLR P3.6		;the port from dropping to 0 during the transfer of values from A to P2.
		 ;------------------------------
		 DJNZ R6,PROC3		;This block handles the process of reloading the dptr register with the address of 
		 MOV R6,#18h		;SINE once all the values under SINE has been cycled through and moved to the port
		 MOV DPTR,#SINE		;P2.
		 ;------------------------------
PROC3:	 	 RETI

ORG 0023H ; Vector Address for Serial Interrupt
;Stores the frequency values sent through serial into address pointed to by R1 which is 03H(R3) , then increments to 04H(R4)
;and stores the next frequency value. It doesn't actually matter in which order the frequencies are sent but for safety it
;is recommended to send the lower(modulated) frequency first followed by higher(carrier) frequency because the signal generation
;is divided between two handlers one is handled through polling mode the other through interrupt mode (somewhat like multi-threading)
;since interrupts are assigned highest priority and since a step in low frequency is longer than that in high frequency 
;exchanging these two might lead to lagging during the generation of the signals.

		 MOV A,SBUF		;Move data from serial buffer received from arduino to the accumulator
		 CLR RI			;Clear the serial interrupt flag to enable receiving more data if any
		 MOV @R1,A		;Move the data received to register R3 whose address (03H) is pointed to by R1
		 INC R1			;Increment the contents of R1 to 04H therefore pointing to register R4
		 RETI
		 
SINE: 	 	 db 127,160,191,217,237,250,255,250,237,217,191,160,127,94,63,37,17,4,0,4,17,37,63,94,127 ; Define the byte sine values

; SETTINGS-	 This is required because since two waves have to been generated and at different frequencies which may or may
;                not be harmonic therefore bear no correlation between each other, so to prevent interlocking whilst accessing 
;	     	 the SINE values by DPTR(FOR Timer 0) and R0(FOR Timer 1) we store them in the memory. So DPTR accesses the 
;                values for Timer0 from code memory and R0 for Timer 1 from RAM address 38H

SETTINGS:	 MOV DPTR,#SINE      ; Store Address of SINE in Data pointer
		 MOV R1,#38h         ; Move Address 38 to register R1
STORE:		 CLR A               ; CLear Acc
		 MOVC A,@A+DPTR      ; Move data at Data pointer(SINE) to ACC
		 INC DPTR            ; Increment Data pointer to next address
		 MOV @R1,A           ; Store SINE values into RAM from code memory starting from address 38H
		 INC R1              ; Increment R1 to point to next address to store next value
		 CJNE R1,#50h,STORE  ; Continue this until all SINE values have been copied into RAM
		 

ORG 0080H
	
MAIN:	 	 MOV P3,#0FFH	     ; Make port P3 as the input Port for receiving serial data
		 ;------------------------
	     	 MOV TCON,#1H	     ; Make the external hardware interrupt which enables serial communication edge triggered
	     	 		     ; This is more efficeint than low level triggered for which for the interrupt to be 
	     	 		     ; recognized the signal should be held at low for atleast 4 machine cycels in contrast
	     	 		     ; with edge triggered where high to low transition immidiately triggers the interrupt
	     	 ;------------------------
		 MOV P2,#0H
		 MOV P1,#0H
		 ;------------------------
		 MOV TMOD,#22H	     ; Configure the timers as 8 bit auto-reload ,therefore as soon as TL(Timer lower byte)
		 		     ; overflows beyond FFH it is restored with the value present in TH(Timer higher byte)
		 		     ; Therefore in this manner we can avoid manually storing the TL value after each interrupt
		 ;------------------------
		 MOV IE,#93H	     ; Enable interrupts for Serial,Hardware and Timer
		 MOV IP,#11H	     ; Set Priority for the interrupt service handler by alloting harware interrupt(signals the
		 		     ; intention of arduino to communicate with 8051) highest priority followed by Serial and 
		 		     ; timer interrupts
		 ;------------------------
		 MOV SCON,#50H	     ; Configure serial comm mode as 8-bit data ,1 start and 1 stop bit
		 MOV DPTR,#SINE	     ; Load Dptr with address of SINE
		 MOV R0,#38h	    
		 MOV R6,#18h	     ; Counter to reset DPTR to address of SINE after cycling through all values of SINE
		 MOV R1,#03H	     ; Points to the address of register R3 where the frequency data is to be stored

LOCK:		 CJNE R1,#05H,LOCK   ; Disables wave generation until both frequencies have been fed at which time R1 will contain 05H having stored values at 03H and 04H
		 ;-------------------------
TRANS:	 	 CLR IE.4	     ; Clear the serial interrupt to prevent any serial comm since baud rate will not be valid 
				     ; since TH1 is being used to generate the signals
		 ;-------------------------
		 MOV R1,#03H	     ; Reset R1 to point to address of R3 incase new frequency data is to be received
		 MOV TL0,R4	     ; Store High frequency HEX value in Timer 0
		 MOV TL1,R3	     ; Store Low frequency HEX value in Timer 1
		 ;-------------------------
		 MOV TH0,R4	     ; Here we take advantage of the auto-reload mode configured by TMOD, therefore as soon
		 MOV TH1,R3	     ; as TL0 or TL1 overflows beyond FFH they are restored with values contained in TH0
		 		     ; and TH1 respectively to count again thereby making precise delays between step counts
		 		     ; to generate the signals of required frequency
		 ;-------------------------
		 SETB TR0	     ; Set timer start bits to enable counting 
		 SETB TR1
		 ;-------------------------
PROC1:	 	 JNB TF1,PROC1	     ; Probe if TL1 has overflown beyond FFH, if it has then TF1(timer 1 interrupt flag) is set
				     ; Until TF1 is set the the program will not execute the suceeding lines
		 ;-------------------------
		 CLR TF1	     ; Clear TF1 
		 ;-------------------------
		 MOV P1,@R0	     ; Move the data at the address pointed to by R0(also points to SINE) to port P1
		 SETB P3.7	     ; The latch is enabled to pass the values to the R-2R ladder following which it
		 CLR P3.7            ; it is immidiately disabled to prevent P1 from dropping to 0 while transferring value
		 		     ; from @R0 to P1
		 ;-------------------------
		 INC R0		     ; Point to the address of next suceeding value
		 ;-------------------------
		 CJNE R0,#50h,PRC    ; Check if R0 has exceeded the address range of SINE that is if all the values under
		 MOV R0,#38h	     ; SINE has been cycled through ,if yes then reload R0 to point to the first value under
		 		     ; under SINE , if no then continue
		 ;-------------------------
PRC:	 	 JMP PROC1 	     ; Jump to PROC1 and repeat the process
END
