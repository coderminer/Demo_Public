package com.example.demo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;

public class ApplicationManager {

	public final int INSTALL_REPLACE_EXISTING = 2;
	

	private PackageInstallObserver observer;
	private PackageDeleteObserver observerdelete;
	private PackageManager pm;
	private Method method;
	private Method uninstallmethod;
	
	private OnPackagedObserver onInstalledPackaged;
	
    class PackageInstallObserver extends IPackageInstallObserver.Stub {

		public void packageInstalled(String packageName, int returnCode) throws RemoteException {
			if (onInstalledPackaged != null) {
				onInstalledPackaged.packageInstalled(packageName, returnCode);
			}
		}
	}
    
    class PackageDeleteObserver extends IPackageDeleteObserver.Stub { 

		public void packageDeleted(String packageName, int returnCode) throws RemoteException {
			if (onInstalledPackaged != null) {
				onInstalledPackaged.packageDeleted(packageName, returnCode);
			}
		}
	}
	 
	public ApplicationManager(Context context) throws SecurityException, NoSuchMethodException {
		
        observer = new PackageInstallObserver();
        observerdelete = new PackageDeleteObserver(); 
        pm = context.getPackageManager();
        
        Class<?>[] types = new Class[] {Uri.class, IPackageInstallObserver.class, int.class, String.class};
        Class<?>[] uninstalltypes = new Class[] {String.class, IPackageDeleteObserver.class, int.class};
        
		method = pm.getClass().getMethod("installPackage", types);
		uninstallmethod = pm.getClass().getMethod("deletePackage", uninstalltypes);
	}
	
	public void setOnPackagedObserver(OnPackagedObserver onInstalledPackaged) {
		this.onInstalledPackaged = onInstalledPackaged; 
	}
	

	
	public void uninstallPackage(String packagename) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        uninstallmethod.invoke(pm, new Object[] {packagename, observerdelete, 0});
    }
	public void installPackage(String apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		installPackage(new File(apkFile));
	}
	
	public void installPackage(File apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (!apkFile.exists()) throw new IllegalArgumentException();
		Uri packageURI = Uri.fromFile(apkFile);
		installPackage(packageURI);
	}
	
	public void installPackage(Uri apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		method.invoke(pm, new Object[] {apkFile, observer, INSTALL_REPLACE_EXISTING, null});
	}
	
}
