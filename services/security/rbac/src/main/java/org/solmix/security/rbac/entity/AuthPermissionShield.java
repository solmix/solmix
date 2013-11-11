package org.solmix.security.rbac.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the AUTH_PERMISSION_SHIELD database table.
 * 
 */
@Entity
@Table(name="AUTH_PERMISSION_SHIELD")
public class AuthPermissionShield implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="SHIELD_ID")
	private long shieldId;

	//bi-directional many-to-one association to AuthPermission
    @ManyToOne
	@JoinColumn(name="PERMISSION_ID")
	private AuthPermission authPermission;

	//bi-directional many-to-one association to AuthRole
    @ManyToOne
	@JoinColumn(name="ROLE_ID")
	private AuthRole authRole;

	//bi-directional many-to-one association to AuthUser
    @ManyToOne
	@JoinColumn(name="USER_ID")
	private AuthUser authUser;

    public AuthPermissionShield() {
    }

	public long getShieldId() {
		return this.shieldId;
	}

	public void setShieldId(long shieldId) {
		this.shieldId = shieldId;
	}

	public AuthPermission getAuthPermission() {
		return this.authPermission;
	}

	public void setAuthPermission(AuthPermission authPermission) {
		this.authPermission = authPermission;
	}
	
	public AuthRole getAuthRole() {
		return this.authRole;
	}

	public void setAuthRole(AuthRole authRole) {
		this.authRole = authRole;
	}
	
	public AuthUser getAuthUser() {
		return this.authUser;
	}

	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}
	
}