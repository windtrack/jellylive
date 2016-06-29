package com.oo58.jelly.socket;




import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class BigEndianUtils {

  /**
   * provide big endian utils
   */

  // ========================================== Swapping read/write routines
  /**
   * Writes a "short" value to a byte array at a given offset.
   *
   * @param data   target byte array
   * @param offset starting offset in the byte array
   * @param value  value to write
   */
  public static void writeShort(byte[] data, int offset, short value) {
    data[offset] = (byte) ((value >> 8) & 0xff);
    data[offset + 1] = (byte) (value & 0xff);
  }

  /**
   * Reads a "short" value from a byte array at a given offset.
   *
   * @param data   source byte array
   * @param offset starting offset in the byte array
   * @return the value read
   */
  public static short readShort(byte[] data, int offset) {
    return (short) (((data[offset] & 0xff) << 8) + ((data[offset + 1] & 0xff)));
  }

  /**
   * Writes a "int" value to a byte array at a given offset.
   *
   * @param data   target byte array
   * @param offset starting offset in the byte array
   * @param value  value to write
   */
  public static void writeInteger(byte[] data, int offset, int value) {
    data[offset + 0] = (byte) ((value >> 24) & 0xff);
    data[offset + 1] = (byte) ((value >> 16) & 0xff);
    data[offset + 2] = (byte) ((value >> 8) & 0xff);
    data[offset + 3] = (byte) (value & 0xff);
  }

  /**
   * Reads a "int" value from a byte array at a given offset.
   *
   * @param data   source byte array
   * @param offset starting offset in the byte array
   * @return the value read
   */
  public static int readInteger(byte[] data, int offset) {
    return (int) (((data[offset + 0] & 0xff) << 24) + ((data[offset + 1] & 0xff) << 16) + ((data[offset + 2] & 0xff) << 8) + (data[offset + 3] & 0xff));
  }

  /**
   * Writes a "long" value to a byte array at a given offset.
   *
   * @param data   target byte array
   * @param offset starting offset in the byte array
   * @param value  value to write
   */
  public static void writeLong(byte[] data, int offset, long value) {
    data[offset + 0] = (byte) ((value >> 56) & 0xff);
    data[offset + 1] = (byte) ((value >> 48) & 0xff);
    data[offset + 2] = (byte) ((value >> 40) & 0xff);
    data[offset + 3] = (byte) ((value >> 32) & 0xff);
    data[offset + 4] = (byte) ((value >> 24) & 0xff);
    data[offset + 5] = (byte) ((value >> 16) & 0xff);
    data[offset + 6] = (byte) ((value >> 8) & 0xff);
    data[offset + 7] = (byte) (value & 0xff);
  }

  /**
   * Reads a "long" value from a byte array at a given offset.
   *
   * @param data   source byte array
   * @param offset starting offset in the byte array
   * @return the value read
   */
  public static long readLong(byte[] data, int offset) {
	  long[] temp = new long[data.length];
	  for(int i = 0; i < data.length; i++){
		  if(data[i] < 0){
			  temp[i] = (short)(data[i] + 256);
		  }else{
			  temp[i] = data[i];
		  }
	  }
    long high =  (((temp[offset + 0] & 0xff) << 24) + ((temp[offset + 1] & 0xff) << 16) + ((temp[offset + 2] & 0xff) << 8) + (temp[offset + 3] & 0xff));
    long low =  (((temp[offset + 4] & 0xff) << 24) + ((temp[offset + 5] & 0xff) << 16) + ((temp[offset + 6] & 0xff) << 8) + (temp[offset + 7] & 0xff));
    high = high<< 32;
    return (high /*<< 32*/) + low;
  }

  /**
   * Writes a "short" value to an OutputStream.
   *
   * @param output target OutputStream
   * @param value  value to write
   * @throws IOException in case of an I/O problem
   */
  public static void writeShort(OutputStream output, short value) throws IOException {
    output.write((byte) ((value >> 8) & 0xff));
    output.write((byte) (value & 0xff));
  }

  /**
   * Reads a "short" value from an InputStream.
   *
   * @param input source InputStream
   * @return the value just read
   * @throws IOException in case of an I/O problem
   */
  public static short readShort(InputStream input) throws IOException {
    return (short) (((read(input) & 0xff) << 8) + (read(input) & 0xff));
  }

  /**
   * Writes a "int" value to an OutputStream.
   *
   * @param output target OutputStream
   * @param value  value to write
   * @throws IOException in case of an I/O problem
   */
  public static void writeInteger(OutputStream output, int value) throws IOException {
    output.write((byte) ((value >> 24) & 0xff));
    output.write((byte) ((value >> 16) & 0xff));
    output.write((byte) ((value >> 8) & 0xff));
    output.write((byte) (value & 0xff));
  }

  /**
   * Reads a "int" value from an InputStream.
   *
   * @param input source InputStream
   * @return the value just read
   * @throws IOException in case of an I/O problem
   */
  public static int readInteger(InputStream input) throws IOException {
    return (int) (((read(input) & 0xff) << 24) + ((read(input) & 0xff) << 16) + ((read(input) & 0xff) << 8) + (read(input) & 0xff));
  }

  /**
   * Writes a "long" value to an OutputStream.
   *
   * @param output target OutputStream
   * @param value  value to write
   * @throws IOException in case of an I/O problem
   */
  public static void writeLong(OutputStream output, long value) throws IOException {
    output.write((byte) ((value >> 56) & 0xff));
    output.write((byte) ((value >> 48) & 0xff));
    output.write((byte) ((value >> 40) & 0xff));
    output.write((byte) ((value >> 32) & 0xff));
    output.write((byte) ((value >> 24) & 0xff));
    output.write((byte) ((value >> 16) & 0xff));
    output.write((byte) ((value >> 8) & 0xff));
    output.write((byte) (value & 0xff));
  }

  /**
   * Reads a "long" value from an InputStream.
   *
   * @param input source InputStream
   * @return the value just read
   * @throws IOException in case of an I/O problem
   */
  public static long readLong(InputStream input) throws IOException {
    long high = (long) (((input.read() & 0xff) << 24) + ((input.read() & 0xff) << 16) + ((input.read() & 0xff) << 8) + (input.read() & 0xff));
    long low = (long) (((input.read() & 0xff) << 24) + ((input.read() & 0xff) << 16) + ((input.read() & 0xff) << 8) + (input.read() & 0xff));

    return (high << 32) + low;

    //        byte[] bytes = new byte[8];
    //        input.read( bytes );
    //        return readSwappedLong( bytes, 0 );
  }

  /**
   * Reads the next byte from the input stream.
   *
   * @param input the stream
   * @return the byte
   * @throws IOException if the end of file is reached
   */
  private static int read(InputStream input) throws IOException {
    int value = input.read();

    if (-1 == value) {
      throw new EOFException("Unexpected EOF reached");
    }

    return value;
  }

  /**
   * read byte[] to String[]
   *
   * @param data
   * @param pos
   * @return UnsupportedEncodingException if not support String endcoding
   */
  public static String[] readStringArray(byte[] data, int pos) {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    buffer.position(pos);
    //        short byteLength = buffer.getShort();
    int arrayNum = buffer.getInt();
    String[] stringArray = new String[arrayNum];
    try {
		for (int i = 0; i < arrayNum; i++) {
		  int l = buffer.getInt();
		  byte[] value = new byte[l];
		  buffer.get(value);
		  try {
		    stringArray[i] = new String(value, "UTF-8");
		  } catch (UnsupportedEncodingException ex) {
		    ex.printStackTrace();
		  }
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
    return stringArray;
  }

  public static String[] readStringArray(byte[] data) {
    return readStringArray(data, 0);
  }

  public static String readUTF(byte[] data) {
    try {
      return new String(data, "UTF-8");
    } catch (UnsupportedEncodingException ex) {

    }
    return "";
  }
  /**
   * Reads a "int array" value from a byte array at a given offset.
   *
   * Add on 20100722 by Hailin
   * @param data   source byte array
   * @return the value read
   */
  public static int[] readIntArray(byte[] data, int count) {
	  int[] temp = new int[count];
	  for (int i = 0; i < count; i++) {
		  temp[i] = (int)(((data[i * 4 + 0] & 0xff) << 24) + ((data[i * 4 + 1] & 0xff) << 16) + ((data[i * 4 + 2] & 0xff) << 8) + (data[i * 4 + 3] & 0xff));
	  }
	  return temp;
  }
  /**
   * Reads a "long" value from an InputStream.
   *
   * @return the value just read
   * @throws IOException in case of an I/O problem
   */
  public static long[] readLongArray(byte[] data,int count) {
    long[] temp = new long[count];
    long[] array = new long[data.length];
    for (int i=0;i<array.length;i++) {
    	if (data[i]<0) {
    		array[i]= data[i]+256;
    	}else {
    		array[i] = data[i];
    	}
    }
    for(int i = 0;i< count;i++){
    	long  high =  ((array[i*8 + 0] & 0xff) << 24) + ((array[i*8 + 1] & 0xff) << 16) + ((array[i*8+ 2] & 0xff) << 8) + (array[i*8+ 3] & 0xff);
    	long  low =   ((array[i*8 + 4] & 0xff) << 24) + ((array[i*8  + 5] & 0xff) << 16) + ((array[i*8  + 6] & 0xff) << 8) + (array[i*8  + 7] & 0xff);
    	    temp[i] = ((high << 32) + low);
    }
    return temp;
  }
}

