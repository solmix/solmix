package org.solmix.jpa.test.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the AUTH_USERS database table.
 * 
 */
@Entity
@Table(name="AUTH_USERS")
public class AuthUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="USER_ID")
	private long userId;

	private String comments;

    @Temporal( TemporalType.DATE)
	@Column(name="CREATE_DATE")
	private Date createDate;

	@Column(name="CREATE_ID")
	private BigDecimal createId;

	@Column(name="CREATE_NAME")
	private String createName;

	@Column(name="DATA_REFERENCE_ID")
	private String dataReferenceId;

    @Temporal( TemporalType.DATE)
	@Column(name="LAST_CHANGE_PASSWORD")
	private Date lastChangePassword;

	@Column(name="LAST_LOGIN_IP")
	private String lastLoginIp;

    @Temporal( TemporalType.DATE)
	@Column(name="LAST_LOGIN_TIME")
	private Date lastLoginTime;

    @Temporal( TemporalType.DATE)
	@Column(name="LOCK_TIME")
	private Date lockTime;

	private String password;

    @Temporal( TemporalType.DATE)
	@Column(name="PWD_REMIND_TIME")
	private Date pwdRemindTime;

	@Column(name="STAFF_ID")
	private String staffId;

	private BigDecimal status;

	@Column(name="SUPER_FLAG")
	private BigDecimal superFlag;

	private String truename;

	@Column(name="USER_NAME")
	private String userName;

	//bi-directional many-to-many association to AuthRole
	@ManyToMany(mappedBy="authUsers")
	private Set<AuthRole> authRoles;

	
    public AuthUser() {
    }

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public BigDecimal getCreateId() {
		return this.createId;
	}

	public void setCreateId(BigDecimal createId) {
		this.createId = createId;
	}

	public String getCreateName() {
		return this.createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getDataReferenceId() {
		return this.dataReferenceId;
	}

	public void setDataReferenceId(String dataReferenceId) {
		this.dataReferenceId = dataReferenceId;
	}

	public Date getLastChangePassword() {
		return this.lastChangePassword;
	}

	public void setLastChangePassword(Date lastChangePassword) {
		this.lastChangePassword = lastChangePassword;
	}

	public String getLastLoginIp() {
		return this.lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLockTime() {
		return this.lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getPwdRemindTime() {
		return this.pwdRemindTime;
	}

	public void setPwdRemindTime(Date pwdRemindTime) {
		this.pwdRemindTime = pwdRemindTime;
	}

	public String getStaffId() {
		return this.staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public BigDecimal getStatus() {
		return this.status;
	}

	public void setStatus(BigDecimal status) {
		this.status = status;
	}

	public BigDecimal getSuperFlag() {
		return this.superFlag;
	}

	public void setSuperFlag(BigDecimal superFlag) {
		this.superFlag = superFlag;
	}

	public String getTruename() {
		return this.truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Set<AuthRole> getAuthRoles() {
		return this.authRoles;
	}

	public void setAuthRoles(Set<AuthRole> authRoles) {
		this.authRoles = authRoles;
	}
}