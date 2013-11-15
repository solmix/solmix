package org.solmix.security.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the AUTH_ACTIONS database table.
 * 
 */
@Entity
@Table(name="AUTH_ACTIONS")
public class AuthAction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="ACTION_ID")
	private long actionId;

	@Column(name="ACTION_NAME")
	private String actionName;

	private String comments;

	//bi-directional many-to-one association to AuthPermission
	@OneToMany(mappedBy="authAction")
	private Set<AuthPermission> authPermissions;

    public AuthAction() {
    }

	public long getActionId() {
		return this.actionId;
	}

	public void setActionId(long actionId) {
		this.actionId = actionId;
	}

	public String getActionName() {
		return this.actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Set<AuthPermission> getAuthPermissions() {
		return this.authPermissions;
	}

	public void setAuthPermissions(Set<AuthPermission> authPermissions) {
		this.authPermissions = authPermissions;
	}
	
}