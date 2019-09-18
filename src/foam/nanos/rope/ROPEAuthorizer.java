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

  }

  public void authorizeOnRead(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();

  }

  public void authorizeOnUpdate(X x, FObject oldObj, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();

  }

  public void authorizeOnDelete(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();

  }

  public List<ROPECell> getMatrixColumns(X x, String operation) {
    DAO ropeDAO = (DAO) x.get("ropeDAO");
    return (List<ROPECell>) ( (ArraySink) ropeDAO.where(
      AND(
        EQ(ROPECell.COLUMN, operation),
        EQ(ROPECell.TARGET_MODEL, user_),
        EQ(ROPECell.CHECKED, true)
      )
    ).select(new ArraySink())).getArray();
  }

  public List<ROPECell> getRelationships(X x, List<ROPECell> targetModels) {
    DAO ropeDAO = (DAO) x.get("ropeDAO");
    return (List<ROPECell>) ( (ArraySink) ropeDAO.where(
      IN(ROPECell.TARGET_MODEL, targetModels)
    ).select(new ArraySink())).getArray();
  }

  public List<String> getModels(List<ROPECell> cells) {
    List<String> stringModels = new ArrayList<String>();
    for ( ROPECell cell: cells ) {
      stringModels.add(cell.getRow());
    } 
    return stringModels;
  }

  public boolean checkAuthorize(X x, List<String> models) {
    DAO ropeDAO = (DAO) x.get("ropeDAO");
    for ( String model : models ) {
      List<ROPECell> childNodes = (List<ROPECell>) ((ArraySink) ropeDAO.where(
        EQ(ROPECell.INVERSE_RELATIONSHIP, model)
      ).select(new ArraySink()));

      List<String> subModels = getModels(childNodes);
    }
    return false;
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