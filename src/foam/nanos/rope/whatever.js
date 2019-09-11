/**
 * @license
 * Copyright 2018 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

foam.CLASS({
  package: 'foam.nanos.rope',
  name: 'relationshipAuthorizationMatrixCell',
  documentation: 'model represents a single cell in a rope matrix',

  properties: [
    {
      name: 'sourceModel',
      class: 'String'
    }, 
    {
      name: 'targetModel',
      class: 'String'
    },
    {
      name: 'junctionDAOKey',
      class: 'String'
    },
    {
      name: 'sourceDAOKey',
      class: 'foam.dao.DAOProperty'
    },
    {
      name: 'checked',
      class: 'Boolean'
    },
    {
      name: 'column',
      class: 'Char'
    },
    {
      name: 'row',
      class: 'Int'
    }
  ]
})
