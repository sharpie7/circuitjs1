import asyncio
import os
import urllib.parse
import aiohttp.web
import sys

config = {
	"host":				"127.0.0.1",
	"port":				8080,
	"circuitws_uri":	"http://127.0.0.1:8123/circuitws.html",
}

async def websocket_handler(request):
	print("Websocket connected.")
	ws = aiohttp.web.WebSocketResponse()
	await ws.prepare(request)

	while True:
		cmd = input("Cmd: ")

		match cmd.lower():
			case "?":
				msg = { "cmd": "status" }
			case "start":
				msg = { "cmd": "set_running", "state": True }
			case "stop":
				msg = { "cmd": "set_running", "state": False }
			case "gnv":
				msg = { "cmd": "get_node_voltage", "nodes": [ "D7", "D6" ] }
			case "list":
				msg = { "cmd": "get_elements" }
			case "sev1":
				msg = { "cmd": "set_ext_voltage", "voltages": { "extsin": 1 } }
			case "sev2":
				msg = { "cmd": "set_ext_voltage", "voltages": { "extsin": 2 } }
			case "svg":
				msg = { "cmd": "get_svg" }
			case "q":
				sys.exit(0)
				break
			case "":
				continue
			case _:
				print("Not understood. Commands: ?, start, stop, gnv, list, sev1, sev2, svg, q")
				continue

		await ws.send_json(msg)
		response = await ws.receive_json()
		print(response)

	return ws


def main():
	my_uri = f"ws://{config['host']}:{config['port']}/ws"
	query = {
		"ws":	my_uri,
	}
	print(f"Point your browser to: {config['circuitws_uri']}?{urllib.parse.urlencode(query)}")

	app = aiohttp.web.Application()
	app.router.add_route("GET", "/ws", websocket_handler)
	aiohttp.web.run_app(app, host = config["host"], port = config["port"])


if __name__ == "__main__":
	main()
