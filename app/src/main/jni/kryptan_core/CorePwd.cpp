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

jlong Java_org_caelus_kryptanandroid_core_CorePwd_GetTimeCreated(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		return pwd->GetTimeCreated();
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlong Java_org_caelus_kryptanandroid_core_CorePwd_GetTimeModified(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		return pwd->GetTimeLastModified();
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jstring Java_org_caelus_kryptanandroid_core_CorePwd_GetTimeCreatedString(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		return env->NewStringUTF(pwd->GetTimeCreatedString().c_str());
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jstring Java_org_caelus_kryptanandroid_core_CorePwd_GetTimeModifiedString(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		return env->NewStringUTF(pwd->GetTimeLastModifiedString().c_str());
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jobject Java_org_caelus_kryptanandroid_core_CorePwd_GetLabels(JNIEnv* env, jobject o)
{
	try {
		Pwd* pwd = getHandle<Pwd>(env, o, HANDLE_LIST);

		PwdLabelVector all = pwd->GetLabels();

		jobjectArray labels =
				(jobjectArray) env->NewObjectArray(all.size(),
						env->FindClass(
								"org/caelus/kryptanandroid/core/CoreSecureStringHandler"),
						createJavaSecureStringHandler(env, 0));

		for (int i = 0; i < all.size(); i++) {
			SPointer* ptr;
			jobject obj = createJavaSecureStringHandler(env, &ptr);

			//Copy value the resulting object to the new java reference counted SecureString object.
			ptr->sString->assign(all[i]);

			//Store java object in the array.
			env->SetObjectArrayElement(labels, i, obj);
		}

		return labels;
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
