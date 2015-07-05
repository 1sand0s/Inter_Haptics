#include<stdio.h>
#include<jni.h>
#include "Ultra_virtual.h"
/* This is an alternative way of sending data regarding phase delays,
 * supposing the IOT device cannot handle the processing library*/
JNIEXPORT void JNICALL Java_Ultra_1virtual_write(JNIEnv *env,jclass c,jstring h)
{
        const char *ch=(*env)->GetStringUTFChars(env,h,0);
        printf("%s",ch);
        FILE* f;
        f=fopen("/dev/ttyACM0","w");
        fprintf("%s",ch);
        fclose(f);
        (*env)->ReleaseStringUTFChars(env,h,ch);
        return;
}
