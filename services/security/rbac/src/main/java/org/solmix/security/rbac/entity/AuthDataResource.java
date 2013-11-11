package org.solmix.security.rbac.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the AUTH_DATA_RESOURCE database table.
 * 
 */
@Entity
@Table(name="AUTH_DATA_RESOURCE")
public class AuthDataResource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="RELA_ID")
	private long relaId;

	//bi-directional many-to-one association to AuthDataType
    @ManyToOne
	@JoinColumn(name="DATA_TYPE")
	private AuthDataType authDataType;

	//bi-directional many-to-one association to AuthResource
    @ManyToOne
	@JoinColumn(name="RESOURCE_ID")
	private AuthResource authResource;

    public AuthDataResource() {
    }

	public long getRelaId() {
		return this.relaId;
	}

	public void setRelaId(long relaId) {
		this.relaId = relaId;
	}

	public AuthDataType getAuthDataType() {
		return this.authDataType;
	}

	public void setAuthDataType(AuthDataType authDataType) {
		this.authDataType = authDataType;
	}
	
	public AuthResource getAuthResource() {
		return this.authResource;
	}

	public void setAuthResource(AuthResource authResource) {
		this.authResource = authResource;
	}
	
}