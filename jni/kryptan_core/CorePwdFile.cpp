#include <jni.h>
#include <string>
#include "kryptan_core/PwdFile.h"
#include "helpers.h"

using namespace Kryptan::Core;

#define HANDLE_FILE "nativeHandle"
#define HANDLE_MASTERKEY "nativeMasterKeyHandle"

extern "C" {
jlong Java_org_caelus_kryptanandroid_core_CorePwdFile_CreateInstance(
		JNIEnv* env, jobject o, jstring filename) {
	try {
		std::string fname;
		GetJStringContent(env, filename, fname);
		jlong ptr = (jlong) (new PwdFile(fname));
		return ptr;
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

void Java_org_caelus_kryptanandroid_core_CorePwdFile_Dispose(JNIEnv* env,
		jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);
		SecureString* masterkey = getHandle<SecureString>(env, o,
				HANDLE_MASTERKEY);

		delete masterkey;
		delete file;

		setHandle<PwdFile>(env, o, 0, HANDLE_FILE);
		setHandle<SecureString>(env, o, 0, HANDLE_MASTERKEY);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

void Java_org_caelus_kryptanandroid_core_CorePwdFile_CreateNew(JNIEnv* env,
		jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);

		file->CreateNew();
	} catch(const KryptanDecryptWrongKeyException& e)
	{
		//just ignore this exception and let java figure it out by calling isOpen
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

void Java_org_caelus_kryptanandroid_core_CorePwdFile_TryOpenAndParse(
		JNIEnv* env, jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);
		file->OpenAndParse(SecureString(), false);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

void Java_org_caelus_kryptanandroid_core_CorePwdFile_Save(JNIEnv* env,
		jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);
		SecureString* masterkey = getHandle<SecureString>(env, o,
				HANDLE_MASTERKEY);
		file->Save(*masterkey);
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

jlong Java_org_caelus_kryptanandroid_core_CorePwdFile_GetPasswordListHandle(
		JNIEnv* env, jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);
		return (jlong) file->GetPasswordList();
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

jstring Java_org_caelus_kryptanandroid_core_CorePwdFile_GetFilename(JNIEnv* env,
		jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);

		std::string filename = file->GetFilename();

		return env->NewStringUTF(filename.c_str());
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

jboolean Java_org_caelus_kryptanandroid_core_CorePwdFile_IsOpen(JNIEnv* env,
		jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);
		return (jboolean) file->IsOpen();
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

jboolean Java_org_caelus_kryptanandroid_core_CorePwdFile_Exists(JNIEnv* env,
		jobject o) {
	try {
		PwdFile* file = getHandle<PwdFile>(env, o, HANDLE_FILE);
		return (jboolean) file->Exists();
	} catch (...) {
		swallow_cpp_exception_and_throw_java(env);
	}
}

}
