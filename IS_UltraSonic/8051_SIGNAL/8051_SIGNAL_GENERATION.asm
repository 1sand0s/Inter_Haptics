
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

		 CLR A 
		 MOVC A,@A+DPTR
		 INC DPTR
 		 MOV P2,A
		 SETB P3.6
		 CLR P3.6
		 DJNZ R6,PROC3
		 MOV R6,#18h
		 MOV DPTR,#SINE
PROC3:	 	 RETI

ORG 0023H ; Vector Address for Serial Interrupt
;Stores the frequency values sent through serial into address pointed to by 60H which is 03H(R3) , then increments to 04H(R4)
;and stores the next frequency value
		 MOV A,SBUF
		 CLR RI
		 MOV @R1,A
		 INC R1
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
	
MAIN:	 	 MOV P3,#0FFH
	     	 MOV TCON,#1H
		 MOV P2,#0H
		 MOV P1,#0H
		 MOV TMOD,#22H
		 MOV IE,#93H
		 MOV IP,#11H
		 MOV SCON,#50H
		 MOV DPTR,#SINE
		 MOV R0,#38h
		 MOV R6,#18h
		 MOV R1,#03H

LOCK:		 CJNE R1,#05H,LOCK ; Disables wave generation until both frequencies have been fed at which time 60H will contain 05H having stored values at 03H and 04H
		 
TRANS:	 	 CLR IE.4
		 MOV R1,#03H
		 MOV TL0,R4
		 MOV TL1,R3
		 MOV TH0,R4
		 MOV TH1,R3
		 SETB TR0
		 SETB TR1
PROC1:	 	 JNB TF1,PROC1
		 CLR TF1
		 MOV P1,@R0
		 SETB P3.7
		 CLR P3.7
		 INC R0
		 CJNE R0,#50h,PRC
		 MOV R0,#38h
PRC:	 	 JMP PROC1
END
