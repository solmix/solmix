package org.solmix.security.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the AUTH_USER_PERMISSION_RELA database table.
 * 
 */
@Entity
@Table(name="AUTH_USER_PERMISSION_RELA")
public class AuthUserPermissionRela implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="RELA_ID")
	private long relaId;

	//bi-directional many-to-one association to AuthPermission
    @ManyToOne
	@JoinColumn(name="PERMISSION_ID")
	private AuthPermission authPermission;

	//bi-directional many-to-one association to AuthUser
    @ManyToOne
	@JoinColumn(name="USER_ID")
	private AuthUser authUser;

    public AuthUserPermissionRela() {
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
	
	public AuthUser getAuthUser() {
		return this.authUser;
	}

	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}
	
}