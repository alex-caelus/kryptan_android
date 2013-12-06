package org.caelus.kryptanandroid.core;

import java.util.ArrayList;

public class CorePwdList
{

	private long nativeHandle;

	public CorePwdList(long nativeHandle)
	{
		this.nativeHandle = nativeHandle;
	}

	public ArrayList<CorePwd> all()
	{
		long[] handles = All();
		ArrayList<CorePwd> pwds = new ArrayList<CorePwd>();

		for (long handle : handles)
		{
			pwds.add(new CorePwd(handle));
		}

		return pwds;
	}

	public ArrayList<CorePwd> filter(CoreSecureStringHandler pattern)
	{
		long[] handles = Filter(pattern.getNativeHandle());
		ArrayList<CorePwd> pwds = new ArrayList<CorePwd>();

		for (long handle : handles)
		{
			pwds.add(new CorePwd(handle));
		}

		return pwds;
	}

	public ArrayList<CorePwd> filter(CoreSecureStringHandlerCollection labels)
	{
		long[] lHandles = new long[labels.getContainer().size()];
		int i = 0;
		for (CoreSecureStringHandler label : labels.getContainer())
		{
			lHandles[i] = label.getNativeHandle();
			i++;
		}

		long[] handles = Filter(lHandles);

		ArrayList<CorePwd> pwds = new ArrayList<CorePwd>();

		for (long handle : handles)
		{
			pwds.add(new CorePwd(handle));
		}

		return pwds;
	}

	public ArrayList<CorePwd> filter(CoreSecureStringHandler pattern,
			CoreSecureStringHandlerCollection labels)
	{
		long[] lHandles = new long[labels.getContainer().size()];
		int i = 0;
		for (CoreSecureStringHandler label : labels.getContainer())
		{
			lHandles[i] = label.getNativeHandle();
		}

		long[] handles = Filter(pattern.getNativeHandle(), lHandles);

		ArrayList<CorePwd> pwds = new ArrayList<CorePwd>();

		for (long handle : handles)
		{
			pwds.add(new CorePwd(handle));
		}

		return pwds;
	}

	public CorePwd createPwd(CoreSecureStringHandler description,
			CoreSecureStringHandler password)
	{
		long handle = CreatePwd(description.getNativeHandle(), password.getNativeHandle());
		return new CorePwd(handle);
	}

	public CorePwd createPwd(CoreSecureStringHandler description,
			CoreSecureStringHandler username, CoreSecureStringHandler password)
	{
		long handle = CreatePwd(description.getNativeHandle(), username.getNativeHandle(), password.getNativeHandle());
		return new CorePwd(handle);
	}

	public void deletePwd(CorePwd pwd)
	{
		DeletePwd(pwd.getNativeHandle());
	}
	
//	public Vector<CoreSecureStringHandler> allLabels()
//	{
//		Vector<CoreSecureStringHandler> allLabels = new Vector<CoreSecureStringHandler>();
//		long[] ptrs = this.AllLabels();
//		for(long ptr : ptrs)
//		{
//			allLabels.add(new CoreSecureStringHandler(ptr));
//		}
//		return allLabels;
//	}
//	
//	public Vector<CoreSecureStringHandler> filterLabels(CoreSecureStringHandler pattern)
//	{
//		Vector<CoreSecureStringHandler> allLabels = new Vector<CoreSecureStringHandler>();
//		long[] ptrs = this.FilterLabels(pattern.getNativeHandle());
//		for(long ptr : ptrs)
//		{
//			allLabels.add(new CoreSecureStringHandler(ptr));
//		}
//		return allLabels;
//	}

	public native int CountPwds();
	
	public int CountPwds(CoreSecureStringHandler label)
	{
		return CountPwds(label.getNativeHandle());
	}
	
	public boolean addPwdToLabel(CorePwd pwd, CoreSecureStringHandler label)
	{
		return AddPwdToLabel(pwd.getNativeHandle(), label.getNativeHandle());
	}
	
	public boolean removePwdFromLabel(CorePwd pwd, CoreSecureStringHandler label)
	{
		return RemovePwdFromLabel(pwd.getNativeHandle(), label.getNativeHandle());
	}

	public native CoreSecureStringHandler[] AllLabels();

	public native CoreSecureStringHandler[] FilterLabels(long pattern);
	
	private native long[] All();

	private native long[] Filter(long pattern);

	private native long[] Filter(long[] labels);

	private native long[] Filter(long pattern, long[] labels);

	private native long CreatePwd(long description, long password);

	private native long CreatePwd(long description, long username, long password);

	private native void DeletePwd(long pwd);

	private native int CountPwds(long label);

	private native boolean AddPwdToLabel(long pwd, long label);

	private native boolean RemovePwdFromLabel(long pwd, long label);

}
