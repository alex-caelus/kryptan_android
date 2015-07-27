/*
 * helpers.cpp
 *
 *  Created on: 30 nov 2013
 *      Author: Alexander
 */

#include "helpers.h"

using namespace Kryptan::Core;
using namespace Caelus::Utilities;

//-------------Get obejct pointers-------------------------

jfieldID getHandleField(JNIEnv *env, jobject obj, const char* name) {
	jclass c = env->GetObjectClass(obj);
	// J is the type signature for long:
	return env->GetFieldID(c, name, "J");
}

void GetJStringContent(JNIEnv *AEnv, jstring AStr, std::string &ARes) {
	if (!AStr) {
		ARes.clear();
		return;
	}

	const char *s = AEnv->GetStringUTFChars(AStr, 0);
	ARes = s;
	AEnv->ReleaseStringUTFChars(AStr, s);
}

//----------------SecureString helpers--------------------

jobject createJavaSecureStringHandler(JNIEnv* env, SPointer** out) {
	SPointer* ref = new SPointer();
	ref->sString = new SecureString();
	ref->nReferences = 0;

	LOG_DEBUG(
			"Created new SecureString with address %lld, SPointer address %lld",
			(jlong)ref->sString, (jlong) ref);

	jclass cls = env->FindClass(
			"org/caelus/kryptanandroid/core/CoreSecureStringHandler");
	jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
	jobject handler = env->NewObject(cls, constructor, (jlong) ref);

	if(out != 0)
	{
		*out = ref;
	}

	LOG_DEBUG("%s", "Created new SecureString and handler java object.");
	return handler;
}

//----------------JAVA EXCEPTION HANDLING-----------------
//This is how we represent a Java exception already in progress
struct ThrownJavaException: std::runtime_error {
	ThrownJavaException() :
			std::runtime_error("") {
	}
	ThrownJavaException(const std::string& msg) :
			std::runtime_error(msg) {
	}
};
inline void assert_no_exception(JNIEnv * env) {
	if (env->ExceptionCheck() == JNI_FALSE)
		throw ThrownJavaException("assert_no_exception");
}

//used to throw a new Java exception. use full paths like:
//"java/lang/NoSuchFieldException"
//"java/lang/NullPointerException"
//"java/security/InvalidParameterException"
struct NewJavaException: public ThrownJavaException {
	NewJavaException(JNIEnv * env, const char* type = "", const char* message =
			"") :
			ThrownJavaException(type + std::string(" ") + message) {
		jclass newExcCls = env->FindClass(type);
		if (newExcCls != NULL)
			env->ThrowNew(newExcCls, message);
		//if it is null, a NoClassDefFoundError was already thrown
	}
};

//We also need a function to swallow C++ exceptions and replace them with Java exceptions
void swallow_cpp_exception_and_throw_java(JNIEnv *env) {
	try {
		throw;
	} catch (const ThrownJavaException&) {
		//already reported to Java, ignore
	} catch (const std::bad_alloc& rhs) {
		//translate OOM C++ exception to a Java exception
		LOG_ERROR("Error: Out of memory error occurred, message is: %s",
				rhs.what());
		NewJavaException(env, "java/lang/OutOfMemoryError", rhs.what());
	} catch (const std::ios_base::failure& rhs) { //sample translation
		//translate IO C++ exception to a Java exception
		LOG_ERROR("Error: IO Exception occured, message: %s", rhs.what());
		NewJavaException(env, "java/io/IOException", rhs.what());
		//TRANSLATE ANY OTHER C++ EXCEPTIONS TO JAVA EXCEPTIONS HERE
	} catch (const Kryptan::Core::KryptanBaseException& e) {
		//Translate to Java version of same
		LOG_ERROR("Error: A Kryptan::Core exception occurred, message: %s",
				e.what());
		NewJavaException(env,
				"org/caelus/kryptanandroid/core/CoreKryptanBaseException",
				e.what());
	} catch (const std::exception& e) {
		//translate unknown C++ exception to a Java exception
		LOG_ERROR("Error: unexpected c++ exception occurred, message: %s",
				e.what());
		NewJavaException(env, "java/lang/Error", e.what());
	} catch (...) {
		//translate unknown C++ exception to a Java exception
		LOG_ERROR("%s",
				"Error: unexpected c++ exception occurred, unknown type and message.");
		NewJavaException(env, "java/lang/Error", "Unknown exception type");
	}
}
