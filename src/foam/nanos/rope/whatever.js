/**
 * @license
 * Copyright 2018 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

foam.CLASS({
  package: 'foam.nsnos.rope',
  name: 'whatever',

  properties: [
    {
      name: 'sourceModel',
      class: 'foam.core.FObject'
    },
    {
      name: 'junctionDAOKey',
      class: 'foam.dao.DAOProperty'
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
      class: 'Int'
    },
    {
      name: 'row',
      class: 'Int'
    }
  ]
})