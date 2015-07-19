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
 
#ifndef COMP
#define COMP
int comp(char* a,char* b)
{
	while(*a!='\0' && *b!='\0')
	{
		if(*a>*b)
		{
			return 0;
		}
		else if(*a<*b)
		{
			return 0;
		}
		a++;
		b++;
	}
	return 1;
}
#endif
