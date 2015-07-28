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
 
#ifndef FREQUENCY_8051
#define FREQUENCY_8051

#define CARRIER_FREQUENCY 1
#define MODULATED_FREQUENCY 0

//Carrier Frequencies listed in KHz

#define CAR_F1 0x0D6
#define CAR_F2 0x0EB
#define CAR_F3 75
#define CAR_F4 1000
#define CAR_F5 2000
#define CAR_F6 4000
#define CAR_F7 6000

//Modulated Frequencies listed in Hz

#define MOD_F1 0x059
#define MOD_F2 0x089
#define MOD_F3 0x0A3
#define MOD_F4 0x0AD
#define MOD_F5 0x0BB
#define MOD_F6 0x0C4
#define MOD_F7 0x0CC
#define MOD_F8 0x0D2
#define MOD_F9 0x0D6

//Type of Signal

#define CAR "Carrier Wave"
#define MOD "Modulated Wave"

typedef struct FREQUENCY
{
	int Type;
	int Value;
	char* type;
}FREQUENCY;

FREQUENCY init(int a,int b,char* c)
{
	FREQUENCY f1={a,b,c};
	return f1;
}
int CHECK_FREQ_TYPE(FREQUENCY f)
{
	return f.Type;
}
int CHECK_FREQ_VALUE(FREQUENCY f)
{
	return f.Value;
}
char* CHECK_FREQ_type(FREQUENCY f)
{
	return f.type;
}
int toFreq(char* p)
{
	if(comp(p,"CAR_F1"))
	{
		return CAR_F1;
	}
	else if(comp(p,"CAR_F2"))
	{
		return CAR_F2;
	}
	else if(comp(p,"CAR_F3"))
	{
		return CAR_F3;
	}
	else if(comp(p,"CAR_F4"))
	{
		return CAR_F4;
	}
	else if(comp(p,"CAR_F5"))
	{
		return CAR_F5;
	}
	else if(comp(p,"CAR_F6"))
	{
		return CAR_F6;
	}
	else if(comp(p,"CAR_F7"))
	{
		return CAR_F7;
	}
	else if(comp(p,"MOD_F1"))
	{
		return MOD_F1;
	}
	else if(comp(p,"MOD_F2"))
	{
		return MOD_F2;
	}
	else if(comp(p,"MOD_F3"))
	{
		return MOD_F3;
	}
	else if(comp(p,"MOD_F4"))
	{
		return MOD_F4;
	}
	else if(comp(p,"MOD_F5"))
	{
		return MOD_F5;
	}
	else if(comp(p,"MOD_F6"))
	{
		return MOD_F6;
	}
	else if(comp(p,"MOD_F7"))
	{
		return MOD_F7;
	}
}
#endif
