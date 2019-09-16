/**
 * @license
 * Copyright 2019 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package foam.nanos.rope;

import foam.core.FObject;
import foam.core.X;
import foam.nanos.auth.AuthService;
import foam.nanos.auth.AuthorizationException;
import foam.nanos.auth.Authorizer;
import foam.nanos.auth.User;
import java.util.List;
import java.util.ArrayList;
import foam.nanos.rope.ROPECell;

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

  /**
   * 
   * 
   * @param searchList - list of all columns consisting of only checked ropecell boxes for a particular model
   */
  public void getAuthorizedSet(List<List<ROPECell>> searchList) {
    String sourceModel = null;

    for ( List<ROPECell> column : searchList ) {
      sourceModel = column.get(0).getSourceModel();
      for ( ROPECell cell : column ) {

      }
    }
  }

  public boolean populateAuthorizedSet(ROPECell searchList, String searchModel, String operation) {

  }

  public boolean checkGlobalRead(X x) {
    return false;
  }

  public boolean checkGlobalRemove(X x) {
    return false;
  }

}