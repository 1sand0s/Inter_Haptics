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
 
#ifndef GRID_MATRIX
#define GRID_MATRIX

// Array Arrangement
#define CROSS4 0
#define CROSS5 1
#define CROSS6 2
#define CROSS7 3
#define CROSS8 4
#define CROSS9 5
#define CROSS45 6
#define CROSS56 7
#define CROSS67 8
#define CROSS78 9
#define CROSS89 10

typedef struct Element
{
	int Row;
	int Column;
	double Delay;
}

Element init(int a,int b,double c)
{
	Element E={a,b,c};
	return E;
}
int CHECK_ELEM_ROW(Element E)
{
	return E.Row;
}
int CHECK_ELEM_COLUMN(Element E)
{
	return E.Column;
}
double CHECK_ELEM_DELAY(Element E)
{
	return E.Delay;
}
		
#endif
