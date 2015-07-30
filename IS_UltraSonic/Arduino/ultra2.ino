#include <comp.h>
#include <Frequency.h>
#include <SoftwareSerial.h>

#define _INT_PIN 12 // Pin to enable serial com with 8051
#define _8051_CRYSTAL 24 // In MHz


String val,val1;
FREQUENCY f;
SoftwareSerial mySerial(10, 11);

void setup()
{
  pinMode(_INT_PIN,OUTPUT);
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
      Enable_8051_Serial_COM();
      mySerial.write(CHECK_FREQ_VALUE(f));
     }
    else if(val.startsWith("CAR_F"))
    {
      char *q=a;
      f=init(CARRIER_FREQUENCY,toFreq(q),CAR);
      Serial.println(CHECK_FREQ_VALUE(f));
      mySerial.begin(9600);
      Enable_8051_Serial_COM();
      mySerial.write(CHECK_FREQ_VALUE(f));
    }
    else if(val.startsWith("CUS_F"))
    {
      /* To set custom frequency values*/
      int freq=(val1.substring(val1.indexOf(":")+1,val1.length()-1)).toInt();
      int value=(256-(1/(freq*24*(12/(_8051_CRYSTAL * 10^6)))));
      mySerial.begin(9600);
      Enable_8051_Serial_COM();
      mySerial.write(value);
    }
  }
  delay(1000);
}
void Enable_8051_Serial_COM()
{
      /* The Hardware interrupt of the 8051 is made edge-triggered and hence generates an interrupt at the falling edge.
       * This is done inorder to allow communication with tye 8051 to set the frequencies for signal generation*/
      digitalWrite(_INT_PIN,HIGH);
      delayMicroseconds(1);
      digitalWrite(_INT_PIN,LOW);
      delayMicroseconds(1);
}
   
