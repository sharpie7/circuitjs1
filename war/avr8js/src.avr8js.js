// modules are defined as an array
// [ module function, map of requires ]
//
// map of requires is short require name -> numeric require
//
// anything defined in a previous bundle is accessed via the
// orig method which is the require for previous bundles
parcelRequire = (function (modules, cache, entry, globalName) {
  // Save the require from previous bundle to this closure if any
  var previousRequire = typeof parcelRequire === 'function' && parcelRequire;
  var nodeRequire = typeof require === 'function' && require;

  function newRequire(name, jumped) {
    if (!cache[name]) {
      if (!modules[name]) {
        // if we cannot find the module within our internal map or
        // cache jump to the current global require ie. the last bundle
        // that was added to the page.
        var currentRequire = typeof parcelRequire === 'function' && parcelRequire;
        if (!jumped && currentRequire) {
          return currentRequire(name, true);
        }

        // If there are other bundles on this page the require from the
        // previous one is saved to 'previousRequire'. Repeat this as
        // many times as there are bundles until the module is found or
        // we exhaust the require chain.
        if (previousRequire) {
          return previousRequire(name, true);
        }

        // Try the node require function if it exists.
        if (nodeRequire && typeof name === 'string') {
          return nodeRequire(name);
        }

        var err = new Error('Cannot find module \'' + name + '\'');
        err.code = 'MODULE_NOT_FOUND';
        throw err;
      }

      localRequire.resolve = resolve;
      localRequire.cache = {};

      var module = cache[name] = new newRequire.Module(name);

      modules[name][0].call(module.exports, localRequire, module, module.exports, this);
    }

    return cache[name].exports;

    function localRequire(x){
      return newRequire(localRequire.resolve(x));
    }

    function resolve(x){
      return modules[name][1][x] || x;
    }
  }

  function Module(moduleName) {
    this.id = moduleName;
    this.bundle = newRequire;
    this.exports = {};
  }

  newRequire.isParcelRequire = true;
  newRequire.Module = Module;
  newRequire.modules = modules;
  newRequire.cache = cache;
  newRequire.parent = previousRequire;
  newRequire.register = function (id, exports) {
    modules[id] = [function (require, module) {
      module.exports = exports;
    }, {}];
  };

  var error;
  for (var i = 0; i < entry.length; i++) {
    try {
      newRequire(entry[i]);
    } catch (e) {
      // Save first error but execute all entries
      if (!error) {
        error = e;
      }
    }
  }

  if (entry.length) {
    // Expose entry point to Node, AMD or browser globals
    // Based on https://github.com/ForbesLindesay/umd/blob/master/template.js
    var mainExports = newRequire(entry[entry.length - 1]);

    // CommonJS
    if (typeof exports === "object" && typeof module !== "undefined") {
      module.exports = mainExports;

    // RequireJS
    } else if (typeof define === "function" && define.amd) {
     define(function () {
       return mainExports;
     });

    // <script>
    } else if (globalName) {
      this[globalName] = mainExports;
    }
  }

  // Override the current require with this new one
  parcelRequire = newRequire;

  if (error) {
    // throw error from earlier, _after updating parcelRequire_
    throw error;
  }

  return newRequire;
})({"../../node_modules/lit-html/lib/dom.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.removeNodes = exports.reparentNodes = exports.isCEPolyfill = void 0;

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * True if the custom elements polyfill is in use.
 */
const isCEPolyfill = typeof window !== 'undefined' && window.customElements != null && window.customElements.polyfillWrapFlushCallback !== undefined;
/**
 * Reparents nodes, starting from `start` (inclusive) to `end` (exclusive),
 * into another container (could be the same container), before `before`. If
 * `before` is null, it appends the nodes to the container.
 */

exports.isCEPolyfill = isCEPolyfill;

const reparentNodes = (container, start, end = null, before = null) => {
  while (start !== end) {
    const n = start.nextSibling;
    container.insertBefore(start, before);
    start = n;
  }
};
/**
 * Removes nodes, starting from `start` (inclusive) to `end` (exclusive), from
 * `container`.
 */


exports.reparentNodes = reparentNodes;

const removeNodes = (container, start, end = null) => {
  while (start !== end) {
    const n = start.nextSibling;
    container.removeChild(start);
    start = n;
  }
};

exports.removeNodes = removeNodes;
},{}],"../../node_modules/lit-html/lib/template.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.lastAttributeNameRegex = exports.createMarker = exports.isTemplatePartActive = exports.Template = exports.boundAttributeSuffix = exports.markerRegex = exports.nodeMarker = exports.marker = void 0;

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * An expression marker with embedded unique key to avoid collision with
 * possible text in templates.
 */
const marker = `{{lit-${String(Math.random()).slice(2)}}}`;
/**
 * An expression marker used text-positions, multi-binding attributes, and
 * attributes with markup-like text values.
 */

exports.marker = marker;
const nodeMarker = `<!--${marker}-->`;
exports.nodeMarker = nodeMarker;
const markerRegex = new RegExp(`${marker}|${nodeMarker}`);
/**
 * Suffix appended to all bound attribute names.
 */

exports.markerRegex = markerRegex;
const boundAttributeSuffix = '$lit$';
/**
 * An updatable Template that tracks the location of dynamic parts.
 */

exports.boundAttributeSuffix = boundAttributeSuffix;

class Template {
  constructor(result, element) {
    this.parts = [];
    this.element = element;
    const nodesToRemove = [];
    const stack = []; // Edge needs all 4 parameters present; IE11 needs 3rd parameter to be null

    const walker = document.createTreeWalker(element.content, 133
    /* NodeFilter.SHOW_{ELEMENT|COMMENT|TEXT} */
    , null, false); // Keeps track of the last index associated with a part. We try to delete
    // unnecessary nodes, but we never want to associate two different parts
    // to the same index. They must have a constant node between.

    let lastPartIndex = 0;
    let index = -1;
    let partIndex = 0;
    const {
      strings,
      values: {
        length
      }
    } = result;

    while (partIndex < length) {
      const node = walker.nextNode();

      if (node === null) {
        // We've exhausted the content inside a nested template element.
        // Because we still have parts (the outer for-loop), we know:
        // - There is a template in the stack
        // - The walker will find a nextNode outside the template
        walker.currentNode = stack.pop();
        continue;
      }

      index++;

      if (node.nodeType === 1
      /* Node.ELEMENT_NODE */
      ) {
          if (node.hasAttributes()) {
            const attributes = node.attributes;
            const {
              length
            } = attributes; // Per
            // https://developer.mozilla.org/en-US/docs/Web/API/NamedNodeMap,
            // attributes are not guaranteed to be returned in document order.
            // In particular, Edge/IE can return them out of order, so we cannot
            // assume a correspondence between part index and attribute index.

            let count = 0;

            for (let i = 0; i < length; i++) {
              if (endsWith(attributes[i].name, boundAttributeSuffix)) {
                count++;
              }
            }

            while (count-- > 0) {
              // Get the template literal section leading up to the first
              // expression in this attribute
              const stringForPart = strings[partIndex]; // Find the attribute name

              const name = lastAttributeNameRegex.exec(stringForPart)[2]; // Find the corresponding attribute
              // All bound attributes have had a suffix added in
              // TemplateResult#getHTML to opt out of special attribute
              // handling. To look up the attribute value we also need to add
              // the suffix.

              const attributeLookupName = name.toLowerCase() + boundAttributeSuffix;
              const attributeValue = node.getAttribute(attributeLookupName);
              node.removeAttribute(attributeLookupName);
              const statics = attributeValue.split(markerRegex);
              this.parts.push({
                type: 'attribute',
                index,
                name,
                strings: statics
              });
              partIndex += statics.length - 1;
            }
          }

          if (node.tagName === 'TEMPLATE') {
            stack.push(node);
            walker.currentNode = node.content;
          }
        } else if (node.nodeType === 3
      /* Node.TEXT_NODE */
      ) {
          const data = node.data;

          if (data.indexOf(marker) >= 0) {
            const parent = node.parentNode;
            const strings = data.split(markerRegex);
            const lastIndex = strings.length - 1; // Generate a new text node for each literal section
            // These nodes are also used as the markers for node parts

            for (let i = 0; i < lastIndex; i++) {
              let insert;
              let s = strings[i];

              if (s === '') {
                insert = createMarker();
              } else {
                const match = lastAttributeNameRegex.exec(s);

                if (match !== null && endsWith(match[2], boundAttributeSuffix)) {
                  s = s.slice(0, match.index) + match[1] + match[2].slice(0, -boundAttributeSuffix.length) + match[3];
                }

                insert = document.createTextNode(s);
              }

              parent.insertBefore(insert, node);
              this.parts.push({
                type: 'node',
                index: ++index
              });
            } // If there's no text, we must insert a comment to mark our place.
            // Else, we can trust it will stick around after cloning.


            if (strings[lastIndex] === '') {
              parent.insertBefore(createMarker(), node);
              nodesToRemove.push(node);
            } else {
              node.data = strings[lastIndex];
            } // We have a part for each match found


            partIndex += lastIndex;
          }
        } else if (node.nodeType === 8
      /* Node.COMMENT_NODE */
      ) {
          if (node.data === marker) {
            const parent = node.parentNode; // Add a new marker node to be the startNode of the Part if any of
            // the following are true:
            //  * We don't have a previousSibling
            //  * The previousSibling is already the start of a previous part

            if (node.previousSibling === null || index === lastPartIndex) {
              index++;
              parent.insertBefore(createMarker(), node);
            }

            lastPartIndex = index;
            this.parts.push({
              type: 'node',
              index
            }); // If we don't have a nextSibling, keep this node so we have an end.
            // Else, we can remove it to save future costs.

            if (node.nextSibling === null) {
              node.data = '';
            } else {
              nodesToRemove.push(node);
              index--;
            }

            partIndex++;
          } else {
            let i = -1;

            while ((i = node.data.indexOf(marker, i + 1)) !== -1) {
              // Comment node has a binding marker inside, make an inactive part
              // The binding won't work, but subsequent bindings will
              // TODO (justinfagnani): consider whether it's even worth it to
              // make bindings in comments work
              this.parts.push({
                type: 'node',
                index: -1
              });
              partIndex++;
            }
          }
        }
    } // Remove text binding nodes after the walk to not disturb the TreeWalker


    for (const n of nodesToRemove) {
      n.parentNode.removeChild(n);
    }
  }

}

exports.Template = Template;

const endsWith = (str, suffix) => {
  const index = str.length - suffix.length;
  return index >= 0 && str.slice(index) === suffix;
};

const isTemplatePartActive = part => part.index !== -1; // Allows `document.createComment('')` to be renamed for a
// small manual size-savings.


exports.isTemplatePartActive = isTemplatePartActive;

const createMarker = () => document.createComment('');
/**
 * This regex extracts the attribute name preceding an attribute-position
 * expression. It does this by matching the syntax allowed for attributes
 * against the string literal directly preceding the expression, assuming that
 * the expression is in an attribute-value position.
 *
 * See attributes in the HTML spec:
 * https://www.w3.org/TR/html5/syntax.html#elements-attributes
 *
 * " \x09\x0a\x0c\x0d" are HTML space characters:
 * https://www.w3.org/TR/html5/infrastructure.html#space-characters
 *
 * "\0-\x1F\x7F-\x9F" are Unicode control characters, which includes every
 * space character except " ".
 *
 * So an attribute is:
 *  * The name: any character except a control character, space character, ('),
 *    ("), ">", "=", or "/"
 *  * Followed by zero or more space characters
 *  * Followed by "="
 *  * Followed by zero or more space characters
 *  * Followed by:
 *    * Any character except space, ('), ("), "<", ">", "=", (`), or
 *    * (") then any non-("), or
 *    * (') then any non-(')
 */


exports.createMarker = createMarker;
const lastAttributeNameRegex = // eslint-disable-next-line no-control-regex
/([ \x09\x0a\x0c\x0d])([^\0-\x1F\x7F-\x9F "'>=/]+)([ \x09\x0a\x0c\x0d]*=[ \x09\x0a\x0c\x0d]*(?:[^ \x09\x0a\x0c\x0d"'`<>=]*|"[^"]*|'[^']*))$/;
exports.lastAttributeNameRegex = lastAttributeNameRegex;
},{}],"../../node_modules/lit-html/lib/modify-template.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.removeNodesFromTemplate = removeNodesFromTemplate;
exports.insertNodeIntoTemplate = insertNodeIntoTemplate;

var _template = require("./template.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * @module shady-render
 */
const walkerNodeFilter = 133
/* NodeFilter.SHOW_{ELEMENT|COMMENT|TEXT} */
;
/**
 * Removes the list of nodes from a Template safely. In addition to removing
 * nodes from the Template, the Template part indices are updated to match
 * the mutated Template DOM.
 *
 * As the template is walked the removal state is tracked and
 * part indices are adjusted as needed.
 *
 * div
 *   div#1 (remove) <-- start removing (removing node is div#1)
 *     div
 *       div#2 (remove)  <-- continue removing (removing node is still div#1)
 *         div
 * div <-- stop removing since previous sibling is the removing node (div#1,
 * removed 4 nodes)
 */

function removeNodesFromTemplate(template, nodesToRemove) {
  const {
    element: {
      content
    },
    parts
  } = template;
  const walker = document.createTreeWalker(content, walkerNodeFilter, null, false);
  let partIndex = nextActiveIndexInTemplateParts(parts);
  let part = parts[partIndex];
  let nodeIndex = -1;
  let removeCount = 0;
  const nodesToRemoveInTemplate = [];
  let currentRemovingNode = null;

  while (walker.nextNode()) {
    nodeIndex++;
    const node = walker.currentNode; // End removal if stepped past the removing node

    if (node.previousSibling === currentRemovingNode) {
      currentRemovingNode = null;
    } // A node to remove was found in the template


    if (nodesToRemove.has(node)) {
      nodesToRemoveInTemplate.push(node); // Track node we're removing

      if (currentRemovingNode === null) {
        currentRemovingNode = node;
      }
    } // When removing, increment count by which to adjust subsequent part indices


    if (currentRemovingNode !== null) {
      removeCount++;
    }

    while (part !== undefined && part.index === nodeIndex) {
      // If part is in a removed node deactivate it by setting index to -1 or
      // adjust the index as needed.
      part.index = currentRemovingNode !== null ? -1 : part.index - removeCount; // go to the next active part.

      partIndex = nextActiveIndexInTemplateParts(parts, partIndex);
      part = parts[partIndex];
    }
  }

  nodesToRemoveInTemplate.forEach(n => n.parentNode.removeChild(n));
}

const countNodes = node => {
  let count = node.nodeType === 11
  /* Node.DOCUMENT_FRAGMENT_NODE */
  ? 0 : 1;
  const walker = document.createTreeWalker(node, walkerNodeFilter, null, false);

  while (walker.nextNode()) {
    count++;
  }

  return count;
};

const nextActiveIndexInTemplateParts = (parts, startIndex = -1) => {
  for (let i = startIndex + 1; i < parts.length; i++) {
    const part = parts[i];

    if ((0, _template.isTemplatePartActive)(part)) {
      return i;
    }
  }

  return -1;
};
/**
 * Inserts the given node into the Template, optionally before the given
 * refNode. In addition to inserting the node into the Template, the Template
 * part indices are updated to match the mutated Template DOM.
 */


function insertNodeIntoTemplate(template, node, refNode = null) {
  const {
    element: {
      content
    },
    parts
  } = template; // If there's no refNode, then put node at end of template.
  // No part indices need to be shifted in this case.

  if (refNode === null || refNode === undefined) {
    content.appendChild(node);
    return;
  }

  const walker = document.createTreeWalker(content, walkerNodeFilter, null, false);
  let partIndex = nextActiveIndexInTemplateParts(parts);
  let insertCount = 0;
  let walkerIndex = -1;

  while (walker.nextNode()) {
    walkerIndex++;
    const walkerNode = walker.currentNode;

    if (walkerNode === refNode) {
      insertCount = countNodes(node);
      refNode.parentNode.insertBefore(node, refNode);
    }

    while (partIndex !== -1 && parts[partIndex].index === walkerIndex) {
      // If we've inserted the node, simply adjust all subsequent parts
      if (insertCount > 0) {
        while (partIndex !== -1) {
          parts[partIndex].index += insertCount;
          partIndex = nextActiveIndexInTemplateParts(parts, partIndex);
        }

        return;
      }

      partIndex = nextActiveIndexInTemplateParts(parts, partIndex);
    }
  }
}
},{"./template.js":"../../node_modules/lit-html/lib/template.js"}],"../../node_modules/lit-html/lib/directive.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.isDirective = exports.directive = void 0;

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
const directives = new WeakMap();
/**
 * Brands a function as a directive factory function so that lit-html will call
 * the function during template rendering, rather than passing as a value.
 *
 * A _directive_ is a function that takes a Part as an argument. It has the
 * signature: `(part: Part) => void`.
 *
 * A directive _factory_ is a function that takes arguments for data and
 * configuration and returns a directive. Users of directive usually refer to
 * the directive factory as the directive. For example, "The repeat directive".
 *
 * Usually a template author will invoke a directive factory in their template
 * with relevant arguments, which will then return a directive function.
 *
 * Here's an example of using the `repeat()` directive factory that takes an
 * array and a function to render an item:
 *
 * ```js
 * html`<ul><${repeat(items, (item) => html`<li>${item}</li>`)}</ul>`
 * ```
 *
 * When `repeat` is invoked, it returns a directive function that closes over
 * `items` and the template function. When the outer template is rendered, the
 * return directive function is called with the Part for the expression.
 * `repeat` then performs it's custom logic to render multiple items.
 *
 * @param f The directive factory function. Must be a function that returns a
 * function of the signature `(part: Part) => void`. The returned function will
 * be called with the part object.
 *
 * @example
 *
 * import {directive, html} from 'lit-html';
 *
 * const immutable = directive((v) => (part) => {
 *   if (part.value !== v) {
 *     part.setValue(v)
 *   }
 * });
 */

const directive = f => (...args) => {
  const d = f(...args);
  directives.set(d, true);
  return d;
};

exports.directive = directive;

const isDirective = o => {
  return typeof o === 'function' && directives.has(o);
};

exports.isDirective = isDirective;
},{}],"../../node_modules/lit-html/lib/part.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.nothing = exports.noChange = void 0;

/**
 * @license
 * Copyright (c) 2018 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * A sentinel value that signals that a value was handled by a directive and
 * should not be written to the DOM.
 */
const noChange = {};
/**
 * A sentinel value that signals a NodePart to fully clear its content.
 */

exports.noChange = noChange;
const nothing = {};
exports.nothing = nothing;
},{}],"../../node_modules/lit-html/lib/template-instance.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.TemplateInstance = void 0;

var _dom = require("./dom.js");

var _template = require("./template.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * @module lit-html
 */

/**
 * An instance of a `Template` that can be attached to the DOM and updated
 * with new values.
 */
class TemplateInstance {
  constructor(template, processor, options) {
    this.__parts = [];
    this.template = template;
    this.processor = processor;
    this.options = options;
  }

  update(values) {
    let i = 0;

    for (const part of this.__parts) {
      if (part !== undefined) {
        part.setValue(values[i]);
      }

      i++;
    }

    for (const part of this.__parts) {
      if (part !== undefined) {
        part.commit();
      }
    }
  }

  _clone() {
    // There are a number of steps in the lifecycle of a template instance's
    // DOM fragment:
    //  1. Clone - create the instance fragment
    //  2. Adopt - adopt into the main document
    //  3. Process - find part markers and create parts
    //  4. Upgrade - upgrade custom elements
    //  5. Update - set node, attribute, property, etc., values
    //  6. Connect - connect to the document. Optional and outside of this
    //     method.
    //
    // We have a few constraints on the ordering of these steps:
    //  * We need to upgrade before updating, so that property values will pass
    //    through any property setters.
    //  * We would like to process before upgrading so that we're sure that the
    //    cloned fragment is inert and not disturbed by self-modifying DOM.
    //  * We want custom elements to upgrade even in disconnected fragments.
    //
    // Given these constraints, with full custom elements support we would
    // prefer the order: Clone, Process, Adopt, Upgrade, Update, Connect
    //
    // But Safari does not implement CustomElementRegistry#upgrade, so we
    // can not implement that order and still have upgrade-before-update and
    // upgrade disconnected fragments. So we instead sacrifice the
    // process-before-upgrade constraint, since in Custom Elements v1 elements
    // must not modify their light DOM in the constructor. We still have issues
    // when co-existing with CEv0 elements like Polymer 1, and with polyfills
    // that don't strictly adhere to the no-modification rule because shadow
    // DOM, which may be created in the constructor, is emulated by being placed
    // in the light DOM.
    //
    // The resulting order is on native is: Clone, Adopt, Upgrade, Process,
    // Update, Connect. document.importNode() performs Clone, Adopt, and Upgrade
    // in one step.
    //
    // The Custom Elements v1 polyfill supports upgrade(), so the order when
    // polyfilled is the more ideal: Clone, Process, Adopt, Upgrade, Update,
    // Connect.
    const fragment = _dom.isCEPolyfill ? this.template.element.content.cloneNode(true) : document.importNode(this.template.element.content, true);
    const stack = [];
    const parts = this.template.parts; // Edge needs all 4 parameters present; IE11 needs 3rd parameter to be null

    const walker = document.createTreeWalker(fragment, 133
    /* NodeFilter.SHOW_{ELEMENT|COMMENT|TEXT} */
    , null, false);
    let partIndex = 0;
    let nodeIndex = 0;
    let part;
    let node = walker.nextNode(); // Loop through all the nodes and parts of a template

    while (partIndex < parts.length) {
      part = parts[partIndex];

      if (!(0, _template.isTemplatePartActive)(part)) {
        this.__parts.push(undefined);

        partIndex++;
        continue;
      } // Progress the tree walker until we find our next part's node.
      // Note that multiple parts may share the same node (attribute parts
      // on a single element), so this loop may not run at all.


      while (nodeIndex < part.index) {
        nodeIndex++;

        if (node.nodeName === 'TEMPLATE') {
          stack.push(node);
          walker.currentNode = node.content;
        }

        if ((node = walker.nextNode()) === null) {
          // We've exhausted the content inside a nested template element.
          // Because we still have parts (the outer for-loop), we know:
          // - There is a template in the stack
          // - The walker will find a nextNode outside the template
          walker.currentNode = stack.pop();
          node = walker.nextNode();
        }
      } // We've arrived at our part's node.


      if (part.type === 'node') {
        const part = this.processor.handleTextExpression(this.options);
        part.insertAfterNode(node.previousSibling);

        this.__parts.push(part);
      } else {
        this.__parts.push(...this.processor.handleAttributeExpressions(node, part.name, part.strings, this.options));
      }

      partIndex++;
    }

    if (_dom.isCEPolyfill) {
      document.adoptNode(fragment);
      customElements.upgrade(fragment);
    }

    return fragment;
  }

}

exports.TemplateInstance = TemplateInstance;
},{"./dom.js":"../../node_modules/lit-html/lib/dom.js","./template.js":"../../node_modules/lit-html/lib/template.js"}],"../../node_modules/lit-html/lib/template-result.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SVGTemplateResult = exports.TemplateResult = void 0;

var _dom = require("./dom.js");

var _template = require("./template.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * @module lit-html
 */
const commentMarker = ` ${_template.marker} `;
/**
 * The return type of `html`, which holds a Template and the values from
 * interpolated expressions.
 */

class TemplateResult {
  constructor(strings, values, type, processor) {
    this.strings = strings;
    this.values = values;
    this.type = type;
    this.processor = processor;
  }
  /**
   * Returns a string of HTML used to create a `<template>` element.
   */


  getHTML() {
    const l = this.strings.length - 1;
    let html = '';
    let isCommentBinding = false;

    for (let i = 0; i < l; i++) {
      const s = this.strings[i]; // For each binding we want to determine the kind of marker to insert
      // into the template source before it's parsed by the browser's HTML
      // parser. The marker type is based on whether the expression is in an
      // attribute, text, or comment position.
      //   * For node-position bindings we insert a comment with the marker
      //     sentinel as its text content, like <!--{{lit-guid}}-->.
      //   * For attribute bindings we insert just the marker sentinel for the
      //     first binding, so that we support unquoted attribute bindings.
      //     Subsequent bindings can use a comment marker because multi-binding
      //     attributes must be quoted.
      //   * For comment bindings we insert just the marker sentinel so we don't
      //     close the comment.
      //
      // The following code scans the template source, but is *not* an HTML
      // parser. We don't need to track the tree structure of the HTML, only
      // whether a binding is inside a comment, and if not, if it appears to be
      // the first binding in an attribute.

      const commentOpen = s.lastIndexOf('<!--'); // We're in comment position if we have a comment open with no following
      // comment close. Because <-- can appear in an attribute value there can
      // be false positives.

      isCommentBinding = (commentOpen > -1 || isCommentBinding) && s.indexOf('-->', commentOpen + 1) === -1; // Check to see if we have an attribute-like sequence preceding the
      // expression. This can match "name=value" like structures in text,
      // comments, and attribute values, so there can be false-positives.

      const attributeMatch = _template.lastAttributeNameRegex.exec(s);

      if (attributeMatch === null) {
        // We're only in this branch if we don't have a attribute-like
        // preceding sequence. For comments, this guards against unusual
        // attribute values like <div foo="<!--${'bar'}">. Cases like
        // <!-- foo=${'bar'}--> are handled correctly in the attribute branch
        // below.
        html += s + (isCommentBinding ? commentMarker : _template.nodeMarker);
      } else {
        // For attributes we use just a marker sentinel, and also append a
        // $lit$ suffix to the name to opt-out of attribute-specific parsing
        // that IE and Edge do for style and certain SVG attributes.
        html += s.substr(0, attributeMatch.index) + attributeMatch[1] + attributeMatch[2] + _template.boundAttributeSuffix + attributeMatch[3] + _template.marker;
      }
    }

    html += this.strings[l];
    return html;
  }

  getTemplateElement() {
    const template = document.createElement('template');
    template.innerHTML = this.getHTML();
    return template;
  }

}
/**
 * A TemplateResult for SVG fragments.
 *
 * This class wraps HTML in an `<svg>` tag in order to parse its contents in the
 * SVG namespace, then modifies the template to remove the `<svg>` tag so that
 * clones only container the original fragment.
 */


exports.TemplateResult = TemplateResult;

class SVGTemplateResult extends TemplateResult {
  getHTML() {
    return `<svg>${super.getHTML()}</svg>`;
  }

  getTemplateElement() {
    const template = super.getTemplateElement();
    const content = template.content;
    const svgElement = content.firstChild;
    content.removeChild(svgElement);
    (0, _dom.reparentNodes)(content, svgElement.firstChild);
    return template;
  }

}

exports.SVGTemplateResult = SVGTemplateResult;
},{"./dom.js":"../../node_modules/lit-html/lib/dom.js","./template.js":"../../node_modules/lit-html/lib/template.js"}],"../../node_modules/lit-html/lib/parts.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.EventPart = exports.PropertyPart = exports.PropertyCommitter = exports.BooleanAttributePart = exports.NodePart = exports.AttributePart = exports.AttributeCommitter = exports.isIterable = exports.isPrimitive = void 0;

var _directive = require("./directive.js");

var _dom = require("./dom.js");

var _part = require("./part.js");

var _templateInstance = require("./template-instance.js");

var _templateResult = require("./template-result.js");

var _template = require("./template.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * @module lit-html
 */
const isPrimitive = value => {
  return value === null || !(typeof value === 'object' || typeof value === 'function');
};

exports.isPrimitive = isPrimitive;

const isIterable = value => {
  return Array.isArray(value) || // eslint-disable-next-line @typescript-eslint/no-explicit-any
  !!(value && value[Symbol.iterator]);
};
/**
 * Writes attribute values to the DOM for a group of AttributeParts bound to a
 * single attribute. The value is only set once even if there are multiple parts
 * for an attribute.
 */


exports.isIterable = isIterable;

class AttributeCommitter {
  constructor(element, name, strings) {
    this.dirty = true;
    this.element = element;
    this.name = name;
    this.strings = strings;
    this.parts = [];

    for (let i = 0; i < strings.length - 1; i++) {
      this.parts[i] = this._createPart();
    }
  }
  /**
   * Creates a single part. Override this to create a differnt type of part.
   */


  _createPart() {
    return new AttributePart(this);
  }

  _getValue() {
    const strings = this.strings;
    const l = strings.length - 1;
    let text = '';

    for (let i = 0; i < l; i++) {
      text += strings[i];
      const part = this.parts[i];

      if (part !== undefined) {
        const v = part.value;

        if (isPrimitive(v) || !isIterable(v)) {
          text += typeof v === 'string' ? v : String(v);
        } else {
          for (const t of v) {
            text += typeof t === 'string' ? t : String(t);
          }
        }
      }
    }

    text += strings[l];
    return text;
  }

  commit() {
    if (this.dirty) {
      this.dirty = false;
      this.element.setAttribute(this.name, this._getValue());
    }
  }

}
/**
 * A Part that controls all or part of an attribute value.
 */


exports.AttributeCommitter = AttributeCommitter;

class AttributePart {
  constructor(committer) {
    this.value = undefined;
    this.committer = committer;
  }

  setValue(value) {
    if (value !== _part.noChange && (!isPrimitive(value) || value !== this.value)) {
      this.value = value; // If the value is a not a directive, dirty the committer so that it'll
      // call setAttribute. If the value is a directive, it'll dirty the
      // committer if it calls setValue().

      if (!(0, _directive.isDirective)(value)) {
        this.committer.dirty = true;
      }
    }
  }

  commit() {
    while ((0, _directive.isDirective)(this.value)) {
      const directive = this.value;
      this.value = _part.noChange;
      directive(this);
    }

    if (this.value === _part.noChange) {
      return;
    }

    this.committer.commit();
  }

}
/**
 * A Part that controls a location within a Node tree. Like a Range, NodePart
 * has start and end locations and can set and update the Nodes between those
 * locations.
 *
 * NodeParts support several value types: primitives, Nodes, TemplateResults,
 * as well as arrays and iterables of those types.
 */


exports.AttributePart = AttributePart;

class NodePart {
  constructor(options) {
    this.value = undefined;
    this.__pendingValue = undefined;
    this.options = options;
  }
  /**
   * Appends this part into a container.
   *
   * This part must be empty, as its contents are not automatically moved.
   */


  appendInto(container) {
    this.startNode = container.appendChild((0, _template.createMarker)());
    this.endNode = container.appendChild((0, _template.createMarker)());
  }
  /**
   * Inserts this part after the `ref` node (between `ref` and `ref`'s next
   * sibling). Both `ref` and its next sibling must be static, unchanging nodes
   * such as those that appear in a literal section of a template.
   *
   * This part must be empty, as its contents are not automatically moved.
   */


  insertAfterNode(ref) {
    this.startNode = ref;
    this.endNode = ref.nextSibling;
  }
  /**
   * Appends this part into a parent part.
   *
   * This part must be empty, as its contents are not automatically moved.
   */


  appendIntoPart(part) {
    part.__insert(this.startNode = (0, _template.createMarker)());

    part.__insert(this.endNode = (0, _template.createMarker)());
  }
  /**
   * Inserts this part after the `ref` part.
   *
   * This part must be empty, as its contents are not automatically moved.
   */


  insertAfterPart(ref) {
    ref.__insert(this.startNode = (0, _template.createMarker)());

    this.endNode = ref.endNode;
    ref.endNode = this.startNode;
  }

  setValue(value) {
    this.__pendingValue = value;
  }

  commit() {
    if (this.startNode.parentNode === null) {
      return;
    }

    while ((0, _directive.isDirective)(this.__pendingValue)) {
      const directive = this.__pendingValue;
      this.__pendingValue = _part.noChange;
      directive(this);
    }

    const value = this.__pendingValue;

    if (value === _part.noChange) {
      return;
    }

    if (isPrimitive(value)) {
      if (value !== this.value) {
        this.__commitText(value);
      }
    } else if (value instanceof _templateResult.TemplateResult) {
      this.__commitTemplateResult(value);
    } else if (value instanceof Node) {
      this.__commitNode(value);
    } else if (isIterable(value)) {
      this.__commitIterable(value);
    } else if (value === _part.nothing) {
      this.value = _part.nothing;
      this.clear();
    } else {
      // Fallback, will render the string representation
      this.__commitText(value);
    }
  }

  __insert(node) {
    this.endNode.parentNode.insertBefore(node, this.endNode);
  }

  __commitNode(value) {
    if (this.value === value) {
      return;
    }

    this.clear();

    this.__insert(value);

    this.value = value;
  }

  __commitText(value) {
    const node = this.startNode.nextSibling;
    value = value == null ? '' : value; // If `value` isn't already a string, we explicitly convert it here in case
    // it can't be implicitly converted - i.e. it's a symbol.

    const valueAsString = typeof value === 'string' ? value : String(value);

    if (node === this.endNode.previousSibling && node.nodeType === 3
    /* Node.TEXT_NODE */
    ) {
        // If we only have a single text node between the markers, we can just
        // set its value, rather than replacing it.
        // TODO(justinfagnani): Can we just check if this.value is primitive?
        node.data = valueAsString;
      } else {
      this.__commitNode(document.createTextNode(valueAsString));
    }

    this.value = value;
  }

  __commitTemplateResult(value) {
    const template = this.options.templateFactory(value);

    if (this.value instanceof _templateInstance.TemplateInstance && this.value.template === template) {
      this.value.update(value.values);
    } else {
      // Make sure we propagate the template processor from the TemplateResult
      // so that we use its syntax extension, etc. The template factory comes
      // from the render function options so that it can control template
      // caching and preprocessing.
      const instance = new _templateInstance.TemplateInstance(template, value.processor, this.options);

      const fragment = instance._clone();

      instance.update(value.values);

      this.__commitNode(fragment);

      this.value = instance;
    }
  }

  __commitIterable(value) {
    // For an Iterable, we create a new InstancePart per item, then set its
    // value to the item. This is a little bit of overhead for every item in
    // an Iterable, but it lets us recurse easily and efficiently update Arrays
    // of TemplateResults that will be commonly returned from expressions like:
    // array.map((i) => html`${i}`), by reusing existing TemplateInstances.
    // If _value is an array, then the previous render was of an
    // iterable and _value will contain the NodeParts from the previous
    // render. If _value is not an array, clear this part and make a new
    // array for NodeParts.
    if (!Array.isArray(this.value)) {
      this.value = [];
      this.clear();
    } // Lets us keep track of how many items we stamped so we can clear leftover
    // items from a previous render


    const itemParts = this.value;
    let partIndex = 0;
    let itemPart;

    for (const item of value) {
      // Try to reuse an existing part
      itemPart = itemParts[partIndex]; // If no existing part, create a new one

      if (itemPart === undefined) {
        itemPart = new NodePart(this.options);
        itemParts.push(itemPart);

        if (partIndex === 0) {
          itemPart.appendIntoPart(this);
        } else {
          itemPart.insertAfterPart(itemParts[partIndex - 1]);
        }
      }

      itemPart.setValue(item);
      itemPart.commit();
      partIndex++;
    }

    if (partIndex < itemParts.length) {
      // Truncate the parts array so _value reflects the current state
      itemParts.length = partIndex;
      this.clear(itemPart && itemPart.endNode);
    }
  }

  clear(startNode = this.startNode) {
    (0, _dom.removeNodes)(this.startNode.parentNode, startNode.nextSibling, this.endNode);
  }

}
/**
 * Implements a boolean attribute, roughly as defined in the HTML
 * specification.
 *
 * If the value is truthy, then the attribute is present with a value of
 * ''. If the value is falsey, the attribute is removed.
 */


exports.NodePart = NodePart;

class BooleanAttributePart {
  constructor(element, name, strings) {
    this.value = undefined;
    this.__pendingValue = undefined;

    if (strings.length !== 2 || strings[0] !== '' || strings[1] !== '') {
      throw new Error('Boolean attributes can only contain a single expression');
    }

    this.element = element;
    this.name = name;
    this.strings = strings;
  }

  setValue(value) {
    this.__pendingValue = value;
  }

  commit() {
    while ((0, _directive.isDirective)(this.__pendingValue)) {
      const directive = this.__pendingValue;
      this.__pendingValue = _part.noChange;
      directive(this);
    }

    if (this.__pendingValue === _part.noChange) {
      return;
    }

    const value = !!this.__pendingValue;

    if (this.value !== value) {
      if (value) {
        this.element.setAttribute(this.name, '');
      } else {
        this.element.removeAttribute(this.name);
      }

      this.value = value;
    }

    this.__pendingValue = _part.noChange;
  }

}
/**
 * Sets attribute values for PropertyParts, so that the value is only set once
 * even if there are multiple parts for a property.
 *
 * If an expression controls the whole property value, then the value is simply
 * assigned to the property under control. If there are string literals or
 * multiple expressions, then the strings are expressions are interpolated into
 * a string first.
 */


exports.BooleanAttributePart = BooleanAttributePart;

class PropertyCommitter extends AttributeCommitter {
  constructor(element, name, strings) {
    super(element, name, strings);
    this.single = strings.length === 2 && strings[0] === '' && strings[1] === '';
  }

  _createPart() {
    return new PropertyPart(this);
  }

  _getValue() {
    if (this.single) {
      return this.parts[0].value;
    }

    return super._getValue();
  }

  commit() {
    if (this.dirty) {
      this.dirty = false; // eslint-disable-next-line @typescript-eslint/no-explicit-any

      this.element[this.name] = this._getValue();
    }
  }

}

exports.PropertyCommitter = PropertyCommitter;

class PropertyPart extends AttributePart {} // Detect event listener options support. If the `capture` property is read
// from the options object, then options are supported. If not, then the third
// argument to add/removeEventListener is interpreted as the boolean capture
// value so we should only pass the `capture` property.


exports.PropertyPart = PropertyPart;
let eventOptionsSupported = false; // Wrap into an IIFE because MS Edge <= v41 does not support having try/catch
// blocks right into the body of a module

(() => {
  try {
    const options = {
      get capture() {
        eventOptionsSupported = true;
        return false;
      }

    }; // eslint-disable-next-line @typescript-eslint/no-explicit-any

    window.addEventListener('test', options, options); // eslint-disable-next-line @typescript-eslint/no-explicit-any

    window.removeEventListener('test', options, options);
  } catch (_e) {// event options not supported
  }
})();

class EventPart {
  constructor(element, eventName, eventContext) {
    this.value = undefined;
    this.__pendingValue = undefined;
    this.element = element;
    this.eventName = eventName;
    this.eventContext = eventContext;

    this.__boundHandleEvent = e => this.handleEvent(e);
  }

  setValue(value) {
    this.__pendingValue = value;
  }

  commit() {
    while ((0, _directive.isDirective)(this.__pendingValue)) {
      const directive = this.__pendingValue;
      this.__pendingValue = _part.noChange;
      directive(this);
    }

    if (this.__pendingValue === _part.noChange) {
      return;
    }

    const newListener = this.__pendingValue;
    const oldListener = this.value;
    const shouldRemoveListener = newListener == null || oldListener != null && (newListener.capture !== oldListener.capture || newListener.once !== oldListener.once || newListener.passive !== oldListener.passive);
    const shouldAddListener = newListener != null && (oldListener == null || shouldRemoveListener);

    if (shouldRemoveListener) {
      this.element.removeEventListener(this.eventName, this.__boundHandleEvent, this.__options);
    }

    if (shouldAddListener) {
      this.__options = getOptions(newListener);
      this.element.addEventListener(this.eventName, this.__boundHandleEvent, this.__options);
    }

    this.value = newListener;
    this.__pendingValue = _part.noChange;
  }

  handleEvent(event) {
    if (typeof this.value === 'function') {
      this.value.call(this.eventContext || this.element, event);
    } else {
      this.value.handleEvent(event);
    }
  }

} // We copy options because of the inconsistent behavior of browsers when reading
// the third argument of add/removeEventListener. IE11 doesn't support options
// at all. Chrome 41 only reads `capture` if the argument is an object.


exports.EventPart = EventPart;

const getOptions = o => o && (eventOptionsSupported ? {
  capture: o.capture,
  passive: o.passive,
  once: o.once
} : o.capture);
},{"./directive.js":"../../node_modules/lit-html/lib/directive.js","./dom.js":"../../node_modules/lit-html/lib/dom.js","./part.js":"../../node_modules/lit-html/lib/part.js","./template-instance.js":"../../node_modules/lit-html/lib/template-instance.js","./template-result.js":"../../node_modules/lit-html/lib/template-result.js","./template.js":"../../node_modules/lit-html/lib/template.js"}],"../../node_modules/lit-html/lib/template-factory.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.templateFactory = templateFactory;
exports.templateCaches = void 0;

var _template = require("./template.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * The default TemplateFactory which caches Templates keyed on
 * result.type and result.strings.
 */
function templateFactory(result) {
  let templateCache = templateCaches.get(result.type);

  if (templateCache === undefined) {
    templateCache = {
      stringsArray: new WeakMap(),
      keyString: new Map()
    };
    templateCaches.set(result.type, templateCache);
  }

  let template = templateCache.stringsArray.get(result.strings);

  if (template !== undefined) {
    return template;
  } // If the TemplateStringsArray is new, generate a key from the strings
  // This key is shared between all templates with identical content


  const key = result.strings.join(_template.marker); // Check if we already have a Template for this key

  template = templateCache.keyString.get(key);

  if (template === undefined) {
    // If we have not seen this key before, create a new Template
    template = new _template.Template(result, result.getTemplateElement()); // Cache the Template for this key

    templateCache.keyString.set(key, template);
  } // Cache all future queries for this TemplateStringsArray


  templateCache.stringsArray.set(result.strings, template);
  return template;
}

const templateCaches = new Map();
exports.templateCaches = templateCaches;
},{"./template.js":"../../node_modules/lit-html/lib/template.js"}],"../../node_modules/lit-html/lib/render.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.render = exports.parts = void 0;

var _dom = require("./dom.js");

var _parts = require("./parts.js");

var _templateFactory = require("./template-factory.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * @module lit-html
 */
const parts = new WeakMap();
/**
 * Renders a template result or other value to a container.
 *
 * To update a container with new values, reevaluate the template literal and
 * call `render` with the new result.
 *
 * @param result Any value renderable by NodePart - typically a TemplateResult
 *     created by evaluating a template tag like `html` or `svg`.
 * @param container A DOM parent to render to. The entire contents are either
 *     replaced, or efficiently updated if the same result type was previous
 *     rendered there.
 * @param options RenderOptions for the entire render tree rendered to this
 *     container. Render options must *not* change between renders to the same
 *     container, as those changes will not effect previously rendered DOM.
 */

exports.parts = parts;

const render = (result, container, options) => {
  let part = parts.get(container);

  if (part === undefined) {
    (0, _dom.removeNodes)(container, container.firstChild);
    parts.set(container, part = new _parts.NodePart(Object.assign({
      templateFactory: _templateFactory.templateFactory
    }, options)));
    part.appendInto(container);
  }

  part.setValue(result);
  part.commit();
};

exports.render = render;
},{"./dom.js":"../../node_modules/lit-html/lib/dom.js","./parts.js":"../../node_modules/lit-html/lib/parts.js","./template-factory.js":"../../node_modules/lit-html/lib/template-factory.js"}],"../../node_modules/lit-html/lib/default-template-processor.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.defaultTemplateProcessor = exports.DefaultTemplateProcessor = void 0;

var _parts = require("./parts.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * Creates Parts when a template is instantiated.
 */
class DefaultTemplateProcessor {
  /**
   * Create parts for an attribute-position binding, given the event, attribute
   * name, and string literals.
   *
   * @param element The element containing the binding
   * @param name  The attribute name
   * @param strings The string literals. There are always at least two strings,
   *   event for fully-controlled bindings with a single expression.
   */
  handleAttributeExpressions(element, name, strings, options) {
    const prefix = name[0];

    if (prefix === '.') {
      const committer = new _parts.PropertyCommitter(element, name.slice(1), strings);
      return committer.parts;
    }

    if (prefix === '@') {
      return [new _parts.EventPart(element, name.slice(1), options.eventContext)];
    }

    if (prefix === '?') {
      return [new _parts.BooleanAttributePart(element, name.slice(1), strings)];
    }

    const committer = new _parts.AttributeCommitter(element, name, strings);
    return committer.parts;
  }
  /**
   * Create parts for a text-position binding.
   * @param templateFactory
   */


  handleTextExpression(options) {
    return new _parts.NodePart(options);
  }

}

exports.DefaultTemplateProcessor = DefaultTemplateProcessor;
const defaultTemplateProcessor = new DefaultTemplateProcessor();
exports.defaultTemplateProcessor = defaultTemplateProcessor;
},{"./parts.js":"../../node_modules/lit-html/lib/parts.js"}],"../../node_modules/lit-html/lit-html.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "DefaultTemplateProcessor", {
  enumerable: true,
  get: function () {
    return _defaultTemplateProcessor.DefaultTemplateProcessor;
  }
});
Object.defineProperty(exports, "defaultTemplateProcessor", {
  enumerable: true,
  get: function () {
    return _defaultTemplateProcessor.defaultTemplateProcessor;
  }
});
Object.defineProperty(exports, "SVGTemplateResult", {
  enumerable: true,
  get: function () {
    return _templateResult.SVGTemplateResult;
  }
});
Object.defineProperty(exports, "TemplateResult", {
  enumerable: true,
  get: function () {
    return _templateResult.TemplateResult;
  }
});
Object.defineProperty(exports, "directive", {
  enumerable: true,
  get: function () {
    return _directive.directive;
  }
});
Object.defineProperty(exports, "isDirective", {
  enumerable: true,
  get: function () {
    return _directive.isDirective;
  }
});
Object.defineProperty(exports, "removeNodes", {
  enumerable: true,
  get: function () {
    return _dom.removeNodes;
  }
});
Object.defineProperty(exports, "reparentNodes", {
  enumerable: true,
  get: function () {
    return _dom.reparentNodes;
  }
});
Object.defineProperty(exports, "noChange", {
  enumerable: true,
  get: function () {
    return _part.noChange;
  }
});
Object.defineProperty(exports, "nothing", {
  enumerable: true,
  get: function () {
    return _part.nothing;
  }
});
Object.defineProperty(exports, "AttributeCommitter", {
  enumerable: true,
  get: function () {
    return _parts.AttributeCommitter;
  }
});
Object.defineProperty(exports, "AttributePart", {
  enumerable: true,
  get: function () {
    return _parts.AttributePart;
  }
});
Object.defineProperty(exports, "BooleanAttributePart", {
  enumerable: true,
  get: function () {
    return _parts.BooleanAttributePart;
  }
});
Object.defineProperty(exports, "EventPart", {
  enumerable: true,
  get: function () {
    return _parts.EventPart;
  }
});
Object.defineProperty(exports, "isIterable", {
  enumerable: true,
  get: function () {
    return _parts.isIterable;
  }
});
Object.defineProperty(exports, "isPrimitive", {
  enumerable: true,
  get: function () {
    return _parts.isPrimitive;
  }
});
Object.defineProperty(exports, "NodePart", {
  enumerable: true,
  get: function () {
    return _parts.NodePart;
  }
});
Object.defineProperty(exports, "PropertyCommitter", {
  enumerable: true,
  get: function () {
    return _parts.PropertyCommitter;
  }
});
Object.defineProperty(exports, "PropertyPart", {
  enumerable: true,
  get: function () {
    return _parts.PropertyPart;
  }
});
Object.defineProperty(exports, "parts", {
  enumerable: true,
  get: function () {
    return _render.parts;
  }
});
Object.defineProperty(exports, "render", {
  enumerable: true,
  get: function () {
    return _render.render;
  }
});
Object.defineProperty(exports, "templateCaches", {
  enumerable: true,
  get: function () {
    return _templateFactory.templateCaches;
  }
});
Object.defineProperty(exports, "templateFactory", {
  enumerable: true,
  get: function () {
    return _templateFactory.templateFactory;
  }
});
Object.defineProperty(exports, "TemplateInstance", {
  enumerable: true,
  get: function () {
    return _templateInstance.TemplateInstance;
  }
});
Object.defineProperty(exports, "createMarker", {
  enumerable: true,
  get: function () {
    return _template.createMarker;
  }
});
Object.defineProperty(exports, "isTemplatePartActive", {
  enumerable: true,
  get: function () {
    return _template.isTemplatePartActive;
  }
});
Object.defineProperty(exports, "Template", {
  enumerable: true,
  get: function () {
    return _template.Template;
  }
});
exports.svg = exports.html = void 0;

var _defaultTemplateProcessor = require("./lib/default-template-processor.js");

var _templateResult = require("./lib/template-result.js");

var _directive = require("./lib/directive.js");

var _dom = require("./lib/dom.js");

var _part = require("./lib/part.js");

var _parts = require("./lib/parts.js");

var _render = require("./lib/render.js");

var _templateFactory = require("./lib/template-factory.js");

var _templateInstance = require("./lib/template-instance.js");

var _template = require("./lib/template.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 *
 * Main lit-html module.
 *
 * Main exports:
 *
 * -  [[html]]
 * -  [[svg]]
 * -  [[render]]
 *
 * @module lit-html
 * @preferred
 */

/**
 * Do not remove this comment; it keeps typedoc from misplacing the module
 * docs.
 */
// TODO(justinfagnani): remove line when we get NodePart moving methods
// IMPORTANT: do not change the property name or the assignment expression.
// This line will be used in regexes to search for lit-html usage.
// TODO(justinfagnani): inject version number at build time
if (typeof window !== 'undefined') {
  (window['litHtmlVersions'] || (window['litHtmlVersions'] = [])).push('1.2.1');
}
/**
 * Interprets a template literal as an HTML template that can efficiently
 * render to and update a container.
 */


const html = (strings, ...values) => new _templateResult.TemplateResult(strings, values, 'html', _defaultTemplateProcessor.defaultTemplateProcessor);
/**
 * Interprets a template literal as an SVG template that can efficiently
 * render to and update a container.
 */


exports.html = html;

const svg = (strings, ...values) => new _templateResult.SVGTemplateResult(strings, values, 'svg', _defaultTemplateProcessor.defaultTemplateProcessor);

exports.svg = svg;
},{"./lib/default-template-processor.js":"../../node_modules/lit-html/lib/default-template-processor.js","./lib/template-result.js":"../../node_modules/lit-html/lib/template-result.js","./lib/directive.js":"../../node_modules/lit-html/lib/directive.js","./lib/dom.js":"../../node_modules/lit-html/lib/dom.js","./lib/part.js":"../../node_modules/lit-html/lib/part.js","./lib/parts.js":"../../node_modules/lit-html/lib/parts.js","./lib/render.js":"../../node_modules/lit-html/lib/render.js","./lib/template-factory.js":"../../node_modules/lit-html/lib/template-factory.js","./lib/template-instance.js":"../../node_modules/lit-html/lib/template-instance.js","./lib/template.js":"../../node_modules/lit-html/lib/template.js"}],"../../node_modules/lit-html/lib/shady-render.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "html", {
  enumerable: true,
  get: function () {
    return _litHtml.html;
  }
});
Object.defineProperty(exports, "svg", {
  enumerable: true,
  get: function () {
    return _litHtml.svg;
  }
});
Object.defineProperty(exports, "TemplateResult", {
  enumerable: true,
  get: function () {
    return _litHtml.TemplateResult;
  }
});
exports.render = void 0;

var _dom = require("./dom.js");

var _modifyTemplate = require("./modify-template.js");

var _render = require("./render.js");

var _templateFactory = require("./template-factory.js");

var _templateInstance = require("./template-instance.js");

var _template = require("./template.js");

var _litHtml = require("../lit-html.js");

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * Module to add shady DOM/shady CSS polyfill support to lit-html template
 * rendering. See the [[render]] method for details.
 *
 * @module shady-render
 * @preferred
 */

/**
 * Do not remove this comment; it keeps typedoc from misplacing the module
 * docs.
 */
// Get a key to lookup in `templateCaches`.
const getTemplateCacheKey = (type, scopeName) => `${type}--${scopeName}`;

let compatibleShadyCSSVersion = true;

if (typeof window.ShadyCSS === 'undefined') {
  compatibleShadyCSSVersion = false;
} else if (typeof window.ShadyCSS.prepareTemplateDom === 'undefined') {
  console.warn(`Incompatible ShadyCSS version detected. ` + `Please update to at least @webcomponents/webcomponentsjs@2.0.2 and ` + `@webcomponents/shadycss@1.3.1.`);
  compatibleShadyCSSVersion = false;
}
/**
 * Template factory which scopes template DOM using ShadyCSS.
 * @param scopeName {string}
 */


const shadyTemplateFactory = scopeName => result => {
  const cacheKey = getTemplateCacheKey(result.type, scopeName);

  let templateCache = _templateFactory.templateCaches.get(cacheKey);

  if (templateCache === undefined) {
    templateCache = {
      stringsArray: new WeakMap(),
      keyString: new Map()
    };

    _templateFactory.templateCaches.set(cacheKey, templateCache);
  }

  let template = templateCache.stringsArray.get(result.strings);

  if (template !== undefined) {
    return template;
  }

  const key = result.strings.join(_template.marker);
  template = templateCache.keyString.get(key);

  if (template === undefined) {
    const element = result.getTemplateElement();

    if (compatibleShadyCSSVersion) {
      window.ShadyCSS.prepareTemplateDom(element, scopeName);
    }

    template = new _template.Template(result, element);
    templateCache.keyString.set(key, template);
  }

  templateCache.stringsArray.set(result.strings, template);
  return template;
};

const TEMPLATE_TYPES = ['html', 'svg'];
/**
 * Removes all style elements from Templates for the given scopeName.
 */

const removeStylesFromLitTemplates = scopeName => {
  TEMPLATE_TYPES.forEach(type => {
    const templates = _templateFactory.templateCaches.get(getTemplateCacheKey(type, scopeName));

    if (templates !== undefined) {
      templates.keyString.forEach(template => {
        const {
          element: {
            content
          }
        } = template; // IE 11 doesn't support the iterable param Set constructor

        const styles = new Set();
        Array.from(content.querySelectorAll('style')).forEach(s => {
          styles.add(s);
        });
        (0, _modifyTemplate.removeNodesFromTemplate)(template, styles);
      });
    }
  });
};

const shadyRenderSet = new Set();
/**
 * For the given scope name, ensures that ShadyCSS style scoping is performed.
 * This is done just once per scope name so the fragment and template cannot
 * be modified.
 * (1) extracts styles from the rendered fragment and hands them to ShadyCSS
 * to be scoped and appended to the document
 * (2) removes style elements from all lit-html Templates for this scope name.
 *
 * Note, <style> elements can only be placed into templates for the
 * initial rendering of the scope. If <style> elements are included in templates
 * dynamically rendered to the scope (after the first scope render), they will
 * not be scoped and the <style> will be left in the template and rendered
 * output.
 */

const prepareTemplateStyles = (scopeName, renderedDOM, template) => {
  shadyRenderSet.add(scopeName); // If `renderedDOM` is stamped from a Template, then we need to edit that
  // Template's underlying template element. Otherwise, we create one here
  // to give to ShadyCSS, which still requires one while scoping.

  const templateElement = !!template ? template.element : document.createElement('template'); // Move styles out of rendered DOM and store.

  const styles = renderedDOM.querySelectorAll('style');
  const {
    length
  } = styles; // If there are no styles, skip unnecessary work

  if (length === 0) {
    // Ensure prepareTemplateStyles is called to support adding
    // styles via `prepareAdoptedCssText` since that requires that
    // `prepareTemplateStyles` is called.
    //
    // ShadyCSS will only update styles containing @apply in the template
    // given to `prepareTemplateStyles`. If no lit Template was given,
    // ShadyCSS will not be able to update uses of @apply in any relevant
    // template. However, this is not a problem because we only create the
    // template for the purpose of supporting `prepareAdoptedCssText`,
    // which doesn't support @apply at all.
    window.ShadyCSS.prepareTemplateStyles(templateElement, scopeName);
    return;
  }

  const condensedStyle = document.createElement('style'); // Collect styles into a single style. This helps us make sure ShadyCSS
  // manipulations will not prevent us from being able to fix up template
  // part indices.
  // NOTE: collecting styles is inefficient for browsers but ShadyCSS
  // currently does this anyway. When it does not, this should be changed.

  for (let i = 0; i < length; i++) {
    const style = styles[i];
    style.parentNode.removeChild(style);
    condensedStyle.textContent += style.textContent;
  } // Remove styles from nested templates in this scope.


  removeStylesFromLitTemplates(scopeName); // And then put the condensed style into the "root" template passed in as
  // `template`.

  const content = templateElement.content;

  if (!!template) {
    (0, _modifyTemplate.insertNodeIntoTemplate)(template, condensedStyle, content.firstChild);
  } else {
    content.insertBefore(condensedStyle, content.firstChild);
  } // Note, it's important that ShadyCSS gets the template that `lit-html`
  // will actually render so that it can update the style inside when
  // needed (e.g. @apply native Shadow DOM case).


  window.ShadyCSS.prepareTemplateStyles(templateElement, scopeName);
  const style = content.querySelector('style');

  if (window.ShadyCSS.nativeShadow && style !== null) {
    // When in native Shadow DOM, ensure the style created by ShadyCSS is
    // included in initially rendered output (`renderedDOM`).
    renderedDOM.insertBefore(style.cloneNode(true), renderedDOM.firstChild);
  } else if (!!template) {
    // When no style is left in the template, parts will be broken as a
    // result. To fix this, we put back the style node ShadyCSS removed
    // and then tell lit to remove that node from the template.
    // There can be no style in the template in 2 cases (1) when Shady DOM
    // is in use, ShadyCSS removes all styles, (2) when native Shadow DOM
    // is in use ShadyCSS removes the style if it contains no content.
    // NOTE, ShadyCSS creates its own style so we can safely add/remove
    // `condensedStyle` here.
    content.insertBefore(condensedStyle, content.firstChild);
    const removes = new Set();
    removes.add(condensedStyle);
    (0, _modifyTemplate.removeNodesFromTemplate)(template, removes);
  }
};
/**
 * Extension to the standard `render` method which supports rendering
 * to ShadowRoots when the ShadyDOM (https://github.com/webcomponents/shadydom)
 * and ShadyCSS (https://github.com/webcomponents/shadycss) polyfills are used
 * or when the webcomponentsjs
 * (https://github.com/webcomponents/webcomponentsjs) polyfill is used.
 *
 * Adds a `scopeName` option which is used to scope element DOM and stylesheets
 * when native ShadowDOM is unavailable. The `scopeName` will be added to
 * the class attribute of all rendered DOM. In addition, any style elements will
 * be automatically re-written with this `scopeName` selector and moved out
 * of the rendered DOM and into the document `<head>`.
 *
 * It is common to use this render method in conjunction with a custom element
 * which renders a shadowRoot. When this is done, typically the element's
 * `localName` should be used as the `scopeName`.
 *
 * In addition to DOM scoping, ShadyCSS also supports a basic shim for css
 * custom properties (needed only on older browsers like IE11) and a shim for
 * a deprecated feature called `@apply` that supports applying a set of css
 * custom properties to a given location.
 *
 * Usage considerations:
 *
 * * Part values in `<style>` elements are only applied the first time a given
 * `scopeName` renders. Subsequent changes to parts in style elements will have
 * no effect. Because of this, parts in style elements should only be used for
 * values that will never change, for example parts that set scope-wide theme
 * values or parts which render shared style elements.
 *
 * * Note, due to a limitation of the ShadyDOM polyfill, rendering in a
 * custom element's `constructor` is not supported. Instead rendering should
 * either done asynchronously, for example at microtask timing (for example
 * `Promise.resolve()`), or be deferred until the first time the element's
 * `connectedCallback` runs.
 *
 * Usage considerations when using shimmed custom properties or `@apply`:
 *
 * * Whenever any dynamic changes are made which affect
 * css custom properties, `ShadyCSS.styleElement(element)` must be called
 * to update the element. There are two cases when this is needed:
 * (1) the element is connected to a new parent, (2) a class is added to the
 * element that causes it to match different custom properties.
 * To address the first case when rendering a custom element, `styleElement`
 * should be called in the element's `connectedCallback`.
 *
 * * Shimmed custom properties may only be defined either for an entire
 * shadowRoot (for example, in a `:host` rule) or via a rule that directly
 * matches an element with a shadowRoot. In other words, instead of flowing from
 * parent to child as do native css custom properties, shimmed custom properties
 * flow only from shadowRoots to nested shadowRoots.
 *
 * * When using `@apply` mixing css shorthand property names with
 * non-shorthand names (for example `border` and `border-width`) is not
 * supported.
 */


const render = (result, container, options) => {
  if (!options || typeof options !== 'object' || !options.scopeName) {
    throw new Error('The `scopeName` option is required.');
  }

  const scopeName = options.scopeName;

  const hasRendered = _render.parts.has(container);

  const needsScoping = compatibleShadyCSSVersion && container.nodeType === 11
  /* Node.DOCUMENT_FRAGMENT_NODE */
  && !!container.host; // Handle first render to a scope specially...

  const firstScopeRender = needsScoping && !shadyRenderSet.has(scopeName); // On first scope render, render into a fragment; this cannot be a single
  // fragment that is reused since nested renders can occur synchronously.

  const renderContainer = firstScopeRender ? document.createDocumentFragment() : container;
  (0, _render.render)(result, renderContainer, Object.assign({
    templateFactory: shadyTemplateFactory(scopeName)
  }, options)); // When performing first scope render,
  // (1) We've rendered into a fragment so that there's a chance to
  // `prepareTemplateStyles` before sub-elements hit the DOM
  // (which might cause them to render based on a common pattern of
  // rendering in a custom element's `connectedCallback`);
  // (2) Scope the template with ShadyCSS one time only for this scope.
  // (3) Render the fragment into the container and make sure the
  // container knows its `part` is the one we just rendered. This ensures
  // DOM will be re-used on subsequent renders.

  if (firstScopeRender) {
    const part = _render.parts.get(renderContainer);

    _render.parts.delete(renderContainer); // ShadyCSS might have style sheets (e.g. from `prepareAdoptedCssText`)
    // that should apply to `renderContainer` even if the rendered value is
    // not a TemplateInstance. However, it will only insert scoped styles
    // into the document if `prepareTemplateStyles` has already been called
    // for the given scope name.


    const template = part.value instanceof _templateInstance.TemplateInstance ? part.value.template : undefined;
    prepareTemplateStyles(scopeName, renderContainer, template);
    (0, _dom.removeNodes)(container, container.firstChild);
    container.appendChild(renderContainer);

    _render.parts.set(container, part);
  } // After elements have hit the DOM, update styling if this is the
  // initial render to this container.
  // This is needed whenever dynamic changes are made so it would be
  // safest to do every render; however, this would regress performance
  // so we leave it up to the user to call `ShadyCSS.styleElement`
  // for dynamic changes.


  if (!hasRendered && needsScoping) {
    window.ShadyCSS.styleElement(container.host);
  }
};

exports.render = render;
},{"./dom.js":"../../node_modules/lit-html/lib/dom.js","./modify-template.js":"../../node_modules/lit-html/lib/modify-template.js","./render.js":"../../node_modules/lit-html/lib/render.js","./template-factory.js":"../../node_modules/lit-html/lib/template-factory.js","./template-instance.js":"../../node_modules/lit-html/lib/template-instance.js","./template.js":"../../node_modules/lit-html/lib/template.js","../lit-html.js":"../../node_modules/lit-html/lit-html.js"}],"../../node_modules/lit-element/lib/updating-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UpdatingElement = exports.notEqual = exports.defaultConverter = void 0;

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
var _a;
/**
 * When using Closure Compiler, JSCompiler_renameProperty(property, object) is
 * replaced at compile time by the munged name for object[property]. We cannot
 * alias this function, so we have to use a small shim that has the same
 * behavior when not compiling.
 */


window.JSCompiler_renameProperty = (prop, _obj) => prop;

const defaultConverter = {
  toAttribute(value, type) {
    switch (type) {
      case Boolean:
        return value ? '' : null;

      case Object:
      case Array:
        // if the value is `null` or `undefined` pass this through
        // to allow removing/no change behavior.
        return value == null ? value : JSON.stringify(value);
    }

    return value;
  },

  fromAttribute(value, type) {
    switch (type) {
      case Boolean:
        return value !== null;

      case Number:
        return value === null ? null : Number(value);

      case Object:
      case Array:
        return JSON.parse(value);
    }

    return value;
  }

};
/**
 * Change function that returns true if `value` is different from `oldValue`.
 * This method is used as the default for a property's `hasChanged` function.
 */

exports.defaultConverter = defaultConverter;

const notEqual = (value, old) => {
  // This ensures (old==NaN, value==NaN) always returns false
  return old !== value && (old === old || value === value);
};

exports.notEqual = notEqual;
const defaultPropertyDeclaration = {
  attribute: true,
  type: String,
  converter: defaultConverter,
  reflect: false,
  hasChanged: notEqual
};
const STATE_HAS_UPDATED = 1;
const STATE_UPDATE_REQUESTED = 1 << 2;
const STATE_IS_REFLECTING_TO_ATTRIBUTE = 1 << 3;
const STATE_IS_REFLECTING_TO_PROPERTY = 1 << 4;
/**
 * The Closure JS Compiler doesn't currently have good support for static
 * property semantics where "this" is dynamic (e.g.
 * https://github.com/google/closure-compiler/issues/3177 and others) so we use
 * this hack to bypass any rewriting by the compiler.
 */

const finalized = 'finalized';
/**
 * Base element class which manages element properties and attributes. When
 * properties change, the `update` method is asynchronously called. This method
 * should be supplied by subclassers to render updates as desired.
 */

class UpdatingElement extends HTMLElement {
  constructor() {
    super();
    this._updateState = 0;
    this._instanceProperties = undefined; // Initialize to an unresolved Promise so we can make sure the element has
    // connected before first update.

    this._updatePromise = new Promise(res => this._enableUpdatingResolver = res);
    /**
     * Map with keys for any properties that have changed since the last
     * update cycle with previous values.
     */

    this._changedProperties = new Map();
    /**
     * Map with keys of properties that should be reflected when updated.
     */

    this._reflectingProperties = undefined;
    this.initialize();
  }
  /**
   * Returns a list of attributes corresponding to the registered properties.
   * @nocollapse
   */


  static get observedAttributes() {
    // note: piggy backing on this to ensure we're finalized.
    this.finalize();
    const attributes = []; // Use forEach so this works even if for/of loops are compiled to for loops
    // expecting arrays

    this._classProperties.forEach((v, p) => {
      const attr = this._attributeNameForProperty(p, v);

      if (attr !== undefined) {
        this._attributeToPropertyMap.set(attr, p);

        attributes.push(attr);
      }
    });

    return attributes;
  }
  /**
   * Ensures the private `_classProperties` property metadata is created.
   * In addition to `finalize` this is also called in `createProperty` to
   * ensure the `@property` decorator can add property metadata.
   */

  /** @nocollapse */


  static _ensureClassProperties() {
    // ensure private storage for property declarations.
    if (!this.hasOwnProperty(JSCompiler_renameProperty('_classProperties', this))) {
      this._classProperties = new Map(); // NOTE: Workaround IE11 not supporting Map constructor argument.

      const superProperties = Object.getPrototypeOf(this)._classProperties;

      if (superProperties !== undefined) {
        superProperties.forEach((v, k) => this._classProperties.set(k, v));
      }
    }
  }
  /**
   * Creates a property accessor on the element prototype if one does not exist
   * and stores a PropertyDeclaration for the property with the given options.
   * The property setter calls the property's `hasChanged` property option
   * or uses a strict identity check to determine whether or not to request
   * an update.
   *
   * This method may be overridden to customize properties; however,
   * when doing so, it's important to call `super.createProperty` to ensure
   * the property is setup correctly. This method calls
   * `getPropertyDescriptor` internally to get a descriptor to install.
   * To customize what properties do when they are get or set, override
   * `getPropertyDescriptor`. To customize the options for a property,
   * implement `createProperty` like this:
   *
   * static createProperty(name, options) {
   *   options = Object.assign(options, {myOption: true});
   *   super.createProperty(name, options);
   * }
   *
   * @nocollapse
   */


  static createProperty(name, options = defaultPropertyDeclaration) {
    // Note, since this can be called by the `@property` decorator which
    // is called before `finalize`, we ensure storage exists for property
    // metadata.
    this._ensureClassProperties();

    this._classProperties.set(name, options); // Do not generate an accessor if the prototype already has one, since
    // it would be lost otherwise and that would never be the user's intention;
    // Instead, we expect users to call `requestUpdate` themselves from
    // user-defined accessors. Note that if the super has an accessor we will
    // still overwrite it


    if (options.noAccessor || this.prototype.hasOwnProperty(name)) {
      return;
    }

    const key = typeof name === 'symbol' ? Symbol() : `__${name}`;
    const descriptor = this.getPropertyDescriptor(name, key, options);

    if (descriptor !== undefined) {
      Object.defineProperty(this.prototype, name, descriptor);
    }
  }
  /**
   * Returns a property descriptor to be defined on the given named property.
   * If no descriptor is returned, the property will not become an accessor.
   * For example,
   *
   *   class MyElement extends LitElement {
   *     static getPropertyDescriptor(name, key, options) {
   *       const defaultDescriptor =
   *           super.getPropertyDescriptor(name, key, options);
   *       const setter = defaultDescriptor.set;
   *       return {
   *         get: defaultDescriptor.get,
   *         set(value) {
   *           setter.call(this, value);
   *           // custom action.
   *         },
   *         configurable: true,
   *         enumerable: true
   *       }
   *     }
   *   }
   *
   * @nocollapse
   */


  static getPropertyDescriptor(name, key, _options) {
    return {
      // tslint:disable-next-line:no-any no symbol in index
      get() {
        return this[key];
      },

      set(value) {
        const oldValue = this[name];
        this[key] = value;

        this._requestUpdate(name, oldValue);
      },

      configurable: true,
      enumerable: true
    };
  }
  /**
   * Returns the property options associated with the given property.
   * These options are defined with a PropertyDeclaration via the `properties`
   * object or the `@property` decorator and are registered in
   * `createProperty(...)`.
   *
   * Note, this method should be considered "final" and not overridden. To
   * customize the options for a given property, override `createProperty`.
   *
   * @nocollapse
   * @final
   */


  static getPropertyOptions(name) {
    return this._classProperties && this._classProperties.get(name) || defaultPropertyDeclaration;
  }
  /**
   * Creates property accessors for registered properties and ensures
   * any superclasses are also finalized.
   * @nocollapse
   */


  static finalize() {
    // finalize any superclasses
    const superCtor = Object.getPrototypeOf(this);

    if (!superCtor.hasOwnProperty(finalized)) {
      superCtor.finalize();
    }

    this[finalized] = true;

    this._ensureClassProperties(); // initialize Map populated in observedAttributes


    this._attributeToPropertyMap = new Map(); // make any properties
    // Note, only process "own" properties since this element will inherit
    // any properties defined on the superClass, and finalization ensures
    // the entire prototype chain is finalized.

    if (this.hasOwnProperty(JSCompiler_renameProperty('properties', this))) {
      const props = this.properties; // support symbols in properties (IE11 does not support this)

      const propKeys = [...Object.getOwnPropertyNames(props), ...(typeof Object.getOwnPropertySymbols === 'function' ? Object.getOwnPropertySymbols(props) : [])]; // This for/of is ok because propKeys is an array

      for (const p of propKeys) {
        // note, use of `any` is due to TypeSript lack of support for symbol in
        // index types
        // tslint:disable-next-line:no-any no symbol in index
        this.createProperty(p, props[p]);
      }
    }
  }
  /**
   * Returns the property name for the given attribute `name`.
   * @nocollapse
   */


  static _attributeNameForProperty(name, options) {
    const attribute = options.attribute;
    return attribute === false ? undefined : typeof attribute === 'string' ? attribute : typeof name === 'string' ? name.toLowerCase() : undefined;
  }
  /**
   * Returns true if a property should request an update.
   * Called when a property value is set and uses the `hasChanged`
   * option for the property if present or a strict identity check.
   * @nocollapse
   */


  static _valueHasChanged(value, old, hasChanged = notEqual) {
    return hasChanged(value, old);
  }
  /**
   * Returns the property value for the given attribute value.
   * Called via the `attributeChangedCallback` and uses the property's
   * `converter` or `converter.fromAttribute` property option.
   * @nocollapse
   */


  static _propertyValueFromAttribute(value, options) {
    const type = options.type;
    const converter = options.converter || defaultConverter;
    const fromAttribute = typeof converter === 'function' ? converter : converter.fromAttribute;
    return fromAttribute ? fromAttribute(value, type) : value;
  }
  /**
   * Returns the attribute value for the given property value. If this
   * returns undefined, the property will *not* be reflected to an attribute.
   * If this returns null, the attribute will be removed, otherwise the
   * attribute will be set to the value.
   * This uses the property's `reflect` and `type.toAttribute` property options.
   * @nocollapse
   */


  static _propertyValueToAttribute(value, options) {
    if (options.reflect === undefined) {
      return;
    }

    const type = options.type;
    const converter = options.converter;
    const toAttribute = converter && converter.toAttribute || defaultConverter.toAttribute;
    return toAttribute(value, type);
  }
  /**
   * Performs element initialization. By default captures any pre-set values for
   * registered properties.
   */


  initialize() {
    this._saveInstanceProperties(); // ensures first update will be caught by an early access of
    // `updateComplete`


    this._requestUpdate();
  }
  /**
   * Fixes any properties set on the instance before upgrade time.
   * Otherwise these would shadow the accessor and break these properties.
   * The properties are stored in a Map which is played back after the
   * constructor runs. Note, on very old versions of Safari (<=9) or Chrome
   * (<=41), properties created for native platform properties like (`id` or
   * `name`) may not have default values set in the element constructor. On
   * these browsers native properties appear on instances and therefore their
   * default value will overwrite any element default (e.g. if the element sets
   * this.id = 'id' in the constructor, the 'id' will become '' since this is
   * the native platform default).
   */


  _saveInstanceProperties() {
    // Use forEach so this works even if for/of loops are compiled to for loops
    // expecting arrays
    this.constructor._classProperties.forEach((_v, p) => {
      if (this.hasOwnProperty(p)) {
        const value = this[p];
        delete this[p];

        if (!this._instanceProperties) {
          this._instanceProperties = new Map();
        }

        this._instanceProperties.set(p, value);
      }
    });
  }
  /**
   * Applies previously saved instance properties.
   */


  _applyInstanceProperties() {
    // Use forEach so this works even if for/of loops are compiled to for loops
    // expecting arrays
    // tslint:disable-next-line:no-any
    this._instanceProperties.forEach((v, p) => this[p] = v);

    this._instanceProperties = undefined;
  }

  connectedCallback() {
    // Ensure first connection completes an update. Updates cannot complete
    // before connection.
    this.enableUpdating();
  }

  enableUpdating() {
    if (this._enableUpdatingResolver !== undefined) {
      this._enableUpdatingResolver();

      this._enableUpdatingResolver = undefined;
    }
  }
  /**
   * Allows for `super.disconnectedCallback()` in extensions while
   * reserving the possibility of making non-breaking feature additions
   * when disconnecting at some point in the future.
   */


  disconnectedCallback() {}
  /**
   * Synchronizes property values when attributes change.
   */


  attributeChangedCallback(name, old, value) {
    if (old !== value) {
      this._attributeToProperty(name, value);
    }
  }

  _propertyToAttribute(name, value, options = defaultPropertyDeclaration) {
    const ctor = this.constructor;

    const attr = ctor._attributeNameForProperty(name, options);

    if (attr !== undefined) {
      const attrValue = ctor._propertyValueToAttribute(value, options); // an undefined value does not change the attribute.


      if (attrValue === undefined) {
        return;
      } // Track if the property is being reflected to avoid
      // setting the property again via `attributeChangedCallback`. Note:
      // 1. this takes advantage of the fact that the callback is synchronous.
      // 2. will behave incorrectly if multiple attributes are in the reaction
      // stack at time of calling. However, since we process attributes
      // in `update` this should not be possible (or an extreme corner case
      // that we'd like to discover).
      // mark state reflecting


      this._updateState = this._updateState | STATE_IS_REFLECTING_TO_ATTRIBUTE;

      if (attrValue == null) {
        this.removeAttribute(attr);
      } else {
        this.setAttribute(attr, attrValue);
      } // mark state not reflecting


      this._updateState = this._updateState & ~STATE_IS_REFLECTING_TO_ATTRIBUTE;
    }
  }

  _attributeToProperty(name, value) {
    // Use tracking info to avoid deserializing attribute value if it was
    // just set from a property setter.
    if (this._updateState & STATE_IS_REFLECTING_TO_ATTRIBUTE) {
      return;
    }

    const ctor = this.constructor; // Note, hint this as an `AttributeMap` so closure clearly understands
    // the type; it has issues with tracking types through statics
    // tslint:disable-next-line:no-unnecessary-type-assertion

    const propName = ctor._attributeToPropertyMap.get(name);

    if (propName !== undefined) {
      const options = ctor.getPropertyOptions(propName); // mark state reflecting

      this._updateState = this._updateState | STATE_IS_REFLECTING_TO_PROPERTY;
      this[propName] = // tslint:disable-next-line:no-any
      ctor._propertyValueFromAttribute(value, options); // mark state not reflecting

      this._updateState = this._updateState & ~STATE_IS_REFLECTING_TO_PROPERTY;
    }
  }
  /**
   * This private version of `requestUpdate` does not access or return the
   * `updateComplete` promise. This promise can be overridden and is therefore
   * not free to access.
   */


  _requestUpdate(name, oldValue) {
    let shouldRequestUpdate = true; // If we have a property key, perform property update steps.

    if (name !== undefined) {
      const ctor = this.constructor;
      const options = ctor.getPropertyOptions(name);

      if (ctor._valueHasChanged(this[name], oldValue, options.hasChanged)) {
        if (!this._changedProperties.has(name)) {
          this._changedProperties.set(name, oldValue);
        } // Add to reflecting properties set.
        // Note, it's important that every change has a chance to add the
        // property to `_reflectingProperties`. This ensures setting
        // attribute + property reflects correctly.


        if (options.reflect === true && !(this._updateState & STATE_IS_REFLECTING_TO_PROPERTY)) {
          if (this._reflectingProperties === undefined) {
            this._reflectingProperties = new Map();
          }

          this._reflectingProperties.set(name, options);
        }
      } else {
        // Abort the request if the property should not be considered changed.
        shouldRequestUpdate = false;
      }
    }

    if (!this._hasRequestedUpdate && shouldRequestUpdate) {
      this._updatePromise = this._enqueueUpdate();
    }
  }
  /**
   * Requests an update which is processed asynchronously. This should
   * be called when an element should update based on some state not triggered
   * by setting a property. In this case, pass no arguments. It should also be
   * called when manually implementing a property setter. In this case, pass the
   * property `name` and `oldValue` to ensure that any configured property
   * options are honored. Returns the `updateComplete` Promise which is resolved
   * when the update completes.
   *
   * @param name {PropertyKey} (optional) name of requesting property
   * @param oldValue {any} (optional) old value of requesting property
   * @returns {Promise} A Promise that is resolved when the update completes.
   */


  requestUpdate(name, oldValue) {
    this._requestUpdate(name, oldValue);

    return this.updateComplete;
  }
  /**
   * Sets up the element to asynchronously update.
   */


  async _enqueueUpdate() {
    this._updateState = this._updateState | STATE_UPDATE_REQUESTED;

    try {
      // Ensure any previous update has resolved before updating.
      // This `await` also ensures that property changes are batched.
      await this._updatePromise;
    } catch (e) {// Ignore any previous errors. We only care that the previous cycle is
      // done. Any error should have been handled in the previous update.
    }

    const result = this.performUpdate(); // If `performUpdate` returns a Promise, we await it. This is done to
    // enable coordinating updates with a scheduler. Note, the result is
    // checked to avoid delaying an additional microtask unless we need to.

    if (result != null) {
      await result;
    }

    return !this._hasRequestedUpdate;
  }

  get _hasRequestedUpdate() {
    return this._updateState & STATE_UPDATE_REQUESTED;
  }

  get hasUpdated() {
    return this._updateState & STATE_HAS_UPDATED;
  }
  /**
   * Performs an element update. Note, if an exception is thrown during the
   * update, `firstUpdated` and `updated` will not be called.
   *
   * You can override this method to change the timing of updates. If this
   * method is overridden, `super.performUpdate()` must be called.
   *
   * For instance, to schedule updates to occur just before the next frame:
   *
   * ```
   * protected async performUpdate(): Promise<unknown> {
   *   await new Promise((resolve) => requestAnimationFrame(() => resolve()));
   *   super.performUpdate();
   * }
   * ```
   */


  performUpdate() {
    // Mixin instance properties once, if they exist.
    if (this._instanceProperties) {
      this._applyInstanceProperties();
    }

    let shouldUpdate = false;
    const changedProperties = this._changedProperties;

    try {
      shouldUpdate = this.shouldUpdate(changedProperties);

      if (shouldUpdate) {
        this.update(changedProperties);
      } else {
        this._markUpdated();
      }
    } catch (e) {
      // Prevent `firstUpdated` and `updated` from running when there's an
      // update exception.
      shouldUpdate = false; // Ensure element can accept additional updates after an exception.

      this._markUpdated();

      throw e;
    }

    if (shouldUpdate) {
      if (!(this._updateState & STATE_HAS_UPDATED)) {
        this._updateState = this._updateState | STATE_HAS_UPDATED;
        this.firstUpdated(changedProperties);
      }

      this.updated(changedProperties);
    }
  }

  _markUpdated() {
    this._changedProperties = new Map();
    this._updateState = this._updateState & ~STATE_UPDATE_REQUESTED;
  }
  /**
   * Returns a Promise that resolves when the element has completed updating.
   * The Promise value is a boolean that is `true` if the element completed the
   * update without triggering another update. The Promise result is `false` if
   * a property was set inside `updated()`. If the Promise is rejected, an
   * exception was thrown during the update.
   *
   * To await additional asynchronous work, override the `_getUpdateComplete`
   * method. For example, it is sometimes useful to await a rendered element
   * before fulfilling this Promise. To do this, first await
   * `super._getUpdateComplete()`, then any subsequent state.
   *
   * @returns {Promise} The Promise returns a boolean that indicates if the
   * update resolved without triggering another update.
   */


  get updateComplete() {
    return this._getUpdateComplete();
  }
  /**
   * Override point for the `updateComplete` promise.
   *
   * It is not safe to override the `updateComplete` getter directly due to a
   * limitation in TypeScript which means it is not possible to call a
   * superclass getter (e.g. `super.updateComplete.then(...)`) when the target
   * language is ES5 (https://github.com/microsoft/TypeScript/issues/338).
   * This method should be overridden instead. For example:
   *
   *   class MyElement extends LitElement {
   *     async _getUpdateComplete() {
   *       await super._getUpdateComplete();
   *       await this._myChild.updateComplete;
   *     }
   *   }
   */


  _getUpdateComplete() {
    return this._updatePromise;
  }
  /**
   * Controls whether or not `update` should be called when the element requests
   * an update. By default, this method always returns `true`, but this can be
   * customized to control when to update.
   *
   * @param _changedProperties Map of changed properties with old values
   */


  shouldUpdate(_changedProperties) {
    return true;
  }
  /**
   * Updates the element. This method reflects property values to attributes.
   * It can be overridden to render and keep updated element DOM.
   * Setting properties inside this method will *not* trigger
   * another update.
   *
   * @param _changedProperties Map of changed properties with old values
   */


  update(_changedProperties) {
    if (this._reflectingProperties !== undefined && this._reflectingProperties.size > 0) {
      // Use forEach so this works even if for/of loops are compiled to for
      // loops expecting arrays
      this._reflectingProperties.forEach((v, k) => this._propertyToAttribute(k, this[k], v));

      this._reflectingProperties = undefined;
    }

    this._markUpdated();
  }
  /**
   * Invoked whenever the element is updated. Implement to perform
   * post-updating tasks via DOM APIs, for example, focusing an element.
   *
   * Setting properties inside this method will trigger the element to update
   * again after this update cycle completes.
   *
   * @param _changedProperties Map of changed properties with old values
   */


  updated(_changedProperties) {}
  /**
   * Invoked when the element is first updated. Implement to perform one time
   * work on the element after update.
   *
   * Setting properties inside this method will trigger the element to update
   * again after this update cycle completes.
   *
   * @param _changedProperties Map of changed properties with old values
   */


  firstUpdated(_changedProperties) {}

}

exports.UpdatingElement = UpdatingElement;
_a = finalized;
/**
 * Marks class as having finished creating properties.
 */

UpdatingElement[_a] = true;
},{}],"../../node_modules/lit-element/lib/decorators.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.property = property;
exports.internalProperty = internalProperty;
exports.query = query;
exports.queryAsync = queryAsync;
exports.queryAll = queryAll;
exports.eventOptions = eventOptions;
exports.queryAssignedNodes = queryAssignedNodes;
exports.customElement = void 0;

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
const legacyCustomElement = (tagName, clazz) => {
  window.customElements.define(tagName, clazz); // Cast as any because TS doesn't recognize the return type as being a
  // subtype of the decorated class when clazz is typed as
  // `Constructor<HTMLElement>` for some reason.
  // `Constructor<HTMLElement>` is helpful to make sure the decorator is
  // applied to elements however.
  // tslint:disable-next-line:no-any

  return clazz;
};

const standardCustomElement = (tagName, descriptor) => {
  const {
    kind,
    elements
  } = descriptor;
  return {
    kind,
    elements,

    // This callback is called once the class is otherwise fully defined
    finisher(clazz) {
      window.customElements.define(tagName, clazz);
    }

  };
};
/**
 * Class decorator factory that defines the decorated class as a custom element.
 *
 * ```
 * @customElement('my-element')
 * class MyElement {
 *   render() {
 *     return html``;
 *   }
 * }
 * ```
 *
 * @param tagName The name of the custom element to define.
 */


const customElement = tagName => classOrDescriptor => typeof classOrDescriptor === 'function' ? legacyCustomElement(tagName, classOrDescriptor) : standardCustomElement(tagName, classOrDescriptor);

exports.customElement = customElement;

const standardProperty = (options, element) => {
  // When decorating an accessor, pass it through and add property metadata.
  // Note, the `hasOwnProperty` check in `createProperty` ensures we don't
  // stomp over the user's accessor.
  if (element.kind === 'method' && element.descriptor && !('value' in element.descriptor)) {
    return Object.assign(Object.assign({}, element), {
      finisher(clazz) {
        clazz.createProperty(element.key, options);
      }

    });
  } else {
    // createProperty() takes care of defining the property, but we still
    // must return some kind of descriptor, so return a descriptor for an
    // unused prototype field. The finisher calls createProperty().
    return {
      kind: 'field',
      key: Symbol(),
      placement: 'own',
      descriptor: {},

      // When @babel/plugin-proposal-decorators implements initializers,
      // do this instead of the initializer below. See:
      // https://github.com/babel/babel/issues/9260 extras: [
      //   {
      //     kind: 'initializer',
      //     placement: 'own',
      //     initializer: descriptor.initializer,
      //   }
      // ],
      initializer() {
        if (typeof element.initializer === 'function') {
          this[element.key] = element.initializer.call(this);
        }
      },

      finisher(clazz) {
        clazz.createProperty(element.key, options);
      }

    };
  }
};

const legacyProperty = (options, proto, name) => {
  proto.constructor.createProperty(name, options);
};
/**
 * A property decorator which creates a LitElement property which reflects a
 * corresponding attribute value. A `PropertyDeclaration` may optionally be
 * supplied to configure property features.
 *
 * This decorator should only be used for public fields. Private or protected
 * fields should use the internalProperty decorator.
 *
 * @example
 *
 *     class MyElement {
 *       @property({ type: Boolean })
 *       clicked = false;
 *     }
 *
 * @ExportDecoratedItems
 */


function property(options) {
  // tslint:disable-next-line:no-any decorator
  return (protoOrDescriptor, name) => name !== undefined ? legacyProperty(options, protoOrDescriptor, name) : standardProperty(options, protoOrDescriptor);
}
/**
 * Declares a private or protected property that still triggers updates to the
 * element when it changes.
 *
 * Properties declared this way must not be used from HTML or HTML templating
 * systems, they're solely for properties internal to the element. These
 * properties may be renamed by optimization tools like closure compiler.
 */


function internalProperty(options) {
  return property({
    attribute: false,
    hasChanged: options === null || options === void 0 ? void 0 : options.hasChanged
  });
}
/**
 * A property decorator that converts a class property into a getter that
 * executes a querySelector on the element's renderRoot.
 *
 * @param selector A DOMString containing one or more selectors to match.
 *
 * See: https://developer.mozilla.org/en-US/docs/Web/API/Document/querySelector
 *
 * @example
 *
 *     class MyElement {
 *       @query('#first')
 *       first;
 *
 *       render() {
 *         return html`
 *           <div id="first"></div>
 *           <div id="second"></div>
 *         `;
 *       }
 *     }
 *
 */


function query(selector) {
  return (protoOrDescriptor, // tslint:disable-next-line:no-any decorator
  name) => {
    const descriptor = {
      get() {
        return this.renderRoot.querySelector(selector);
      },

      enumerable: true,
      configurable: true
    };
    return name !== undefined ? legacyQuery(descriptor, protoOrDescriptor, name) : standardQuery(descriptor, protoOrDescriptor);
  };
} // Note, in the future, we may extend this decorator to support the use case
// where the queried element may need to do work to become ready to interact
// with (e.g. load some implementation code). If so, we might elect to
// add a second argument defining a function that can be run to make the
// queried element loaded/updated/ready.

/**
 * A property decorator that converts a class property into a getter that
 * returns a promise that resolves to the result of a querySelector on the
 * element's renderRoot done after the element's `updateComplete` promise
 * resolves. When the queried property may change with element state, this
 * decorator can be used instead of requiring users to await the
 * `updateComplete` before accessing the property.
 *
 * @param selector A DOMString containing one or more selectors to match.
 *
 * See: https://developer.mozilla.org/en-US/docs/Web/API/Document/querySelector
 *
 * @example
 *
 *     class MyElement {
 *       @queryAsync('#first')
 *       first;
 *
 *       render() {
 *         return html`
 *           <div id="first"></div>
 *           <div id="second"></div>
 *         `;
 *       }
 *     }
 *
 *     // external usage
 *     async doSomethingWithFirst() {
 *      (await aMyElement.first).doSomething();
 *     }
 */


function queryAsync(selector) {
  return (protoOrDescriptor, // tslint:disable-next-line:no-any decorator
  name) => {
    const descriptor = {
      async get() {
        await this.updateComplete;
        return this.renderRoot.querySelector(selector);
      },

      enumerable: true,
      configurable: true
    };
    return name !== undefined ? legacyQuery(descriptor, protoOrDescriptor, name) : standardQuery(descriptor, protoOrDescriptor);
  };
}
/**
 * A property decorator that converts a class property into a getter
 * that executes a querySelectorAll on the element's renderRoot.
 *
 * @param selector A DOMString containing one or more selectors to match.
 *
 * See:
 * https://developer.mozilla.org/en-US/docs/Web/API/Document/querySelectorAll
 *
 * @example
 *
 *     class MyElement {
 *       @queryAll('div')
 *       divs;
 *
 *       render() {
 *         return html`
 *           <div id="first"></div>
 *           <div id="second"></div>
 *         `;
 *       }
 *     }
 */


function queryAll(selector) {
  return (protoOrDescriptor, // tslint:disable-next-line:no-any decorator
  name) => {
    const descriptor = {
      get() {
        return this.renderRoot.querySelectorAll(selector);
      },

      enumerable: true,
      configurable: true
    };
    return name !== undefined ? legacyQuery(descriptor, protoOrDescriptor, name) : standardQuery(descriptor, protoOrDescriptor);
  };
}

const legacyQuery = (descriptor, proto, name) => {
  Object.defineProperty(proto, name, descriptor);
};

const standardQuery = (descriptor, element) => ({
  kind: 'method',
  placement: 'prototype',
  key: element.key,
  descriptor
});

const standardEventOptions = (options, element) => {
  return Object.assign(Object.assign({}, element), {
    finisher(clazz) {
      Object.assign(clazz.prototype[element.key], options);
    }

  });
};

const legacyEventOptions = // tslint:disable-next-line:no-any legacy decorator
(options, proto, name) => {
  Object.assign(proto[name], options);
};
/**
 * Adds event listener options to a method used as an event listener in a
 * lit-html template.
 *
 * @param options An object that specifies event listener options as accepted by
 * `EventTarget#addEventListener` and `EventTarget#removeEventListener`.
 *
 * Current browsers support the `capture`, `passive`, and `once` options. See:
 * https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener#Parameters
 *
 * @example
 *
 *     class MyElement {
 *       clicked = false;
 *
 *       render() {
 *         return html`
 *           <div @click=${this._onClick}`>
 *             <button></button>
 *           </div>
 *         `;
 *       }
 *
 *       @eventOptions({capture: true})
 *       _onClick(e) {
 *         this.clicked = true;
 *       }
 *     }
 */


function eventOptions(options) {
  // Return value typed as any to prevent TypeScript from complaining that
  // standard decorator function signature does not match TypeScript decorator
  // signature
  // TODO(kschaaf): unclear why it was only failing on this decorator and not
  // the others
  return (protoOrDescriptor, name) => name !== undefined ? legacyEventOptions(options, protoOrDescriptor, name) : standardEventOptions(options, protoOrDescriptor);
}
/**
 * A property decorator that converts a class property into a getter that
 * returns the `assignedNodes` of the given named `slot`. Note, the type of
 * this property should be annotated as `NodeListOf<HTMLElement>`.
 *
 */


function queryAssignedNodes(slotName = '', flatten = false) {
  return (protoOrDescriptor, // tslint:disable-next-line:no-any decorator
  name) => {
    const descriptor = {
      get() {
        const selector = `slot${slotName ? `[name=${slotName}]` : ''}`;
        const slot = this.renderRoot.querySelector(selector);
        return slot && slot.assignedNodes({
          flatten
        });
      },

      enumerable: true,
      configurable: true
    };
    return name !== undefined ? legacyQuery(descriptor, protoOrDescriptor, name) : standardQuery(descriptor, protoOrDescriptor);
  };
}
},{}],"../../node_modules/lit-element/lib/css-tag.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.css = exports.unsafeCSS = exports.CSSResult = exports.supportsAdoptingStyleSheets = void 0;

/**
@license
Copyright (c) 2019 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at
http://polymer.github.io/LICENSE.txt The complete set of authors may be found at
http://polymer.github.io/AUTHORS.txt The complete set of contributors may be
found at http://polymer.github.io/CONTRIBUTORS.txt Code distributed by Google as
part of the polymer project is also subject to an additional IP rights grant
found at http://polymer.github.io/PATENTS.txt
*/
const supportsAdoptingStyleSheets = 'adoptedStyleSheets' in Document.prototype && 'replace' in CSSStyleSheet.prototype;
exports.supportsAdoptingStyleSheets = supportsAdoptingStyleSheets;
const constructionToken = Symbol();

class CSSResult {
  constructor(cssText, safeToken) {
    if (safeToken !== constructionToken) {
      throw new Error('CSSResult is not constructable. Use `unsafeCSS` or `css` instead.');
    }

    this.cssText = cssText;
  } // Note, this is a getter so that it's lazy. In practice, this means
  // stylesheets are not created until the first element instance is made.


  get styleSheet() {
    if (this._styleSheet === undefined) {
      // Note, if `adoptedStyleSheets` is supported then we assume CSSStyleSheet
      // is constructable.
      if (supportsAdoptingStyleSheets) {
        this._styleSheet = new CSSStyleSheet();

        this._styleSheet.replaceSync(this.cssText);
      } else {
        this._styleSheet = null;
      }
    }

    return this._styleSheet;
  }

  toString() {
    return this.cssText;
  }

}
/**
 * Wrap a value for interpolation in a css tagged template literal.
 *
 * This is unsafe because untrusted CSS text can be used to phone home
 * or exfiltrate data to an attacker controlled site. Take care to only use
 * this with trusted input.
 */


exports.CSSResult = CSSResult;

const unsafeCSS = value => {
  return new CSSResult(String(value), constructionToken);
};

exports.unsafeCSS = unsafeCSS;

const textFromCSSResult = value => {
  if (value instanceof CSSResult) {
    return value.cssText;
  } else if (typeof value === 'number') {
    return value;
  } else {
    throw new Error(`Value passed to 'css' function must be a 'css' function result: ${value}. Use 'unsafeCSS' to pass non-literal values, but
            take care to ensure page security.`);
  }
};
/**
 * Template tag which which can be used with LitElement's `style` property to
 * set element styles. For security reasons, only literal string values may be
 * used. To incorporate non-literal values `unsafeCSS` may be used inside a
 * template string part.
 */


const css = (strings, ...values) => {
  const cssText = values.reduce((acc, v, idx) => acc + textFromCSSResult(v) + strings[idx + 1], strings[0]);
  return new CSSResult(cssText, constructionToken);
};

exports.css = css;
},{}],"../../node_modules/lit-element/lit-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
var _exportNames = {
  LitElement: true,
  html: true,
  svg: true,
  TemplateResult: true,
  SVGTemplateResult: true
};
Object.defineProperty(exports, "html", {
  enumerable: true,
  get: function () {
    return _litHtml.html;
  }
});
Object.defineProperty(exports, "svg", {
  enumerable: true,
  get: function () {
    return _litHtml.svg;
  }
});
Object.defineProperty(exports, "TemplateResult", {
  enumerable: true,
  get: function () {
    return _litHtml.TemplateResult;
  }
});
Object.defineProperty(exports, "SVGTemplateResult", {
  enumerable: true,
  get: function () {
    return _litHtml.SVGTemplateResult;
  }
});
exports.LitElement = void 0;

var _shadyRender = require("lit-html/lib/shady-render.js");

var _updatingElement = require("./lib/updating-element.js");

Object.keys(_updatingElement).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (Object.prototype.hasOwnProperty.call(_exportNames, key)) return;
  Object.defineProperty(exports, key, {
    enumerable: true,
    get: function () {
      return _updatingElement[key];
    }
  });
});

var _decorators = require("./lib/decorators.js");

Object.keys(_decorators).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (Object.prototype.hasOwnProperty.call(_exportNames, key)) return;
  Object.defineProperty(exports, key, {
    enumerable: true,
    get: function () {
      return _decorators[key];
    }
  });
});

var _litHtml = require("lit-html/lit-html.js");

var _cssTag = require("./lib/css-tag.js");

Object.keys(_cssTag).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (Object.prototype.hasOwnProperty.call(_exportNames, key)) return;
  Object.defineProperty(exports, key, {
    enumerable: true,
    get: function () {
      return _cssTag[key];
    }
  });
});

/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
// IMPORTANT: do not change the property name or the assignment expression.
// This line will be used in regexes to search for LitElement usage.
// TODO(justinfagnani): inject version number at build time
(window['litElementVersions'] || (window['litElementVersions'] = [])).push('2.3.1');
/**
 * Sentinal value used to avoid calling lit-html's render function when
 * subclasses do not implement `render`
 */

const renderNotImplemented = {};

class LitElement extends _updatingElement.UpdatingElement {
  /**
   * Return the array of styles to apply to the element.
   * Override this method to integrate into a style management system.
   *
   * @nocollapse
   */
  static getStyles() {
    return this.styles;
  }
  /** @nocollapse */


  static _getUniqueStyles() {
    // Only gather styles once per class
    if (this.hasOwnProperty(JSCompiler_renameProperty('_styles', this))) {
      return;
    } // Take care not to call `this.getStyles()` multiple times since this
    // generates new CSSResults each time.
    // TODO(sorvell): Since we do not cache CSSResults by input, any
    // shared styles will generate new stylesheet objects, which is wasteful.
    // This should be addressed when a browser ships constructable
    // stylesheets.


    const userStyles = this.getStyles();

    if (userStyles === undefined) {
      this._styles = [];
    } else if (Array.isArray(userStyles)) {
      // De-duplicate styles preserving the _last_ instance in the set.
      // This is a performance optimization to avoid duplicated styles that can
      // occur especially when composing via subclassing.
      // The last item is kept to try to preserve the cascade order with the
      // assumption that it's most important that last added styles override
      // previous styles.
      const addStyles = (styles, set) => styles.reduceRight((set, s) => // Note: On IE set.add() does not return the set
      Array.isArray(s) ? addStyles(s, set) : (set.add(s), set), set); // Array.from does not work on Set in IE, otherwise return
      // Array.from(addStyles(userStyles, new Set<CSSResult>())).reverse()


      const set = addStyles(userStyles, new Set());
      const styles = [];
      set.forEach(v => styles.unshift(v));
      this._styles = styles;
    } else {
      this._styles = [userStyles];
    }
  }
  /**
   * Performs element initialization. By default this calls `createRenderRoot`
   * to create the element `renderRoot` node and captures any pre-set values for
   * registered properties.
   */


  initialize() {
    super.initialize();

    this.constructor._getUniqueStyles();

    this.renderRoot = this.createRenderRoot(); // Note, if renderRoot is not a shadowRoot, styles would/could apply to the
    // element's getRootNode(). While this could be done, we're choosing not to
    // support this now since it would require different logic around de-duping.

    if (window.ShadowRoot && this.renderRoot instanceof window.ShadowRoot) {
      this.adoptStyles();
    }
  }
  /**
   * Returns the node into which the element should render and by default
   * creates and returns an open shadowRoot. Implement to customize where the
   * element's DOM is rendered. For example, to render into the element's
   * childNodes, return `this`.
   * @returns {Element|DocumentFragment} Returns a node into which to render.
   */


  createRenderRoot() {
    return this.attachShadow({
      mode: 'open'
    });
  }
  /**
   * Applies styling to the element shadowRoot using the `static get styles`
   * property. Styling will apply using `shadowRoot.adoptedStyleSheets` where
   * available and will fallback otherwise. When Shadow DOM is polyfilled,
   * ShadyCSS scopes styles and adds them to the document. When Shadow DOM
   * is available but `adoptedStyleSheets` is not, styles are appended to the
   * end of the `shadowRoot` to [mimic spec
   * behavior](https://wicg.github.io/construct-stylesheets/#using-constructed-stylesheets).
   */


  adoptStyles() {
    const styles = this.constructor._styles;

    if (styles.length === 0) {
      return;
    } // There are three separate cases here based on Shadow DOM support.
    // (1) shadowRoot polyfilled: use ShadyCSS
    // (2) shadowRoot.adoptedStyleSheets available: use it.
    // (3) shadowRoot.adoptedStyleSheets polyfilled: append styles after
    // rendering


    if (window.ShadyCSS !== undefined && !window.ShadyCSS.nativeShadow) {
      window.ShadyCSS.ScopingShim.prepareAdoptedCssText(styles.map(s => s.cssText), this.localName);
    } else if (_cssTag.supportsAdoptingStyleSheets) {
      this.renderRoot.adoptedStyleSheets = styles.map(s => s.styleSheet);
    } else {
      // This must be done after rendering so the actual style insertion is done
      // in `update`.
      this._needsShimAdoptedStyleSheets = true;
    }
  }

  connectedCallback() {
    super.connectedCallback(); // Note, first update/render handles styleElement so we only call this if
    // connected after first update.

    if (this.hasUpdated && window.ShadyCSS !== undefined) {
      window.ShadyCSS.styleElement(this);
    }
  }
  /**
   * Updates the element. This method reflects property values to attributes
   * and calls `render` to render DOM via lit-html. Setting properties inside
   * this method will *not* trigger another update.
   * @param _changedProperties Map of changed properties with old values
   */


  update(changedProperties) {
    // Setting properties in `render` should not trigger an update. Since
    // updates are allowed after super.update, it's important to call `render`
    // before that.
    const templateResult = this.render();
    super.update(changedProperties); // If render is not implemented by the component, don't call lit-html render

    if (templateResult !== renderNotImplemented) {
      this.constructor.render(templateResult, this.renderRoot, {
        scopeName: this.localName,
        eventContext: this
      });
    } // When native Shadow DOM is used but adoptedStyles are not supported,
    // insert styling after rendering to ensure adoptedStyles have highest
    // priority.


    if (this._needsShimAdoptedStyleSheets) {
      this._needsShimAdoptedStyleSheets = false;

      this.constructor._styles.forEach(s => {
        const style = document.createElement('style');
        style.textContent = s.cssText;
        this.renderRoot.appendChild(style);
      });
    }
  }
  /**
   * Invoked on each update to perform rendering tasks. This method may return
   * any value renderable by lit-html's NodePart - typically a TemplateResult.
   * Setting properties inside this method will *not* trigger the element to
   * update.
   */


  render() {
    return renderNotImplemented;
  }

}
/**
 * Ensure this class is marked as `finalized` as an optimization ensuring
 * it will not needlessly try to `finalize`.
 *
 * Note this property name is a string to prevent breaking Closure JS Compiler
 * optimizations. See updating-element.ts for more information.
 */


exports.LitElement = LitElement;
LitElement['finalized'] = true;
/**
 * Render method used to render the value to the element's DOM.
 * @param result The value to render.
 * @param container Node into which to render.
 * @param options Element name.
 * @nocollapse
 */

LitElement.render = _shadyRender.render;
},{"lit-html/lib/shady-render.js":"../../node_modules/lit-html/lib/shady-render.js","./lib/updating-element.js":"../../node_modules/lit-element/lib/updating-element.js","./lib/decorators.js":"../../node_modules/lit-element/lib/decorators.js","lit-html/lit-html.js":"../../node_modules/lit-html/lit-html.js","./lib/css-tag.js":"../../node_modules/lit-element/lib/css-tag.js"}],"../../node_modules/@wokwi/elements/dist/esm/7segment-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SevenSegmentElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

let SevenSegmentElement = class SevenSegmentElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.color = 'red';
    this.values = [0, 0, 0, 0, 0, 0, 0, 0];
  }

  static get styles() {
    return _litElement.css`
      polygon {
        transform: scale(0.9);
        transform-origin: 50% 50%;
        transform-box: fill-box;
      }
    `;
  }

  render() {
    const {
      color,
      values
    } = this;

    const fill = index => values[index] ? color : '#ddd';

    return _litElement.html`
      <svg
        width="12mm"
        height="18.5mm"
        version="1.1"
        viewBox="0 0 12 18.5"
        xmlns="http://www.w3.org/2000/svg"
      >
        <g transform="skewX(-8) translate(2, 0)">
          <polygon points="2 0 8 0 9 1 8 2 2 2 1 1" fill="${fill(0)}" />
          <polygon points="10 2 10 8 9 9 8 8 8 2 9 1" fill="${fill(1)}" />
          <polygon points="10 10 10 16 9 17 8 16 8 10 9 9" fill="${fill(2)}" />
          <polygon points="8 18 2 18 1 17 2 16 8 16 9 17" fill="${fill(3)}" />
          <polygon points="0 16 0 10 1 9 2 10 2 16 1 17" fill="${fill(4)}" />
          <polygon points="0 8 0 2 1 1 2 2 2 8 1 9" fill=${fill(5)} />
          <polygon points="2 8 8 8 9 9 8 10 2 10 1 9" fill=${fill(6)} />
        </g>
        <circle cx="11" cy="17" r="1.1" fill="${fill(7)}" />
      </svg>
    `;
  }

};
exports.SevenSegmentElement = SevenSegmentElement;

__decorate([(0, _litElement.property)()], SevenSegmentElement.prototype, "color", void 0);

__decorate([(0, _litElement.property)({
  type: Array
})], SevenSegmentElement.prototype, "values", void 0);

exports.SevenSegmentElement = SevenSegmentElement = __decorate([(0, _litElement.customElement)('wokwi-7segment')], SevenSegmentElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/arduino-uno-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ArduinoUnoElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

let ArduinoUnoElement = class ArduinoUnoElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.led13 = false;
    this.ledRX = false;
    this.ledTX = false;
    this.ledPower = false;
  }

  render() {
    const {
      ledPower,
      led13,
      ledRX,
      ledTX
    } = this;
    return _litElement.html`
      <svg
        width="72.58mm"
        height="53.34mm"
        version="1.1"
        viewBox="-4 0 72.58 53.34"
        style="font-size: 2px; font-family: monospace"
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <g id="led-body" fill="#eee">
            <rect x="0" y="0" height="1.2" width="2.6" fill="#c6c6c6" />
            <rect x="0.6" y="-0.1" width="1.35" height="1.4" stroke="#aaa" stroke-width="0.05" />
          </g>
        </defs>

        <filter id="ledFilter" x="-0.8" y="-0.8" height="2.2" width="2.8">
          <feGaussianBlur stdDeviation="0.5" />
        </filter>

        <pattern id="pins" width="2.54" height="2.54" patternUnits="userSpaceOnUse">
          <rect x="0" y="0" width="2.54" height="2.54" fill="#333"></rect>
          <rect x="1.079" y="0.896" width="0.762" height="0.762" style="fill: #191919"></rect>
          <path
            transform="translate(1.079, 1.658) rotate(180 0 0)"
            d="m 0 0 v 0.762 l 0.433,0.433 c 0.046,-0.046 0.074,-0.109 0.074,-0.179 v -1.27 c 0,-0.070 -0.028,-0.133 -0.074,-0.179 z"
            style="opacity: 0.25"
          ></path>
          <path
            transform="translate(1.841, 1.658) rotate(90 0 0)"
            d="m 0 0 v 0.762 l 0.433,0.433 c 0.046,-0.046 0.074,-0.109 0.074,-0.179 v -1.27 c 0,-0.070 -0.028,-0.133 -0.074,-0.179 z"
            style="opacity: 0.3; fill: #fff"
          ></path>
          <path
            transform="translate(1.841, 0.896)"
            d="m 0 0 v 0.762 l 0.433,0.433 c 0.046,-0.046 0.074,-0.109 0.074,-0.179 v -1.27 c 0,-0.070 -0.028,-0.133 -0.074,-0.179 z"
            style="opacity: 0.15; fill: #fff"
          ></path>
          <path
            transform="translate(1.079, 0.896) rotate(270 0 0)"
            d="m 0 0 v 0.762 l 0.433,0.433 c 0.046,-0.046 0.074,-0.109 0.074,-0.179 v -1.27 c 0,-0.070 -0.028,-0.133 -0.074,-0.179 z"
            style="opacity: 0.35"
          ></path>
        </pattern>

        <pattern id="pin-male" width="2.54" height="4.80" patternUnits="userSpaceOnUse">
          <rect ry="0.3" rx="0.3" width="2.12" height="4.80" fill="#565656" />
          <ellipse cx="1" cy="1.13" rx="0.5" ry="0.5" fill="#aaa"></ellipse>
          <ellipse cx="1" cy="3.67" rx="0.5" ry="0.5" fill="#aaa"></ellipse>
        </pattern>

        <pattern id="mcu-leads" width="2.54" height="0.508" patternUnits="userSpaceOnUse">
          <path
            d="M 0.254,0 C 0.114,0 0,0.114 0,0.254 v 0 c 0,0.139 0,0.253 0,0.253 h 1.523 c 0,0 0,-0.114 0,-0.253 v 0 C 1.523,0.114 1.409,0 1.269,0 Z"
            fill="#ddd"
          />
        </pattern>

        <!-- PCB -->
        <path
          d="m0.999 0a1 1 0 0 0-0.999 0.999v51.34a1 1 0 0 0 0.999 0.999h64.04a1 1 0 0 0 0.999-0.999v-1.54l2.539-2.539v-32.766l-2.539-2.539v-11.43l-1.524-1.523zm14.078 0.835h0.325l0.212 0.041h0l0.105 0.021 0.300 0.124 0.270 0.180 0.229 0.229 0.180 0.270 0.017 0.042 0.097 0.234 0.01 0.023 0.050 0.252 0.013 0.066v0.325l-0.063 0.318-0.040 0.097-0.083 0.202-0 0.001-0.180 0.270-0.229 0.229-0.270 0.180-0.300 0.124-0.106 0.020-0.212 0.042h-0.325l-0.212-0.042-0.106-0.020-0.300-0.124-0.270-0.180-0.229-0.229-0.180-0.270-0 -0.001-0.083-0.202-0.040-0.097-0.063-0.318v-0.325l0.013-0.066 0.050-0.252 0.01-0.023 0.097-0.234 0.017-0.042 0.180-0.270 0.229-0.229 0.270-0.180 0.300-0.124 0.105-0.021zm50.799 15.239h0.325l0.212 0.042 0.105 0.021 0.300 0.124 0.270 0.180 0.229 0.229 0.180 0.270 0.014 0.035 0.110 0.264 0.01 0.051 0.053 0.267v0.325l-0.03 0.152-0.033 0.166-0.037 0.089-0.079 0.191-0 0.020-0.180 0.270-0.229 0.229-0.270 0.180-0.071 0.029-0.228 0.094-0.106 0.021-0.212 0.042h-0.325l-0.212-0.042-0.106-0.021-0.228-0.094-0.071-0.029-0.270-0.180-0.229-0.229-0.180-0.270-0 -0.020-0.079-0.191-0.036-0.089-0.033-0.166-0.030-0.152v-0.325l0.053-0.267 0.010-0.051 0.109-0.264 0.014-0.035 0.180-0.270 0.229-0.229 0.270-0.180 0.300-0.124 0.105-0.021zm0 27.94h0.325l0.180 0.036 0.138 0.027 0.212 0.087 0.058 0.024 0.029 0.012 0.270 0.180 0.229 0.229 0.180 0.270 0.124 0.300 0.063 0.319v0.325l-0.063 0.318-0.124 0.300-0.180 0.270-0.229 0.229-0.270 0.180-0.300 0.124-0.106 0.021-0.212 0.042h-0.325l-0.212-0.042-0.105-0.021-0.300-0.124-0.270-0.180-0.229-0.229-0.180-0.270-0.124-0.300-0.063-0.318v-0.325l0.063-0.319 0.124-0.300 0.180-0.270 0.229-0.229 0.270-0.180 0.029-0.012 0.058-0.024 0.212-0.087 0.137-0.027zm-52.07 5.080h0.325l0.212 0.041 0.106 0.021 0.300 0.124 0.270 0.180 0.229 0.229 0.121 0.182 0.058 0.087h0l0.114 0.275 0.01 0.023 0.063 0.318v0.325l-0.035 0.179-0.027 0.139-0.01 0.023-0.114 0.275h-0l-0.180 0.270-0.229 0.229-0.270 0.180-0.300 0.124-0.106 0.020-0.212 0.042h-0.325l-0.212-0.042-0.105-0.020-0.300-0.124-0.270-0.180-0.229-0.229-0.180-0.270-0.114-0.275-0.01-0.023-0.027-0.139-0.036-0.179v-0.325l0.063-0.318 0.01-0.023 0.114-0.275 0.058-0.087 0.121-0.182 0.229-0.229 0.270-0.180 0.300-0.124 0.105-0.021z"
          fill="#2b6b99"
        />

        <!-- USB Connector -->
        <g style="fill:#b3b2b2;stroke:#b3b2b2;stroke-width:0.010">
          <ellipse cx="3.84" cy="9.56" rx="1.12" ry="1.03" />
          <ellipse cx="3.84" cy="21.04" rx="1.12" ry="1.03" />
          <g fill="#000">
            <rect width="11" height="11.93" x="-0.05" y="9.72" rx="0.2" ry="0.2" opacity="0.24" />
          </g>
          <rect x="-4" y="9.37" height="11.85" width="14.46" />
          <rect x="-4" y="9.61" height="11.37" width="14.05" fill="#706f6f" />
          <rect x="-4" y="9.71" height="11.17" width="13.95" fill="#9d9d9c" />
        </g>

        <!-- Power jack -->
        <g stroke-width=".254" fill="black">
          <path
            d="m-2.58 48.53v2.289c0 0.279 0.228 0.508 0.508 0.508h1.722c0.279 0 0.508-0.228 0.508-0.508v-2.289z"
            fill="#252728"
            opacity=".3"
          />
          <path
            d="m11.334 42.946c0-0.558-0.509-1.016-1.132-1.016h-10.043v9.652h10.043c0.622 0 1.132-0.457 1.132-1.016z"
            opacity=".3"
          />
          <path
            d="m-2.072 40.914c-0.279 0-0.507 0.204-0.507 0.454v8.435c0 0.279 0.228 0.507 0.507 0.507h1.722c0.279 0 0.507-0.228 0.507-0.507v-8.435c0-0.249-0.228-0.454-0.507-0.454z"
          />
          <path
            d="m-2.58 48.784v1.019c0 0.279 0.228 0.508 0.508 0.508h1.722c0.279 0 0.508-0.228 0.508-0.508v-1.019z"
            opacity=".3"
          />
          <path
            d="m11.334 43.327c0.139 0 0.254 0.114 0.254 0.254v4.064c0 0.139-0.114 0.254-0.254 0.254"
          />
          <path
            d="m11.334 42.438c0-0.558-0.457-1.016-1.016-1.016h-10.16v8.382h10.16c0.558 0 1.016-0.457 1.016-1.016z"
          />
          <path
            d="m10.064 49.804h-9.906v-8.382h1.880c-1.107 0-1.363 1.825-1.363 3.826 0 1.765 1.147 3.496 3.014 3.496h6.374z"
            opacity=".3"
          />
          <rect x="10.064" y="41.422" width=".254" height="8.382" fill="#ffffff" opacity=".2" />
          <path
            d="m10.318 48.744v1.059c0.558 0 1.016-0.457 1.016-1.016v-0.364c0 0.313-1.016 0.320-1.016 0.320z"
            opacity=".3"
          />
        </g>

        <!-- Pin Headers -->
        <g transform="translate(17.497 1.27)">
          <rect width="${0.38 + 2.54 * 10}" height="2.54" fill="url(#pins)"></rect>
        </g>
        <g transform="translate(44.421 1.27)">
          <rect width="${0.38 + 2.54 * 8}" height="2.54" fill="url(#pins)"></rect>
        </g>
        <g transform="translate(26.641 49.53)">
          <rect width="${0.38 + 2.54 * 8}" height="2.54" fill="url(#pins)"></rect>
        </g>
        <g transform="translate(49.501 49.53)">
          <rect width="${0.38 + 2.54 * 6}" height="2.54" fill="url(#pins)"></rect>
        </g>

        <!-- MCU -->
        <g>
          <path
            d="m64.932 41.627h-36.72c-0.209 0-0.379-0.170-0.379-0.379v-8.545c0-0.209 0.170-0.379 0.379-0.379h36.72c0.209 0 0.379 0.170 0.379 0.379v8.545c0 0.209-0.169 0.379-0.379 0.379z"
            fill="#292c2d"
          />
          <path
            d="m65.019 40.397c0 0.279-0.228 0.508-0.508 0.508h-35.879c-0.279 0-0.507 0.025-0.507-0.254v-6.338c0-0.279 0.228-0.508 0.507-0.508h35.879c0.279 0 0.508 0.228 0.508 0.508z"
            opacity=".3"
          />
          <path
            d="m65.019 40.016c0 0.279-0.228 0.508-0.508 0.508h-35.879c-0.279 0-0.507 0.448-0.507-0.508v-6.084c0-0.279 0.228-0.508 0.507-0.508h35.879c0.279 0 0.508 0.228 0.508 0.508z"
            fill="#3c4042"
          />
          <rect
            transform="translate(29.205, 32.778)"
            fill="url(#mcu-leads)"
            height="0.508"
            width="35.56"
          ></rect>
          <rect
            transform="translate(29.205, 41.159) scale(1 -1)"
            fill="url(#mcu-leads)"
            height="0.508"
            width="35.56"
          ></rect>
          <circle cx="33.269" cy="36.847" r="1.016" fill="#252728" />
          <circle cx="59.939" cy="36.847" r="1.016" fill="#252728" />
        </g>

        <!-- Programming Headers -->
        <g transform="translate(14.1 4.4)">
          <rect width="7" height="4.80" fill="url(#pin-male)" />
        </g>

        <g transform="translate(63 27.2) rotate(270 0 0)">
          <rect width="7" height="4.80" fill="url(#pin-male)" />
        </g>

        <!-- LEDs -->
        <g transform="translate(57.3, 16.21)">
          <use xlink:href="#led-body" />
          ${ledPower && _litElement.svg`<circle cx="1.3" cy="0.55" r="1.3" fill="#80ff80" filter="url(#ledFilter)" />`}
        </g>

        <text fill="#fff">
          <tspan x="60.88" y="17.5">ON</tspan>
        </text>

        <g transform="translate(26.87,11.69)">
          <use xlink:href="#led-body" />
          ${led13 && _litElement.svg`<circle cx="1.3" cy="0.55" r="1.3" fill="#ff8080" filter="url(#ledFilter)" />`}
        </g>

        <g transform="translate(26.9, 16.2)">
          <use xlink:href="#led-body" />
          ${ledTX && _litElement.svg`<circle cx="0.975" cy="0.55" r="1.3" fill="yellow" filter="url(#ledFilter)" />`}
        </g>

        <g transform="translate(26.9, 18.5)">
          <use xlink:href="#led-body" />
          ${ledRX && _litElement.svg`<circle cx="0.975" cy="0.55" r="1.3" fill="yellow" filter="url(#ledFilter)" />`}
        </g>

        <text fill="#fff" style="text-anchor: end">
          <tspan x="26.5" y="13">L</tspan>
          <tspan x="26.5" y="17.5">TX</tspan>
          <tspan x="26.5" y="19.8">RX</tspan>
          <tspan x="26.5" y="20">&nbsp;</tspan>
        </text>

        <!-- Pin Labels -->
        <rect x="28.27" y="10.34" width="36.5" height="0.16" fill="#fff"></rect>
        <text fill="#fff" style="font-weight: 900">
          <tspan x="40.84" y="9.48">DIGITAL (PWM ~)</tspan>
        </text>
        <text
          transform="translate(22.6 4) rotate(270 0 0)"
          fill="#fff"
          style="font-size: 2px; text-anchor: end; font-family: monospace"
        >
          <tspan x="0" dy="2.54">AREF</tspan>
          <tspan x="0" dy="2.54">GND</tspan>
          <tspan x="0" dy="2.54">13</tspan>
          <tspan x="0" dy="2.54">12</tspan>
          <tspan x="0" dy="2.54">~11</tspan>
          <tspan x="0" dy="2.54">~10</tspan>
          <tspan x="0" dy="2.54">~9</tspan>
          <tspan x="0" dy="2.54">8</tspan>
          <tspan x="0" dy="4.08">~7</tspan>
          <tspan x="0" dy="2.54">~6</tspan>
          <tspan x="0" dy="2.54">~5</tspan>
          <tspan x="0" dy="2.54">4</tspan>
          <tspan x="0" dy="2.54">~3</tspan>
          <tspan x="0" dy="2.54">2</tspan>
          <tspan x="0" dy="2.54">TX→1</tspan>
          <tspan x="0" dy="2.54">RX←0</tspan>
          <tspan x="0" dy="2.54">&nbsp;</tspan>
        </text>

        <rect x="33.90" y="42.76" width="12.84" height="0.16" fill="#fff"></rect>
        <rect x="49.48" y="42.76" width="14.37" height="0.16" fill="#fff"></rect>
        <text fill="#fff" style="font-weight: 900">
          <tspan x="41" y="44.96">POWER</tspan>
          <tspan x="53.5" y="44.96">ANALOG IN</tspan>
        </text>
        <text transform="translate(29.19 49) rotate(270 0 0)" fill="#fff" style="font-weight: 700">
          <tspan x="0" dy="2.54">IOREF</tspan>
          <tspan x="0" dy="2.54">RESET</tspan>
          <tspan x="0" dy="2.54">3.3V</tspan>
          <tspan x="0" dy="2.54">5V</tspan>
          <tspan x="0" dy="2.54">GND</tspan>
          <tspan x="0" dy="2.54">GND</tspan>
          <tspan x="0" dy="2.54">Vin</tspan>
          <tspan x="0" dy="4.54">A0</tspan>
          <tspan x="0" dy="2.54">A1</tspan>
          <tspan x="0" dy="2.54">A2</tspan>
          <tspan x="0" dy="2.54">A3</tspan>
          <tspan x="0" dy="2.54">A4</tspan>
          <tspan x="0" dy="2.54">A5</tspan>
          <tspan x="0" dy="2.54">&nbsp;</tspan>
        </text>

        <!-- Logo -->
        <path
          style="fill:none;stroke:#fff;stroke-width:1.03"
          d="m 34.21393,12.01079 c -1.66494,-0.13263 -3.06393,1.83547 -2.37559,3.36182 0.66469,1.65332 3.16984,2.10396 4.36378,0.77797 1.15382,-1.13053 1.59956,-2.86476 3.00399,-3.75901 1.43669,-0.9801 3.75169,-0.0547 4.02384,1.68886 0.27358,1.66961 -1.52477,3.29596 -3.15725,2.80101 -1.20337,-0.27199 -2.06928,-1.29866 -2.56193,-2.37788 -0.6046,-1.0328 -1.39499,-2.13327 -2.62797,-2.42367 -0.2191,-0.0497 -0.44434,-0.0693 -0.66887,-0.0691 z"
        />
        <path
          style="fill:none;stroke:#fff;stroke-width:0.56"
          d="m 39.67829,14.37519 h 1.75141 m -0.89321,-0.8757 v 1.7514 m -7.30334,-0.8757 h 2.10166"
        />
        <text x="31" y="20.2" style="font-size:2.8px;font-weight:bold;line-height:1.25;fill:#fff">
          ARDUINO
        </text>

        <rect
          style="fill:none;stroke:#fff;stroke-width:0.1;stroke-dasharray:0.1, 0.1"
          width="11"
          height="5.45"
          x="45.19"
          y="11.83"
          rx="1"
          ry="1"
        />

        <text x="46.5" y="16" style="font-size:5px; line-height:1.25" fill="#fff">
          UNO
        </text>
      </svg>
    `;
  }

};
exports.ArduinoUnoElement = ArduinoUnoElement;

__decorate([(0, _litElement.property)()], ArduinoUnoElement.prototype, "led13", void 0);

__decorate([(0, _litElement.property)()], ArduinoUnoElement.prototype, "ledRX", void 0);

__decorate([(0, _litElement.property)()], ArduinoUnoElement.prototype, "ledTX", void 0);

__decorate([(0, _litElement.property)()], ArduinoUnoElement.prototype, "ledPower", void 0);

exports.ArduinoUnoElement = ArduinoUnoElement = __decorate([(0, _litElement.customElement)('wokwi-arduino-uno')], ArduinoUnoElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/lcd1602-font-a00.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.fontA00 = void 0;
// Font rasterized from datasheet: https://www.sparkfun.com/datasheets/LCD/HD44780.pdf
// prettier-ignore
const fontA00 = new Uint8Array([
/* 0 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 1 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 2 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 3 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 4 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 5 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 6 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 7 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 8 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 9 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 10 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 11 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 12 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 13 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 14 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 15 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 16 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 17 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 18 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 19 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 20 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 21 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 22 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 23 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 24 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 25 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 26 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 27 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 28 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 29 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 30 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 31 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 32 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 33 */
4, 4, 4, 4, 0, 0, 4, 0,
/* 34 */
10, 10, 10, 0, 0, 0, 0, 0,
/* 35 */
10, 10, 31, 10, 31, 10, 10, 0,
/* 36 */
4, 30, 5, 14, 20, 15, 4, 0,
/* 37 */
3, 19, 8, 4, 2, 25, 24, 0,
/* 38 */
6, 9, 5, 2, 21, 9, 22, 0,
/* 39 */
6, 4, 2, 0, 0, 0, 0, 0,
/* 40 */
8, 4, 2, 2, 2, 4, 8, 0,
/* 41 */
2, 4, 8, 8, 8, 4, 2, 0,
/* 42 */
0, 4, 21, 14, 21, 4, 0, 0,
/* 43 */
0, 4, 4, 31, 4, 4, 0, 0,
/* 44 */
0, 0, 0, 0, 6, 4, 2, 0,
/* 45 */
0, 0, 0, 31, 0, 0, 0, 0,
/* 46 */
0, 0, 0, 0, 0, 6, 6, 0,
/* 47 */
0, 16, 8, 4, 2, 1, 0, 0,
/* 48 */
14, 17, 25, 21, 19, 17, 14, 0,
/* 49 */
4, 6, 4, 4, 4, 4, 14, 0,
/* 50 */
14, 17, 16, 8, 4, 2, 31, 0,
/* 51 */
31, 8, 4, 8, 16, 17, 14, 0,
/* 52 */
8, 12, 10, 9, 31, 8, 8, 0,
/* 53 */
31, 1, 15, 16, 16, 17, 14, 0,
/* 54 */
12, 2, 1, 15, 17, 17, 14, 0,
/* 55 */
31, 17, 16, 8, 4, 4, 4, 0,
/* 56 */
14, 17, 17, 14, 17, 17, 14, 0,
/* 57 */
14, 17, 17, 30, 16, 8, 6, 0,
/* 58 */
0, 6, 6, 0, 6, 6, 0, 0,
/* 59 */
0, 6, 6, 0, 6, 4, 2, 0,
/* 60 */
8, 4, 2, 1, 2, 4, 8, 0,
/* 61 */
0, 0, 31, 0, 31, 0, 0, 0,
/* 62 */
2, 4, 8, 16, 8, 4, 2, 0,
/* 63 */
14, 17, 16, 8, 4, 0, 4, 0,
/* 64 */
14, 17, 16, 22, 21, 21, 14, 0,
/* 65 */
14, 17, 17, 17, 31, 17, 17, 0,
/* 66 */
15, 17, 17, 15, 17, 17, 15, 0,
/* 67 */
14, 17, 1, 1, 1, 17, 14, 0,
/* 68 */
7, 9, 17, 17, 17, 9, 7, 0,
/* 69 */
31, 1, 1, 15, 1, 1, 31, 0,
/* 70 */
31, 1, 1, 15, 1, 1, 1, 0,
/* 71 */
14, 17, 1, 29, 17, 17, 30, 0,
/* 72 */
17, 17, 17, 31, 17, 17, 17, 0,
/* 73 */
14, 4, 4, 4, 4, 4, 14, 0,
/* 74 */
28, 8, 8, 8, 8, 9, 6, 0,
/* 75 */
17, 9, 5, 3, 5, 9, 17, 0,
/* 76 */
1, 1, 1, 1, 1, 1, 31, 0,
/* 77 */
17, 27, 21, 21, 17, 17, 17, 0,
/* 78 */
17, 17, 19, 21, 25, 17, 17, 0,
/* 79 */
14, 17, 17, 17, 17, 17, 14, 0,
/* 80 */
15, 17, 17, 15, 1, 1, 1, 0,
/* 81 */
14, 17, 17, 17, 21, 9, 22, 0,
/* 82 */
15, 17, 17, 15, 5, 9, 17, 0,
/* 83 */
30, 1, 1, 14, 16, 16, 15, 0,
/* 84 */
31, 4, 4, 4, 4, 4, 4, 0,
/* 85 */
17, 17, 17, 17, 17, 17, 14, 0,
/* 86 */
17, 17, 17, 17, 17, 10, 4, 0,
/* 87 */
17, 17, 17, 21, 21, 21, 10, 0,
/* 88 */
17, 17, 10, 4, 10, 17, 17, 0,
/* 89 */
17, 17, 17, 10, 4, 4, 4, 0,
/* 90 */
31, 16, 8, 4, 2, 1, 31, 0,
/* 91 */
7, 1, 1, 1, 1, 1, 7, 0,
/* 92 */
17, 10, 31, 4, 31, 4, 4, 0,
/* 93 */
14, 8, 8, 8, 8, 8, 14, 0,
/* 94 */
4, 10, 17, 0, 0, 0, 0, 0,
/* 95 */
0, 0, 0, 0, 0, 0, 31, 0,
/* 96 */
2, 4, 8, 0, 0, 0, 0, 0,
/* 97 */
0, 0, 14, 16, 30, 17, 30, 0,
/* 98 */
1, 1, 13, 19, 17, 17, 15, 0,
/* 99 */
0, 0, 14, 1, 1, 17, 14, 0,
/* 100 */
16, 16, 22, 25, 17, 17, 30, 0,
/* 101 */
0, 0, 14, 17, 31, 1, 14, 0,
/* 102 */
12, 18, 2, 7, 2, 2, 2, 0,
/* 103 */
0, 30, 17, 17, 30, 16, 14, 0,
/* 104 */
1, 1, 13, 19, 17, 17, 17, 0,
/* 105 */
4, 0, 6, 4, 4, 4, 14, 0,
/* 106 */
8, 0, 12, 8, 8, 9, 6, 0,
/* 107 */
1, 1, 9, 5, 3, 5, 9, 0,
/* 108 */
6, 4, 4, 4, 4, 4, 14, 0,
/* 109 */
0, 0, 11, 21, 21, 17, 17, 0,
/* 110 */
0, 0, 13, 19, 17, 17, 17, 0,
/* 111 */
0, 0, 14, 17, 17, 17, 14, 0,
/* 112 */
0, 0, 15, 17, 15, 1, 1, 0,
/* 113 */
0, 0, 22, 25, 30, 16, 16, 0,
/* 114 */
0, 0, 13, 19, 1, 1, 1, 0,
/* 115 */
0, 0, 14, 1, 14, 16, 15, 0,
/* 116 */
2, 2, 7, 2, 2, 18, 12, 0,
/* 117 */
0, 0, 17, 17, 17, 25, 22, 0,
/* 118 */
0, 0, 17, 17, 17, 10, 4, 0,
/* 119 */
0, 0, 17, 21, 21, 21, 10, 0,
/* 120 */
0, 0, 17, 10, 4, 10, 17, 0,
/* 121 */
0, 0, 17, 17, 30, 16, 14, 0,
/* 122 */
0, 0, 31, 8, 4, 2, 31, 0,
/* 123 */
8, 4, 4, 2, 4, 4, 8, 0,
/* 124 */
4, 4, 4, 4, 4, 4, 4, 0,
/* 125 */
2, 4, 4, 8, 4, 4, 2, 0,
/* 126 */
0, 4, 8, 31, 8, 4, 0, 0,
/* 127 */
0, 4, 2, 31, 2, 4, 0, 0,
/* 128 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 129 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 130 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 131 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 132 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 133 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 134 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 135 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 136 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 137 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 138 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 139 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 140 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 141 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 142 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 143 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 144 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 145 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 146 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 147 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 148 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 149 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 150 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 151 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 152 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 153 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 154 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 155 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 156 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 157 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 158 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 159 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 160 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 161 */
0, 0, 0, 0, 7, 5, 7, 0,
/* 162 */
28, 4, 4, 4, 0, 0, 0, 0,
/* 163 */
0, 0, 0, 4, 4, 4, 7, 0,
/* 164 */
0, 0, 0, 0, 1, 2, 4, 0,
/* 165 */
0, 0, 0, 6, 6, 0, 0, 0,
/* 166 */
0, 31, 16, 31, 16, 8, 4, 0,
/* 167 */
0, 0, 31, 16, 12, 4, 2, 0,
/* 168 */
0, 0, 8, 4, 6, 5, 4, 0,
/* 169 */
0, 0, 4, 31, 17, 16, 12, 0,
/* 170 */
0, 0, 31, 4, 4, 4, 31, 0,
/* 171 */
0, 0, 8, 31, 12, 10, 9, 0,
/* 172 */
0, 0, 2, 31, 18, 10, 2, 0,
/* 173 */
0, 0, 0, 14, 8, 8, 31, 0,
/* 174 */
0, 0, 15, 8, 15, 8, 15, 0,
/* 175 */
0, 0, 0, 21, 21, 16, 12, 0,
/* 176 */
0, 0, 0, 31, 0, 0, 0, 0,
/* 177 */
31, 16, 20, 12, 4, 4, 2, 0,
/* 178 */
16, 8, 4, 6, 5, 4, 4, 0,
/* 179 */
4, 31, 17, 17, 16, 8, 4, 0,
/* 180 */
0, 31, 4, 4, 4, 4, 31, 0,
/* 181 */
8, 31, 8, 12, 10, 9, 8, 0,
/* 182 */
2, 31, 18, 18, 18, 18, 9, 0,
/* 183 */
4, 31, 4, 31, 4, 4, 4, 0,
/* 184 */
0, 30, 18, 17, 16, 8, 6, 0,
/* 185 */
2, 30, 9, 8, 8, 8, 4, 0,
/* 186 */
0, 31, 16, 16, 16, 16, 31, 0,
/* 187 */
10, 31, 10, 10, 8, 4, 2, 0,
/* 188 */
0, 3, 16, 19, 16, 8, 7, 0,
/* 189 */
0, 31, 16, 8, 4, 10, 17, 0,
/* 190 */
2, 31, 18, 10, 2, 2, 28, 0,
/* 191 */
0, 17, 17, 18, 16, 8, 6, 0,
/* 192 */
0, 30, 18, 21, 24, 8, 6, 0,
/* 193 */
8, 7, 4, 31, 4, 4, 2, 0,
/* 194 */
0, 21, 21, 21, 16, 8, 4, 0,
/* 195 */
14, 0, 31, 4, 4, 4, 2, 0,
/* 196 */
2, 2, 2, 6, 10, 2, 2, 0,
/* 197 */
4, 4, 31, 4, 4, 2, 1, 0,
/* 198 */
0, 14, 0, 0, 0, 0, 31, 0,
/* 199 */
0, 31, 16, 10, 4, 10, 1, 0,
/* 200 */
4, 31, 8, 4, 14, 21, 4, 0,
/* 201 */
8, 8, 8, 8, 8, 4, 2, 0,
/* 202 */
0, 4, 8, 17, 17, 17, 17, 0,
/* 203 */
1, 1, 31, 1, 1, 1, 30, 0,
/* 204 */
0, 31, 16, 16, 16, 8, 6, 0,
/* 205 */
0, 2, 5, 8, 16, 16, 0, 0,
/* 206 */
4, 31, 4, 4, 21, 21, 4, 0,
/* 207 */
0, 31, 16, 16, 10, 4, 8, 0,
/* 208 */
0, 14, 0, 14, 0, 14, 16, 0,
/* 209 */
0, 4, 2, 1, 17, 31, 16, 0,
/* 210 */
0, 16, 16, 10, 4, 10, 1, 0,
/* 211 */
0, 31, 2, 31, 2, 2, 28, 0,
/* 212 */
2, 2, 31, 18, 10, 2, 2, 0,
/* 213 */
0, 14, 8, 8, 8, 8, 31, 0,
/* 214 */
0, 31, 16, 31, 16, 16, 31, 0,
/* 215 */
14, 0, 31, 16, 16, 8, 4, 0,
/* 216 */
9, 9, 9, 9, 8, 4, 2, 0,
/* 217 */
0, 4, 5, 5, 21, 21, 13, 0,
/* 218 */
0, 1, 1, 17, 9, 5, 3, 0,
/* 219 */
0, 31, 17, 17, 17, 17, 31, 0,
/* 220 */
0, 31, 17, 17, 16, 8, 4, 0,
/* 221 */
0, 3, 0, 16, 16, 8, 7, 0,
/* 222 */
4, 9, 2, 0, 0, 0, 0, 0,
/* 223 */
7, 5, 7, 0, 0, 0, 0, 0,
/* 224 */
0, 0, 18, 21, 9, 9, 22, 0,
/* 225 */
10, 0, 14, 16, 30, 17, 30, 0,
/* 226 */
0, 0, 14, 17, 15, 17, 15, 1,
/* 227 */
0, 0, 14, 1, 6, 17, 14, 0,
/* 228 */
0, 0, 17, 17, 17, 25, 23, 1,
/* 229 */
0, 0, 30, 5, 9, 17, 14, 0,
/* 230 */
0, 0, 12, 18, 17, 17, 15, 1,
/* 231 */
0, 0, 30, 17, 17, 17, 30, 16,
/* 232 */
0, 0, 28, 4, 4, 5, 2, 0,
/* 233 */
0, 8, 11, 8, 0, 0, 0, 0,
/* 234 */
8, 0, 12, 8, 8, 8, 8, 8,
/* 235 */
0, 5, 2, 5, 0, 0, 0, 0,
/* 236 */
0, 4, 14, 5, 21, 14, 4, 0,
/* 237 */
2, 2, 7, 2, 7, 2, 30, 0,
/* 238 */
14, 0, 13, 19, 17, 17, 17, 0,
/* 239 */
10, 0, 14, 17, 17, 17, 14, 0,
/* 240 */
0, 0, 13, 19, 17, 17, 15, 1,
/* 241 */
0, 0, 22, 25, 17, 17, 30, 16,
/* 242 */
0, 14, 17, 31, 17, 17, 14, 0,
/* 243 */
0, 0, 0, 26, 21, 11, 0, 0,
/* 244 */
0, 0, 14, 17, 17, 10, 27, 0,
/* 245 */
10, 0, 17, 17, 17, 17, 25, 22,
/* 246 */
31, 1, 2, 4, 2, 1, 31, 0,
/* 247 */
0, 0, 31, 10, 10, 10, 25, 0,
/* 248 */
31, 0, 17, 10, 4, 10, 17, 0,
/* 249 */
0, 0, 17, 17, 17, 17, 30, 16,
/* 250 */
0, 16, 15, 4, 31, 4, 4, 0,
/* 251 */
0, 0, 31, 2, 30, 18, 17, 0,
/* 252 */
0, 0, 31, 21, 31, 17, 17, 0,
/* 253 */
0, 4, 0, 31, 0, 4, 0, 0,
/* 254 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 255 */
31, 31, 31, 31, 31, 31, 31, 31]);
exports.fontA00 = fontA00;
},{}],"../../node_modules/@wokwi/elements/dist/esm/lcd1602-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.LCD1602Element = void 0;

var _litElement = require("lit-element");

var _lcd1602FontA = require("./lcd1602-font-a00");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

const ROWS = 2;
const COLS = 16;
const charXSpacing = 3.55;
const charYSpacing = 5.95;
const backgroundColors = {
  green: '#6cb201',
  blue: '#000eff'
};
let LCD1602Element = class LCD1602Element extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.color = 'black';
    this.background = 'green';
    this.characters = new Uint8Array(32);
    this.font = _lcd1602FontA.fontA00;
    this.cursor = false;
    this.blink = false;
    this.cursorX = 0;
    this.cursorY = 0;
    this.backlight = true;
  }

  static get styles() {
    return _litElement.css`
      .cursor-blink {
        animation: cursor-blink;
      }

      @keyframes cursor-blink {
        from {
          opacity: 0;
        }
        25% {
          opacity: 1;
        }
        75% {
          opacity: 1;
        }
        to {
          opacity: 0;
        }
      }
    `;
  }

  path(characters) {
    const xSpacing = 0.6;
    const ySpacing = 0.7;
    const result = [];

    for (let i = 0; i < characters.length; i++) {
      const charX = i % COLS * charXSpacing;
      const charY = Math.floor(i / COLS) * charYSpacing;

      for (let py = 0; py < 8; py++) {
        const row = this.font[characters[i] * 8 + py];

        for (let px = 0; px < 5; px++) {
          if (row & 1 << px) {
            const x = (charX + px * xSpacing).toFixed(2);
            const y = (charY + py * ySpacing).toFixed(2);
            result.push(`M ${x} ${y}h0.55v0.65h-0.55Z`);
          }
        }
      }
    }

    return result.join(' ');
  }

  renderCursor() {
    const xOffset = 12.45 + this.cursorX * charXSpacing;
    const yOffset = 12.55 + this.cursorY * charYSpacing;

    if (this.cursorX < 0 || this.cursorX >= COLS || this.cursorY < 0 || this.cursorY >= ROWS) {
      return null;
    }

    const result = [];

    if (this.blink) {
      result.push(_litElement.svg`
        <rect x="${xOffset}" y="${yOffset}" width="2.95" height="5.55" fill="${this.color}">
          <animate
            attributeName="opacity"
            values="0;0;0;0;1;1;0;0;0;0"
            dur="1s"
            fill="freeze"
            repeatCount="indefinite"
          />
        </rect>
      `);
    }

    if (this.cursor) {
      const y = yOffset + 0.7 * 7;
      result.push(_litElement.svg`<rect x="${xOffset}" y="${y}" width="2.95" height="0.65" fill="${this.color}" />`);
    }

    return result;
  }

  render() {
    const {
      color,
      characters,
      background
    } = this;
    const darken = this.backlight ? 0 : 0.5;
    const actualBgColor = background in backgroundColors ? backgroundColors[background] : backgroundColors; // Dimensions according to:
    // https://www.winstar.com.tw/products/character-lcd-display-module/16x2-lcd.html

    return _litElement.html`
      <svg
        width="80mm"
        height="36mm"
        version="1.1"
        viewBox="0 0 80 36"
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <pattern
            id="characters"
            width="3.55"
            height="5.95"
            patternUnits="userSpaceOnUse"
            x="12.45"
            y="12.55"
          >
            <rect width="2.95" height="5.55" fill-opacity="0.05" />
          </pattern>
        </defs>
        <rect width="80" height="36" fill="#087f45" />
        <rect x="4.95" y="5.7" width="71.2" height="25.2" />
        <rect x="7.55" y="10.3" width="66" height="16" rx="1.5" ry="1.5" fill="${actualBgColor}" />
        <rect x="7.55" y="10.3" width="66" height="16" rx="1.5" ry="1.5" opacity="${darken}" />
        <rect x="12.45" y="12.55" width="56.2" height="11.5" fill="url(#characters)" />
        <path d="${this.path(characters)}" transform="translate(12.45, 12.55)" fill="${color}" />
        ${this.renderCursor()}
      </svg>
    `;
  }

};
exports.LCD1602Element = LCD1602Element;

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "color", void 0);

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "background", void 0);

__decorate([(0, _litElement.property)({
  type: Array
})], LCD1602Element.prototype, "characters", void 0);

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "font", void 0);

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "cursor", void 0);

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "blink", void 0);

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "cursorX", void 0);

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "cursorY", void 0);

__decorate([(0, _litElement.property)()], LCD1602Element.prototype, "backlight", void 0);

exports.LCD1602Element = LCD1602Element = __decorate([(0, _litElement.customElement)('wokwi-lcd1602')], LCD1602Element);
},{"lit-element":"../../node_modules/lit-element/lit-element.js","./lcd1602-font-a00":"../../node_modules/@wokwi/elements/dist/esm/lcd1602-font-a00.js"}],"../../node_modules/@wokwi/elements/dist/esm/lcd1602-font-a02.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.fontA02 = void 0;
// Font rasterized from datasheet: https://www.sparkfun.com/datasheets/LCD/HD44780.pdf
// prettier-ignore
const fontA02 = new Uint8Array([
/* 0 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 1 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 2 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 3 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 4 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 5 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 6 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 7 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 8 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 9 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 10 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 11 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 12 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 13 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 14 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 15 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 32 */
0, 2, 6, 14, 30, 14, 6, 2,
/* 33 */
0, 8, 12, 14, 15, 14, 12, 8,
/* 34 */
0, 18, 9, 27, 0, 0, 0, 0,
/* 35 */
0, 27, 18, 9, 0, 0, 0, 0,
/* 36 */
0, 4, 14, 31, 0, 4, 14, 31,
/* 37 */
0, 31, 14, 4, 0, 31, 14, 4,
/* 38 */
0, 0, 14, 31, 31, 31, 14, 0,
/* 39 */
0, 16, 16, 20, 18, 31, 2, 4,
/* 40 */
0, 4, 14, 21, 4, 4, 4, 4,
/* 41 */
0, 4, 4, 4, 4, 21, 14, 4,
/* 42 */
0, 0, 4, 8, 31, 8, 4, 0,
/* 43 */
0, 0, 4, 2, 31, 2, 4, 0,
/* 44 */
0, 8, 4, 2, 4, 8, 0, 31,
/* 45 */
0, 2, 4, 8, 4, 2, 0, 31,
/* 46 */
0, 0, 4, 4, 14, 14, 31, 0,
/* 47 */
0, 0, 31, 14, 14, 4, 4, 0,
/* 48 */
0, 0, 0, 0, 0, 0, 0, 0,
/* 49 */
0, 4, 4, 4, 4, 0, 0, 4,
/* 50 */
0, 10, 10, 10, 0, 0, 0, 0,
/* 51 */
0, 10, 10, 31, 10, 31, 10, 10,
/* 52 */
0, 4, 30, 5, 14, 20, 15, 4,
/* 53 */
0, 3, 19, 8, 4, 2, 25, 24,
/* 54 */
0, 6, 9, 5, 2, 21, 9, 22,
/* 55 */
0, 6, 4, 2, 0, 0, 0, 0,
/* 56 */
0, 8, 4, 2, 2, 2, 4, 8,
/* 57 */
0, 2, 4, 8, 8, 8, 4, 2,
/* 58 */
0, 0, 4, 21, 14, 21, 4, 0,
/* 59 */
0, 0, 4, 4, 31, 4, 4, 0,
/* 60 */
0, 0, 0, 0, 0, 6, 4, 2,
/* 61 */
0, 0, 0, 0, 31, 0, 0, 0,
/* 62 */
0, 0, 0, 0, 0, 0, 6, 6,
/* 63 */
0, 0, 16, 8, 4, 2, 1, 0,
/* 64 */
0, 14, 17, 25, 21, 19, 17, 14,
/* 65 */
0, 4, 6, 4, 4, 4, 4, 14,
/* 66 */
0, 14, 17, 16, 8, 4, 2, 31,
/* 67 */
0, 31, 8, 4, 8, 16, 17, 14,
/* 68 */
0, 8, 12, 10, 9, 31, 8, 8,
/* 69 */
0, 31, 1, 15, 16, 16, 17, 14,
/* 70 */
0, 12, 2, 1, 15, 17, 17, 14,
/* 71 */
0, 31, 17, 16, 8, 4, 4, 4,
/* 72 */
0, 14, 17, 17, 14, 17, 17, 14,
/* 73 */
0, 14, 17, 17, 30, 16, 8, 6,
/* 74 */
0, 0, 6, 6, 0, 6, 6, 0,
/* 75 */
0, 0, 6, 6, 0, 6, 4, 2,
/* 76 */
0, 8, 4, 2, 1, 2, 4, 8,
/* 77 */
0, 0, 0, 31, 0, 31, 0, 0,
/* 78 */
0, 2, 4, 8, 16, 8, 4, 2,
/* 79 */
0, 14, 17, 16, 8, 4, 0, 4,
/* 80 */
0, 14, 17, 16, 22, 21, 21, 14,
/* 81 */
0, 4, 10, 17, 17, 31, 17, 17,
/* 82 */
0, 15, 17, 17, 15, 17, 17, 15,
/* 83 */
0, 14, 17, 1, 1, 1, 17, 14,
/* 84 */
0, 7, 9, 17, 17, 17, 9, 7,
/* 85 */
0, 31, 1, 1, 15, 1, 1, 31,
/* 86 */
0, 31, 1, 1, 15, 1, 1, 1,
/* 87 */
0, 14, 17, 1, 29, 17, 17, 30,
/* 88 */
0, 17, 17, 17, 31, 17, 17, 17,
/* 89 */
0, 14, 4, 4, 4, 4, 4, 14,
/* 90 */
0, 28, 8, 8, 8, 8, 9, 6,
/* 91 */
0, 17, 9, 5, 3, 5, 9, 17,
/* 92 */
0, 1, 1, 1, 1, 1, 1, 31,
/* 93 */
0, 17, 27, 21, 21, 17, 17, 17,
/* 94 */
0, 17, 17, 19, 21, 25, 17, 17,
/* 95 */
0, 14, 17, 17, 17, 17, 17, 14,
/* 96 */
0, 15, 17, 17, 15, 1, 1, 1,
/* 97 */
0, 14, 17, 17, 17, 21, 9, 22,
/* 98 */
0, 15, 17, 17, 15, 5, 9, 17,
/* 99 */
0, 14, 17, 1, 14, 16, 17, 14,
/* 100 */
0, 31, 4, 4, 4, 4, 4, 4,
/* 101 */
0, 17, 17, 17, 17, 17, 17, 14,
/* 102 */
0, 17, 17, 17, 17, 17, 10, 4,
/* 103 */
0, 17, 17, 17, 21, 21, 21, 10,
/* 104 */
0, 17, 17, 10, 4, 10, 17, 17,
/* 105 */
0, 17, 17, 17, 10, 4, 4, 4,
/* 106 */
0, 31, 16, 8, 4, 2, 1, 31,
/* 107 */
0, 14, 2, 2, 2, 2, 2, 14,
/* 108 */
0, 0, 1, 2, 4, 8, 16, 0,
/* 109 */
0, 14, 8, 8, 8, 8, 8, 14,
/* 110 */
0, 4, 10, 17, 0, 0, 0, 0,
/* 111 */
0, 0, 0, 0, 0, 0, 0, 31,
/* 112 */
0, 2, 4, 8, 0, 0, 0, 0,
/* 113 */
0, 0, 0, 14, 16, 30, 17, 30,
/* 114 */
0, 1, 1, 13, 19, 17, 17, 15,
/* 115 */
0, 0, 0, 14, 1, 1, 17, 14,
/* 116 */
0, 16, 16, 22, 25, 17, 17, 30,
/* 117 */
0, 0, 0, 14, 17, 31, 1, 14,
/* 118 */
0, 12, 18, 2, 7, 2, 2, 2,
/* 119 */
0, 0, 0, 30, 17, 30, 16, 14,
/* 120 */
0, 1, 1, 13, 19, 17, 17, 17,
/* 121 */
0, 4, 0, 4, 6, 4, 4, 14,
/* 122 */
0, 8, 0, 12, 8, 8, 9, 6,
/* 123 */
0, 1, 1, 9, 5, 3, 5, 9,
/* 124 */
0, 6, 4, 4, 4, 4, 4, 14,
/* 125 */
0, 0, 0, 11, 21, 21, 21, 21,
/* 126 */
0, 0, 0, 13, 19, 17, 17, 17,
/* 127 */
0, 0, 0, 14, 17, 17, 17, 14,
/* 128 */
0, 0, 0, 15, 17, 15, 1, 1,
/* 129 */
0, 0, 0, 22, 25, 30, 16, 16,
/* 130 */
0, 0, 0, 13, 19, 1, 1, 1,
/* 131 */
0, 0, 0, 14, 1, 14, 16, 15,
/* 132 */
0, 2, 2, 7, 2, 2, 18, 12,
/* 133 */
0, 0, 0, 17, 17, 17, 25, 22,
/* 134 */
0, 0, 0, 17, 17, 17, 10, 4,
/* 135 */
0, 0, 0, 17, 17, 21, 21, 10,
/* 136 */
0, 0, 0, 17, 10, 4, 10, 17,
/* 137 */
0, 0, 0, 17, 17, 30, 16, 14,
/* 138 */
0, 0, 0, 31, 8, 4, 2, 31,
/* 139 */
0, 8, 4, 4, 2, 4, 4, 8,
/* 140 */
0, 4, 4, 4, 4, 4, 4, 4,
/* 141 */
0, 2, 4, 4, 8, 4, 4, 2,
/* 142 */
0, 0, 0, 0, 22, 9, 0, 0,
/* 143 */
0, 4, 10, 17, 17, 17, 31, 0,
/* 144 */
0, 31, 17, 1, 15, 17, 17, 15,
/* 145 */
30, 20, 20, 18, 17, 31, 17, 17,
/* 146 */
0, 21, 21, 21, 14, 21, 21, 21,
/* 147 */
0, 15, 16, 16, 12, 16, 16, 15,
/* 148 */
0, 17, 17, 25, 21, 19, 17, 17,
/* 149 */
10, 4, 17, 17, 25, 21, 19, 17,
/* 150 */
0, 30, 20, 20, 20, 20, 21, 18,
/* 151 */
0, 31, 17, 17, 17, 17, 17, 17,
/* 152 */
0, 17, 17, 17, 10, 4, 2, 1,
/* 153 */
0, 17, 17, 17, 17, 17, 31, 16,
/* 154 */
0, 17, 17, 17, 30, 16, 16, 16,
/* 155 */
0, 0, 21, 21, 21, 21, 21, 31,
/* 156 */
0, 21, 21, 21, 21, 21, 31, 16,
/* 157 */
0, 3, 2, 2, 14, 18, 18, 14,
/* 158 */
0, 17, 17, 17, 19, 21, 21, 19,
/* 159 */
0, 14, 17, 20, 26, 16, 17, 14,
/* 160 */
0, 0, 0, 18, 21, 9, 9, 22,
/* 161 */
0, 4, 12, 20, 20, 4, 7, 7,
/* 162 */
0, 31, 17, 1, 1, 1, 1, 1,
/* 163 */
0, 0, 0, 31, 10, 10, 10, 25,
/* 164 */
0, 31, 1, 2, 4, 2, 1, 31,
/* 165 */
0, 0, 0, 30, 9, 9, 9, 6,
/* 166 */
12, 20, 28, 20, 20, 23, 27, 24,
/* 167 */
0, 0, 16, 14, 5, 4, 4, 8,
/* 168 */
0, 4, 14, 14, 14, 31, 4, 0,
/* 169 */
0, 14, 17, 17, 31, 17, 17, 14,
/* 170 */
0, 0, 14, 17, 17, 17, 10, 27,
/* 171 */
0, 12, 18, 4, 10, 17, 17, 14,
/* 172 */
0, 0, 0, 26, 21, 11, 0, 0,
/* 173 */
0, 0, 10, 31, 31, 31, 14, 4,
/* 174 */
0, 0, 0, 14, 1, 6, 17, 14,
/* 175 */
0, 14, 17, 17, 17, 17, 17, 17,
/* 176 */
0, 27, 27, 27, 27, 27, 27, 27,
/* 177 */
0, 4, 0, 0, 4, 4, 4, 4,
/* 178 */
0, 4, 14, 5, 5, 21, 14, 4,
/* 179 */
0, 12, 2, 2, 7, 2, 18, 13,
/* 180 */
0, 0, 17, 14, 10, 14, 17, 0,
/* 181 */
0, 17, 10, 31, 4, 31, 4, 4,
/* 182 */
0, 4, 4, 4, 0, 4, 4, 4,
/* 183 */
0, 12, 18, 4, 10, 4, 9, 6,
/* 184 */
0, 8, 20, 4, 31, 4, 5, 2,
/* 185 */
0, 31, 17, 21, 29, 21, 17, 31,
/* 186 */
0, 14, 16, 30, 17, 30, 0, 31,
/* 187 */
0, 0, 20, 10, 5, 10, 20, 0,
/* 188 */
0, 9, 21, 21, 23, 21, 21, 9,
/* 189 */
0, 30, 17, 17, 30, 20, 18, 17,
/* 190 */
0, 31, 17, 21, 17, 25, 21, 31,
/* 191 */
0, 4, 2, 6, 0, 0, 0, 0,
/* 192 */
6, 9, 9, 9, 6, 0, 0, 0,
/* 193 */
0, 4, 4, 31, 4, 4, 0, 31,
/* 194 */
6, 9, 4, 2, 15, 0, 0, 0,
/* 195 */
7, 8, 6, 8, 7, 0, 0, 0,
/* 196 */
7, 9, 7, 1, 9, 29, 9, 24,
/* 197 */
0, 17, 17, 17, 25, 23, 1, 1,
/* 198 */
0, 30, 25, 25, 30, 24, 24, 24,
/* 199 */
0, 0, 0, 0, 6, 6, 0, 0,
/* 200 */
0, 0, 0, 10, 17, 21, 21, 10,
/* 201 */
2, 3, 2, 2, 7, 0, 0, 0,
/* 202 */
0, 14, 17, 17, 17, 14, 0, 31,
/* 203 */
0, 0, 5, 10, 20, 10, 5, 0,
/* 204 */
17, 9, 5, 10, 13, 10, 30, 8,
/* 205 */
17, 9, 5, 10, 21, 16, 8, 28,
/* 206 */
3, 2, 3, 18, 27, 20, 28, 16,
/* 207 */
0, 4, 0, 4, 2, 1, 17, 14,
/* 208 */
2, 4, 4, 10, 17, 31, 17, 17,
/* 209 */
8, 4, 4, 10, 17, 31, 17, 17,
/* 210 */
4, 10, 0, 14, 17, 31, 17, 17,
/* 211 */
22, 9, 0, 14, 17, 31, 17, 17,
/* 212 */
10, 0, 4, 10, 17, 31, 17, 17,
/* 213 */
4, 10, 4, 14, 17, 31, 17, 17,
/* 214 */
0, 28, 6, 5, 29, 7, 5, 29,
/* 215 */
14, 17, 1, 1, 17, 14, 8, 12,
/* 216 */
2, 4, 0, 31, 1, 15, 1, 31,
/* 217 */
8, 4, 0, 31, 1, 15, 1, 31,
/* 218 */
4, 10, 0, 31, 1, 15, 1, 31,
/* 219 */
0, 10, 0, 31, 1, 15, 1, 31,
/* 220 */
2, 4, 0, 14, 4, 4, 4, 14,
/* 221 */
8, 4, 0, 14, 4, 4, 4, 14,
/* 222 */
4, 10, 0, 14, 4, 4, 4, 14,
/* 223 */
0, 10, 0, 14, 4, 4, 4, 14,
/* 224 */
0, 14, 18, 18, 23, 18, 18, 14,
/* 225 */
22, 9, 0, 17, 19, 21, 25, 17,
/* 226 */
2, 4, 14, 17, 17, 17, 17, 14,
/* 227 */
8, 4, 14, 17, 17, 17, 17, 14,
/* 228 */
4, 10, 0, 14, 17, 17, 17, 14,
/* 229 */
22, 9, 0, 14, 17, 17, 17, 14,
/* 230 */
10, 0, 14, 17, 17, 17, 17, 14,
/* 231 */
0, 0, 17, 10, 4, 10, 17, 0,
/* 232 */
0, 14, 4, 14, 21, 14, 4, 14,
/* 233 */
2, 4, 17, 17, 17, 17, 17, 14,
/* 234 */
8, 4, 17, 17, 17, 17, 17, 14,
/* 235 */
4, 10, 0, 17, 17, 17, 17, 14,
/* 236 */
10, 0, 17, 17, 17, 17, 17, 14,
/* 237 */
8, 4, 17, 10, 4, 4, 4, 4,
/* 238 */
3, 2, 14, 18, 18, 14, 2, 7,
/* 239 */
0, 12, 18, 18, 14, 18, 18, 13,
/* 240 */
2, 4, 0, 14, 16, 30, 17, 30,
/* 241 */
8, 4, 0, 14, 16, 30, 17, 30,
/* 242 */
4, 10, 0, 14, 16, 30, 17, 30,
/* 243 */
22, 9, 0, 14, 16, 30, 17, 30,
/* 244 */
0, 10, 0, 14, 16, 30, 17, 30,
/* 245 */
4, 10, 4, 14, 16, 30, 17, 30,
/* 246 */
0, 0, 11, 20, 30, 5, 21, 10,
/* 247 */
0, 0, 14, 1, 17, 14, 4, 6,
/* 248 */
2, 4, 0, 14, 17, 31, 1, 14,
/* 249 */
8, 4, 0, 14, 17, 31, 1, 14,
/* 250 */
4, 10, 0, 14, 17, 31, 1, 14,
/* 251 */
0, 10, 0, 14, 17, 31, 1, 14,
/* 252 */
2, 4, 0, 4, 6, 4, 4, 14,
/* 253 */
8, 4, 0, 4, 6, 4, 4, 14,
/* 254 */
4, 10, 0, 4, 6, 4, 4, 14,
/* 255 */
0, 10, 0, 4, 6, 4, 4, 14,
/* 256 */
0, 5, 2, 5, 8, 30, 17, 14,
/* 257 */
22, 9, 0, 13, 19, 17, 17, 17,
/* 258 */
2, 4, 0, 14, 17, 17, 17, 14,
/* 259 */
8, 4, 0, 14, 17, 17, 17, 14,
/* 260 */
0, 4, 10, 0, 14, 17, 17, 14,
/* 261 */
0, 22, 9, 0, 14, 17, 17, 14,
/* 262 */
0, 10, 0, 14, 17, 17, 17, 14,
/* 263 */
0, 0, 4, 0, 31, 0, 4, 0,
/* 264 */
0, 8, 4, 14, 21, 14, 4, 2,
/* 265 */
2, 4, 0, 17, 17, 17, 25, 22,
/* 266 */
8, 4, 0, 17, 17, 17, 25, 22,
/* 267 */
4, 10, 0, 17, 17, 17, 25, 22,
/* 268 */
0, 10, 0, 17, 17, 17, 25, 22,
/* 269 */
0, 8, 4, 17, 17, 30, 16, 14,
/* 270 */
0, 6, 4, 12, 20, 12, 4, 14,
/* 271 */
0, 10, 0, 17, 17, 30, 16, 14]);
exports.fontA02 = fontA02;
},{}],"../../node_modules/@wokwi/elements/dist/esm/led-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.LEDElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

const lightColors = {
  red: '#ff8080',
  green: '#80ff80',
  blue: '#8080ff',
  yellow: '#ffff80',
  orange: '#ffcf80',
  white: '#ffffff'
};
let LEDElement = class LEDElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.value = false;
    this.brightness = 1.0;
    this.color = 'red';
    this.lightColor = null;
    this.label = '';
  }

  static get styles() {
    return _litElement.css`
      :host {
        display: inline-block;
      }

      .led-container {
        display: flex;
        flex-direction: column;
        width: 40px;
      }

      .led-label {
        font-size: 10px;
        text-align: center;
        color: gray;
        position: relative;
        line-height: 1;
        top: -8px;
      }
    `;
  }

  render() {
    const {
      color,
      lightColor
    } = this;
    const lightColorActual = lightColor || lightColors[color] || '#ff8080';
    const opacity = this.brightness ? 0.3 + this.brightness * 0.7 : 0;
    const lightOn = this.value && this.brightness > Number.EPSILON;
    return _litElement.html`
      <div class="led-container">
        <svg
          width="40"
          height="50"
          version="1.2"
          viewBox="-10 -5 35.456 39.618"
          xmlns="http://www.w3.org/2000/svg"
        >
          <filter id="light1" x="-0.8" y="-0.8" height="2.2" width="2.8">
            <feGaussianBlur stdDeviation="2" />
          </filter>
          <filter id="light2" x="-0.8" y="-0.8" height="2.2" width="2.8">
            <feGaussianBlur stdDeviation="4" />
          </filter>
          <rect x="3.451" y="19.379" width="2.1514" height="9.8273" fill="#8c8c8c" />
          <path
            d="m12.608 29.618c0-1.1736-0.86844-2.5132-1.8916-3.4024-0.41616-0.3672-1.1995-1.0015-1.1995-1.4249v-5.4706h-2.1614v5.7802c0 1.0584 0.94752 1.8785 1.9462 2.7482 0.44424 0.37584 1.3486 1.2496 1.3486 1.7694"
            fill="#8c8c8c"
          />
          <path
            d="m14.173 13.001v-5.9126c0-3.9132-3.168-7.0884-7.0855-7.0884-3.9125 0-7.0877 3.1694-7.0877 7.0884v13.649c1.4738 1.651 4.0968 2.7526 7.0877 2.7526 4.6195 0 8.3686-2.6179 8.3686-5.8594v-1.5235c-7.4e-4 -1.1426-0.47444-2.2039-1.283-3.1061z"
            opacity=".3"
          />
          <path
            d="m14.173 13.001v-5.9126c0-3.9132-3.168-7.0884-7.0855-7.0884-3.9125 0-7.0877 3.1694-7.0877 7.0884v13.649c1.4738 1.651 4.0968 2.7526 7.0877 2.7526 4.6195 0 8.3686-2.6179 8.3686-5.8594v-1.5235c-7.4e-4 -1.1426-0.47444-2.2039-1.283-3.1061z"
            fill="#e6e6e6"
            opacity=".5"
          />
          <path
            d="m14.173 13.001v3.1054c0 2.7389-3.1658 4.9651-7.0855 4.9651-3.9125 2e-5 -7.0877-2.219-7.0877-4.9651v4.6296c1.4738 1.6517 4.0968 2.7526 7.0877 2.7526 4.6195 0 8.3686-2.6179 8.3686-5.8586l-4e-5 -1.5235c-7e-4 -1.1419-0.4744-2.2032-1.283-3.1054z"
            fill="#d1d1d1"
            opacity=".9"
          />
          <g>
            <path
              d="m14.173 13.001v3.1054c0 2.7389-3.1658 4.9651-7.0855 4.9651-3.9125 2e-5 -7.0877-2.219-7.0877-4.9651v4.6296c1.4738 1.6517 4.0968 2.7526 7.0877 2.7526 4.6195 0 8.3686-2.6179 8.3686-5.8586l-4e-5 -1.5235c-7e-4 -1.1419-0.4744-2.2032-1.283-3.1054z"
              opacity=".7"
            />
            <path
              d="m14.173 13.001v3.1054c0 2.7389-3.1658 4.9651-7.0855 4.9651-3.9125 2e-5 -7.0877-2.219-7.0877-4.9651v3.1054c1.4738 1.6502 4.0968 2.7526 7.0877 2.7526 4.6195 0 8.3686-2.6179 8.3686-5.8586-7.4e-4 -1.1412-0.47444-2.2025-1.283-3.1047z"
              opacity=".25"
            />
            <ellipse cx="7.0877" cy="16.106" rx="7.087" ry="4.9608" opacity=".25" />
          </g>
          <polygon
            points="2.2032 16.107 3.1961 16.107 3.1961 13.095 6.0156 13.095 10.012 8.8049 3.407 8.8049 2.2032 9.648"
            fill="#666666"
          />
          <polygon
            points="11.215 9.0338 7.4117 13.095 11.06 13.095 11.06 16.107 11.974 16.107 11.974 8.5241 10.778 8.5241"
            fill="#666666"
          />
          <path
            d="m14.173 13.001v-5.9126c0-3.9132-3.168-7.0884-7.0855-7.0884-3.9125 0-7.0877 3.1694-7.0877 7.0884v13.649c1.4738 1.651 4.0968 2.7526 7.0877 2.7526 4.6195 0 8.3686-2.6179 8.3686-5.8594v-1.5235c-7.4e-4 -1.1426-0.47444-2.2039-1.283-3.1061z"
            fill="${color}"
            opacity=".65"
          />
          <g fill="#ffffff">
            <path
              d="m10.388 3.7541 1.4364-0.2736c-0.84168-1.1318-2.0822-1.9577-3.5417-2.2385l0.25416 1.0807c0.76388 0.27072 1.4068 0.78048 1.8511 1.4314z"
              opacity=".5"
            />
            <path
              d="m0.76824 19.926v1.5199c0.64872 0.5292 1.4335 0.97632 2.3076 1.3169v-1.525c-0.8784-0.33624-1.6567-0.78194-2.3076-1.3118z"
              opacity=".5"
            />
            <path
              d="m11.073 20.21c-0.2556 0.1224-0.52992 0.22968-0.80568 0.32976-0.05832 0.01944-0.11736 0.04032-0.17784 0.05832-0.56376 0.17928-1.1614 0.31896-1.795 0.39456-0.07488 0.0094-0.1512 0.01872-0.22464 0.01944-0.3204 0.03024-0.64368 0.05832-0.97056 0.05832-0.14832 0-0.30744-0.01512-0.4716-0.02376-1.2002-0.05688-2.3306-0.31464-3.2976-0.73944l-2e-5 -8.3895v-4.8254c0-1.471 0.84816-2.7295 2.0736-3.3494l-0.02232-0.05328-1.2478-1.512c-1.6697 1.003-2.79 2.8224-2.79 4.9118v11.905c-0.04968-0.04968-0.30816-0.30888-0.48024-0.52992l-0.30744 0.6876c1.4011 1.4818 3.8088 2.4617 6.5426 2.4617 1.6798 0 3.2371-0.37368 4.5115-1.0022l-0.52704-0.40896-0.01006 0.0072z"
              opacity=".5"
            />
          </g>
          <g class="light" style="display: ${lightOn ? '' : 'none'}">
            <ellipse
              cx="8"
              cy="10"
              rx="10"
              ry="10"
              fill="${lightColorActual}"
              filter="url(#light2)"
              style="opacity: ${opacity}"
            ></ellipse>
            <ellipse cx="8" cy="10" rx="2" ry="2" fill="white" filter="url(#light1)"></ellipse>
            <ellipse
              cx="8"
              cy="10"
              rx="3"
              ry="3"
              fill="white"
              filter="url(#light1)"
              style="opacity: ${opacity}"
            ></ellipse>
          </g>
        </svg>
        <span class="led-label">${this.label}</span>
      </div>
    `;
  }

};
exports.LEDElement = LEDElement;

__decorate([(0, _litElement.property)()], LEDElement.prototype, "value", void 0);

__decorate([(0, _litElement.property)()], LEDElement.prototype, "brightness", void 0);

__decorate([(0, _litElement.property)()], LEDElement.prototype, "color", void 0);

__decorate([(0, _litElement.property)()], LEDElement.prototype, "lightColor", void 0);

__decorate([(0, _litElement.property)()], LEDElement.prototype, "label", void 0);

exports.LEDElement = LEDElement = __decorate([(0, _litElement.customElement)('wokwi-led')], LEDElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/neopixel-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.NeoPixelElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

let NeoPixelElement = class NeoPixelElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.r = 0;
    this.g = 0;
    this.b = 0;
  }

  render() {
    const {
      r,
      g,
      b
    } = this;

    const spotOpacity = value => value > 0.001 ? 0.7 + value * 0.3 : 0;

    const maxOpacity = Math.max(r, g, b);
    const minOpacity = Math.min(r, g, b);
    const opacityDelta = maxOpacity - minOpacity;
    const multiplier = Math.max(1, 2 - opacityDelta * 20);
    const glowBase = 0.1 + Math.max(maxOpacity * 2 - opacityDelta * 5, 0);

    const glowColor = value => value > 0.005 ? 0.1 + value * 0.9 : 0;

    const glowOpacity = value => value > 0.005 ? glowBase + value * (1 - glowBase) : 0;

    const cssVal = value => maxOpacity ? Math.floor(Math.min(glowColor(value / maxOpacity) * multiplier, 1) * 255) : 255;

    const cssColor = `rgb(${cssVal(r)}, ${cssVal(g)}, ${cssVal(b)})`;
    const bkgWhite = 242 - (maxOpacity > 0.1 && opacityDelta < 0.2 ? Math.floor(maxOpacity * 50 * (1 - opacityDelta / 0.2)) : 0);
    const background = `rgb(${bkgWhite}, ${bkgWhite}, ${bkgWhite})`;
    return _litElement.html`
      <svg
        width="5.6631mm"
        height="5mm"
        version="1.1"
        viewBox="0 0 5.6631 5"
        xmlns="http://www.w3.org/2000/svg"
      >
        <filter id="light1" x="-0.8" y="-0.8" height="2.8" width="2.8">
          <feGaussianBlur stdDeviation="${Math.max(0.1, maxOpacity)}" />
        </filter>
        <filter id="light2" x="-0.8" y="-0.8" height="2.2" width="2.8">
          <feGaussianBlur stdDeviation="0.5" />
        </filter>
        <rect x=".33308" y="0" width="5" height="5" fill="${background}" />
        <rect x=".016709" y=".4279" width=".35114" height=".9" fill="#eaeaea" />
        <rect x="0" y="3.6518" width=".35114" height=".9" fill="#eaeaea" />
        <rect x="5.312" y="3.6351" width=".35114" height=".9" fill="#eaeaea" />
        <rect x="5.312" y=".3945" width=".35114" height=".9" fill="#eaeaea" />
        <circle cx="2.8331" cy="2.5" r="2.1" fill="#ddd" />
        <circle cx="2.8331" cy="2.5" r="1.7325" fill="#e6e6e6" />
        <g fill="#bfbfbf">
          <path
            d="m4.3488 3.3308s-0.0889-0.087-0.0889-0.1341c0-0.047-6e-3 -1.1533-6e-3 -1.1533s-0.0591-0.1772-0.2008-0.1772c-0.14174 0-0.81501 0.012-0.81501 0.012s-0.24805 0.024-0.23624 0.3071c0.0118 0.2835 0.032 2.0345 0.032 2.0345 0.54707-0.046 1.0487-0.3494 1.3146-0.8888z"
          />
          <path
            d="m4.34 1.6405h-1.0805s-0.24325 0.019-0.26204-0.2423l6e-3 -0.6241c0.57782 0.075 1.0332 0.3696 1.3366 0.8706z"
          />
          <path
            d="m2.7778 2.6103-0.17127 0.124-0.8091-0.012c-0.17122-0.019-0.17062-0.2078-0.17062-0.2078-1e-3 -0.3746 1e-3 -0.2831-9e-3 -0.8122l-0.31248-0.018s0.43453-0.9216 1.4786-0.9174c-1.1e-4 0.6144-4e-3 1.2289-6e-3 1.8434z"
          />
          <path
            d="m2.7808 3.0828-0.0915-0.095h-0.96857l-0.0915 0.1447-3e-3 0.1127c0 0.065-0.12108 0.08-0.12108 0.08h-0.20909c0.55906 0.9376 1.4867 0.9155 1.4867 0.9155 1e-3 -0.3845-2e-3 -0.7692-2e-3 -1.1537z"
          />
        </g>
        <path
          d="m4.053 1.8619c-0.14174 0-0.81494 0.013-0.81494 0.013s-0.24797 0.024-0.23616 0.3084c3e-3 0.077 5e-3 0.3235 9e-3 0.5514h1.247c-2e-3 -0.33-4e-3 -0.6942-4e-3 -0.6942s-0.0593-0.1781-0.20102-0.1781z"
          fill="#666"
        />
        <ellipse
          cx="2.5"
          cy="2.3"
          rx="0.3"
          ry="0.3"
          fill="red"
          opacity=${spotOpacity(r)}
          filter="url(#light1)"
        ></ellipse>
        <ellipse
          cx="3.5"
          cy="3.2"
          rx="0.3"
          ry="0.3"
          fill="green"
          opacity=${spotOpacity(g)}
          filter="url(#light1)"
        ></ellipse>
        <ellipse
          cx="3.3"
          cy="1.45"
          rx="0.3"
          ry="0.3"
          fill="blue"
          opacity=${spotOpacity(b)}
          filter="url(#light1)"
        ></ellipse>
        <ellipse
          cx="3"
          cy="2.5"
          rx="2.2"
          ry="2.2"
          opacity="${glowOpacity(maxOpacity)}"
          fill="${cssColor}"
          filter="url(#light2)"
        ></ellipse>
      </svg>
    `;
  }

};
exports.NeoPixelElement = NeoPixelElement;

__decorate([(0, _litElement.property)()], NeoPixelElement.prototype, "r", void 0);

__decorate([(0, _litElement.property)()], NeoPixelElement.prototype, "g", void 0);

__decorate([(0, _litElement.property)()], NeoPixelElement.prototype, "b", void 0);

exports.NeoPixelElement = NeoPixelElement = __decorate([(0, _litElement.customElement)('wokwi-neopixel')], NeoPixelElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/pushbutton-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.PushbuttonElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

const SPACE_KEY = 32;
let PushbuttonElement = class PushbuttonElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.color = 'red';
    this.pressed = false;
  }

  static get styles() {
    return _litElement.css`
      button {
        border: none;
        background: none;
        padding: 0;
        margin: 0;
        text-decoration: none;
        -webkit-appearance: none;
        -moz-appearance: none;
      }

      button:active .button-circle {
        fill: url(#grad-down);
      }

      .clickable-element {
        cursor: pointer;
      }
    `;
  }

  render() {
    const {
      color
    } = this;
    return _litElement.html`
      <button
        aria-label="${color} pushbutton"
        @mousedown=${this.down}
        @mouseup=${this.up}
        @touchstart=${this.down}
        @touchend=${this.up}
        @keydown=${e => e.keyCode === SPACE_KEY && this.down()}
        @keyup=${e => e.keyCode === SPACE_KEY && this.up()}
      >
        <svg
          width="18mm"
          height="12mm"
          version="1.1"
          viewBox="-3 0 18 12"
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
        >
          <defs>
            <linearGradient id="grad-up" x1="0" x2="1" y1="0" y2="1">
              <stop stop-color="#ffffff" offset="0" />
              <stop stop-color="${color}" offset="0.3" />
              <stop stop-color="${color}" offset="0.5" />
              <stop offset="1" />
            </linearGradient>
            <linearGradient
              id="grad-down"
              xlink:href="#grad-up"
              gradientTransform="rotate(180,0.5,0.5)"
            ></linearGradient>
          </defs>
          <rect x="0" y="0" width="12" height="12" rx=".44" ry=".44" fill="#464646" />
          <rect x=".75" y=".75" width="10.5" height="10.5" rx=".211" ry=".211" fill="#eaeaea" />
          <g fill="#1b1b1">
            <circle cx="1.767" cy="1.7916" r=".37" />
            <circle cx="10.161" cy="1.7916" r=".37" />
            <circle cx="10.161" cy="10.197" r=".37" />
            <circle cx="1.767" cy="10.197" r=".37" />
          </g>
          <g fill="#eaeaea">
            <path
              d="m-0.3538 1.4672c-0.058299 0-0.10523 0.0469-0.10523 0.10522v0.38698h-2.1504c-0.1166 0-0.21045 0.0938-0.21045 0.21045v0.50721c0 0.1166 0.093855 0.21045 0.21045 0.21045h2.1504v0.40101c0 0.0583 0.046928 0.10528 0.10523 0.10528h0.35723v-1.9266z"
            />
            <path
              d="m-0.35376 8.6067c-0.058299 0-0.10523 0.0469-0.10523 0.10523v0.38697h-2.1504c-0.1166 0-0.21045 0.0939-0.21045 0.21045v0.50721c0 0.1166 0.093855 0.21046 0.21045 0.21046h2.1504v0.401c0 0.0583 0.046928 0.10528 0.10523 0.10528h0.35723v-1.9266z"
            />
            <path
              d="m12.354 1.4672c0.0583 0 0.10522 0.0469 0.10523 0.10522v0.38698h2.1504c0.1166 0 0.21045 0.0938 0.21045 0.21045v0.50721c0 0.1166-0.09385 0.21045-0.21045 0.21045h-2.1504v0.40101c0 0.0583-0.04693 0.10528-0.10523 0.10528h-0.35723v-1.9266z"
            />
            <path
              d="m12.354 8.6067c0.0583 0 0.10523 0.0469 0.10523 0.10522v0.38698h2.1504c0.1166 0 0.21045 0.0938 0.21045 0.21045v0.50721c0 0.1166-0.09386 0.21045-0.21045 0.21045h-2.1504v0.40101c0 0.0583-0.04693 0.10528-0.10523 0.10528h-0.35723v-1.9266z"
            />
          </g>
          <g class="clickable-element">
            <circle class="button-circle" cx="6" cy="6" r="3.822" fill="url(#grad-up)" />
            <circle
              cx="6"
              cy="6"
              r="2.9"
              fill="${color}"
              stroke="#2f2f2f"
              stroke-opacity=".47"
              stroke-width=".08"
            />
          </g>
        </svg>
      </button>
    `;
  }

  down() {
    if (!this.pressed) {
      this.pressed = true;
      this.dispatchEvent(new Event('button-press'));
    }
  }

  up() {
    if (this.pressed) {
      this.pressed = false;
      this.dispatchEvent(new Event('button-release'));
    }
  }

};
exports.PushbuttonElement = PushbuttonElement;

__decorate([(0, _litElement.property)()], PushbuttonElement.prototype, "color", void 0);

__decorate([(0, _litElement.property)()], PushbuttonElement.prototype, "pressed", void 0);

exports.PushbuttonElement = PushbuttonElement = __decorate([(0, _litElement.customElement)('wokwi-pushbutton')], PushbuttonElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/resistor-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ResistorElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

const bandColors = {
  [-2]: 'silver',
  [-1]: '#c4a000',
  0: 'black',
  1: 'brown',
  2: 'red',
  3: 'orange',
  4: 'yellow',
  5: 'green',
  6: 'blue',
  7: 'violet',
  8: 'gray',
  9: 'white'
};
/**
 * Renders an axial-lead resistor with 4 color bands.
 */

let ResistorElement = class ResistorElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    /**
     * Resitance value, in ohms. The value is reflected in the color of the bands, according to
     * standard [electronic color code](https://en.wikipedia.org/wiki/Electronic_color_code#Resistors).
     */

    this.value = '1000';
  }

  breakValue(value) {
    const exponent = value >= 1e10 ? 9 : value >= 1e9 ? 8 : value >= 1e8 ? 7 : value >= 1e7 ? 6 : value >= 1e6 ? 5 : value >= 1e5 ? 4 : value >= 1e4 ? 3 : value >= 1e3 ? 2 : value >= 1e2 ? 1 : value >= 1e1 ? 0 : value >= 1 ? -1 : -2;
    const base = Math.round(value / 10 ** exponent);

    if (value === 0) {
      return [0, 0];
    }

    if (exponent < 0 && base % 10 === 0) {
      return [base / 10, exponent + 1];
    }

    return [Math.round(base % 100), exponent];
  }

  render() {
    const {
      value
    } = this;
    const numValue = parseFloat(value);
    const [base, exponent] = this.breakValue(numValue);
    const band1Color = bandColors[Math.floor(base / 10)];
    const band2Color = bandColors[base % 10];
    const band3Color = bandColors[exponent];
    return _litElement.html`
      <svg
        width="15.645mm"
        height="3mm"
        version="1.1"
        viewBox="0 0 15.645 3"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
      >
        <defs>
          <linearGradient
            id="a"
            x2="0"
            y1="22.332"
            y2="38.348"
            gradientTransform="matrix(.14479 0 0 .14479 -23.155 -4.0573)"
            gradientUnits="userSpaceOnUse"
            spreadMethod="reflect"
          >
            <stop stop-color="#323232" offset="0" />
            <stop stop-color="#fff" stop-opacity=".42268" offset="1" />
          </linearGradient>
        </defs>
        <rect y="1.1759" width="15.645" height=".63826" fill="#eaeaea" />
        <g stroke-width=".14479">
          <path
            d="m4.6918 0c-1.0586 0-1.9185 0.67468-1.9185 1.5022 0 0.82756 0.85995 1.4978 1.9185 1.4978 0.4241 0 0.81356-0.11167 1.1312-0.29411h4.0949c0.31802 0.18313 0.71075 0.29411 1.1357 0.29411 1.0586 0 1.9185-0.67015 1.9185-1.4978 0-0.8276-0.85995-1.5022-1.9185-1.5022-0.42499 0-0.81773 0.11098-1.1357 0.29411h-4.0949c-0.31765-0.18244-0.7071-0.29411-1.1312-0.29411z"
            fill="#d5b597"
          />
          <path
            d="m4.6918 0c-1.0586 0-1.9185 0.67468-1.9185 1.5022 0 0.82756 0.85995 1.4978 1.9185 1.4978 0.4241 0 0.81356-0.11167 1.1312-0.29411h4.0949c0.31802 0.18313 0.71075 0.29411 1.1357 0.29411 1.0586 0 1.9185-0.67015 1.9185-1.4978 0-0.8276-0.85995-1.5022-1.9185-1.5022-0.42499 0-0.81773 0.11098-1.1357 0.29411h-4.0949c-0.31765-0.18244-0.7071-0.29411-1.1312-0.29411z"
            fill="url(#a)"
            opacity=".44886"
          />
          <path
            d="m4.6917 0c-0.10922 0-0.21558 0.00884-0.31985 0.022624v2.955c0.10426 0.013705 0.21063 0.02234 0.31985 0.02234 0.15603 0 0.3074-0.015363 0.4522-0.043551v-2.9129c-0.1448-0.028193-0.29617-0.043551-0.4522-0.043552z"
            fill="${band1Color}"
          />
          <path d="m6.4482 0.29411v2.4117h0.77205v-2.4117z" fill="${band2Color}" />
          <path d="m8.5245 0.29411v2.4117h0.77205v-2.4117z" fill="${band3Color}" />
          <path
            d="m11.054 0c-0.15608 0-0.30749 0.015253-0.45277 0.043268v2.9134c0.14527 0.028012 0.29669 0.043268 0.45277 0.043268 0.10912 0 0.21539-0.00867 0.31957-0.02234v-2.955c-0.10418-0.013767-0.21044-0.022624-0.31957-0.022624z"
            fill="#c4a000"
          />
        </g>
      </svg>
    `;
  }

};
exports.ResistorElement = ResistorElement;

__decorate([(0, _litElement.property)()], ResistorElement.prototype, "value", void 0);

exports.ResistorElement = ResistorElement = __decorate([(0, _litElement.customElement)('wokwi-resistor')], ResistorElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/membrane-keypad-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.MembraneKeypadElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

const SPACE_KEY = 32;

function isNumeric(text) {
  return !isNaN(parseFloat(text));
}

let MembraneKeypadElement = class MembraneKeypadElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.threeColumns = false;
    this.pressedKeys = new Set();
  }

  renderKey(text, x, y) {
    const keyClass = isNumeric(text) ? 'blue-key' : 'red-key';
    const keyName = text.toUpperCase();
    return _litElement.svg`<g
      transform="translate(${x} ${y})"
      tabindex="0"
      class=${keyClass}
      data-key-name=${keyName}
      @blur=${e => {
      this.up(text, e.currentTarget);
    }}
      @mousedown=${() => this.down(text)}
      @mouseup=${() => this.up(text)}
      @touchstart=${() => this.down(text)}
      @touchend=${() => this.up(text)}
      @keydown=${e => e.keyCode === SPACE_KEY && this.down(text, e.currentTarget)}
      @keyup=${e => e.keyCode === SPACE_KEY && this.up(text, e.currentTarget)}
    >
      <use xlink:href="#key" />
      <text x="5.6" y="8.1">${text}</text>
    </g>`;
  }

  render() {
    const fourColumns = !this.threeColumns;
    const columnWidth = 15;
    const width = fourColumns ? 70.336 : 70.336 - columnWidth;
    return _litElement.html`
      <style>
        text {
          fill: #dfe2e5;
          user-select: none;
        }

        g[tabindex] {
          cursor: pointer;
        }

        g[tabindex]:focus,
        g[tabindex]:active {
          stroke: white;
          outline: none;
        }

        .blue-key:focus,
        .red-key:focus {
          filter: url(#shadow);
        }

        .blue-key:active,
        .blue-key.pressed {
          fill: #4e50d7;
        }

        .red-key:active,
        .red-key.pressed {
          fill: #ab040b;
        }

        g[tabindex]:focus text {
          stroke: none;
        }

        g[tabindex]:active text,
        .blue-key.pressed text,
        .red-key.pressed text {
          fill: white;
          stroke: none;
        }
      </style>

      <svg
        width="${width}mm"
        height="76mm"
        version="1.1"
        viewBox="0 0 ${width} 76"
        font-family="sans-serif"
        font-size="8.2px"
        text-anchor="middle"
        xmlns="http://www.w3.org/2000/svg"
        @keydown=${e => this.keyStrokeDown(e.key)}
        @keyup=${e => this.keyStrokeUp(e.key)}
      >
        <defs>
          <rect
            id="key"
            width="11.2"
            height="11"
            rx="1.4"
            ry="1.4"
            stroke="#b1b5b9"
            stroke-width=".75"
          />

          <filter id="shadow">
            <feDropShadow dx="0" dy="0" stdDeviation="0.5" flood-color="#ffff99" />
          </filter>
        </defs>

        <!-- Keypad outline -->
        <rect x="0" y="0" width="${width}" height="76" rx="5" ry="5" fill="#454449" />
        <rect
          x="2.78"
          y="3.25"
          width="${fourColumns ? 65 : 65 - columnWidth}"
          height="68.6"
          rx="3.5"
          ry="3.5"
          fill="none"
          stroke="#b1b5b9"
          stroke-width="1"
        />

        <!-- Blue keys -->
        <g fill="#4e90d7">
          <g>${this.renderKey('1', 7, 10.7)}</g>
          <g>${this.renderKey('2', 22, 10.7)}</g>
          <g>${this.renderKey('3', 37, 10.7)}</g>
          <g>${this.renderKey('4', 7, 25)}</g>
          <g>${this.renderKey('5', 22, 25)}</g>
          <g>${this.renderKey('6', 37, 25)}</g>
          <g>${this.renderKey('7', 7, 39.3)}</g>
          <g>${this.renderKey('8', 22, 39.3)}</g>
          <g>${this.renderKey('9', 37, 39.3)}</g>
          <g>${this.renderKey('0', 22, 53.6)}</g>
        </g>

        <!-- Red keys -->
        <g fill="#e94541">
          <g>${this.renderKey('*', 7, 53.6)}</g>
          <g>${this.renderKey('#', 37, 53.6)}</g>
          ${fourColumns && _litElement.svg`
              <g>${this.renderKey('A', 52, 10.7)}</g>
              <g>${this.renderKey('B', 52, 25)}</g>
              <g>${this.renderKey('C', 52, 39.3)}</g>
              <g>${this.renderKey('D', 52, 53.6)}</g>
          `}
        </g>
      </svg>
    `;
  }

  down(key, element) {
    if (!this.pressedKeys.has(key)) {
      if (element) {
        element.classList.add('pressed');
      }

      this.pressedKeys.add(key);
      this.dispatchEvent(new CustomEvent('button-press', {
        detail: {
          key
        }
      }));
    }
  }

  up(key, element) {
    if (this.pressedKeys.has(key)) {
      if (element) {
        element.classList.remove('pressed');
      }

      this.pressedKeys.delete(key);
      this.dispatchEvent(new CustomEvent('button-release', {
        detail: {
          key
        }
      }));
    }
  }

  keyStrokeDown(key) {
    var _a;

    const text = key.toUpperCase();
    const selectedKey = (_a = this.shadowRoot) === null || _a === void 0 ? void 0 : _a.querySelector(`[data-key-name="${text}"]`);

    if (selectedKey) {
      this.down(text, selectedKey);
    }
  }

  keyStrokeUp(key) {
    var _a, _b, _c;

    const text = key.toUpperCase();
    const selectedKey = (_a = this.shadowRoot) === null || _a === void 0 ? void 0 : _a.querySelector(`[data-key-name="${text}"]`);
    const pressedKeys = (_b = this.shadowRoot) === null || _b === void 0 ? void 0 : _b.querySelectorAll('.pressed');

    if (key === 'Shift') {
      (_c = pressedKeys) === null || _c === void 0 ? void 0 : _c.forEach(pressedKey => {
        const pressedText = pressedKey.dataset.keyName;
        this.up(pressedText, pressedKey);
      });
    }

    if (selectedKey) {
      this.up(text, selectedKey);
    }
  }

};
exports.MembraneKeypadElement = MembraneKeypadElement;

__decorate([(0, _litElement.property)()], MembraneKeypadElement.prototype, "threeColumns", void 0);

exports.MembraneKeypadElement = MembraneKeypadElement = __decorate([(0, _litElement.customElement)('wokwi-membrane-keypad')], MembraneKeypadElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/lit-html/directives/style-map.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.styleMap = void 0;

var _litHtml = require("../lit-html.js");

/**
 * @license
 * Copyright (c) 2018 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

/**
 * Stores the StyleInfo object applied to a given AttributePart.
 * Used to unset existing values when a new StyleInfo object is applied.
 */
const previousStylePropertyCache = new WeakMap();
/**
 * A directive that applies CSS properties to an element.
 *
 * `styleMap` can only be used in the `style` attribute and must be the only
 * expression in the attribute. It takes the property names in the `styleInfo`
 * object and adds the property values as CSS properties. Property names with
 * dashes (`-`) are assumed to be valid CSS property names and set on the
 * element's style object using `setProperty()`. Names without dashes are
 * assumed to be camelCased JavaScript property names and set on the element's
 * style object using property assignment, allowing the style object to
 * translate JavaScript-style names to CSS property names.
 *
 * For example `styleMap({backgroundColor: 'red', 'border-top': '5px', '--size':
 * '0'})` sets the `background-color`, `border-top` and `--size` properties.
 *
 * @param styleInfo {StyleInfo}
 */

const styleMap = (0, _litHtml.directive)(styleInfo => part => {
  if (!(part instanceof _litHtml.AttributePart) || part instanceof _litHtml.PropertyPart || part.committer.name !== 'style' || part.committer.parts.length > 1) {
    throw new Error('The `styleMap` directive must be used in the style attribute ' + 'and must be the only part in the attribute.');
  }

  const {
    committer
  } = part;
  const {
    style
  } = committer.element;
  let previousStyleProperties = previousStylePropertyCache.get(part);

  if (previousStyleProperties === undefined) {
    // Write static styles once
    style.cssText = committer.strings.join(' ');
    previousStylePropertyCache.set(part, previousStyleProperties = new Set());
  } // Remove old properties that no longer exist in styleInfo
  // We use forEach() instead of for-of so that re don't require down-level
  // iteration.


  previousStyleProperties.forEach(name => {
    if (!(name in styleInfo)) {
      previousStyleProperties.delete(name);

      if (name.indexOf('-') === -1) {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        style[name] = null;
      } else {
        style.removeProperty(name);
      }
    }
  }); // Add or update properties

  for (const name in styleInfo) {
    previousStyleProperties.add(name);

    if (name.indexOf('-') === -1) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      style[name] = styleInfo[name];
    } else {
      style.setProperty(name, styleInfo[name]);
    }
  }
});
exports.styleMap = styleMap;
},{"../lit-html.js":"../../node_modules/lit-html/lit-html.js"}],"../../node_modules/@wokwi/elements/dist/esm/potentiometer-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.PotentiometerElement = void 0;

var _litElement = require("lit-element");

var _styleMap = require("lit-html/directives/style-map");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/** The potentiometer SVG is taken from https://freesvg.org/potentiometer and some of the
    functions are taken from https://github.com/vitaliy-bobrov/js-rocks knob component */
let PotentiometerElement = class PotentiometerElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.min = 0;
    this.max = 100;
    this.value = 0;
    this.step = 1;
    this.startDegree = -135;
    this.endDegree = 135;
    this.center = {
      x: 0,
      y: 0
    };
    this.pressed = false;
  }

  static get styles() {
    return _litElement.css`
      #rotating {
        transform-origin: 10px 8px;
        transform: rotate(var(--knob-angle, 0deg));
      }

      svg text {
        font-size: 1px;
        line-height: 1.25;
        letter-spacing: 0px;
        word-spacing: 0px;
        fill: #ffffff;
      }
      .hide-input {
        position: absolute;
        clip: rect(0 0 0 0);
        width: 1px;
        height: 1px;
        margin: -1px;
      }
      input:focus + svg #knob {
        stroke: #ccdae3;
        filter: url(#outline);
      }
    `;
  }

  clamp(min, max, value) {
    return Math.min(Math.max(value, min), max);
  }

  mapToMinMax(value, min, max) {
    return value * (max - min) + min;
  }

  percentFromMinMax(value, min, max) {
    return (value - min) / (max - min);
  }

  render() {
    const percent = this.clamp(0, 1, this.percentFromMinMax(this.value, this.min, this.max));
    const knobDeg = (this.endDegree - this.startDegree) * percent + this.startDegree;
    return _litElement.html`
      <input
        tabindex="0"
        type="range"
        class="hide-input"
        max="${this.max}"
        min="${this.min}"
        value="${this.value}"
        step="${this.step}"
        aria-valuemin="${this.min}"
        aria-valuenow="${this.value}"
        @input="${this.onValueChange}"
      />
      <svg
        role="slider"
        width="20mm"
        height="20mm"
        version="1.1"
        viewBox="0 0 20 20"
        xmlns="http://www.w3.org/2000/svg"
        @click="${this.focusInput}"
        @mousedown=${this.down}
        @mousemove=${this.move}
        @mouseup=${this.up}
        @touchstart=${this.down}
        @touchmove=${this.move}
        @touchend=${this.up}
        style=${(0, _styleMap.styleMap)({
      '--knob-angle': knobDeg + 'deg'
    })}
      >
        <defs>
          <filter id="outline">
            <feDropShadow id="glow" dx="0" dy="0" stdDeviation="0.5" flood-color="cyan" />
          </filter>
        </defs>
        <rect
          x=".15"
          y=".15"
          width="19.5"
          height="19.5"
          ry="1.23"
          fill="#045881"
          stroke="#045881"
          stroke-width=".30"
        />
        <rect x="5.4" y=".70" width="9.1" height="1.9" fill="#ccdae3" stroke-width=".15" />
        <ellipse
          id="knob"
          cx="9.91"
          cy="8.18"
          rx="7.27"
          ry="7.43"
          fill="#e4e8eb"
          stroke-width=".15"
        />
        <rect
          x="6.6"
          y="17"
          width="6.5"
          height="2"
          fill-opacity="0"
          stroke="#fff"
          stroke-width=".30"
        />
        <g stroke-width=".15">
          <text x="6.21" y="16.6">GND</text>
          <text x="8.75" y="16.63">VCC</text>
          <text x="11.25" y="16.59">SIG</text>
        </g>
        <g fill="#fff" stroke-width=".15">
          <ellipse cx="1.68" cy="1.81" rx=".99" ry=".96" />
          <ellipse cx="1.48" cy="18.37" rx=".99" ry=".96" />
          <ellipse cx="17.97" cy="18.47" rx=".99" ry=".96" />
          <ellipse cx="18.07" cy="1.91" rx=".99" ry=".96" />
        </g>
        <g fill="#b3b1b0" stroke-width=".15">
          <ellipse cx="7.68" cy="18" rx=".61" ry=".63" />
          <ellipse cx="9.75" cy="18" rx=".61" ry=".63" />
          <ellipse cx="11.87" cy="18" rx=".61" ry=".63" />
        </g>
        <ellipse cx="9.95" cy="8.06" rx="6.60" ry="6.58" fill="#c3c2c3" stroke-width=".15" />
        <rect id="rotating" x="10" y="2" width=".42" height="3.1" stroke-width=".15" />
      </svg>
    `;
  }

  focusInput() {
    var _a, _b;

    const inputEl = (_a = this.shadowRoot) === null || _a === void 0 ? void 0 : _a.querySelector('.hide-input');
    (_b = inputEl) === null || _b === void 0 ? void 0 : _b.focus();
  }

  onValueChange(event) {
    const target = event.target;
    this.updateValue(parseFloat(target.value));
  }

  down(event) {
    if (event.button === 0 || window.navigator.maxTouchPoints) {
      this.pressed = true;
      this.updatePotentiometerPosition(event);
    }
  }

  move(event) {
    const {
      pressed
    } = this;

    if (pressed) {
      this.rotateHandler(event);
    }
  }

  up() {
    this.pressed = false;
  }

  updatePotentiometerPosition(event) {
    var _a, _b;

    event.stopPropagation();
    event.preventDefault();
    const potentiometerRect = (_b = (_a = this.shadowRoot) === null || _a === void 0 ? void 0 : _a.querySelector('#knob')) === null || _b === void 0 ? void 0 : _b.getBoundingClientRect();

    if (potentiometerRect) {
      this.center = {
        x: window.scrollX + potentiometerRect.left + potentiometerRect.width / 2,
        y: window.scrollY + potentiometerRect.top + potentiometerRect.height / 2
      };
    }
  }

  rotateHandler(event) {
    event.stopPropagation();
    event.preventDefault();
    const isTouch = event.type === 'touchmove';
    const pageX = isTouch ? event.touches[0].pageX : event.pageX;
    const pageY = isTouch ? event.touches[0].pageY : event.pageY;
    const x = this.center.x - pageX;
    const y = this.center.y - pageY;
    let deg = Math.round(Math.atan2(y, x) * 180 / Math.PI);

    if (deg < 0) {
      deg += 360;
    }

    deg -= 90;

    if (x > 0 && y <= 0) {
      deg -= 360;
    }

    deg = this.clamp(this.startDegree, this.endDegree, deg);
    const percent = this.percentFromMinMax(deg, this.startDegree, this.endDegree);
    const value = this.mapToMinMax(percent, this.min, this.max);
    this.updateValue(value);
  }

  updateValue(value) {
    const clamped = this.clamp(this.min, this.max, value);
    const updated = Math.round(clamped / this.step) * this.step;
    this.value = Math.round(updated * 100) / 100;
    this.dispatchEvent(new InputEvent('input', {
      detail: this.value
    }));
  }

};
exports.PotentiometerElement = PotentiometerElement;

__decorate([(0, _litElement.property)()], PotentiometerElement.prototype, "min", void 0);

__decorate([(0, _litElement.property)()], PotentiometerElement.prototype, "max", void 0);

__decorate([(0, _litElement.property)()], PotentiometerElement.prototype, "value", void 0);

__decorate([(0, _litElement.property)()], PotentiometerElement.prototype, "step", void 0);

__decorate([(0, _litElement.property)()], PotentiometerElement.prototype, "startDegree", void 0);

__decorate([(0, _litElement.property)()], PotentiometerElement.prototype, "endDegree", void 0);

exports.PotentiometerElement = PotentiometerElement = __decorate([(0, _litElement.customElement)('wokwi-potentiometer')], PotentiometerElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js","lit-html/directives/style-map":"../../node_modules/lit-html/directives/style-map.js"}],"../../node_modules/@wokwi/elements/dist/esm/neopixel-matrix-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.NeopixelMatrixElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

const pixelWidth = 5.66;
const pixelHeight = 5;
/**
 * Renders a matrix of NeoPixels (smart RGB LEDs).
 * Optimized for displaying large matrices (up to thousands of elements).
 *
 * The color of individual pixels can be set by calling `setPixel(row, col, { r, g, b })`
 * on this element, e.g. `element.setPixel(0, 0, { r: 1, g: 0, b: 0 })` to set the leftmost
 * pixel to red.
 */

let NeopixelMatrixElement = class NeopixelMatrixElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    /**
     * Number of rows in the matrix
     */

    this.rows = 8;
    /**
     * Number of columns in the matrix
     */

    this.cols = 8;
    /**
     * The spacing between two adjacent rows, in mm
     */

    /**
     * The spacing between two adjacent columns, in mm
     */

    this.colSpacing = 1;
    /**
     * Whether to apply blur to the light. Blurring the light
     * creates a bit more realistic look, but negatively impacts
     * performance. It's recommended to leave this off for large
     * matrices.
     */

    this.blurLight = false;
    /**
     * Animate the LEDs in the matrix. Used primarily for testing in Storybook.
     * The animation sequence is not guaranteed and may change in future releases of
     * this element.
     */

    this.animation = false;
    this.rowSpacing = 1;
    this.pixelElements = null;
    this.animationFrame = null;

    this.animateStep = () => {
      const time = new Date().getTime();
      const {
        rows,
        cols
      } = this;

      const pixelValue = n => n % 2000 > 1000 ? 1 - n % 1000 / 1000 : n % 1000 / 1000;

      for (let row = 0; row < rows; row++) {
        for (let col = 0; col < cols; col++) {
          const radius = Math.sqrt((row - rows / 2 + 0.5) ** 2 + (col - cols / 2 + 0.5) ** 2);
          this.setPixel(row, col, {
            r: pixelValue(radius * 100 + time),
            g: pixelValue(radius * 100 + time + 200),
            b: pixelValue(radius * 100 + time + 400)
          });
        }
      }

      this.animationFrame = requestAnimationFrame(this.animateStep);
    };
  }

  static get styles() {
    return _litElement.css`
      :host {
        display: flex;
      }
    `;
  }

  getPixelElements() {
    if (!this.shadowRoot) {
      return null;
    }

    if (!this.pixelElements) {
      this.pixelElements = Array.from(this.shadowRoot.querySelectorAll('g.pixel')).map(e => Array.from(e.querySelectorAll('ellipse')));
    }

    return this.pixelElements;
  }
  /**
   * Resets all the pixels to off state (r=0, g=0, b=0).
   */


  reset() {
    const pixelElements = this.getPixelElements();

    if (!pixelElements) {
      return;
    }

    for (const [rElement, gElement, bElement, colorElement] of pixelElements) {
      rElement.style.opacity = '0';
      gElement.style.opacity = '0';
      bElement.style.opacity = '0';
      colorElement.style.opacity = '0';
    }
  }
  /**
   * Sets the color of a single neopixel in the matrix
   * @param row Row number of the pixel to set
   * @param col Column number of the pixel to set
   * @param rgb An object containing the {r, g, b} values for the pixel
   */


  setPixel(row, col, rgb) {
    const pixelElements = this.getPixelElements();

    if (row < 0 || col < 0 || row >= this.rows || col >= this.cols || !pixelElements) {
      return null;
    }

    const {
      r,
      g,
      b
    } = rgb;

    const spotOpacity = value => value > 0.001 ? 0.7 + value * 0.3 : 0;

    const maxOpacity = Math.max(r, g, b);
    const minOpacity = Math.min(r, g, b);
    const opacityDelta = maxOpacity - minOpacity;
    const multiplier = Math.max(1, 2 - opacityDelta * 20);
    const glowBase = 0.1 + Math.max(maxOpacity * 2 - opacityDelta * 5, 0);

    const glowColor = value => value > 0.005 ? 0.1 + value * 0.9 : 0;

    const glowOpacity = value => value > 0.005 ? glowBase + value * (1 - glowBase) : 0;

    const cssVal = value => maxOpacity ? Math.floor(Math.min(glowColor(value / maxOpacity) * multiplier, 1) * 255) : 255;

    const cssColor = `rgb(${cssVal(r)}, ${cssVal(g)}, ${cssVal(b)})`;
    const pixelElement = pixelElements[row * this.cols + col];
    const [rElement, gElement, bElement, colorElement] = pixelElement;
    rElement.style.opacity = spotOpacity(r).toFixed(2);
    gElement.style.opacity = spotOpacity(g).toFixed(2);
    bElement.style.opacity = spotOpacity(b).toFixed(2);
    colorElement.style.opacity = glowOpacity(maxOpacity).toFixed(2);
    colorElement.style.fill = cssColor;
  }

  updated() {
    if (this.animation && !this.animationFrame) {
      this.animationFrame = requestAnimationFrame(this.animateStep);
    } else if (!this.animation && this.animationFrame) {
      cancelAnimationFrame(this.animationFrame);
      this.animationFrame = null;
    }
  }

  renderPixels() {
    const result = [];
    const {
      cols,
      rows,
      colSpacing,
      rowSpacing
    } = this;
    const patWidth = pixelWidth + colSpacing;
    const patHeight = pixelHeight + rowSpacing;

    for (let row = 0; row < rows; row++) {
      for (let col = 0; col < cols; col++) {
        result.push(_litElement.svg`
        <g transform="translate(${patWidth * col}, ${patHeight * row})" class="pixel">
          <ellipse cx="2.5" cy="2.3" rx="0.3" ry="0.3" fill="red" opacity="0" />
          <ellipse cx="3.5" cy="3.2" rx="0.3" ry="0.3" fill="green" opacity="0" />
          <ellipse cx="3.3" cy="1.45" rx="0.3" ry="0.3" fill="blue" opacity="0" />
          <ellipse cx="3" cy="2.5" rx="2.2" ry="2.2" opacity="0" />
        </g>`);
      }
    }

    this.pixelElements = null;
    return result;
  }

  render() {
    const {
      cols,
      rows,
      rowSpacing,
      colSpacing,
      blurLight
    } = this;
    const patWidth = pixelWidth + colSpacing;
    const patHeight = pixelHeight + rowSpacing;
    const width = pixelWidth * cols + colSpacing * (cols - 1);
    const height = pixelHeight * rows + rowSpacing * (rows - 1);
    return _litElement.html`
      <svg
        width="${width}mm"
        height="${height}mm"
        version="1.1"
        viewBox="0 0 ${width} ${height}"
        xmlns="http://www.w3.org/2000/svg"
      >
        <filter id="blurLight" x="-0.8" y="-0.8" height="2.8" width="2.8">
          <feGaussianBlur stdDeviation="0.3" />
        </filter>

        <pattern id="pixel" width="${patWidth}" height="${patHeight}" patternUnits="userSpaceOnUse">
          <rect x=".33308" y="0" width="5" height="5" fill="#fff" />
          <rect x=".016709" y=".4279" width=".35114" height=".9" fill="#eaeaea" />
          <rect x="0" y="3.6518" width=".35114" height=".9" fill="#eaeaea" />
          <rect x="5.312" y="3.6351" width=".35114" height=".9" fill="#eaeaea" />
          <rect x="5.312" y=".3945" width=".35114" height=".9" fill="#eaeaea" />
          <circle cx="2.8331" cy="2.5" r="2.1" fill="#ddd" />
          <circle cx="2.8331" cy="2.5" r="1.7325" fill="#e6e6e6" />
          <g fill="#bfbfbf">
            <path
              d="m4.3488 3.3308s-0.0889-0.087-0.0889-0.1341c0-0.047-6e-3 -1.1533-6e-3 -1.1533s-0.0591-0.1772-0.2008-0.1772c-0.14174 0-0.81501 0.012-0.81501 0.012s-0.24805 0.024-0.23624 0.3071c0.0118 0.2835 0.032 2.0345 0.032 2.0345 0.54707-0.046 1.0487-0.3494 1.3146-0.8888z"
            />
            <path
              d="m4.34 1.6405h-1.0805s-0.24325 0.019-0.26204-0.2423l6e-3 -0.6241c0.57782 0.075 1.0332 0.3696 1.3366 0.8706z"
            />
            <path
              d="m2.7778 2.6103-0.17127 0.124-0.8091-0.012c-0.17122-0.019-0.17062-0.2078-0.17062-0.2078-1e-3 -0.3746 1e-3 -0.2831-9e-3 -0.8122l-0.31248-0.018s0.43453-0.9216 1.4786-0.9174c-1.1e-4 0.6144-4e-3 1.2289-6e-3 1.8434z"
            />
            <path
              d="m2.7808 3.0828-0.0915-0.095h-0.96857l-0.0915 0.1447-3e-3 0.1127c0 0.065-0.12108 0.08-0.12108 0.08h-0.20909c0.55906 0.9376 1.4867 0.9155 1.4867 0.9155 1e-3 -0.3845-2e-3 -0.7692-2e-3 -1.1537z"
            />
          </g>
          <path
            d="m4.053 1.8619c-0.14174 0-0.81494 0.013-0.81494 0.013s-0.24797 0.024-0.23616 0.3084c3e-3 0.077 5e-3 0.3235 9e-3 0.5514h1.247c-2e-3 -0.33-4e-3 -0.6942-4e-3 -0.6942s-0.0593-0.1781-0.20102-0.1781z"
            fill="#666"
          />
        </pattern>
        <rect width="${width}" height="${height}" fill="url(#pixel)"></rect>
        <g style="${blurLight ? 'filter: url(#blurLight)' : ''}">
          ${this.renderPixels()}
        </g>
      </svg>
    `;
  }

};
exports.NeopixelMatrixElement = NeopixelMatrixElement;

__decorate([(0, _litElement.property)()], NeopixelMatrixElement.prototype, "rows", void 0);

__decorate([(0, _litElement.property)()], NeopixelMatrixElement.prototype, "cols", void 0);

__decorate([(0, _litElement.property)({
  attribute: 'colspacing'
})], NeopixelMatrixElement.prototype, "colSpacing", void 0);

__decorate([(0, _litElement.property)()], NeopixelMatrixElement.prototype, "blurLight", void 0);

__decorate([(0, _litElement.property)()], NeopixelMatrixElement.prototype, "animation", void 0);

__decorate([(0, _litElement.property)({
  attribute: 'rowspacing'
})], NeopixelMatrixElement.prototype, "rowSpacing", void 0);

exports.NeopixelMatrixElement = NeopixelMatrixElement = __decorate([(0, _litElement.customElement)('wokwi-neopixel-matrix')], NeopixelMatrixElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/ssd1306-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SSD1306Element = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
}; // Reference: https://cdn-learn.adafruit.com/assets/assets/000/036/494/original/lcds___displays_fabprint.png?1476374574


let SSD1306Element = class SSD1306Element extends _litElement.LitElement {
  constructor() {
    super();
    this.width = 150;
    this.height = 116;
    this.screenWidth = 128;
    this.screenHeight = 64;
    this.canvas = void 0;
    this.ctx = null;
    this.imageData = new ImageData(this.screenWidth, this.screenHeight);
  }
  /**
   * Used for initiating update of an imageData data which its reference wasn't changed
   */


  redraw() {
    var _a;

    (_a = this.ctx) === null || _a === void 0 ? void 0 : _a.putImageData(this.imageData, 0, 0);
  }

  initContext() {
    var _a, _b;

    this.canvas = (_a = this.shadowRoot) === null || _a === void 0 ? void 0 : _a.querySelector('canvas'); // No need to clear canvas rect - all images will have full opacity

    this.ctx = (_b = this.canvas) === null || _b === void 0 ? void 0 : _b.getContext('2d');
  }

  firstUpdated() {
    var _a;

    this.initContext();
    (_a = this.ctx) === null || _a === void 0 ? void 0 : _a.putImageData(this.imageData, 0, 0);
  }

  updated() {
    if (this.imageData) {
      this.redraw();
    }
  }

  render() {
    const {
      width,
      height,
      screenWidth,
      screenHeight,
      imageData
    } = this;
    const visibility = imageData ? 'visible' : 'hidden';
    return _litElement.html`<svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
      <g>
        <rect stroke="#BE9B72" fill="#025CAF" x=".5" y=".5" width="148" height="114" rx="13" />

        <g transform="translate(6 6)" fill="#59340A" stroke="#BE9B72" stroke-width="0.6px">
          <circle cx="130" cy="6" r="5.5" />
          <circle cx="6" cy="6" r="5.5" />
          <circle cx="130" cy="96" r="5.5" />
          <circle cx="6" cy="96" r="5.5" />
        </g>

        <g transform="translate(11.4 26)">
          <!-- 128 x 64 screen -->
          <rect fill="#1A1A1A" width="${screenWidth}" height="${screenHeight}" />
          <!-- image holder -->
          <foreignObject
            ?visibility="${visibility}"
            width="${screenWidth}"
            height="${screenHeight}"
          >
            <canvas width="${screenWidth}" height="${screenHeight}"></canvas>
          </foreignObject>
        </g>

        <!-- All texts -->
        <g
          fill="#FFF"
          text-anchor="middle"
          font-size="5"
          font-weight="300"
          font-family="MarkerFelt-Wide, Marker Felt, monospace"
        >
          <g transform="translate(37 3)">
            <text x="0" y="5">Data</text>
            <text x="19" y="5">SA0</text>
            <text x="41" y="5">CS</text>
            <text x="60" y="5">Vin</text>
          </g>

          <g transform="translate(41 17)">
            <text x="0" y="6">C1k</text>
            <text x="12" y="6">DC</text>
            <text x="23" y="6">Rst</text>
            <text x="39" y="6">3v3</text>
            <text x="58" y="6">Gnd</text>
          </g>
          <!-- Star -->
          <path
            d="M115.5 10.06l-1.59 2.974-3.453.464 2.495 2.245-.6 3.229 3.148-1.528 3.148 1.528-.6-3.23 2.495-2.244-3.453-.464-1.59-2.974z"
            stroke="#FFF"
          />
        </g>

        <!-- PINS -->
        <g transform="translate(33 9)" fill="#9D9D9A" stroke-width="0.4">
          <circle stroke="#262626" cx="70.5" cy="3.5" r="3.5" />
          <circle stroke="#007ADB" cx="60.5" cy="3.5" r="3.5" />
          <circle stroke="#9D5B96" cx="50.5" cy="3.5" r="3.5" />
          <circle stroke="#009E9B" cx="41.5" cy="3.5" r="3.5" />
          <circle stroke="#E8D977" cx="31.5" cy="3.5" r="3.5" />
          <circle stroke="#C08540" cx="21.5" cy="3.5" r="3.5" />
          <circle stroke="#B4AEAB" cx="12.5" cy="3.5" r="3.5" />
          <circle stroke="#E7DBDB" cx="3.5" cy="3.5" r="3.5" />
        </g>
      </g>
    </svg> `;
  }

};
exports.SSD1306Element = SSD1306Element;

__decorate([(0, _litElement.property)()], SSD1306Element.prototype, "imageData", void 0);

exports.SSD1306Element = SSD1306Element = __decorate([(0, _litElement.customElement)('wokwi-ssd1306')], SSD1306Element);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/@wokwi/elements/dist/esm/buzzer-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.BuzzerElement = void 0;

var _litElement = require("lit-element");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * Renders a piezo electric buzzer.
 */
let BuzzerElement = class BuzzerElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    /**
     * Boolean representing if an electric signal is coming in the buzzer
     * If true a music note will be displayed on top of the buzzer
     */

    this.hasSignal = false;
  }

  static get styles() {
    return _litElement.css`
      :host {
        display: inline-block;
      }

      .buzzer-container {
        display: flex;
        flex-direction: column;
        width: 75px;
      }

      .music-note {
        position: relative;
        left: 40px;
        animation-duration: 1.5s;
        animation-name: animate-note;
        animation-iteration-count: infinite;
        animation-timing-function: linear;
        transform: scale(1.5);
        fill: blue;
        offset-path: path(
          'm0 0c-0.9-0.92-1.8-1.8-2.4-2.8-0.56-0.92-0.78-1.8-0.58-2.8 0.2-0.92 0.82-1.8 1.6-2.8 0.81-0.92 1.8-1.8 2.6-2.8 0.81-0.92 1.4-1.8 1.6-2.8 0.2-0.92-0.02-1.8-0.58-2.8-0.56-0.92-1.5-1.8-2.4-2.8'
        );
        offset-rotate: 0deg;
      }

      @keyframes animate-note {
        0% {
          offset-distance: 0%;
          opacity: 0;
        }
        10% {
          offset-distance: 10%;
          opacity: 1;
        }
        75% {
          offset-distance: 75%;
          opacity: 1;
        }
        100% {
          offset-distance: 100%;
          opacity: 0;
        }
      }
    `;
  }

  render() {
    const buzzerOn = this.hasSignal;
    return _litElement.html`
      <div class="buzzer-container">
        <svg
          class="music-note"
          style="visibility: ${buzzerOn ? '' : 'hidden'}"
          xmlns="http://www.w3.org/2000/svg"
          width="8"
          height="8"
          viewBox="0 0 8 8"
        >
          <path
            d="M8 0c-5 0-6 1-6 1v4.09c-.15-.05-.33-.09-.5-.09-.83 0-1.5.67-1.5 1.5s.67 1.5 1.5 1.5 1.5-.67 1.5-1.5v-3.97c.73-.23 1.99-.44 4-.5v2.06c-.15-.05-.33-.09-.5-.09-.83 0-1.5.67-1.5 1.5s.67 1.5 1.5 1.5 1.5-.67 1.5-1.5v-5.5z"
          />
        </svg>

        <svg
          width="17mm"
          height="20mm"
          version="1.1"
          viewBox="0 0 17 20"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path d="m8 16.5v3.5" fill="none" stroke="#000" stroke-width=".5" />
          <path d="m9 16.5v3.5" fill="#f00" stroke="#f00" stroke-width=".5" />
          <g stroke="#000">
            <g>
              <ellipse cx="8.5" cy="8.5" rx="8.15" ry="8.15" fill="#1a1a1a" stroke-width=".7" />
              <circle
                cx="8.5"
                cy="8.5"
                r="6.3472"
                fill="none"
                stroke-width=".3"
                style="paint-order:normal"
              />
              <circle
                cx="8.5"
                cy="8.5"
                r="4.3488"
                fill="none"
                stroke-width=".3"
                style="paint-order:normal"
              />
            </g>
            <circle cx="8.5" cy="8.5" r="1.3744" fill="#ccc" stroke-width=".25" />
          </g>
        </svg>
      </div>
    `;
  }

};
exports.BuzzerElement = BuzzerElement;

__decorate([(0, _litElement.property)()], BuzzerElement.prototype, "hasSignal", void 0);

exports.BuzzerElement = BuzzerElement = __decorate([(0, _litElement.customElement)('wokwi-buzzer')], BuzzerElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js"}],"../../node_modules/lit-html/directives/class-map.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.classMap = void 0;

var _litHtml = require("../lit-html.js");

/**
 * @license
 * Copyright (c) 2018 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at
 * http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at
 * http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
// IE11 doesn't support classList on SVG elements, so we emulate it with a Set
class ClassList {
  constructor(element) {
    this.classes = new Set();
    this.changed = false;
    this.element = element;
    const classList = (element.getAttribute('class') || '').split(/\s+/);

    for (const cls of classList) {
      this.classes.add(cls);
    }
  }

  add(cls) {
    this.classes.add(cls);
    this.changed = true;
  }

  remove(cls) {
    this.classes.delete(cls);
    this.changed = true;
  }

  commit() {
    if (this.changed) {
      let classString = '';
      this.classes.forEach(cls => classString += cls + ' ');
      this.element.setAttribute('class', classString);
    }
  }

}
/**
 * Stores the ClassInfo object applied to a given AttributePart.
 * Used to unset existing values when a new ClassInfo object is applied.
 */


const previousClassesCache = new WeakMap();
/**
 * A directive that applies CSS classes. This must be used in the `class`
 * attribute and must be the only part used in the attribute. It takes each
 * property in the `classInfo` argument and adds the property name to the
 * element's `class` if the property value is truthy; if the property value is
 * falsey, the property name is removed from the element's `class`. For example
 * `{foo: bar}` applies the class `foo` if the value of `bar` is truthy.
 * @param classInfo {ClassInfo}
 */

const classMap = (0, _litHtml.directive)(classInfo => part => {
  if (!(part instanceof _litHtml.AttributePart) || part instanceof _litHtml.PropertyPart || part.committer.name !== 'class' || part.committer.parts.length > 1) {
    throw new Error('The `classMap` directive must be used in the `class` attribute ' + 'and must be the only part in the attribute.');
  }

  const {
    committer
  } = part;
  const {
    element
  } = committer;
  let previousClasses = previousClassesCache.get(part);

  if (previousClasses === undefined) {
    // Write static classes once
    // Use setAttribute() because className isn't a string on SVG elements
    element.setAttribute('class', committer.strings.join(' '));
    previousClassesCache.set(part, previousClasses = new Set());
  }

  const classList = element.classList || new ClassList(element); // Remove old classes that no longer apply
  // We use forEach() instead of for-of so that re don't require down-level
  // iteration.

  previousClasses.forEach(name => {
    if (!(name in classInfo)) {
      classList.remove(name);
      previousClasses.delete(name);
    }
  }); // Add or remove classes based on their classMap value

  for (const name in classInfo) {
    const value = classInfo[name];

    if (value != previousClasses.has(name)) {
      // We explicitly want a loose truthy check of `value` because it seems
      // more convenient that '' and 0 are skipped.
      if (value) {
        classList.add(name);
        previousClasses.add(name);
      } else {
        classList.remove(name);
        previousClasses.delete(name);
      }
    }
  }

  if (typeof classList.commit === 'function') {
    classList.commit();
  }
});
exports.classMap = classMap;
},{"../lit-html.js":"../../node_modules/lit-html/lit-html.js"}],"../../node_modules/@wokwi/elements/dist/esm/rotary-dialer-element.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.RotaryDialerElement = void 0;

var _litElement = require("lit-element");

var _styleMap = require("lit-html/directives/style-map");

var _classMap = require("lit-html/directives/class-map");

var __decorate = void 0 && (void 0).__decorate || function (decorators, target, key, desc) {
  var c = arguments.length,
      r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
      d;
  if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
  return c > 3 && r && Object.defineProperty(target, key, r), r;
};

let RotaryDialerElement = class RotaryDialerElement extends _litElement.LitElement {
  constructor() {
    super(...arguments);
    this.digit = '';
    this.stylesMapping = {};
    this.classes = {
      'rotate-path': true
    };
    this.degrees = [320, 56, 87, 115, 143, 173, 204, 232, 260, 290];
  }

  static get styles() {
    return _litElement.css`
      .text {
        cursor: grab;
        user-select: none;
      }
      input:focus + svg #container {
        stroke: #4e50d7;
        stroke-width: 3;
      }
      .hide-input {
        position: absolute;
        clip: rect(0 0 0 0);
        width: 1px;
        height: 1px;
        margin: -1px;
      }
      .rotate-path {
        transform-origin: center;
        transition: transform 1000ms ease-in;
      }
      .dialer-anim {
        transform: rotate(var(--angle));
      }
    `;
  }

  removeDialerAnim() {
    this.classes = Object.assign(Object.assign({}, this.classes), {
      'dialer-anim': false
    });
    this.stylesMapping = {
      '--angle': 0
    };
    this.requestUpdate();
  }
  /**
   * Exposed lazy dial by applying dial method with a given argument of number from 0-9
   * @param digit
   */


  dial(digit) {
    this.digit = digit;
    this.addDialerAnim(digit);
  }

  onValueChange(event) {
    const target = event.target;
    this.digit = parseInt(target.value);
    this.dial(this.digit);
    target.value = '';
  }

  addDialerAnim(digit) {
    requestAnimationFrame(() => {
      this.dispatchEvent(new CustomEvent('dial-start', {
        detail: {
          digit: this.digit
        }
      })); // When you click on a digit, the circle-hole of that digit
      // should go all the way until the finger stop.

      this.classes = Object.assign(Object.assign({}, this.classes), {
        'dialer-anim': true
      });
      const deg = this.degrees[digit];
      this.stylesMapping = {
        '--angle': deg + 'deg'
      };
      this.requestUpdate();
    });
  }

  focusInput() {
    var _a, _b;

    const inputEl = (_a = this.shadowRoot) === null || _a === void 0 ? void 0 : _a.querySelector('.hide-input');
    (_b = inputEl) === null || _b === void 0 ? void 0 : _b.focus();
  }

  render() {
    return _litElement.html`
      <input
        tabindex="0"
        type="number"
        class="hide-input"
        value="${this.digit}"
        @input="${this.onValueChange}"
      />
      <svg width="266" height="266" @click="${this.focusInput}" xmlns="http://www.w3.org/2000/svg">
        <g transform="translate(1 1)">
          <circle stroke="#979797" stroke-width="3" fill="#1F1F1F" cx="133" cy="133" r="131" />
          <circle stroke="#fff" stroke-width="2" fill="#D8D8D8" cx="133" cy="133" r="72" />
          <path
            class=${(0, _classMap.classMap)(this.classes)}
            @transitionstart="${() => {
      if (!this.classes['dialer-anim']) {
        this.dispatchEvent(new CustomEvent('dial', {
          detail: {
            digit: this.digit
          }
        }));
      }
    }}"
            @transitionend="${() => {
      if (!this.classes['dialer-anim']) {
        this.dispatchEvent(new CustomEvent('dial-end', {
          detail: {
            digit: this.digit
          }
        }));
      }

      this.removeDialerAnim();
    }}"
            d="M133.5,210 C146.478692,210 157,220.521308 157,233.5 C157,246.478692 146.478692,257 133.5,257 C120.521308,257 110,246.478692 110,233.5 C110,220.521308 120.521308,210 133.5,210 Z M83.5,197 C96.4786916,197 107,207.521308 107,220.5 C107,233.478692 96.4786916,244 83.5,244 C70.5213084,244 60,233.478692 60,220.5 C60,207.521308 70.5213084,197 83.5,197 Z M45.5,163 C58.4786916,163 69,173.521308 69,186.5 C69,199.478692 58.4786916,210 45.5,210 C32.5213084,210 22,199.478692 22,186.5 C22,173.521308 32.5213084,163 45.5,163 Z M32.5,114 C45.4786916,114 56,124.521308 56,137.5 C56,150.478692 45.4786916,161 32.5,161 C19.5213084,161 9,150.478692 9,137.5 C9,124.521308 19.5213084,114 32.5,114 Z M234.5,93 C247.478692,93 258,103.521308 258,116.5 C258,129.478692 247.478692,140 234.5,140 C221.521308,140 211,129.478692 211,116.5 C211,103.521308 221.521308,93 234.5,93 Z M41.5,64 C54.4786916,64 65,74.5213084 65,87.5 C65,100.478692 54.4786916,111 41.5,111 C28.5213084,111 18,100.478692 18,87.5 C18,74.5213084 28.5213084,64 41.5,64 Z M214.5,46 C227.478692,46 238,56.5213084 238,69.5 C238,82.4786916 227.478692,93 214.5,93 C201.521308,93 191,82.4786916 191,69.5 C191,56.5213084 201.521308,46 214.5,46 Z M76.5,26 C89.4786916,26 100,36.5213084 100,49.5 C100,62.4786916 89.4786916,73 76.5,73 C63.5213084,73 53,62.4786916 53,49.5 C53,36.5213084 63.5213084,26 76.5,26 Z M173.5,15 C186.478692,15 197,25.5213084 197,38.5 C197,51.4786916 186.478692,62 173.5,62 C160.521308,62 150,51.4786916 150,38.5 C150,25.5213084 160.521308,15 173.5,15 Z M123.5,7 C136.478692,7 147,17.5213084 147,30.5 C147,43.4786916 136.478692,54 123.5,54 C110.521308,54 100,43.4786916 100,30.5 C100,17.5213084 110.521308,7 123.5,7 Z"
            id="slots"
            stroke="#fff"
            fill-opacity="0.5"
            fill="#D8D8D8"
            style=${(0, _styleMap.styleMap)(this.stylesMapping)}
          ></path>
        </g>
        <circle id="container" fill-opacity=".5" fill="#070707" cx="132.5" cy="132.5" r="132.5" />
        <g class="text" font-family="Marker Felt, monospace" font-size="21" fill="#FFF">
          <text @mouseup=${() => this.dial(0)} x="129" y="243">0</text>
          <text @mouseup=${() => this.dial(9)} x="78" y="230">9</text>
          <text @mouseup=${() => this.dial(8)} x="40" y="194">8</text>
          <text @mouseup=${() => this.dial(7)} x="28" y="145">7</text>
          <text @mouseup=${() => this.dial(6)} x="35" y="97">6</text>
          <text @mouseup=${() => this.dial(5)} x="72" y="58">5</text>
          <text @mouseup=${() => this.dial(4)} x="117" y="41">4</text>
          <text @mouseup=${() => this.dial(3)} x="168" y="47">3</text>
          <text @mouseup=${() => this.dial(2)} x="210" y="79">2</text>
          <text @mouseup=${() => this.dial(1)} x="230" y="126">1</text>
        </g>
        <path
          d="M182.738529,211.096297 L177.320119,238.659185 L174.670528,252.137377 L188.487742,252.137377 L182.738529,211.096297 Z"
          stroke="#979797"
          fill="#D8D8D8"
          transform="translate(181.562666, 230.360231) rotate(-22.000000) translate(-181.562666, -230.360231)"
        ></path>
      </svg>
    `;
  }

};
exports.RotaryDialerElement = RotaryDialerElement;
exports.RotaryDialerElement = RotaryDialerElement = __decorate([(0, _litElement.customElement)('wokwi-rotary-dialer')], RotaryDialerElement);
},{"lit-element":"../../node_modules/lit-element/lit-element.js","lit-html/directives/style-map":"../../node_modules/lit-html/directives/style-map.js","lit-html/directives/class-map":"../../node_modules/lit-html/directives/class-map.js"}],"../../node_modules/@wokwi/elements/dist/esm/index.js":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "SevenSegmentElement", {
  enumerable: true,
  get: function () {
    return _segmentElement.SevenSegmentElement;
  }
});
Object.defineProperty(exports, "ArduinoUnoElement", {
  enumerable: true,
  get: function () {
    return _arduinoUnoElement.ArduinoUnoElement;
  }
});
Object.defineProperty(exports, "LCD1602Element", {
  enumerable: true,
  get: function () {
    return _lcd1602Element.LCD1602Element;
  }
});
Object.defineProperty(exports, "fontA00", {
  enumerable: true,
  get: function () {
    return _lcd1602FontA.fontA00;
  }
});
Object.defineProperty(exports, "fontA02", {
  enumerable: true,
  get: function () {
    return _lcd1602FontA2.fontA02;
  }
});
Object.defineProperty(exports, "LEDElement", {
  enumerable: true,
  get: function () {
    return _ledElement.LEDElement;
  }
});
Object.defineProperty(exports, "NeoPixelElement", {
  enumerable: true,
  get: function () {
    return _neopixelElement.NeoPixelElement;
  }
});
Object.defineProperty(exports, "PushbuttonElement", {
  enumerable: true,
  get: function () {
    return _pushbuttonElement.PushbuttonElement;
  }
});
Object.defineProperty(exports, "ResistorElement", {
  enumerable: true,
  get: function () {
    return _resistorElement.ResistorElement;
  }
});
Object.defineProperty(exports, "MembraneKeypadElement", {
  enumerable: true,
  get: function () {
    return _membraneKeypadElement.MembraneKeypadElement;
  }
});
Object.defineProperty(exports, "PotentiometerElement", {
  enumerable: true,
  get: function () {
    return _potentiometerElement.PotentiometerElement;
  }
});
Object.defineProperty(exports, "NeopixelMatrixElement", {
  enumerable: true,
  get: function () {
    return _neopixelMatrixElement.NeopixelMatrixElement;
  }
});
Object.defineProperty(exports, "SSD1306Element", {
  enumerable: true,
  get: function () {
    return _ssd1306Element.SSD1306Element;
  }
});
Object.defineProperty(exports, "BuzzerElement", {
  enumerable: true,
  get: function () {
    return _buzzerElement.BuzzerElement;
  }
});
Object.defineProperty(exports, "RotaryDialerElement", {
  enumerable: true,
  get: function () {
    return _rotaryDialerElement.RotaryDialerElement;
  }
});

var _segmentElement = require("./7segment-element");

var _arduinoUnoElement = require("./arduino-uno-element");

var _lcd1602Element = require("./lcd1602-element");

var _lcd1602FontA = require("./lcd1602-font-a00");

var _lcd1602FontA2 = require("./lcd1602-font-a02");

var _ledElement = require("./led-element");

var _neopixelElement = require("./neopixel-element");

var _pushbuttonElement = require("./pushbutton-element");

var _resistorElement = require("./resistor-element");

var _membraneKeypadElement = require("./membrane-keypad-element");

var _potentiometerElement = require("./potentiometer-element");

var _neopixelMatrixElement = require("./neopixel-matrix-element");

var _ssd1306Element = require("./ssd1306-element");

var _buzzerElement = require("./buzzer-element");

var _rotaryDialerElement = require("./rotary-dialer-element");
},{"./7segment-element":"../../node_modules/@wokwi/elements/dist/esm/7segment-element.js","./arduino-uno-element":"../../node_modules/@wokwi/elements/dist/esm/arduino-uno-element.js","./lcd1602-element":"../../node_modules/@wokwi/elements/dist/esm/lcd1602-element.js","./lcd1602-font-a00":"../../node_modules/@wokwi/elements/dist/esm/lcd1602-font-a00.js","./lcd1602-font-a02":"../../node_modules/@wokwi/elements/dist/esm/lcd1602-font-a02.js","./led-element":"../../node_modules/@wokwi/elements/dist/esm/led-element.js","./neopixel-element":"../../node_modules/@wokwi/elements/dist/esm/neopixel-element.js","./pushbutton-element":"../../node_modules/@wokwi/elements/dist/esm/pushbutton-element.js","./resistor-element":"../../node_modules/@wokwi/elements/dist/esm/resistor-element.js","./membrane-keypad-element":"../../node_modules/@wokwi/elements/dist/esm/membrane-keypad-element.js","./potentiometer-element":"../../node_modules/@wokwi/elements/dist/esm/potentiometer-element.js","./neopixel-matrix-element":"../../node_modules/@wokwi/elements/dist/esm/neopixel-matrix-element.js","./ssd1306-element":"../../node_modules/@wokwi/elements/dist/esm/ssd1306-element.js","./buzzer-element":"../../node_modules/@wokwi/elements/dist/esm/buzzer-element.js","./rotary-dialer-element":"../../node_modules/@wokwi/elements/dist/esm/rotary-dialer-element.js"}],"compile.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.buildHex = void 0;
const url = 'https://hexi.wokwi.com';

async function buildHex(source) {
  const resp = await fetch(url + '/build', {
    method: 'POST',
    mode: 'cors',
    cache: 'no-cache',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      sketch: source
    })
  });
  return await resp.json();
}

exports.buildHex = buildHex;
},{}],"cpu-performance.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.CPUPerformance = void 0;

class CPUPerformance {
  constructor(cpu, MHZ) {
    this.cpu = cpu;
    this.MHZ = MHZ;
    this.prevTime = 0;
    this.prevCycles = 0;
    this.samples = new Float32Array(64);
    this.sampleIndex = 0;
  }

  reset() {
    this.prevTime = 0;
    this.prevCycles = 0;
    this.sampleIndex = 0;
  }

  update() {
    if (this.prevTime) {
      const delta = performance.now() - this.prevTime;

      if (delta > 0) {
        const deltaCycles = this.cpu.cycles - this.prevCycles;
        const deltaCpuMillis = 1000 * (deltaCycles / this.MHZ);
        const factor = deltaCpuMillis / delta;

        if (!this.sampleIndex) {
          this.samples.fill(factor);
        }

        this.samples[this.sampleIndex++ % this.samples.length] = factor;
      }
    }

    this.prevCycles = this.cpu.cycles;
    this.prevTime = performance.now();
    const avg = this.samples.reduce((x, y) => x + y) / this.samples.length;
    return avg;
  }

}

exports.CPUPerformance = CPUPerformance;
},{}],"../../src/cpu/interrupt.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.avrInterrupt = avrInterrupt;

/**
 * AVR-8 Interrupt Handling
 * Part of AVR8js
 * Reference: http://ww1.microchip.com/downloads/en/devicedoc/atmel-0856-avr-instruction-set-manual.pdf
 *
 * Copyright (C) 2019, Uri Shaked
 */
function avrInterrupt(cpu, addr) {
  const sp = cpu.dataView.getUint16(93, true);
  cpu.data[sp] = cpu.pc & 0xff;
  cpu.data[sp - 1] = cpu.pc >> 8 & 0xff;

  if (cpu.pc22Bits) {
    cpu.data[sp - 2] = cpu.pc >> 16 & 0xff;
  }

  cpu.dataView.setUint16(93, sp - (cpu.pc22Bits ? 3 : 2), true);
  cpu.data[95] &= 0x7f; // clear global interrupt flag

  cpu.cycles += 2;
  cpu.pc = addr;
}
},{}],"../../src/cpu/cpu.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.CPU = void 0;

var _interrupt = require("./interrupt");

/**
 * AVR 8 CPU data structures
 * Part of AVR8js
 *
 * Copyright (C) 2019, Uri Shaked
 */
const registerSpace = 0x100;

class CPU {
  constructor(progMem, sramBytes = 8192) {
    this.progMem = progMem;
    this.sramBytes = sramBytes;
    this.data = new Uint8Array(this.sramBytes + registerSpace);
    this.data16 = new Uint16Array(this.data.buffer);
    this.dataView = new DataView(this.data.buffer);
    this.progBytes = new Uint8Array(this.progMem.buffer);
    this.readHooks = [];
    this.writeHooks = [];
    this.pendingInterrupts = [];
    this.nextClockEvent = null;
    this.clockEventPool = []; // helps avoid garbage collection

    this.pc22Bits = this.progBytes.length > 0x20000; // This lets the Timer Compare output override GPIO pins:

    this.gpioTimerHooks = [];
    this.pc = 0;
    this.cycles = 0;
    this.nextInterrupt = -1;
    this.reset();
  }

  reset() {
    this.data.fill(0);
    this.SP = this.data.length - 1;
    this.pendingInterrupts.splice(0, this.pendingInterrupts.length);
    this.nextInterrupt = -1;
  }

  readData(addr) {
    if (addr >= 32 && this.readHooks[addr]) {
      return this.readHooks[addr](addr);
    }

    return this.data[addr];
  }

  writeData(addr, value) {
    const hook = this.writeHooks[addr];

    if (hook) {
      if (hook(value, this.data[addr], addr)) {
        return;
      }
    }

    this.data[addr] = value;
  }

  get SP() {
    return this.dataView.getUint16(93, true);
  }

  set SP(value) {
    this.dataView.setUint16(93, value, true);
  }

  get SREG() {
    return this.data[95];
  }

  get interruptsEnabled() {
    return this.SREG & 0x80 ? true : false;
  }

  updateNextInterrupt() {
    this.nextInterrupt = this.pendingInterrupts.findIndex(item => !!item);
  }

  setInterruptFlag(interrupt) {
    const {
      flagRegister,
      flagMask,
      enableRegister,
      enableMask
    } = interrupt;

    if (interrupt.inverseFlag) {
      this.data[flagRegister] &= ~flagMask;
    } else {
      this.data[flagRegister] |= flagMask;
    }

    if (this.data[enableRegister] & enableMask) {
      this.queueInterrupt(interrupt);
    }
  }

  updateInterruptEnable(interrupt, registerValue) {
    const {
      enableMask,
      flagRegister,
      flagMask
    } = interrupt;

    if (registerValue & enableMask) {
      if (this.data[flagRegister] & flagMask) {
        this.queueInterrupt(interrupt);
      }
    } else {
      this.clearInterrupt(interrupt, false);
    }
  }

  queueInterrupt(interrupt) {
    this.pendingInterrupts[interrupt.address] = interrupt;
    this.updateNextInterrupt();
  }

  clearInterrupt({
    address,
    flagRegister,
    flagMask
  }, clearFlag = true) {
    delete this.pendingInterrupts[address];

    if (clearFlag) {
      this.data[flagRegister] &= ~flagMask;
    }

    this.updateNextInterrupt();
  }

  clearInterruptByFlag(interrupt, registerValue) {
    const {
      flagRegister,
      flagMask
    } = interrupt;

    if (registerValue & flagMask) {
      this.data[flagRegister] &= ~flagMask;
      this.clearInterrupt(interrupt);
    }
  }

  addClockEvent(callback, cycles) {
    const {
      clockEventPool
    } = this;
    cycles = this.cycles + Math.max(1, cycles);
    const maybeEntry = clockEventPool.pop();
    const entry = maybeEntry !== null && maybeEntry !== void 0 ? maybeEntry : {
      cycles,
      callback,
      next: null
    };
    entry.cycles = cycles;
    entry.callback = callback;
    let {
      nextClockEvent: clockEvent
    } = this;
    let lastItem = null;

    while (clockEvent && clockEvent.cycles < cycles) {
      lastItem = clockEvent;
      clockEvent = clockEvent.next;
    }

    if (lastItem) {
      lastItem.next = entry;
      entry.next = clockEvent;
    } else {
      this.nextClockEvent = entry;
      entry.next = clockEvent;
    }

    return callback;
  }

  updateClockEvent(callback, cycles) {
    if (this.clearClockEvent(callback)) {
      this.addClockEvent(callback, cycles);
      return true;
    }

    return false;
  }

  clearClockEvent(callback) {
    let {
      nextClockEvent: clockEvent
    } = this;

    if (!clockEvent) {
      return false;
    }

    const {
      clockEventPool
    } = this;
    let lastItem = null;

    while (clockEvent) {
      if (clockEvent.callback === callback) {
        if (lastItem) {
          lastItem.next = clockEvent.next;
        } else {
          this.nextClockEvent = clockEvent.next;
        }

        if (clockEventPool.length < 10) {
          clockEventPool.push(clockEvent);
        }

        return true;
      }

      lastItem = clockEvent;
      clockEvent = clockEvent.next;
    }

    return false;
  }

  tick() {
    const {
      nextClockEvent
    } = this;

    if (nextClockEvent && nextClockEvent.cycles <= this.cycles) {
      nextClockEvent.callback();
      this.nextClockEvent = nextClockEvent.next;

      if (this.clockEventPool.length < 10) {
        this.clockEventPool.push(nextClockEvent);
      }
    }

    const {
      nextInterrupt
    } = this;

    if (this.interruptsEnabled && nextInterrupt >= 0) {
      const interrupt = this.pendingInterrupts[nextInterrupt];
      (0, _interrupt.avrInterrupt)(this, interrupt.address);

      if (!interrupt.constant) {
        this.clearInterrupt(interrupt);
      }
    }
  }

}

exports.CPU = CPU;
},{"./interrupt":"../../src/cpu/interrupt.ts"}],"../../src/cpu/instruction.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.avrInstruction = avrInstruction;

/**
 * AVR-8 Instruction Simulation
 * Part of AVR8js
 *
 * Reference: http://ww1.microchip.com/downloads/en/devicedoc/atmel-0856-avr-instruction-set-manual.pdf
 *
 * Instruction timing is currently based on ATmega328p (see the Instruction Set Summary at the end of
 * the datasheet)
 *
 * Copyright (C) 2019, 2020 Uri Shaked
 */
function isTwoWordInstruction(opcode) {
  return (
    /* LDS */
    (opcode & 0xfe0f) === 0x9000 ||
    /* STS */
    (opcode & 0xfe0f) === 0x9200 ||
    /* CALL */
    (opcode & 0xfe0e) === 0x940e ||
    /* JMP */
    (opcode & 0xfe0e) === 0x940c
  );
}

function avrInstruction(cpu) {
  const opcode = cpu.progMem[cpu.pc];

  if ((opcode & 0xfc00) === 0x1c00) {
    /* ADC, 0001 11rd dddd rrrr */
    const d = cpu.data[(opcode & 0x1f0) >> 4];
    const r = cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    const sum = d + r + (cpu.data[95] & 1);
    const R = sum & 255;
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xc0;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= (R ^ r) & (d ^ R) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= sum & 256 ? 1 : 0;
    sreg |= 1 & (d & r | r & ~R | ~R & d) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfc00) === 0xc00) {
    /* ADD, 0000 11rd dddd rrrr */
    const d = cpu.data[(opcode & 0x1f0) >> 4];
    const r = cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    const R = d + r & 255;
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xc0;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= (R ^ r) & (R ^ d) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= d + r & 256 ? 1 : 0;
    sreg |= 1 & (d & r | r & ~R | ~R & d) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xff00) === 0x9600) {
    /* ADIW, 1001 0110 KKdd KKKK */
    const addr = 2 * ((opcode & 0x30) >> 4) + 24;
    const value = cpu.dataView.getUint16(addr, true);
    const R = value + (opcode & 0xf | (opcode & 0xc0) >> 2) & 0xffff;
    cpu.dataView.setUint16(addr, R, true);
    let sreg = cpu.data[95] & 0xe0;
    sreg |= R ? 0 : 2;
    sreg |= 0x8000 & R ? 4 : 0;
    sreg |= ~value & R & 0x8000 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= ~R & value & 0x8000 ? 1 : 0;
    cpu.data[95] = sreg;
    cpu.cycles++;
  } else if ((opcode & 0xfc00) === 0x2000) {
    /* AND, 0010 00rd dddd rrrr */
    const R = cpu.data[(opcode & 0x1f0) >> 4] & cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xe1;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xf000) === 0x7000) {
    /* ANDI, 0111 KKKK dddd KKKK */
    const R = cpu.data[((opcode & 0xf0) >> 4) + 16] & (opcode & 0xf | (opcode & 0xf00) >> 4);
    cpu.data[((opcode & 0xf0) >> 4) + 16] = R;
    let sreg = cpu.data[95] & 0xe1;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfe0f) === 0x9405) {
    /* ASR, 1001 010d dddd 0101 */
    const value = cpu.data[(opcode & 0x1f0) >> 4];
    const R = value >>> 1 | 128 & value;
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xe0;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= value & 1;
    sreg |= sreg >> 2 & 1 ^ sreg & 1 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xff8f) === 0x9488) {
    /* BCLR, 1001 0100 1sss 1000 */
    cpu.data[95] &= ~(1 << ((opcode & 0x70) >> 4));
  } else if ((opcode & 0xfe08) === 0xf800) {
    /* BLD, 1111 100d dddd 0bbb */
    const b = opcode & 7;
    const d = (opcode & 0x1f0) >> 4;
    cpu.data[d] = ~(1 << b) & cpu.data[d] | (cpu.data[95] >> 6 & 1) << b;
  } else if ((opcode & 0xfc00) === 0xf400) {
    /* BRBC, 1111 01kk kkkk ksss */
    if (!(cpu.data[95] & 1 << (opcode & 7))) {
      cpu.pc = cpu.pc + (((opcode & 0x1f8) >> 3) - (opcode & 0x200 ? 0x40 : 0));
      cpu.cycles++;
    }
  } else if ((opcode & 0xfc00) === 0xf000) {
    /* BRBS, 1111 00kk kkkk ksss */
    if (cpu.data[95] & 1 << (opcode & 7)) {
      cpu.pc = cpu.pc + (((opcode & 0x1f8) >> 3) - (opcode & 0x200 ? 0x40 : 0));
      cpu.cycles++;
    }
  } else if ((opcode & 0xff8f) === 0x9408) {
    /* BSET, 1001 0100 0sss 1000 */
    cpu.data[95] |= 1 << ((opcode & 0x70) >> 4);
  } else if ((opcode & 0xfe08) === 0xfa00) {
    /* BST, 1111 101d dddd 0bbb */
    const d = cpu.data[(opcode & 0x1f0) >> 4];
    const b = opcode & 7;
    cpu.data[95] = cpu.data[95] & 0xbf | (d >> b & 1 ? 0x40 : 0);
  } else if ((opcode & 0xfe0e) === 0x940e) {
    /* CALL, 1001 010k kkkk 111k kkkk kkkk kkkk kkkk */
    const k = cpu.progMem[cpu.pc + 1] | (opcode & 1) << 16 | (opcode & 0x1f0) << 13;
    const ret = cpu.pc + 2;
    const sp = cpu.dataView.getUint16(93, true);
    const {
      pc22Bits
    } = cpu;
    cpu.data[sp] = 255 & ret;
    cpu.data[sp - 1] = ret >> 8 & 255;

    if (pc22Bits) {
      cpu.data[sp - 2] = ret >> 16 & 255;
    }

    cpu.dataView.setUint16(93, sp - (pc22Bits ? 3 : 2), true);
    cpu.pc = k - 1;
    cpu.cycles += pc22Bits ? 4 : 3;
  } else if ((opcode & 0xff00) === 0x9800) {
    /* CBI, 1001 1000 AAAA Abbb */
    const A = opcode & 0xf8;
    const b = opcode & 7;
    const R = cpu.readData((A >> 3) + 32);
    cpu.writeData((A >> 3) + 32, R & ~(1 << b));
  } else if ((opcode & 0xfe0f) === 0x9400) {
    /* COM, 1001 010d dddd 0000 */
    const d = (opcode & 0x1f0) >> 4;
    const R = 255 - cpu.data[d];
    cpu.data[d] = R;
    let sreg = cpu.data[95] & 0xe1 | 1;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfc00) === 0x1400) {
    /* CP, 0001 01rd dddd rrrr */
    const val1 = cpu.data[(opcode & 0x1f0) >> 4];
    const val2 = cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    const R = val1 - val2;
    let sreg = cpu.data[95] & 0xc0;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= 0 !== ((val1 ^ val2) & (val1 ^ R) & 128) ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= val2 > val1 ? 1 : 0;
    sreg |= 1 & (~val1 & val2 | val2 & R | R & ~val1) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfc00) === 0x400) {
    /* CPC, 0000 01rd dddd rrrr */
    const arg1 = cpu.data[(opcode & 0x1f0) >> 4];
    const arg2 = cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    let sreg = cpu.data[95];
    const r = arg1 - arg2 - (sreg & 1);
    sreg = sreg & 0xc0 | (!r && sreg >> 1 & 1 ? 2 : 0) | (arg2 + (sreg & 1) > arg1 ? 1 : 0);
    sreg |= 128 & r ? 4 : 0;
    sreg |= (arg1 ^ arg2) & (arg1 ^ r) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= 1 & (~arg1 & arg2 | arg2 & r | r & ~arg1) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xf000) === 0x3000) {
    /* CPI, 0011 KKKK dddd KKKK */
    const arg1 = cpu.data[((opcode & 0xf0) >> 4) + 16];
    const arg2 = opcode & 0xf | (opcode & 0xf00) >> 4;
    const r = arg1 - arg2;
    let sreg = cpu.data[95] & 0xc0;
    sreg |= r ? 0 : 2;
    sreg |= 128 & r ? 4 : 0;
    sreg |= (arg1 ^ arg2) & (arg1 ^ r) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= arg2 > arg1 ? 1 : 0;
    sreg |= 1 & (~arg1 & arg2 | arg2 & r | r & ~arg1) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfc00) === 0x1000) {
    /* CPSE, 0001 00rd dddd rrrr */
    if (cpu.data[(opcode & 0x1f0) >> 4] === cpu.data[opcode & 0xf | (opcode & 0x200) >> 5]) {
      const nextOpcode = cpu.progMem[cpu.pc + 1];
      const skipSize = isTwoWordInstruction(nextOpcode) ? 2 : 1;
      cpu.pc += skipSize;
      cpu.cycles += skipSize;
    }
  } else if ((opcode & 0xfe0f) === 0x940a) {
    /* DEC, 1001 010d dddd 1010 */
    const value = cpu.data[(opcode & 0x1f0) >> 4];
    const R = value - 1;
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xe1;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= 128 === value ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if (opcode === 0x9519) {
    /* EICALL, 1001 0101 0001 1001 */
    const retAddr = cpu.pc + 1;
    const sp = cpu.dataView.getUint16(93, true);
    const eind = cpu.data[0x5c];
    cpu.data[sp] = retAddr & 255;
    cpu.data[sp - 1] = retAddr >> 8 & 255;
    cpu.data[sp - 2] = retAddr >> 16 & 255;
    cpu.dataView.setUint16(93, sp - 3, true);
    cpu.pc = (eind << 16 | cpu.dataView.getUint16(30, true)) - 1;
    cpu.cycles += 3;
  } else if (opcode === 0x9419) {
    /* EIJMP, 1001 0100 0001 1001 */
    const eind = cpu.data[0x5c];
    cpu.pc = (eind << 16 | cpu.dataView.getUint16(30, true)) - 1;
    cpu.cycles++;
  } else if (opcode === 0x95d8) {
    /* ELPM, 1001 0101 1101 1000 */
    const rampz = cpu.data[0x5b];
    cpu.data[0] = cpu.progBytes[rampz << 16 | cpu.dataView.getUint16(30, true)];
    cpu.cycles += 2;
  } else if ((opcode & 0xfe0f) === 0x9006) {
    /* ELPM(REG), 1001 000d dddd 0110 */
    const rampz = cpu.data[0x5b];
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.progBytes[rampz << 16 | cpu.dataView.getUint16(30, true)];
    cpu.cycles += 2;
  } else if ((opcode & 0xfe0f) === 0x9007) {
    /* ELPM(INC), 1001 000d dddd 0111 */
    const rampz = cpu.data[0x5b];
    const i = cpu.dataView.getUint16(30, true);
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.progBytes[rampz << 16 | i];
    cpu.dataView.setUint16(30, i + 1, true);

    if (i === 0xffff) {
      cpu.data[0x5b] = (rampz + 1) % (cpu.progBytes.length >> 16);
    }

    cpu.cycles += 2;
  } else if ((opcode & 0xfc00) === 0x2400) {
    /* EOR, 0010 01rd dddd rrrr */
    const R = cpu.data[(opcode & 0x1f0) >> 4] ^ cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xe1;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xff88) === 0x308) {
    /* FMUL, 0000 0011 0ddd 1rrr */
    const v1 = cpu.data[((opcode & 0x70) >> 4) + 16];
    const v2 = cpu.data[(opcode & 7) + 16];
    const R = v1 * v2 << 1;
    cpu.dataView.setUint16(0, R, true);
    cpu.data[95] = cpu.data[95] & 0xfc | (0xffff & R ? 0 : 2) | (v1 * v2 & 0x8000 ? 1 : 0);
    cpu.cycles++;
  } else if ((opcode & 0xff88) === 0x380) {
    /* FMULS, 0000 0011 1ddd 0rrr */
    const v1 = cpu.dataView.getInt8(((opcode & 0x70) >> 4) + 16);
    const v2 = cpu.dataView.getInt8((opcode & 7) + 16);
    const R = v1 * v2 << 1;
    cpu.dataView.setInt16(0, R, true);
    cpu.data[95] = cpu.data[95] & 0xfc | (0xffff & R ? 0 : 2) | (v1 * v2 & 0x8000 ? 1 : 0);
    cpu.cycles++;
  } else if ((opcode & 0xff88) === 0x388) {
    /* FMULSU, 0000 0011 1ddd 1rrr */
    const v1 = cpu.dataView.getInt8(((opcode & 0x70) >> 4) + 16);
    const v2 = cpu.data[(opcode & 7) + 16];
    const R = v1 * v2 << 1;
    cpu.dataView.setInt16(0, R, true);
    cpu.data[95] = cpu.data[95] & 0xfc | (0xffff & R ? 2 : 0) | (v1 * v2 & 0x8000 ? 1 : 0);
    cpu.cycles++;
  } else if (opcode === 0x9509) {
    /* ICALL, 1001 0101 0000 1001 */
    const retAddr = cpu.pc + 1;
    const sp = cpu.dataView.getUint16(93, true);
    const {
      pc22Bits
    } = cpu;
    cpu.data[sp] = retAddr & 255;
    cpu.data[sp - 1] = retAddr >> 8 & 255;

    if (pc22Bits) {
      cpu.data[sp - 2] = retAddr >> 16 & 255;
    }

    cpu.dataView.setUint16(93, sp - (pc22Bits ? 3 : 2), true);
    cpu.pc = cpu.dataView.getUint16(30, true) - 1;
    cpu.cycles += pc22Bits ? 3 : 2;
  } else if (opcode === 0x9409) {
    /* IJMP, 1001 0100 0000 1001 */
    cpu.pc = cpu.dataView.getUint16(30, true) - 1;
    cpu.cycles++;
  } else if ((opcode & 0xf800) === 0xb000) {
    /* IN, 1011 0AAd dddd AAAA */
    const i = cpu.readData((opcode & 0xf | (opcode & 0x600) >> 5) + 32);
    cpu.data[(opcode & 0x1f0) >> 4] = i;
  } else if ((opcode & 0xfe0f) === 0x9403) {
    /* INC, 1001 010d dddd 0011 */
    const d = cpu.data[(opcode & 0x1f0) >> 4];
    const r = d + 1 & 255;
    cpu.data[(opcode & 0x1f0) >> 4] = r;
    let sreg = cpu.data[95] & 0xe1;
    sreg |= r ? 0 : 2;
    sreg |= 128 & r ? 4 : 0;
    sreg |= 127 === d ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfe0e) === 0x940c) {
    /* JMP, 1001 010k kkkk 110k kkkk kkkk kkkk kkkk */
    cpu.pc = (cpu.progMem[cpu.pc + 1] | (opcode & 1) << 16 | (opcode & 0x1f0) << 13) - 1;
    cpu.cycles += 2;
  } else if ((opcode & 0xfe0f) === 0x9206) {
    /* LAC, 1001 001r rrrr 0110 */
    const r = (opcode & 0x1f0) >> 4;
    const clear = cpu.data[r];
    const value = cpu.readData(cpu.dataView.getUint16(30, true));
    cpu.writeData(cpu.dataView.getUint16(30, true), value & 255 - clear);
    cpu.data[r] = value;
  } else if ((opcode & 0xfe0f) === 0x9205) {
    /* LAS, 1001 001r rrrr 0101 */
    const r = (opcode & 0x1f0) >> 4;
    const set = cpu.data[r];
    const value = cpu.readData(cpu.dataView.getUint16(30, true));
    cpu.writeData(cpu.dataView.getUint16(30, true), value | set);
    cpu.data[r] = value;
  } else if ((opcode & 0xfe0f) === 0x9207) {
    /* LAT, 1001 001r rrrr 0111 */
    const r = cpu.data[(opcode & 0x1f0) >> 4];
    const R = cpu.readData(cpu.dataView.getUint16(30, true));
    cpu.writeData(cpu.dataView.getUint16(30, true), r ^ R);
    cpu.data[(opcode & 0x1f0) >> 4] = R;
  } else if ((opcode & 0xf000) === 0xe000) {
    /* LDI, 1110 KKKK dddd KKKK */
    cpu.data[((opcode & 0xf0) >> 4) + 16] = opcode & 0xf | (opcode & 0xf00) >> 4;
  } else if ((opcode & 0xfe0f) === 0x9000) {
    /* LDS, 1001 000d dddd 0000 kkkk kkkk kkkk kkkk */
    cpu.cycles++;
    const value = cpu.readData(cpu.progMem[cpu.pc + 1]);
    cpu.data[(opcode & 0x1f0) >> 4] = value;
    cpu.pc++;
  } else if ((opcode & 0xfe0f) === 0x900c) {
    /* LDX, 1001 000d dddd 1100 */
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(cpu.dataView.getUint16(26, true));
  } else if ((opcode & 0xfe0f) === 0x900d) {
    /* LDX(INC), 1001 000d dddd 1101 */
    const x = cpu.dataView.getUint16(26, true);
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(x);
    cpu.dataView.setUint16(26, x + 1, true);
  } else if ((opcode & 0xfe0f) === 0x900e) {
    /* LDX(DEC), 1001 000d dddd 1110 */
    const x = cpu.dataView.getUint16(26, true) - 1;
    cpu.dataView.setUint16(26, x, true);
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(x);
  } else if ((opcode & 0xfe0f) === 0x8008) {
    /* LDY, 1000 000d dddd 1000 */
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(cpu.dataView.getUint16(28, true));
  } else if ((opcode & 0xfe0f) === 0x9009) {
    /* LDY(INC), 1001 000d dddd 1001 */
    const y = cpu.dataView.getUint16(28, true);
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(y);
    cpu.dataView.setUint16(28, y + 1, true);
  } else if ((opcode & 0xfe0f) === 0x900a) {
    /* LDY(DEC), 1001 000d dddd 1010 */
    const y = cpu.dataView.getUint16(28, true) - 1;
    cpu.dataView.setUint16(28, y, true);
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(y);
  } else if ((opcode & 0xd208) === 0x8008 && opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8) {
    /* LDDY, 10q0 qq0d dddd 1qqq */
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(cpu.dataView.getUint16(28, true) + (opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8));
  } else if ((opcode & 0xfe0f) === 0x8000) {
    /* LDZ, 1000 000d dddd 0000 */
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(cpu.dataView.getUint16(30, true));
  } else if ((opcode & 0xfe0f) === 0x9001) {
    /* LDZ(INC), 1001 000d dddd 0001 */
    const z = cpu.dataView.getUint16(30, true);
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(z);
    cpu.dataView.setUint16(30, z + 1, true);
  } else if ((opcode & 0xfe0f) === 0x9002) {
    /* LDZ(DEC), 1001 000d dddd 0010 */
    const z = cpu.dataView.getUint16(30, true) - 1;
    cpu.dataView.setUint16(30, z, true);
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(z);
  } else if ((opcode & 0xd208) === 0x8000 && opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8) {
    /* LDDZ, 10q0 qq0d dddd 0qqq */
    cpu.cycles++;
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.readData(cpu.dataView.getUint16(30, true) + (opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8));
  } else if (opcode === 0x95c8) {
    /* LPM, 1001 0101 1100 1000 */
    cpu.data[0] = cpu.progBytes[cpu.dataView.getUint16(30, true)];
    cpu.cycles += 2;
  } else if ((opcode & 0xfe0f) === 0x9004) {
    /* LPM(REG), 1001 000d dddd 0100 */
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.progBytes[cpu.dataView.getUint16(30, true)];
    cpu.cycles += 2;
  } else if ((opcode & 0xfe0f) === 0x9005) {
    /* LPM(INC), 1001 000d dddd 0101 */
    const i = cpu.dataView.getUint16(30, true);
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.progBytes[i];
    cpu.dataView.setUint16(30, i + 1, true);
    cpu.cycles += 2;
  } else if ((opcode & 0xfe0f) === 0x9406) {
    /* LSR, 1001 010d dddd 0110 */
    const value = cpu.data[(opcode & 0x1f0) >> 4];
    const R = value >>> 1;
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xe0;
    sreg |= R ? 0 : 2;
    sreg |= value & 1;
    sreg |= sreg >> 2 & 1 ^ sreg & 1 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfc00) === 0x2c00) {
    /* MOV, 0010 11rd dddd rrrr */
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
  } else if ((opcode & 0xff00) === 0x100) {
    /* MOVW, 0000 0001 dddd rrrr */
    const r2 = 2 * (opcode & 0xf);
    const d2 = 2 * ((opcode & 0xf0) >> 4);
    cpu.data[d2] = cpu.data[r2];
    cpu.data[d2 + 1] = cpu.data[r2 + 1];
  } else if ((opcode & 0xfc00) === 0x9c00) {
    /* MUL, 1001 11rd dddd rrrr */
    const R = cpu.data[(opcode & 0x1f0) >> 4] * cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    cpu.dataView.setUint16(0, R, true);
    cpu.data[95] = cpu.data[95] & 0xfc | (0xffff & R ? 0 : 2) | (0x8000 & R ? 1 : 0);
    cpu.cycles++;
  } else if ((opcode & 0xff00) === 0x200) {
    /* MULS, 0000 0010 dddd rrrr */
    const R = cpu.dataView.getInt8(((opcode & 0xf0) >> 4) + 16) * cpu.dataView.getInt8((opcode & 0xf) + 16);
    cpu.dataView.setInt16(0, R, true);
    cpu.data[95] = cpu.data[95] & 0xfc | (0xffff & R ? 0 : 2) | (0x8000 & R ? 1 : 0);
    cpu.cycles++;
  } else if ((opcode & 0xff88) === 0x300) {
    /* MULSU, 0000 0011 0ddd 0rrr */
    const R = cpu.dataView.getInt8(((opcode & 0x70) >> 4) + 16) * cpu.data[(opcode & 7) + 16];
    cpu.dataView.setInt16(0, R, true);
    cpu.data[95] = cpu.data[95] & 0xfc | (0xffff & R ? 0 : 2) | (0x8000 & R ? 1 : 0);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x9401) {
    /* NEG, 1001 010d dddd 0001 */
    const d = (opcode & 0x1f0) >> 4;
    const value = cpu.data[d];
    const R = 0 - value;
    cpu.data[d] = R;
    let sreg = cpu.data[95] & 0xc0;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= 128 === R ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= R ? 1 : 0;
    sreg |= 1 & (R | value) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if (opcode === 0) {
    /* NOP, 0000 0000 0000 0000 */

    /* NOP */
  } else if ((opcode & 0xfc00) === 0x2800) {
    /* OR, 0010 10rd dddd rrrr */
    const R = cpu.data[(opcode & 0x1f0) >> 4] | cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xe1;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xf000) === 0x6000) {
    /* SBR, 0110 KKKK dddd KKKK */
    const R = cpu.data[((opcode & 0xf0) >> 4) + 16] | (opcode & 0xf | (opcode & 0xf00) >> 4);
    cpu.data[((opcode & 0xf0) >> 4) + 16] = R;
    let sreg = cpu.data[95] & 0xe1;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xf800) === 0xb800) {
    /* OUT, 1011 1AAr rrrr AAAA */
    cpu.writeData((opcode & 0xf | (opcode & 0x600) >> 5) + 32, cpu.data[(opcode & 0x1f0) >> 4]);
  } else if ((opcode & 0xfe0f) === 0x900f) {
    /* POP, 1001 000d dddd 1111 */
    const value = cpu.dataView.getUint16(93, true) + 1;
    cpu.dataView.setUint16(93, value, true);
    cpu.data[(opcode & 0x1f0) >> 4] = cpu.data[value];
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x920f) {
    /* PUSH, 1001 001d dddd 1111 */
    const value = cpu.dataView.getUint16(93, true);
    cpu.data[value] = cpu.data[(opcode & 0x1f0) >> 4];
    cpu.dataView.setUint16(93, value - 1, true);
    cpu.cycles++;
  } else if ((opcode & 0xf000) === 0xd000) {
    /* RCALL, 1101 kkkk kkkk kkkk */
    const k = (opcode & 0x7ff) - (opcode & 0x800 ? 0x800 : 0);
    const retAddr = cpu.pc + 1;
    const sp = cpu.dataView.getUint16(93, true);
    const {
      pc22Bits
    } = cpu;
    cpu.data[sp] = 255 & retAddr;
    cpu.data[sp - 1] = retAddr >> 8 & 255;

    if (pc22Bits) {
      cpu.data[sp - 2] = retAddr >> 16 & 255;
    }

    cpu.dataView.setUint16(93, sp - (pc22Bits ? 3 : 2), true);
    cpu.pc += k;
    cpu.cycles += pc22Bits ? 3 : 2;
  } else if (opcode === 0x9508) {
    /* RET, 1001 0101 0000 1000 */
    const {
      pc22Bits
    } = cpu;
    const i = cpu.dataView.getUint16(93, true) + (pc22Bits ? 3 : 2);
    cpu.dataView.setUint16(93, i, true);
    cpu.pc = (cpu.data[i - 1] << 8) + cpu.data[i] - 1;

    if (pc22Bits) {
      cpu.pc |= cpu.data[i - 2] << 16;
    }

    cpu.cycles += pc22Bits ? 4 : 3;
  } else if (opcode === 0x9518) {
    /* RETI, 1001 0101 0001 1000 */
    const {
      pc22Bits
    } = cpu;
    const i = cpu.dataView.getUint16(93, true) + (pc22Bits ? 3 : 2);
    cpu.dataView.setUint16(93, i, true);
    cpu.pc = (cpu.data[i - 1] << 8) + cpu.data[i] - 1;

    if (pc22Bits) {
      cpu.pc |= cpu.data[i - 2] << 16;
    }

    cpu.cycles += pc22Bits ? 4 : 3;
    cpu.data[95] |= 0x80; // Enable interrupts
  } else if ((opcode & 0xf000) === 0xc000) {
    /* RJMP, 1100 kkkk kkkk kkkk */
    cpu.pc = cpu.pc + ((opcode & 0x7ff) - (opcode & 0x800 ? 0x800 : 0));
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x9407) {
    /* ROR, 1001 010d dddd 0111 */
    const d = cpu.data[(opcode & 0x1f0) >> 4];
    const r = d >>> 1 | (cpu.data[95] & 1) << 7;
    cpu.data[(opcode & 0x1f0) >> 4] = r;
    let sreg = cpu.data[95] & 0xe0;
    sreg |= r ? 0 : 2;
    sreg |= 128 & r ? 4 : 0;
    sreg |= 1 & d ? 1 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg & 1 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfc00) === 0x800) {
    /* SBC, 0000 10rd dddd rrrr */
    const val1 = cpu.data[(opcode & 0x1f0) >> 4];
    const val2 = cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    let sreg = cpu.data[95];
    const R = val1 - val2 - (sreg & 1);
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    sreg = sreg & 0xc0 | (!R && sreg >> 1 & 1 ? 2 : 0) | (val2 + (sreg & 1) > val1 ? 1 : 0);
    sreg |= 128 & R ? 4 : 0;
    sreg |= (val1 ^ val2) & (val1 ^ R) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= 1 & (~val1 & val2 | val2 & R | R & ~val1) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xf000) === 0x4000) {
    /* SBCI, 0100 KKKK dddd KKKK */
    const val1 = cpu.data[((opcode & 0xf0) >> 4) + 16];
    const val2 = opcode & 0xf | (opcode & 0xf00) >> 4;
    let sreg = cpu.data[95];
    const R = val1 - val2 - (sreg & 1);
    cpu.data[((opcode & 0xf0) >> 4) + 16] = R;
    sreg = sreg & 0xc0 | (!R && sreg >> 1 & 1 ? 2 : 0) | (val2 + (sreg & 1) > val1 ? 1 : 0);
    sreg |= 128 & R ? 4 : 0;
    sreg |= (val1 ^ val2) & (val1 ^ R) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= 1 & (~val1 & val2 | val2 & R | R & ~val1) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xff00) === 0x9a00) {
    /* SBI, 1001 1010 AAAA Abbb */
    const target = ((opcode & 0xf8) >> 3) + 32;
    cpu.writeData(target, cpu.readData(target) | 1 << (opcode & 7));
    cpu.cycles++;
  } else if ((opcode & 0xff00) === 0x9900) {
    /* SBIC, 1001 1001 AAAA Abbb */
    const value = cpu.readData(((opcode & 0xf8) >> 3) + 32);

    if (!(value & 1 << (opcode & 7))) {
      const nextOpcode = cpu.progMem[cpu.pc + 1];
      const skipSize = isTwoWordInstruction(nextOpcode) ? 2 : 1;
      cpu.cycles += skipSize;
      cpu.pc += skipSize;
    }
  } else if ((opcode & 0xff00) === 0x9b00) {
    /* SBIS, 1001 1011 AAAA Abbb */
    const value = cpu.readData(((opcode & 0xf8) >> 3) + 32);

    if (value & 1 << (opcode & 7)) {
      const nextOpcode = cpu.progMem[cpu.pc + 1];
      const skipSize = isTwoWordInstruction(nextOpcode) ? 2 : 1;
      cpu.cycles += skipSize;
      cpu.pc += skipSize;
    }
  } else if ((opcode & 0xff00) === 0x9700) {
    /* SBIW, 1001 0111 KKdd KKKK */
    const i = 2 * ((opcode & 0x30) >> 4) + 24;
    const a = cpu.dataView.getUint16(i, true);
    const l = opcode & 0xf | (opcode & 0xc0) >> 2;
    const R = a - l;
    cpu.dataView.setUint16(i, R, true);
    let sreg = cpu.data[95] & 0xc0;
    sreg |= R ? 0 : 2;
    sreg |= 0x8000 & R ? 4 : 0;
    sreg |= a & ~R & 0x8000 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= l > a ? 1 : 0;
    sreg |= 1 & (~a & l | l & R | R & ~a) ? 0x20 : 0;
    cpu.data[95] = sreg;
    cpu.cycles++;
  } else if ((opcode & 0xfe08) === 0xfc00) {
    /* SBRC, 1111 110r rrrr 0bbb */
    if (!(cpu.data[(opcode & 0x1f0) >> 4] & 1 << (opcode & 7))) {
      const nextOpcode = cpu.progMem[cpu.pc + 1];
      const skipSize = isTwoWordInstruction(nextOpcode) ? 2 : 1;
      cpu.cycles += skipSize;
      cpu.pc += skipSize;
    }
  } else if ((opcode & 0xfe08) === 0xfe00) {
    /* SBRS, 1111 111r rrrr 0bbb */
    if (cpu.data[(opcode & 0x1f0) >> 4] & 1 << (opcode & 7)) {
      const nextOpcode = cpu.progMem[cpu.pc + 1];
      const skipSize = isTwoWordInstruction(nextOpcode) ? 2 : 1;
      cpu.cycles += skipSize;
      cpu.pc += skipSize;
    }
  } else if (opcode === 0x9588) {
    /* SLEEP, 1001 0101 1000 1000 */

    /* not implemented */
  } else if (opcode === 0x95e8) {
    /* SPM, 1001 0101 1110 1000 */

    /* not implemented */
  } else if (opcode === 0x95f8) {
    /* SPM(INC), 1001 0101 1111 1000 */

    /* not implemented */
  } else if ((opcode & 0xfe0f) === 0x9200) {
    /* STS, 1001 001d dddd 0000 kkkk kkkk kkkk kkkk */
    const value = cpu.data[(opcode & 0x1f0) >> 4];
    const addr = cpu.progMem[cpu.pc + 1];
    cpu.writeData(addr, value);
    cpu.pc++;
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x920c) {
    /* STX, 1001 001r rrrr 1100 */
    cpu.writeData(cpu.dataView.getUint16(26, true), cpu.data[(opcode & 0x1f0) >> 4]);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x920d) {
    /* STX(INC), 1001 001r rrrr 1101 */
    const x = cpu.dataView.getUint16(26, true);
    cpu.writeData(x, cpu.data[(opcode & 0x1f0) >> 4]);
    cpu.dataView.setUint16(26, x + 1, true);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x920e) {
    /* STX(DEC), 1001 001r rrrr 1110 */
    const i = cpu.data[(opcode & 0x1f0) >> 4];
    const x = cpu.dataView.getUint16(26, true) - 1;
    cpu.dataView.setUint16(26, x, true);
    cpu.writeData(x, i);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x8208) {
    /* STY, 1000 001r rrrr 1000 */
    cpu.writeData(cpu.dataView.getUint16(28, true), cpu.data[(opcode & 0x1f0) >> 4]);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x9209) {
    /* STY(INC), 1001 001r rrrr 1001 */
    const i = cpu.data[(opcode & 0x1f0) >> 4];
    const y = cpu.dataView.getUint16(28, true);
    cpu.writeData(y, i);
    cpu.dataView.setUint16(28, y + 1, true);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x920a) {
    /* STY(DEC), 1001 001r rrrr 1010 */
    const i = cpu.data[(opcode & 0x1f0) >> 4];
    const y = cpu.dataView.getUint16(28, true) - 1;
    cpu.dataView.setUint16(28, y, true);
    cpu.writeData(y, i);
    cpu.cycles++;
  } else if ((opcode & 0xd208) === 0x8208 && opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8) {
    /* STDY, 10q0 qq1r rrrr 1qqq */
    cpu.writeData(cpu.dataView.getUint16(28, true) + (opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8), cpu.data[(opcode & 0x1f0) >> 4]);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x8200) {
    /* STZ, 1000 001r rrrr 0000 */
    cpu.writeData(cpu.dataView.getUint16(30, true), cpu.data[(opcode & 0x1f0) >> 4]);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x9201) {
    /* STZ(INC), 1001 001r rrrr 0001 */
    const z = cpu.dataView.getUint16(30, true);
    cpu.writeData(z, cpu.data[(opcode & 0x1f0) >> 4]);
    cpu.dataView.setUint16(30, z + 1, true);
    cpu.cycles++;
  } else if ((opcode & 0xfe0f) === 0x9202) {
    /* STZ(DEC), 1001 001r rrrr 0010 */
    const i = cpu.data[(opcode & 0x1f0) >> 4];
    const z = cpu.dataView.getUint16(30, true) - 1;
    cpu.dataView.setUint16(30, z, true);
    cpu.writeData(z, i);
    cpu.cycles++;
  } else if ((opcode & 0xd208) === 0x8200 && opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8) {
    /* STDZ, 10q0 qq1r rrrr 0qqq */
    cpu.writeData(cpu.dataView.getUint16(30, true) + (opcode & 7 | (opcode & 0xc00) >> 7 | (opcode & 0x2000) >> 8), cpu.data[(opcode & 0x1f0) >> 4]);
    cpu.cycles++;
  } else if ((opcode & 0xfc00) === 0x1800) {
    /* SUB, 0001 10rd dddd rrrr */
    const val1 = cpu.data[(opcode & 0x1f0) >> 4];
    const val2 = cpu.data[opcode & 0xf | (opcode & 0x200) >> 5];
    const R = val1 - val2;
    cpu.data[(opcode & 0x1f0) >> 4] = R;
    let sreg = cpu.data[95] & 0xc0;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= (val1 ^ val2) & (val1 ^ R) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= val2 > val1 ? 1 : 0;
    sreg |= 1 & (~val1 & val2 | val2 & R | R & ~val1) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xf000) === 0x5000) {
    /* SUBI, 0101 KKKK dddd KKKK */
    const val1 = cpu.data[((opcode & 0xf0) >> 4) + 16];
    const val2 = opcode & 0xf | (opcode & 0xf00) >> 4;
    const R = val1 - val2;
    cpu.data[((opcode & 0xf0) >> 4) + 16] = R;
    let sreg = cpu.data[95] & 0xc0;
    sreg |= R ? 0 : 2;
    sreg |= 128 & R ? 4 : 0;
    sreg |= (val1 ^ val2) & (val1 ^ R) & 128 ? 8 : 0;
    sreg |= sreg >> 2 & 1 ^ sreg >> 3 & 1 ? 0x10 : 0;
    sreg |= val2 > val1 ? 1 : 0;
    sreg |= 1 & (~val1 & val2 | val2 & R | R & ~val1) ? 0x20 : 0;
    cpu.data[95] = sreg;
  } else if ((opcode & 0xfe0f) === 0x9402) {
    /* SWAP, 1001 010d dddd 0010 */
    const d = (opcode & 0x1f0) >> 4;
    const i = cpu.data[d];
    cpu.data[d] = (15 & i) << 4 | (240 & i) >>> 4;
  } else if (opcode === 0x95a8) {
    /* WDR, 1001 0101 1010 1000 */

    /* not implemented */
  } else if ((opcode & 0xfe0f) === 0x9204) {
    /* XCH, 1001 001r rrrr 0100 */
    const r = (opcode & 0x1f0) >> 4;
    const val1 = cpu.data[r];
    const val2 = cpu.data[cpu.dataView.getUint16(30, true)];
    cpu.data[cpu.dataView.getUint16(30, true)] = val1;
    cpu.data[r] = val2;
  }

  cpu.pc = (cpu.pc + 1) % cpu.progMem.length;
  cpu.cycles++;
}
},{}],"../../src/peripherals/gpio.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVRIOPort = exports.PinOverrideMode = exports.PinState = exports.portLConfig = exports.portKConfig = exports.portJConfig = exports.portHConfig = exports.portGConfig = exports.portFConfig = exports.portEConfig = exports.portDConfig = exports.portCConfig = exports.portBConfig = exports.portAConfig = void 0;
const portAConfig = {
  PIN: 0x20,
  DDR: 0x21,
  PORT: 0x22
};
exports.portAConfig = portAConfig;
const portBConfig = {
  PIN: 0x23,
  DDR: 0x24,
  PORT: 0x25
};
exports.portBConfig = portBConfig;
const portCConfig = {
  PIN: 0x26,
  DDR: 0x27,
  PORT: 0x28
};
exports.portCConfig = portCConfig;
const portDConfig = {
  PIN: 0x29,
  DDR: 0x2a,
  PORT: 0x2b
};
exports.portDConfig = portDConfig;
const portEConfig = {
  PIN: 0x2c,
  DDR: 0x2d,
  PORT: 0x2e
};
exports.portEConfig = portEConfig;
const portFConfig = {
  PIN: 0x2f,
  DDR: 0x30,
  PORT: 0x31
};
exports.portFConfig = portFConfig;
const portGConfig = {
  PIN: 0x32,
  DDR: 0x33,
  PORT: 0x34
};
exports.portGConfig = portGConfig;
const portHConfig = {
  PIN: 0x100,
  DDR: 0x101,
  PORT: 0x102
};
exports.portHConfig = portHConfig;
const portJConfig = {
  PIN: 0x103,
  DDR: 0x104,
  PORT: 0x105
};
exports.portJConfig = portJConfig;
const portKConfig = {
  PIN: 0x106,
  DDR: 0x107,
  PORT: 0x108
};
exports.portKConfig = portKConfig;
const portLConfig = {
  PIN: 0x109,
  DDR: 0x10a,
  PORT: 0x10b
};
exports.portLConfig = portLConfig;
var PinState;
exports.PinState = PinState;

(function (PinState) {
  PinState[PinState["Low"] = 0] = "Low";
  PinState[PinState["High"] = 1] = "High";
  PinState[PinState["Input"] = 2] = "Input";
  PinState[PinState["InputPullUp"] = 3] = "InputPullUp";
})(PinState || (exports.PinState = PinState = {}));
/* This mechanism allows timers to override specific GPIO pins */


var PinOverrideMode;
exports.PinOverrideMode = PinOverrideMode;

(function (PinOverrideMode) {
  PinOverrideMode[PinOverrideMode["None"] = 0] = "None";
  PinOverrideMode[PinOverrideMode["Enable"] = 1] = "Enable";
  PinOverrideMode[PinOverrideMode["Set"] = 2] = "Set";
  PinOverrideMode[PinOverrideMode["Clear"] = 3] = "Clear";
  PinOverrideMode[PinOverrideMode["Toggle"] = 4] = "Toggle";
})(PinOverrideMode || (exports.PinOverrideMode = PinOverrideMode = {}));

class AVRIOPort {
  constructor(cpu, portConfig) {
    this.cpu = cpu;
    this.portConfig = portConfig;
    this.listeners = [];
    this.pinValue = 0;
    this.overrideMask = 0xff;
    this.lastValue = 0;
    this.lastDdr = 0;

    cpu.writeHooks[portConfig.DDR] = value => {
      const portValue = cpu.data[portConfig.PORT];
      cpu.data[portConfig.DDR] = value;
      this.updatePinRegister(portValue, value);
      this.writeGpio(portValue, value);
      return true;
    };

    cpu.writeHooks[portConfig.PORT] = value => {
      const ddrMask = cpu.data[portConfig.DDR];
      cpu.data[portConfig.PORT] = value;
      this.updatePinRegister(value, ddrMask);
      this.writeGpio(value, ddrMask);
      return true;
    };

    cpu.writeHooks[portConfig.PIN] = value => {
      // Writing to 1 PIN toggles PORT bits
      const oldPortValue = cpu.data[portConfig.PORT];
      const ddrMask = cpu.data[portConfig.DDR];
      const portValue = oldPortValue ^ value;
      cpu.data[portConfig.PORT] = portValue;
      cpu.data[portConfig.PIN] = cpu.data[portConfig.PIN] & ~ddrMask | portValue & ddrMask;
      this.writeGpio(portValue, ddrMask);
      return true;
    }; // The following hook is used by the timer compare output to override GPIO pins:


    cpu.gpioTimerHooks[portConfig.PORT] = (pin, mode) => {
      const pinMask = 1 << pin;

      if (mode == PinOverrideMode.None) {
        this.overrideMask |= pinMask;
      } else {
        this.overrideMask &= ~pinMask;

        switch (mode) {
          case PinOverrideMode.Enable:
            this.overrideValue &= ~pinMask;
            this.overrideValue |= cpu.data[portConfig.PORT] & pinMask;
            break;

          case PinOverrideMode.Set:
            this.overrideValue |= pinMask;
            break;

          case PinOverrideMode.Clear:
            this.overrideValue &= ~pinMask;
            break;

          case PinOverrideMode.Toggle:
            this.overrideValue ^= pinMask;
            break;
        }
      }

      this.writeGpio(cpu.data[portConfig.PORT], cpu.data[portConfig.DDR]);
    };
  }

  addListener(listener) {
    this.listeners.push(listener);
  }

  removeListener(listener) {
    this.listeners = this.listeners.filter(l => l !== listener);
  }
  /**
   * Get the state of a given GPIO pin
   *
   * @param index Pin index to return from 0 to 7
   * @returns PinState.Low or PinState.High if the pin is set to output, PinState.Input if the pin is set
   *   to input, and PinState.InputPullUp if the pin is set to input and the internal pull-up resistor has
   *   been enabled.
   */


  pinState(index) {
    const ddr = this.cpu.data[this.portConfig.DDR];
    const port = this.cpu.data[this.portConfig.PORT];
    const bitMask = 1 << index;

    if (ddr & bitMask) {
      return this.lastValue & bitMask ? PinState.High : PinState.Low;
    } else {
      return port & bitMask ? PinState.InputPullUp : PinState.Input;
    }
  }
  /**
   * Sets the input value for the given pin. This is the value that
   * will be returned when reading from the PIN register.
   */


  setPin(index, value) {
    const bitMask = 1 << index;
    this.pinValue &= ~bitMask;

    if (value) {
      this.pinValue |= bitMask;
    }

    this.updatePinRegister(this.cpu.data[this.portConfig.PORT], this.cpu.data[this.portConfig.DDR]);
  }

  updatePinRegister(port, ddr) {
    this.cpu.data[this.portConfig.PIN] = this.pinValue & ~ddr | port & ddr;
  }

  writeGpio(value, ddr) {
    const newValue = (value & this.overrideMask | this.overrideValue) & ddr | value & ~ddr;
    const prevValue = this.lastValue;

    if (newValue !== prevValue || ddr !== this.lastDdr) {
      this.lastValue = newValue;
      this.lastDdr = ddr;

      for (const listener of this.listeners) {
        listener(newValue, prevValue);
      }
    }
  }

}

exports.AVRIOPort = AVRIOPort;
},{}],"../../src/peripherals/timer.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVRTimer = exports.timer2Config = exports.timer1Config = exports.timer0Config = void 0;

var _gpio = require("./gpio");

/**
 * AVR-8 Timers
 * Part of AVR8js
 * Reference: http://ww1.microchip.com/downloads/en/DeviceDoc/ATmega48A-PA-88A-PA-168A-PA-328-P-DS-DS40002061A.pdf
 *
 * Copyright (C) 2019, 2020, Uri Shaked
 */
const timer01Dividers = {
  0: 0,
  1: 1,
  2: 8,
  3: 64,
  4: 256,
  5: 1024,
  6: 0,
  7: 0
};
/** These are differnet for some devices (e.g. ATtiny85) */

const defaultTimerBits = {
  // TIFR bits
  TOV: 1,
  OCFA: 2,
  OCFB: 4,
  // TIMSK bits
  TOIE: 1,
  OCIEA: 2,
  OCIEB: 4
};
const timer0Config = Object.assign({
  bits: 8,
  captureInterrupt: 0,
  compAInterrupt: 0x1c,
  compBInterrupt: 0x1e,
  ovfInterrupt: 0x20,
  TIFR: 0x35,
  OCRA: 0x47,
  OCRB: 0x48,
  ICR: 0,
  TCNT: 0x46,
  TCCRA: 0x44,
  TCCRB: 0x45,
  TCCRC: 0,
  TIMSK: 0x6e,
  dividers: timer01Dividers,
  compPortA: _gpio.portDConfig.PORT,
  compPinA: 6,
  compPortB: _gpio.portDConfig.PORT,
  compPinB: 5
}, defaultTimerBits);
exports.timer0Config = timer0Config;
const timer1Config = Object.assign({
  bits: 16,
  captureInterrupt: 0x14,
  compAInterrupt: 0x16,
  compBInterrupt: 0x18,
  ovfInterrupt: 0x1a,
  TIFR: 0x36,
  OCRA: 0x88,
  OCRB: 0x8a,
  ICR: 0x86,
  TCNT: 0x84,
  TCCRA: 0x80,
  TCCRB: 0x81,
  TCCRC: 0x82,
  TIMSK: 0x6f,
  dividers: timer01Dividers,
  compPortA: _gpio.portBConfig.PORT,
  compPinA: 1,
  compPortB: _gpio.portBConfig.PORT,
  compPinB: 2
}, defaultTimerBits);
exports.timer1Config = timer1Config;
const timer2Config = Object.assign({
  bits: 8,
  captureInterrupt: 0,
  compAInterrupt: 0x0e,
  compBInterrupt: 0x10,
  ovfInterrupt: 0x12,
  TIFR: 0x37,
  OCRA: 0xb3,
  OCRB: 0xb4,
  ICR: 0,
  TCNT: 0xb2,
  TCCRA: 0xb0,
  TCCRB: 0xb1,
  TCCRC: 0,
  TIMSK: 0x70,
  dividers: {
    0: 0,
    1: 1,
    2: 8,
    3: 32,
    4: 64,
    5: 128,
    6: 256,
    7: 1024
  },
  compPortA: _gpio.portBConfig.PORT,
  compPinA: 3,
  compPortB: _gpio.portDConfig.PORT,
  compPinB: 3
}, defaultTimerBits);
/* All the following types and constants are related to WGM (Waveform Generation Mode) bits: */

exports.timer2Config = timer2Config;
var TimerMode;

(function (TimerMode) {
  TimerMode[TimerMode["Normal"] = 0] = "Normal";
  TimerMode[TimerMode["PWMPhaseCorrect"] = 1] = "PWMPhaseCorrect";
  TimerMode[TimerMode["CTC"] = 2] = "CTC";
  TimerMode[TimerMode["FastPWM"] = 3] = "FastPWM";
  TimerMode[TimerMode["PWMPhaseFrequencyCorrect"] = 4] = "PWMPhaseFrequencyCorrect";
  TimerMode[TimerMode["Reserved"] = 5] = "Reserved";
})(TimerMode || (TimerMode = {}));

var TOVUpdateMode;

(function (TOVUpdateMode) {
  TOVUpdateMode[TOVUpdateMode["Max"] = 0] = "Max";
  TOVUpdateMode[TOVUpdateMode["Top"] = 1] = "Top";
  TOVUpdateMode[TOVUpdateMode["Bottom"] = 2] = "Bottom";
})(TOVUpdateMode || (TOVUpdateMode = {}));

var OCRUpdateMode;

(function (OCRUpdateMode) {
  OCRUpdateMode[OCRUpdateMode["Immediate"] = 0] = "Immediate";
  OCRUpdateMode[OCRUpdateMode["Top"] = 1] = "Top";
  OCRUpdateMode[OCRUpdateMode["Bottom"] = 2] = "Bottom";
})(OCRUpdateMode || (OCRUpdateMode = {}));

const TopOCRA = 1;
const TopICR = 2; // Enable Toggle mode for OCxA in PWM Wave Generation mode

const OCToggle = 1;
const {
  Normal,
  PWMPhaseCorrect,
  CTC,
  FastPWM,
  Reserved,
  PWMPhaseFrequencyCorrect
} = TimerMode;
const wgmModes8Bit = [
/*0*/
[Normal, 0xff, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*1*/
[PWMPhaseCorrect, 0xff, OCRUpdateMode.Top, TOVUpdateMode.Bottom, 0],
/*2*/
[CTC, TopOCRA, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*3*/
[FastPWM, 0xff, OCRUpdateMode.Bottom, TOVUpdateMode.Max, 0],
/*4*/
[Reserved, 0xff, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*5*/
[PWMPhaseCorrect, TopOCRA, OCRUpdateMode.Top, TOVUpdateMode.Bottom, OCToggle],
/*6*/
[Reserved, 0xff, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*7*/
[FastPWM, TopOCRA, OCRUpdateMode.Bottom, TOVUpdateMode.Top, OCToggle]]; // Table 16-4 in the datasheet

const wgmModes16Bit = [
/*0 */
[Normal, 0xffff, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*1 */
[PWMPhaseCorrect, 0x00ff, OCRUpdateMode.Top, TOVUpdateMode.Bottom, 0],
/*2 */
[PWMPhaseCorrect, 0x01ff, OCRUpdateMode.Top, TOVUpdateMode.Bottom, 0],
/*3 */
[PWMPhaseCorrect, 0x03ff, OCRUpdateMode.Top, TOVUpdateMode.Bottom, 0],
/*4 */
[CTC, TopOCRA, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*5 */
[FastPWM, 0x00ff, OCRUpdateMode.Bottom, TOVUpdateMode.Top, 0],
/*6 */
[FastPWM, 0x01ff, OCRUpdateMode.Bottom, TOVUpdateMode.Top, 0],
/*7 */
[FastPWM, 0x03ff, OCRUpdateMode.Bottom, TOVUpdateMode.Top, 0],
/*8 */
[PWMPhaseFrequencyCorrect, TopICR, OCRUpdateMode.Bottom, TOVUpdateMode.Bottom, 0],
/*9 */
[PWMPhaseFrequencyCorrect, TopOCRA, OCRUpdateMode.Bottom, TOVUpdateMode.Bottom, OCToggle],
/*10*/
[PWMPhaseCorrect, TopICR, OCRUpdateMode.Top, TOVUpdateMode.Bottom, 0],
/*11*/
[PWMPhaseCorrect, TopOCRA, OCRUpdateMode.Top, TOVUpdateMode.Bottom, OCToggle],
/*12*/
[CTC, TopICR, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*13*/
[Reserved, 0xffff, OCRUpdateMode.Immediate, TOVUpdateMode.Max, 0],
/*14*/
[FastPWM, TopICR, OCRUpdateMode.Bottom, TOVUpdateMode.Top, OCToggle],
/*15*/
[FastPWM, TopOCRA, OCRUpdateMode.Bottom, TOVUpdateMode.Top, OCToggle]];

function compToOverride(comp) {
  switch (comp) {
    case 1:
      return _gpio.PinOverrideMode.Toggle;

    case 2:
      return _gpio.PinOverrideMode.Clear;

    case 3:
      return _gpio.PinOverrideMode.Set;

    default:
      return _gpio.PinOverrideMode.Enable;
  }
}

class AVRTimer {
  constructor(cpu, config) {
    this.cpu = cpu;
    this.config = config;
    this.MAX = this.config.bits === 16 ? 0xffff : 0xff;
    this.lastCycle = 0;
    this.ocrA = 0;
    this.nextOcrA = 0;
    this.ocrB = 0;
    this.nextOcrB = 0;
    this.ocrUpdateMode = OCRUpdateMode.Immediate;
    this.tovUpdateMode = TOVUpdateMode.Max;
    this.icr = 0; // only for 16-bit timers

    this.tcnt = 0;
    this.tcntNext = 0;
    this.tcntUpdated = false;
    this.updateDivider = false;
    this.countingUp = true;
    this.divider = 0; // This is the temporary register used to access 16-bit registers (section 16.3 of the datasheet)

    this.highByteTemp = 0; // Interrupts

    this.OVF = {
      address: this.config.ovfInterrupt,
      flagRegister: this.config.TIFR,
      flagMask: this.config.TOV,
      enableRegister: this.config.TIMSK,
      enableMask: this.config.TOIE
    };
    this.OCFA = {
      address: this.config.compAInterrupt,
      flagRegister: this.config.TIFR,
      flagMask: this.config.OCFA,
      enableRegister: this.config.TIMSK,
      enableMask: this.config.OCIEA
    };
    this.OCFB = {
      address: this.config.compBInterrupt,
      flagRegister: this.config.TIFR,
      flagMask: this.config.OCFB,
      enableRegister: this.config.TIMSK,
      enableMask: this.config.OCIEB
    };

    this.count = (reschedule = true) => {
      const {
        divider,
        lastCycle,
        cpu
      } = this;
      const {
        cycles
      } = cpu;
      const delta = cycles - lastCycle;

      if (divider && delta >= divider) {
        const counterDelta = Math.floor(delta / divider);
        this.lastCycle += counterDelta * divider;
        const val = this.tcnt;
        const {
          timerMode,
          TOP
        } = this;
        const phasePwm = timerMode === PWMPhaseCorrect || timerMode === PWMPhaseFrequencyCorrect;
        const newVal = phasePwm ? this.phasePwmCount(val, counterDelta) : (val + counterDelta) % (TOP + 1);
        const overflow = val + counterDelta > TOP; // A CPU write overrides (has priority over) all counter clear or count operations.

        if (!this.tcntUpdated) {
          this.tcnt = newVal;

          if (!phasePwm) {
            this.timerUpdated(newVal, val);
          }
        }

        if (!phasePwm) {
          if (timerMode === FastPWM && overflow) {
            const {
              compA,
              compB
            } = this;

            if (compA) {
              this.updateCompPin(compA, 'A', true);
            }

            if (compB) {
              this.updateCompPin(compB, 'B', true);
            }
          }

          if (this.ocrUpdateMode == OCRUpdateMode.Bottom && overflow) {
            // OCRUpdateMode.Top only occurs in Phase Correct modes, handled by phasePwmCount()
            this.ocrA = this.nextOcrA;
            this.ocrB = this.nextOcrB;
          } // OCRUpdateMode.Bottom only occurs in Phase Correct modes, handled by phasePwmCount().
          // Thus we only handle TOVUpdateMode.Top or TOVUpdateMode.Max here.


          if (overflow && (this.tovUpdateMode == TOVUpdateMode.Top || TOP === this.MAX)) {
            cpu.setInterruptFlag(this.OVF);
          }
        }
      }

      if (this.tcntUpdated) {
        this.tcnt = this.tcntNext;
        this.tcntUpdated = false;
      }

      if (this.updateDivider) {
        const newDivider = this.config.dividers[this.CS];
        this.lastCycle = newDivider ? this.cpu.cycles : 0;
        this.updateDivider = false;
        this.divider = newDivider;

        if (newDivider) {
          cpu.addClockEvent(this.count, this.lastCycle + newDivider - cpu.cycles);
        }

        return;
      }

      if (reschedule && divider) {
        cpu.addClockEvent(this.count, this.lastCycle + divider - cpu.cycles);
      }
    };

    this.updateWGMConfig();

    this.cpu.readHooks[config.TCNT] = addr => {
      this.count(false);

      if (this.config.bits === 16) {
        this.cpu.data[addr + 1] = this.tcnt >> 8;
      }

      return this.cpu.data[addr] = this.tcnt & 0xff;
    };

    this.cpu.writeHooks[config.TCNT] = value => {
      this.tcntNext = this.highByteTemp << 8 | value;
      this.countingUp = true;
      this.tcntUpdated = true;
      this.cpu.updateClockEvent(this.count, 0);

      if (this.divider) {
        this.timerUpdated(this.tcntNext, this.tcntNext);
      }
    };

    this.cpu.writeHooks[config.OCRA] = value => {
      this.nextOcrA = this.highByteTemp << 8 | value;

      if (this.ocrUpdateMode === OCRUpdateMode.Immediate) {
        this.ocrA = this.nextOcrA;
      }
    };

    this.cpu.writeHooks[config.OCRB] = value => {
      this.nextOcrB = this.highByteTemp << 8 | value;

      if (this.ocrUpdateMode === OCRUpdateMode.Immediate) {
        this.ocrB = this.nextOcrB;
      }
    };

    this.cpu.writeHooks[config.ICR] = value => {
      this.icr = this.highByteTemp << 8 | value;
    };

    if (this.config.bits === 16) {
      const updateTempRegister = value => {
        this.highByteTemp = value;
      };

      this.cpu.writeHooks[config.TCNT + 1] = updateTempRegister;
      this.cpu.writeHooks[config.OCRA + 1] = updateTempRegister;
      this.cpu.writeHooks[config.OCRB + 1] = updateTempRegister;
      this.cpu.writeHooks[config.ICR + 1] = updateTempRegister;
    }

    cpu.writeHooks[config.TCCRA] = value => {
      this.cpu.data[config.TCCRA] = value;
      this.updateWGMConfig();
      return true;
    };

    cpu.writeHooks[config.TCCRB] = value => {
      this.cpu.data[config.TCCRB] = value;
      this.updateDivider = true;
      this.cpu.clearClockEvent(this.count);
      this.cpu.addClockEvent(this.count, 0);
      this.updateWGMConfig();
      return true;
    };

    cpu.writeHooks[config.TIFR] = value => {
      this.cpu.data[config.TIFR] = value;
      this.cpu.clearInterruptByFlag(this.OVF, value);
      this.cpu.clearInterruptByFlag(this.OCFA, value);
      this.cpu.clearInterruptByFlag(this.OCFB, value);
      return true;
    };

    cpu.writeHooks[config.TIMSK] = value => {
      this.cpu.updateInterruptEnable(this.OVF, value);
      this.cpu.updateInterruptEnable(this.OCFA, value);
      this.cpu.updateInterruptEnable(this.OCFB, value);
    };
  }

  reset() {
    this.divider = 0;
    this.lastCycle = 0;
    this.ocrA = 0;
    this.nextOcrA = 0;
    this.ocrB = 0;
    this.nextOcrB = 0;
    this.icr = 0;
    this.tcnt = 0;
    this.tcntNext = 0;
    this.tcntUpdated = false;
    this.countingUp = false;
    this.updateDivider = true;
  }

  get TCCRA() {
    return this.cpu.data[this.config.TCCRA];
  }

  get TCCRB() {
    return this.cpu.data[this.config.TCCRB];
  }

  get TIMSK() {
    return this.cpu.data[this.config.TIMSK];
  }

  get CS() {
    return this.TCCRB & 0x7;
  }

  get WGM() {
    const mask = this.config.bits === 16 ? 0x18 : 0x8;
    return (this.TCCRB & mask) >> 1 | this.TCCRA & 0x3;
  }

  get TOP() {
    switch (this.topValue) {
      case TopOCRA:
        return this.ocrA;

      case TopICR:
        return this.icr;

      default:
        return this.topValue;
    }
  }

  updateWGMConfig() {
    const {
      config,
      WGM
    } = this;
    const wgmModes = config.bits === 16 ? wgmModes16Bit : wgmModes8Bit;
    const TCCRA = this.cpu.data[config.TCCRA];
    const [timerMode, topValue, ocrUpdateMode, tovUpdateMode, flags] = wgmModes[WGM];
    this.timerMode = timerMode;
    this.topValue = topValue;
    this.ocrUpdateMode = ocrUpdateMode;
    this.tovUpdateMode = tovUpdateMode;
    const pwmMode = timerMode === FastPWM || timerMode === PWMPhaseCorrect || timerMode === PWMPhaseFrequencyCorrect;
    const prevCompA = this.compA;
    this.compA = TCCRA >> 6 & 0x3;

    if (this.compA === 1 && pwmMode && !(flags & OCToggle)) {
      this.compA = 0;
    }

    if (!!prevCompA !== !!this.compA) {
      this.updateCompA(this.compA ? _gpio.PinOverrideMode.Enable : _gpio.PinOverrideMode.None);
    }

    const prevCompB = this.compB;
    this.compB = TCCRA >> 4 & 0x3;

    if (this.compB === 1 && pwmMode) {
      this.compB = 0; // Reserved, according to the datasheet
    }

    if (!!prevCompB !== !!this.compB) {
      this.updateCompB(this.compB ? _gpio.PinOverrideMode.Enable : _gpio.PinOverrideMode.None);
    }
  }

  phasePwmCount(value, delta) {
    const {
      ocrA,
      ocrB,
      TOP,
      tcntUpdated
    } = this;

    while (delta > 0) {
      if (this.countingUp) {
        value++;

        if (value === TOP && !tcntUpdated) {
          this.countingUp = false;

          if (this.ocrUpdateMode === OCRUpdateMode.Top) {
            this.ocrA = this.nextOcrA;
            this.ocrB = this.nextOcrB;
          }
        }
      } else {
        value--;

        if (!value && !tcntUpdated) {
          this.countingUp = true;
          this.cpu.setInterruptFlag(this.OVF);

          if (this.ocrUpdateMode === OCRUpdateMode.Bottom) {
            this.ocrA = this.nextOcrA;
            this.ocrB = this.nextOcrB;
          }
        }
      }

      if (!tcntUpdated && value === ocrA) {
        this.cpu.setInterruptFlag(this.OCFA);

        if (this.compA) {
          this.updateCompPin(this.compA, 'A');
        }
      }

      if (!tcntUpdated && value === ocrB) {
        this.cpu.setInterruptFlag(this.OCFB);

        if (this.compB) {
          this.updateCompPin(this.compB, 'B');
        }
      }

      delta--;
    }

    return value;
  }

  timerUpdated(value, prevValue) {
    const {
      ocrA,
      ocrB
    } = this;
    const overflow = prevValue > value;

    if ((prevValue < ocrA || overflow) && value >= ocrA || prevValue < ocrA && overflow) {
      this.cpu.setInterruptFlag(this.OCFA);

      if (this.compA) {
        this.updateCompPin(this.compA, 'A');
      }
    }

    if ((prevValue < ocrB || overflow) && value >= ocrB || prevValue < ocrB && overflow) {
      this.cpu.setInterruptFlag(this.OCFB);

      if (this.compB) {
        this.updateCompPin(this.compB, 'B');
      }
    }
  }

  updateCompPin(compValue, pinName, bottom = false) {
    let newValue = _gpio.PinOverrideMode.None;
    const invertingMode = compValue === 3;
    const isSet = this.countingUp === invertingMode;

    switch (this.timerMode) {
      case Normal:
      case CTC:
        newValue = compToOverride(compValue);
        break;

      case FastPWM:
        if (compValue === 1) {
          newValue = bottom ? _gpio.PinOverrideMode.None : _gpio.PinOverrideMode.Toggle;
        } else {
          newValue = invertingMode !== bottom ? _gpio.PinOverrideMode.Set : _gpio.PinOverrideMode.Clear;
        }

        break;

      case PWMPhaseCorrect:
      case PWMPhaseFrequencyCorrect:
        if (compValue === 1) {
          newValue = _gpio.PinOverrideMode.Toggle;
        } else {
          newValue = isSet ? _gpio.PinOverrideMode.Set : _gpio.PinOverrideMode.Clear;
        }

        break;
    }

    if (newValue !== _gpio.PinOverrideMode.None) {
      if (pinName === 'A') {
        this.updateCompA(newValue);
      } else {
        this.updateCompB(newValue);
      }
    }
  }

  updateCompA(value) {
    const {
      compPortA,
      compPinA
    } = this.config;
    const hook = this.cpu.gpioTimerHooks[compPortA];

    if (hook) {
      hook(compPinA, value, compPortA);
    }
  }

  updateCompB(value) {
    const {
      compPortB,
      compPinB
    } = this.config;
    const hook = this.cpu.gpioTimerHooks[compPortB];

    if (hook) {
      hook(compPinB, value, compPortB);
    }
  }

}

exports.AVRTimer = AVRTimer;
},{"./gpio":"../../src/peripherals/gpio.ts"}],"../../src/peripherals/usart.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVRUSART = exports.usart0Config = void 0;

/**
 * AVR-8 USART Peripheral
 * Part of AVR8js
 * Reference: http://ww1.microchip.com/downloads/en/DeviceDoc/ATmega48A-PA-88A-PA-168A-PA-328-P-DS-DS40002061A.pdf
 *
 * Copyright (C) 2019, 2020, Uri Shaked
 */
const usart0Config = {
  rxCompleteInterrupt: 0x24,
  dataRegisterEmptyInterrupt: 0x26,
  txCompleteInterrupt: 0x28,
  UCSRA: 0xc0,
  UCSRB: 0xc1,
  UCSRC: 0xc2,
  UBRRL: 0xc4,
  UBRRH: 0xc5,
  UDR: 0xc6
};
/* eslint-disable @typescript-eslint/no-unused-vars */
// Register bits:

exports.usart0Config = usart0Config;
const UCSRA_RXC = 0x80; // USART Receive Complete

const UCSRA_TXC = 0x40; // USART Transmit Complete

const UCSRA_UDRE = 0x20; // USART Data Register Empty

const UCSRA_FE = 0x10; // Frame Error

const UCSRA_DOR = 0x8; // Data OverRun

const UCSRA_UPE = 0x4; // USART Parity Error

const UCSRA_U2X = 0x2; // Double the USART Transmission Speed

const UCSRA_MPCM = 0x1; // Multi-processor Communication Mode

const UCSRB_RXCIE = 0x80; // RX Complete Interrupt Enable

const UCSRB_TXCIE = 0x40; // TX Complete Interrupt Enable

const UCSRB_UDRIE = 0x20; // USART Data Register Empty Interrupt Enable

const UCSRB_RXEN = 0x10; // Receiver Enable

const UCSRB_TXEN = 0x8; // Transmitter Enable

const UCSRB_UCSZ2 = 0x4; // Character Size 2

const UCSRB_RXB8 = 0x2; // Receive Data Bit 8

const UCSRB_TXB8 = 0x1; // Transmit Data Bit 8

const UCSRC_UMSEL1 = 0x80; // USART Mode Select 1

const UCSRC_UMSEL0 = 0x40; // USART Mode Select 0

const UCSRC_UPM1 = 0x20; // Parity Mode 1

const UCSRC_UPM0 = 0x10; // Parity Mode 0

const UCSRC_USBS = 0x8; // Stop Bit Select

const UCSRC_UCSZ1 = 0x4; // Character Size 1

const UCSRC_UCSZ0 = 0x2; // Character Size 0

const UCSRC_UCPOL = 0x1; // Clock Polarity

/* eslint-enable @typescript-eslint/no-unused-vars */

const rxMasks = {
  5: 0x1f,
  6: 0x3f,
  7: 0x7f,
  8: 0xff,
  9: 0xff
};

class AVRUSART {
  constructor(cpu, config, freqHz) {
    this.cpu = cpu;
    this.config = config;
    this.freqHz = freqHz;
    this.onByteTransmit = null;
    this.onLineTransmit = null;
    this.onRxComplete = null;
    this.rxBusyValue = false;
    this.rxByte = 0;
    this.lineBuffer = ''; // Interrupts

    this.RXC = {
      address: this.config.rxCompleteInterrupt,
      flagRegister: this.config.UCSRA,
      flagMask: UCSRA_RXC,
      enableRegister: this.config.UCSRB,
      enableMask: UCSRB_RXCIE,
      constant: true
    };
    this.UDRE = {
      address: this.config.dataRegisterEmptyInterrupt,
      flagRegister: this.config.UCSRA,
      flagMask: UCSRA_UDRE,
      enableRegister: this.config.UCSRB,
      enableMask: UCSRB_UDRIE
    };
    this.TXC = {
      address: this.config.txCompleteInterrupt,
      flagRegister: this.config.UCSRA,
      flagMask: UCSRA_TXC,
      enableRegister: this.config.UCSRB,
      enableMask: UCSRB_TXCIE
    };
    this.reset();

    this.cpu.writeHooks[config.UCSRA] = value => {
      cpu.data[config.UCSRA] = value & (UCSRA_MPCM | UCSRA_U2X);
      cpu.clearInterruptByFlag(this.TXC, value);
      return true;
    };

    this.cpu.writeHooks[config.UCSRB] = (value, oldValue) => {
      cpu.updateInterruptEnable(this.RXC, value);
      cpu.updateInterruptEnable(this.UDRE, value);
      cpu.updateInterruptEnable(this.TXC, value);

      if (value & UCSRB_RXEN && oldValue & UCSRB_RXEN) {
        cpu.clearInterrupt(this.RXC);
      }

      if (value & UCSRB_TXEN && !(oldValue & UCSRB_TXEN)) {
        // Enabling the transmission - mark UDR as empty
        cpu.setInterruptFlag(this.UDRE);
      }
    };

    this.cpu.readHooks[config.UDR] = () => {
      var _a;

      const mask = (_a = rxMasks[this.bitsPerChar]) !== null && _a !== void 0 ? _a : 0xff;
      const result = this.rxByte & mask;
      this.rxByte = 0;
      this.cpu.clearInterrupt(this.RXC);
      return result;
    };

    this.cpu.writeHooks[config.UDR] = value => {
      if (this.onByteTransmit) {
        this.onByteTransmit(value);
      }

      if (this.onLineTransmit) {
        const ch = String.fromCharCode(value);

        if (ch === '\n') {
          this.onLineTransmit(this.lineBuffer);
          this.lineBuffer = '';
        } else {
          this.lineBuffer += ch;
        }
      }

      this.cpu.addClockEvent(() => {
        cpu.setInterruptFlag(this.UDRE);
        cpu.setInterruptFlag(this.TXC);
      }, this.cyclesPerChar);
      this.cpu.clearInterrupt(this.TXC);
      this.cpu.clearInterrupt(this.UDRE);
    };
  }

  reset() {
    this.cpu.data[this.config.UCSRA] = UCSRA_UDRE;
    this.cpu.data[this.config.UCSRB] = 0;
    this.cpu.data[this.config.UCSRC] = UCSRC_UCSZ1 | UCSRC_UCSZ0; // default: 8 bits per byte

    this.rxBusyValue = false;
    this.rxByte = 0;
    this.lineBuffer = '';
  }

  get rxBusy() {
    return this.rxBusyValue;
  }

  writeByte(value) {
    const {
      cpu,
      config
    } = this;

    if (this.rxBusyValue || !(cpu.data[config.UCSRB] & UCSRB_RXEN)) {
      return false;
    }

    this.rxBusyValue = true;
    cpu.addClockEvent(() => {
      var _a;

      this.rxByte = value;
      this.rxBusyValue = false;
      cpu.setInterruptFlag(this.RXC);
      (_a = this.onRxComplete) === null || _a === void 0 ? void 0 : _a.call(this);
    }, this.cyclesPerChar);
    return true;
  }

  get cyclesPerChar() {
    const symbolsPerChar = 1 + this.bitsPerChar + this.stopBits + (this.parityEnabled ? 1 : 0);
    return (this.UBRR * this.multiplier + 1) * symbolsPerChar;
  }

  get UBRR() {
    return this.cpu.data[this.config.UBRRH] << 8 | this.cpu.data[this.config.UBRRL];
  }

  get multiplier() {
    return this.cpu.data[this.config.UCSRA] & UCSRA_U2X ? 8 : 16;
  }

  get baudRate() {
    return Math.floor(this.freqHz / (this.multiplier * (1 + this.UBRR)));
  }

  get bitsPerChar() {
    const ucsz = (this.cpu.data[this.config.UCSRC] & (UCSRC_UCSZ1 | UCSRC_UCSZ0)) >> 1 | this.cpu.data[this.config.UCSRB] & UCSRB_UCSZ2;

    switch (ucsz) {
      case 0:
        return 5;

      case 1:
        return 6;

      case 2:
        return 7;

      case 3:
        return 8;

      default: // 4..6 are reserved

      case 7:
        return 9;
    }
  }

  get stopBits() {
    return this.cpu.data[this.config.UCSRC] & UCSRC_USBS ? 2 : 1;
  }

  get parityEnabled() {
    return this.cpu.data[this.config.UCSRC] & UCSRC_UPM1 ? true : false;
  }

  get parityOdd() {
    return this.cpu.data[this.config.UCSRC] & UCSRC_UPM0 ? true : false;
  }

}

exports.AVRUSART = AVRUSART;
},{}],"../../src/peripherals/eeprom.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVREEPROM = exports.eepromConfig = exports.EEPROMMemoryBackend = void 0;

class EEPROMMemoryBackend {
  constructor(size) {
    this.memory = new Uint8Array(size);
    this.memory.fill(0xff);
  }

  readMemory(addr) {
    return this.memory[addr];
  }

  writeMemory(addr, value) {
    this.memory[addr] &= value;
  }

  eraseMemory(addr) {
    this.memory[addr] = 0xff;
  }

}

exports.EEPROMMemoryBackend = EEPROMMemoryBackend;
const eepromConfig = {
  eepromReadyInterrupt: 0x2c,
  EECR: 0x3f,
  EEDR: 0x40,
  EEARL: 0x41,
  EEARH: 0x42,
  eraseCycles: 28800,
  writeCycles: 28800
};
exports.eepromConfig = eepromConfig;
const EERE = 1 << 0;
const EEPE = 1 << 1;
const EEMPE = 1 << 2;
const EERIE = 1 << 3;
const EEPM0 = 1 << 4;
const EEPM1 = 1 << 5;

class AVREEPROM {
  constructor(cpu, backend, config = eepromConfig) {
    this.cpu = cpu;
    this.backend = backend;
    this.config = config;
    /**
     * Used to keep track on the last write to EEMPE. From the datasheet:
     * The EEMPE bit determines whether setting EEPE to one causes the EEPROM to be written.
     * When EEMPE is set, setting EEPE within four clock cycles will write data to the EEPROM
     * at the selected address If EEMPE is zero, setting EEPE will have no effect.
     */

    this.writeEnabledCycles = 0;
    this.writeCompleteCycles = 0; // Interrupts

    this.EER = {
      address: this.config.eepromReadyInterrupt,
      flagRegister: this.config.EECR,
      flagMask: EEPE,
      enableRegister: this.config.EECR,
      enableMask: EERIE,
      constant: true,
      inverseFlag: true
    };

    this.cpu.writeHooks[this.config.EECR] = eecr => {
      const {
        EEARH,
        EEARL,
        EECR,
        EEDR
      } = this.config;
      const addr = this.cpu.data[EEARH] << 8 | this.cpu.data[EEARL];

      if (eecr & EERE) {
        this.cpu.clearInterrupt(this.EER);
      }

      if (eecr & EEMPE) {
        const eempeCycles = 4;
        this.writeEnabledCycles = this.cpu.cycles + eempeCycles;
        this.cpu.addClockEvent(() => {
          this.cpu.data[EECR] &= ~EEMPE;
        }, eempeCycles);
      } // Read


      if (eecr & EERE) {
        this.cpu.data[EEDR] = this.backend.readMemory(addr); // When the EEPROM is read, the CPU is halted for four cycles before the
        // next instruction is executed.

        this.cpu.cycles += 4;
        return true;
      } // Write


      if (eecr & EEPE) {
        //  If EEMPE is zero, setting EEPE will have no effect.
        if (this.cpu.cycles >= this.writeEnabledCycles) {
          return true;
        } // Check for write-in-progress


        if (this.cpu.cycles < this.writeCompleteCycles) {
          return true;
        }

        const eedr = this.cpu.data[EEDR];
        this.writeCompleteCycles = this.cpu.cycles; // Erase

        if (!(eecr & EEPM1)) {
          this.backend.eraseMemory(addr);
          this.writeCompleteCycles += this.config.eraseCycles;
        } // Write


        if (!(eecr & EEPM0)) {
          this.backend.writeMemory(addr, eedr);
          this.writeCompleteCycles += this.config.writeCycles;
        }

        this.cpu.data[EECR] |= EEPE;
        this.cpu.addClockEvent(() => {
          this.cpu.setInterruptFlag(this.EER);
        }, this.writeCompleteCycles - this.cpu.cycles); // When EEPE has been set, the CPU is halted for two cycles before the
        // next instruction is executed.

        this.cpu.cycles += 2;
        return true;
      }

      return false;
    };
  }

}

exports.AVREEPROM = AVREEPROM;
},{}],"../../src/peripherals/twi.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVRTWI = exports.NoopTWIEventHandler = exports.twiConfig = void 0;

/* eslint-disable @typescript-eslint/no-unused-vars */
// Register bits:
const TWCR_TWINT = 0x80; // TWI Interrupt Flag

const TWCR_TWEA = 0x40; // TWI Enable Acknowledge Bit

const TWCR_TWSTA = 0x20; // TWI START Condition Bit

const TWCR_TWSTO = 0x10; // TWI STOP Condition Bit

const TWCR_TWWC = 0x8; //TWI Write Collision Flag

const TWCR_TWEN = 0x4; //  TWI Enable Bit

const TWCR_TWIE = 0x1; // TWI Interrupt Enable

const TWSR_TWS_MASK = 0xf8; // TWI Status

const TWSR_TWPS1 = 0x2; // TWI Prescaler Bits

const TWSR_TWPS0 = 0x1; // TWI Prescaler Bits

const TWSR_TWPS_MASK = TWSR_TWPS1 | TWSR_TWPS0; // TWI Prescaler mask

const TWAR_TWA_MASK = 0xfe; //  TWI (Slave) Address Register

const TWAR_TWGCE = 0x1; // TWI General Call Recognition Enable Bit

const STATUS_BUS_ERROR = 0x0;
const STATUS_TWI_IDLE = 0xf8; // Master states

const STATUS_START = 0x08;
const STATUS_REPEATED_START = 0x10;
const STATUS_SLAW_ACK = 0x18;
const STATUS_SLAW_NACK = 0x20;
const STATUS_DATA_SENT_ACK = 0x28;
const STATUS_DATA_SENT_NACK = 0x30;
const STATUS_DATA_LOST_ARBITRATION = 0x38;
const STATUS_SLAR_ACK = 0x40;
const STATUS_SLAR_NACK = 0x48;
const STATUS_DATA_RECEIVED_ACK = 0x50;
const STATUS_DATA_RECEIVED_NACK = 0x58; // TODO: add slave states

/* eslint-enable @typescript-eslint/no-unused-vars */

const twiConfig = {
  twiInterrupt: 0x30,
  TWBR: 0xb8,
  TWSR: 0xb9,
  TWAR: 0xba,
  TWDR: 0xbb,
  TWCR: 0xbc,
  TWAMR: 0xbd
}; // A simple TWI Event Handler that sends a NACK for all events

exports.twiConfig = twiConfig;

class NoopTWIEventHandler {
  constructor(twi) {
    this.twi = twi;
  }

  start() {
    this.twi.completeStart();
  }

  stop() {
    this.twi.completeStop();
  }

  connectToSlave() {
    this.twi.completeConnect(false);
  }

  writeByte() {
    this.twi.completeWrite(false);
  }

  readByte() {
    this.twi.completeRead(0xff);
  }

}

exports.NoopTWIEventHandler = NoopTWIEventHandler;

class AVRTWI {
  constructor(cpu, config, freqHz) {
    this.cpu = cpu;
    this.config = config;
    this.freqHz = freqHz;
    this.eventHandler = new NoopTWIEventHandler(this); // Interrupts

    this.TWI = {
      address: this.config.twiInterrupt,
      flagRegister: this.config.TWCR,
      flagMask: TWCR_TWINT,
      enableRegister: this.config.TWCR,
      enableMask: TWCR_TWIE
    };
    this.updateStatus(STATUS_TWI_IDLE);

    this.cpu.writeHooks[config.TWCR] = value => {
      this.cpu.data[config.TWCR] = value;
      const clearInt = value & TWCR_TWINT;
      this.cpu.clearInterruptByFlag(this.TWI, value);
      this.cpu.updateInterruptEnable(this.TWI, value);
      const {
        status
      } = this;

      if (clearInt && value & TWCR_TWEN) {
        const twdrValue = this.cpu.data[this.config.TWDR];
        this.cpu.addClockEvent(() => {
          if (value & TWCR_TWSTA) {
            this.eventHandler.start(status !== STATUS_TWI_IDLE);
          } else if (value & TWCR_TWSTO) {
            this.eventHandler.stop();
          } else if (status === STATUS_START || status === STATUS_REPEATED_START) {
            this.eventHandler.connectToSlave(twdrValue >> 1, twdrValue & 0x1 ? false : true);
          } else if (status === STATUS_SLAW_ACK || status === STATUS_DATA_SENT_ACK) {
            this.eventHandler.writeByte(twdrValue);
          } else if (status === STATUS_SLAR_ACK || status === STATUS_DATA_RECEIVED_ACK) {
            const ack = !!(value & TWCR_TWEA);
            this.eventHandler.readByte(ack);
          }
        }, 0);
        return true;
      }
    };
  }

  get prescaler() {
    switch (this.cpu.data[this.config.TWSR] & TWSR_TWPS_MASK) {
      case 0:
        return 1;

      case 1:
        return 4;

      case 2:
        return 16;

      case 3:
        return 64;
    } // We should never get here:


    throw new Error('Invalid prescaler value!');
  }

  get sclFrequency() {
    return this.freqHz / (16 + 2 * this.cpu.data[this.config.TWBR] * this.prescaler);
  }

  completeStart() {
    this.updateStatus(this.status === STATUS_TWI_IDLE ? STATUS_START : STATUS_REPEATED_START);
  }

  completeStop() {
    this.cpu.data[this.config.TWCR] &= ~TWCR_TWSTO;
    this.updateStatus(STATUS_TWI_IDLE);
  }

  completeConnect(ack) {
    if (this.cpu.data[this.config.TWDR] & 0x1) {
      this.updateStatus(ack ? STATUS_SLAR_ACK : STATUS_SLAR_NACK);
    } else {
      this.updateStatus(ack ? STATUS_SLAW_ACK : STATUS_SLAW_NACK);
    }
  }

  completeWrite(ack) {
    this.updateStatus(ack ? STATUS_DATA_SENT_ACK : STATUS_DATA_SENT_NACK);
  }

  completeRead(value) {
    const ack = !!(this.cpu.data[this.config.TWCR] & TWCR_TWEA);
    this.cpu.data[this.config.TWDR] = value;
    this.updateStatus(ack ? STATUS_DATA_RECEIVED_ACK : STATUS_DATA_RECEIVED_NACK);
  }

  get status() {
    return this.cpu.data[this.config.TWSR] & TWSR_TWS_MASK;
  }

  updateStatus(value) {
    const {
      TWSR
    } = this.config;
    this.cpu.data[TWSR] = this.cpu.data[TWSR] & ~TWSR_TWS_MASK | value;
    this.cpu.setInterruptFlag(this.TWI);
  }

}

exports.AVRTWI = AVRTWI;
},{}],"../../src/peripherals/spi.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVRSPI = exports.spiConfig = void 0;
// Register bits:
const SPCR_SPIE = 0x80; //  SPI Interrupt Enable

const SPCR_SPE = 0x40; // SPI Enable

const SPCR_DORD = 0x20; // Data Order

const SPCR_MSTR = 0x10; //  Master/Slave Select

const SPCR_CPOL = 0x8; // Clock Polarity

const SPCR_CPHA = 0x4; // Clock Phase

const SPCR_SPR1 = 0x2; // SPI Clock Rate Select 1

const SPCR_SPR0 = 0x1; // SPI Clock Rate Select 0

const SPSR_SPR_MASK = SPCR_SPR1 | SPCR_SPR0;
const SPSR_SPIF = 0x80; // SPI Interrupt Flag

const SPSR_WCOL = 0x40; // Write COLlision Flag

const SPSR_SPI2X = 0x1; // Double SPI Speed Bit

const spiConfig = {
  spiInterrupt: 0x22,
  SPCR: 0x4c,
  SPSR: 0x4d,
  SPDR: 0x4e
};
exports.spiConfig = spiConfig;
const bitsPerByte = 8;

class AVRSPI {
  constructor(cpu, config, freqHz) {
    this.cpu = cpu;
    this.config = config;
    this.freqHz = freqHz;
    this.onTransfer = null;
    this.transmissionActive = false;
    this.receivedByte = 0; // Interrupts

    this.SPI = {
      address: this.config.spiInterrupt,
      flagRegister: this.config.SPSR,
      flagMask: SPSR_SPIF,
      enableRegister: this.config.SPCR,
      enableMask: SPCR_SPIE
    };
    const {
      SPCR,
      SPSR,
      SPDR
    } = config;

    cpu.writeHooks[SPDR] = value => {
      var _a, _b;

      if (!(cpu.data[SPCR] & SPCR_SPE)) {
        // SPI not enabled, ignore write
        return;
      } // Write collision


      if (this.transmissionActive) {
        cpu.data[SPSR] |= SPSR_WCOL;
        return true;
      } // Clear write collision / interrupt flags


      cpu.data[SPSR] &= ~SPSR_WCOL;
      this.cpu.clearInterrupt(this.SPI);
      this.receivedByte = (_b = (_a = this.onTransfer) === null || _a === void 0 ? void 0 : _a.call(this, value)) !== null && _b !== void 0 ? _b : 0;
      const cyclesToComplete = this.clockDivider * bitsPerByte;
      this.transmissionActive = true;
      this.cpu.addClockEvent(() => {
        this.cpu.data[SPDR] = this.receivedByte;
        this.cpu.setInterruptFlag(this.SPI);
        this.transmissionActive = false;
      }, cyclesToComplete);
      return true;
    };

    cpu.writeHooks[SPSR] = value => {
      this.cpu.data[SPSR] = value;
      this.cpu.clearInterruptByFlag(this.SPI, value);
    };
  }

  reset() {
    this.transmissionActive = false;
    this.receivedByte = 0;
  }

  get isMaster() {
    return this.cpu.data[this.config.SPCR] & SPCR_MSTR ? true : false;
  }

  get dataOrder() {
    return this.cpu.data[this.config.SPCR] & SPCR_DORD ? 'lsbFirst' : 'msbFirst';
  }

  get spiMode() {
    const CPHA = this.cpu.data[this.config.SPCR] & SPCR_CPHA;
    const CPOL = this.cpu.data[this.config.SPCR] & SPCR_CPOL;
    return (CPHA ? 2 : 0) | (CPOL ? 1 : 0);
  }
  /**
   * The clock divider is only relevant for Master mode
   */


  get clockDivider() {
    const base = this.cpu.data[this.config.SPSR] & SPSR_SPI2X ? 2 : 4;

    switch (this.cpu.data[this.config.SPCR] & SPSR_SPR_MASK) {
      case 0b00:
        return base;

      case 0b01:
        return base * 4;

      case 0b10:
        return base * 16;

      case 0b11:
        return base * 32;
    } // We should never get here:


    throw new Error('Invalid divider value!');
  }
  /**
   * The SPI freqeuncy is only relevant to Master mode.
   * In slave mode, the frequency can be as high as F(osc) / 4.
   */


  get spiFrequency() {
    return this.freqHz / this.clockDivider;
  }

}

exports.AVRSPI = AVRSPI;
},{}],"../../src/peripherals/clock.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVRClock = exports.clockConfig = void 0;

/**
 * AVR8 Clock
 * Part of AVR8js
 * Reference: http://ww1.microchip.com/downloads/en/DeviceDoc/ATmega48A-PA-88A-PA-168A-PA-328-P-DS-DS40002061A.pdf
 *
 * Copyright (C) 2020, Uri Shaked
 */
const CLKPCE = 128;
const clockConfig = {
  CLKPR: 0x61
};
exports.clockConfig = clockConfig;
const prescalers = [1, 2, 4, 8, 16, 32, 64, 128, 256, // The following values are "reserved" according to the datasheet, so we measured
// with a scope to figure them out (on ATmega328p)
2, 4, 8, 16, 32, 64, 128];

class AVRClock {
  constructor(cpu, baseFreqHz, config = clockConfig) {
    this.cpu = cpu;
    this.baseFreqHz = baseFreqHz;
    this.config = config;
    this.clockEnabledCycles = 0;
    this.prescalerValue = 1;
    this.cyclesDelta = 0;

    this.cpu.writeHooks[this.config.CLKPR] = clkpr => {
      if ((!this.clockEnabledCycles || this.clockEnabledCycles < cpu.cycles) && clkpr === CLKPCE) {
        this.clockEnabledCycles = this.cpu.cycles + 4;
      } else if (this.clockEnabledCycles && this.clockEnabledCycles >= cpu.cycles) {
        this.clockEnabledCycles = 0;
        const index = clkpr & 0xf;
        const oldPrescaler = this.prescalerValue;
        this.prescalerValue = prescalers[index];
        this.cpu.data[this.config.CLKPR] = index;

        if (oldPrescaler !== this.prescalerValue) {
          this.cyclesDelta = (cpu.cycles + this.cyclesDelta) * (oldPrescaler / this.prescalerValue) - cpu.cycles;
        }
      }

      return true;
    };
  }

  get frequency() {
    return this.baseFreqHz / this.prescalerValue;
  }

  get prescaler() {
    return this.prescalerValue;
  }

  get timeNanos() {
    return (this.cpu.cycles + this.cyclesDelta) / this.frequency * 1e9;
  }

  get timeMicros() {
    return (this.cpu.cycles + this.cyclesDelta) / this.frequency * 1e6;
  }

  get timeMillis() {
    return (this.cpu.cycles + this.cyclesDelta) / this.frequency * 1e3;
  }

}

exports.AVRClock = AVRClock;
},{}],"../../src/index.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
var _exportNames = {
  CPU: true,
  ICPU: true,
  CPUMemoryHook: true,
  CPUMemoryHooks: true,
  avrInstruction: true,
  avrInterrupt: true,
  AVRTimer: true,
  AVRTimerConfig: true,
  timer0Config: true,
  timer1Config: true,
  timer2Config: true,
  AVRIOPort: true,
  GPIOListener: true,
  AVRPortConfig: true,
  portAConfig: true,
  portBConfig: true,
  portCConfig: true,
  portDConfig: true,
  portEConfig: true,
  portFConfig: true,
  portGConfig: true,
  portHConfig: true,
  portJConfig: true,
  portKConfig: true,
  portLConfig: true,
  PinState: true,
  AVRUSART: true,
  usart0Config: true,
  AVREEPROM: true,
  AVREEPROMConfig: true,
  EEPROMBackend: true,
  EEPROMMemoryBackend: true,
  eepromConfig: true,
  spiConfig: true,
  SPIConfig: true,
  SPITransferCallback: true,
  AVRSPI: true,
  AVRClock: true,
  AVRClockConfig: true,
  clockConfig: true
};
Object.defineProperty(exports, "CPU", {
  enumerable: true,
  get: function () {
    return _cpu.CPU;
  }
});
Object.defineProperty(exports, "ICPU", {
  enumerable: true,
  get: function () {
    return _cpu.ICPU;
  }
});
Object.defineProperty(exports, "CPUMemoryHook", {
  enumerable: true,
  get: function () {
    return _cpu.CPUMemoryHook;
  }
});
Object.defineProperty(exports, "CPUMemoryHooks", {
  enumerable: true,
  get: function () {
    return _cpu.CPUMemoryHooks;
  }
});
Object.defineProperty(exports, "avrInstruction", {
  enumerable: true,
  get: function () {
    return _instruction.avrInstruction;
  }
});
Object.defineProperty(exports, "avrInterrupt", {
  enumerable: true,
  get: function () {
    return _interrupt.avrInterrupt;
  }
});
Object.defineProperty(exports, "AVRTimer", {
  enumerable: true,
  get: function () {
    return _timer.AVRTimer;
  }
});
Object.defineProperty(exports, "AVRTimerConfig", {
  enumerable: true,
  get: function () {
    return _timer.AVRTimerConfig;
  }
});
Object.defineProperty(exports, "timer0Config", {
  enumerable: true,
  get: function () {
    return _timer.timer0Config;
  }
});
Object.defineProperty(exports, "timer1Config", {
  enumerable: true,
  get: function () {
    return _timer.timer1Config;
  }
});
Object.defineProperty(exports, "timer2Config", {
  enumerable: true,
  get: function () {
    return _timer.timer2Config;
  }
});
Object.defineProperty(exports, "AVRIOPort", {
  enumerable: true,
  get: function () {
    return _gpio.AVRIOPort;
  }
});
Object.defineProperty(exports, "GPIOListener", {
  enumerable: true,
  get: function () {
    return _gpio.GPIOListener;
  }
});
Object.defineProperty(exports, "AVRPortConfig", {
  enumerable: true,
  get: function () {
    return _gpio.AVRPortConfig;
  }
});
Object.defineProperty(exports, "portAConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portAConfig;
  }
});
Object.defineProperty(exports, "portBConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portBConfig;
  }
});
Object.defineProperty(exports, "portCConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portCConfig;
  }
});
Object.defineProperty(exports, "portDConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portDConfig;
  }
});
Object.defineProperty(exports, "portEConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portEConfig;
  }
});
Object.defineProperty(exports, "portFConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portFConfig;
  }
});
Object.defineProperty(exports, "portGConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portGConfig;
  }
});
Object.defineProperty(exports, "portHConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portHConfig;
  }
});
Object.defineProperty(exports, "portJConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portJConfig;
  }
});
Object.defineProperty(exports, "portKConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portKConfig;
  }
});
Object.defineProperty(exports, "portLConfig", {
  enumerable: true,
  get: function () {
    return _gpio.portLConfig;
  }
});
Object.defineProperty(exports, "PinState", {
  enumerable: true,
  get: function () {
    return _gpio.PinState;
  }
});
Object.defineProperty(exports, "AVRUSART", {
  enumerable: true,
  get: function () {
    return _usart.AVRUSART;
  }
});
Object.defineProperty(exports, "usart0Config", {
  enumerable: true,
  get: function () {
    return _usart.usart0Config;
  }
});
Object.defineProperty(exports, "AVREEPROM", {
  enumerable: true,
  get: function () {
    return _eeprom.AVREEPROM;
  }
});
Object.defineProperty(exports, "AVREEPROMConfig", {
  enumerable: true,
  get: function () {
    return _eeprom.AVREEPROMConfig;
  }
});
Object.defineProperty(exports, "EEPROMBackend", {
  enumerable: true,
  get: function () {
    return _eeprom.EEPROMBackend;
  }
});
Object.defineProperty(exports, "EEPROMMemoryBackend", {
  enumerable: true,
  get: function () {
    return _eeprom.EEPROMMemoryBackend;
  }
});
Object.defineProperty(exports, "eepromConfig", {
  enumerable: true,
  get: function () {
    return _eeprom.eepromConfig;
  }
});
Object.defineProperty(exports, "spiConfig", {
  enumerable: true,
  get: function () {
    return _spi.spiConfig;
  }
});
Object.defineProperty(exports, "SPIConfig", {
  enumerable: true,
  get: function () {
    return _spi.SPIConfig;
  }
});
Object.defineProperty(exports, "SPITransferCallback", {
  enumerable: true,
  get: function () {
    return _spi.SPITransferCallback;
  }
});
Object.defineProperty(exports, "AVRSPI", {
  enumerable: true,
  get: function () {
    return _spi.AVRSPI;
  }
});
Object.defineProperty(exports, "AVRClock", {
  enumerable: true,
  get: function () {
    return _clock.AVRClock;
  }
});
Object.defineProperty(exports, "AVRClockConfig", {
  enumerable: true,
  get: function () {
    return _clock.AVRClockConfig;
  }
});
Object.defineProperty(exports, "clockConfig", {
  enumerable: true,
  get: function () {
    return _clock.clockConfig;
  }
});

var _cpu = require("./cpu/cpu");

var _instruction = require("./cpu/instruction");

var _interrupt = require("./cpu/interrupt");

var _timer = require("./peripherals/timer");

var _gpio = require("./peripherals/gpio");

var _usart = require("./peripherals/usart");

var _eeprom = require("./peripherals/eeprom");

var _twi = require("./peripherals/twi");

Object.keys(_twi).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (Object.prototype.hasOwnProperty.call(_exportNames, key)) return;
  Object.defineProperty(exports, key, {
    enumerable: true,
    get: function () {
      return _twi[key];
    }
  });
});

var _spi = require("./peripherals/spi");

var _clock = require("./peripherals/clock");
},{"./cpu/cpu":"../../src/cpu/cpu.ts","./cpu/instruction":"../../src/cpu/instruction.ts","./cpu/interrupt":"../../src/cpu/interrupt.ts","./peripherals/timer":"../../src/peripherals/timer.ts","./peripherals/gpio":"../../src/peripherals/gpio.ts","./peripherals/usart":"../../src/peripherals/usart.ts","./peripherals/eeprom":"../../src/peripherals/eeprom.ts","./peripherals/twi":"../../src/peripherals/twi.ts","./peripherals/spi":"../../src/peripherals/spi.ts","./peripherals/clock":"../../src/peripherals/clock.ts"}],"intelhex.ts":[function(require,module,exports) {
"use strict";
/**
 * Minimal Intel HEX loader
 * Part of AVR8js
 *
 * Copyright (C) 2019, Uri Shaked
 */

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.loadHex = void 0;

function loadHex(source, target) {
  for (const line of source.split('\n')) {
    if (line[0] === ':' && line.substr(7, 2) === '00') {
      const bytes = parseInt(line.substr(1, 2), 16);
      const addr = parseInt(line.substr(3, 4), 16);

      for (let i = 0; i < bytes; i++) {
        target[addr + i] = parseInt(line.substr(9 + i * 2, 2), 16);
      }
    }
  }
}

exports.loadHex = loadHex;
},{}],"execute.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AVRRunner = void 0;

const avr8js_1 = require("avr8js");

const intelhex_1 = require("./intelhex"); // ATmega328p params


const FLASH = 0x8000;

class AVRRunner {
  constructor(hex, sim_) {
    this.program = new Uint16Array(FLASH);
    this.speed = 16e6; // 16 MHZ

    this.workUnitCycles = 500000;
    intelhex_1.loadHex(hex, new Uint8Array(this.program.buffer));
    this.cpu = new avr8js_1.CPU(this.program);
    this.timer0 = new avr8js_1.AVRTimer(this.cpu, avr8js_1.timer0Config);
    this.timer1 = new avr8js_1.AVRTimer(this.cpu, avr8js_1.timer1Config);
    this.timer2 = new avr8js_1.AVRTimer(this.cpu, avr8js_1.timer2Config);
    this.portB = new avr8js_1.AVRIOPort(this.cpu, avr8js_1.portBConfig);
    this.portC = new avr8js_1.AVRIOPort(this.cpu, avr8js_1.portCConfig);
    this.portD = new avr8js_1.AVRIOPort(this.cpu, avr8js_1.portDConfig);
    this.usart = new avr8js_1.AVRUSART(this.cpu, avr8js_1.usart0Config, this.speed); // Simulate analog port (so that analogRead() eventually return)

    this.cpu.writeHooks[0x7a] = value => {
      if (value & 1 << 6) {
        this.cpu.data[0x7a] = value & ~(1 << 6); // clear bit - conversion done

        const ADMUXval = this.cpu.data[0x7c]; //Value held in ADMUX selection register

        const analogPin = ADMUXval & 15; //Apply mask to clear first 4 bits as only latter half is important for selection

        this.setAnalogValue(Math.floor(this.sim.getNodeVoltage("A" + analogPin) * 1023 / 5));
        return true; // don't update
      }
    };

    this.sim = sim_;
    this.prevTime = this.sim.getTime();
  }

  setAnalogValue(analogValue) {
    //Write analogValue to ADCH and ADCL
    this.cpu.data[0x78] = analogValue & 0xff;
    this.cpu.data[0x79] = analogValue >> 8 & 0x3;
  } // set CPU main loop


  execute(callback) {
    var runner = this;

    this.sim.ontimestep = function () {
      var timeDiff = runner.sim.getTime() - runner.prevTime; //Added by Mark Megarry

      var cyclesToRun = runner.cpu.cycles + timeDiff * runner.speed; //Added by Mark Megarry

      runner.getPinStates();

      while (runner.cpu.cycles < cyclesToRun) {
        avr8js_1.avrInstruction(runner.cpu);
        runner.cpu.tick();
      }

      runner.prevTime = runner.sim.getTime();
      callback(runner.cpu);
    };
  }

  getPinStates() {
    var i;

    for (i = 0; i != 14; i++) {
      var port = this.portD;
      var pn = i;

      if (i >= 8) {
        port = this.portB;
        pn = i - 8;
      }

      var ps = port.pinState(pn);
      if (ps == avr8js_1.PinState.Input) port.setPin(pn, this.sim.getNodeVoltage("pin " + i) > 2.5);else this.sim.setExtVoltage("pin " + i, ps == avr8js_1.PinState.High ? 5 : 0);
    }
  }

  stop() {
    this.sim.ontimestep = null;
  }

}

exports.AVRRunner = AVRRunner;
},{"avr8js":"../../src/index.ts","./intelhex":"intelhex.ts"}],"format-time.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.formatTime = void 0;

function zeroPad(value, length) {
  let sval = value.toString();

  while (sval.length < length) {
    sval = '0' + sval;
  }

  return sval;
}

function formatTime(seconds) {
  const ms = Math.floor(seconds * 1000) % 1000;
  const secs = Math.floor(seconds % 60);
  const mins = Math.floor(seconds / 60);
  return `${zeroPad(mins, 2)}:${zeroPad(secs, 2)}.${zeroPad(ms, 3)}`;
}

exports.formatTime = formatTime;
},{}],"../../node_modules/parcel-bundler/src/builtins/bundle-url.js":[function(require,module,exports) {
var bundleURL = null;

function getBundleURLCached() {
  if (!bundleURL) {
    bundleURL = getBundleURL();
  }

  return bundleURL;
}

function getBundleURL() {
  // Attempt to find the URL of the current script and use that as the base URL
  try {
    throw new Error();
  } catch (err) {
    var matches = ('' + err.stack).match(/(https?|file|ftp|chrome-extension|moz-extension):\/\/[^)\n]+/g);

    if (matches) {
      return getBaseURL(matches[0]);
    }
  }

  return '/';
}

function getBaseURL(url) {
  return ('' + url).replace(/^((?:https?|file|ftp|chrome-extension|moz-extension):\/\/.+)\/[^/]+$/, '$1') + '/';
}

exports.getBundleURL = getBundleURLCached;
exports.getBaseURL = getBaseURL;
},{}],"../../node_modules/parcel-bundler/src/builtins/css-loader.js":[function(require,module,exports) {
var bundle = require('./bundle-url');

function updateLink(link) {
  var newLink = link.cloneNode();

  newLink.onload = function () {
    link.remove();
  };

  newLink.href = link.href.split('?')[0] + '?' + Date.now();
  link.parentNode.insertBefore(newLink, link.nextSibling);
}

var cssTimeout = null;

function reloadCSS() {
  if (cssTimeout) {
    return;
  }

  cssTimeout = setTimeout(function () {
    var links = document.querySelectorAll('link[rel="stylesheet"]');

    for (var i = 0; i < links.length; i++) {
      if (bundle.getBaseURL(links[i].href) === bundle.getBundleURL()) {
        updateLink(links[i]);
      }
    }

    cssTimeout = null;
  }, 50);
}

module.exports = reloadCSS;
},{"./bundle-url":"../../node_modules/parcel-bundler/src/builtins/bundle-url.js"}],"index.css":[function(require,module,exports) {
var reloadCSS = require('_css_loader');

module.hot.dispose(reloadCSS);
module.hot.accept(reloadCSS);
},{"_css_loader":"../../node_modules/parcel-bundler/src/builtins/css-loader.js"}],"utils/editor-history.util.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.EditorHistoryUtil = void 0;
const AVRJS8_EDITOR_HISTORY = 'AVRJS8_EDITOR_HISTORY';

class EditorHistoryUtil {
  static storeSnippet(codeSnippet) {
    if (!EditorHistoryUtil.hasLocalStorage) {
      return;
    }

    window.localStorage.setItem(AVRJS8_EDITOR_HISTORY, codeSnippet);
  }

  static clearSnippet() {
    if (!EditorHistoryUtil.hasLocalStorage) {
      return;
    }

    localStorage.removeItem(AVRJS8_EDITOR_HISTORY);
  }

  static getValue() {
    if (!EditorHistoryUtil.hasLocalStorage) {
      return;
    }

    return localStorage.getItem(AVRJS8_EDITOR_HISTORY);
  }

}

exports.EditorHistoryUtil = EditorHistoryUtil;
EditorHistoryUtil.hasLocalStorage = !!window.localStorage;
},{}],"index.ts":[function(require,module,exports) {
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});

require("@wokwi/elements");

const compile_1 = require("./compile");

const cpu_performance_1 = require("./cpu-performance");

const execute_1 = require("./execute");

const format_time_1 = require("./format-time");

require("./index.css");

const editor_history_util_1 = require("./utils/editor-history.util");

let editor; // eslint-disable-line @typescript-eslint/no-explicit-any

const BLINK_CODE = `
// Green LED connected to LED_BUILTIN,
// Red LED connected to pin 12. Enjoy!

void setup() {
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
  Serial.println("Blink");
  digitalWrite(LED_BUILTIN, HIGH);
  delay(500);
  digitalWrite(LED_BUILTIN, LOW);
  delay(500);
}`.trim();

window.require.config({
  paths: {
    vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.21.2/min/vs'
  }
});

window.require(['vs/editor/editor.main'], () => {
  editor = monaco.editor.create(document.querySelector('.code-editor'), {
    value:
    /*EditorHistoryUtil.getValue() || */
    window.defaultCode || BLINK_CODE,
    language: 'cpp',
    minimap: {
      enabled: false
    }
  });
}); // Set up LEDs


var sim; // Set up toolbar

let runner;
/* eslint-disable @typescript-eslint/no-use-before-define */

const runButton = document.querySelector('#run-button');
runButton.addEventListener('click', compileAndRun);
const stopButton = document.querySelector('#stop-button');
stopButton.addEventListener('click', stopCode);
const revertButton = document.querySelector('#revert-button');
revertButton.addEventListener('click', setBlinkSnippet);
const statusLabel = document.querySelector('#status-label');
const compilerOutputText = document.querySelector('#compiler-output-text');
const serialOutputText = document.querySelector('#serial-output-text');

function executeProgram(hex) {
  const MHZ = 16000000; // get iframe the simulator is running in.  Must have same origin as this file!

  var iframe = document.getElementById("circuitFrame");
  sim = iframe.contentWindow.CircuitJS1;
  runner = new execute_1.AVRRunner(hex, sim);

  runner.usart.onByteTransmit = value => {
    serialOutputText.textContent += String.fromCharCode(value);
  };

  const cpuPerf = new cpu_performance_1.CPUPerformance(runner.cpu, MHZ);
  runner.execute(cpu => {
    const time = format_time_1.formatTime(cpu.cycles / MHZ);
    const speed = (cpuPerf.update() * 100).toFixed(0);
    statusLabel.textContent = `Simulation time: ${time} (${speed}%)`;
  });
  sim.setSimRunning(true);
}

async function compileAndRun() {
  storeUserSnippet();
  runButton.setAttribute('disabled', '1');
  revertButton.setAttribute('disabled', '1');
  serialOutputText.textContent = '';

  try {
    statusLabel.textContent = 'Compiling...';
    const result = await compile_1.buildHex(editor.getModel().getValue());
    compilerOutputText.textContent = result.stderr || result.stdout;

    if (result.hex) {
      compilerOutputText.textContent += '\nProgram running...';
      stopButton.removeAttribute('disabled');
      executeProgram(result.hex);
    } else {
      runButton.removeAttribute('disabled');
    }
  } catch (err) {
    runButton.removeAttribute('disabled');
    revertButton.removeAttribute('disabled');
    alert('Failed: ' + err);
  } finally {
    statusLabel.textContent = '';
  }
}

function storeUserSnippet() {
  editor_history_util_1.EditorHistoryUtil.clearSnippet();
  editor_history_util_1.EditorHistoryUtil.storeSnippet(editor.getValue());
}

function stopCode() {
  stopButton.setAttribute('disabled', '1');
  runButton.removeAttribute('disabled');
  revertButton.removeAttribute('disabled');

  if (runner) {
    runner.stop();
    runner = null;
  }

  if (sim) sim.setSimRunning(false);
}

function setBlinkSnippet() {
  editor.setValue(window.defaultCode || BLINK_CODE);
  editor_history_util_1.EditorHistoryUtil.storeSnippet(editor.getValue());
}
},{"@wokwi/elements":"../../node_modules/@wokwi/elements/dist/esm/index.js","./compile":"compile.ts","./cpu-performance":"cpu-performance.ts","./execute":"execute.ts","./format-time":"format-time.ts","./index.css":"index.css","./utils/editor-history.util":"utils/editor-history.util.ts"}],"../../node_modules/parcel-bundler/src/builtins/hmr-runtime.js":[function(require,module,exports) {
var global = arguments[3];
var OVERLAY_ID = '__parcel__error__overlay__';
var OldModule = module.bundle.Module;

function Module(moduleName) {
  OldModule.call(this, moduleName);
  this.hot = {
    data: module.bundle.hotData,
    _acceptCallbacks: [],
    _disposeCallbacks: [],
    accept: function (fn) {
      this._acceptCallbacks.push(fn || function () {});
    },
    dispose: function (fn) {
      this._disposeCallbacks.push(fn);
    }
  };
  module.bundle.hotData = null;
}

module.bundle.Module = Module;
var checkedAssets, assetsToAccept;
var parent = module.bundle.parent;

if ((!parent || !parent.isParcelRequire) && typeof WebSocket !== 'undefined') {
  var hostname = "" || location.hostname;
  var protocol = location.protocol === 'https:' ? 'wss' : 'ws';
  var ws = new WebSocket(protocol + '://' + hostname + ':' + "57449" + '/');

  ws.onmessage = function (event) {
    checkedAssets = {};
    assetsToAccept = [];
    var data = JSON.parse(event.data);

    if (data.type === 'update') {
      var handled = false;
      data.assets.forEach(function (asset) {
        if (!asset.isNew) {
          var didAccept = hmrAcceptCheck(global.parcelRequire, asset.id);

          if (didAccept) {
            handled = true;
          }
        }
      }); // Enable HMR for CSS by default.

      handled = handled || data.assets.every(function (asset) {
        return asset.type === 'css' && asset.generated.js;
      });

      if (handled) {
        console.clear();
        data.assets.forEach(function (asset) {
          hmrApply(global.parcelRequire, asset);
        });
        assetsToAccept.forEach(function (v) {
          hmrAcceptRun(v[0], v[1]);
        });
      } else if (location.reload) {
        // `location` global exists in a web worker context but lacks `.reload()` function.
        location.reload();
      }
    }

    if (data.type === 'reload') {
      ws.close();

      ws.onclose = function () {
        location.reload();
      };
    }

    if (data.type === 'error-resolved') {
      console.log('[parcel] ✨ Error resolved');
      removeErrorOverlay();
    }

    if (data.type === 'error') {
      console.error('[parcel] 🚨  ' + data.error.message + '\n' + data.error.stack);
      removeErrorOverlay();
      var overlay = createErrorOverlay(data);
      document.body.appendChild(overlay);
    }
  };
}

function removeErrorOverlay() {
  var overlay = document.getElementById(OVERLAY_ID);

  if (overlay) {
    overlay.remove();
  }
}

function createErrorOverlay(data) {
  var overlay = document.createElement('div');
  overlay.id = OVERLAY_ID; // html encode message and stack trace

  var message = document.createElement('div');
  var stackTrace = document.createElement('pre');
  message.innerText = data.error.message;
  stackTrace.innerText = data.error.stack;
  overlay.innerHTML = '<div style="background: black; font-size: 16px; color: white; position: fixed; height: 100%; width: 100%; top: 0px; left: 0px; padding: 30px; opacity: 0.85; font-family: Menlo, Consolas, monospace; z-index: 9999;">' + '<span style="background: red; padding: 2px 4px; border-radius: 2px;">ERROR</span>' + '<span style="top: 2px; margin-left: 5px; position: relative;">🚨</span>' + '<div style="font-size: 18px; font-weight: bold; margin-top: 20px;">' + message.innerHTML + '</div>' + '<pre>' + stackTrace.innerHTML + '</pre>' + '</div>';
  return overlay;
}

function getParents(bundle, id) {
  var modules = bundle.modules;

  if (!modules) {
    return [];
  }

  var parents = [];
  var k, d, dep;

  for (k in modules) {
    for (d in modules[k][1]) {
      dep = modules[k][1][d];

      if (dep === id || Array.isArray(dep) && dep[dep.length - 1] === id) {
        parents.push(k);
      }
    }
  }

  if (bundle.parent) {
    parents = parents.concat(getParents(bundle.parent, id));
  }

  return parents;
}

function hmrApply(bundle, asset) {
  var modules = bundle.modules;

  if (!modules) {
    return;
  }

  if (modules[asset.id] || !bundle.parent) {
    var fn = new Function('require', 'module', 'exports', asset.generated.js);
    asset.isNew = !modules[asset.id];
    modules[asset.id] = [fn, asset.deps];
  } else if (bundle.parent) {
    hmrApply(bundle.parent, asset);
  }
}

function hmrAcceptCheck(bundle, id) {
  var modules = bundle.modules;

  if (!modules) {
    return;
  }

  if (!modules[id] && bundle.parent) {
    return hmrAcceptCheck(bundle.parent, id);
  }

  if (checkedAssets[id]) {
    return;
  }

  checkedAssets[id] = true;
  var cached = bundle.cache[id];
  assetsToAccept.push([bundle, id]);

  if (cached && cached.hot && cached.hot._acceptCallbacks.length) {
    return true;
  }

  return getParents(global.parcelRequire, id).some(function (id) {
    return hmrAcceptCheck(global.parcelRequire, id);
  });
}

function hmrAcceptRun(bundle, id) {
  var cached = bundle.cache[id];
  bundle.hotData = {};

  if (cached) {
    cached.hot.data = bundle.hotData;
  }

  if (cached && cached.hot && cached.hot._disposeCallbacks.length) {
    cached.hot._disposeCallbacks.forEach(function (cb) {
      cb(bundle.hotData);
    });
  }

  delete bundle.cache[id];
  bundle(id);
  cached = bundle.cache[id];

  if (cached && cached.hot && cached.hot._acceptCallbacks.length) {
    cached.hot._acceptCallbacks.forEach(function (cb) {
      cb();
    });

    return true;
  }
}
},{}]},{},["../../node_modules/parcel-bundler/src/builtins/hmr-runtime.js","index.ts"], null)
//# sourceMappingURL=/src.77de5100.js.map