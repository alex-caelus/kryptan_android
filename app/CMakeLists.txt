# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             kryptan_core

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
                src/main/jni/kryptan_core/CorePwd.cpp
                src/main/jni/kryptan_core/CorePwdFile.cpp
                src/main/jni/kryptan_core/CorePwdList.cpp
                src/main/jni/kryptan_core/CoreSecureStringHandler.cpp
                src/main/jni/kryptan_core/cryptopp/3way.cpp
                src/main/jni/kryptan_core/cryptopp/adler32.cpp
                src/main/jni/kryptan_core/cryptopp/algebra.cpp
                src/main/jni/kryptan_core/cryptopp/algparam.cpp
                src/main/jni/kryptan_core/cryptopp/arc4.cpp
                src/main/jni/kryptan_core/cryptopp/asn.cpp
                src/main/jni/kryptan_core/cryptopp/authenc.cpp
                src/main/jni/kryptan_core/cryptopp/base32.cpp
                src/main/jni/kryptan_core/cryptopp/base64.cpp
                src/main/jni/kryptan_core/cryptopp/basecode.cpp
                src/main/jni/kryptan_core/cryptopp/bfinit.cpp
                src/main/jni/kryptan_core/cryptopp/blowfish.cpp
                src/main/jni/kryptan_core/cryptopp/blumshub.cpp
                src/main/jni/kryptan_core/cryptopp/camellia.cpp
                src/main/jni/kryptan_core/cryptopp/cast.cpp
                src/main/jni/kryptan_core/cryptopp/casts.cpp
                src/main/jni/kryptan_core/cryptopp/cbcmac.cpp
                src/main/jni/kryptan_core/cryptopp/ccm.cpp
                src/main/jni/kryptan_core/cryptopp/channels.cpp
                src/main/jni/kryptan_core/cryptopp/cmac.cpp
                src/main/jni/kryptan_core/cryptopp/cpu.cpp
                src/main/jni/kryptan_core/cryptopp/crc.cpp
                src/main/jni/kryptan_core/cryptopp/cryptlib.cpp
                src/main/jni/kryptan_core/cryptopp/cryptlib_bds.cpp
                src/main/jni/kryptan_core/cryptopp/default.cpp
                src/main/jni/kryptan_core/cryptopp/des.cpp
                src/main/jni/kryptan_core/cryptopp/dessp.cpp
                src/main/jni/kryptan_core/cryptopp/dh.cpp
                src/main/jni/kryptan_core/cryptopp/dh2.cpp
                src/main/jni/kryptan_core/cryptopp/dll.cpp
                src/main/jni/kryptan_core/cryptopp/dsa.cpp
                src/main/jni/kryptan_core/cryptopp/eax.cpp
                src/main/jni/kryptan_core/cryptopp/ec2n.cpp
                src/main/jni/kryptan_core/cryptopp/eccrypto.cpp
                src/main/jni/kryptan_core/cryptopp/ecp.cpp
                src/main/jni/kryptan_core/cryptopp/elgamal.cpp
                src/main/jni/kryptan_core/cryptopp/emsa2.cpp
                src/main/jni/kryptan_core/cryptopp/eprecomp.cpp
                src/main/jni/kryptan_core/cryptopp/esign.cpp
                src/main/jni/kryptan_core/cryptopp/files.cpp
                src/main/jni/kryptan_core/cryptopp/filters.cpp
                src/main/jni/kryptan_core/cryptopp/fips140.cpp
                src/main/jni/kryptan_core/cryptopp/fipstest.cpp
                src/main/jni/kryptan_core/cryptopp/gcm.cpp
                src/main/jni/kryptan_core/cryptopp/gf256.cpp
                src/main/jni/kryptan_core/cryptopp/gf2n.cpp
                src/main/jni/kryptan_core/cryptopp/gf2_32.cpp
                src/main/jni/kryptan_core/cryptopp/gfpcrypt.cpp
                src/main/jni/kryptan_core/cryptopp/gost.cpp
                src/main/jni/kryptan_core/cryptopp/gzip.cpp
                src/main/jni/kryptan_core/cryptopp/hex.cpp
                src/main/jni/kryptan_core/cryptopp/hmac.cpp
                src/main/jni/kryptan_core/cryptopp/hrtimer.cpp
                src/main/jni/kryptan_core/cryptopp/ida.cpp
                src/main/jni/kryptan_core/cryptopp/idea.cpp
                src/main/jni/kryptan_core/cryptopp/integer.cpp
                src/main/jni/kryptan_core/cryptopp/iterhash.cpp
                src/main/jni/kryptan_core/cryptopp/luc.cpp
                src/main/jni/kryptan_core/cryptopp/mars.cpp
                src/main/jni/kryptan_core/cryptopp/marss.cpp
                src/main/jni/kryptan_core/cryptopp/md2.cpp
                src/main/jni/kryptan_core/cryptopp/md4.cpp
                src/main/jni/kryptan_core/cryptopp/md5.cpp
                src/main/jni/kryptan_core/cryptopp/misc.cpp
                src/main/jni/kryptan_core/cryptopp/modes.cpp
                src/main/jni/kryptan_core/cryptopp/mqueue.cpp
                src/main/jni/kryptan_core/cryptopp/mqv.cpp
                src/main/jni/kryptan_core/cryptopp/nbtheory.cpp
                src/main/jni/kryptan_core/cryptopp/oaep.cpp
                src/main/jni/kryptan_core/cryptopp/osrng.cpp
                src/main/jni/kryptan_core/cryptopp/panama.cpp
                src/main/jni/kryptan_core/cryptopp/pch.cpp
                src/main/jni/kryptan_core/cryptopp/pkcspad.cpp
                src/main/jni/kryptan_core/cryptopp/polynomi.cpp
                src/main/jni/kryptan_core/cryptopp/pssr.cpp
                src/main/jni/kryptan_core/cryptopp/pubkey.cpp
                src/main/jni/kryptan_core/cryptopp/queue.cpp
                src/main/jni/kryptan_core/cryptopp/rabin.cpp
                src/main/jni/kryptan_core/cryptopp/randpool.cpp
                src/main/jni/kryptan_core/cryptopp/rc2.cpp
                src/main/jni/kryptan_core/cryptopp/rc5.cpp
                src/main/jni/kryptan_core/cryptopp/rc6.cpp
                src/main/jni/kryptan_core/cryptopp/rdtables.cpp
                src/main/jni/kryptan_core/cryptopp/rijndael.cpp
                src/main/jni/kryptan_core/cryptopp/ripemd.cpp
                src/main/jni/kryptan_core/cryptopp/rng.cpp
                src/main/jni/kryptan_core/cryptopp/rsa.cpp
                src/main/jni/kryptan_core/cryptopp/rw.cpp
                src/main/jni/kryptan_core/cryptopp/safer.cpp
                src/main/jni/kryptan_core/cryptopp/salsa.cpp
                src/main/jni/kryptan_core/cryptopp/seal.cpp
                src/main/jni/kryptan_core/cryptopp/seed.cpp
                src/main/jni/kryptan_core/cryptopp/serpent.cpp
                src/main/jni/kryptan_core/cryptopp/sha.cpp
                src/main/jni/kryptan_core/cryptopp/sha3.cpp
                src/main/jni/kryptan_core/cryptopp/shacal2.cpp
                src/main/jni/kryptan_core/cryptopp/shark.cpp
                src/main/jni/kryptan_core/cryptopp/sharkbox.cpp
                src/main/jni/kryptan_core/cryptopp/simple.cpp
                src/main/jni/kryptan_core/cryptopp/skipjack.cpp
                src/main/jni/kryptan_core/cryptopp/socketft.cpp
                src/main/jni/kryptan_core/cryptopp/sosemanuk.cpp
                src/main/jni/kryptan_core/cryptopp/square.cpp
                src/main/jni/kryptan_core/cryptopp/squaretb.cpp
                src/main/jni/kryptan_core/cryptopp/strciphr.cpp
                src/main/jni/kryptan_core/cryptopp/tea.cpp
                src/main/jni/kryptan_core/cryptopp/tftables.cpp
                src/main/jni/kryptan_core/cryptopp/tiger.cpp
                src/main/jni/kryptan_core/cryptopp/tigertab.cpp
                src/main/jni/kryptan_core/cryptopp/trdlocal.cpp
                src/main/jni/kryptan_core/cryptopp/ttmac.cpp
                src/main/jni/kryptan_core/cryptopp/twofish.cpp
                src/main/jni/kryptan_core/cryptopp/vmac.cpp
                src/main/jni/kryptan_core/cryptopp/wait.cpp
                #src/main/jni/kryptan_core/cryptopp/wake.cpp
                src/main/jni/kryptan_core/cryptopp/whrlpool.cpp
                src/main/jni/kryptan_core/cryptopp/winpipes.cpp
                src/main/jni/kryptan_core/cryptopp/xtr.cpp
                src/main/jni/kryptan_core/cryptopp/xtrcrypt.cpp
                src/main/jni/kryptan_core/cryptopp/zdeflate.cpp
                src/main/jni/kryptan_core/cryptopp/zinflate.cpp
                src/main/jni/kryptan_core/cryptopp/zlib.cpp
                src/main/jni/kryptan_core/helpers.cpp
                src/main/jni/kryptan_core/kryptan_core/ModifiedEncryptor.cpp
                src/main/jni/kryptan_core/kryptan_core/Pwd.cpp
                src/main/jni/kryptan_core/kryptan_core/PwdFile.cpp
                src/main/jni/kryptan_core/kryptan_core/PwdFileWorker.cpp
                src/main/jni/kryptan_core/kryptan_core/PwdList.cpp
                src/main/jni/kryptan_core/kryptan_core/SecureString/SecureString.cpp
                src/main/jni/kryptan_core/kryptan_core/SerpentEncryptor.cpp
                src/main/jni/kryptan_core/kryptan_core/Server.cpp )

# Specifies a path to native header files.
include_directories(
                src/main/jni/kryptan_core
                src/main/jni/kryptan_core/kryptan_core/
                src/main/jni/kryptan_core/kryptan_core/SecureString/
                src/main/jni/kryptan_core/cryptopp/ )