export class CircuitWS {
	constructor(iframe) {
		this._iframe = iframe;
		this._autoshutoff = false;
		this._ws_uri = null;
		this._ws = null;
		this._circuitjs_loaded = false;
		this._iframe.addEventListener("load", (event) => this._on_iframe_load(event));
		this._on_iframe_load();
	}

	reload_circuitjs(src_uri) {
		this._iframe.src = src_uri;
	}

	connect(ws_uri) {
		this._ws_uri = ws_uri;
		if (ws_uri != null) {
			this._ws = new WebSocket(ws_uri);
			this._ws.onmessage = (event) => this._ws_message(event);
			this._ws.onclose = (event) => this._ws_close(event);
		}
	}

	initialize_parameters(query_params) {
		if (query_params.has("ws")) {
			this.connect(query_params.get("ws"));
		}
		if (query_params.has("autoshutoff")) {
			this._autoshutoff = query_params.get("autoshutoff") != 0;
		}
	}

	_respond(msg) {
		if ((this._ws != null) && (this._ws.readyState == WebSocket.OPEN)) {
			this._ws.send(JSON.stringify(msg));
		} else {
			console.log("Discarded response:", msg);
		}
	}

	_respond_error(msg, error_code, error_text) {
		msg.status = "error";
		msg.text = error_text;
		this._respond(msg);
	}

	_respond_event(event_code, event_data) {
		const msg = {
			"type": "event",
			"code": event_code,
		};
		if (event_data) {
			msg["data"] = event_data;
		}
		this._respond(msg);
	}

	async _ws_message(event) {
		const msg = JSON.parse(event.data);
		if (!msg.hasOwnProperty("cmd")) {
			this._respond_error("no_cmd_in_request", "No 'cmd' element found in JSON request.")
			return;
		}

		/* The default response layout */
		let response = {
			"type": "response",
			"status": "ok",
			"cmd": msg.cmd,
		};
		if (msg.hasOwnProperty("msgid")) {
			response.msgid = msg.msgid;
		}

		/* Process commands first which do not require a simulator; they do not
		 * send a response. */
		if (msg.cmd == "shutdown") {
			this.connect(null);
			this._ws.close();
			this._shutdown();
			return this._respond(response);
		} else if (msg.cmd == "reload") {
			const url = new URL(this._iframe.src);
			url.search = "?" + (new URLSearchParams(msg.args).toString());
			this._iframe.src = url.toString();
			return this._respond(response);
		}

		if (!this.sim) {
			return this._respond_error(response, "no_sim_running", "No simulation running (iframe not loaded yet?).")
		}

		if (msg.cmd == "status") {
			response.data = {
				"running":	this.sim.isRunning(),
				"time":		this.sim.getTime(),
				"timestep":	this.sim.getTimeStep(),
			};
		} else if (msg.cmd == "set_running") {
			this.sim.setSimRunning(msg.state);
		} else if (msg.cmd == "set_timestep") {
			if (!msg.hasOwnProperty("timestep")) {
				return this._respond_error(response, "no_timestep_in_request", "No 'timestep' element found in JSON request.");
			}
			this.sim.setTimeStep(msg.timestep);
		} else if (msg.cmd == "get_node_voltage") {
			response.data = { };
			for (let node_name of msg.nodes) {
				response.data[node_name] = this.sim.getNodeVoltage(node_name);
			}
		} else if (msg.cmd == "get_elements") {
			response.data = [ ];
			for (let element of this.sim.getElements()) {
				response.data.push({
					"type":			element.getType(),
					"post_count":	element.getPostCount(),
					"info":			element.getInfo(),
				});
			}
		} else if (msg.cmd == "set_ext_voltage") {
			for (const [name, value] of Object.entries(msg.voltages)) {
				this.sim.setExtVoltage(name, value);
			}
		} else if (msg.cmd == "circuit_export") {
			response.data = this.sim.exportCircuit();
		} else if (msg.cmd == "circuit_import") {
			if (!msg.hasOwnProperty("circuit")) {
				return this._respond_error(response, "no_circuit_in_request", "No 'circuit' element found in JSON request.")
			}
			const subcircuits_only = !!msg.subcircuits_only;
			this.sim.importCircuit(msg.circuit, subcircuits_only);
		} else if (msg.cmd == "get_svg") {
			this.sim.getCircuitAsSVG();
			response.data = "deferred";
		} else {
			return this._respond_error(response, "unknown_cmd", "Unknown command: " + msg.cmd)
		}
		this._respond(response);
	}

	_shutdown() {
		this._iframe.src = "about:blank";
	}

	_ws_close(event) {
		/* Attempt to reconnect after a while if we're not in autoshutoff mode.
		 * Otherwise, shut down the simulator. */
		if (this._autoshutoff) {
			this._shutdown();
			return;
		}

		setTimeout(() => {
			this.connect(this._ws_uri);
		}, 1000);
	}

	_on_iframe_load(event) {
		this._iframe.contentWindow.oncircuitjsloaded = (cjs) => this._on_circuitjs_loaded(cjs);
	}

	_on_circuitjs_loaded(cjs) {
		this._respond_event("reload_complete");
		cjs.onsvgrendered = (cjs, svg_data) => this._on_circuitjs_svg_rendered(cjs, svg_data);
		this._circuitjs_loaded = true;
	}

	_on_circuitjs_svg_rendered(cjs, svg_data) {
		this._respond_event("svg_rendered", svg_data);
	}

	get sim() {
		if (this._iframe.contentWindow == null) {
			return null;
		}
		if (!this._circuitjs_loaded) {
			return null;
		}
		return this._iframe.contentWindow.CircuitJS1;
	}
}
