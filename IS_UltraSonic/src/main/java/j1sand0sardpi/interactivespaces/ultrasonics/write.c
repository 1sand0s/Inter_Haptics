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
 
#include<stdio.h>
#include<jni.h>
#include "IS_UltraSonic_Ultra_virtual.h"
JNIEXPORT void JNICALL Java_Ultra_1virtual_blink(JNIEnv *env,jclass c,jstring h)
=======
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
