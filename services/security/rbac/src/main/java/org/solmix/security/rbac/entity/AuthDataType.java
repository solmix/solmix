package org.solmix.security.rbac.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the AUTH_DATA_TYPE database table.
 * 
 */
@Entity
@Table(name="AUTH_DATA_TYPE")
public class AuthDataType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="DATA_TYPE")
	private long dataType;

	private String comments;

	@Column(name="DATA_NAME")
	private String dataName;

	//bi-directional many-to-one association to AuthDataReference
	@OneToMany(mappedBy="authDataType")
	private Set<AuthDataReference> authDataReferences;

	//bi-directional many-to-one association to AuthDataResource
	@OneToMany(mappedBy="authDataType")
	private Set<AuthDataResource> authDataResources;

    public AuthDataType() {
    }

	public long getDataType() {
		return this.dataType;
	}

	public void setDataType(long dataType) {
		this.dataType = dataType;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDataName() {
		return this.dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public Set<AuthDataReference> getAuthDataReferences() {
		return this.authDataReferences;
	}

	public void setAuthDataReferences(Set<AuthDataReference> authDataReferences) {
		this.authDataReferences = authDataReferences;
	}
	
	public Set<AuthDataResource> getAuthDataResources() {
		return this.authDataResources;
	}

	public void setAuthDataResources(Set<AuthDataResource> authDataResources) {
		this.authDataResources = authDataResources;
	}
	
}