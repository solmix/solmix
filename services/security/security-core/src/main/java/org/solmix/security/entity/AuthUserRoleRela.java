package org.solmix.security.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the AUTH_USER_ROLE_RELA database table.
 * 
 */
@Entity
@Table(name="AUTH_USER_ROLE_RELA")
public class AuthUserRoleRela implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="RELA_ID")
	private long relaId;

	//bi-directional many-to-one association to AuthRole
    @ManyToOne
	@JoinColumn(name="ROLE_ID")
	private AuthRole authRole;

	//bi-directional many-to-one association to AuthUser
    @ManyToOne
	@JoinColumn(name="USER_ID")
	private AuthUser authUser;

    public AuthUserRoleRela() {
    }

	public long getRelaId() {
		return this.relaId;
	}

	public void setRelaId(long relaId) {
		this.relaId = relaId;
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