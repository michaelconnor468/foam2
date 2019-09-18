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
    if ( ! checkAuthorize(x, targetModel, "C") ) throw new AuthorizationException();
  }

  public void authorizeOnRead(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();
    if ( ! checkAuthorize(x, targetModel, "R") ) throw new AuthorizationException();
  }

  public void authorizeOnUpdate(X x, FObject oldObj, FObject obj) throws AuthorizationException {
    String targetModel = oldObj.getClassInfo().getId();
    if ( ! checkAuthorize(x, targetModel, "U") ) throw new AuthorizationException();
  }

  public void authorizeOnDelete(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();
    if ( ! checkAuthorize(x, targetModel, "D") ) throw new AuthorizationException();
  }

  public boolean checkAuthorize(X x, String targetModel, String operation) {
    List<ROPECell> sourceColumns = getMatrixColumns(x, targetModel, operation);

    return false;
  }

  public List<ROPECell> getMatrixColumns(X x, String targetModel, String operation) {
    DAO ropeDAO = (DAO) x.get("ropeDAO");
    return (List<ROPECell>) ( (ArraySink) ropeDAO.where(
      AND(
        EQ(ROPECell.COLUMN, operation),
        EQ(ROPECell.TARGET_MODEL, targetModel),
        EQ(ROPECell.CHECKED, true)
      )
    ).select(new ArraySink())).getArray();
  }

  public List<String> getModels(List<ROPECell> cells) {
    List<String> stringModels = new ArrayList<String>();
    for ( ROPECell cell: cells ) {
      stringModels.add(cell.getRow());
    } 
    return stringModels;
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