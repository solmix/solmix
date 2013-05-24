package org.solmix.web.shared.action;

import com.gwtplatform.dispatch.shared.Action;

public class LockScreenAction implements Action<LockScreenResult> { 

  java.lang.String userName;
  java.lang.String password;

  public LockScreenAction(java.lang.String userName, java.lang.String password) {
    this.userName = userName;
    this.password = password;
  }

  protected LockScreenAction() {
    // Possibly for serialization.
  }

  public java.lang.String getUserName() {
    return userName;
  }

  public java.lang.String getPassword() {
    return password;
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
    LockScreenAction other = (LockScreenAction) obj;
    if (userName == null) {
      if (other.userName != null)
        return false;
    } else if (!userName.equals(other.userName))
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + (userName == null ? 1 : userName.hashCode());
    hashCode = (hashCode * 37) + (password == null ? 1 : password.hashCode());
    return hashCode;
  }

  @Override
  public String toString() {
    return "LockScreenAction["
                 + userName
                 + ","
                 + password
    + "]";
  }
}
