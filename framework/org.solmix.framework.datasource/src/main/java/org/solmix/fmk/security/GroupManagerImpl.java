
package org.solmix.fmk.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.solmix.api.exception.SlxException;
import org.solmix.api.security.Group;
import org.solmix.api.security.GroupManager;
import org.solmix.api.security.auth.ACL;

public class GroupManagerImpl implements GroupManager, Serializable
{

    private static final long serialVersionUID = -6146916224123117082L;

    private static GroupManager instance;

    GroupManagerImpl()
    {

    }

    public static synchronized GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManagerImpl();
        }
        return instance;
    }

    @Override
    public Group createGroup(String name) throws SlxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group getGroup(String name) throws SlxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Group> getAllGroups() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getAllGroups(String groupName) throws SlxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, ACL> getACLs(String group) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group addRole(Group group, String roleName) throws SlxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group addGroup(Group group, String groupName) throws SlxException {
        // TODO Auto-generated method stub
        return null;
    }

}
