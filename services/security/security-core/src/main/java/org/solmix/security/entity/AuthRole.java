package org.solmix.security.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the AUTH_ROLES database table.
 * 
 */
@Entity
@Table(name="AUTH_ROLES")
public class AuthRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="ROLE_ID")
	private long roleId;

	private String comments;

      @Temporal( TemporalType.DATE)
	@Column(name="CREATE_DATE")
	private Date createDate;

	@Column(name="CREATE_USER_ID")
	private BigDecimal createUserId;

	private BigDecimal deptcode;

    @Temporal( TemporalType.DATE)
	@Column(name="END_DATE")
	private Date endDate;

    @Temporal( TemporalType.DATE)
	@Column(name="MODIFY_DATE")
	private Date modifyDate;

	@Column(name="MODIFY_USER_ID")
	private BigDecimal modifyUserId;

	@Column(name="ROLE_NAME")
	private String roleName;

	//bi-directional many-to-many association to AuthUser
    @ManyToMany
	@JoinTable(
		name="AUTH_USER_ROLE_RELA"
		, joinColumns={
			@JoinColumn(name="ROLE_ID")
			}
		, inverseJoinColumns={
			@JoinColumn(name="USER_ID")
			}
		)
	private Set<AuthUser> authUsers;

	//bi-directional many-to-one association to AuthUserRoleRela
	@OneToMany(mappedBy="authRole")
	private Set<AuthUserRoleRela> authUserRoleRelas;

	//bi-directional many-to-one association to AuthPermissionShield
	@OneToMany(mappedBy="authRole")
	private Set<AuthPermissionShield> authPermissionShields;

	//bi-directional many-to-one association to AuthRolePermissionRela
	@OneToMany(mappedBy="authRole")
	private Set<AuthRolePermissionRela> authRolePermissionRelas;

    public AuthRole() {
    }

	public long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
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

	public BigDecimal getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(BigDecimal createUserId) {
		this.createUserId = createUserId;
	}

	public BigDecimal getDeptcode() {
		return this.deptcode;
	}

	public void setDeptcode(BigDecimal deptcode) {
		this.deptcode = deptcode;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getModifyDate() {
		return this.modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public BigDecimal getModifyUserId() {
		return this.modifyUserId;
	}

	public void setModifyUserId(BigDecimal modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<AuthUser> getAuthUsers() {
		return this.authUsers;
	}

	public void setAuthUsers(Set<AuthUser> authUsers) {
		this.authUsers = authUsers;
	}
	
	public Set<AuthUserRoleRela> getAuthUserRoleRelas() {
		return this.authUserRoleRelas;
	}

	public void setAuthUserRoleRelas(Set<AuthUserRoleRela> authUserRoleRelas) {
		this.authUserRoleRelas = authUserRoleRelas;
	}
	
	public Set<AuthPermissionShield> getAuthPermissionShields() {
		return this.authPermissionShields;
	}

	public void setAuthPermissionShields(Set<AuthPermissionShield> authPermissionShields) {
		this.authPermissionShields = authPermissionShields;
	}
	
	public Set<AuthRolePermissionRela> getAuthRolePermissionRelas() {
		return this.authRolePermissionRelas;
	}

	public void setAuthRolePermissionRelas(Set<AuthRolePermissionRela> authRolePermissionRelas) {
		this.authRolePermissionRelas = authRolePermissionRelas;
	}
	
}