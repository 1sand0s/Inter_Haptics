#include <comp.h>
#include <Frequency.h>
#include <SoftwareSerial.h>

#define INT_PIN 12
String val;
FREQUENCY f;
SoftwareSerial mySerial(10, 11);

void setup()
{
  pinMode(INT_PIN,OUTPUT);
  Serial.begin(9600);
}
void loop()
{
  if(Serial.available())
  {
    val="";
    while(Serial.available())
    {
      char p=Serial.read();
      val=val+p;
    }
    val=val+" ";
    Serial.end();
    Serial.begin(9600);
    val=val.substring(0,val.indexOf(":")-1);
    char a[100];
    val.toCharArray(a,sizeof(a));
    if(val.startsWith("MOD"))
    {
      char *q=a;
      f=init(MODULATED_FREQUENCY,toFreq(q),MOD);
      Serial.println(CHECK_FREQ_VALUE(f));
      mySerial.begin(9600);
      digitalWrite(INT_PIN,HIGH);
      delayMicroseconds(1);
      digitalWrite(INT_PIN,LOW);
      delayMicroseconds(1);
      mySerial.write(CHECK_FREQ_VALUE(f));
     }
    else if(val.startsWith("CAR"))
    {
      char *q=a;
      f=init(CARRIER_FREQUENCY,toFreq(q),CAR);
      Serial.println(CHECK_FREQ_VALUE(f));
      mySerial.begin(9600);
      digitalWrite(INT_PIN,HIGH);
      delayMicroseconds(1);
      digitalWrite(INT_PIN,LOW);
      delayMicroseconds(1);
      mySerial.write(CHECK_FREQ_VALUE(f));
    }
  }
  delay(1000);
}
   
