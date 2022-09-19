package com.friendlyarm.AndroidSDK;
import android.util.Log;

public class HardwareControler
{
	/* File */
	static public native int open(String devName, int flags);
	static public native int write(int fd, byte[] data);
	static public native int read(int fd, byte[] buf, int len);
	static public native int select(int fd, int sec, int usec);
	static public native void close(int fd);
    static public native int ioctlWithIntValue(int fd, int cmd, int value);
    
	/* Serial Port */
	static public native int openSerialPort(String devName, long baud, int dataBits, int stopBits );
    static public native int openSerialPortEx( String devName
    		, long baud
    		, int dataBits
    		, int stopBits
    		, String parityBit
    		, String flowCtrl
    		);
	
	/* LED */
	static public native int setLedState( int ledID, int ledState );
    
    /* PWM */
	static public native int PWMPlay(int frequency);
	static public native int PWMStop();
    
    /* ADC */
	static public native int readADC();
	static public native int readADCWithChannel(int channel);
    static public native int[] readADCWithChannels(int[] channels);
    
    /* I2C */
    static public native int setI2CSlave(int fd, int slave);
    static public native int setI2CTimeout(int fd, int timeout);
    static public native int setI2CRetries(int fd, int retries);
	static public native int writeByteToI2C(int fd, int pos, byte byteData, int wait_ms);
	static public native int readByteFromI2C(int fd, int pos, int wait_ms);
    
    /* SPI */
    static public native int setSPIWriteBitsPerWord( int spi_fd, int bits );
    static public native int setSPIReadBitsPerWord( int spi_fd, int bits );
    static public native int setSPIBitOrder( int spi_fd, int order);
    static public native int setSPIClockDivider( int spi_fd, int divider);
    static public native int setSPIDataMode( int spi_fd, int mode);
    static public native int SPItransferOneByte( int spi_fd, byte byteData, int spi_delay, int spi_speed, int spi_bits);
    static public native int SPItransferBytes(int spi_fd, byte[] writeData, byte[] readBuff, int spi_delay, int spi_speed, int spi_bits);    
    static public native int writeBytesToSPI(int spi_fd, byte[] writeData, int spi_delay, int spi_speed, int spi_bits);
    static public native int readBytesFromSPI(int spi_fd, byte[] readBuff, int spi_delay, int spi_speed, int spi_bits);
    
    /* GPIO */
    static public native int exportGPIOPin(int pin);
    static public native int unexportGPIOPin(int pin);
    //GPIOEnum.LOW or GPIOEnum.HIGH
    static public native int setGPIOValue(int pin, int value);
    static public native int getGPIOValue(int pin);
    //GPIOEnum.IN or GPIOEnum.OUT
    static public native int setGPIODirection(int pin, int direction);
    static public native int getGPIODirection(int pin);

	/* OldInterface: for EEPROM */
	static public native int openI2CDevice();
	static public native int writeByteDataToI2C(int fd, int pos, byte byteData);
	static public native int readByteDataFromI2C(int fd, int pos);
    
    /* return 6410 or 210 or 4412 */
    static public native int getBoardType();
    
    static {
        try {//friendlyarm
        	System.loadLibrary("friendlyarm-hardware");
        } catch (UnsatisfiedLinkError e) {
            Log.d("GT_i", "libfriendlyarm-hardware library not found!");
        }
    }
}
