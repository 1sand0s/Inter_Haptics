#include <comp.h>
#include <Frequency.h>
#include <SoftwareSerial.h>
#include "8051_Signal.h"




String val,val1;
FREQUENCY f;
SoftwareSerial mySerial(10, 11);
Serial_COM8051 com;

void setup()
{
  pinMode(_8051_SERIAL_ENABLE,OUTPUT);
  Serial.begin(9600);
}
void loop()
{
  if(Serial.available())
  {
    val1="";
    while(Serial.available())
    {
      char p=Serial.read();
      val1=val1+p;
    }
    val1=val1+" ";
    Serial.end();
    Serial.begin(9600);
    val=val1.substring(0,val1.indexOf(":")-1);
    char a[100];
    val.toCharArray(a,sizeof(a));
    if(val.startsWith("MOD_F"))
    {
      char *q=a;
      f=init(MODULATED_FREQUENCY,toFreq(q),MOD);
      Serial.println(CHECK_FREQ_VALUE(f));
      mySerial.begin(9600);
      com.Enable_8051_Serial_COM();
      mySerial.write(CHECK_FREQ_VALUE(f));
     }
    else if(val.startsWith("CAR_F"))
    {
      char *q=a;
      f=init(CARRIER_FREQUENCY,toFreq(q),CAR);
      Serial.println(CHECK_FREQ_VALUE(f));
      mySerial.begin(9600);
      com.Enable_8051_Serial_COM();
      mySerial.write(CHECK_FREQ_VALUE(f));
    }
    else if(val.startsWith("CUS_F"))
    {
      /* To set custom frequency values*/
      int freq=(val1.substring(val1.indexOf(":")+1,val1.length()-1)).toInt();
      int value=com.FREQ_TO_HEX(freq);
      mySerial.begin(9600);
      com.Enable_8051_Serial_COM();
      mySerial.write(value);
    }
  }
  delay(1000);
}
   
