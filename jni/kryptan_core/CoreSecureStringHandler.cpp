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

jobject Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_NewSecureString(
		JNIEnv* env) {
	try {
		return createJavaSecureStringHandler(env, 0);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

void Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_IncrementReference(
		JNIEnv* env, jobject o) {
	try {
		SPointer* ptr = getHandle<SPointer>(env, o, HANDLE_SECURESTRING);

		LOG_VERBOSE(
				"Incrementing reference for SecureString with SPointer address: %lld ...and with contents: %s",
				(jlong)ptr, ptr->sString->getUnsecureString());

		ptr->sString->UnsecuredStringFinished();
		if (ptr->nReferences == -1) {
			throw std::runtime_error(
					"Incrementing a CoreSecureStringHandler which has already been deleted!");
		}
		ptr->nReferences++;

	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

void Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_DecrementReference(
		JNIEnv* env, jobject o) {
	try {
		SPointer* ptr = getHandle<SPointer>(env, o, HANDLE_SECURESTRING);
		LOG_VERBOSE("Decrementing SecureString with contents: %s",
				ptr->sString->getUnsecureString());
		ptr->sString->UnsecuredStringFinished();
		ptr->nReferences--;
		if (ptr->nReferences == 0) {
			LOG_VERBOSE("%s", "Deleting SecureString");
			ptr->sString->UnsecuredStringFinished();
			delete ptr->sString;
			ptr->sString = 0;
			ptr->nReferences = -1;
		} else if (ptr->nReferences == -1) {
			throw std::runtime_error(
					"Decrementing a CoreSecureStringHandler which has already been deleted!");
		}
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}

}

void Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_Clear(
		JNIEnv* env, jobject o) {
	SPointer* ptr = getHandle<SPointer>(env, o, HANDLE_SECURESTRING);
	ptr->sString->assign((char*) "", 0, false);
}

jint Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_GetLength(
		JNIEnv* env, jobject o) {
	SPointer* ptr = getHandle<SPointer>(env, o, HANDLE_SECURESTRING);
	return ptr->sString->length();
}

void Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_AddChar(
		JNIEnv* env, jobject o, jchar c) {
	SPointer* ptr = getHandle<SPointer>(env, o, HANDLE_SECURESTRING);
	ptr->sString->append((char *) (&c), 1, false);
}

jchar Java_org_caelus_kryptanandroid_core_CoreSecureStringHandler_GetChar(
		JNIEnv* env, jobject o, jint i) {
	SPointer* ptr = getHandle<SPointer>(env, o, HANDLE_SECURESTRING);
	return i >= ptr->sString->length() || i < 0 ? 0 : ptr->sString->at(i);
}

}
