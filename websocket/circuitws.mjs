export class CircuitWS {
	constructor(iframe) {
		this._iframe = iframe;
		this._ws_uri = null;
		this._ws = null;
		this._iframe.addEventListener("load", (event) => this._iframe_load());
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
	}

	_respond(msg) {
		if ((this._ws != null) && (this._ws.readyState == WebSocket.OPEN)) {
			this._ws.send(JSON.stringify(msg));
		} else {
			console.log("Discard", msg, this._ws);
		}
	}

	_respond_error(error_code, error_text) {
		this._respond({ "type": "response", "status": "error", "code": error_code, "text": error_text })
	}

	async _sleep(time_millis) {
		await new Promise(resolved => setTimeout(resolved, 100));
	}

	async _initialize_svg() {
		if (!this.sim.isSVGInitialized()) {
			this.sim.initializeSVG();
			for (let i = 0; i < 50; i++) {
				if (this.sim.isSVGInitialized()) {
					return true;
				}
				await this._sleep(100);
			}
			return false;
		}
		return true;
	}

	async _ws_message(event) {
		const msg = JSON.parse(event.data);
		if (!msg.hasOwnProperty("cmd")) {
			this._respond_error("no_cmd_in_request", "No 'cmd' element found in JSON request.")
			return;
		}

		if (!this.sim) {
			this._respond_error("no_sim_running", "No simulation running (iframe not loaded yet?).")
			return;
		}

		let response = {
			"type": "response",
			"status": "ok",
			"cmd": msg.cmd,
		};
		if (msg.hasOwnProperty("msgid")) {
			response.msgid = msg.msgid;
		}
		if (msg.cmd == "reload") {
			const url = new URL(this._iframe.src);
			url.search = "?" + (new URLSearchParams(msg.args).toString());
			this._iframe.src = url.toString();
		} else if (msg.cmd == "status") {
			response.data = {
				"running":	this.sim.isRunning(),
				"time":		this.sim.getTime(),
				"timestep":	this.sim.getTimeStep(),
			};
		} else if (msg.cmd == "set_running") {
			this.sim.setSimRunning(msg.state);
		} else if (msg.cmd == "set_timestep") {
			if (!msg.hasOwnProperty("timestep")) {
				this._respond_error("no_timestep_in_request", "No 'timestep' element found in JSON request.")
				return;
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
				this._respond_error("no_circuit_in_request", "No 'circuit' element found in JSON request.")
				return;
			}
			const subcircuits_only = !!msg.subcircuits_only;
			this.sim.importCircuit(msg.circuit, subcircuits_only);
		} else if (msg.cmd == "get_svg") {
			const initialized = await this._initialize_svg();
			if (!initialized) {
				this._respond_error("init_svg", "Cannot initialize SVG engine.");
				return
			}
			response.data = this.sim.getCircuitAsSVG();
		} else if (msg.cmd == "shutdown") {
			this.connect(null);
			this._ws.close();
			this._iframe.src = "about:blank";
		} else {
			this._respond_error("unknown_cmd", "Unknown command: " + msg.cmd)
			return;
		}
		this._respond(response);
	}

	_ws_close(event) {
		/* Attempt to reconnect after a while */
		setTimeout(() => {
			this.connect(this._ws_uri);
		}, 1000);
	}

	async _send_reload_complete_event() {
		while (true) {
			if (this.sim) {
				break;
			}
			await this._sleep(100);
		}
		this._respond({
			"type": 	"event",
			"event":	"reload_complete",
		})
	}

	_iframe_load(event) {
		this._send_reload_complete_event();
	}

	get sim() {
		return this._iframe.contentWindow.CircuitJS1;
	}
}
