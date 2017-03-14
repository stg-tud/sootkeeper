package de.tu_darmstadt.stg.sootkeeper.flowdroid.base;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.ipc.IIPCManager;

public class FlowDroidConfig extends IAnalysisConfig {

    public static boolean DEBUG = false;
    private InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
    private int repeatCount = 1;
    private int timeout = -1;
    private int sysTimeout = -1;
    private boolean aggressiveTaintWrapper = false;
    private boolean noTaintWrapper = false;
    private String summaryPath = "";
    private String resultFilePath = "";
    private IIPCManager ipcManager;
    private String apkFile;
    private String androidJarPath;

    public InfoflowAndroidConfiguration getConfig() {
        return config;
    }

    public void setConfig(InfoflowAndroidConfiguration config) {
        this.config = config;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getSysTimeout() {
        return sysTimeout;
    }

    public void setSysTimeout(int sysTimeout) {
        this.sysTimeout = sysTimeout;
    }

    public boolean isAggressiveTaintWrapper() {
        return aggressiveTaintWrapper;
    }

    public void setAggressiveTaintWrapper(boolean aggressiveTaintWrapper) {
        this.aggressiveTaintWrapper = aggressiveTaintWrapper;
    }

    public boolean isNoTaintWrapper() {
        return noTaintWrapper;
    }

    public void setNoTaintWrapper(boolean noTaintWrapper) {
        this.noTaintWrapper = noTaintWrapper;
    }

    public String getSummaryPath() {
        return summaryPath;
    }

    public void setSummaryPath(String summaryPath) {
        this.summaryPath = summaryPath;
    }

    public String getResultFilePath() {
        return resultFilePath;
    }

    public void setResultFilePath(String resultFilePath) {
        this.resultFilePath = resultFilePath;
    }


    public IIPCManager getIpcManager() {
        return ipcManager;
    }

    public void setIpcManager(IIPCManager ipcManager) {
        this.ipcManager = ipcManager;
    }

    public String getApkFile() {
        return apkFile;
    }

    public void setApkFile(String apkFile) {
        this.apkFile = apkFile;
    }

    public String getAndroidJarPath() {
        return androidJarPath;
    }

    public void setAndroidJarPath(String androidJarPath) {
        this.androidJarPath = androidJarPath;
    }

    @Override
    public String toString() {
        return "FlowDroidConfig{" +
                "config=" + config +
                ", repeatCount=" + repeatCount +
                ", timeout=" + timeout +
                ", sysTimeout=" + sysTimeout +
                ", aggressiveTaintWrapper=" + aggressiveTaintWrapper +
                ", noTaintWrapper=" + noTaintWrapper +
                ", summaryPath='" + summaryPath + '\'' +
                ", resultFilePath='" + resultFilePath + '\'' +
                ", ipcManager=" + ipcManager +
                ", apkFile='" + apkFile + '\'' +
                ", androidJarPath='" + androidJarPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowDroidConfig that = (FlowDroidConfig) o;

        if (repeatCount != that.repeatCount) return false;
        if (timeout != that.timeout) return false;
        if (sysTimeout != that.sysTimeout) return false;
        if (aggressiveTaintWrapper != that.aggressiveTaintWrapper) return false;
        if (noTaintWrapper != that.noTaintWrapper) return false;
        if (config != null ? !config.equals(that.config) : that.config != null) return false;
        if (summaryPath != null ? !summaryPath.equals(that.summaryPath) : that.summaryPath != null) return false;
        if (resultFilePath != null ? !resultFilePath.equals(that.resultFilePath) : that.resultFilePath != null)
            return false;
        if (ipcManager != null ? !ipcManager.equals(that.ipcManager) : that.ipcManager != null) return false;
        if (apkFile != null ? !apkFile.equals(that.apkFile) : that.apkFile != null) return false;
        return androidJarPath != null ? androidJarPath.equals(that.androidJarPath) : that.androidJarPath == null;
    }

    @Override
    public int hashCode() {
        int result = config != null ? config.hashCode() : 0;
        result = 31 * result + repeatCount;
        result = 31 * result + timeout;
        result = 31 * result + sysTimeout;
        result = 31 * result + (aggressiveTaintWrapper ? 1 : 0);
        result = 31 * result + (noTaintWrapper ? 1 : 0);
        result = 31 * result + (summaryPath != null ? summaryPath.hashCode() : 0);
        result = 31 * result + (resultFilePath != null ? resultFilePath.hashCode() : 0);
        result = 31 * result + (ipcManager != null ? ipcManager.hashCode() : 0);
        result = 31 * result + (apkFile != null ? apkFile.hashCode() : 0);
        result = 31 * result + (androidJarPath != null ? androidJarPath.hashCode() : 0);
        return result;
    }
}
