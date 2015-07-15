#ifndef FREQUENCY_8051
#define FREQUENCY_8051

#define MODULATING_FREQUENCY 1
#define MODULATED_FREQUENCY 0

//Carrier Frequencies listed in KHz

#define CAR_F1 20
#define CAR_F2 45
#define CAR_F3 75
#define CAR_F4 1000
#define CAR_F5 2000
#define CAR_F6 4000
#define CAR_F7 6000
//Modulated Frequencies listed in Hz

#define MOD_F1 50
#define MOD_F2 70
#define MOD_F3 90
#define MOD_F4 100
#define MOD_F5 120
#define MOD_F6 140
#define MOD_F7 160
#define MOD_F8 180
#define MOD_F9 200

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

#endif
