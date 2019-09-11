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
import foam.nanos.auth.AuthService;
import foam.nanos.auth.AuthorizationException;
import foam.nanos.auth.User;
import java.util.List;
import static foam.mlang.MLang.*;

public class ROPEAuthorizer implements Authorizer {

  protected User user;
  protected DAO relationshipAuthorizationCellDAO;

  public ROPEAuthorizer(X x) {
    user = (User) x.get("user");
    ropeDAO = x.get("ropeDAO");
  }

  public List<RelationshipAuthorizationMatrixCell> findRelationship(X x, FObject obj) {
    List<RelationshipAuthorizationMatrixCell> ropes = (List<RelationshipAuthorizationMatrixCell>) ((ArraySink) ropeDAO 
      .where(
        EQ(RelationshipAuthorizationMatrixCell.TARGET_MODEL, obj.getClassInfo().getId())
        EQ(RelationshipAuthorizationMatrixCell.CHECKED, true), 
      )
      .select(new ArraySink()))
      .getArray();
  }

  public void authorizeOnCreate(X x, FObject obj) throws AuthorizationException {

  }

  public void authorizeOnRead(X x, FObject obj) throws AuthorizationException {



  }

  public void authorizeOnUpdate(X x, FObject oldObj, FObject obj) throws AuthorizationException {

  }

  public void authorizeOnDelete(X x, FObject obj) throws AuthorizationException {

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