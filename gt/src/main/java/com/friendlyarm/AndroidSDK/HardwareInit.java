package com.friendlyarm.AndroidSDK;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2017-12-18.
 */

public class HardwareInit {

    /**
     * 已经被选中的通信串口的设备标号
     */
    public static int DEV_COM = -1;

    /**
     * 串口数据位长度
     */
    public static final int SERIAL_PORT_DATABITS = 8;
    /**
     * 串口停止位长度
     */
    public static final int SERIAL_PORT_STOPBITS = 1;

    private static final String TAG = "SerialUtils";

    /**
     * 初始化串口
     */
    public static final void InitSerialPort(String COM_PATH, int BAUD_RATE) {
        DEV_COM = HardwareControler.openSerialPort(COM_PATH, BAUD_RATE, SERIAL_PORT_DATABITS, SERIAL_PORT_STOPBITS);
    }

    /**
     * 发送进出卡命令
     */
    public static synchronized void sendData(String data) {
        if (data == null) return;
        if (DEV_COM > 0) {
            HardwareControler.write(DEV_COM, data.getBytes(Charset.forName("gb2312")));
        }
    }

    /**
     * COM3发送数据
     *
     * @param data
     * @return
     */
    public static synchronized boolean COM3_SendData(byte[] data) {
        if (DEV_COM > 0) {
            return sendData(data, DEV_COM);
        }
        return false;
    }

    private static synchronized boolean sendData(byte[] data, int fd) {
        try {
            Log.d(TAG, "sendData = " + new String(data, "GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bs = new byte[1024];
        HardwareControler.read(fd, bs, bs.length);
        int write = HardwareControler.write(fd, data);
        if (write == data.length) {
            Log.i(TAG, "串口数据发送成功。。。");
            return true;
        } else {
            Log.i(TAG, "串口数据发送失败。。。");
            return false;
        }
    }


    /**
     * 读取COM3接收到的数据
     *
     * @return
     */
    public static synchronized byte[] COM3_RevData() {

        byte[] rec = new byte[1024];
        int len = HardwareControler.read(DEV_COM, rec, rec.length);

        if (len == 0) {
            return null;
        }
        byte[] data = new byte[len];
        System.arraycopy(rec, 0, data, 0, len);
        return data;
    }

    /**
     * COM4发送数据
     *
     * @param data
     * @return
     */
    public static synchronized boolean COM4_SendData(byte[] data) {
        if (DEV_COM > 0) {
            return sendData(data, DEV_COM);
        }
        return false;
    }
}
