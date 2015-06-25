#include<stdio.h>
#include<jni.h>
#include "Ultra_virtual.h"
JNIEXPORT void JNICALL Java_Ultra_1virtual_blink(JNIEnv *env,jclass c,jstring h)
{
        const char *ch=(*env)->GetStringUTFChars(env,h,0);
        printf("%s",ch);
        FILE* f;
        f=fopen("/dev/ttyACM0","w");
        fprintf("%s",ch);
        sleep(10);
        fprintf("%d",344);
        fclose(f);
        (*env)->ReleaseStringUTFChars(env,h,ch);
        return;
}
