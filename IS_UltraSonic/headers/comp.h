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