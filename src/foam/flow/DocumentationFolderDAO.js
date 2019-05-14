/**
 * @license
 * Copyright 2019 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

foam.CLASS({
  package: 'foam.flow',
  name: 'DocumentationFolderDAO',
  requires: [
    'foam.flow.Document'
  ],
  documentation: 'Loads/stores documentation models from a directory of HTML markup.  Useful for saving and editing documentation in a version control repository.',
  extends: 'foam.dao.AbstractDAO',
  properties: [
    {
      class: 'String',
      name: 'dir'
    },
    {
      name: 'of',
      javaFactory: 'return foam.flow.Document.getOwnClassInfo();'
    },
    {
      name: 'delegate',
      javaFactory: 'return new foam.dao.MDAO.Builder(getX()).build();'
    }
  ],
  methods: [
    {
      name: 'select_',
      javaCode: `
sink = prepareSink(sink);

foam.dao.Sink         decorated = decorateSink_(sink, skip, limit, order, predicate);
foam.dao.Subscription sub       = new foam.dao.Subscription();

// Load files from both the Jar and regular FileSystem
java.util.Map<String, java.io.InputStream> iRStreamMap = new foam.nanos.fs.Storage(getDir(), true).getDirectoryAsStream("");
java.util.Map<String, java.io.InputStream> iFSStreamMap = new foam.nanos.fs.Storage(getDir(), false).getDirectoryAsStream("");

// Merge both maps, preferring values in iFSStream
iFSStreamMap.forEach((key, value) -> iRStreamMap.merge(key, value, (oldValue, newValue) -> newValue));

for ( java.util.Map.Entry<String, java.io.InputStream> path : iRStreamMap.entrySet() ) {
  if ( sub.getDetached() ) break;

  foam.flow.Document obj = new foam.flow.Document();
  String id = path.getKey().substring(0, path.getKey().lastIndexOf(".flow"));

  obj.setId(id);

  // TODO: We could parse the markup on the server to get the embedded title.

  try {
    byte[] data = new byte[path.getValue().available()];
    path.getValue().read(data);
    obj.setMarkup(new String(data, java.nio.charset.Charset.forName("UTF-8")));
    decorated.put(obj, sub);
  } catch(java.io.IOException e) {
    e.printStackTrace();
  }
}

decorated.eof();

return sink;`
    },
    {
      name: 'verifyId',
      args: [ { name: 'id', type: 'String' } ],
      javaCode: `
// Very conservative allowable characters to avoid any possible filename shennanigans.

if ( ! id.matches("^[a-zA-Z0-9_-]+$") ) {
  throw new RuntimeException("Invalid primary key, must use only alphanumeric characters, _ and -.");
}
`
    },
    {
      name: 'put_',
      javaCode: `
String id = (String)getPK(obj);
verifyId(id);

java.nio.file.Path path = new foam.nanos.fs.Storage(getDir(), false).getPath(id + ".flow");
if ( ! java.nio.file.Files.exists(path) ) {
  try {
    if ( ! java.nio.file.Files.isDirectory(path.getParent()) ) {
      java.nio.file.Files.createDirectories(path.getParent());
    }
    java.nio.file.Files.createFile(path);
  } catch (java.io.IOException e) {
    throw new RuntimeException(e);
  }
}

java.io.OutputStream oStream = new foam.nanos.fs.Storage(getDir(), false).getResourceOutputStream(id + ".flow");

if ( oStream == null ) {
  return obj;
}

try {
  oStream.write(((foam.flow.Document)obj).getMarkup().getBytes(java.nio.charset.Charset.forName("UTF-8")));
} catch ( java.io.IOException e ) {
  throw new RuntimeException(e);
}

return obj;`
    },
    {
      name: 'remove_',
      javaCode: `java.nio.file.FileSystem fs = java.nio.file.FileSystems.getDefault();
java.nio.file.Path path = fs.getPath(getDir(), ((String)getPK(obj)) + ".flow");
try {
  java.nio.file.Files.deleteIfExists(path);
} catch ( java.io.IOException e ) {
  throw new RuntimeException(e);
}

return obj;`
    },
    {
      name: 'find_',
      javaCode: `
// TODO: Escape/sanitize file name
verifyId((String)id);

java.io.InputStream iStream = new foam.nanos.fs.Storage(getDir(), false).getResourceInputStream(id + ".flow");

if ( iStream == null ) {
  iStream = new foam.nanos.fs.Storage(getDir(), true).getResourceInputStream(id + ".flow");
  if ( iStream == null ) return null;
}

foam.flow.Document obj = new foam.flow.Document();
obj.setId((String)id);

// TODO: We could parse the markup on the server to get the embedded title.

try {
  byte[] data = new byte[iStream.available()];
  iStream.read(data);
  obj.setMarkup(new String(data, java.nio.charset.Charset.forName("UTF-8")));
} catch(java.io.IOException e) {
  e.printStackTrace();
  return null;
}


return obj;`
    }
  ]
});
