package org.solmix.web.shared.action;

import com.gwtplatform.dispatch.shared.Action;

public class LogoutAction implements Action<LogoutResult> { 

  int type;

  public LogoutAction(int type) {
    this.type = type;
  }

  protected LogoutAction() {
    // Possibly for serialization.
  }

  public int getType() {
    return type;
  }

  @Override
  public String getServiceName() {
    return "dispatch/";
  }

  @Override
  public boolean isSecured() {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    LogoutAction other = (LogoutAction) obj;
    if (type != other.type)
        return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + new Integer(type).hashCode();
    return hashCode;
  }

  @Override
  public String toString() {
    return "LogoutAction["
                 + type
    + "]";
  }
}
