package org.solmix.security.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the AUTH_DATA_REFERENCE database table.
 * 
 */
@Entity
@Table(name="AUTH_DATA_REFERENCE")
public class AuthDataReference implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="DATA_REFERENCE_ID")
	private String dataReferenceId;

	private String comments;

	@Column(name="REFERENCE_NAME")
	private String referenceName;

	//bi-directional many-to-one association to AuthDataType
    @ManyToOne
	@JoinColumn(name="DATA_TYPE")
	private AuthDataType authDataType;

	//bi-directional many-to-one association to AuthUserOReference
	@OneToMany(mappedBy="authDataReference")
	private Set<AuthUserOReference> authUserOReferences;

    public AuthDataReference() {
    }

	public String getDataReferenceId() {
		return this.dataReferenceId;
	}

	public void setDataReferenceId(String dataReferenceId) {
		this.dataReferenceId = dataReferenceId;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getReferenceName() {
		return this.referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public AuthDataType getAuthDataType() {
		return this.authDataType;
	}

	public void setAuthDataType(AuthDataType authDataType) {
		this.authDataType = authDataType;
	}
	
	public Set<AuthUserOReference> getAuthUserOReferences() {
		return this.authUserOReferences;
	}

	public void setAuthUserOReferences(Set<AuthUserOReference> authUserOReferences) {
		this.authUserOReferences = authUserOReferences;
	}
	
}