/*
 * CorePwd.cpp
 *
 *  Created on: 4 dec 2013
 *      Author: Alexander
 */


#include "helpers.h"
#include "kryptan_core/Pwd.h"

using namespace Kryptan::Core;

#define HANDLE_LIST "nativeHandle"

extern "C"
{

jobject Java_org_caelus_kryptanandroid_core_CorePwd_GetDescriptionCopy(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		SecureString orig = pwd->GetDescription();

		SPointer* ptr;
		jobject handler = createJavaSecureStringHandler(env, &ptr);

		ptr->sString->assign(orig);

		return handler;
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jobject Java_org_caelus_kryptanandroid_core_CorePwd_GetUsernameCopy(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		SecureString orig = pwd->GetUsername();

		SPointer* ptr;
		jobject handler = createJavaSecureStringHandler(env, &ptr);

		ptr->sString->assign(orig);

		return handler;
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jobject Java_org_caelus_kryptanandroid_core_CorePwd_GetPasswordCopy(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		SecureString orig = pwd->GetPassword();

		SPointer* ptr;
		jobject handler = createJavaSecureStringHandler(env, &ptr);

		ptr->sString->assign(orig);

		return handler;
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

void Java_org_caelus_kryptanandroid_core_CorePwd_SetNewDescription(JNIEnv* env, jobject o, jlong toSet)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		SPointer* ptr = (SPointer*)toSet;

		pwd->SetDescription(*ptr->sString);

	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

void Java_org_caelus_kryptanandroid_core_CorePwd_SetNewUsername(JNIEnv* env, jobject o, jlong toSet)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		SPointer* ptr = (SPointer*)toSet;

		pwd->SetUsername(*ptr->sString);

	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

void Java_org_caelus_kryptanandroid_core_CorePwd_SetNewPassword(JNIEnv* env, jobject o, jlong toSet)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		SPointer* ptr = (SPointer*)toSet;

		pwd->SetPassword(*ptr->sString);

	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

}
