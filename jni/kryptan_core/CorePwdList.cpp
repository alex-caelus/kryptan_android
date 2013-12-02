/*
 * CorePwdList.cpp
 *
 *  Created on: 30 nov 2013
 *      Author: Alexander
 */

#include <jni.h>
#include "helpers.h"
#include "kryptan_core/PwdList.h"

#define HANDLE_LIST "nativeHandle"

using namespace Kryptan::Core;


extern "C" {


jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_All(JNIEnv* env,
		jobject o) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);

		PwdList::PwdVector all = list->All();

		return convertToJavaArray(env, all);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}


jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J(JNIEnv* env, jobject o, jlong p)
{
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		SecureString* pattern = (SecureString*)p;

		PwdList::PwdVector filtered = list->Filter(*pattern);

		return convertToJavaArray(env, filtered);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter___3J(JNIEnv* env, jobject o, jlongArray l)
{
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		std::vector<SecureString> labels = convertToVectorFromJavaArray_value<SecureString>(env, l);

		PwdList::PwdVector filtered = list->Filter(labels);

		return convertToJavaArray(env, filtered);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J_3J(JNIEnv* env, jobject o, jlong p, jlongArray l)
{
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		std::vector<SecureString> labels = convertToVectorFromJavaArray_value<SecureString>(env, l);
		SecureString* pattern = (SecureString*)p;

		PwdList::PwdVector filtered = list->Filter(*pattern, labels);

		return convertToJavaArray(env, filtered);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}


jlong Java_org_caelus_kryptanandroid_core_CorePwdList_CreatePwd__JJ(JNIEnv* env, jobject o, jlong desciption, jlong password)
{

}

jlong Java_org_caelus_kryptanandroid_core_CorePwdList_CreatePwd__JJJ(JNIEnv* env, jobject o, jlong description, jlong username, jlong password)
{

}

void Java_org_caelus_kryptanandroid_core_CorePwdList_DeletePwd(JNIEnv* env, jobject o, jlong pwd)
{

}


jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_AllLabels(JNIEnv* env, jobject o)
{

}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_FilterLabels(JNIEnv* env, jobject o, jlong pattern)
{

}

jint Java_org_caelus_kryptanandroid_core_CorePwdList_CountPwds__(JNIEnv* env, jobject o)
{

}

jint Java_org_caelus_kryptanandroid_core_CorePwdList_CountPwds__J(JNIEnv* env, jobject o, jlong label)
{

}


jboolean Java_org_caelus_kryptanandroid_core_CorePwdList_AddPwdToLabel(JNIEnv* env, jobject o, jlong pwd, jlong label)
{

}

jboolean Java_org_caelus_kryptanandroid_core_CorePwdList_RemovePwdFromLabel(JNIEnv* env, jobject o, jlong pwd, jlong label)
{

}


}
