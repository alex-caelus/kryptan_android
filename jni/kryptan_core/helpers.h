/*
 * handle.h
 *
 *  Created on: 29 nov 2013
 *      Author: Alexander
 */

#ifndef HELPERS_H_
#define HELPERS_H_

#include <jni.h>
#include <vector>
#include <ios>
#include "kryptan_core/Exceptions.h"

jfieldID getHandleField(JNIEnv *env, jobject obj, const char* name);

template<typename T>
T *getHandle(JNIEnv *env, jobject obj, const char* fieldName) {
	jlong handle = env->GetLongField(obj, getHandleField(env, obj, fieldName));
	return reinterpret_cast<T *>(handle);
}

template<typename T>
void setHandle(JNIEnv *env, jobject obj, T *t, const char* fieldName) {
	jlong handle = reinterpret_cast<jlong>(t);
	env->SetLongField(obj, getHandleField(env, obj, fieldName), handle);
}

void GetJStringContent(JNIEnv *AEnv, jstring AStr, std::string &ARes);

//----------------JAVA EXCEPTION HANDLING-----------------

//We need a function to swallow C++ exceptions and replace them with Java exceptions
void swallow_cpp_exception_and_throw_java(JNIEnv *env);


//-----------------OTHER HELPERS--------------------------

template<class T>
jlongArray convertToJavaArray(JNIEnv* env, std::vector<T*> vector)
{
	int size = vector.size();
	jlong* array = new jlong[size];

	for(int i=0; i != size; i++)
	{
		array[i] = (jlong)vector[i];
	}

	jlongArray toReturn = env->NewLongArray(size);
	env->SetLongArrayRegion(toReturn, 0, size, array);

	delete[] array;

	return toReturn;
}

template<class T>
std::vector<T> convertToVectorFromJavaArray_value(JNIEnv* env, jlongArray array)
{
	int size = env->GetArrayLength(array);
	jlong* handles = new jlong[size];
	env->GetLongArrayRegion(array, 0, size, handles );

	std::vector<T> v = new std::vector<T>();

	for(int i=0; i < size; i++)
	{
		v.push_back(*((T)handles[i]));
	}

	return v;
}

template<class T>
std::vector<T*> convertToVectorFromJavaArray_pointer(JNIEnv* env, jlongArray array)
{
	int size = env->GetArrayLength(array);
	jlong* handles = new jlong[size];
	env->GetLongArrayRegion(array, 0, size, handles );

	std::vector<T*> v = new std::vector<T*>();

	for(int i=0; i < size; i++)
	{
		v.push_back(handles[i]);
	}

	return v;
}

#endif /* HELPERS_H_ */
