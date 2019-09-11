/**
 * @license
 * Copyright 2019 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package foam.nanos.rope;

import foam.core.FObject;
import foam.core.X;
import foam.dao.DAO;
import foam.dao.ArraySink;
import foam.nanos.auth.*;
import java.util.List;
import static foam.mlang.MLang.*;

public class ROPEAuthorizer implements Authorizer {

  protected User user;
  protected DAO relationshipAuthorizationCellDAO;

  public ROPEAuthorizer(X x) {
    user = (User) x.get("user");
    ropeDAO = x.get("ropeDAO");
  }

  public List<RelationshipAuthorizationMatrixCell> findRelationship(X x, FObject obj, char column) {
    List<RelationshipAuthorizationMatrixCell> ropes = (List<RelationshipAuthorizationMatrixCell>) ((ArraySink) ropeDAO 
      .where(
        EQ(RelationshipAuthorizationMatrixCell.TARGET_MODEL, obj.getClassInfo().getId())
        EQ(RelationshipAuthorizationMatrixCell.CHECKED, true), 
        EQ(RelationshipAuthorizationMatrixCell.COLUMN, column)
      )
      .select(new ArraySink()))
      .getArray();
  }

  public void authorizeOnCreate(X x, FObject obj) throws AuthorizationException {
    if ( ! isAuthorized(x, obj, 'C') ) {
      throw new AuthorizationException();
    }
  }

  public void authorizeOnRead(X x, FObject obj) throws AuthorizationException {
    if ( ! isAuthorized(x, obj, 'R') ) {
      throw new AuthorizationException();
    }
  }

  public void authorizeOnUpdate(X x, FObject oldObj, FObject obj) throws AuthorizationException {
    if ( ! isAuthorized(x, obj, 'U') ) {
      throw new AuthorizationException();
    }
  }

  public void authorizeOnDelete(X x, FObject obj) throws AuthorizationException {
    if ( ! isAuthorized(x, obj, 'D') ) {
      throw new AuthorizationException();
    }
  }

  public boolean isAuthorized(X x, FObject obj, char column) {
    List<RelationshipAuthorizationMatrixCell> searchList = findRelationship(x, obj, column);
  }

  public boolean checkGlobalRead(X x) {
    AuthService authService = (AuthService) x.get("auth");
    try {
      return authService.check(x, "*");
    } catch ( AuthorizationException e ) {
      return false;
    }
  }

  public boolean checkGlobalRemove(X x) {
    AuthService authService = (AuthService) x.get("auth");
    try {
      return authService.check(x, "*");
    } catch ( AuthorizationException e ) {
      return false;
    }

  }
}