export class CircuitWS {
	constructor(iframe) {
		this._iframe = iframe;
		this._ws_uri = null;
		this._ws = null;
	}

	connect(ws_uri) {
		this._ws_uri = ws_uri;
		this._ws = new WebSocket(ws_uri);
		this._ws.onmessage = (event) => this._ws_message(event);
		this._ws.onclose = (event) => this._ws_close(event);
	}

	initialize_parameters(query_params) {
		if (query_params.has("ws")) {
			this.connect(query_params.get("ws"));
		}
	}

	_respond(msg) {
		this._ws.send(JSON.stringify(msg));
	}

	_respond_error(error_code, error_text) {
		this._respond({ "status": "error", "code": error_code, "text": error_text })
	}

	_ws_message(event) {
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
			"status": "ok",
			"cmd": msg.cmd,
		};
		if (msg.hasOwnProperty("msgid")) {
			response.msgid = msg.msgid;
		}
		if (msg.cmd == "status") {
			response.data = {
				"running":	this.sim.isRunning(),
				"time":		this.sim.getTime(),
			};
		} else if (msg.cmd == "set_running") {
			this.sim.setSimRunning(msg.state);
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

	get sim() {
		return this._iframe.contentWindow.CircuitJS1;
	}
}
