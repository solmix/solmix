package org.solmix.security.rbac.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the AUTH_PERMISSIONS database table.
 * 
 */
@Entity
@Table(name="AUTH_PERMISSIONS")
public class AuthPermission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="PERMISSION_ID")
	private long permissionId;

	@Column(name="PERMISSION_NAME")
	private String permissionName;

	//bi-directional many-to-one association to AuthAction
    @ManyToOne
	@JoinColumn(name="ACTION_ID")
	private AuthAction authAction;

	//bi-directional many-to-one association to AuthResource
    @ManyToOne
	@JoinColumn(name="RESOURCE_ID")
	private AuthResource authResource;

	//bi-directional many-to-one association to AuthPermissionShield
	@OneToMany(mappedBy="authPermission")
	private Set<AuthPermissionShield> authPermissionShields;

	//bi-directional many-to-one association to AuthRolePermissionRela
	@OneToMany(mappedBy="authPermission")
	private Set<AuthRolePermissionRela> authRolePermissionRelas;

	//bi-directional many-to-one association to AuthUserPermissionRela
	@OneToMany(mappedBy="authPermission")
	private Set<AuthUserPermissionRela> authUserPermissionRelas;

    public AuthPermission() {
    }

	public long getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}

	public String getPermissionName() {
		return this.permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public AuthAction getAuthAction() {
		return this.authAction;
	}

	public void setAuthAction(AuthAction authAction) {
		this.authAction = authAction;
	}
	
	public AuthResource getAuthResource() {
		return this.authResource;
	}

	public void setAuthResource(AuthResource authResource) {
		this.authResource = authResource;
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
	
	public Set<AuthUserPermissionRela> getAuthUserPermissionRelas() {
		return this.authUserPermissionRelas;
	}

	public void setAuthUserPermissionRelas(Set<AuthUserPermissionRela> authUserPermissionRelas) {
		this.authUserPermissionRelas = authUserPermissionRelas;
	}
	
}