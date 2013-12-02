/*
 * CoreSecureStringHandler.cpp
 *
 *  Created on: 30 nov 2013
 *      Author: Alexander
 */

#include <jni.h>
#include <string>
#include "kryptan_core/SecureString.h"
#include "helpers.h"

using namespace Kryptan::Core;

#define HANDLE_SECURESTRING "nativeHandle"

extern "C" {

long Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_NewSecureString(JNIEnv* env) {
	SecureString* theString = new SecureString();
	return (jlong)theString;
}

void Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_Dispose(JNIEnv* env, jobject o) {
	SecureString* theString = getHandle<SecureString>(env, o, HANDLE_SECURESTRING);
	delete theString;
}

void Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_Clear(JNIEnv* env, jobject o) {
	SecureString* theString = getHandle<SecureString>(env, o, HANDLE_SECURESTRING);
	theString->assign("", 0, false);
}

void Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_AddChar(JNIEnv* env, jobject o, jchar c) {
	SecureString* theString = getHandle<SecureString>(env, o, HANDLE_SECURESTRING);
	theString->append((char *)(&c), 1, false);
}

jchar Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_GetChar(JNIEnv* env, jobject o, jint i) {
	SecureString* theString = getHandle<SecureString>(env, o, HANDLE_SECURESTRING);
	return theString->at(i);
}

}
