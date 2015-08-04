/* Copyright (C) 2015 ADITYA T 
 * ORG: Interactive Spaces
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
 
 #include "8051_Signal.h"
 #include<Arduino.h>
 #include<cmath>
 
 void Serial_COM8051::ENABLE_SERIALCOM8051(void)
 {
      digitalWrite(_8051_SERIAL_ENABLE,HIGH);
      delayMicroseconds(1);
      digitalWrite(_8051_SERIAL_ENABLE,LOW);
      delayMicroseconds(1);
 }
 double Serial_COM8051::CALC_MC(void)
 {
      return (12/(_8051_CRYSTAL*pow(10.0,6)));
 }
 int Serial_COM8051::FREQ_TO_HEX(int f)
 {
      return (int)(256-(1/(f*24*Serial_COM8051::CALC_MC())));
 }
