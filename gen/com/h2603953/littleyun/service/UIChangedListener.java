/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\littleyun\\20171111\\LittleYunMusic\\src\\com\\h2603953\\littleyun\\service\\UIChangedListener.aidl
 */
package com.h2603953.littleyun.service;
public interface UIChangedListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.h2603953.littleyun.service.UIChangedListener
{
private static final java.lang.String DESCRIPTOR = "com.h2603953.littleyun.service.UIChangedListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.h2603953.littleyun.service.UIChangedListener interface,
 * generating a proxy if needed.
 */
public static com.h2603953.littleyun.service.UIChangedListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.h2603953.littleyun.service.UIChangedListener))) {
return ((com.h2603953.littleyun.service.UIChangedListener)iin);
}
return new com.h2603953.littleyun.service.UIChangedListener.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onBufferingUpdate:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onBufferingUpdate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onMusicStart:
{
data.enforceInterface(DESCRIPTOR);
this.onMusicStart();
reply.writeNoException();
return true;
}
case TRANSACTION_onMusicPause:
{
data.enforceInterface(DESCRIPTOR);
this.onMusicPause();
reply.writeNoException();
return true;
}
case TRANSACTION_onDeleteAll:
{
data.enforceInterface(DESCRIPTOR);
this.onDeleteAll();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.h2603953.littleyun.service.UIChangedListener
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void onBufferingUpdate(int percent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(percent);
mRemote.transact(Stub.TRANSACTION_onBufferingUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onMusicStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onMusicStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onMusicPause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onMusicPause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onDeleteAll() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onDeleteAll, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onBufferingUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onMusicStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onMusicPause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onDeleteAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void onBufferingUpdate(int percent) throws android.os.RemoteException;
public void onMusicStart() throws android.os.RemoteException;
public void onMusicPause() throws android.os.RemoteException;
public void onDeleteAll() throws android.os.RemoteException;
}
