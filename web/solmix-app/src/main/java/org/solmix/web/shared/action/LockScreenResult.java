package org.solmix.web.shared.action;

import com.gwtplatform.dispatch.shared.Result;

public class LockScreenResult implements Result { 

  boolean success;

  public LockScreenResult(boolean success) {
    this.success = success;
  }

  protected LockScreenResult() {
    // Possibly for serialization.
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    LockScreenResult other = (LockScreenResult) obj;
    if (success != other.success)
        return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + new Boolean(success).hashCode();
    return hashCode;
  }

  @Override
  public String toString() {
    return "LockScreenResult["
                 + success
    + "]";
  }
}
