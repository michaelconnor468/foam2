/**
 * @license
 * Copyright 2019 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package foam.nanos.rope;

import foam.core.FObject;
import foam.core.X;
import foam.dao.ArraySink;
import foam.dao.DAO;
import foam.nanos.auth.AuthorizationException;
import foam.nanos.auth.Authorizer;
import foam.nanos.auth.AuthService;
import foam.nanos.auth.User;
import foam.nanos.rope.ROPE;
import foam.nanos.rope.ROPEActions;
import java.lang.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static foam.mlang.MLang.*;

public class ROPEAuthorizer implements Authorizer {

  protected User user_;
  protected DAO ropeDAO_;

  public ROPEAuthorizer(X x) {
    user_ = (User) x.get("user");
    ropeDAO_ = (DAO) x.get("ropeDAO");
  }

  public void authorizeOnCreate(X x, FObject obj) throws AuthorizationException {
    if ( ! ropeSearch(ROPEActions.C, obj, x) ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public void authorizeOnRead(X x, FObject obj) throws AuthorizationException {
    if ( ! ropeSearch(ROPEActions.R, obj, x) ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public void authorizeOnUpdate(X x, FObject oldObj, FObject obj) throws AuthorizationException {
    if ( ! ropeSearch(ROPEActions.U, obj, x) ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public void authorizeOnDelete(X x, FObject obj) throws AuthorizationException {
    String targetModel = obj.getClassInfo().getId();
    if ( ! ropeSearch(ROPEActions.D, obj, x) ) throw new AuthorizationException("You don't have permission to create this object");
  }

  public <T> T retrieveProperty(FObject obj, String prefix, String propertyName) {
    Method method;
    try {
        method = obj.getClass().getDeclaredMethod(
          prefix + 
          propertyName.substring(0, 1).toUpperCase() + 
          propertyName.substring(1)
        );
        method.setAccessible(true);
        return (T) method.invoke((FObject) obj);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
        // Should never occur
        System.err.println("ROPE ERROR: Attempted access on non-existant property");
    } 
    return null;
  }

  /**
    * TODO
    * refactor the getTargetRopes function or overload the function so that we can 
    * check if the targetObj passed in to the ropeSearch is an instance of foam.nanos.auth.User 
    * (i.e., User, Business, Contact, etc) and if so, select only the rope objects where the 
    * sourceModel is foam.nanos.auth.User and return 
    */ 
  public List<ROPE> getTargetRopes(FObject obj) {
    if ( obj instanceof User )
      return (List<ROPE>) ( (ArraySink) this.ropeDAO_
        .where(
          AND(
            EQ(ROPE.TARGET_MODEL, obj.getClassInfo()),
            EQ(ROPE.SOURCE_MODEL, User.getOwnClassInfo())
          )
        ) 
        .select(new ArraySink()))
        .getArray();
    else 
      return (List<ROPE>) ((ArraySink) this.ropeDAO_
        .where(EQ(ROPE.TARGET_MODEL, obj.getClassInfo())) 
       .select(new ArraySink()))
       .getArray();
  }

  public List<FObject> getJunctionObjects(X x, ROPE rope, FObject obj) {
    Object predicateProperty = rope.getIsInverse() ? rope.getJunctionModel().getAxiomByName("sourceId") : rope.getJunctionModel().getAxiomByName("targetId");

    return ((ArraySink) ( (DAO) x.get(rope.getJunctionDAOKey()))
    .where(
      EQ(predicateProperty, (Long) retrieveProperty(obj, "get", "id"))
    )
    .select(new ArraySink()))
    .getArray();
  }

  public boolean ropeSearch(ROPEActions operation, FObject obj, X x) {

    List<ROPE> ropes = getTargetRopes(obj);

    for ( ROPE rope : ropes ) {
      DAO junctionDAO = (DAO) x.get(rope.getJunctionDAOKey());
      DAO sourceDAO = (DAO) x.get(rope.getSourceDAOKey());
      List<FObject> sourceObjs = new ArrayList(); 

      if ( rope.getCardinality().equals("*:*") ) {

        List<FObject> junctionObjs = getJunctionObjects(x, rope, obj);
        for ( FObject junctionObj : junctionObjs ) {
          FObject sourceObj = rope.getIsInverse() ? (FObject) sourceDAO.find(((Long)retrieveProperty(junctionObj, "get", "targetId")).longValue()) : (FObject) sourceDAO.find(((Long)retrieveProperty(junctionObj, "get", "sourceId")).longValue());
          sourceObjs.add(sourceObj);
        }
      } else if ( rope.getCardinality().equals("*:1") ) {
        DAO rDAO = retrieveProperty(obj, "get", rope.getInverseName());
        sourceObjs = ((ArraySink) rDAO.where(INSTANCE_OF(rope.getSourceModel().getObjClass())).select(new ArraySink())).getArray();
      } else if (rope.getCardinality().equals("1:*") ) {
        FObject sourceObj = retrieveProperty(obj, "find", rope.getInverseName());
        sourceObjs.add(sourceObj);
      } else return false;
        
      if ( ( rope.getRelationshipImplies()).contains(operation) && sourceObjs.size() > 0 ) {
        for ( FObject sourceObj : sourceObjs ) {
          if ( ( sourceObj instanceof User && obj instanceof User ) ) return true;
          
          for ( ROPEActions action : rope.getRequiredSourceAction() ) {
            if ( ropeSearch(action, sourceObj, x) ) return true;
          }
        }
      }

      // if we need to check in the CRUD Matrix
      List<ROPEActions> actions = rope.getCRUD().get(operation);
      if ( actions != null && actions.size() > 0 ) {
        for ( FObject sourceObj : sourceObjs ) {
          for ( ROPEActions action : actions ) {
            if ( ( sourceObj instanceof User && obj instanceof User ) || ropeSearch(action, sourceObj, x) ) return true;
          }
        }
      }
    }

    return false; 
  }


  public boolean checkGlobalRead(X x) {
    return false;
  }

  public boolean checkGlobalRemove(X x) {
    return false;
  }

}