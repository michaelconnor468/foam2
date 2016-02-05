/**
 * @license
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

FObject.__proto__.describe = function(opt_name) {
  console.log('CLASS:  ', this.name);
  console.log('extends:', this.model_.extends);
  console.log('Axiom Type           Source Class   Name');
      console.log('----------------------------------------------------');
  for ( var key in this.axiomMap_ ) {
    var a = this.axiomMap_[key];
    console.log(
      foam.string.pad(a.cls_.name, 20),
      foam.string.pad(a.sourceCls_.name, 14),
      a.name);
  }
  console.log('\n');
};


CLASS({
  name: 'FObject',

  methods: [
    function describe(opt_name) {
      console.log('Instance of', this.cls_.name);
      console.log('Axiom Type           Name           Value');
      console.log('----------------------------------------------------');
      var ps = this.cls_.getAxiomsByClass(Property);
      for ( var i = 0 ; i < ps.length ; i++ ) {
        var p = ps[i];
        console.log(
          foam.string.pad(p.cls_.name, 20),
          foam.string.pad(p.name, 14),
          this[p.name]);
      }
      console.log('\n');
    }
  ]
});


// TODO: Add describe() to Context
