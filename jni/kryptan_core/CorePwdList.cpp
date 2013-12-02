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

		return convertToJavaArray_fromReferences(env, all);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J(
		JNIEnv* env, jobject o, jlong p) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		SecureString* pattern = (SecureString*) p;

		PwdList::PwdVector filtered = list->Filter(*pattern);

		return convertToJavaArray_fromReferences(env, filtered);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter___3J(
		JNIEnv* env, jobject o, jlongArray l) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		PwdLabelVector labels = convertToVectorFromJavaArray_value<
				SecureString>(env, l);

		PwdList::PwdVector filtered = list->Filter(labels);

		return convertToJavaArray_fromReferences(env, filtered);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J_3J(
		JNIEnv* env, jobject o, jlong p, jlongArray l) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		PwdLabelVector labels = convertToVectorFromJavaArray_value<
				SecureString>(env, l);
		SecureString* pattern = (SecureString*) p;

		PwdList::PwdVector filtered = list->Filter(*pattern, labels);

		return convertToJavaArray_fromReferences(env, filtered);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlong Java_org_caelus_kryptanandroid_core_CorePwdList_CreatePwd__JJ(JNIEnv* env,
		jobject o, jlong description, jlong password) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		Pwd* pwd = list->CreatePwd(*((SecureString*) description),
				*((SecureString*) password));
		return (jlong) pwd;
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlong Java_org_caelus_kryptanandroid_core_CorePwdList_CreatePwd__JJJ(
		JNIEnv* env, jobject o, jlong description, jlong username,
		jlong password) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		Pwd* pwd = list->CreatePwd(*((SecureString*) description),
				*((SecureString*) username), *((SecureString*) password));
		return (jlong) pwd;
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

void Java_org_caelus_kryptanandroid_core_CorePwdList_DeletePwd(JNIEnv* env,
		jobject o, jlong pwd) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		list->DeletePwd((Pwd*) pwd);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

jobjectArray Java_org_caelus_kryptanandroid_core_CorePwdList_AllLabels(
		JNIEnv* env, jobject o) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		PwdLabelVector all = list->AllLabels();

		jobjectArray labels =
				(jobjectArray) env->NewObjectArray(all.size(),
						env->FindClass(
								"org/caelus/kryptanandroid/core/CoreSecureStringHandler"),
						createJavaSecureStringHandler(env,
								SecureString("initial array element")));

		for (int i = 0; i < all.size(); i++) {
			env->SetObjectArrayElement(labels, i,
					createJavaSecureStringHandler(env, all[i]));
		}

		return labels;

	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jobjectArray Java_org_caelus_kryptanandroid_core_CorePwdList_FilterLabels(
		JNIEnv* env, jobject o, jlong p) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		SecureString* pattern = (SecureString*) p;
		PwdLabelVector filtered = list->FilterLabels(*pattern);

		jobjectArray labels =
				(jobjectArray) env->NewObjectArray(filtered.size(),
						env->FindClass(
								"org/caelus/kryptanandroid/core/CoreSecureStringHandler"),
						createJavaSecureStringHandler(env,
								SecureString("initial array element")));

		for (int i = 0; i < filtered.size(); i++) {
			env->SetObjectArrayElement(labels, i,
					createJavaSecureStringHandler(env, filtered[i]));
		}

		return labels;

	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jint Java_org_caelus_kryptanandroid_core_CorePwdList_CountPwds__(JNIEnv* env,
		jobject o) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		return (jint) list->CountPwds();
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jint Java_org_caelus_kryptanandroid_core_CorePwdList_CountPwds__J(JNIEnv* env,
		jobject o, jlong l) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		SecureString label = *(SecureString*)l;
		return (jint) list->CountPwds(label);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jboolean Java_org_caelus_kryptanandroid_core_CorePwdList_AddPwdToLabel(
		JNIEnv* env, jobject o, jlong pwd, jlong label) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		return list->AddPwdToLabel((Pwd*) pwd, *(SecureString*) label);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jboolean Java_org_caelus_kryptanandroid_core_CorePwdList_RemovePwdFromLabel(
		JNIEnv* env, jobject o, jlong pwd, jlong label) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		return list->RemovePwdFromLabel((Pwd*) pwd, *(SecureString*) label);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

}
