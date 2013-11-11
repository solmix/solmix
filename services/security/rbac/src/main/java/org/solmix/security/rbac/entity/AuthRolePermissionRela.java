package org.solmix.security.rbac.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the AUTH_ROLE_PERMISSION_RELA database table.
 * 
 */
@Entity
@Table(name="AUTH_ROLE_PERMISSION_RELA")
public class AuthRolePermissionRela implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="RELA_ID")
	private long relaId;

	//bi-directional many-to-one association to AuthPermission
    @ManyToOne
	@JoinColumn(name="PERMISSION_ID")
	private AuthPermission authPermission;

	//bi-directional many-to-one association to AuthRole
    @ManyToOne
	@JoinColumn(name="ROLE_ID")
	private AuthRole authRole;

    public AuthRolePermissionRela() {
    }

	public long getRelaId() {
		return this.relaId;
	}

	public void setRelaId(long relaId) {
		this.relaId = relaId;
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
	
}