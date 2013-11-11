package org.solmix.security.rbac.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the AUTH_RESOURCES database table.
 * 
 */
@Entity
@Table(name="AUTH_RESOURCES")
public class AuthResource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="RESOURCE_ID")
	private long resourceId;

	private String comments;

	@Column(name="RESOURCE_NAME")
	private String resourceName;

	//bi-directional many-to-one association to AuthPermission
	@OneToMany(mappedBy="authResource")
	private Set<AuthPermission> authPermissions;

	//bi-directional many-to-one association to AuthDataResource
	@OneToMany(mappedBy="authResource")
	private Set<AuthDataResource> authDataResources;

    public AuthResource() {
    }

	public long getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Set<AuthPermission> getAuthPermissions() {
		return this.authPermissions;
	}

	public void setAuthPermissions(Set<AuthPermission> authPermissions) {
		this.authPermissions = authPermissions;
	}
	
	public Set<AuthDataResource> getAuthDataResources() {
		return this.authDataResources;
	}

	public void setAuthDataResources(Set<AuthDataResource> authDataResources) {
		this.authDataResources = authDataResources;
	}
	
}