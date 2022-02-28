package com.pminstall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import com.udroid.content.pm.ApplicationManager;
import com.udroid.content.pm.OnFinishObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText filePathEd;
    ApplicationManager am;
    static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePathEd = (EditText) findViewById(R.id.filePath);
        filePathEd.setText("com.example.devicemanager");
        Button del = (Button) findViewById(R.id.delapk);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //String filePath = filePathEd.getText().toString().trim();
                    // install(filePath);
                    //am.uninstallApplication(filePathEd.getText().toString(), false);
                    am.deletePackage(filePathEd.getText().toString());
                    Log.d("aa", "buttonClick: " );
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("aa", "buttonClick: " + e.toString());
                }
            }
        });
        try {
            am = new ApplicationManager(this);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        am.setOnInstalledPackaged(new OnFinishObserver() {
            public void packageInstalled(String packageName, int returnCode) {
                final String pkgName = packageName;
                if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
                    Log.d("aa", "Install succeeded");
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "Install succeeded" + pkgName, Toast.LENGTH_SHORT).show();
                            filePathEd.setText(pkgName);
                        }
                    });

                } else {
                    final int retCode =returnCode;
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "Install failed"+ retCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("aa", "Install failed: " + returnCode);
                }
            }

            @Override
            public void packageDeleted(String packageName, int returnCode) {
                // TODO Auto-generated method stub
                final String pkgName = packageName;
                Log.d("aa", "delete succeeded=====================================================" + packageName);
                if (returnCode == ApplicationManager.DELETE_SUCCEEDED) {
                    Log.d("aa", "delete succeeded");
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "delete succeeded" + pkgName, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    final int retCode =returnCode;
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(MainActivity.this, "delete failed"+ retCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("aa", "delete failed: " + returnCode);
                }
            }
        });

    }
    /**
     * 读取系统属性
     * */
    public static String getSystemProperty(String key) {
        String result = null;
        // 直接使用导入进来的android.jar中的接口，如果不导入使用下面的反射方式调用
        // result = SystemProperties.get(key, null);
        try {
            Class<?> spCls = Class.forName("android.os.SystemProperties");
            Class<?>[] typeArgs = new Class[2];
            typeArgs[0] = String.class;
            typeArgs[1] = String.class;
            Constructor<?> spcs = spCls.getConstructor(null);

            Object[] valueArgs = new Object[2];
            valueArgs[0] = key;
            valueArgs[1] = null;
            Object sp = spcs.newInstance(null);

            Method method = spCls.getMethod("get", typeArgs);
            result = (String) method.invoke(sp, valueArgs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return result;
    }

    // /**
    // * 卸载apk
    // * 后台静默卸载
    // **/
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void uninstallBackground(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();

            Class IPackageInstallObserverClass = Class
                    .forName("android.content.pm.IPackageInstallObserver");
            // public abstract void deletePackage(String packageName,
            // IPackageDeleteObserver observer, int flags);
            Class[] argsClass = new Class[3];

            argsClass[0] = String.class;
            argsClass[1] = IPackageInstallObserverClass;
            argsClass[2] = int.class;

            // 参数， 在方法运行的时候需要的参数

            Class PackageManagerClass = Class
                    .forName("android.content.pm.PackageManager");

            // Field filedINSTALL_REPLACE_EXISTING =
            // PackageManagerClass.getClass().getField("INSTALL_REPLACE_EXISTING");
            Object[] params = new Object[3];

            params[0] = packageName;
            params[1] = null;
            params[2] = 1;// filedINSTALL_REPLACE_EXISTING.get(pm);//2;//

            // 获取参数 getNetworkTypeName 是你要获取的方法的名称 argsClass 是你方法的参数类型

            Method[] methods = PackageManagerClass.getMethods();
            int size = methods.length;
            for (int i = 0; i < size; i++) {
                if ("deletePackage".equals(methods[i].getName())) {
                    Method method = methods[i];
                    method.setAccessible(true);
                    method.invoke(pm, params);
                    break;
                }
            }
            // Method method = PackageManagerClass.getMethod("deletePackage",
            // argsClass);
            // method.setAccessible(true);
            // // 使用 method.invoke 来调用方法 mTelephonyManager 调用方法的对象 ，params 则就是参数
            // Log.e("dddd", packageName);
            // method.invoke(pm, params);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
            // } catch (NoSuchMethodException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // Log.e(e.toString());
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

    };

    /**
     * 静默安装apk (联迪)测试通过
     *
     * @author handsome
     * @date 2014-10-10
     * @param context
     * @param apkPath
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean silentInstall(Context context, String apkPath) {

        File file = new File(apkPath);
        if (!file.exists()) {
            Log.e(TAG, apkPath + " , 文件不存在。");
            return false;
        }

        Uri uri = Uri.fromFile(file);
        try {
            PackageManager pm = context.getPackageManager();

            Class IPackageInstallObserverClass = Class
                    .forName("android.content.pm.IPackageInstallObserver");

            // installPackage(Uri packageURI, IPackageInstallObserver
            // observer,int flags, String installerPackageName)
            Class[] argsClass = new Class[4];

            argsClass[0] = Uri.class;
            argsClass[1] = IPackageInstallObserverClass;
            argsClass[2] = int.class;
            argsClass[3] = String.class;

            // 参数， 在方法运行的时候需要的参数

            Class PackageManagerClass = Class
                    .forName("android.content.pm.PackageManager");

            // Field filedINSTALL_REPLACE_EXISTING =
            // PackageManagerClass.getClass().getField("INSTALL_REPLACE_EXISTING");
            Object[] params = new Object[4];

            params[0] = uri;
            params[1] = null;
            params[2] = 2;// filedINSTALL_REPLACE_EXISTING.get(pm);//2;//
            params[3] = null;

            // 获取参数 getNetworkTypeName 是你要获取的方法的名称 argsClass 是你方法的参数类型

            Method method = PackageManagerClass.getMethod("installPackage",
                    argsClass);
            method.setAccessible(true);
            // 使用 method.invoke 来调用方法 mTelephonyManager 调用方法的对象 ，params 则就是参数

            method.invoke(pm, params);
            return true;
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        return false;
    }
    public void buttonClick(View view) {
        String filePath = filePathEd.getText().toString().trim();
	    try {
			//String filePath = filePathEd.getText().toString().trim();
			// install(filePath);
			am.installPackage(MainActivity.this, filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("aa", "buttonClick: " + e.toString());
		}
        /*if(filePath.equals(""))
            filePath = "/data/user/0/com.chinaums.store/files/ChinaUmsStore/Download/3069.apk";
        silentInstall(this, filePath);*/
    }
    public String install(String apkAbsolutePath) {
        String[] args = { "pm", "install", "-r", apkAbsolutePath };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("aa", "eee: " + e);
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        Log.d("aa", "result: " + result);
        return result;
    }
}
