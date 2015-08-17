#include "comp.h"
#include "Frequency.h"
#include "Grid.h"
#include "Signal_8051.h"
#include "wiringPi.h"

int main(void)
{
  int i=0;
  FREQUENCY F;
  Element* E;
  int pins[]={28,7,6,5,4,3,2,1,0};
  char delays[]={1};// Delays for transducers here
  for(int i=0;i<sizeof(pins);i++)
  {
      pinMode(pins[i],OUTPUT);
  }
  E=Transducer::SETUP_TRANS(sizeof(pins),delays,CROSS3);
  for(i=0;i<=sizeof(pins);i++)
  {
	    if(i==sizeof(pins))
	    {
  		  i=0;
	    }
	    digitalWrite(pins[i],HIGH);
	    delay((E+i)->Delay);
  }
}
     
  
