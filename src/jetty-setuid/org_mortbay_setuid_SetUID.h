/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_mortbay_setuid_SetUID */

#ifndef _Included_org_mortbay_setuid_SetUID
#define _Included_org_mortbay_setuid_SetUID
#ifdef __cplusplus
extern "C" {
#endif
#undef org_mortbay_setuid_SetUID_OK
#define org_mortbay_setuid_SetUID_OK 0L
#undef org_mortbay_setuid_SetUID_ERROR
#define org_mortbay_setuid_SetUID_ERROR -1L

JNIEXPORT jint JNICALL Java_org_mortbay_setuid_SetUID_setuid
  (JNIEnv *, jclass, jint);

JNIEXPORT jint JNICALL Java_org_mortbay_setuid_SetUID_setumask
  (JNIEnv *, jclass, jint);

JNIEXPORT jint JNICALL Java_org_mortbay_setuid_SetUID_setgid
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
