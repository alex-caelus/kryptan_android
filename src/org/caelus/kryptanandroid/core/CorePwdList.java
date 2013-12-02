package org.caelus.kryptanandroid.core;

import java.util.Vector;

public class CorePwdList
{

	private long nativeHandle;

	public CorePwdList(long nativeHandle)
	{
		this.nativeHandle = nativeHandle;
	}

	public Vector<CorePwd> filter(CoreSecureStringHandler pattern)
	{
		long[] handles = Filter(pattern.getNativeHandle());
		Vector<CorePwd> pwds = new Vector<CorePwd>();

		for (long handle : handles)
		{
			pwds.add(new CorePwd(handle));
		}

		return pwds;
	}

	public Vector<CorePwd> filter(Vector<CoreSecureStringHandler> labels)
	{
		long[] lHandles = new long[labels.size()];
		int i = 0;
		for (CoreSecureStringHandler label : labels)
		{
			lHandles[i] = label.getNativeHandle();
		}

		long[] handles = Filter(lHandles);

		Vector<CorePwd> pwds = new Vector<CorePwd>();

		for (long handle : handles)
		{
			pwds.add(new CorePwd(handle));
		}

		return pwds;
	}

	public Vector<CorePwd> filter(CoreSecureStringHandler pattern,
			Vector<CoreSecureStringHandler> labels)
	{
		long[] lHandles = new long[labels.size()];
		int i = 0;
		for (CoreSecureStringHandler label : labels)
		{
			lHandles[i] = label.getNativeHandle();
		}

		long[] handles = Filter(pattern.getNativeHandle(), lHandles);

		Vector<CorePwd> pwds = new Vector<CorePwd>();

		for (long handle : handles)
		{
			pwds.add(new CorePwd(handle));
		}

		return pwds;
	}

	public CorePwd createPwd(CoreSecureStringHandler description,
			CoreSecureStringHandler password)
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	public CorePwd createPwd(CoreSecureStringHandler description,
			CoreSecureStringHandler username, CoreSecureStringHandler password)
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	public void deletePwd(CorePwd pwd)
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}
	
	public Vector<CoreSecureStringHandler> allLabels()
	{
		Vector<CoreSecureStringHandler> allLabels = new Vector<CoreSecureStringHandler>();
	}
	
	public Vector<CoreSecureStringHandler> filterLabels()
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	public native int CountPwds();
	
	public int CountPwds(CoreSecureStringHandler label)
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}
	
	public boolean addPwdToLabel(CorePwd pwd, CoreSecureStringHandler label)
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}
	
	public boolean removePwdFromLabel(CorePwd pwd, CoreSecureStringHandler label)
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}
	
	private native long[] All();

	private native long[] Filter(long pattern);

	private native long[] Filter(long[] labels);

	private native long[] Filter(long pattern, long[] labels);

	private native long CreatePwd(long description, long password);

	private native long CreatePwd(long description, long username, long password);

	private native void DeletePwd(long pwd);

	private native long[] AllLabels();

	private native long[] FilterLabels(long pattern);

	private native int CountPwds(long label);

	private native boolean AddPwdToLabel(long pwd, long label);

	private native boolean RemovePwdFromLabel(long pwd, long label);

}
