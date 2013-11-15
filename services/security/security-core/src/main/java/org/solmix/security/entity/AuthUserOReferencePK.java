package org.solmix.security.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the AUTH_USER_O_REFERENCE database table.
 * 
 */
@Embeddable
public class AuthUserOReferencePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="USER_ID")
	private long userId;

	@Column(name="ORG_ID")
	private String orgId;

	@Column(name="DATA_REFERENCE_ID")
	private String dataReferenceId;

    public AuthUserOReferencePK() {
    }
	public long getUserId() {
		return this.userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getOrgId() {
		return this.orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getDataReferenceId() {
		return this.dataReferenceId;
	}
	public void setDataReferenceId(String dataReferenceId) {
		this.dataReferenceId = dataReferenceId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AuthUserOReferencePK)) {
			return false;
		}
		AuthUserOReferencePK castOther = (AuthUserOReferencePK)other;
		return 
			(this.userId == castOther.userId)
			&& this.orgId.equals(castOther.orgId)
			&& this.dataReferenceId.equals(castOther.dataReferenceId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.userId ^ (this.userId >>> 32)));
		hash = hash * prime + this.orgId.hashCode();
		hash = hash * prime + this.dataReferenceId.hashCode();
		
		return hash;
    }
}