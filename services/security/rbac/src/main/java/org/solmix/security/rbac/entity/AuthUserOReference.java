package org.solmix.security.rbac.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the AUTH_USER_O_REFERENCE database table.
 * 
 */
@Entity
@Table(name="AUTH_USER_O_REFERENCE")
public class AuthUserOReference implements Serializable {
	private static final long serialVersionUID = 1L;

//	@EmbeddedId
//	private AuthUserOReferencePK id;
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="RELA_ID")
	private long relaId;
	
	@Column(name="ORG_ID")
      private String orgId;
	//bi-directional many-to-one association to AuthDataReference
    @ManyToOne
	@JoinColumn(name="DATA_REFERENCE_ID")
	private AuthDataReference authDataReference;

	//bi-directional many-to-one association to AuthUser
    @ManyToOne
	@JoinColumn(name="USER_ID")
	private AuthUser authUser;

    public AuthUserOReference() {
    }

	
	
	
    /**
     * @return the relaId
     */
    public long getRelaId() {
        return relaId;
    }



    
    /**
     * @param relaId the relaId to set
     */
    public void setRelaId(long relaId) {
        this.relaId = relaId;
    }



    
    /**
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }



    
    /**
     * @param orgId the orgId to set
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }



    public AuthDataReference getAuthDataReference() {
		return this.authDataReference;
	}

	public void setAuthDataReference(AuthDataReference authDataReference) {
		this.authDataReference = authDataReference;
	}
	
	public AuthUser getAuthUser() {
		return this.authUser;
	}

	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}
	
}