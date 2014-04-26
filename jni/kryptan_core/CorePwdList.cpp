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

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J_3J(
		JNIEnv* env, jobject o, jlong p, jlongArray array) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		PwdLabelVector labels;

		int size = env->GetArrayLength(array);
		LOG_DEBUG("Filtering with %d labels%s", size, p == 0 ? "" : " and with a pattern.");
		jlong* handles = new jlong[size];
		env->GetLongArrayRegion(array, 0, size, handles);
		for (int i = 0; i < size; i++) {
			SPointer* ptr = (SPointer*) handles[i];
			labels.push_back(*ptr->sString);
		}

		PwdList::PwdVector filtered;

		if (p != 0) {
			SecureString* pattern = ((SPointer*) p)->sString;
			filtered = list->Filter(*pattern, labels);
		} else {
			filtered = list->Filter(labels);
		}

		return convertToJavaArray_fromReferences(env, filtered);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J(
		JNIEnv* env, jobject o, jlong p) {
	try {
		jlongArray l = env->NewLongArray(0);
		return Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J_3J(env,
				o, p, l);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jlongArray Java_org_caelus_kryptanandroid_core_CorePwdList_Filter___3J(
		JNIEnv* env, jobject o, jlongArray l) {
	return Java_org_caelus_kryptanandroid_core_CorePwdList_Filter__J_3J(env, o,
			0, l);
}

jlong Java_org_caelus_kryptanandroid_core_CorePwdList_CreatePwd__JJ(JNIEnv* env,
		jobject o, jlong description, jlong password) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		Pwd* pwd = list->CreatePwd(*((SPointer*) description)->sString,
				*((SPointer*) password)->sString);
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
		Pwd* pwd = list->CreatePwd(*((SPointer*) description)->sString,
				*((SPointer*) username)->sString,
				*((SPointer*) password)->sString);
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

jobjectArray Java_org_caelus_kryptanandroid_core_CorePwdList_FilterLabels(
		JNIEnv* env, jobject o, jlong p) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		SecureString* pattern = ((SPointer*) p)->sString;
		PwdLabelVector filtered = list->FilterLabels(*pattern);

		jobjectArray labels =
				(jobjectArray) env->NewObjectArray(filtered.size(),
						env->FindClass(
								"org/caelus/kryptanandroid/core/CoreSecureStringHandler"),
						createJavaSecureStringHandler(env, 0));

		for (int i = 0; i < filtered.size(); i++) {
			SPointer* ptr;
			jobject obj = createJavaSecureStringHandler(env, &ptr);

			//Copy value the resulting object to the new java reference counted SecureString object.
			ptr->sString->assign(filtered[i]);

			//Store java object in the array.
			env->SetObjectArrayElement(labels, i, obj);
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
		SPointer* ptr = (SPointer*) l;
		return (jint) list->CountPwds(*ptr->sString);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jboolean Java_org_caelus_kryptanandroid_core_CorePwdList_AddPwdToLabel(
		JNIEnv* env, jobject o, jlong pwd, jlong label) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		return list->AddPwdToLabel((Pwd*) pwd, *((SPointer*) label)->sString);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

jboolean Java_org_caelus_kryptanandroid_core_CorePwdList_RemovePwdFromLabel(
		JNIEnv* env, jobject o, jlong pwd, jlong label) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		return list->RemovePwdFromLabel((Pwd*) pwd,
				*((SPointer*) label)->sString);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
	return 0;
}

void Java_org_caelus_kryptanandroid_core_CorePwdList_ImportPwd(
		JNIEnv* env, jobject o, jlong pwd) {
	try {
		PwdList* list = getHandle<PwdList>(env, o, HANDLE_LIST);
		list->ImportPwd((Pwd*) pwd);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

}
