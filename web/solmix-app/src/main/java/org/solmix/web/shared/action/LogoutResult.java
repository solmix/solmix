package org.solmix.web.shared.action;

import com.gwtplatform.dispatch.shared.Result;

public class LogoutResult implements Result { 

  boolean success;
  boolean getIn;

  public LogoutResult(boolean success, boolean getIn) {
    this.success = success;
    this.getIn = getIn;
  }

  protected LogoutResult() {
    // Possibly for serialization.
  }

  public boolean isSuccess() {
    return success;
  }

  public boolean isGetIn() {
    return getIn;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    LogoutResult other = (LogoutResult) obj;
    if (success != other.success)
        return false;
    if (getIn != other.getIn)
        return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + new Boolean(success).hashCode();
    hashCode = (hashCode * 37) + new Boolean(getIn).hashCode();
    return hashCode;
  }

  @Override
  public String toString() {
    return "LogoutResult["
                 + success
                 + ","
                 + getIn
    + "]";
  }
}
