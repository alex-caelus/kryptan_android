/*
 * helpers.cpp
 *
 *  Created on: 30 nov 2013
 *      Author: Alexander
 */

#include "helpers.h"

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

jobject createJavaSecureStringHandler(JNIEnv* env, const Kryptan::Core::SecureString &str)
{
	jclass cls = env->FindClass("org/caelus/kryptanandroid/core/CoreSecureStringHandler");
	jmethodID constructor = env->GetMethodID(cls, "<init>", "(J)V");
	Kryptan::Core::SecureString* copy = new Kryptan::Core::SecureString(str);
	jobject obj = env->NewObject(cls, constructor, (jlong)copy);
	LOG_VERBOSE("Created new SecureString with contents: %s",  copy->getUnsecureString());
	copy->UnsecuredStringFinished();
	return obj;
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
		NewJavaException(env, "java/lang/OutOfMemoryError", rhs.what());
	} catch (const std::ios_base::failure& rhs) { //sample translation
		//translate IO C++ exception to a Java exception
		NewJavaException(env, "java/io/IOException", rhs.what());

		//TRANSLATE ANY OTHER C++ EXCEPTIONS TO JAVA EXCEPTIONS HERE
	} catch (const Kryptan::Core::KryptanBaseException& e) {
		//Translate to Java version of same
		NewJavaException(env,
				"org/caelus/kryptanandroid/core/CoreKryptanBaseException",
				e.what());
	} catch (const std::exception& e) {
		//translate unknown C++ exception to a Java exception
		NewJavaException(env, "java/lang/Error", e.what());
	} catch (...) {
		//translate unknown C++ exception to a Java exception
		NewJavaException(env, "java/lang/Error", "Unknown exception type");
	}
}
