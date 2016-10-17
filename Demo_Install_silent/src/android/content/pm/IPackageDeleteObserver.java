package android.content.pm;

public interface IPackageDeleteObserver extends android.os.IInterface {
	
	public abstract static class Stub extends android.os.Binder implements android.content.pm.IPackageDeleteObserver {
		public Stub() {
			throw new RuntimeException("Stub!");
		}

		public static android.content.pm.IPackageDeleteObserver asInterface(android.os.IBinder obj) {
			throw new RuntimeException("Stub!");
		}

		public android.os.IBinder asBinder() {
			throw new RuntimeException("Stub!");
		}

		public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
				throws android.os.RemoteException {
			throw new RuntimeException("Stub!");
		}
	}

	public abstract void packageDeleted(java.lang.String packageName, int returnCode)
			throws android.os.RemoteException;
}