package com.cloud.exception;

import com.cloud.utils.SerialVersionUID;

public class IllegalVirtualMachineException extends CloudException {

  private static final long serialVersionUID = SerialVersionUID.IllegalVirtualMachineException;

  public IllegalVirtualMachineException(String msg) {
    super(msg);
  }
}