/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package com.othersonline.kv.gen;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import org.apache.thrift.*;
import org.apache.thrift.meta_data.*;
import org.apache.thrift.protocol.*;

public class GetResult implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("GetResult");
  private static final TField EXISTS_FIELD_DESC = new TField("exists", TType.BOOL, (short)1);
  private static final TField DATA_FIELD_DESC = new TField("data", TType.STRING, (short)2);

  private boolean exists;
  public static final int EXISTS = 1;
  private byte[] data;
  public static final int DATA = 2;

  private final Isset __isset = new Isset();
  private static final class Isset implements java.io.Serializable {
    public boolean exists = false;
  }

  public static final Map<Integer, FieldMetaData> metaDataMap = Collections.unmodifiableMap(new HashMap<Integer, FieldMetaData>() {{
    put(EXISTS, new FieldMetaData("exists", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    put(DATA, new FieldMetaData("data", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
  }});

  static {
    FieldMetaData.addStructMetaDataMap(GetResult.class, metaDataMap);
  }

  public GetResult() {
  }

  public GetResult(
    boolean exists,
    byte[] data)
  {
    this();
    this.exists = exists;
    this.__isset.exists = true;
    this.data = data;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetResult(GetResult other) {
    __isset.exists = other.__isset.exists;
    this.exists = other.exists;
    if (other.isSetData()) {
      this.data = new byte[other.data.length];
      System.arraycopy(other.data, 0, data, 0, other.data.length);
    }
  }

  @Override
  public GetResult clone() {
    return new GetResult(this);
  }

  public boolean isExists() {
    return this.exists;
  }

  public void setExists(boolean exists) {
    this.exists = exists;
    this.__isset.exists = true;
  }

  public void unsetExists() {
    this.__isset.exists = false;
  }

  // Returns true if field exists is set (has been asigned a value) and false otherwise
  public boolean isSetExists() {
    return this.__isset.exists;
  }

  public byte[] getData() {
    return this.data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public void unsetData() {
    this.data = null;
  }

  // Returns true if field data is set (has been asigned a value) and false otherwise
  public boolean isSetData() {
    return this.data != null;
  }

  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case EXISTS:
      if (value == null) {
        unsetExists();
      } else {
        setExists((Boolean)value);
      }
      break;

    case DATA:
      if (value == null) {
        unsetData();
      } else {
        setData((byte[])value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case EXISTS:
      return new Boolean(isExists());

    case DATA:
      return getData();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been asigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case EXISTS:
      return isSetExists();
    case DATA:
      return isSetData();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof GetResult)
      return this.equals((GetResult)that);
    return false;
  }

  public boolean equals(GetResult that) {
    if (that == null)
      return false;

    boolean this_present_exists = true;
    boolean that_present_exists = true;
    if (this_present_exists || that_present_exists) {
      if (!(this_present_exists && that_present_exists))
        return false;
      if (this.exists != that.exists)
        return false;
    }

    boolean this_present_data = true && this.isSetData();
    boolean that_present_data = true && that.isSetData();
    if (this_present_data || that_present_data) {
      if (!(this_present_data && that_present_data))
        return false;
      if (!java.util.Arrays.equals(this.data, that.data))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public void read(TProtocol iprot) throws TException {
    TField field;
    iprot.readStructBegin();
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == TType.STOP) { 
        break;
      }
      switch (field.id)
      {
        case EXISTS:
          if (field.type == TType.BOOL) {
            this.exists = iprot.readBool();
            this.__isset.exists = true;
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case DATA:
          if (field.type == TType.STRING) {
            this.data = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        default:
          TProtocolUtil.skip(iprot, field.type);
          break;
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();

    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(EXISTS_FIELD_DESC);
    oprot.writeBool(this.exists);
    oprot.writeFieldEnd();
    if (this.data != null) {
      oprot.writeFieldBegin(DATA_FIELD_DESC);
      oprot.writeBinary(this.data);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("GetResult(");
    boolean first = true;

    sb.append("exists:");
    sb.append(this.exists);
    first = false;
    if (!first) sb.append(", ");
    sb.append("data:");
    if (this.data == null) {
      sb.append("null");
    } else {
        int __data_size = Math.min(this.data.length, 128);
        for (int i = 0; i < __data_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.data[i]).length() > 1 ? Integer.toHexString(this.data[i]).substring(Integer.toHexString(this.data[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.data[i]).toUpperCase());
        }
        if (this.data.length > 128) sb.append(" ...");
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // check that fields of type enum have valid values
  }

}
