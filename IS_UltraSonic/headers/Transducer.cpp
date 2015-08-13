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
 
#include "Grid.h"

Element Transducer::init(int a,int b,double c)
{
	Element E={a,b,c};
	return E;
}
int Transducer::CHECK_ELEM_ROW(Element E)
{
	return E.Row;
}
int Transducer::CHECK_ELEM_COLUMN(Element E)
{
	return E.Column;
}
double Transducer::CHECK_ELEM_DELAY(Element E)
{
	return E.Delay;
}
Element* SETUP_TRANS(int trans_count,char* del,int arrange)
{
  while(trans_count!=0)
  {
	  Transducer::elem2[Transducer::count-trans_count]=Transducer::init(Transducer::row,Transducer::column,del[Transducer::count-trans_count]);
	  trans_count--;
	  Transducer::column++;
	  if(arrange==Transducer::column)
	  {
	    Transducer::row++;
	    Transducer::column=0;
	  }
  }
  Transducer::elem=&Transducer::elem2[0];
  return Transducer::elem;
}
    
