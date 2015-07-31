/* Copyright (C) 2015 ADITYA T 
 * ORG: Interactive Spaces
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.*/
 
 #include "8051_Signal.h"
 #include<Arduino.h>
 
 void Serial_COM8051::ENABLE_SERIALCOM8051(void)
 {
      digitalWrite(_8051_SERIAL_ENABLE,HIGH);
      delayMicroseconds(1);
      digitalWrite(_8051_SERIAL_ENABLE,LOW);
      delayMicroseconds(1);
 }
 double Serial_COM8051::CALC_MC(void)
 {
      return (12/(_8051_CRYSTAL*10^6));
 }
 int Serial_COM8051::FREQ_TO_HEX(int f)
 {
      return (int)(256-(1/(freq*24*Serial_COM8051::CALC_MC())));
 }
