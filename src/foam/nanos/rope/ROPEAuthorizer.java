/**
 * @license
 * Copyright 2019 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package foam.nanos.rope;

import foam.core.FObject;
import foam.core.X;
import foam.dao.DAO;
import foam.nanos.auth.AuthService;
import foam.nanos.auth.AuthorizationException;
import foam.nanos.auth.Authorizer;
import foam.nanos.auth.User;
import java.util.List;
import java.util.ArrayList;
import foam.nanos.rope.ROPECell;
import static foam.mlang.MLang.*;

public class ROPEAuthorizer implements Authorizer {

  protected User user_;
  protected List<FObject> authorizedSet;

  public ROPEAuthorizer(X x) {
    user_ = (User) x.get("user");
  }

  public void authorizeOnCreate(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();
    if ( ! relationshipTreeSearch(targetModel, "C") ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public void authorizeOnRead(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();
    if ( ! relationshipTreeSearch(targetModel, "R") ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public void authorizeOnUpdate(X x, FObject oldObj, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();
    if ( ! relationshipTreeSearch(targetModel, "U") ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public void authorizeOnDelete(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();
    if ( ! relationshipTreeSearch(targetModel, "D") ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public List<ROPECell> getMatrixColumns(X x, String operation) {
    DAO ropeDAO = x.get("ropeDAO");
    return ropeDAO.where(
      AND(
        EQ(ropeDAO.COLUMN, operation),
        EQ(ropeDAO.TARGET_MODEL, user_),
        EQ(ropeDAO.CHECKED, true)
      )
    );
  }

  /**
   * Checks to see if the current user is authorized to perform a particular action
   * 
   * @param searchList - list of all columns for a particular model
   */
  public boolean checkAuthorize(X x, List<ROPECell> searchList) {
    String sourceModel = null;
    DAO relationshipDAO = null;

    for ( ROPECell cell : searchList ) {
      sourceModel = cell.getSourceModel();
      relationshipDAO = x.get(cell.getJunctionDAOKey() != null ? cell.getJuncctionDAOKey() : cell.getSourceDAOKey());

    }
  }

  public boolean recursiveFind(FObject searchStartObject, FObject searchTarget) {
    return false;
  }

  public boolean checkGlobalRead(X x) {
    return false;
  }

  public boolean checkGlobalRemove(X x) {
    return false;
  }

}