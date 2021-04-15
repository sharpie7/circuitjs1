parcelRequire=function(e,r,t,n){var i,o="function"==typeof parcelRequire&&parcelRequire,u="function"==typeof require&&require;function f(t,n){if(!r[t]){if(!e[t]){var i="function"==typeof parcelRequire&&parcelRequire;if(!n&&i)return i(t,!0);if(o)return o(t,!0);if(u&&"string"==typeof t)return u(t);var c=new Error("Cannot find module '"+t+"'");throw c.code="MODULE_NOT_FOUND",c}p.resolve=function(r){return e[t][1][r]||r},p.cache={};var l=r[t]=new f.Module(t);e[t][0].call(l.exports,p,l,l.exports,this)}return r[t].exports;function p(e){return f(p.resolve(e))}}f.isParcelRequire=!0,f.Module=function(e){this.id=e,this.bundle=f,this.exports={}},f.modules=e,f.cache=r,f.parent=o,f.register=function(r,t){e[r]=[function(e,r){r.exports=t},{}]};for(var c=0;c<t.length;c++)try{f(t[c])}catch(e){i||(i=e)}if(t.length){var l=f(t[t.length-1]);"object"==typeof exports&&"undefined"!=typeof module?module.exports=l:"function"==typeof define&&define.amd?define(function(){return l}):n&&(this[n]=l)}if(parcelRequire=f,i)throw i;return f}({"tUV2":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.removeNodes=exports.reparentNodes=exports.isCEPolyfill=void 0;const e="undefined"!=typeof window&&null!=window.customElements&&void 0!==window.customElements.polyfillWrapFlushCallback;exports.isCEPolyfill=e;const o=(e,o,l=null,s=null)=>{for(;o!==l;){const l=o.nextSibling;e.insertBefore(o,s),o=l}};exports.reparentNodes=o;const l=(e,o,l=null)=>{for(;o!==l;){const l=o.nextSibling;e.removeChild(o),o=l}};exports.removeNodes=l;
},{}],"uVtz":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.lastAttributeNameRegex=exports.createMarker=exports.isTemplatePartActive=exports.Template=exports.boundAttributeSuffix=exports.markerRegex=exports.nodeMarker=exports.marker=void 0;const e=`{{lit-${String(Math.random()).slice(2)}}}`;exports.marker=e;const t=`\x3c!--${e}--\x3e`;exports.nodeMarker=t;const r=new RegExp(`${e}|${t}`);exports.markerRegex=r;const s="$lit$";exports.boundAttributeSuffix=s;class o{constructor(t,o){this.parts=[],this.element=o;const i=[],l=[],p=document.createTreeWalker(o.content,133,null,!1);let c=0,d=-1,u=0;const{strings:f,values:{length:h}}=t;for(;u<h;){const t=p.nextNode();if(null!==t){if(d++,1===t.nodeType){if(t.hasAttributes()){const e=t.attributes,{length:o}=e;let i=0;for(let t=0;t<o;t++)n(e[t].name,s)&&i++;for(;i-- >0;){const e=f[u],o=x.exec(e)[2],n=o.toLowerCase()+s,i=t.getAttribute(n);t.removeAttribute(n);const a=i.split(r);this.parts.push({type:"attribute",index:d,name:o,strings:a}),u+=a.length-1}}"TEMPLATE"===t.tagName&&(l.push(t),p.currentNode=t.content)}else if(3===t.nodeType){const o=t.data;if(o.indexOf(e)>=0){const e=t.parentNode,l=o.split(r),p=l.length-1;for(let r=0;r<p;r++){let o,i=l[r];if(""===i)o=a();else{const e=x.exec(i);null!==e&&n(e[2],s)&&(i=i.slice(0,e.index)+e[1]+e[2].slice(0,-s.length)+e[3]),o=document.createTextNode(i)}e.insertBefore(o,t),this.parts.push({type:"node",index:++d})}""===l[p]?(e.insertBefore(a(),t),i.push(t)):t.data=l[p],u+=p}}else if(8===t.nodeType)if(t.data===e){const e=t.parentNode;null!==t.previousSibling&&d!==c||(d++,e.insertBefore(a(),t)),c=d,this.parts.push({type:"node",index:d}),null===t.nextSibling?t.data="":(i.push(t),d--),u++}else{let r=-1;for(;-1!==(r=t.data.indexOf(e,r+1));)this.parts.push({type:"node",index:-1}),u++}}else p.currentNode=l.pop()}for(const e of i)e.parentNode.removeChild(e)}}exports.Template=o;const n=(e,t)=>{const r=e.length-t.length;return r>=0&&e.slice(r)===t},i=e=>-1!==e.index;exports.isTemplatePartActive=i;const a=()=>document.createComment("");exports.createMarker=a;const x=/([ \x09\x0a\x0c\x0d])([^\0-\x1F\x7F-\x9F "'>=/]+)([ \x09\x0a\x0c\x0d]*=[ \x09\x0a\x0c\x0d]*(?:[^ \x09\x0a\x0c\x0d"'`<>=]*|"[^"]*|'[^']*))$/;exports.lastAttributeNameRegex=x;
},{}],"KzFD":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.removeNodesFromTemplate=n,exports.insertNodeIntoTemplate=l;var e=require("./template.js");const t=133;function n(e,n){const{element:{content:r},parts:l}=e,u=document.createTreeWalker(r,t,null,!1);let c=o(l),d=l[c],s=-1,i=0;const a=[];let p=null;for(;u.nextNode();){s++;const e=u.currentNode;for(e.previousSibling===p&&(p=null),n.has(e)&&(a.push(e),null===p&&(p=e)),null!==p&&i++;void 0!==d&&d.index===s;)d.index=null!==p?-1:d.index-i,d=l[c=o(l,c)]}a.forEach(e=>e.parentNode.removeChild(e))}const r=e=>{let n=11===e.nodeType?0:1;const r=document.createTreeWalker(e,t,null,!1);for(;r.nextNode();)n++;return n},o=(t,n=-1)=>{for(let r=n+1;r<t.length;r++){const n=t[r];if((0,e.isTemplatePartActive)(n))return r}return-1};function l(e,n,l=null){const{element:{content:u},parts:c}=e;if(null==l)return void u.appendChild(n);const d=document.createTreeWalker(u,t,null,!1);let s=o(c),i=0,a=-1;for(;d.nextNode();){for(a++,d.currentNode===l&&(i=r(n),l.parentNode.insertBefore(n,l));-1!==s&&c[s].index===a;){if(i>0){for(;-1!==s;)c[s].index+=i,s=o(c,s);return}s=o(c,s)}}}
},{"./template.js":"uVtz"}],"m8nK":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.isDirective=exports.directive=void 0;const e=new WeakMap,t=t=>(...s)=>{const i=t(...s);return e.set(i,!0),i};exports.directive=t;const s=t=>"function"==typeof t&&e.has(t);exports.isDirective=s;
},{}],"b70n":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.nothing=exports.noChange=void 0;const e={};exports.noChange=e;const o={};exports.nothing=o;
},{}],"YArp":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.TemplateInstance=void 0;var e=require("./dom.js"),t=require("./template.js");class s{constructor(e,t,s){this.__parts=[],this.template=e,this.processor=t,this.options=s}update(e){let t=0;for(const s of this.__parts)void 0!==s&&s.setValue(e[t]),t++;for(const s of this.__parts)void 0!==s&&s.commit()}_clone(){const s=e.isCEPolyfill?this.template.element.content.cloneNode(!0):document.importNode(this.template.element.content,!0),o=[],r=this.template.parts,n=document.createTreeWalker(s,133,null,!1);let i,p=0,l=0,a=n.nextNode();for(;p<r.length;)if(i=r[p],(0,t.isTemplatePartActive)(i)){for(;l<i.index;)l++,"TEMPLATE"===a.nodeName&&(o.push(a),n.currentNode=a.content),null===(a=n.nextNode())&&(n.currentNode=o.pop(),a=n.nextNode());if("node"===i.type){const e=this.processor.handleTextExpression(this.options);e.insertAfterNode(a.previousSibling),this.__parts.push(e)}else this.__parts.push(...this.processor.handleAttributeExpressions(a,i.name,i.strings,this.options));p++}else this.__parts.push(void 0),p++;return e.isCEPolyfill&&(document.adoptNode(s),customElements.upgrade(s)),s}}exports.TemplateInstance=s;
},{"./dom.js":"tUV2","./template.js":"uVtz"}],"xfRr":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.SVGTemplateResult=exports.TemplateResult=void 0;var e=require("./dom.js"),t=require("./template.js");const s=` ${t.marker} `;class r{constructor(e,t,s,r){this.strings=e,this.values=t,this.type=s,this.processor=r}getHTML(){const e=this.strings.length-1;let r="",n=!1;for(let l=0;l<e;l++){const e=this.strings[l],i=e.lastIndexOf("\x3c!--");n=(i>-1||n)&&-1===e.indexOf("--\x3e",i+1);const o=t.lastAttributeNameRegex.exec(e);r+=null===o?e+(n?s:t.nodeMarker):e.substr(0,o.index)+o[1]+o[2]+t.boundAttributeSuffix+o[3]+t.marker}return r+=this.strings[e]}getTemplateElement(){const e=document.createElement("template");return e.innerHTML=this.getHTML(),e}}exports.TemplateResult=r;class n extends r{getHTML(){return`<svg>${super.getHTML()}</svg>`}getTemplateElement(){const t=super.getTemplateElement(),s=t.content,r=s.firstChild;return s.removeChild(r),(0,e.reparentNodes)(s,r.firstChild),t}}exports.SVGTemplateResult=n;
},{"./dom.js":"tUV2","./template.js":"uVtz"}],"wzje":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.EventPart=exports.PropertyPart=exports.PropertyCommitter=exports.BooleanAttributePart=exports.NodePart=exports.AttributePart=exports.AttributeCommitter=exports.isIterable=exports.isPrimitive=void 0;var t=require("./directive.js"),e=require("./dom.js"),i=require("./part.js"),s=require("./template-instance.js"),n=require("./template-result.js"),r=require("./template.js");const o=t=>null===t||!("object"==typeof t||"function"==typeof t);exports.isPrimitive=o;const a=t=>Array.isArray(t)||!(!t||!t[Symbol.iterator]);exports.isIterable=a;class h{constructor(t,e,i){this.dirty=!0,this.element=t,this.name=e,this.strings=i,this.parts=[];for(let s=0;s<i.length-1;s++)this.parts[s]=this._createPart()}_createPart(){return new l(this)}_getValue(){const t=this.strings,e=t.length-1;let i="";for(let s=0;s<e;s++){i+=t[s];const e=this.parts[s];if(void 0!==e){const t=e.value;if(o(t)||!a(t))i+="string"==typeof t?t:String(t);else for(const e of t)i+="string"==typeof e?e:String(e)}}return i+=t[e]}commit(){this.dirty&&(this.dirty=!1,this.element.setAttribute(this.name,this._getValue()))}}exports.AttributeCommitter=h;class l{constructor(t){this.value=void 0,this.committer=t}setValue(e){e===i.noChange||o(e)&&e===this.value||(this.value=e,(0,t.isDirective)(e)||(this.committer.dirty=!0))}commit(){for(;(0,t.isDirective)(this.value);){const t=this.value;this.value=i.noChange,t(this)}this.value!==i.noChange&&this.committer.commit()}}exports.AttributePart=l;class u{constructor(t){this.value=void 0,this.__pendingValue=void 0,this.options=t}appendInto(t){this.startNode=t.appendChild((0,r.createMarker)()),this.endNode=t.appendChild((0,r.createMarker)())}insertAfterNode(t){this.startNode=t,this.endNode=t.nextSibling}appendIntoPart(t){t.__insert(this.startNode=(0,r.createMarker)()),t.__insert(this.endNode=(0,r.createMarker)())}insertAfterPart(t){t.__insert(this.startNode=(0,r.createMarker)()),this.endNode=t.endNode,t.endNode=this.startNode}setValue(t){this.__pendingValue=t}commit(){if(null===this.startNode.parentNode)return;for(;(0,t.isDirective)(this.__pendingValue);){const t=this.__pendingValue;this.__pendingValue=i.noChange,t(this)}const e=this.__pendingValue;e!==i.noChange&&(o(e)?e!==this.value&&this.__commitText(e):e instanceof n.TemplateResult?this.__commitTemplateResult(e):e instanceof Node?this.__commitNode(e):a(e)?this.__commitIterable(e):e===i.nothing?(this.value=i.nothing,this.clear()):this.__commitText(e))}__insert(t){this.endNode.parentNode.insertBefore(t,this.endNode)}__commitNode(t){this.value!==t&&(this.clear(),this.__insert(t),this.value=t)}__commitText(t){const e=this.startNode.nextSibling,i="string"==typeof(t=null==t?"":t)?t:String(t);e===this.endNode.previousSibling&&3===e.nodeType?e.data=i:this.__commitNode(document.createTextNode(i)),this.value=t}__commitTemplateResult(t){const e=this.options.templateFactory(t);if(this.value instanceof s.TemplateInstance&&this.value.template===e)this.value.update(t.values);else{const i=new s.TemplateInstance(e,t.processor,this.options),n=i._clone();i.update(t.values),this.__commitNode(n),this.value=i}}__commitIterable(t){Array.isArray(this.value)||(this.value=[],this.clear());const e=this.value;let i,s=0;for(const n of t)void 0===(i=e[s])&&(i=new u(this.options),e.push(i),0===s?i.appendIntoPart(this):i.insertAfterPart(e[s-1])),i.setValue(n),i.commit(),s++;s<e.length&&(e.length=s,this.clear(i&&i.endNode))}clear(t=this.startNode){(0,e.removeNodes)(this.startNode.parentNode,t.nextSibling,this.endNode)}}exports.NodePart=u;class d{constructor(t,e,i){if(this.value=void 0,this.__pendingValue=void 0,2!==i.length||""!==i[0]||""!==i[1])throw new Error("Boolean attributes can only contain a single expression");this.element=t,this.name=e,this.strings=i}setValue(t){this.__pendingValue=t}commit(){for(;(0,t.isDirective)(this.__pendingValue);){const t=this.__pendingValue;this.__pendingValue=i.noChange,t(this)}if(this.__pendingValue===i.noChange)return;const e=!!this.__pendingValue;this.value!==e&&(e?this.element.setAttribute(this.name,""):this.element.removeAttribute(this.name),this.value=e),this.__pendingValue=i.noChange}}exports.BooleanAttributePart=d;class c extends h{constructor(t,e,i){super(t,e,i),this.single=2===i.length&&""===i[0]&&""===i[1]}_createPart(){return new p(this)}_getValue(){return this.single?this.parts[0].value:super._getValue()}commit(){this.dirty&&(this.dirty=!1,this.element[this.name]=this._getValue())}}exports.PropertyCommitter=c;class p extends l{}exports.PropertyPart=p;let _=!1;(()=>{try{const e={get capture(){return _=!0,!1}};window.addEventListener("test",e,e),window.removeEventListener("test",e,e)}catch(t){}})();class m{constructor(t,e,i){this.value=void 0,this.__pendingValue=void 0,this.element=t,this.eventName=e,this.eventContext=i,this.__boundHandleEvent=(t=>this.handleEvent(t))}setValue(t){this.__pendingValue=t}commit(){for(;(0,t.isDirective)(this.__pendingValue);){const t=this.__pendingValue;this.__pendingValue=i.noChange,t(this)}if(this.__pendingValue===i.noChange)return;const e=this.__pendingValue,s=this.value,n=null==e||null!=s&&(e.capture!==s.capture||e.once!==s.once||e.passive!==s.passive),r=null!=e&&(null==s||n);n&&this.element.removeEventListener(this.eventName,this.__boundHandleEvent,this.__options),r&&(this.__options=v(e),this.element.addEventListener(this.eventName,this.__boundHandleEvent,this.__options)),this.value=e,this.__pendingValue=i.noChange}handleEvent(t){"function"==typeof this.value?this.value.call(this.eventContext||this.element,t):this.value.handleEvent(t)}}exports.EventPart=m;const v=t=>t&&(_?{capture:t.capture,passive:t.passive,once:t.once}:t.capture);
},{"./directive.js":"m8nK","./dom.js":"tUV2","./part.js":"b70n","./template-instance.js":"YArp","./template-result.js":"xfRr","./template.js":"uVtz"}],"fz7E":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.templateFactory=t,exports.templateCaches=void 0;var e=require("./template.js");function t(t){let s=r.get(t.type);void 0===s&&(s={stringsArray:new WeakMap,keyString:new Map},r.set(t.type,s));let n=s.stringsArray.get(t.strings);if(void 0!==n)return n;const a=t.strings.join(e.marker);return void 0===(n=s.keyString.get(a))&&(n=new e.Template(t,t.getTemplateElement()),s.keyString.set(a,n)),s.stringsArray.set(t.strings,n),n}const r=new Map;exports.templateCaches=r;
},{"./template.js":"uVtz"}],"XeeT":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.render=exports.parts=void 0;var e=require("./dom.js"),t=require("./parts.js"),r=require("./template-factory.js");const s=new WeakMap;exports.parts=s;const o=(o,a,p)=>{let d=s.get(a);void 0===d&&((0,e.removeNodes)(a,a.firstChild),s.set(a,d=new t.NodePart(Object.assign({templateFactory:r.templateFactory},p))),d.appendInto(a)),d.setValue(o),d.commit()};exports.render=o;
},{"./dom.js":"tUV2","./parts.js":"wzje","./template-factory.js":"fz7E"}],"p68d":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.defaultTemplateProcessor=exports.DefaultTemplateProcessor=void 0;var e=require("./parts.js");class t{handleAttributeExpressions(t,r,s,o){const a=r[0];if("."===a){return new e.PropertyCommitter(t,r.slice(1),s).parts}return"@"===a?[new e.EventPart(t,r.slice(1),o.eventContext)]:"?"===a?[new e.BooleanAttributePart(t,r.slice(1),s)]:new e.AttributeCommitter(t,r,s).parts}handleTextExpression(t){return new e.NodePart(t)}}exports.DefaultTemplateProcessor=t;const r=new t;exports.defaultTemplateProcessor=r;
},{"./parts.js":"wzje"}],"zUh2":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),Object.defineProperty(exports,"DefaultTemplateProcessor",{enumerable:!0,get:function(){return e.DefaultTemplateProcessor}}),Object.defineProperty(exports,"defaultTemplateProcessor",{enumerable:!0,get:function(){return e.defaultTemplateProcessor}}),Object.defineProperty(exports,"SVGTemplateResult",{enumerable:!0,get:function(){return t.SVGTemplateResult}}),Object.defineProperty(exports,"TemplateResult",{enumerable:!0,get:function(){return t.TemplateResult}}),Object.defineProperty(exports,"directive",{enumerable:!0,get:function(){return r.directive}}),Object.defineProperty(exports,"isDirective",{enumerable:!0,get:function(){return r.isDirective}}),Object.defineProperty(exports,"removeNodes",{enumerable:!0,get:function(){return n.removeNodes}}),Object.defineProperty(exports,"reparentNodes",{enumerable:!0,get:function(){return n.reparentNodes}}),Object.defineProperty(exports,"noChange",{enumerable:!0,get:function(){return o.noChange}}),Object.defineProperty(exports,"nothing",{enumerable:!0,get:function(){return o.nothing}}),Object.defineProperty(exports,"AttributeCommitter",{enumerable:!0,get:function(){return i.AttributeCommitter}}),Object.defineProperty(exports,"AttributePart",{enumerable:!0,get:function(){return i.AttributePart}}),Object.defineProperty(exports,"BooleanAttributePart",{enumerable:!0,get:function(){return i.BooleanAttributePart}}),Object.defineProperty(exports,"EventPart",{enumerable:!0,get:function(){return i.EventPart}}),Object.defineProperty(exports,"isIterable",{enumerable:!0,get:function(){return i.isIterable}}),Object.defineProperty(exports,"isPrimitive",{enumerable:!0,get:function(){return i.isPrimitive}}),Object.defineProperty(exports,"NodePart",{enumerable:!0,get:function(){return i.NodePart}}),Object.defineProperty(exports,"PropertyCommitter",{enumerable:!0,get:function(){return i.PropertyCommitter}}),Object.defineProperty(exports,"PropertyPart",{enumerable:!0,get:function(){return i.PropertyPart}}),Object.defineProperty(exports,"parts",{enumerable:!0,get:function(){return u.parts}}),Object.defineProperty(exports,"render",{enumerable:!0,get:function(){return u.render}}),Object.defineProperty(exports,"templateCaches",{enumerable:!0,get:function(){return p.templateCaches}}),Object.defineProperty(exports,"templateFactory",{enumerable:!0,get:function(){return p.templateFactory}}),Object.defineProperty(exports,"TemplateInstance",{enumerable:!0,get:function(){return a.TemplateInstance}}),Object.defineProperty(exports,"createMarker",{enumerable:!0,get:function(){return s.createMarker}}),Object.defineProperty(exports,"isTemplatePartActive",{enumerable:!0,get:function(){return s.isTemplatePartActive}}),Object.defineProperty(exports,"Template",{enumerable:!0,get:function(){return s.Template}}),exports.svg=exports.html=void 0;var e=require("./lib/default-template-processor.js"),t=require("./lib/template-result.js"),r=require("./lib/directive.js"),n=require("./lib/dom.js"),o=require("./lib/part.js"),i=require("./lib/parts.js"),u=require("./lib/render.js"),p=require("./lib/template-factory.js"),a=require("./lib/template-instance.js"),s=require("./lib/template.js");"undefined"!=typeof window&&(window.litHtmlVersions||(window.litHtmlVersions=[])).push("1.2.1");const l=(r,...n)=>new t.TemplateResult(r,n,"html",e.defaultTemplateProcessor);exports.html=l;const c=(r,...n)=>new t.SVGTemplateResult(r,n,"svg",e.defaultTemplateProcessor);exports.svg=c;
},{"./lib/default-template-processor.js":"p68d","./lib/template-result.js":"xfRr","./lib/directive.js":"m8nK","./lib/dom.js":"tUV2","./lib/part.js":"b70n","./lib/parts.js":"wzje","./lib/render.js":"XeeT","./lib/template-factory.js":"fz7E","./lib/template-instance.js":"YArp","./lib/template.js":"uVtz"}],"onlA":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),Object.defineProperty(exports,"html",{enumerable:!0,get:function(){return a.html}}),Object.defineProperty(exports,"svg",{enumerable:!0,get:function(){return a.svg}}),Object.defineProperty(exports,"TemplateResult",{enumerable:!0,get:function(){return a.TemplateResult}}),exports.render=void 0;var e=require("./dom.js"),t=require("./modify-template.js"),r=require("./render.js"),n=require("./template-factory.js"),o=require("./template-instance.js"),s=require("./template.js"),a=require("../lit-html.js");const l=(e,t)=>`${e}--${t}`;let i=!0;void 0===window.ShadyCSS?i=!1:void 0===window.ShadyCSS.prepareTemplateDom&&(console.warn("Incompatible ShadyCSS version detected. Please update to at least @webcomponents/webcomponentsjs@2.0.2 and @webcomponents/shadycss@1.3.1."),i=!1);const d=e=>t=>{const r=l(t.type,e);let o=n.templateCaches.get(r);void 0===o&&(o={stringsArray:new WeakMap,keyString:new Map},n.templateCaches.set(r,o));let a=o.stringsArray.get(t.strings);if(void 0!==a)return a;const d=t.strings.join(s.marker);if(void 0===(a=o.keyString.get(d))){const r=t.getTemplateElement();i&&window.ShadyCSS.prepareTemplateDom(r,e),a=new s.Template(t,r),o.keyString.set(d,a)}return o.stringsArray.set(t.strings,a),a},p=["html","svg"],c=e=>{p.forEach(r=>{const o=n.templateCaches.get(l(r,e));void 0!==o&&o.keyString.forEach(e=>{const{element:{content:r}}=e,n=new Set;Array.from(r.querySelectorAll("style")).forEach(e=>{n.add(e)}),(0,t.removeNodesFromTemplate)(e,n)})})},m=new Set,y=(e,r,n)=>{m.add(e);const o=n?n.element:document.createElement("template"),s=r.querySelectorAll("style"),{length:a}=s;if(0===a)return void window.ShadyCSS.prepareTemplateStyles(o,e);const l=document.createElement("style");for(let t=0;t<a;t++){const e=s[t];e.parentNode.removeChild(e),l.textContent+=e.textContent}c(e);const i=o.content;n?(0,t.insertNodeIntoTemplate)(n,l,i.firstChild):i.insertBefore(l,i.firstChild),window.ShadyCSS.prepareTemplateStyles(o,e);const d=i.querySelector("style");if(window.ShadyCSS.nativeShadow&&null!==d)r.insertBefore(d.cloneNode(!0),r.firstChild);else if(n){i.insertBefore(l,i.firstChild);const e=new Set;e.add(l),(0,t.removeNodesFromTemplate)(n,e)}},S=(t,n,s)=>{if(!s||"object"!=typeof s||!s.scopeName)throw new Error("The `scopeName` option is required.");const a=s.scopeName,l=r.parts.has(n),p=i&&11===n.nodeType&&!!n.host,c=p&&!m.has(a),S=c?document.createDocumentFragment():n;if((0,r.render)(t,S,Object.assign({templateFactory:d(a)},s)),c){const t=r.parts.get(S);r.parts.delete(S);const s=t.value instanceof o.TemplateInstance?t.value.template:void 0;y(a,S,s),(0,e.removeNodes)(n,n.firstChild),n.appendChild(S),r.parts.set(n,t)}!l&&p&&window.ShadyCSS.styleElement(n.host)};exports.render=S;
},{"./dom.js":"tUV2","./modify-template.js":"KzFD","./render.js":"XeeT","./template-factory.js":"fz7E","./template-instance.js":"YArp","./template.js":"uVtz","../lit-html.js":"zUh2"}],"xgJI":[function(require,module,exports) {
"use strict";var t;Object.defineProperty(exports,"__esModule",{value:!0}),exports.UpdatingElement=exports.notEqual=exports.defaultConverter=void 0,window.JSCompiler_renameProperty=((t,e)=>t);const e={toAttribute(t,e){switch(e){case Boolean:return t?"":null;case Object:case Array:return null==t?t:JSON.stringify(t)}return t},fromAttribute(t,e){switch(e){case Boolean:return null!==t;case Number:return null===t?null:Number(t);case Object:case Array:return JSON.parse(t)}return t}};exports.defaultConverter=e;const r=(t,e)=>e!==t&&(e==e||t==t);exports.notEqual=r;const s={attribute:!0,type:String,converter:e,reflect:!1,hasChanged:r},i=1,a=4,o=8,p=16,n="finalized";class h extends HTMLElement{constructor(){super(),this._updateState=0,this._instanceProperties=void 0,this._updatePromise=new Promise(t=>this._enableUpdatingResolver=t),this._changedProperties=new Map,this._reflectingProperties=void 0,this.initialize()}static get observedAttributes(){this.finalize();const t=[];return this._classProperties.forEach((e,r)=>{const s=this._attributeNameForProperty(r,e);void 0!==s&&(this._attributeToPropertyMap.set(s,r),t.push(s))}),t}static _ensureClassProperties(){if(!this.hasOwnProperty(JSCompiler_renameProperty("_classProperties",this))){this._classProperties=new Map;const t=Object.getPrototypeOf(this)._classProperties;void 0!==t&&t.forEach((t,e)=>this._classProperties.set(e,t))}}static createProperty(t,e=s){if(this._ensureClassProperties(),this._classProperties.set(t,e),e.noAccessor||this.prototype.hasOwnProperty(t))return;const r="symbol"==typeof t?Symbol():`__${t}`,i=this.getPropertyDescriptor(t,r,e);void 0!==i&&Object.defineProperty(this.prototype,t,i)}static getPropertyDescriptor(t,e,r){return{get(){return this[e]},set(r){const s=this[t];this[e]=r,this._requestUpdate(t,s)},configurable:!0,enumerable:!0}}static getPropertyOptions(t){return this._classProperties&&this._classProperties.get(t)||s}static finalize(){const t=Object.getPrototypeOf(this);if(t.hasOwnProperty(n)||t.finalize(),this[n]=!0,this._ensureClassProperties(),this._attributeToPropertyMap=new Map,this.hasOwnProperty(JSCompiler_renameProperty("properties",this))){const t=this.properties,e=[...Object.getOwnPropertyNames(t),..."function"==typeof Object.getOwnPropertySymbols?Object.getOwnPropertySymbols(t):[]];for(const r of e)this.createProperty(r,t[r])}}static _attributeNameForProperty(t,e){const r=e.attribute;return!1===r?void 0:"string"==typeof r?r:"string"==typeof t?t.toLowerCase():void 0}static _valueHasChanged(t,e,s=r){return s(t,e)}static _propertyValueFromAttribute(t,r){const s=r.type,i=r.converter||e,a="function"==typeof i?i:i.fromAttribute;return a?a(t,s):t}static _propertyValueToAttribute(t,r){if(void 0===r.reflect)return;const s=r.type,i=r.converter;return(i&&i.toAttribute||e.toAttribute)(t,s)}initialize(){this._saveInstanceProperties(),this._requestUpdate()}_saveInstanceProperties(){this.constructor._classProperties.forEach((t,e)=>{if(this.hasOwnProperty(e)){const t=this[e];delete this[e],this._instanceProperties||(this._instanceProperties=new Map),this._instanceProperties.set(e,t)}})}_applyInstanceProperties(){this._instanceProperties.forEach((t,e)=>this[e]=t),this._instanceProperties=void 0}connectedCallback(){this.enableUpdating()}enableUpdating(){void 0!==this._enableUpdatingResolver&&(this._enableUpdatingResolver(),this._enableUpdatingResolver=void 0)}disconnectedCallback(){}attributeChangedCallback(t,e,r){e!==r&&this._attributeToProperty(t,r)}_propertyToAttribute(t,e,r=s){const i=this.constructor,a=i._attributeNameForProperty(t,r);if(void 0!==a){const t=i._propertyValueToAttribute(e,r);if(void 0===t)return;this._updateState=this._updateState|o,null==t?this.removeAttribute(a):this.setAttribute(a,t),this._updateState=this._updateState&~o}}_attributeToProperty(t,e){if(this._updateState&o)return;const r=this.constructor,s=r._attributeToPropertyMap.get(t);if(void 0!==s){const t=r.getPropertyOptions(s);this._updateState=this._updateState|p,this[s]=r._propertyValueFromAttribute(e,t),this._updateState=this._updateState&~p}}_requestUpdate(t,e){let r=!0;if(void 0!==t){const s=this.constructor,i=s.getPropertyOptions(t);s._valueHasChanged(this[t],e,i.hasChanged)?(this._changedProperties.has(t)||this._changedProperties.set(t,e),!0!==i.reflect||this._updateState&p||(void 0===this._reflectingProperties&&(this._reflectingProperties=new Map),this._reflectingProperties.set(t,i))):r=!1}!this._hasRequestedUpdate&&r&&(this._updatePromise=this._enqueueUpdate())}requestUpdate(t,e){return this._requestUpdate(t,e),this.updateComplete}async _enqueueUpdate(){this._updateState=this._updateState|a;try{await this._updatePromise}catch(e){}const t=this.performUpdate();return null!=t&&await t,!this._hasRequestedUpdate}get _hasRequestedUpdate(){return this._updateState&a}get hasUpdated(){return this._updateState&i}performUpdate(){this._instanceProperties&&this._applyInstanceProperties();let t=!1;const e=this._changedProperties;try{(t=this.shouldUpdate(e))?this.update(e):this._markUpdated()}catch(r){throw t=!1,this._markUpdated(),r}t&&(this._updateState&i||(this._updateState=this._updateState|i,this.firstUpdated(e)),this.updated(e))}_markUpdated(){this._changedProperties=new Map,this._updateState=this._updateState&~a}get updateComplete(){return this._getUpdateComplete()}_getUpdateComplete(){return this._updatePromise}shouldUpdate(t){return!0}update(t){void 0!==this._reflectingProperties&&this._reflectingProperties.size>0&&(this._reflectingProperties.forEach((t,e)=>this._propertyToAttribute(e,this[e],t)),this._reflectingProperties=void 0),this._markUpdated()}updated(t){}firstUpdated(t){}}exports.UpdatingElement=h,h[t=n]=!0;
},{}],"QCBo":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.property=i,exports.internalProperty=s,exports.query=c,exports.queryAsync=u,exports.queryAll=l,exports.eventOptions=f,exports.queryAssignedNodes=m,exports.customElement=void 0;const e=(e,t)=>(window.customElements.define(e,t),t),t=(e,t)=>{const{kind:r,elements:n}=t;return{kind:r,elements:n,finisher(t){window.customElements.define(e,t)}}},r=r=>n=>"function"==typeof n?e(r,n):t(r,n);exports.customElement=r;const n=(e,t)=>"method"!==t.kind||!t.descriptor||"value"in t.descriptor?{kind:"field",key:Symbol(),placement:"own",descriptor:{},initializer(){"function"==typeof t.initializer&&(this[t.key]=t.initializer.call(this))},finisher(r){r.createProperty(t.key,e)}}:Object.assign(Object.assign({},t),{finisher(r){r.createProperty(t.key,e)}}),o=(e,t,r)=>{t.constructor.createProperty(r,e)};function i(e){return(t,r)=>void 0!==r?o(e,t,r):n(e,t)}function s(e){return i({attribute:!1,hasChanged:null==e?void 0:e.hasChanged})}function c(e){return(t,r)=>{const n={get(){return this.renderRoot.querySelector(e)},enumerable:!0,configurable:!0};return void 0!==r?a(n,t,r):d(n,t)}}function u(e){return(t,r)=>{const n={async get(){return await this.updateComplete,this.renderRoot.querySelector(e)},enumerable:!0,configurable:!0};return void 0!==r?a(n,t,r):d(n,t)}}function l(e){return(t,r)=>{const n={get(){return this.renderRoot.querySelectorAll(e)},enumerable:!0,configurable:!0};return void 0!==r?a(n,t,r):d(n,t)}}const a=(e,t,r)=>{Object.defineProperty(t,r,e)},d=(e,t)=>({kind:"method",placement:"prototype",key:t.key,descriptor:e}),p=(e,t)=>Object.assign(Object.assign({},t),{finisher(r){Object.assign(r.prototype[t.key],e)}}),y=(e,t,r)=>{Object.assign(t[r],e)};function f(e){return(t,r)=>void 0!==r?y(e,t,r):p(e,t)}function m(e="",t=!1){return(r,n)=>{const o={get(){const r=`slot${e?`[name=${e}]`:""}`,n=this.renderRoot.querySelector(r);return n&&n.assignedNodes({flatten:t})},enumerable:!0,configurable:!0};return void 0!==n?a(o,r,n):d(o,r)}}
},{}],"ZfrT":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.css=exports.unsafeCSS=exports.CSSResult=exports.supportsAdoptingStyleSheets=void 0;const e="adoptedStyleSheets"in Document.prototype&&"replace"in CSSStyleSheet.prototype;exports.supportsAdoptingStyleSheets=e;const t=Symbol();class s{constructor(e,s){if(s!==t)throw new Error("CSSResult is not constructable. Use `unsafeCSS` or `css` instead.");this.cssText=e}get styleSheet(){return void 0===this._styleSheet&&(e?(this._styleSheet=new CSSStyleSheet,this._styleSheet.replaceSync(this.cssText)):this._styleSheet=null),this._styleSheet}toString(){return this.cssText}}exports.CSSResult=s;const r=e=>new s(String(e),t);exports.unsafeCSS=r;const o=e=>{if(e instanceof s)return e.cssText;if("number"==typeof e)return e;throw new Error(`Value passed to 'css' function must be a 'css' function result: ${e}. Use 'unsafeCSS' to pass non-literal values, but\n            take care to ensure page security.`)},n=(e,...r)=>{const n=r.reduce((t,s,r)=>t+o(s)+e[r+1],e[0]);return new s(n,t)};exports.css=n;
},{}],"AInt":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0});var e={LitElement:!0,html:!0,svg:!0,TemplateResult:!0,SVGTemplateResult:!0};Object.defineProperty(exports,"html",{enumerable:!0,get:function(){return o.html}}),Object.defineProperty(exports,"svg",{enumerable:!0,get:function(){return o.svg}}),Object.defineProperty(exports,"TemplateResult",{enumerable:!0,get:function(){return o.TemplateResult}}),Object.defineProperty(exports,"SVGTemplateResult",{enumerable:!0,get:function(){return o.SVGTemplateResult}}),exports.LitElement=void 0;var t=require("lit-html/lib/shady-render.js"),r=require("./lib/updating-element.js");Object.keys(r).forEach(function(t){"default"!==t&&"__esModule"!==t&&(Object.prototype.hasOwnProperty.call(e,t)||t in exports&&exports[t]===r[t]||Object.defineProperty(exports,t,{enumerable:!0,get:function(){return r[t]}}))});var s=require("./lib/decorators.js");Object.keys(s).forEach(function(t){"default"!==t&&"__esModule"!==t&&(Object.prototype.hasOwnProperty.call(e,t)||t in exports&&exports[t]===s[t]||Object.defineProperty(exports,t,{enumerable:!0,get:function(){return s[t]}}))});var o=require("lit-html/lit-html.js"),n=require("./lib/css-tag.js");Object.keys(n).forEach(function(t){"default"!==t&&"__esModule"!==t&&(Object.prototype.hasOwnProperty.call(e,t)||t in exports&&exports[t]===n[t]||Object.defineProperty(exports,t,{enumerable:!0,get:function(){return n[t]}}))}),(window.litElementVersions||(window.litElementVersions=[])).push("2.3.1");const i={};class l extends r.UpdatingElement{static getStyles(){return this.styles}static _getUniqueStyles(){if(this.hasOwnProperty(JSCompiler_renameProperty("_styles",this)))return;const e=this.getStyles();if(void 0===e)this._styles=[];else if(Array.isArray(e)){const t=(e,r)=>e.reduceRight((e,r)=>Array.isArray(r)?t(r,e):(e.add(r),e),r),r=t(e,new Set),s=[];r.forEach(e=>s.unshift(e)),this._styles=s}else this._styles=[e]}initialize(){super.initialize(),this.constructor._getUniqueStyles(),this.renderRoot=this.createRenderRoot(),window.ShadowRoot&&this.renderRoot instanceof window.ShadowRoot&&this.adoptStyles()}createRenderRoot(){return this.attachShadow({mode:"open"})}adoptStyles(){const e=this.constructor._styles;0!==e.length&&(void 0===window.ShadyCSS||window.ShadyCSS.nativeShadow?n.supportsAdoptingStyleSheets?this.renderRoot.adoptedStyleSheets=e.map(e=>e.styleSheet):this._needsShimAdoptedStyleSheets=!0:window.ShadyCSS.ScopingShim.prepareAdoptedCssText(e.map(e=>e.cssText),this.localName))}connectedCallback(){super.connectedCallback(),this.hasUpdated&&void 0!==window.ShadyCSS&&window.ShadyCSS.styleElement(this)}update(e){const t=this.render();super.update(e),t!==i&&this.constructor.render(t,this.renderRoot,{scopeName:this.localName,eventContext:this}),this._needsShimAdoptedStyleSheets&&(this._needsShimAdoptedStyleSheets=!1,this.constructor._styles.forEach(e=>{const t=document.createElement("style");t.textContent=e.cssText,this.renderRoot.appendChild(t)}))}render(){return i}}exports.LitElement=l,l.finalized=!0,l.render=t.render;
},{"lit-html/lib/shady-render.js":"onlA","./lib/updating-element.js":"xgJI","./lib/decorators.js":"QCBo","lit-html/lit-html.js":"zUh2","./lib/css-tag.js":"ZfrT"}],"U1nF":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.SevenSegmentElement=void 0;var e=require("lit-element"),t=function(e,t,o,r){var l,n=arguments.length,s=n<3?t:null===r?r=Object.getOwnPropertyDescriptor(t,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(e,t,o,r);else for(var i=e.length-1;i>=0;i--)(l=e[i])&&(s=(n<3?l(s):n>3?l(t,o,s):l(t,o))||s);return n>3&&s&&Object.defineProperty(t,o,s),s};let o=class extends e.LitElement{constructor(){super(...arguments),this.color="red",this.values=[0,0,0,0,0,0,0,0]}static get styles(){return e.css`
      polygon {
        transform: scale(0.9);
        transform-origin: 50% 50%;
        transform-box: fill-box;
      }
    `}render(){const{color:t,values:o}=this,r=e=>o[e]?t:"#ddd";return e.html`
      <svg
        width="12mm"
        height="18.5mm"
        version="1.1"
        viewBox="0 0 12 18.5"
        xmlns="http://www.w3.org/2000/svg"
      >
        <g transform="skewX(-8) translate(2, 0)">
          <polygon points="2 0 8 0 9 1 8 2 2 2 1 1" fill="${r(0)}" />
          <polygon points="10 2 10 8 9 9 8 8 8 2 9 1" fill="${r(1)}" />
          <polygon points="10 10 10 16 9 17 8 16 8 10 9 9" fill="${r(2)}" />
          <polygon points="8 18 2 18 1 17 2 16 8 16 9 17" fill="${r(3)}" />
          <polygon points="0 16 0 10 1 9 2 10 2 16 1 17" fill="${r(4)}" />
          <polygon points="0 8 0 2 1 1 2 2 2 8 1 9" fill=${r(5)} />
          <polygon points="2 8 8 8 9 9 8 10 2 10 1 9" fill=${r(6)} />
        </g>
        <circle cx="11" cy="17" r="1.1" fill="${r(7)}" />
      </svg>
    `}};exports.SevenSegmentElement=o,t([(0,e.property)()],o.prototype,"color",void 0),t([(0,e.property)({type:Array})],o.prototype,"values",void 0),exports.SevenSegmentElement=o=t([(0,e.customElement)("wokwi-7segment")],o);
},{"lit-element":"AInt"}],"RLG9":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.ArduinoUnoElement=void 0;var t=require("lit-element"),e=function(t,e,l,r){var a,s=arguments.length,i=s<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,l):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)i=Reflect.decorate(t,e,l,r);else for(var n=t.length-1;n>=0;n--)(a=t[n])&&(i=(s<3?a(i):s>3?a(e,l,i):a(e,l))||i);return s>3&&i&&Object.defineProperty(e,l,i),i};let l=class extends t.LitElement{constructor(){super(...arguments),this.led13=!1,this.ledRX=!1,this.ledTX=!1,this.ledPower=!1}render(){const{ledPower:e,led13:l,ledRX:r,ledTX:a}=this;return t.html`
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
          <rect width="${.38+25.4}" height="2.54" fill="url(#pins)"></rect>
        </g>
        <g transform="translate(44.421 1.27)">
          <rect width="${20.7}" height="2.54" fill="url(#pins)"></rect>
        </g>
        <g transform="translate(26.641 49.53)">
          <rect width="${20.7}" height="2.54" fill="url(#pins)"></rect>
        </g>
        <g transform="translate(49.501 49.53)">
          <rect width="${.38+15.24}" height="2.54" fill="url(#pins)"></rect>
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
          ${e&&t.svg`<circle cx="1.3" cy="0.55" r="1.3" fill="#80ff80" filter="url(#ledFilter)" />`}
        </g>

        <text fill="#fff">
          <tspan x="60.88" y="17.5">ON</tspan>
        </text>

        <g transform="translate(26.87,11.69)">
          <use xlink:href="#led-body" />
          ${l&&t.svg`<circle cx="1.3" cy="0.55" r="1.3" fill="#ff8080" filter="url(#ledFilter)" />`}
        </g>

        <g transform="translate(26.9, 16.2)">
          <use xlink:href="#led-body" />
          ${a&&t.svg`<circle cx="0.975" cy="0.55" r="1.3" fill="yellow" filter="url(#ledFilter)" />`}
        </g>

        <g transform="translate(26.9, 18.5)">
          <use xlink:href="#led-body" />
          ${r&&t.svg`<circle cx="0.975" cy="0.55" r="1.3" fill="yellow" filter="url(#ledFilter)" />`}
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
    `}};exports.ArduinoUnoElement=l,e([(0,t.property)()],l.prototype,"led13",void 0),e([(0,t.property)()],l.prototype,"ledRX",void 0),e([(0,t.property)()],l.prototype,"ledTX",void 0),e([(0,t.property)()],l.prototype,"ledPower",void 0),exports.ArduinoUnoElement=l=e([(0,t.customElement)("wokwi-arduino-uno")],l);
},{"lit-element":"AInt"}],"Fq3W":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.fontA00=void 0;const e=new Uint8Array([0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,0,0,4,0,10,10,10,0,0,0,0,0,10,10,31,10,31,10,10,0,4,30,5,14,20,15,4,0,3,19,8,4,2,25,24,0,6,9,5,2,21,9,22,0,6,4,2,0,0,0,0,0,8,4,2,2,2,4,8,0,2,4,8,8,8,4,2,0,0,4,21,14,21,4,0,0,0,4,4,31,4,4,0,0,0,0,0,0,6,4,2,0,0,0,0,31,0,0,0,0,0,0,0,0,0,6,6,0,0,16,8,4,2,1,0,0,14,17,25,21,19,17,14,0,4,6,4,4,4,4,14,0,14,17,16,8,4,2,31,0,31,8,4,8,16,17,14,0,8,12,10,9,31,8,8,0,31,1,15,16,16,17,14,0,12,2,1,15,17,17,14,0,31,17,16,8,4,4,4,0,14,17,17,14,17,17,14,0,14,17,17,30,16,8,6,0,0,6,6,0,6,6,0,0,0,6,6,0,6,4,2,0,8,4,2,1,2,4,8,0,0,0,31,0,31,0,0,0,2,4,8,16,8,4,2,0,14,17,16,8,4,0,4,0,14,17,16,22,21,21,14,0,14,17,17,17,31,17,17,0,15,17,17,15,17,17,15,0,14,17,1,1,1,17,14,0,7,9,17,17,17,9,7,0,31,1,1,15,1,1,31,0,31,1,1,15,1,1,1,0,14,17,1,29,17,17,30,0,17,17,17,31,17,17,17,0,14,4,4,4,4,4,14,0,28,8,8,8,8,9,6,0,17,9,5,3,5,9,17,0,1,1,1,1,1,1,31,0,17,27,21,21,17,17,17,0,17,17,19,21,25,17,17,0,14,17,17,17,17,17,14,0,15,17,17,15,1,1,1,0,14,17,17,17,21,9,22,0,15,17,17,15,5,9,17,0,30,1,1,14,16,16,15,0,31,4,4,4,4,4,4,0,17,17,17,17,17,17,14,0,17,17,17,17,17,10,4,0,17,17,17,21,21,21,10,0,17,17,10,4,10,17,17,0,17,17,17,10,4,4,4,0,31,16,8,4,2,1,31,0,7,1,1,1,1,1,7,0,17,10,31,4,31,4,4,0,14,8,8,8,8,8,14,0,4,10,17,0,0,0,0,0,0,0,0,0,0,0,31,0,2,4,8,0,0,0,0,0,0,0,14,16,30,17,30,0,1,1,13,19,17,17,15,0,0,0,14,1,1,17,14,0,16,16,22,25,17,17,30,0,0,0,14,17,31,1,14,0,12,18,2,7,2,2,2,0,0,30,17,17,30,16,14,0,1,1,13,19,17,17,17,0,4,0,6,4,4,4,14,0,8,0,12,8,8,9,6,0,1,1,9,5,3,5,9,0,6,4,4,4,4,4,14,0,0,0,11,21,21,17,17,0,0,0,13,19,17,17,17,0,0,0,14,17,17,17,14,0,0,0,15,17,15,1,1,0,0,0,22,25,30,16,16,0,0,0,13,19,1,1,1,0,0,0,14,1,14,16,15,0,2,2,7,2,2,18,12,0,0,0,17,17,17,25,22,0,0,0,17,17,17,10,4,0,0,0,17,21,21,21,10,0,0,0,17,10,4,10,17,0,0,0,17,17,30,16,14,0,0,0,31,8,4,2,31,0,8,4,4,2,4,4,8,0,4,4,4,4,4,4,4,0,2,4,4,8,4,4,2,0,0,4,8,31,8,4,0,0,0,4,2,31,2,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,5,7,0,28,4,4,4,0,0,0,0,0,0,0,4,4,4,7,0,0,0,0,0,1,2,4,0,0,0,0,6,6,0,0,0,0,31,16,31,16,8,4,0,0,0,31,16,12,4,2,0,0,0,8,4,6,5,4,0,0,0,4,31,17,16,12,0,0,0,31,4,4,4,31,0,0,0,8,31,12,10,9,0,0,0,2,31,18,10,2,0,0,0,0,14,8,8,31,0,0,0,15,8,15,8,15,0,0,0,0,21,21,16,12,0,0,0,0,31,0,0,0,0,31,16,20,12,4,4,2,0,16,8,4,6,5,4,4,0,4,31,17,17,16,8,4,0,0,31,4,4,4,4,31,0,8,31,8,12,10,9,8,0,2,31,18,18,18,18,9,0,4,31,4,31,4,4,4,0,0,30,18,17,16,8,6,0,2,30,9,8,8,8,4,0,0,31,16,16,16,16,31,0,10,31,10,10,8,4,2,0,0,3,16,19,16,8,7,0,0,31,16,8,4,10,17,0,2,31,18,10,2,2,28,0,0,17,17,18,16,8,6,0,0,30,18,21,24,8,6,0,8,7,4,31,4,4,2,0,0,21,21,21,16,8,4,0,14,0,31,4,4,4,2,0,2,2,2,6,10,2,2,0,4,4,31,4,4,2,1,0,0,14,0,0,0,0,31,0,0,31,16,10,4,10,1,0,4,31,8,4,14,21,4,0,8,8,8,8,8,4,2,0,0,4,8,17,17,17,17,0,1,1,31,1,1,1,30,0,0,31,16,16,16,8,6,0,0,2,5,8,16,16,0,0,4,31,4,4,21,21,4,0,0,31,16,16,10,4,8,0,0,14,0,14,0,14,16,0,0,4,2,1,17,31,16,0,0,16,16,10,4,10,1,0,0,31,2,31,2,2,28,0,2,2,31,18,10,2,2,0,0,14,8,8,8,8,31,0,0,31,16,31,16,16,31,0,14,0,31,16,16,8,4,0,9,9,9,9,8,4,2,0,0,4,5,5,21,21,13,0,0,1,1,17,9,5,3,0,0,31,17,17,17,17,31,0,0,31,17,17,16,8,4,0,0,3,0,16,16,8,7,0,4,9,2,0,0,0,0,0,7,5,7,0,0,0,0,0,0,0,18,21,9,9,22,0,10,0,14,16,30,17,30,0,0,0,14,17,15,17,15,1,0,0,14,1,6,17,14,0,0,0,17,17,17,25,23,1,0,0,30,5,9,17,14,0,0,0,12,18,17,17,15,1,0,0,30,17,17,17,30,16,0,0,28,4,4,5,2,0,0,8,11,8,0,0,0,0,8,0,12,8,8,8,8,8,0,5,2,5,0,0,0,0,0,4,14,5,21,14,4,0,2,2,7,2,7,2,30,0,14,0,13,19,17,17,17,0,10,0,14,17,17,17,14,0,0,0,13,19,17,17,15,1,0,0,22,25,17,17,30,16,0,14,17,31,17,17,14,0,0,0,0,26,21,11,0,0,0,0,14,17,17,10,27,0,10,0,17,17,17,17,25,22,31,1,2,4,2,1,31,0,0,0,31,10,10,10,25,0,31,0,17,10,4,10,17,0,0,0,17,17,17,17,30,16,0,16,15,4,31,4,4,0,0,0,31,2,30,18,17,0,0,0,31,21,31,17,17,0,0,4,0,31,0,4,0,0,0,0,0,0,0,0,0,0,31,31,31,31,31,31,31,31]);exports.fontA00=e;
},{}],"CZgF":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.LCD1602Element=void 0;var t=require("lit-element"),r=require("./lcd1602-font-a00"),e=function(t,r,e,o){var i,s=arguments.length,c=s<3?r:null===o?o=Object.getOwnPropertyDescriptor(r,e):o;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)c=Reflect.decorate(t,r,e,o);else for(var h=t.length-1;h>=0;h--)(i=t[h])&&(c=(s<3?i(c):s>3?i(r,e,c):i(r,e))||c);return s>3&&c&&Object.defineProperty(r,e,c),c};const o=2,i=16,s=3.55,c=5.95,h={green:"#6cb201",blue:"#000eff"};let n=class extends t.LitElement{constructor(){super(...arguments),this.color="black",this.background="green",this.characters=new Uint8Array(32),this.font=r.fontA00,this.cursor=!1,this.blink=!1,this.cursorX=0,this.cursorY=0,this.backlight=!0}static get styles(){return t.css`
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
    `}path(t){const r=[];for(let e=0;e<t.length;e++){const o=e%16*3.55,i=5.95*Math.floor(e/16);for(let s=0;s<8;s++){const c=this.font[8*t[e]+s];for(let t=0;t<5;t++)if(c&1<<t){const e=(o+.6*t).toFixed(2),c=(i+.7*s).toFixed(2);r.push(`M ${e} ${c}h0.55v0.65h-0.55Z`)}}}return r.join(" ")}renderCursor(){const r=12.45+3.55*this.cursorX,e=12.55+5.95*this.cursorY;if(this.cursorX<0||this.cursorX>=16||this.cursorY<0||this.cursorY>=2)return null;const o=[];if(this.blink&&o.push(t.svg`
        <rect x="${r}" y="${e}" width="2.95" height="5.55" fill="${this.color}">
          <animate
            attributeName="opacity"
            values="0;0;0;0;1;1;0;0;0;0"
            dur="1s"
            fill="freeze"
            repeatCount="indefinite"
          />
        </rect>
      `),this.cursor){const i=e+.7*7;o.push(t.svg`<rect x="${r}" y="${i}" width="2.95" height="0.65" fill="${this.color}" />`)}return o}render(){const{color:r,characters:e,background:o}=this,i=this.backlight?0:.5,s=o in h?h[o]:h;return t.html`
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
        <rect x="7.55" y="10.3" width="66" height="16" rx="1.5" ry="1.5" fill="${s}" />
        <rect x="7.55" y="10.3" width="66" height="16" rx="1.5" ry="1.5" opacity="${i}" />
        <rect x="12.45" y="12.55" width="56.2" height="11.5" fill="url(#characters)" />
        <path d="${this.path(e)}" transform="translate(12.45, 12.55)" fill="${r}" />
        ${this.renderCursor()}
      </svg>
    `}};exports.LCD1602Element=n,e([(0,t.property)()],n.prototype,"color",void 0),e([(0,t.property)()],n.prototype,"background",void 0),e([(0,t.property)({type:Array})],n.prototype,"characters",void 0),e([(0,t.property)()],n.prototype,"font",void 0),e([(0,t.property)()],n.prototype,"cursor",void 0),e([(0,t.property)()],n.prototype,"blink",void 0),e([(0,t.property)()],n.prototype,"cursorX",void 0),e([(0,t.property)()],n.prototype,"cursorY",void 0),e([(0,t.property)()],n.prototype,"backlight",void 0),exports.LCD1602Element=n=e([(0,t.customElement)("wokwi-lcd1602")],n);
},{"lit-element":"AInt","./lcd1602-font-a00":"Fq3W"}],"gnIK":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.fontA02=void 0;const e=new Uint8Array([0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,6,14,30,14,6,2,0,8,12,14,15,14,12,8,0,18,9,27,0,0,0,0,0,27,18,9,0,0,0,0,0,4,14,31,0,4,14,31,0,31,14,4,0,31,14,4,0,0,14,31,31,31,14,0,0,16,16,20,18,31,2,4,0,4,14,21,4,4,4,4,0,4,4,4,4,21,14,4,0,0,4,8,31,8,4,0,0,0,4,2,31,2,4,0,0,8,4,2,4,8,0,31,0,2,4,8,4,2,0,31,0,0,4,4,14,14,31,0,0,0,31,14,14,4,4,0,0,0,0,0,0,0,0,0,0,4,4,4,4,0,0,4,0,10,10,10,0,0,0,0,0,10,10,31,10,31,10,10,0,4,30,5,14,20,15,4,0,3,19,8,4,2,25,24,0,6,9,5,2,21,9,22,0,6,4,2,0,0,0,0,0,8,4,2,2,2,4,8,0,2,4,8,8,8,4,2,0,0,4,21,14,21,4,0,0,0,4,4,31,4,4,0,0,0,0,0,0,6,4,2,0,0,0,0,31,0,0,0,0,0,0,0,0,0,6,6,0,0,16,8,4,2,1,0,0,14,17,25,21,19,17,14,0,4,6,4,4,4,4,14,0,14,17,16,8,4,2,31,0,31,8,4,8,16,17,14,0,8,12,10,9,31,8,8,0,31,1,15,16,16,17,14,0,12,2,1,15,17,17,14,0,31,17,16,8,4,4,4,0,14,17,17,14,17,17,14,0,14,17,17,30,16,8,6,0,0,6,6,0,6,6,0,0,0,6,6,0,6,4,2,0,8,4,2,1,2,4,8,0,0,0,31,0,31,0,0,0,2,4,8,16,8,4,2,0,14,17,16,8,4,0,4,0,14,17,16,22,21,21,14,0,4,10,17,17,31,17,17,0,15,17,17,15,17,17,15,0,14,17,1,1,1,17,14,0,7,9,17,17,17,9,7,0,31,1,1,15,1,1,31,0,31,1,1,15,1,1,1,0,14,17,1,29,17,17,30,0,17,17,17,31,17,17,17,0,14,4,4,4,4,4,14,0,28,8,8,8,8,9,6,0,17,9,5,3,5,9,17,0,1,1,1,1,1,1,31,0,17,27,21,21,17,17,17,0,17,17,19,21,25,17,17,0,14,17,17,17,17,17,14,0,15,17,17,15,1,1,1,0,14,17,17,17,21,9,22,0,15,17,17,15,5,9,17,0,14,17,1,14,16,17,14,0,31,4,4,4,4,4,4,0,17,17,17,17,17,17,14,0,17,17,17,17,17,10,4,0,17,17,17,21,21,21,10,0,17,17,10,4,10,17,17,0,17,17,17,10,4,4,4,0,31,16,8,4,2,1,31,0,14,2,2,2,2,2,14,0,0,1,2,4,8,16,0,0,14,8,8,8,8,8,14,0,4,10,17,0,0,0,0,0,0,0,0,0,0,0,31,0,2,4,8,0,0,0,0,0,0,0,14,16,30,17,30,0,1,1,13,19,17,17,15,0,0,0,14,1,1,17,14,0,16,16,22,25,17,17,30,0,0,0,14,17,31,1,14,0,12,18,2,7,2,2,2,0,0,0,30,17,30,16,14,0,1,1,13,19,17,17,17,0,4,0,4,6,4,4,14,0,8,0,12,8,8,9,6,0,1,1,9,5,3,5,9,0,6,4,4,4,4,4,14,0,0,0,11,21,21,21,21,0,0,0,13,19,17,17,17,0,0,0,14,17,17,17,14,0,0,0,15,17,15,1,1,0,0,0,22,25,30,16,16,0,0,0,13,19,1,1,1,0,0,0,14,1,14,16,15,0,2,2,7,2,2,18,12,0,0,0,17,17,17,25,22,0,0,0,17,17,17,10,4,0,0,0,17,17,21,21,10,0,0,0,17,10,4,10,17,0,0,0,17,17,30,16,14,0,0,0,31,8,4,2,31,0,8,4,4,2,4,4,8,0,4,4,4,4,4,4,4,0,2,4,4,8,4,4,2,0,0,0,0,22,9,0,0,0,4,10,17,17,17,31,0,0,31,17,1,15,17,17,15,30,20,20,18,17,31,17,17,0,21,21,21,14,21,21,21,0,15,16,16,12,16,16,15,0,17,17,25,21,19,17,17,10,4,17,17,25,21,19,17,0,30,20,20,20,20,21,18,0,31,17,17,17,17,17,17,0,17,17,17,10,4,2,1,0,17,17,17,17,17,31,16,0,17,17,17,30,16,16,16,0,0,21,21,21,21,21,31,0,21,21,21,21,21,31,16,0,3,2,2,14,18,18,14,0,17,17,17,19,21,21,19,0,14,17,20,26,16,17,14,0,0,0,18,21,9,9,22,0,4,12,20,20,4,7,7,0,31,17,1,1,1,1,1,0,0,0,31,10,10,10,25,0,31,1,2,4,2,1,31,0,0,0,30,9,9,9,6,12,20,28,20,20,23,27,24,0,0,16,14,5,4,4,8,0,4,14,14,14,31,4,0,0,14,17,17,31,17,17,14,0,0,14,17,17,17,10,27,0,12,18,4,10,17,17,14,0,0,0,26,21,11,0,0,0,0,10,31,31,31,14,4,0,0,0,14,1,6,17,14,0,14,17,17,17,17,17,17,0,27,27,27,27,27,27,27,0,4,0,0,4,4,4,4,0,4,14,5,5,21,14,4,0,12,2,2,7,2,18,13,0,0,17,14,10,14,17,0,0,17,10,31,4,31,4,4,0,4,4,4,0,4,4,4,0,12,18,4,10,4,9,6,0,8,20,4,31,4,5,2,0,31,17,21,29,21,17,31,0,14,16,30,17,30,0,31,0,0,20,10,5,10,20,0,0,9,21,21,23,21,21,9,0,30,17,17,30,20,18,17,0,31,17,21,17,25,21,31,0,4,2,6,0,0,0,0,6,9,9,9,6,0,0,0,0,4,4,31,4,4,0,31,6,9,4,2,15,0,0,0,7,8,6,8,7,0,0,0,7,9,7,1,9,29,9,24,0,17,17,17,25,23,1,1,0,30,25,25,30,24,24,24,0,0,0,0,6,6,0,0,0,0,0,10,17,21,21,10,2,3,2,2,7,0,0,0,0,14,17,17,17,14,0,31,0,0,5,10,20,10,5,0,17,9,5,10,13,10,30,8,17,9,5,10,21,16,8,28,3,2,3,18,27,20,28,16,0,4,0,4,2,1,17,14,2,4,4,10,17,31,17,17,8,4,4,10,17,31,17,17,4,10,0,14,17,31,17,17,22,9,0,14,17,31,17,17,10,0,4,10,17,31,17,17,4,10,4,14,17,31,17,17,0,28,6,5,29,7,5,29,14,17,1,1,17,14,8,12,2,4,0,31,1,15,1,31,8,4,0,31,1,15,1,31,4,10,0,31,1,15,1,31,0,10,0,31,1,15,1,31,2,4,0,14,4,4,4,14,8,4,0,14,4,4,4,14,4,10,0,14,4,4,4,14,0,10,0,14,4,4,4,14,0,14,18,18,23,18,18,14,22,9,0,17,19,21,25,17,2,4,14,17,17,17,17,14,8,4,14,17,17,17,17,14,4,10,0,14,17,17,17,14,22,9,0,14,17,17,17,14,10,0,14,17,17,17,17,14,0,0,17,10,4,10,17,0,0,14,4,14,21,14,4,14,2,4,17,17,17,17,17,14,8,4,17,17,17,17,17,14,4,10,0,17,17,17,17,14,10,0,17,17,17,17,17,14,8,4,17,10,4,4,4,4,3,2,14,18,18,14,2,7,0,12,18,18,14,18,18,13,2,4,0,14,16,30,17,30,8,4,0,14,16,30,17,30,4,10,0,14,16,30,17,30,22,9,0,14,16,30,17,30,0,10,0,14,16,30,17,30,4,10,4,14,16,30,17,30,0,0,11,20,30,5,21,10,0,0,14,1,17,14,4,6,2,4,0,14,17,31,1,14,8,4,0,14,17,31,1,14,4,10,0,14,17,31,1,14,0,10,0,14,17,31,1,14,2,4,0,4,6,4,4,14,8,4,0,4,6,4,4,14,4,10,0,4,6,4,4,14,0,10,0,4,6,4,4,14,0,5,2,5,8,30,17,14,22,9,0,13,19,17,17,17,2,4,0,14,17,17,17,14,8,4,0,14,17,17,17,14,0,4,10,0,14,17,17,14,0,22,9,0,14,17,17,14,0,10,0,14,17,17,17,14,0,0,4,0,31,0,4,0,0,8,4,14,21,14,4,2,2,4,0,17,17,17,25,22,8,4,0,17,17,17,25,22,4,10,0,17,17,17,25,22,0,10,0,17,17,17,25,22,0,8,4,17,17,30,16,14,0,6,4,12,20,12,4,14,0,10,0,17,17,30,16,14]);exports.fontA02=e;
},{}],"AwTz":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.LEDElement=void 0;var e=require("lit-element"),t=function(e,t,l,i){var o,r=arguments.length,c=r<3?t:null===i?i=Object.getOwnPropertyDescriptor(t,l):i;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)c=Reflect.decorate(e,t,l,i);else for(var s=e.length-1;s>=0;s--)(o=e[s])&&(c=(r<3?o(c):r>3?o(t,l,c):o(t,l))||c);return r>3&&c&&Object.defineProperty(t,l,c),c};const l={red:"#ff8080",green:"#80ff80",blue:"#8080ff",yellow:"#ffff80",orange:"#ffcf80",white:"#ffffff"};let i=class extends e.LitElement{constructor(){super(...arguments),this.value=!1,this.brightness=1,this.color="red",this.lightColor=null,this.label=""}static get styles(){return e.css`
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
    `}render(){const{color:t,lightColor:i}=this,o=i||l[t]||"#ff8080",r=this.brightness?.3+.7*this.brightness:0,c=this.value&&this.brightness>Number.EPSILON;return e.html`
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
            fill="${t}"
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
          <g class="light" style="display: ${c?"":"none"}">
            <ellipse
              cx="8"
              cy="10"
              rx="10"
              ry="10"
              fill="${o}"
              filter="url(#light2)"
              style="opacity: ${r}"
            ></ellipse>
            <ellipse cx="8" cy="10" rx="2" ry="2" fill="white" filter="url(#light1)"></ellipse>
            <ellipse
              cx="8"
              cy="10"
              rx="3"
              ry="3"
              fill="white"
              filter="url(#light1)"
              style="opacity: ${r}"
            ></ellipse>
          </g>
        </svg>
        <span class="led-label">${this.label}</span>
      </div>
    `}};exports.LEDElement=i,t([(0,e.property)()],i.prototype,"value",void 0),t([(0,e.property)()],i.prototype,"brightness",void 0),t([(0,e.property)()],i.prototype,"color",void 0),t([(0,e.property)()],i.prototype,"lightColor",void 0),t([(0,e.property)()],i.prototype,"label",void 0),exports.LEDElement=i=t([(0,e.customElement)("wokwi-led")],i);
},{"lit-element":"AInt"}],"Lqo3":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.NeoPixelElement=void 0;var e=require("lit-element"),t=function(e,t,l,i){var r,h=arguments.length,c=h<3?t:null===i?i=Object.getOwnPropertyDescriptor(t,l):i;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)c=Reflect.decorate(e,t,l,i);else for(var o=e.length-1;o>=0;o--)(r=e[o])&&(c=(h<3?r(c):h>3?r(t,l,c):r(t,l))||c);return h>3&&c&&Object.defineProperty(t,l,c),c};let l=class extends e.LitElement{constructor(){super(...arguments),this.r=0,this.g=0,this.b=0}render(){const{r:t,g:l,b:i}=this,r=e=>e>.001?.7+.3*e:0,h=Math.max(t,l,i),c=Math.min(t,l,i),o=h-c,a=Math.max(1,2-20*o),s=.1+Math.max(2*h-5*o,0),p=e=>h?Math.floor(255*Math.min((e=>e>.005?.1+.9*e:0)(e/h)*a,1)):255,f=`rgb(${p(t)}, ${p(l)}, ${p(i)})`,d=242-(h>.1&&o<.2?Math.floor(50*h*(1-o/.2)):0),n=`rgb(${d}, ${d}, ${d})`;return e.html`
      <svg
        width="5.6631mm"
        height="5mm"
        version="1.1"
        viewBox="0 0 5.6631 5"
        xmlns="http://www.w3.org/2000/svg"
      >
        <filter id="light1" x="-0.8" y="-0.8" height="2.8" width="2.8">
          <feGaussianBlur stdDeviation="${Math.max(.1,h)}" />
        </filter>
        <filter id="light2" x="-0.8" y="-0.8" height="2.2" width="2.8">
          <feGaussianBlur stdDeviation="0.5" />
        </filter>
        <rect x=".33308" y="0" width="5" height="5" fill="${n}" />
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
          opacity=${r(t)}
          filter="url(#light1)"
        ></ellipse>
        <ellipse
          cx="3.5"
          cy="3.2"
          rx="0.3"
          ry="0.3"
          fill="green"
          opacity=${r(l)}
          filter="url(#light1)"
        ></ellipse>
        <ellipse
          cx="3.3"
          cy="1.45"
          rx="0.3"
          ry="0.3"
          fill="blue"
          opacity=${r(i)}
          filter="url(#light1)"
        ></ellipse>
        <ellipse
          cx="3"
          cy="2.5"
          rx="2.2"
          ry="2.2"
          opacity="${(e=>e>.005?s+e*(1-s):0)(h)}"
          fill="${f}"
          filter="url(#light2)"
        ></ellipse>
      </svg>
    `}};exports.NeoPixelElement=l,t([(0,e.property)()],l.prototype,"r",void 0),t([(0,e.property)()],l.prototype,"g",void 0),t([(0,e.property)()],l.prototype,"b",void 0),exports.NeoPixelElement=l=t([(0,e.customElement)("wokwi-neopixel")],l);
},{"lit-element":"AInt"}],"a5Yb":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.PushbuttonElement=void 0;var e=require("lit-element"),t=function(e,t,r,o){var c,s=arguments.length,n=s<3?t:null===o?o=Object.getOwnPropertyDescriptor(t,r):o;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)n=Reflect.decorate(e,t,r,o);else for(var i=e.length-1;i>=0;i--)(c=e[i])&&(n=(s<3?c(n):s>3?c(t,r,n):c(t,r))||n);return s>3&&n&&Object.defineProperty(t,r,n),n};const r=32;let o=class extends e.LitElement{constructor(){super(...arguments),this.color="red",this.pressed=!1}static get styles(){return e.css`
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
    `}render(){const{color:t}=this;return e.html`
      <button
        aria-label="${t} pushbutton"
        @mousedown=${this.down}
        @mouseup=${this.up}
        @touchstart=${this.down}
        @touchend=${this.up}
        @keydown=${e=>32===e.keyCode&&this.down()}
        @keyup=${e=>32===e.keyCode&&this.up()}
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
              <stop stop-color="${t}" offset="0.3" />
              <stop stop-color="${t}" offset="0.5" />
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
              fill="${t}"
              stroke="#2f2f2f"
              stroke-opacity=".47"
              stroke-width=".08"
            />
          </g>
        </svg>
      </button>
    `}down(){this.pressed||(this.pressed=!0,this.dispatchEvent(new Event("button-press")))}up(){this.pressed&&(this.pressed=!1,this.dispatchEvent(new Event("button-release")))}};exports.PushbuttonElement=o,t([(0,e.property)()],o.prototype,"color",void 0),t([(0,e.property)()],o.prototype,"pressed",void 0),exports.PushbuttonElement=o=t([(0,e.customElement)("wokwi-pushbutton")],o);
},{"lit-element":"AInt"}],"X1an":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.ResistorElement=void 0;var e=require("lit-element"),t=function(e,t,r,o){var l,s=arguments.length,i=s<3?t:null===o?o=Object.getOwnPropertyDescriptor(t,r):o;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)i=Reflect.decorate(e,t,r,o);else for(var a=e.length-1;a>=0;a--)(l=e[a])&&(i=(s<3?l(i):s>3?l(t,r,i):l(t,r))||i);return s>3&&i&&Object.defineProperty(t,r,i),i};const r={[-2]:"silver",[-1]:"#c4a000",0:"black",1:"brown",2:"red",3:"orange",4:"yellow",5:"green",6:"blue",7:"violet",8:"gray",9:"white"};let o=class extends e.LitElement{constructor(){super(...arguments),this.value="1000"}breakValue(e){const t=e>=1e10?9:e>=1e9?8:e>=1e8?7:e>=1e7?6:e>=1e6?5:e>=1e5?4:e>=1e4?3:e>=1e3?2:e>=100?1:e>=10?0:e>=1?-1:-2,r=Math.round(e/10**t);return 0===e?[0,0]:t<0&&r%10==0?[r/10,t+1]:[Math.round(r%100),t]}render(){const{value:t}=this,o=parseFloat(t),[l,s]=this.breakValue(o),i=r[Math.floor(l/10)],a=r[l%10],n=r[s];return e.html`
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
            fill="${i}"
          />
          <path d="m6.4482 0.29411v2.4117h0.77205v-2.4117z" fill="${a}" />
          <path d="m8.5245 0.29411v2.4117h0.77205v-2.4117z" fill="${n}" />
          <path
            d="m11.054 0c-0.15608 0-0.30749 0.015253-0.45277 0.043268v2.9134c0.14527 0.028012 0.29669 0.043268 0.45277 0.043268 0.10912 0 0.21539-0.00867 0.31957-0.02234v-2.955c-0.10418-0.013767-0.21044-0.022624-0.31957-0.022624z"
            fill="#c4a000"
          />
        </g>
      </svg>
    `}};exports.ResistorElement=o,t([(0,e.property)()],o.prototype,"value",void 0),exports.ResistorElement=o=t([(0,e.customElement)("wokwi-resistor")],o);
},{"lit-element":"AInt"}],"L6Zo":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.MembraneKeypadElement=void 0;var e=require("lit-element"),t=function(e,t,r,s){var o,i=arguments.length,n=i<3?t:null===s?s=Object.getOwnPropertyDescriptor(t,r):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)n=Reflect.decorate(e,t,r,s);else for(var d=e.length-1;d>=0;d--)(o=e[d])&&(n=(i<3?o(n):i>3?o(t,r,n):o(t,r))||n);return i>3&&n&&Object.defineProperty(t,r,n),n};const r=32;function s(e){return!isNaN(parseFloat(e))}let o=class extends e.LitElement{constructor(){super(...arguments),this.threeColumns=!1,this.pressedKeys=new Set}renderKey(t,r,o){const i=s(t)?"blue-key":"red-key",n=t.toUpperCase();return e.svg`<g
      transform="translate(${r} ${o})"
      tabindex="0"
      class=${i}
      data-key-name=${n}
      @blur=${e=>{this.up(t,e.currentTarget)}}
      @mousedown=${()=>this.down(t)}
      @mouseup=${()=>this.up(t)}
      @touchstart=${()=>this.down(t)}
      @touchend=${()=>this.up(t)}
      @keydown=${e=>32===e.keyCode&&this.down(t,e.currentTarget)}
      @keyup=${e=>32===e.keyCode&&this.up(t,e.currentTarget)}
    >
      <use xlink:href="#key" />
      <text x="5.6" y="8.1">${t}</text>
    </g>`}render(){const t=!this.threeColumns,r=t?70.336:55.336;return e.html`
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
        width="${r}mm"
        height="76mm"
        version="1.1"
        viewBox="0 0 ${r} 76"
        font-family="sans-serif"
        font-size="8.2px"
        text-anchor="middle"
        xmlns="http://www.w3.org/2000/svg"
        @keydown=${e=>this.keyStrokeDown(e.key)}
        @keyup=${e=>this.keyStrokeUp(e.key)}
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
        <rect x="0" y="0" width="${r}" height="76" rx="5" ry="5" fill="#454449" />
        <rect
          x="2.78"
          y="3.25"
          width="${t?65:50}"
          height="68.6"
          rx="3.5"
          ry="3.5"
          fill="none"
          stroke="#b1b5b9"
          stroke-width="1"
        />

        <!-- Blue keys -->
        <g fill="#4e90d7">
          <g>${this.renderKey("1",7,10.7)}</g>
          <g>${this.renderKey("2",22,10.7)}</g>
          <g>${this.renderKey("3",37,10.7)}</g>
          <g>${this.renderKey("4",7,25)}</g>
          <g>${this.renderKey("5",22,25)}</g>
          <g>${this.renderKey("6",37,25)}</g>
          <g>${this.renderKey("7",7,39.3)}</g>
          <g>${this.renderKey("8",22,39.3)}</g>
          <g>${this.renderKey("9",37,39.3)}</g>
          <g>${this.renderKey("0",22,53.6)}</g>
        </g>

        <!-- Red keys -->
        <g fill="#e94541">
          <g>${this.renderKey("*",7,53.6)}</g>
          <g>${this.renderKey("#",37,53.6)}</g>
          ${t&&e.svg`
              <g>${this.renderKey("A",52,10.7)}</g>
              <g>${this.renderKey("B",52,25)}</g>
              <g>${this.renderKey("C",52,39.3)}</g>
              <g>${this.renderKey("D",52,53.6)}</g>
          `}
        </g>
      </svg>
    `}down(e,t){this.pressedKeys.has(e)||(t&&t.classList.add("pressed"),this.pressedKeys.add(e),this.dispatchEvent(new CustomEvent("button-press",{detail:{key:e}})))}up(e,t){this.pressedKeys.has(e)&&(t&&t.classList.remove("pressed"),this.pressedKeys.delete(e),this.dispatchEvent(new CustomEvent("button-release",{detail:{key:e}})))}keyStrokeDown(e){var t;const r=e.toUpperCase(),s=null===(t=this.shadowRoot)||void 0===t?void 0:t.querySelector(`[data-key-name="${r}"]`);s&&this.down(r,s)}keyStrokeUp(e){var t,r,s;const o=e.toUpperCase(),i=null===(t=this.shadowRoot)||void 0===t?void 0:t.querySelector(`[data-key-name="${o}"]`),n=null===(r=this.shadowRoot)||void 0===r?void 0:r.querySelectorAll(".pressed");"Shift"===e&&(null===(s=n)||void 0===s||s.forEach(e=>{const t=e.dataset.keyName;this.up(t,e)})),i&&this.up(o,i)}};exports.MembraneKeypadElement=o,t([(0,e.property)()],o.prototype,"threeColumns",void 0),exports.MembraneKeypadElement=o=t([(0,e.customElement)("wokwi-membrane-keypad")],o);
},{"lit-element":"AInt"}],"bjcn":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.styleMap=void 0;var e=require("../lit-html.js");const t=new WeakMap,r=(0,e.directive)(r=>s=>{if(!(s instanceof e.AttributePart)||s instanceof e.PropertyPart||"style"!==s.committer.name||s.committer.parts.length>1)throw new Error("The `styleMap` directive must be used in the style attribute and must be the only part in the attribute.");const{committer:i}=s,{style:n}=i.element;let o=t.get(s);void 0===o&&(n.cssText=i.strings.join(" "),t.set(s,o=new Set)),o.forEach(e=>{e in r||(o.delete(e),-1===e.indexOf("-")?n[e]=null:n.removeProperty(e))});for(const e in r)o.add(e),-1===e.indexOf("-")?n[e]=r[e]:n.setProperty(e,r[e])});exports.styleMap=r;
},{"../lit-html.js":"zUh2"}],"xQyG":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.PotentiometerElement=void 0;var t=require("lit-element"),e=require("lit-html/directives/style-map"),i=function(t,e,i,o){var r,s=arguments.length,n=s<3?e:null===o?o=Object.getOwnPropertyDescriptor(e,i):o;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)n=Reflect.decorate(t,e,i,o);else for(var l=t.length-1;l>=0;l--)(r=t[l])&&(n=(s<3?r(n):s>3?r(e,i,n):r(e,i))||n);return s>3&&n&&Object.defineProperty(e,i,n),n};let o=class extends t.LitElement{constructor(){super(...arguments),this.min=0,this.max=100,this.value=0,this.step=1,this.startDegree=-135,this.endDegree=135,this.center={x:0,y:0},this.pressed=!1}static get styles(){return t.css`
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
    `}clamp(t,e,i){return Math.min(Math.max(i,t),e)}mapToMinMax(t,e,i){return t*(i-e)+e}percentFromMinMax(t,e,i){return(t-e)/(i-e)}render(){const i=this.clamp(0,1,this.percentFromMinMax(this.value,this.min,this.max)),o=(this.endDegree-this.startDegree)*i+this.startDegree;return t.html`
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
        style=${(0,e.styleMap)({"--knob-angle":o+"deg"})}
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
    `}focusInput(){var t,e;null===(e=null===(t=this.shadowRoot)||void 0===t?void 0:t.querySelector(".hide-input"))||void 0===e||e.focus()}onValueChange(t){const e=t.target;this.updateValue(parseFloat(e.value))}down(t){(0===t.button||window.navigator.maxTouchPoints)&&(this.pressed=!0,this.updatePotentiometerPosition(t))}move(t){const{pressed:e}=this;e&&this.rotateHandler(t)}up(){this.pressed=!1}updatePotentiometerPosition(t){var e,i;t.stopPropagation(),t.preventDefault();const o=null===(i=null===(e=this.shadowRoot)||void 0===e?void 0:e.querySelector("#knob"))||void 0===i?void 0:i.getBoundingClientRect();o&&(this.center={x:window.scrollX+o.left+o.width/2,y:window.scrollY+o.top+o.height/2})}rotateHandler(t){t.stopPropagation(),t.preventDefault();const e="touchmove"===t.type,i=e?t.touches[0].pageX:t.pageX,o=e?t.touches[0].pageY:t.pageY,r=this.center.x-i,s=this.center.y-o;let n=Math.round(180*Math.atan2(s,r)/Math.PI);n<0&&(n+=360),n-=90,r>0&&s<=0&&(n-=360),n=this.clamp(this.startDegree,this.endDegree,n);const l=this.percentFromMinMax(n,this.startDegree,this.endDegree),a=this.mapToMinMax(l,this.min,this.max);this.updateValue(a)}updateValue(t){const e=this.clamp(this.min,this.max,t),i=Math.round(e/this.step)*this.step;this.value=Math.round(100*i)/100,this.dispatchEvent(new InputEvent("input",{detail:this.value}))}};exports.PotentiometerElement=o,i([(0,t.property)()],o.prototype,"min",void 0),i([(0,t.property)()],o.prototype,"max",void 0),i([(0,t.property)()],o.prototype,"value",void 0),i([(0,t.property)()],o.prototype,"step",void 0),i([(0,t.property)()],o.prototype,"startDegree",void 0),i([(0,t.property)()],o.prototype,"endDegree",void 0),exports.PotentiometerElement=o=i([(0,t.customElement)("wokwi-potentiometer")],o);
},{"lit-element":"AInt","lit-html/directives/style-map":"bjcn"}],"WZVO":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.NeopixelMatrixElement=void 0;var e=require("lit-element"),t=function(e,t,i,l){var r,s=arguments.length,o=s<3?t:null===l?l=Object.getOwnPropertyDescriptor(t,i):l;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)o=Reflect.decorate(e,t,i,l);else for(var a=e.length-1;a>=0;a--)(r=e[a])&&(o=(s<3?r(o):s>3?r(t,i,o):r(t,i))||o);return s>3&&o&&Object.defineProperty(t,i,o),o};const i=5.66,l=5;let r=class extends e.LitElement{constructor(){super(...arguments),this.rows=8,this.cols=8,this.colSpacing=1,this.blurLight=!1,this.animation=!1,this.rowSpacing=1,this.pixelElements=null,this.animationFrame=null,this.animateStep=(()=>{const e=(new Date).getTime(),{rows:t,cols:i}=this,l=e=>e%2e3>1e3?1-e%1e3/1e3:e%1e3/1e3;for(let r=0;r<t;r++)for(let s=0;s<i;s++){const o=Math.sqrt((r-t/2+.5)**2+(s-i/2+.5)**2);this.setPixel(r,s,{r:l(100*o+e),g:l(100*o+e+200),b:l(100*o+e+400)})}this.animationFrame=requestAnimationFrame(this.animateStep)})}static get styles(){return e.css`
      :host {
        display: flex;
      }
    `}getPixelElements(){return this.shadowRoot?(this.pixelElements||(this.pixelElements=Array.from(this.shadowRoot.querySelectorAll("g.pixel")).map(e=>Array.from(e.querySelectorAll("ellipse")))),this.pixelElements):null}reset(){const e=this.getPixelElements();if(e)for(const[t,i,l,r]of e)t.style.opacity="0",i.style.opacity="0",l.style.opacity="0",r.style.opacity="0"}setPixel(e,t,i){const l=this.getPixelElements();if(e<0||t<0||e>=this.rows||t>=this.cols||!l)return null;const{r:r,g:s,b:o}=i,a=e=>e>.001?.7+.3*e:0,n=Math.max(r,s,o),c=Math.min(r,s,o),p=n-c,h=Math.max(1,2-20*p),m=.1+Math.max(2*n-5*p,0),y=e=>n?Math.floor(255*Math.min((e=>e>.005?.1+.9*e:0)(e/n)*h,1)):255,x=`rgb(${y(r)}, ${y(s)}, ${y(o)})`,f=l[e*this.cols+t],[g,d,u,w]=f;g.style.opacity=a(r).toFixed(2),d.style.opacity=a(s).toFixed(2),u.style.opacity=a(o).toFixed(2),w.style.opacity=(e=>e>.005?m+e*(1-m):0)(n).toFixed(2),w.style.fill=x}updated(){this.animation&&!this.animationFrame?this.animationFrame=requestAnimationFrame(this.animateStep):!this.animation&&this.animationFrame&&(cancelAnimationFrame(this.animationFrame),this.animationFrame=null)}renderPixels(){const t=[],{cols:i,rows:l,colSpacing:r,rowSpacing:s}=this,o=5.66+r,a=5+s;for(let n=0;n<l;n++)for(let l=0;l<i;l++)t.push(e.svg`
        <g transform="translate(${o*l}, ${a*n})" class="pixel">
          <ellipse cx="2.5" cy="2.3" rx="0.3" ry="0.3" fill="red" opacity="0" />
          <ellipse cx="3.5" cy="3.2" rx="0.3" ry="0.3" fill="green" opacity="0" />
          <ellipse cx="3.3" cy="1.45" rx="0.3" ry="0.3" fill="blue" opacity="0" />
          <ellipse cx="3" cy="2.5" rx="2.2" ry="2.2" opacity="0" />
        </g>`);return this.pixelElements=null,t}render(){const{cols:t,rows:i,rowSpacing:l,colSpacing:r,blurLight:s}=this,o=5.66+r,a=5+l,n=5.66*t+r*(t-1),c=5*i+l*(i-1);return e.html`
      <svg
        width="${n}mm"
        height="${c}mm"
        version="1.1"
        viewBox="0 0 ${n} ${c}"
        xmlns="http://www.w3.org/2000/svg"
      >
        <filter id="blurLight" x="-0.8" y="-0.8" height="2.8" width="2.8">
          <feGaussianBlur stdDeviation="0.3" />
        </filter>

        <pattern id="pixel" width="${o}" height="${a}" patternUnits="userSpaceOnUse">
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
        <rect width="${n}" height="${c}" fill="url(#pixel)"></rect>
        <g style="${s?"filter: url(#blurLight)":""}">
          ${this.renderPixels()}
        </g>
      </svg>
    `}};exports.NeopixelMatrixElement=r,t([(0,e.property)()],r.prototype,"rows",void 0),t([(0,e.property)()],r.prototype,"cols",void 0),t([(0,e.property)({attribute:"colspacing"})],r.prototype,"colSpacing",void 0),t([(0,e.property)()],r.prototype,"blurLight",void 0),t([(0,e.property)()],r.prototype,"animation",void 0),t([(0,e.property)({attribute:"rowspacing"})],r.prototype,"rowSpacing",void 0),exports.NeopixelMatrixElement=r=t([(0,e.customElement)("wokwi-neopixel-matrix")],r);
},{"lit-element":"AInt"}],"rlLF":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.SSD1306Element=void 0;var t=require("lit-element"),e=function(t,e,r,i){var c,s=arguments.length,a=s<3?e:null===i?i=Object.getOwnPropertyDescriptor(e,r):i;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)a=Reflect.decorate(t,e,r,i);else for(var l=t.length-1;l>=0;l--)(c=t[l])&&(a=(s<3?c(a):s>3?c(e,r,a):c(e,r))||a);return s>3&&a&&Object.defineProperty(e,r,a),a};let r=class extends t.LitElement{constructor(){super(),this.width=150,this.height=116,this.screenWidth=128,this.screenHeight=64,this.canvas=void 0,this.ctx=null,this.imageData=new ImageData(this.screenWidth,this.screenHeight)}redraw(){var t;null===(t=this.ctx)||void 0===t||t.putImageData(this.imageData,0,0)}initContext(){var t,e;this.canvas=null===(t=this.shadowRoot)||void 0===t?void 0:t.querySelector("canvas"),this.ctx=null===(e=this.canvas)||void 0===e?void 0:e.getContext("2d")}firstUpdated(){var t;this.initContext(),null===(t=this.ctx)||void 0===t||t.putImageData(this.imageData,0,0)}updated(){this.imageData&&this.redraw()}render(){const{width:e,height:r,screenWidth:i,screenHeight:c,imageData:s}=this,a=s?"visible":"hidden";return t.html`<svg width="${e}" height="${r}" xmlns="http://www.w3.org/2000/svg">
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
          <rect fill="#1A1A1A" width="${i}" height="${c}" />
          <!-- image holder -->
          <foreignObject
            ?visibility="${a}"
            width="${i}"
            height="${c}"
          >
            <canvas width="${i}" height="${c}"></canvas>
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
    </svg> `}};exports.SSD1306Element=r,e([(0,t.property)()],r.prototype,"imageData",void 0),exports.SSD1306Element=r=e([(0,t.customElement)("wokwi-ssd1306")],r);
},{"lit-element":"AInt"}],"fzQk":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.BuzzerElement=void 0;var e=require("lit-element"),t=function(e,t,i,o){var n,r=arguments.length,s=r<3?t:null===o?o=Object.getOwnPropertyDescriptor(t,i):o;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(e,t,i,o);else for(var l=e.length-1;l>=0;l--)(n=e[l])&&(s=(r<3?n(s):r>3?n(t,i,s):n(t,i))||s);return r>3&&s&&Object.defineProperty(t,i,s),s};let i=class extends e.LitElement{constructor(){super(...arguments),this.hasSignal=!1}static get styles(){return e.css`
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
    `}render(){const t=this.hasSignal;return e.html`
      <div class="buzzer-container">
        <svg
          class="music-note"
          style="visibility: ${t?"":"hidden"}"
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
    `}};exports.BuzzerElement=i,t([(0,e.property)()],i.prototype,"hasSignal",void 0),exports.BuzzerElement=i=t([(0,e.customElement)("wokwi-buzzer")],i);
},{"lit-element":"AInt"}],"rDJF":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.classMap=void 0;var t=require("../lit-html.js");class e{constructor(t){this.classes=new Set,this.changed=!1,this.element=t;const e=(t.getAttribute("class")||"").split(/\s+/);for(const s of e)this.classes.add(s)}add(t){this.classes.add(t),this.changed=!0}remove(t){this.classes.delete(t),this.changed=!0}commit(){if(this.changed){let t="";this.classes.forEach(e=>t+=e+" "),this.element.setAttribute("class",t)}}}const s=new WeakMap,i=(0,t.directive)(i=>a=>{if(!(a instanceof t.AttributePart)||a instanceof t.PropertyPart||"class"!==a.committer.name||a.committer.parts.length>1)throw new Error("The `classMap` directive must be used in the `class` attribute and must be the only part in the attribute.");const{committer:c}=a,{element:r}=c;let o=s.get(a);void 0===o&&(r.setAttribute("class",c.strings.join(" ")),s.set(a,o=new Set));const n=r.classList||new e(r);o.forEach(t=>{t in i||(n.remove(t),o.delete(t))});for(const t in i){const e=i[t];e!=o.has(t)&&(e?(n.add(t),o.add(t)):(n.remove(t),o.delete(t)))}"function"==typeof n.commit&&n.commit()});exports.classMap=i;
},{"../lit-html.js":"zUh2"}],"oxIu":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.RotaryDialerElement=void 0;var t=require("lit-element"),e=require("lit-html/directives/style-map"),i=require("lit-html/directives/class-map"),s=function(t,e,i,s){var a,r=arguments.length,l=r<3?e:null===s?s=Object.getOwnPropertyDescriptor(e,i):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(t,e,i,s);else for(var n=t.length-1;n>=0;n--)(a=t[n])&&(l=(r<3?a(l):r>3?a(e,i,l):a(e,i))||l);return r>3&&l&&Object.defineProperty(e,i,l),l};let a=class extends t.LitElement{constructor(){super(...arguments),this.digit="",this.stylesMapping={},this.classes={"rotate-path":!0},this.degrees=[320,56,87,115,143,173,204,232,260,290]}static get styles(){return t.css`
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
    `}removeDialerAnim(){this.classes=Object.assign(Object.assign({},this.classes),{"dialer-anim":!1}),this.stylesMapping={"--angle":0},this.requestUpdate()}dial(t){this.digit=t,this.addDialerAnim(t)}onValueChange(t){const e=t.target;this.digit=parseInt(e.value),this.dial(this.digit),e.value=""}addDialerAnim(t){requestAnimationFrame(()=>{this.dispatchEvent(new CustomEvent("dial-start",{detail:{digit:this.digit}})),this.classes=Object.assign(Object.assign({},this.classes),{"dialer-anim":!0});const e=this.degrees[t];this.stylesMapping={"--angle":e+"deg"},this.requestUpdate()})}focusInput(){var t,e;null===(e=null===(t=this.shadowRoot)||void 0===t?void 0:t.querySelector(".hide-input"))||void 0===e||e.focus()}render(){return t.html`
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
            class=${(0,i.classMap)(this.classes)}
            @transitionstart="${()=>{this.classes["dialer-anim"]||this.dispatchEvent(new CustomEvent("dial",{detail:{digit:this.digit}}))}}"
            @transitionend="${()=>{this.classes["dialer-anim"]||this.dispatchEvent(new CustomEvent("dial-end",{detail:{digit:this.digit}})),this.removeDialerAnim()}}"
            d="M133.5,210 C146.478692,210 157,220.521308 157,233.5 C157,246.478692 146.478692,257 133.5,257 C120.521308,257 110,246.478692 110,233.5 C110,220.521308 120.521308,210 133.5,210 Z M83.5,197 C96.4786916,197 107,207.521308 107,220.5 C107,233.478692 96.4786916,244 83.5,244 C70.5213084,244 60,233.478692 60,220.5 C60,207.521308 70.5213084,197 83.5,197 Z M45.5,163 C58.4786916,163 69,173.521308 69,186.5 C69,199.478692 58.4786916,210 45.5,210 C32.5213084,210 22,199.478692 22,186.5 C22,173.521308 32.5213084,163 45.5,163 Z M32.5,114 C45.4786916,114 56,124.521308 56,137.5 C56,150.478692 45.4786916,161 32.5,161 C19.5213084,161 9,150.478692 9,137.5 C9,124.521308 19.5213084,114 32.5,114 Z M234.5,93 C247.478692,93 258,103.521308 258,116.5 C258,129.478692 247.478692,140 234.5,140 C221.521308,140 211,129.478692 211,116.5 C211,103.521308 221.521308,93 234.5,93 Z M41.5,64 C54.4786916,64 65,74.5213084 65,87.5 C65,100.478692 54.4786916,111 41.5,111 C28.5213084,111 18,100.478692 18,87.5 C18,74.5213084 28.5213084,64 41.5,64 Z M214.5,46 C227.478692,46 238,56.5213084 238,69.5 C238,82.4786916 227.478692,93 214.5,93 C201.521308,93 191,82.4786916 191,69.5 C191,56.5213084 201.521308,46 214.5,46 Z M76.5,26 C89.4786916,26 100,36.5213084 100,49.5 C100,62.4786916 89.4786916,73 76.5,73 C63.5213084,73 53,62.4786916 53,49.5 C53,36.5213084 63.5213084,26 76.5,26 Z M173.5,15 C186.478692,15 197,25.5213084 197,38.5 C197,51.4786916 186.478692,62 173.5,62 C160.521308,62 150,51.4786916 150,38.5 C150,25.5213084 160.521308,15 173.5,15 Z M123.5,7 C136.478692,7 147,17.5213084 147,30.5 C147,43.4786916 136.478692,54 123.5,54 C110.521308,54 100,43.4786916 100,30.5 C100,17.5213084 110.521308,7 123.5,7 Z"
            id="slots"
            stroke="#fff"
            fill-opacity="0.5"
            fill="#D8D8D8"
            style=${(0,e.styleMap)(this.stylesMapping)}
          ></path>
        </g>
        <circle id="container" fill-opacity=".5" fill="#070707" cx="132.5" cy="132.5" r="132.5" />
        <g class="text" font-family="Marker Felt, monospace" font-size="21" fill="#FFF">
          <text @mouseup=${()=>this.dial(0)} x="129" y="243">0</text>
          <text @mouseup=${()=>this.dial(9)} x="78" y="230">9</text>
          <text @mouseup=${()=>this.dial(8)} x="40" y="194">8</text>
          <text @mouseup=${()=>this.dial(7)} x="28" y="145">7</text>
          <text @mouseup=${()=>this.dial(6)} x="35" y="97">6</text>
          <text @mouseup=${()=>this.dial(5)} x="72" y="58">5</text>
          <text @mouseup=${()=>this.dial(4)} x="117" y="41">4</text>
          <text @mouseup=${()=>this.dial(3)} x="168" y="47">3</text>
          <text @mouseup=${()=>this.dial(2)} x="210" y="79">2</text>
          <text @mouseup=${()=>this.dial(1)} x="230" y="126">1</text>
        </g>
        <path
          d="M182.738529,211.096297 L177.320119,238.659185 L174.670528,252.137377 L188.487742,252.137377 L182.738529,211.096297 Z"
          stroke="#979797"
          fill="#D8D8D8"
          transform="translate(181.562666, 230.360231) rotate(-22.000000) translate(-181.562666, -230.360231)"
        ></path>
      </svg>
    `}};exports.RotaryDialerElement=a,exports.RotaryDialerElement=a=s([(0,t.customElement)("wokwi-rotary-dialer")],a);
},{"lit-element":"AInt","lit-html/directives/style-map":"bjcn","lit-html/directives/class-map":"rDJF"}],"JIMd":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),Object.defineProperty(exports,"SevenSegmentElement",{enumerable:!0,get:function(){return e.SevenSegmentElement}}),Object.defineProperty(exports,"ArduinoUnoElement",{enumerable:!0,get:function(){return t.ArduinoUnoElement}}),Object.defineProperty(exports,"LCD1602Element",{enumerable:!0,get:function(){return r.LCD1602Element}}),Object.defineProperty(exports,"fontA00",{enumerable:!0,get:function(){return n.fontA00}}),Object.defineProperty(exports,"fontA02",{enumerable:!0,get:function(){return o.fontA02}}),Object.defineProperty(exports,"LEDElement",{enumerable:!0,get:function(){return u.LEDElement}}),Object.defineProperty(exports,"NeoPixelElement",{enumerable:!0,get:function(){return l.NeoPixelElement}}),Object.defineProperty(exports,"PushbuttonElement",{enumerable:!0,get:function(){return i.PushbuttonElement}}),Object.defineProperty(exports,"ResistorElement",{enumerable:!0,get:function(){return m.ResistorElement}}),Object.defineProperty(exports,"MembraneKeypadElement",{enumerable:!0,get:function(){return p.MembraneKeypadElement}}),Object.defineProperty(exports,"PotentiometerElement",{enumerable:!0,get:function(){return b.PotentiometerElement}}),Object.defineProperty(exports,"NeopixelMatrixElement",{enumerable:!0,get:function(){return f.NeopixelMatrixElement}}),Object.defineProperty(exports,"SSD1306Element",{enumerable:!0,get:function(){return a.SSD1306Element}}),Object.defineProperty(exports,"BuzzerElement",{enumerable:!0,get:function(){return c.BuzzerElement}}),Object.defineProperty(exports,"RotaryDialerElement",{enumerable:!0,get:function(){return s.RotaryDialerElement}});var e=require("./7segment-element"),t=require("./arduino-uno-element"),r=require("./lcd1602-element"),n=require("./lcd1602-font-a00"),o=require("./lcd1602-font-a02"),u=require("./led-element"),l=require("./neopixel-element"),i=require("./pushbutton-element"),m=require("./resistor-element"),p=require("./membrane-keypad-element"),b=require("./potentiometer-element"),f=require("./neopixel-matrix-element"),a=require("./ssd1306-element"),c=require("./buzzer-element"),s=require("./rotary-dialer-element");
},{"./7segment-element":"U1nF","./arduino-uno-element":"RLG9","./lcd1602-element":"CZgF","./lcd1602-font-a00":"Fq3W","./lcd1602-font-a02":"gnIK","./led-element":"AwTz","./neopixel-element":"Lqo3","./pushbutton-element":"a5Yb","./resistor-element":"X1an","./membrane-keypad-element":"L6Zo","./potentiometer-element":"xQyG","./neopixel-matrix-element":"WZVO","./ssd1306-element":"rlLF","./buzzer-element":"fzQk","./rotary-dialer-element":"oxIu"}],"tdl1":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.buildHex=void 0;const e="https://hexi.wokwi.com";async function t(t){const o=await fetch(e+"/build",{method:"POST",mode:"cors",cache:"no-cache",headers:{"Content-Type":"application/json"},body:JSON.stringify({sketch:t})});return await o.json()}exports.buildHex=t;
},{}],"Q59z":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.CPUPerformance=void 0;class e{constructor(e,s){this.cpu=e,this.MHZ=s,this.prevTime=0,this.prevCycles=0,this.samples=new Float32Array(64),this.sampleIndex=0}reset(){this.prevTime=0,this.prevCycles=0,this.sampleIndex=0}update(){if(this.prevTime){const e=performance.now()-this.prevTime;if(e>0){const s=(this.cpu.cycles-this.prevCycles)/this.MHZ*1e3/e;this.sampleIndex||this.samples.fill(s),this.samples[this.sampleIndex++%this.samples.length]=s}}return this.prevCycles=this.cpu.cycles,this.prevTime=performance.now(),this.samples.reduce((e,s)=>e+s)/this.samples.length}}exports.CPUPerformance=e;
},{}],"cTh7":[function(require,module,exports) {
"use strict";function t(t,e){const a=t.dataView.getUint16(93,!0);t.data[a]=255&t.pc,t.data[a-1]=t.pc>>8&255,t.pc22Bits&&(t.data[a-2]=t.pc>>16&255),t.dataView.setUint16(93,a-(t.pc22Bits?3:2),!0),t.data[95]&=127,t.cycles+=2,t.pc=e}Object.defineProperty(exports,"__esModule",{value:!0}),exports.avrInterrupt=t;
},{}],"vM7b":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.CPU=void 0;var t=require("./interrupt");const e=256;class s{constructor(t,s=8192){this.progMem=t,this.sramBytes=s,this.data=new Uint8Array(this.sramBytes+e),this.data16=new Uint16Array(this.data.buffer),this.dataView=new DataView(this.data.buffer),this.progBytes=new Uint8Array(this.progMem.buffer),this.readHooks=[],this.writeHooks=[],this.pendingInterrupts=[],this.clockEvents=[],this.pc22Bits=this.progBytes.length>131072,this.gpioTimerHooks=[],this.pc=0,this.cycles=0,this.nextInterrupt=-1,this.nextClockEvent=0,this.reset()}reset(){this.data.fill(0),this.SP=this.data.length-1,this.pendingInterrupts.splice(0,this.pendingInterrupts.length),this.nextInterrupt=-1}readData(t){return t>=32&&this.readHooks[t]?this.readHooks[t](t):this.data[t]}writeData(t,e){const s=this.writeHooks[t];s&&s(e,this.data[t],t)||(this.data[t]=e)}get SP(){return this.dataView.getUint16(93,!0)}set SP(t){this.dataView.setUint16(93,t,!0)}get SREG(){return this.data[95]}get interruptsEnabled(){return!!(128&this.SREG)}updateNextInterrupt(){this.nextInterrupt=this.pendingInterrupts.findIndex(t=>!!t)}setInterruptFlag(t){const{flagRegister:e,flagMask:s,enableRegister:i,enableMask:r}=t;t.inverseFlag?this.data[e]&=~s:this.data[e]|=s,this.data[i]&r&&this.queueInterrupt(t)}updateInterruptEnable(t,e){const{enableMask:s,flagRegister:i,flagMask:r}=t;e&s?this.data[i]&r&&this.queueInterrupt(t):this.clearInterrupt(t,!1)}queueInterrupt(t){this.pendingInterrupts[t.address]=t,this.updateNextInterrupt()}clearInterrupt({address:t,flagRegister:e,flagMask:s},i=!0){delete this.pendingInterrupts[t],i&&(this.data[e]&=~s),this.updateNextInterrupt()}clearInterruptByFlag(t,e){const{flagRegister:s,flagMask:i}=t;e&i&&(this.data[s]&=~i,this.clearInterrupt(t))}addClockEvent(t,e){const s={cycles:this.cycles+Math.max(1,e),callback:t},{clockEvents:i}=this;if(!i.length||i[i.length-1].cycles<=s.cycles)i.push(s);else if(i[0].cycles>=s.cycles)i.unshift(s);else for(let r=1;r<i.length;r++)if(i[r].cycles>=s.cycles){i.splice(r,0,s);break}return this.nextClockEvent=this.clockEvents[0].cycles,t}updateClockEvent(t,e){return!!this.clearClockEvent(t)&&(this.addClockEvent(t,e),!0)}clearClockEvent(t){var e,s;const i=this.clockEvents.findIndex(e=>e.callback===t);return i>=0&&(this.clockEvents.splice(i,1),this.nextClockEvent=null!==(s=null===(e=this.clockEvents[0])||void 0===e?void 0:e.cycles)&&void 0!==s?s:0,!0)}tick(){var e,s,i;const{nextClockEvent:r,clockEvents:n}=this;r&&r<=this.cycles&&(null===(e=n.shift())||void 0===e||e.callback(),this.nextClockEvent=null!==(i=null===(s=n[0])||void 0===s?void 0:s.cycles)&&void 0!==i?i:0);const{nextInterrupt:a}=this;if(this.interruptsEnabled&&a>=0){const e=this.pendingInterrupts[a];(0,t.avrInterrupt)(this,e.address),e.constant||this.clearInterrupt(e)}}}exports.CPU=s;
},{"./interrupt":"cTh7"}],"YNAr":[function(require,module,exports) {
"use strict";function a(a){return 36864==(65039&a)||37376==(65039&a)||37902==(65038&a)||37900==(65038&a)}function t(t){const e=t.progMem[t.pc];if(7168==(64512&e)){const a=t.data[(496&e)>>4],d=t.data[15&e|(512&e)>>5],i=a+d+(1&t.data[95]),s=255&i;t.data[(496&e)>>4]=s;let c=192&t.data[95];c|=s?0:2,c|=128&s?4:0,c|=(s^d)&(a^s)&128?8:0,c|=c>>2&1^c>>3&1?16:0,c|=256&i?1:0,c|=1&(a&d|d&~s|~s&a)?32:0,t.data[95]=c}else if(3072==(64512&e)){const a=t.data[(496&e)>>4],d=t.data[15&e|(512&e)>>5],i=a+d&255;t.data[(496&e)>>4]=i;let s=192&t.data[95];s|=i?0:2,s|=128&i?4:0,s|=(i^d)&(i^a)&128?8:0,s|=s>>2&1^s>>3&1?16:0,s|=a+d&256?1:0,s|=1&(a&d|d&~i|~i&a)?32:0,t.data[95]=s}else if(38400==(65280&e)){const a=2*((48&e)>>4)+24,d=t.dataView.getUint16(a,!0),i=d+(15&e|(192&e)>>2)&65535;t.dataView.setUint16(a,i,!0);let s=224&t.data[95];s|=i?0:2,s|=32768&i?4:0,s|=~d&i&32768?8:0,s|=s>>2&1^s>>3&1?16:0,s|=~i&d&32768?1:0,t.data[95]=s,t.cycles++}else if(8192==(64512&e)){const a=t.data[(496&e)>>4]&t.data[15&e|(512&e)>>5];t.data[(496&e)>>4]=a;let d=225&t.data[95];d|=a?0:2,d|=128&a?4:0,d|=d>>2&1^d>>3&1?16:0,t.data[95]=d}else if(28672==(61440&e)){const a=t.data[16+((240&e)>>4)]&(15&e|(3840&e)>>4);t.data[16+((240&e)>>4)]=a;let d=225&t.data[95];d|=a?0:2,d|=128&a?4:0,d|=d>>2&1^d>>3&1?16:0,t.data[95]=d}else if(37893==(65039&e)){const a=t.data[(496&e)>>4],d=a>>>1|128&a;t.data[(496&e)>>4]=d;let i=224&t.data[95];i|=d?0:2,i|=128&d?4:0,i|=1&a,i|=i>>2&1^1&i?8:0,i|=i>>2&1^i>>3&1?16:0,t.data[95]=i}else if(38024==(65423&e))t.data[95]&=~(1<<((112&e)>>4));else if(63488==(65032&e)){const a=7&e,d=(496&e)>>4;t.data[d]=~(1<<a)&t.data[d]|(t.data[95]>>6&1)<<a}else if(62464==(64512&e))t.data[95]&1<<(7&e)||(t.pc=t.pc+(((504&e)>>3)-(512&e?64:0)),t.cycles++);else if(61440==(64512&e))t.data[95]&1<<(7&e)&&(t.pc=t.pc+(((504&e)>>3)-(512&e?64:0)),t.cycles++);else if(37896==(65423&e))t.data[95]|=1<<((112&e)>>4);else if(64e3==(65032&e)){const a=t.data[(496&e)>>4],d=7&e;t.data[95]=191&t.data[95]|(a>>d&1?64:0)}else if(37902==(65038&e)){const a=t.progMem[t.pc+1]|(1&e)<<16|(496&e)<<13,d=t.pc+2,i=t.dataView.getUint16(93,!0),{pc22Bits:s}=t;t.data[i]=255&d,t.data[i-1]=d>>8&255,s&&(t.data[i-2]=d>>16&255),t.dataView.setUint16(93,i-(s?3:2),!0),t.pc=a-1,t.cycles+=s?4:3}else if(38912==(65280&e)){const a=248&e,d=7&e,i=t.readData(32+(a>>3));t.writeData(32+(a>>3),i&~(1<<d))}else if(37888==(65039&e)){const a=(496&e)>>4,d=255-t.data[a];t.data[a]=d;let i=225&t.data[95]|1;i|=d?0:2,i|=128&d?4:0,i|=i>>2&1^i>>3&1?16:0,t.data[95]=i}else if(5120==(64512&e)){const a=t.data[(496&e)>>4],d=t.data[15&e|(512&e)>>5],i=a-d;let s=192&t.data[95];s|=i?0:2,s|=128&i?4:0,s|=0!=((a^d)&(a^i)&128)?8:0,s|=s>>2&1^s>>3&1?16:0,s|=d>a?1:0,s|=1&(~a&d|d&i|i&~a)?32:0,t.data[95]=s}else if(1024==(64512&e)){const a=t.data[(496&e)>>4],d=t.data[15&e|(512&e)>>5];let i=t.data[95];const s=a-d-(1&i);i=192&i|(!s&&i>>1&1?2:0)|(d+(1&i)>a?1:0),i|=128&s?4:0,i|=(a^d)&(a^s)&128?8:0,i|=i>>2&1^i>>3&1?16:0,i|=1&(~a&d|d&s|s&~a)?32:0,t.data[95]=i}else if(12288==(61440&e)){const a=t.data[16+((240&e)>>4)],d=15&e|(3840&e)>>4,i=a-d;let s=192&t.data[95];s|=i?0:2,s|=128&i?4:0,s|=(a^d)&(a^i)&128?8:0,s|=s>>2&1^s>>3&1?16:0,s|=d>a?1:0,s|=1&(~a&d|d&i|i&~a)?32:0,t.data[95]=s}else if(4096==(64512&e)){if(t.data[(496&e)>>4]===t.data[15&e|(512&e)>>5]){const e=a(t.progMem[t.pc+1])?2:1;t.pc+=e,t.cycles+=e}}else if(37898==(65039&e)){const a=t.data[(496&e)>>4],d=a-1;t.data[(496&e)>>4]=d;let i=225&t.data[95];i|=d?0:2,i|=128&d?4:0,i|=128===a?8:0,i|=i>>2&1^i>>3&1?16:0,t.data[95]=i}else if(38169===e){const a=t.pc+1,e=t.dataView.getUint16(93,!0),d=t.data[92];t.data[e]=255&a,t.data[e-1]=a>>8&255,t.data[e-2]=a>>16&255,t.dataView.setUint16(93,e-3,!0),t.pc=(d<<16|t.dataView.getUint16(30,!0))-1,t.cycles+=3}else if(37913===e){const a=t.data[92];t.pc=(a<<16|t.dataView.getUint16(30,!0))-1,t.cycles++}else if(38360===e){const a=t.data[91];t.data[0]=t.progBytes[a<<16|t.dataView.getUint16(30,!0)],t.cycles+=2}else if(36870==(65039&e)){const a=t.data[91];t.data[(496&e)>>4]=t.progBytes[a<<16|t.dataView.getUint16(30,!0)],t.cycles+=2}else if(36871==(65039&e)){const a=t.data[91],d=t.dataView.getUint16(30,!0);t.data[(496&e)>>4]=t.progBytes[a<<16|d],t.dataView.setUint16(30,d+1,!0),65535===d&&(t.data[91]=(a+1)%(t.progBytes.length>>16)),t.cycles+=2}else if(9216==(64512&e)){const a=t.data[(496&e)>>4]^t.data[15&e|(512&e)>>5];t.data[(496&e)>>4]=a;let d=225&t.data[95];d|=a?0:2,d|=128&a?4:0,d|=d>>2&1^d>>3&1?16:0,t.data[95]=d}else if(776==(65416&e)){const a=t.data[16+((112&e)>>4)],d=t.data[16+(7&e)],i=a*d<<1;t.dataView.setUint16(0,i,!0),t.data[95]=252&t.data[95]|(65535&i?0:2)|(a*d&32768?1:0),t.cycles++}else if(896==(65416&e)){const a=t.dataView.getInt8(16+((112&e)>>4)),d=t.dataView.getInt8(16+(7&e)),i=a*d<<1;t.dataView.setInt16(0,i,!0),t.data[95]=252&t.data[95]|(65535&i?0:2)|(a*d&32768?1:0),t.cycles++}else if(904==(65416&e)){const a=t.dataView.getInt8(16+((112&e)>>4)),d=t.data[16+(7&e)],i=a*d<<1;t.dataView.setInt16(0,i,!0),t.data[95]=252&t.data[95]|(65535&i?2:0)|(a*d&32768?1:0),t.cycles++}else if(38153===e){const a=t.pc+1,e=t.dataView.getUint16(93,!0),{pc22Bits:d}=t;t.data[e]=255&a,t.data[e-1]=a>>8&255,d&&(t.data[e-2]=a>>16&255),t.dataView.setUint16(93,e-(d?3:2),!0),t.pc=t.dataView.getUint16(30,!0)-1,t.cycles+=d?3:2}else if(37897===e)t.pc=t.dataView.getUint16(30,!0)-1,t.cycles++;else if(45056==(63488&e)){const a=t.readData(32+(15&e|(1536&e)>>5));t.data[(496&e)>>4]=a}else if(37891==(65039&e)){const a=t.data[(496&e)>>4],d=a+1&255;t.data[(496&e)>>4]=d;let i=225&t.data[95];i|=d?0:2,i|=128&d?4:0,i|=127===a?8:0,i|=i>>2&1^i>>3&1?16:0,t.data[95]=i}else if(37900==(65038&e))t.pc=(t.progMem[t.pc+1]|(1&e)<<16|(496&e)<<13)-1,t.cycles+=2;else if(37382==(65039&e)){const a=(496&e)>>4,d=t.data[a],i=t.readData(t.dataView.getUint16(30,!0));t.writeData(t.dataView.getUint16(30,!0),i&255-d),t.data[a]=i}else if(37381==(65039&e)){const a=(496&e)>>4,d=t.data[a],i=t.readData(t.dataView.getUint16(30,!0));t.writeData(t.dataView.getUint16(30,!0),i|d),t.data[a]=i}else if(37383==(65039&e)){const a=t.data[(496&e)>>4],d=t.readData(t.dataView.getUint16(30,!0));t.writeData(t.dataView.getUint16(30,!0),a^d),t.data[(496&e)>>4]=d}else if(57344==(61440&e))t.data[16+((240&e)>>4)]=15&e|(3840&e)>>4;else if(36864==(65039&e)){t.cycles++;const a=t.readData(t.progMem[t.pc+1]);t.data[(496&e)>>4]=a,t.pc++}else if(36876==(65039&e))t.cycles++,t.data[(496&e)>>4]=t.readData(t.dataView.getUint16(26,!0));else if(36877==(65039&e)){const a=t.dataView.getUint16(26,!0);t.cycles++,t.data[(496&e)>>4]=t.readData(a),t.dataView.setUint16(26,a+1,!0)}else if(36878==(65039&e)){const a=t.dataView.getUint16(26,!0)-1;t.dataView.setUint16(26,a,!0),t.cycles++,t.data[(496&e)>>4]=t.readData(a)}else if(32776==(65039&e))t.cycles++,t.data[(496&e)>>4]=t.readData(t.dataView.getUint16(28,!0));else if(36873==(65039&e)){const a=t.dataView.getUint16(28,!0);t.cycles++,t.data[(496&e)>>4]=t.readData(a),t.dataView.setUint16(28,a+1,!0)}else if(36874==(65039&e)){const a=t.dataView.getUint16(28,!0)-1;t.dataView.setUint16(28,a,!0),t.cycles++,t.data[(496&e)>>4]=t.readData(a)}else if(32776==(53768&e)&&7&e|(3072&e)>>7|(8192&e)>>8)t.cycles++,t.data[(496&e)>>4]=t.readData(t.dataView.getUint16(28,!0)+(7&e|(3072&e)>>7|(8192&e)>>8));else if(32768==(65039&e))t.cycles++,t.data[(496&e)>>4]=t.readData(t.dataView.getUint16(30,!0));else if(36865==(65039&e)){const a=t.dataView.getUint16(30,!0);t.cycles++,t.data[(496&e)>>4]=t.readData(a),t.dataView.setUint16(30,a+1,!0)}else if(36866==(65039&e)){const a=t.dataView.getUint16(30,!0)-1;t.dataView.setUint16(30,a,!0),t.cycles++,t.data[(496&e)>>4]=t.readData(a)}else if(32768==(53768&e)&&7&e|(3072&e)>>7|(8192&e)>>8)t.cycles++,t.data[(496&e)>>4]=t.readData(t.dataView.getUint16(30,!0)+(7&e|(3072&e)>>7|(8192&e)>>8));else if(38344===e)t.data[0]=t.progBytes[t.dataView.getUint16(30,!0)],t.cycles+=2;else if(36868==(65039&e))t.data[(496&e)>>4]=t.progBytes[t.dataView.getUint16(30,!0)],t.cycles+=2;else if(36869==(65039&e)){const a=t.dataView.getUint16(30,!0);t.data[(496&e)>>4]=t.progBytes[a],t.dataView.setUint16(30,a+1,!0),t.cycles+=2}else if(37894==(65039&e)){const a=t.data[(496&e)>>4],d=a>>>1;t.data[(496&e)>>4]=d;let i=224&t.data[95];i|=d?0:2,i|=1&a,i|=i>>2&1^1&i?8:0,i|=i>>2&1^i>>3&1?16:0,t.data[95]=i}else if(11264==(64512&e))t.data[(496&e)>>4]=t.data[15&e|(512&e)>>5];else if(256==(65280&e)){const a=2*(15&e),d=2*((240&e)>>4);t.data[d]=t.data[a],t.data[d+1]=t.data[a+1]}else if(39936==(64512&e)){const a=t.data[(496&e)>>4]*t.data[15&e|(512&e)>>5];t.dataView.setUint16(0,a,!0),t.data[95]=252&t.data[95]|(65535&a?0:2)|(32768&a?1:0),t.cycles++}else if(512==(65280&e)){const a=t.dataView.getInt8(16+((240&e)>>4))*t.dataView.getInt8(16+(15&e));t.dataView.setInt16(0,a,!0),t.data[95]=252&t.data[95]|(65535&a?0:2)|(32768&a?1:0),t.cycles++}else if(768==(65416&e)){const a=t.dataView.getInt8(16+((112&e)>>4))*t.data[16+(7&e)];t.dataView.setInt16(0,a,!0),t.data[95]=252&t.data[95]|(65535&a?0:2)|(32768&a?1:0),t.cycles++}else if(37889==(65039&e)){const a=(496&e)>>4,d=t.data[a],i=0-d;t.data[a]=i;let s=192&t.data[95];s|=i?0:2,s|=128&i?4:0,s|=128===i?8:0,s|=s>>2&1^s>>3&1?16:0,s|=i?1:0,s|=1&(i|d)?32:0,t.data[95]=s}else if(0===e);else if(10240==(64512&e)){const a=t.data[(496&e)>>4]|t.data[15&e|(512&e)>>5];t.data[(496&e)>>4]=a;let d=225&t.data[95];d|=a?0:2,d|=128&a?4:0,d|=d>>2&1^d>>3&1?16:0,t.data[95]=d}else if(24576==(61440&e)){const a=t.data[16+((240&e)>>4)]|15&e|(3840&e)>>4;t.data[16+((240&e)>>4)]=a;let d=225&t.data[95];d|=a?0:2,d|=128&a?4:0,d|=d>>2&1^d>>3&1?16:0,t.data[95]=d}else if(47104==(63488&e))t.writeData(32+(15&e|(1536&e)>>5),t.data[(496&e)>>4]);else if(36879==(65039&e)){const a=t.dataView.getUint16(93,!0)+1;t.dataView.setUint16(93,a,!0),t.data[(496&e)>>4]=t.data[a],t.cycles++}else if(37391==(65039&e)){const a=t.dataView.getUint16(93,!0);t.data[a]=t.data[(496&e)>>4],t.dataView.setUint16(93,a-1,!0),t.cycles++}else if(53248==(61440&e)){const a=(2047&e)-(2048&e?2048:0),d=t.pc+1,i=t.dataView.getUint16(93,!0),{pc22Bits:s}=t;t.data[i]=255&d,t.data[i-1]=d>>8&255,s&&(t.data[i-2]=d>>16&255),t.dataView.setUint16(93,i-(s?3:2),!0),t.pc+=a,t.cycles+=s?3:2}else if(38152===e){const{pc22Bits:a}=t,e=t.dataView.getUint16(93,!0)+(a?3:2);t.dataView.setUint16(93,e,!0),t.pc=(t.data[e-1]<<8)+t.data[e]-1,a&&(t.pc|=t.data[e-2]<<16),t.cycles+=a?4:3}else if(38168===e){const{pc22Bits:a}=t,e=t.dataView.getUint16(93,!0)+(a?3:2);t.dataView.setUint16(93,e,!0),t.pc=(t.data[e-1]<<8)+t.data[e]-1,a&&(t.pc|=t.data[e-2]<<16),t.cycles+=a?4:3,t.data[95]|=128}else if(49152==(61440&e))t.pc=t.pc+((2047&e)-(2048&e?2048:0)),t.cycles++;else if(37895==(65039&e)){const a=t.data[(496&e)>>4],d=a>>>1|(1&t.data[95])<<7;t.data[(496&e)>>4]=d;let i=224&t.data[95];i|=d?0:2,i|=128&d?4:0,i|=1&a?1:0,i|=i>>2&1^1&i?8:0,i|=i>>2&1^i>>3&1?16:0,t.data[95]=i}else if(2048==(64512&e)){const a=t.data[(496&e)>>4],d=t.data[15&e|(512&e)>>5];let i=t.data[95];const s=a-d-(1&i);t.data[(496&e)>>4]=s,i=192&i|(!s&&i>>1&1?2:0)|(d+(1&i)>a?1:0),i|=128&s?4:0,i|=(a^d)&(a^s)&128?8:0,i|=i>>2&1^i>>3&1?16:0,i|=1&(~a&d|d&s|s&~a)?32:0,t.data[95]=i}else if(16384==(61440&e)){const a=t.data[16+((240&e)>>4)],d=15&e|(3840&e)>>4;let i=t.data[95];const s=a-d-(1&i);t.data[16+((240&e)>>4)]=s,i=192&i|(!s&&i>>1&1?2:0)|(d+(1&i)>a?1:0),i|=128&s?4:0,i|=(a^d)&(a^s)&128?8:0,i|=i>>2&1^i>>3&1?16:0,i|=1&(~a&d|d&s|s&~a)?32:0,t.data[95]=i}else if(39424==(65280&e)){const a=32+((248&e)>>3);t.writeData(a,t.readData(a)|1<<(7&e)),t.cycles++}else if(39168==(65280&e)){if(!(t.readData(32+((248&e)>>3))&1<<(7&e))){const e=a(t.progMem[t.pc+1])?2:1;t.cycles+=e,t.pc+=e}}else if(39680==(65280&e)){if(t.readData(32+((248&e)>>3))&1<<(7&e)){const e=a(t.progMem[t.pc+1])?2:1;t.cycles+=e,t.pc+=e}}else if(38656==(65280&e)){const a=2*((48&e)>>4)+24,d=t.dataView.getUint16(a,!0),i=15&e|(192&e)>>2,s=d-i;t.dataView.setUint16(a,s,!0);let c=192&t.data[95];c|=s?0:2,c|=32768&s?4:0,c|=d&~s&32768?8:0,c|=c>>2&1^c>>3&1?16:0,c|=i>d?1:0,c|=1&(~d&i|i&s|s&~d)?32:0,t.data[95]=c,t.cycles++}else if(64512==(65032&e)){if(!(t.data[(496&e)>>4]&1<<(7&e))){const e=a(t.progMem[t.pc+1])?2:1;t.cycles+=e,t.pc+=e}}else if(65024==(65032&e)){if(t.data[(496&e)>>4]&1<<(7&e)){const e=a(t.progMem[t.pc+1])?2:1;t.cycles+=e,t.pc+=e}}else if(38280===e);else if(38376===e);else if(38392===e);else if(37376==(65039&e)){const a=t.data[(496&e)>>4],d=t.progMem[t.pc+1];t.writeData(d,a),t.pc++,t.cycles++}else if(37388==(65039&e))t.writeData(t.dataView.getUint16(26,!0),t.data[(496&e)>>4]),t.cycles++;else if(37389==(65039&e)){const a=t.dataView.getUint16(26,!0);t.writeData(a,t.data[(496&e)>>4]),t.dataView.setUint16(26,a+1,!0),t.cycles++}else if(37390==(65039&e)){const a=t.data[(496&e)>>4],d=t.dataView.getUint16(26,!0)-1;t.dataView.setUint16(26,d,!0),t.writeData(d,a),t.cycles++}else if(33288==(65039&e))t.writeData(t.dataView.getUint16(28,!0),t.data[(496&e)>>4]),t.cycles++;else if(37385==(65039&e)){const a=t.data[(496&e)>>4],d=t.dataView.getUint16(28,!0);t.writeData(d,a),t.dataView.setUint16(28,d+1,!0),t.cycles++}else if(37386==(65039&e)){const a=t.data[(496&e)>>4],d=t.dataView.getUint16(28,!0)-1;t.dataView.setUint16(28,d,!0),t.writeData(d,a),t.cycles++}else if(33288==(53768&e)&&7&e|(3072&e)>>7|(8192&e)>>8)t.writeData(t.dataView.getUint16(28,!0)+(7&e|(3072&e)>>7|(8192&e)>>8),t.data[(496&e)>>4]),t.cycles++;else if(33280==(65039&e))t.writeData(t.dataView.getUint16(30,!0),t.data[(496&e)>>4]),t.cycles++;else if(37377==(65039&e)){const a=t.dataView.getUint16(30,!0);t.writeData(a,t.data[(496&e)>>4]),t.dataView.setUint16(30,a+1,!0),t.cycles++}else if(37378==(65039&e)){const a=t.data[(496&e)>>4],d=t.dataView.getUint16(30,!0)-1;t.dataView.setUint16(30,d,!0),t.writeData(d,a),t.cycles++}else if(33280==(53768&e)&&7&e|(3072&e)>>7|(8192&e)>>8)t.writeData(t.dataView.getUint16(30,!0)+(7&e|(3072&e)>>7|(8192&e)>>8),t.data[(496&e)>>4]),t.cycles++;else if(6144==(64512&e)){const a=t.data[(496&e)>>4],d=t.data[15&e|(512&e)>>5],i=a-d;t.data[(496&e)>>4]=i;let s=192&t.data[95];s|=i?0:2,s|=128&i?4:0,s|=(a^d)&(a^i)&128?8:0,s|=s>>2&1^s>>3&1?16:0,s|=d>a?1:0,s|=1&(~a&d|d&i|i&~a)?32:0,t.data[95]=s}else if(20480==(61440&e)){const a=t.data[16+((240&e)>>4)],d=15&e|(3840&e)>>4,i=a-d;t.data[16+((240&e)>>4)]=i;let s=192&t.data[95];s|=i?0:2,s|=128&i?4:0,s|=(a^d)&(a^i)&128?8:0,s|=s>>2&1^s>>3&1?16:0,s|=d>a?1:0,s|=1&(~a&d|d&i|i&~a)?32:0,t.data[95]=s}else if(37890==(65039&e)){const a=(496&e)>>4,d=t.data[a];t.data[a]=(15&d)<<4|(240&d)>>>4}else if(38312===e);else if(37380==(65039&e)){const a=(496&e)>>4,d=t.data[a],i=t.data[t.dataView.getUint16(30,!0)];t.data[t.dataView.getUint16(30,!0)]=d,t.data[a]=i}t.pc=(t.pc+1)%t.progMem.length,t.cycles++}Object.defineProperty(exports,"__esModule",{value:!0}),exports.avrInstruction=t;
},{}],"ZLro":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVRIOPort=exports.PinOverrideMode=exports.PinState=exports.portLConfig=exports.portKConfig=exports.portJConfig=exports.portHConfig=exports.portGConfig=exports.portFConfig=exports.portEConfig=exports.portDConfig=exports.portCConfig=exports.portBConfig=exports.portAConfig=void 0;const t={PIN:32,DDR:33,PORT:34};exports.portAConfig=t;const o={PIN:35,DDR:36,PORT:37};exports.portBConfig=o;const e={PIN:38,DDR:39,PORT:40};exports.portCConfig=e;const i={PIN:41,DDR:42,PORT:43};exports.portDConfig=i;const s={PIN:44,DDR:45,PORT:46};exports.portEConfig=s;const r={PIN:47,DDR:48,PORT:49};exports.portFConfig=r;const n={PIN:50,DDR:51,PORT:52};exports.portGConfig=n;const p={PIN:256,DDR:257,PORT:258};exports.portHConfig=p;const a={PIN:259,DDR:260,PORT:261};exports.portJConfig=a;const P={PIN:262,DDR:263,PORT:264};exports.portKConfig=P;const h={PIN:265,DDR:266,PORT:267};var l,d;exports.portLConfig=h,exports.PinState=l,function(t){t[t.Low=0]="Low",t[t.High=1]="High",t[t.Input=2]="Input",t[t.InputPullUp=3]="InputPullUp"}(l||(exports.PinState=l={})),exports.PinOverrideMode=d,function(t){t[t.None=0]="None",t[t.Enable=1]="Enable",t[t.Set=2]="Set",t[t.Clear=3]="Clear",t[t.Toggle=4]="Toggle"}(d||(exports.PinOverrideMode=d={}));class R{constructor(t,o){this.cpu=t,this.portConfig=o,this.listeners=[],this.pinValue=0,this.overrideMask=255,this.lastValue=0,this.lastDdr=0,t.writeHooks[o.DDR]=(e=>{const i=t.data[o.PORT];return t.data[o.DDR]=e,this.updatePinRegister(i,e),this.writeGpio(i,e),!0}),t.writeHooks[o.PORT]=(e=>{const i=t.data[o.DDR];return t.data[o.PORT]=e,this.updatePinRegister(e,i),this.writeGpio(e,i),!0}),t.writeHooks[o.PIN]=(e=>{const i=t.data[o.PORT],s=t.data[o.DDR],r=i^e;return t.data[o.PORT]=r,t.data[o.PIN]=t.data[o.PIN]&~s|r&s,this.writeGpio(r,s),!0}),t.gpioTimerHooks[o.PORT]=((e,i)=>{const s=1<<e;if(i==d.None)this.overrideMask|=s;else switch(this.overrideMask&=~s,i){case d.Enable:this.overrideValue&=~s,this.overrideValue|=t.data[o.PORT]&s;break;case d.Set:this.overrideValue|=s;break;case d.Clear:this.overrideValue&=~s;break;case d.Toggle:this.overrideValue^=s}this.writeGpio(t.data[o.PORT],t.data[o.DDR])})}addListener(t){this.listeners.push(t)}removeListener(t){this.listeners=this.listeners.filter(o=>o!==t)}pinState(t){const o=this.cpu.data[this.portConfig.DDR],e=this.cpu.data[this.portConfig.PORT],i=1<<t;return o&i?this.lastValue&i?l.High:l.Low:e&i?l.InputPullUp:l.Input}setPin(t,o){const e=1<<t;this.pinValue&=~e,o&&(this.pinValue|=e),this.updatePinRegister(this.cpu.data[this.portConfig.PORT],this.cpu.data[this.portConfig.DDR])}updatePinRegister(t,o){this.cpu.data[this.portConfig.PIN]=this.pinValue&~o|t&o}writeGpio(t,o){const e=(t&this.overrideMask|this.overrideValue)&o|t&~o,i=this.lastValue;if(e!==i||o!==this.lastDdr){this.lastValue=e,this.lastDdr=o;for(const t of this.listeners)t(e,i)}}}exports.AVRIOPort=R;
},{}],"xYD4":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVRTimer=exports.timer2Config=exports.timer1Config=exports.timer0Config=void 0;var t=require("./gpio");const i={0:0,1:1,2:8,3:64,4:256,5:1024,6:0,7:0},e={TOV:1,OCFA:2,OCFB:4,TOIE:1,OCIEA:2,OCIEB:4},o=Object.assign({bits:8,captureInterrupt:0,compAInterrupt:28,compBInterrupt:30,ovfInterrupt:32,TIFR:53,OCRA:71,OCRB:72,ICR:0,TCNT:70,TCCRA:68,TCCRB:69,TCCRC:0,TIMSK:110,dividers:i,compPortA:t.portDConfig.PORT,compPinA:6,compPortB:t.portDConfig.PORT,compPinB:5},e);exports.timer0Config=o;const s=Object.assign({bits:16,captureInterrupt:20,compAInterrupt:22,compBInterrupt:24,ovfInterrupt:26,TIFR:54,OCRA:136,OCRB:138,ICR:134,TCNT:132,TCCRA:128,TCCRB:129,TCCRC:130,TIMSK:111,dividers:i,compPortA:t.portBConfig.PORT,compPinA:1,compPortB:t.portBConfig.PORT,compPinB:2},e);exports.timer1Config=s;const r=Object.assign({bits:8,captureInterrupt:0,compAInterrupt:14,compBInterrupt:16,ovfInterrupt:18,TIFR:55,OCRA:179,OCRB:180,ICR:0,TCNT:178,TCCRA:176,TCCRB:177,TCCRC:0,TIMSK:112,dividers:{0:0,1:1,2:8,3:32,4:64,5:128,6:256,7:1024},compPortA:t.portBConfig.PORT,compPinA:3,compPortB:t.portDConfig.PORT,compPinB:3},e);var c,h,n;exports.timer2Config=r,function(t){t[t.Normal=0]="Normal",t[t.PWMPhaseCorrect=1]="PWMPhaseCorrect",t[t.CTC=2]="CTC",t[t.FastPWM=3]="FastPWM",t[t.PWMPhaseFrequencyCorrect=4]="PWMPhaseFrequencyCorrect",t[t.Reserved=5]="Reserved"}(c||(c={})),function(t){t[t.Max=0]="Max",t[t.Top=1]="Top",t[t.Bottom=2]="Bottom"}(h||(h={})),function(t){t[t.Immediate=0]="Immediate",t[t.Top=1]="Top",t[t.Bottom=2]="Bottom"}(n||(n={}));const p=1,a=2,d=1,{Normal:C,PWMPhaseCorrect:u,CTC:m,FastPWM:B,Reserved:T,PWMPhaseFrequencyCorrect:g}=c,O=[[C,255,n.Immediate,h.Max,0],[u,255,n.Top,h.Bottom,0],[m,p,n.Immediate,h.Max,0],[B,255,n.Bottom,h.Max,0],[T,255,n.Immediate,h.Max,0],[u,p,n.Top,h.Bottom,d],[T,255,n.Immediate,h.Max,0],[B,p,n.Bottom,h.Top,d]],M=[[C,65535,n.Immediate,h.Max,0],[u,255,n.Top,h.Bottom,0],[u,511,n.Top,h.Bottom,0],[u,1023,n.Top,h.Bottom,0],[m,p,n.Immediate,h.Max,0],[B,255,n.Bottom,h.Top,0],[B,511,n.Bottom,h.Top,0],[B,1023,n.Bottom,h.Top,0],[g,a,n.Bottom,h.Bottom,0],[g,p,n.Bottom,h.Bottom,d],[u,a,n.Top,h.Bottom,0],[u,p,n.Top,h.Bottom,d],[m,a,n.Immediate,h.Max,0],[T,65535,n.Immediate,h.Max,0],[B,a,n.Bottom,h.Top,d],[B,p,n.Bottom,h.Top,d]];function l(i){switch(i){case 1:return t.PinOverrideMode.Toggle;case 2:return t.PinOverrideMode.Clear;case 3:return t.PinOverrideMode.Set;default:return t.PinOverrideMode.Enable}}class A{constructor(t,i){if(this.cpu=t,this.config=i,this.MAX=16===this.config.bits?65535:255,this.lastCycle=0,this.ocrA=0,this.nextOcrA=0,this.ocrB=0,this.nextOcrB=0,this.ocrUpdateMode=n.Immediate,this.tovUpdateMode=h.Max,this.icr=0,this.tcnt=0,this.tcntNext=0,this.tcntUpdated=!1,this.updateDivider=!1,this.countingUp=!0,this.divider=0,this.highByteTemp=0,this.OVF={address:this.config.ovfInterrupt,flagRegister:this.config.TIFR,flagMask:this.config.TOV,enableRegister:this.config.TIMSK,enableMask:this.config.TOIE},this.OCFA={address:this.config.compAInterrupt,flagRegister:this.config.TIFR,flagMask:this.config.OCFA,enableRegister:this.config.TIMSK,enableMask:this.config.OCIEA},this.OCFB={address:this.config.compBInterrupt,flagRegister:this.config.TIFR,flagMask:this.config.OCFB,enableRegister:this.config.TIMSK,enableMask:this.config.OCIEB},this.count=((t=!0)=>{const{divider:i,lastCycle:e,cpu:o}=this,{cycles:s}=o,r=s-e;if(i&&r>=i){const t=Math.floor(r/i);this.lastCycle+=t*i;const e=this.tcnt,{timerMode:s,TOP:c}=this,p=s===u||s===g,a=p?this.phasePwmCount(e,t):(e+t)%(c+1),d=e+t>c;if(this.tcntUpdated||(this.tcnt=a,p||this.timerUpdated(a,e)),!p){if(s===B&&d){const{compA:t,compB:i}=this;t&&this.updateCompPin(t,"A",!0),i&&this.updateCompPin(i,"B",!0)}this.ocrUpdateMode==n.Bottom&&d&&(this.ocrA=this.nextOcrA,this.ocrB=this.nextOcrB),!d||this.tovUpdateMode!=h.Top&&c!==this.MAX||o.setInterruptFlag(this.OVF)}}if(this.tcntUpdated&&(this.tcnt=this.tcntNext,this.tcntUpdated=!1),this.updateDivider){const t=this.config.dividers[this.CS];return this.lastCycle=t?this.cpu.cycles:0,this.updateDivider=!1,this.divider=t,void(t&&o.addClockEvent(this.count,this.lastCycle+t-o.cycles))}t&&i&&o.addClockEvent(this.count,this.lastCycle+i-o.cycles)}),this.updateWGMConfig(),this.cpu.readHooks[i.TCNT]=(t=>(this.count(!1),16===this.config.bits&&(this.cpu.data[t+1]=this.tcnt>>8),this.cpu.data[t]=255&this.tcnt)),this.cpu.writeHooks[i.TCNT]=(t=>{this.tcntNext=this.highByteTemp<<8|t,this.countingUp=!0,this.tcntUpdated=!0,this.cpu.updateClockEvent(this.count,0),this.divider&&this.timerUpdated(this.tcntNext,this.tcntNext)}),this.cpu.writeHooks[i.OCRA]=(t=>{this.nextOcrA=this.highByteTemp<<8|t,this.ocrUpdateMode===n.Immediate&&(this.ocrA=this.nextOcrA)}),this.cpu.writeHooks[i.OCRB]=(t=>{this.nextOcrB=this.highByteTemp<<8|t,this.ocrUpdateMode===n.Immediate&&(this.ocrB=this.nextOcrB)}),this.cpu.writeHooks[i.ICR]=(t=>{this.icr=this.highByteTemp<<8|t}),16===this.config.bits){const t=t=>{this.highByteTemp=t};this.cpu.writeHooks[i.TCNT+1]=t,this.cpu.writeHooks[i.OCRA+1]=t,this.cpu.writeHooks[i.OCRB+1]=t,this.cpu.writeHooks[i.ICR+1]=t}t.writeHooks[i.TCCRA]=(t=>(this.cpu.data[i.TCCRA]=t,this.updateWGMConfig(),!0)),t.writeHooks[i.TCCRB]=(t=>(this.cpu.data[i.TCCRB]=t,this.updateDivider=!0,this.cpu.clearClockEvent(this.count),this.cpu.addClockEvent(this.count,0),this.updateWGMConfig(),!0)),t.writeHooks[i.TIFR]=(t=>(this.cpu.data[i.TIFR]=t,this.cpu.clearInterruptByFlag(this.OVF,t),this.cpu.clearInterruptByFlag(this.OCFA,t),this.cpu.clearInterruptByFlag(this.OCFB,t),!0)),t.writeHooks[i.TIMSK]=(t=>{this.cpu.updateInterruptEnable(this.OVF,t),this.cpu.updateInterruptEnable(this.OCFA,t),this.cpu.updateInterruptEnable(this.OCFB,t)})}reset(){this.divider=0,this.lastCycle=0,this.ocrA=0,this.nextOcrA=0,this.ocrB=0,this.nextOcrB=0,this.icr=0,this.tcnt=0,this.tcntNext=0,this.tcntUpdated=!1,this.countingUp=!1,this.updateDivider=!0}get TCCRA(){return this.cpu.data[this.config.TCCRA]}get TCCRB(){return this.cpu.data[this.config.TCCRB]}get TIMSK(){return this.cpu.data[this.config.TIMSK]}get CS(){return 7&this.TCCRB}get WGM(){const t=16===this.config.bits?24:8;return(this.TCCRB&t)>>1|3&this.TCCRA}get TOP(){switch(this.topValue){case p:return this.ocrA;case a:return this.icr;default:return this.topValue}}updateWGMConfig(){const{config:i,WGM:e}=this,o=16===i.bits?M:O,s=this.cpu.data[i.TCCRA],[r,c,h,n,p]=o[e];this.timerMode=r,this.topValue=c,this.ocrUpdateMode=h,this.tovUpdateMode=n;const a=r===B||r===u||r===g,C=this.compA;this.compA=s>>6&3,1!==this.compA||!a||p&d||(this.compA=0),!!C!=!!this.compA&&this.updateCompA(this.compA?t.PinOverrideMode.Enable:t.PinOverrideMode.None);const m=this.compB;this.compB=s>>4&3,1===this.compB&&a&&(this.compB=0),!!m!=!!this.compB&&this.updateCompB(this.compB?t.PinOverrideMode.Enable:t.PinOverrideMode.None)}phasePwmCount(t,i){const{ocrA:e,ocrB:o,TOP:s,tcntUpdated:r}=this;for(;i>0;)this.countingUp?++t!==s||r||(this.countingUp=!1,this.ocrUpdateMode===n.Top&&(this.ocrA=this.nextOcrA,this.ocrB=this.nextOcrB)):--t||r||(this.countingUp=!0,this.cpu.setInterruptFlag(this.OVF),this.ocrUpdateMode===n.Bottom&&(this.ocrA=this.nextOcrA,this.ocrB=this.nextOcrB)),r||t!==e||(this.cpu.setInterruptFlag(this.OCFA),this.compA&&this.updateCompPin(this.compA,"A")),r||t!==o||(this.cpu.setInterruptFlag(this.OCFB),this.compB&&this.updateCompPin(this.compB,"B")),i--;return t}timerUpdated(t,i){const{ocrA:e,ocrB:o}=this,s=i>t;(i<e||s)&&t>=e&&(this.cpu.setInterruptFlag(this.OCFA),this.compA&&this.updateCompPin(this.compA,"A")),(i<o||s)&&t>=o&&(this.cpu.setInterruptFlag(this.OCFB),this.compB&&this.updateCompPin(this.compB,"B"))}updateCompPin(i,e,o=!1){let s=t.PinOverrideMode.None;const r=3===i,c=this.countingUp===r;switch(this.timerMode){case C:case m:s=l(i);break;case B:s=1===i?o?t.PinOverrideMode.None:t.PinOverrideMode.Toggle:r!==o?t.PinOverrideMode.Set:t.PinOverrideMode.Clear;break;case u:case g:s=1===i?t.PinOverrideMode.Toggle:c?t.PinOverrideMode.Set:t.PinOverrideMode.Clear}s!==t.PinOverrideMode.None&&("A"===e?this.updateCompA(s):this.updateCompB(s))}updateCompA(t){const{compPortA:i,compPinA:e}=this.config,o=this.cpu.gpioTimerHooks[i];o&&o(e,t,i)}updateCompB(t){const{compPortB:i,compPinB:e}=this.config,o=this.cpu.gpioTimerHooks[i];o&&o(e,t,i)}}exports.AVRTimer=A;
},{"./gpio":"ZLro"}],"RlY3":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVRUSART=exports.usart0Config=void 0;const t={rxCompleteInterrupt:36,dataRegisterEmptyInterrupt:38,txCompleteInterrupt:40,UCSRA:192,UCSRB:193,UCSRC:194,UBRRL:196,UBRRH:197,UDR:198};exports.usart0Config=t;const e=128,s=64,i=32,r=16,a=8,n=4,h=2,u=1,c=128,o=64,l=32,p=16,R=8,C=4,g=2,f=1,d=128,B=64,U=32,y=16,S=8,x=4,m=2,I=1,b={5:31,6:63,7:127,8:255,9:255};class E{constructor(t,r,a){this.cpu=t,this.config=r,this.freqHz=a,this.onByteTransmit=null,this.onLineTransmit=null,this.onRxComplete=null,this.rxBusyValue=!1,this.rxByte=0,this.lineBuffer="",this.RXC={address:this.config.rxCompleteInterrupt,flagRegister:this.config.UCSRA,flagMask:e,enableRegister:this.config.UCSRB,enableMask:c,constant:!0},this.UDRE={address:this.config.dataRegisterEmptyInterrupt,flagRegister:this.config.UCSRA,flagMask:i,enableRegister:this.config.UCSRB,enableMask:l},this.TXC={address:this.config.txCompleteInterrupt,flagRegister:this.config.UCSRA,flagMask:s,enableRegister:this.config.UCSRB,enableMask:o},this.reset(),this.cpu.writeHooks[r.UCSRA]=(e=>(t.data[r.UCSRA]=e&(u|h),t.clearInterruptByFlag(this.TXC,e),!0)),this.cpu.writeHooks[r.UCSRB]=((e,s)=>{t.updateInterruptEnable(this.RXC,e),t.updateInterruptEnable(this.UDRE,e),t.updateInterruptEnable(this.TXC,e),e&p&&s&p&&t.clearInterrupt(this.RXC),e&R&&!(s&R)&&t.setInterruptFlag(this.UDRE)}),this.cpu.readHooks[r.UDR]=(()=>{var t;const e=null!==(t=b[this.bitsPerChar])&&void 0!==t?t:255,s=this.rxByte&e;return this.rxByte=0,this.cpu.clearInterrupt(this.RXC),s}),this.cpu.writeHooks[r.UDR]=(e=>{if(this.onByteTransmit&&this.onByteTransmit(e),this.onLineTransmit){const t=String.fromCharCode(e);"\n"===t?(this.onLineTransmit(this.lineBuffer),this.lineBuffer=""):this.lineBuffer+=t}this.cpu.addClockEvent(()=>{t.setInterruptFlag(this.UDRE),t.setInterruptFlag(this.TXC)},this.cyclesPerChar),this.cpu.clearInterrupt(this.TXC),this.cpu.clearInterrupt(this.UDRE)})}reset(){this.cpu.data[this.config.UCSRA]=i,this.cpu.data[this.config.UCSRB]=0,this.cpu.data[this.config.UCSRC]=x|m,this.rxBusyValue=!1,this.rxByte=0,this.lineBuffer=""}get rxBusy(){return this.rxBusyValue}writeByte(t){const{cpu:e,config:s}=this;return!(this.rxBusyValue||!(e.data[s.UCSRB]&p))&&(this.rxBusyValue=!0,e.addClockEvent(()=>{var s;this.rxByte=t,this.rxBusyValue=!1,e.setInterruptFlag(this.RXC),null===(s=this.onRxComplete)||void 0===s||s.call(this)},this.cyclesPerChar),!0)}get cyclesPerChar(){const t=1+this.bitsPerChar+this.stopBits+(this.parityEnabled?1:0);return(this.UBRR*this.multiplier+1)*t}get UBRR(){return this.cpu.data[this.config.UBRRH]<<8|this.cpu.data[this.config.UBRRL]}get multiplier(){return this.cpu.data[this.config.UCSRA]&h?8:16}get baudRate(){return Math.floor(this.freqHz/(this.multiplier*(1+this.UBRR)))}get bitsPerChar(){switch((this.cpu.data[this.config.UCSRC]&(x|m))>>1|this.cpu.data[this.config.UCSRB]&C){case 0:return 5;case 1:return 6;case 2:return 7;case 3:return 8;default:case 7:return 9}}get stopBits(){return this.cpu.data[this.config.UCSRC]&S?2:1}get parityEnabled(){return!!(this.cpu.data[this.config.UCSRC]&U)}get parityOdd(){return!!(this.cpu.data[this.config.UCSRC]&y)}}exports.AVRUSART=E;
},{}],"JcIU":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVREEPROM=exports.eepromConfig=exports.EEPROMMemoryBackend=void 0;class e{constructor(e){this.memory=new Uint8Array(e),this.memory.fill(255)}readMemory(e){return this.memory[e]}writeMemory(e,t){this.memory[e]&=t}eraseMemory(e){this.memory[e]=255}}exports.EEPROMMemoryBackend=e;const t={eepromReadyInterrupt:44,EECR:63,EEDR:64,EEARL:65,EEARH:66,eraseCycles:28800,writeCycles:28800};exports.eepromConfig=t;const s=1,i=2,r=4,c=8,o=16,h=32;class a{constructor(e,a,n=t){this.cpu=e,this.backend=a,this.config=n,this.writeEnabledCycles=0,this.writeCompleteCycles=0,this.EER={address:this.config.eepromReadyInterrupt,flagRegister:this.config.EECR,flagMask:i,enableRegister:this.config.EECR,enableMask:c,constant:!0,inverseFlag:!0},this.cpu.writeHooks[this.config.EECR]=(e=>{const{EEARH:t,EEARL:c,EECR:a,EEDR:n}=this.config,l=this.cpu.data[t]<<8|this.cpu.data[c];if(e&s&&this.cpu.clearInterrupt(this.EER),e&r){const e=4;this.writeEnabledCycles=this.cpu.cycles+e,this.cpu.addClockEvent(()=>{this.cpu.data[a]&=~r},e)}if(e&s)return this.cpu.data[n]=this.backend.readMemory(l),this.cpu.cycles+=4,!0;if(e&i){if(this.cpu.cycles>=this.writeEnabledCycles)return!0;if(this.cpu.cycles<this.writeCompleteCycles)return!0;const t=this.cpu.data[n];return this.writeCompleteCycles=this.cpu.cycles,e&h||(this.backend.eraseMemory(l),this.writeCompleteCycles+=this.config.eraseCycles),e&o||(this.backend.writeMemory(l,t),this.writeCompleteCycles+=this.config.writeCycles),this.cpu.data[a]|=i,this.cpu.addClockEvent(()=>{this.cpu.setInterruptFlag(this.EER)},this.writeCompleteCycles-this.cpu.cycles),this.cpu.cycles+=2,!0}return!1})}}exports.AVREEPROM=a;
},{}],"SWYB":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVRTWI=exports.NoopTWIEventHandler=exports.twiConfig=void 0;const t=128,e=64,s=32,i=16,a=8,r=4,c=1,n=248,o=2,h=1,p=3,u=254,l=1,d=0,T=248,W=8,f=16,R=24,g=32,S=40,w=48,v=56,I=64,C=72,H=80,m=88,x={twiInterrupt:48,TWBR:184,TWSR:185,TWAR:186,TWDR:187,TWCR:188,TWAMR:189};exports.twiConfig=x;class y{constructor(t){this.twi=t}start(){this.twi.completeStart()}stop(){this.twi.completeStop()}connectToSlave(){this.twi.completeConnect(!1)}writeByte(){this.twi.completeWrite(!1)}readByte(){this.twi.completeRead(255)}}exports.NoopTWIEventHandler=y;class B{constructor(a,n,o){this.cpu=a,this.config=n,this.freqHz=o,this.eventHandler=new y(this),this.TWI={address:this.config.twiInterrupt,flagRegister:this.config.TWCR,flagMask:t,enableRegister:this.config.TWCR,enableMask:c},this.updateStatus(T),this.cpu.writeHooks[n.TWCR]=(a=>{this.cpu.data[n.TWCR]=a;const c=a&t;this.cpu.clearInterruptByFlag(this.TWI,a),this.cpu.updateInterruptEnable(this.TWI,a);const{status:o}=this;if(c&&a&r){const t=this.cpu.data[this.config.TWDR];return this.cpu.addClockEvent(()=>{if(a&s)this.eventHandler.start(o!==T);else if(a&i)this.eventHandler.stop();else if(o===W)this.eventHandler.connectToSlave(t>>1,!(1&t));else if(o===R||o===S)this.eventHandler.writeByte(t);else if(o===I||o===H){const t=!!(a&e);this.eventHandler.readByte(t)}},0),!0}})}get prescaler(){switch(this.cpu.data[this.config.TWSR]&p){case 0:return 1;case 1:return 4;case 2:return 16;case 3:return 64}throw new Error("Invalid prescaler value!")}get sclFrequency(){return this.freqHz/(16+2*this.cpu.data[this.config.TWBR]*this.prescaler)}completeStart(){this.updateStatus(this.status===T?W:f)}completeStop(){this.cpu.data[this.config.TWCR]&=~i,this.updateStatus(T)}completeConnect(t){1&this.cpu.data[this.config.TWDR]?this.updateStatus(t?I:C):this.updateStatus(t?R:g)}completeWrite(t){this.updateStatus(t?S:w)}completeRead(t){const s=!!(this.cpu.data[this.config.TWCR]&e);this.cpu.data[this.config.TWDR]=t,this.updateStatus(s?H:m)}get status(){return this.cpu.data[this.config.TWSR]&n}updateStatus(t){const{TWSR:e}=this.config;this.cpu.data[e]=this.cpu.data[e]&~n|t,this.cpu.setInterruptFlag(this.TWI)}}exports.AVRTWI=B;
},{}],"q1W3":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVRSPI=exports.spiConfig=void 0;const t=128,i=64,s=32,e=16,r=8,c=4,n=2,a=1,o=3,h=128,u=64,d=1,p={spiInterrupt:34,SPCR:76,SPSR:77,SPDR:78};exports.spiConfig=p;const l=8;class S{constructor(s,e,r){this.cpu=s,this.config=e,this.freqHz=r,this.onTransfer=null,this.transmissionActive=!1,this.receivedByte=0,this.SPI={address:this.config.spiInterrupt,flagRegister:this.config.SPSR,flagMask:h,enableRegister:this.config.SPCR,enableMask:t};const{SPCR:c,SPSR:n,SPDR:a}=e;s.writeHooks[a]=(t=>{var e,r;if(!(s.data[c]&i))return;if(this.transmissionActive)return s.data[n]|=u,!0;s.data[n]&=~u,this.cpu.clearInterrupt(this.SPI),this.receivedByte=null!==(r=null===(e=this.onTransfer)||void 0===e?void 0:e.call(this,t))&&void 0!==r?r:0;const o=this.clockDivider*l;return this.transmissionActive=!0,this.cpu.addClockEvent(()=>{this.cpu.data[a]=this.receivedByte,this.cpu.setInterruptFlag(this.SPI),this.transmissionActive=!1},o),!0}),s.writeHooks[n]=(t=>{this.cpu.data[n]=t,this.cpu.clearInterruptByFlag(this.SPI,t)})}reset(){this.transmissionActive=!1,this.receivedByte=0}get isMaster(){return!!(this.cpu.data[this.config.SPCR]&e)}get dataOrder(){return this.cpu.data[this.config.SPCR]&s?"lsbFirst":"msbFirst"}get spiMode(){return(this.cpu.data[this.config.SPCR]&c?2:0)|(this.cpu.data[this.config.SPCR]&r?1:0)}get clockDivider(){const t=this.cpu.data[this.config.SPSR]&d?2:4;switch(this.cpu.data[this.config.SPCR]&o){case 0:return t;case 1:return 4*t;case 2:return 16*t;case 3:return 32*t}throw new Error("Invalid divider value!")}get spiFrequency(){return this.freqHz/this.clockDivider}}exports.AVRSPI=S;
},{}],"UXBG":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVRClock=exports.clockConfig=void 0;const e=128,c={CLKPR:97};exports.clockConfig=c;const s=[1,2,4,8,16,32,64,128,256,2,4,8,16,32,64,128];class t{constructor(t,l,i=c){this.cpu=t,this.baseFreqHz=l,this.config=i,this.clockEnabledCycles=0,this.prescalerValue=1,this.cyclesDelta=0,this.cpu.writeHooks[this.config.CLKPR]=(c=>{if((!this.clockEnabledCycles||this.clockEnabledCycles<t.cycles)&&c===e)this.clockEnabledCycles=this.cpu.cycles+4;else if(this.clockEnabledCycles&&this.clockEnabledCycles>=t.cycles){this.clockEnabledCycles=0;const e=15&c,l=this.prescalerValue;this.prescalerValue=s[e],this.cpu.data[this.config.CLKPR]=e,l!==this.prescalerValue&&(this.cyclesDelta=(t.cycles+this.cyclesDelta)*(l/this.prescalerValue)-t.cycles)}return!0})}get frequency(){return this.baseFreqHz/this.prescalerValue}get prescaler(){return this.prescalerValue}get timeNanos(){return(this.cpu.cycles+this.cyclesDelta)/this.frequency*1e9}get timeMicros(){return(this.cpu.cycles+this.cyclesDelta)/this.frequency*1e6}get timeMillis(){return(this.cpu.cycles+this.cyclesDelta)/this.frequency*1e3}}exports.AVRClock=t;
},{}],"WdSo":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0});var e={CPU:!0,ICPU:!0,CPUMemoryHook:!0,CPUMemoryHooks:!0,avrInstruction:!0,avrInterrupt:!0,AVRTimer:!0,AVRTimerConfig:!0,timer0Config:!0,timer1Config:!0,timer2Config:!0,AVRIOPort:!0,GPIOListener:!0,AVRPortConfig:!0,portAConfig:!0,portBConfig:!0,portCConfig:!0,portDConfig:!0,portEConfig:!0,portFConfig:!0,portGConfig:!0,portHConfig:!0,portJConfig:!0,portKConfig:!0,portLConfig:!0,PinState:!0,AVRUSART:!0,usart0Config:!0,AVREEPROM:!0,AVREEPROMConfig:!0,EEPROMBackend:!0,EEPROMMemoryBackend:!0,eepromConfig:!0,spiConfig:!0,SPIConfig:!0,SPITransferCallback:!0,AVRSPI:!0,AVRClock:!0,AVRClockConfig:!0,clockConfig:!0};Object.defineProperty(exports,"CPU",{enumerable:!0,get:function(){return r.CPU}}),Object.defineProperty(exports,"ICPU",{enumerable:!0,get:function(){return r.ICPU}}),Object.defineProperty(exports,"CPUMemoryHook",{enumerable:!0,get:function(){return r.CPUMemoryHook}}),Object.defineProperty(exports,"CPUMemoryHooks",{enumerable:!0,get:function(){return r.CPUMemoryHooks}}),Object.defineProperty(exports,"avrInstruction",{enumerable:!0,get:function(){return t.avrInstruction}}),Object.defineProperty(exports,"avrInterrupt",{enumerable:!0,get:function(){return n.avrInterrupt}}),Object.defineProperty(exports,"AVRTimer",{enumerable:!0,get:function(){return o.AVRTimer}}),Object.defineProperty(exports,"AVRTimerConfig",{enumerable:!0,get:function(){return o.AVRTimerConfig}}),Object.defineProperty(exports,"timer0Config",{enumerable:!0,get:function(){return o.timer0Config}}),Object.defineProperty(exports,"timer1Config",{enumerable:!0,get:function(){return o.timer1Config}}),Object.defineProperty(exports,"timer2Config",{enumerable:!0,get:function(){return o.timer2Config}}),Object.defineProperty(exports,"AVRIOPort",{enumerable:!0,get:function(){return i.AVRIOPort}}),Object.defineProperty(exports,"GPIOListener",{enumerable:!0,get:function(){return i.GPIOListener}}),Object.defineProperty(exports,"AVRPortConfig",{enumerable:!0,get:function(){return i.AVRPortConfig}}),Object.defineProperty(exports,"portAConfig",{enumerable:!0,get:function(){return i.portAConfig}}),Object.defineProperty(exports,"portBConfig",{enumerable:!0,get:function(){return i.portBConfig}}),Object.defineProperty(exports,"portCConfig",{enumerable:!0,get:function(){return i.portCConfig}}),Object.defineProperty(exports,"portDConfig",{enumerable:!0,get:function(){return i.portDConfig}}),Object.defineProperty(exports,"portEConfig",{enumerable:!0,get:function(){return i.portEConfig}}),Object.defineProperty(exports,"portFConfig",{enumerable:!0,get:function(){return i.portFConfig}}),Object.defineProperty(exports,"portGConfig",{enumerable:!0,get:function(){return i.portGConfig}}),Object.defineProperty(exports,"portHConfig",{enumerable:!0,get:function(){return i.portHConfig}}),Object.defineProperty(exports,"portJConfig",{enumerable:!0,get:function(){return i.portJConfig}}),Object.defineProperty(exports,"portKConfig",{enumerable:!0,get:function(){return i.portKConfig}}),Object.defineProperty(exports,"portLConfig",{enumerable:!0,get:function(){return i.portLConfig}}),Object.defineProperty(exports,"PinState",{enumerable:!0,get:function(){return i.PinState}}),Object.defineProperty(exports,"AVRUSART",{enumerable:!0,get:function(){return f.AVRUSART}}),Object.defineProperty(exports,"usart0Config",{enumerable:!0,get:function(){return f.usart0Config}}),Object.defineProperty(exports,"AVREEPROM",{enumerable:!0,get:function(){return u.AVREEPROM}}),Object.defineProperty(exports,"AVREEPROMConfig",{enumerable:!0,get:function(){return u.AVREEPROMConfig}}),Object.defineProperty(exports,"EEPROMBackend",{enumerable:!0,get:function(){return u.EEPROMBackend}}),Object.defineProperty(exports,"EEPROMMemoryBackend",{enumerable:!0,get:function(){return u.EEPROMMemoryBackend}}),Object.defineProperty(exports,"eepromConfig",{enumerable:!0,get:function(){return u.eepromConfig}}),Object.defineProperty(exports,"spiConfig",{enumerable:!0,get:function(){return c.spiConfig}}),Object.defineProperty(exports,"SPIConfig",{enumerable:!0,get:function(){return c.SPIConfig}}),Object.defineProperty(exports,"SPITransferCallback",{enumerable:!0,get:function(){return c.SPITransferCallback}}),Object.defineProperty(exports,"AVRSPI",{enumerable:!0,get:function(){return c.AVRSPI}}),Object.defineProperty(exports,"AVRClock",{enumerable:!0,get:function(){return g.AVRClock}}),Object.defineProperty(exports,"AVRClockConfig",{enumerable:!0,get:function(){return g.AVRClockConfig}}),Object.defineProperty(exports,"clockConfig",{enumerable:!0,get:function(){return g.clockConfig}});var r=require("./cpu/cpu"),t=require("./cpu/instruction"),n=require("./cpu/interrupt"),o=require("./peripherals/timer"),i=require("./peripherals/gpio"),f=require("./peripherals/usart"),u=require("./peripherals/eeprom"),p=require("./peripherals/twi");Object.keys(p).forEach(function(r){"default"!==r&&"__esModule"!==r&&(Object.prototype.hasOwnProperty.call(e,r)||r in exports&&exports[r]===p[r]||Object.defineProperty(exports,r,{enumerable:!0,get:function(){return p[r]}}))});var c=require("./peripherals/spi"),g=require("./peripherals/clock");
},{"./cpu/cpu":"vM7b","./cpu/instruction":"YNAr","./cpu/interrupt":"cTh7","./peripherals/timer":"xYD4","./peripherals/gpio":"ZLro","./peripherals/usart":"RlY3","./peripherals/eeprom":"JcIU","./peripherals/twi":"SWYB","./peripherals/spi":"q1W3","./peripherals/clock":"UXBG"}],"zLe3":[function(require,module,exports) {
"use strict";function s(s,t){for(const e of s.split("\n"))if(":"===e[0]&&"00"===e.substr(7,2)){const s=parseInt(e.substr(1,2),16),r=parseInt(e.substr(3,4),16);for(let o=0;o<s;o++)t[r+o]=parseInt(e.substr(9+2*o,2),16)}}Object.defineProperty(exports,"__esModule",{value:!0}),exports.loadHex=void 0,exports.loadHex=s;
},{}],"LMf0":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.AVRRunner=void 0;const t=require("avr8js"),i=require("./intelhex"),e=32768;class s{constructor(s,r){this.program=new Uint16Array(e),this.speed=16e6,this.workUnitCycles=5e5,i.loadHex(s,new Uint8Array(this.program.buffer)),this.cpu=new t.CPU(this.program),this.timer0=new t.AVRTimer(this.cpu,t.timer0Config),this.timer1=new t.AVRTimer(this.cpu,t.timer1Config),this.timer2=new t.AVRTimer(this.cpu,t.timer2Config),this.portB=new t.AVRIOPort(this.cpu,t.portBConfig),this.portC=new t.AVRIOPort(this.cpu,t.portCConfig),this.portD=new t.AVRIOPort(this.cpu,t.portDConfig),this.usart=new t.AVRUSART(this.cpu,t.usart0Config,this.speed),this.cpu.writeHooks[122]=(t=>{if(64&t){this.cpu.data[122]=-65&t;const i=15&this.cpu.data[124];return this.setAnalogValue(Math.floor(1023*this.sim.getNodeVoltage("A"+i)/5)),!0}}),this.sim=r,this.prevTime=this.sim.getTime()}setAnalogValue(t){this.cpu.data[120]=255&t,this.cpu.data[121]=t>>8&3}execute(i){var e=this;this.sim.ontimestep=function(){var s=e.sim.getTime()-e.prevTime,r=e.cpu.cycles+s*e.speed;for(e.getPinStates();e.cpu.cycles<r;)t.avrInstruction(e.cpu),e.cpu.tick();e.prevTime=e.sim.getTime(),i(e.cpu)}}getPinStates(){var i;for(i=0;14!=i;i++){var e=this.portD,s=i;i>=8&&(e=this.portB,s=i-8);var r=e.pinState(s);r==t.PinState.Input?e.setPin(s,this.sim.getNodeVoltage("pin "+i)>2.5):this.sim.setExtVoltage("pin "+i,r==t.PinState.High?5:0)}}stop(){this.sim.ontimestep=null}}exports.AVRRunner=s;
},{"avr8js":"WdSo","./intelhex":"zLe3"}],"fcge":[function(require,module,exports) {
"use strict";function t(t,e){let o=t.toString();for(;o.length<e;)o="0"+o;return o}function e(e){const o=Math.floor(1e3*e)%1e3,r=Math.floor(e%60);return`${t(Math.floor(e/60),2)}:${t(r,2)}.${t(o,3)}`}Object.defineProperty(exports,"__esModule",{value:!0}),exports.formatTime=void 0,exports.formatTime=e;
},{}],"vKFU":[function(require,module,exports) {

},{}],"nIKE":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports.EditorHistoryUtil=void 0;const t="AVRJS8_EDITOR_HISTORY";class e{static storeSnippet(o){e.hasLocalStorage&&window.localStorage.setItem(t,o)}static clearSnippet(){e.hasLocalStorage&&localStorage.removeItem(t)}static getValue(){if(e.hasLocalStorage)return localStorage.getItem(t)}}exports.EditorHistoryUtil=e,e.hasLocalStorage=!!window.localStorage;
},{}],"QCba":[function(require,module,exports) {
"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),require("@wokwi/elements");const e=require("./compile"),t=require("./cpu-performance"),n=require("./execute"),i=require("./format-time");require("./index.css");const o=require("./utils/editor-history.util");let r;const u='\n// Green LED connected to LED_BUILTIN,\n// Red LED connected to pin 12. Enjoy!\n\nvoid setup() {\n  Serial.begin(115200);\n  pinMode(LED_BUILTIN, OUTPUT);\n}\n\nvoid loop() {\n  Serial.println("Blink");\n  digitalWrite(LED_BUILTIN, HIGH);\n  delay(500);\n  digitalWrite(LED_BUILTIN, LOW);\n  delay(500);\n}'.trim();var d;let c;window.require.config({paths:{vs:"https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.21.2/min/vs"}}),window.require(["vs/editor/editor.main"],()=>{r=monaco.editor.create(document.querySelector(".code-editor"),{value:window.defaultCode||u,language:"cpp",minimap:{enabled:!1}})});const l=document.querySelector("#run-button");l.addEventListener("click",v);const a=document.querySelector("#stop-button");a.addEventListener("click",g);const s=document.querySelector("#revert-button");s.addEventListener("click",S);const m=document.querySelector("#status-label"),p=document.querySelector("#compiler-output-text"),b=document.querySelector("#serial-output-text");function y(e){var o=document.getElementById("circuitFrame");d=o.contentWindow.CircuitJS1,(c=new n.AVRRunner(e,d)).usart.onByteTransmit=(e=>{b.textContent+=String.fromCharCode(e)});const r=new t.CPUPerformance(c.cpu,16e6);c.execute(e=>{const t=i.formatTime(e.cycles/16e6),n=(100*r.update()).toFixed(0);m.textContent=`Simulation time: ${t} (${n}%)`}),d.setSimRunning(!0)}async function v(){x(),l.setAttribute("disabled","1"),s.setAttribute("disabled","1"),b.textContent="";try{m.textContent="Compiling...";const n=await e.buildHex(r.getModel().getValue());p.textContent=n.stderr||n.stdout,n.hex?(p.textContent+="\nProgram running...",a.removeAttribute("disabled"),y(n.hex)):l.removeAttribute("disabled")}catch(t){l.removeAttribute("disabled"),s.removeAttribute("disabled"),alert("Failed: "+t)}finally{m.textContent=""}}function x(){o.EditorHistoryUtil.clearSnippet(),o.EditorHistoryUtil.storeSnippet(r.getValue())}function g(){a.setAttribute("disabled","1"),l.removeAttribute("disabled"),s.removeAttribute("disabled"),c&&(c.stop(),c=null),d&&d.setSimRunning(!1)}function S(){r.setValue(window.defaultCode||u),o.EditorHistoryUtil.storeSnippet(r.getValue())}
},{"@wokwi/elements":"JIMd","./compile":"tdl1","./cpu-performance":"Q59z","./execute":"LMf0","./format-time":"fcge","./index.css":"vKFU","./utils/editor-history.util":"nIKE"}]},{},["QCba"], null)
//# sourceMappingURL=src.3c340f16.js.map