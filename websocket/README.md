# WebSocket interface
CircuitJS supports reading and writing of internal circuit data already, as the
demo in `jsinterface.html` showcases. There is also a shim that connects the
CircuitJS JavaScript functionality by exposing it to a WebSocket interface.
This is shown in `websocket/circuitws.html`.

The CircuitJS circuit simulator connects to a given target URI using a
WebSocket and then expects to receive JSON-formatted commands; similarly,
JSON-formatted responses are generated. There are multiple scenarios in which
this can be useful, but generally it allows remote controlling a browser-run
simulation to be controlled and read out from any programming language.

## Usage
You can start by copying `circuitws.html` and `circuitws.mjs` to the `war/`
directory, then serve it from there. You then need to invoke the URI by adding
a `ws=` query string parameter. The code expects to find a WebSocket end point
there and will connect to it (and reconnect if the connection is lost).

Additionally, you can supply a `src=` query parameter which is the initial
`src` attribute of the iframe. If not supplied, a sensible default is chosen
for you so that the page always loads with a simulator.

For example, assume you're the `war/` subdirectory at `http://127.0.0.1:8123`,
then you could have a WebSocket endpoint listening on port 4444 as
`ws://127.0.0.1:4444/ws`. You would then open your web browser with the
following URI:

`http://127.0.0.1:8123/circuitws.html?ws=ws%3A%2F%2F127.0.0.1%3A4444%2Fws`

The simulator will start up and immediately connect to
`ws://127.0.0.1:4444/ws`, from which it expects commands.

## Commands
The command structure is always as follows:

```json
{
	"cmd":		"cmdname",
	"msgid":	1234,
	"foo":		"bar"
}
```

Every command needs to have the `cmd` key set in the dictionary. The `msgid` is
optional, but will be reflected in the response so that it is easy to correlate
requests with their corresponding responses, even if responses are sent
out-of-order.

Additional parameters may be required for some commands. These are commands
that are understood, along with examples:

  - `status`: Query the state of the simulator.
  - `set_running`: Start or stop the simulator (depending on the boolean
    argument in `state`)
  - `reload`: Reload the iframe. Supports an additional `args` command, which
    is a dictionary that contains the query parameters for the new iframe. The
    source cannot be modified.
  - `set_timestamp`: Sets the timestamp to the double value in the `timestep`
    key.
  - `get_node_voltage`: Query the node voltage of nodes. Expects a list of
    named nodes in the `nodes` element.
  - `get_elements`: List all elements of the circuit.
  - `set_ext_voltage`: Sets external voltage sources to their values.  Expects
    a `voltages` dictionary as parameter that contains name/value pairs.
  - `get_svg`: Returns the circuit as a SVG image.
  - `circuit_export`: Returns the cirucit schematic.
  - `circuit_import`: Loads a circuit, provided a textual representation in the
    `circuit` element.

## Example
If you have Python3.10+ and the module aiohttp installed, you can easily try
the example. It expects the `war` directory to be served at port 8123 and will
open a websocket listening port on 8080:

```
$ python3 circuitws_server.py
Point your browser to: http://127.0.0.1:8123/circuitws.html?ws=ws%3A%2F%2F127.0.0.1%3A8080%2Fws
======== Running on http://127.0.0.1:8080 ========
(Press CTRL+C to quit)
```

Then, Python will wait for the user to open the URL. Once that happens, a CLI console starts:

```
Websocket connected.
Cmd:
```

You can now enter commands, such as `?`, `start`, `stop`, `svg`, etc. If you
type `help` you'll get an overview of what is supported.

```
Cmd: ?
{'type': 'response', 'status': 'ok', 'cmd': 'status', 'msgid': 1, 'data': {'running': True, 'time': 1.0127750000024012, 'timestep': 5e-06}}
Cmd: stop
{'type': 'response', 'status': 'ok', 'cmd': 'set_running', 'msgid': 2}
Cmd: ?
{'type': 'response', 'status': 'ok', 'cmd': 'status', 'msgid': 3, 'data': {'running': False, 'time': 1.0562150000026858, 'timestep': 5e-06}}
Cmd: ?
{'type': 'response', 'status': 'ok', 'cmd': 'status', 'msgid': 4, 'data': {'running': False, 'time': 1.0562150000026858, 'timestep': 5e-06}}
Cmd: start
{'type': 'response', 'status': 'ok', 'cmd': 'set_running', 'msgid': 5}
Cmd: ?
{'type': 'response', 'status': 'ok', 'cmd': 'status', 'msgid': 6, 'data': {'running': True, 'time': 1.0699000000027754, 'timestep': 5e-06}}
Cmd: sev1
{'type': 'response', 'status': 'ok', 'cmd': 'set_ext_voltage', 'msgid': 7}
Cmd: help
Not understood. Commands: ?, start, stop, setts, gnv, list, sev1, sev2, svg, export, import, q
```
