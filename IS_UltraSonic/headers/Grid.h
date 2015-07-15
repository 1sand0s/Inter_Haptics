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